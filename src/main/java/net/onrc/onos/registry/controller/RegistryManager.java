package net.onrc.onos.registry.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;

import org.apache.commons.lang.NotImplementedException;
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
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.netflix.curator.framework.recipes.leader.LeaderLatch;
import com.netflix.curator.framework.recipes.leader.Participant;
import com.netflix.curator.retry.ExponentialBackoffRetry;

public class RegistryManager implements IFloodlightModule, IControllerRegistryService {

	protected static Logger log = LoggerFactory.getLogger(RegistryManager.class);
	protected String mastershipId = null;
	
	protected IRestApiService restApi;
	
	//TODO read this from configuration
	protected String connectionString = "localhost:2181";
	
	
	private final String namespace = "onos";
	private final String switchLatchesPath = "/switches";
	private final String controllerPath = "/controllers";
	
	protected CuratorFramework client;
	
	protected PathChildrenCache controllerCache;
	protected PathChildrenCache switchCache;

	protected Map<String, LeaderLatch> switchLatches;
	protected Map<String, ControlChangeCallback> switchCallbacks;
	protected Map<String, PathChildrenCache> switchPathCaches;
	
	protected boolean moduleEnabled = false;
	
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
					ControlChangeCallback cb = switchCallbacks.get(dpid);
					if (cb != null) {
						//Allow callback to be null if the requester doesn't want a callback
						cb.controlChanged(HexString.toLong(dpid), false);
					}
				}
				return;
			}
			
			try {
				
				Participant leader = latch.getLeader();

				if (leader.getId().equals(mastershipId) && !isLeader){
					log.debug("Became leader for {}", dpid);
					
					isLeader = true;
					switchCallbacks.get(dpid).controlChanged(HexString.toLong(dpid), true);
				}
				else if (!leader.getId().equals(mastershipId) && isLeader){
					log.debug("Lost leadership for {}", dpid);
					
					isLeader = false;
					switchCallbacks.get(dpid).controlChanged(HexString.toLong(dpid), false);
				}
			} catch (Exception e){
				if (isLeader){
					log.debug("Exception checking leadership status. Assume leadship lost for {}",
							dpid);
					
					isLeader = false;
					switchCallbacks.get(dpid).controlChanged(HexString.toLong(dpid), false);
				}
			}
			
			client.getChildren().usingWatcher(this).inBackground().forPath(latchPath);
			//client.getChildren().usingWatcher(this).forPath(latchPath);
		}
	}
	
	
	/*
	 * Listens for changes to the switch znodes in Zookeeper. This maintains the second level of
	 * PathChildrenCaches that hold the controllers contending for each switch - there's one for
	 * each switch.
	 */
	PathChildrenCacheListener switchPathCacheListener = new PathChildrenCacheListener() {
		@Override
		public void childEvent(CuratorFramework client,
				PathChildrenCacheEvent event) throws Exception {
			// TODO Auto-generated method stub
			log.debug("Root switch path cache got {} event", event.getType());
			
			String strSwitch = null;
			if (event.getData() != null){
				log.debug("Event path {}", event.getData().getPath());
				String[] splitted = event.getData().getPath().split("/");
				strSwitch = splitted[splitted.length - 1];
				log.debug("Switch name is {}", strSwitch);
			}
			
			switch (event.getType()){
			case CHILD_ADDED:
			case CHILD_UPDATED:
				//Check we have a PathChildrenCache for this child, add one if not
				if (switchPathCaches.get(strSwitch) == null){
					PathChildrenCache pc = new PathChildrenCache(client, 
							event.getData().getPath(), true);
					pc.start(StartMode.NORMAL);
					switchPathCaches.put(strSwitch, pc);
				}
				break;
			case CHILD_REMOVED:
				//Remove our PathChildrenCache for this child
				PathChildrenCache pc = switchPathCaches.remove(strSwitch);
				pc.close();
				break;
			default:
				//All other events are connection status events. We need to do anything
				//as the path cache handles these on its own.
				break;
			}
			
		}
	};

	
	@Override
	public void requestControl(long dpid, ControlChangeCallback cb) throws RegistryException {
		
		if (!moduleEnabled) return;
		
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
			throw new RegistryException("Error starting leader latch for " + dpidStr, e);
		}
		
	}

	@Override
	public void releaseControl(long dpid) {
		if (!moduleEnabled) return;
		
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
	public boolean hasControl(long dpid) {
		if (!moduleEnabled) return false;
		
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
		if (!moduleEnabled) return null;
		
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
		if (!moduleEnabled) return;
		
		byte bytes[] = null;
		try {
			bytes = id.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RegistryException("Error encoding string", e1);
		}
		
		String path = controllerPath + "/" + id;
		
		log.info("Registering controller with id {}", id);
		
		//Create ephemeral node in controller registry
		try {
			client.create().withProtection().withMode(CreateMode.EPHEMERAL)
					.forPath(path, bytes);
		} catch (Exception e) {
			throw new RegistryException("Error contacting the Zookeeper service", e);
		}
	}
	
	@Override
	public String getControllerForSwitch(long dpid) throws RegistryException {
		if (!moduleEnabled) return null;
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
		//TODO remove this if not needed
		throw new NotImplementedException();
	}
	

	@Override
	public Map<String, List<ControllerRegistryEntry>> getAllSwitches() {
		Map<String, List<ControllerRegistryEntry>> data = 
				new HashMap<String, List<ControllerRegistryEntry>>();
		
		for (Map.Entry<String, PathChildrenCache> entry : switchPathCaches.entrySet()){
			List<ControllerRegistryEntry> contendingControllers =
					 new ArrayList<ControllerRegistryEntry>(); 
			
			if (entry.getValue().getCurrentData().size() < 1){
				log.info("Switch entry with no leader elections: {}", entry.getKey());
				continue;
			}
			
			for (ChildData d : entry.getValue().getCurrentData()) {
				/*
				if (d.getPath().length() < 1){
					log.info("Switch entry with no leader elections: {}", d.getPath());
					continue;
				}
				*/
				
				String controllerId = null;
				try {
					controllerId = new String(d.getData(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.warn("Encoding exception: {}", e.getMessage());
				}
				
				String[] splitted = d.getPath().split("-");
				int sequenceNumber = Integer.parseInt(splitted[splitted.length - 1]);
				
				contendingControllers.add(new ControllerRegistryEntry(controllerId, sequenceNumber));
			 }
			
			Collections.sort(contendingControllers);
			data.put(entry.getKey(), contendingControllers);
		}
		return data;
	}
	
	/*
	 * IFloodlightModule
	 */
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IControllerRegistryService.class);
		return l;
	}
	
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IControllerRegistryService.class,  this);
		return m;
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IRestApiService.class);
		return l;
	}
	
	@Override
	public void init (FloodlightModuleContext context) throws FloodlightModuleException {
		
		restApi = context.getServiceImpl(IRestApiService.class);
		
		//Read config to see if we should try and connect to zookeeper
		Map<String, String> configOptions = context.getConfigParams(this);
		String enableZookeeper = configOptions.get("enableZookeeper");
		if (enableZookeeper != null) {
			log.info("Enabling Mastership module - requires Zookeeper connection");
			moduleEnabled = true;
		}
		else {
			log.info("Mastership module is disabled");
			return;
		}
		
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			mastershipId = localHostname;
			log.debug("Setting mastership id to {}", mastershipId);
		} catch (UnknownHostException e) {
			// TODO Handle this exception
			e.printStackTrace();
		}

		switchLatches = new HashMap<String, LeaderLatch>();
		switchCallbacks = new HashMap<String, ControlChangeCallback>();
		switchPathCaches = new HashMap<String, PathChildrenCache>();
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		//RetryPolicy retryPolicy = new RetryOneTime(0);
		client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
		
		client.start();
		
		client = client.usingNamespace(namespace);
		
		//Put some data in for testing
		try {
			registerController(mastershipId);
			requestControl(2L, null);
		} catch (RegistryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		controllerCache = new PathChildrenCache(client, controllerPath, true);
		switchCache = new PathChildrenCache(client, switchLatchesPath, true);
		switchCache.getListenable().addListener(switchPathCacheListener);
		
		try {
			controllerCache.start(StartMode.BUILD_INITIAL_CACHE);
			
			//Don't prime the cache, we want a notification for each child node in the path
			switchCache.start(StartMode.NORMAL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void startUp (FloodlightModuleContext context) {
		restApi.addRestletRoutable(new RegistryWebRoutable());
	}

}
