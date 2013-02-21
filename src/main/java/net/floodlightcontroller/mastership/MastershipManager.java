package net.floodlightcontroller.mastership;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.CuratorWatcher;
import com.netflix.curator.framework.recipes.cache.ChildData;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import com.netflix.curator.framework.recipes.leader.LeaderLatch;
import com.netflix.curator.framework.recipes.leader.Participant;
import com.netflix.curator.retry.ExponentialBackoffRetry;

public class MastershipManager implements IFloodlightModule, IMastershipService {

	protected static Logger log = LoggerFactory.getLogger(MastershipManager.class);
	protected String mastershipId = null;
	
	//TODO read this from configuration
	protected String connectionString = "localhost:2181";
	private final String namespace = "onos";
	private final String switchLatchesPath = "/switches";
	
	protected CuratorFramework client;
	
	private final String controllerPath = "/controllers";
	protected PathChildrenCache controllerCache;

	protected Map<String, LeaderLatch> switchLatches;
	protected Map<String, MastershipCallback> switchCallbacks;
	
	protected class ParamaterizedCuratorWatcher implements CuratorWatcher {
		private String dpid;
		private boolean isLeader = false;
		private String latchPath;
		
		public ParamaterizedCuratorWatcher(String dpid, String latchPath){
			this.dpid = dpid;
			this.latchPath = latchPath;
		}
		
		@Override
		public synchronized void process(WatchedEvent event) throws Exception {
			log.debug("Watch Event: {}", event);

			LeaderLatch latch = switchLatches.get(dpid);
			
			if (event.getState() == KeeperState.Disconnected){
				if (isLeader) {
					log.debug("Disconnected while leader - lost leadership for {}", dpid);
					
					isLeader = false;
					switchCallbacks.get(dpid).changeCallback(HexString.toLong(dpid), false);
				}
				return;
			}
			
			try {
				
				Participant leader = latch.getLeader();

				if (leader.getId().equals(mastershipId) && !isLeader){
					log.debug("Became leader for {}", dpid);
					
					isLeader = true;
					switchCallbacks.get(dpid).changeCallback(HexString.toLong(dpid), true);
				}
				else if (!leader.getId().equals(mastershipId) && isLeader){
					log.debug("Lost leadership for {}", dpid);
					
					isLeader = false;
					switchCallbacks.get(dpid).changeCallback(HexString.toLong(dpid), false);
				}
			} catch (Exception e){
				if (isLeader){
					log.debug("Exception checking leadership status. Assume leadship lost for {}",
							dpid);
					
					isLeader = false;
					switchCallbacks.get(dpid).changeCallback(HexString.toLong(dpid), false);
				}
			}
			
			client.getChildren().usingWatcher(this).inBackground().forPath(latchPath);
			//client.getChildren().usingWatcher(this).forPath(latchPath);
		}
	}

	
	@Override
	public void acquireMastership(long dpid, MastershipCallback cb) throws Exception {
		
		if (mastershipId == null){
			throw new RuntimeException("Must set mastershipId before calling aquireMastership");
		}
		
		String dpidStr = HexString.toHexString(dpid);
		String latchPath = switchLatchesPath + "/" + dpidStr;
		
		if (switchLatches.get(dpidStr) != null){
			throw new RuntimeException("Leader election for switch " + dpidStr +
					"is already running");
		}
		
		LeaderLatch latch = new LeaderLatch(client, latchPath, mastershipId);
		switchLatches.put(dpidStr, latch);
		switchCallbacks.put(dpidStr, cb);
		
		try {
			//client.getChildren().usingWatcher(watcher).inBackground().forPath(singleLatchPath);
			client.getChildren().usingWatcher(
					new ParamaterizedCuratorWatcher(dpidStr, latchPath))
					.inBackground().forPath(latchPath);
			latch.start();
		} catch (Exception e) {
			log.warn("Error starting leader latch: {}", e.getMessage());
			throw e;
		}
		
	}

	@Override
	public void releaseMastership(long dpid) {
		String dpidStr = HexString.toHexString(dpid);
		
		LeaderLatch latch = switchLatches.get(dpidStr);
		if (latch == null) {
			log.debug("Trying to release mastership for switch we are not contesting");
			return;
		}
		
		try {
			latch.close();
		} catch (IOException e) {
			//I think it's OK not to do anything here. Either the node got deleted correctly,
			//or the connection went down and the node got deleted.
		} finally {
			switchLatches.remove(dpidStr);
			switchCallbacks.remove(dpidStr);
		}
	}

	@Override
	public boolean amMaster(long dpid) {
		LeaderLatch latch = switchLatches.get(HexString.toHexString(dpid));
		
		if (latch == null) {
			log.warn("No leader latch for dpid {}", HexString.toHexString(dpid));
			return false;
		}
		
		try {
			return latch.getLeader().getId().equals(mastershipId);
		} catch (Exception e) {
			//TODO swallow exception?
			return false;
		}
	}

	@Override
	public void setMastershipId(String id) {
		mastershipId = id;
	}

	@Override
	public String getMastershipId() {
		return mastershipId;
	}
	
	@Override
	public Collection<String> getAllControllers() throws RegistryException {
		log.debug("Getting all controllers");
		
		List<String> controllers = new ArrayList<String>();
		for (ChildData data : controllerCache.getCurrentData()){

			String d = null;
			try {
				d = new String(data.getData(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RegistryException("Error encoding string", e);
			}

			controllers.add(d);
		}
		return controllers;
	}

	@Override
	public void registerController(String id) throws RegistryException {
		byte bytes[] = null;
		try {
			bytes = id.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RegistryException("Error encoding string", e1);
		}
		
		String path = controllerPath + "/" + id;
		
		log.info("Registering controller with id {}", id);
		
		//Create ephemeral node with my id
		try {
			client.create().withProtection().withMode(CreateMode.EPHEMERAL)
					.forPath(path, bytes);
		} catch (Exception e) {
			throw new RegistryException("Error contacting the Zookeeper service", e);
		}
	}
	
	@Override
	public String getControllerForSwitch(long dpid) throws RegistryException {
		// TODO Work out how we should store this controller/switch data.
		// The leader latch might be a index to the /controllers collections
		// which holds more info on the controller (how to talk to it for example).
		
		
		String strDpid = HexString.toHexString(dpid);
		LeaderLatch latch = switchLatches.get(strDpid);
		
		if (latch == null){
			log.warn("Tried to get controller for non-existent switch");
			return null;
		}
		
		Participant leader = null;
		try {
			leader = latch.getLeader();
		} catch (Exception e) {
			throw new RegistryException("Error contacting the Zookeeper service", e);
		}
		
		return leader.getId();
	}
	
	@Override
	public Collection<Long> getSwitchesControlledByController(String controllerId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * IFloodlightModule
	 */
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IMastershipService.class);
		return l;
	}
	
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IMastershipService.class,  this);
		return m;
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// no module dependencies
		return null;
	}
	
	@Override
	public void init (FloodlightModuleContext context) throws FloodlightModuleException {
		/*
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			mastershipId = localHostname;
			log.debug("Setting mastership id to {}", mastershipId);
		} catch (UnknownHostException e) {
			// TODO Handle this exception
			e.printStackTrace();
		}

		switchLatches = new HashMap<String, LeaderLatch>();
		switchCallbacks = new HashMap<String, MastershipCallback>();
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
		
		client.start();
		
		client = client.usingNamespace(namespace);
		
		controllerCache = new PathChildrenCache(client, controllerPath, true);
		
		try {
			controllerCache.start(StartMode.BUILD_INITIAL_CACHE);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
	}
	
	@Override
	public void startUp (FloodlightModuleContext context) {
		// Nothing to be done on startup
	}
}
