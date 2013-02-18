package net.floodlightcontroller.mastership;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import org.apache.zookeeper.WatchedEvent;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.CuratorWatcher;
import com.netflix.curator.framework.recipes.leader.LeaderLatch;
import com.netflix.curator.framework.recipes.leader.Participant;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;
import com.netflix.curator.retry.ExponentialBackoffRetry;

public class MastershipManager implements IFloodlightModule, IMastershipService,
											ConnectionStateListener {
	protected static Logger log = LoggerFactory.getLogger(MastershipManager.class);
	protected String mastershipId;
	
	//TODO read this from configuration
	protected String connectionString = "localhost:2181";
	private final String namespace = "onos";
	private final String switchLatchesPath = "/switchmastership";
	
	protected CuratorFramework client;

	protected Map<String, LeaderLatch> switchLatches;
	protected Map<String, MastershipCallback> switchCallbacks;
	
	//protected final String singleLatchPath = switchLatchesPath + "/00:00:00:00:00:00:00:01";
	//protected LeaderLatch singleLatch;

	
	protected class ParamaterizedCuratorWatcher implements CuratorWatcher {
		private String dpid;
		private String latchPath;
		
		public ParamaterizedCuratorWatcher(String dpid, String latchPath){
			this.dpid = dpid;
			this.latchPath = latchPath;
		}
		
		@Override
		public void process(WatchedEvent event) throws Exception {
			log.debug("Watch Event: {}", event);

			LeaderLatch latch = switchLatches.get(dpid);
			
			Participant leader = latch.getLeader();
			
			log.debug("Leader for {} changed. Now {}", dpid, leader.getId());
			
			if (leader.getId().equals(mastershipId)){
				switchCallbacks.get(dpid).changeCallback(HexString.toLong(dpid), true);
			}
			
			client.getChildren().usingWatcher(this).inBackground().forPath(latchPath);
			//client.getChildren().usingWatcher(this).forPath(latchPath);
		}
	};
	
	@Override
	public void acquireMastership(long dpid, MastershipCallback cb) {
		//TODO check if there's already a latch in the list
		//TODO throw exception if unique ID not set
		//TODO use unique ID
		if (cb == null) {
			//TODO throw exception?
		}
		
		String dpidStr = HexString.toHexString(dpid);
		String latchPath = switchLatchesPath + "/" + dpidStr;
		
		LeaderLatch latch = new LeaderLatch(client, latchPath, mastershipId);
		switchLatches.put(dpidStr, latch);
		//if (switchCallbacks.put(dpidStr, cb) != cb){
			//NOTE instance equality intended
		//	log.debug("Throwing out old callback for switch {}: {}", dpidStr, cb);
		//}
		switchCallbacks.put(dpidStr, cb);
		
		try {
			//client.getChildren().usingWatcher(watcher).inBackground().forPath(singleLatchPath);
			client.getChildren().usingWatcher(
					new ParamaterizedCuratorWatcher(dpidStr, latchPath))
					.inBackground().forPath(latchPath);
			latch.start();
		} catch (Exception e) {
			log.debug("WATCHER ERROROROROR");
			e.printStackTrace();
		}
		
	}

	@Override
	public void releaseMastership(long dpid) {
		// TODO Auto-generated method stub
		LeaderLatch latch = switchLatches.get(HexString.toHexString(dpid));
		if (latch == null) {
			return;
		}
		
		try {
			latch.close();
		} catch (IOException e) {
			
		}
	}

	@Override
	public boolean amMaster(long dpid) {
		LeaderLatch latch = switchLatches.get(HexString.toHexString(dpid));
		
		if (latch == null) {
			log.warn("No leader latch for dpid {}", HexString.toHexString(dpid));
			return false;
		}
		
		//return latch.hasLeadership();
		try {
			return latch.getLeader().getId().equals(mastershipId);
		} catch (Exception e) {
			//TODO swallow exception?
			return false;
		}
	}

	@Override
	public void setMastershipId(String id) {
		// TODO Synchronisation?
		mastershipId = id;
	}

	@Override
	public String getMastershipId() {
		// TODO Synchronisation?
		return mastershipId;
	}
	
	/*
	 * ConnectionStateListener
	 */
	
	@Override
	public void stateChanged(CuratorFramework client, ConnectionState state) {
		// TODO Auto-generated method stub
		log.debug("Connection state change: {}", state);
		
		switch (state){
		case SUSPENDED:
		case LOST:
			//If we're suspended or lost, we can no longer assume mastership
			for (Map.Entry<String, MastershipCallback> entry : switchCallbacks.entrySet()){
				entry.getValue().changeCallback(HexString.toLong(entry.getKey()), false);
			}
			break;
		default:
			//CONNECTED or RECONNECTED
			//LeaderLatches should automatically try and re-gain the leadership
			break;
		}
	}
	
	/*
	 * IFloodlightModule
	 */
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
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
	
	//be ready to serve mastership requests by startup?
	@Override
	public void init (FloodlightModuleContext context) throws FloodlightModuleException {
		//TODO
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
		
		return;
	}
	
	@Override
	public void startUp (FloodlightModuleContext context) {
		//TODO
		return;
	}
	
	public static void main(String args[]){
		FloodlightModuleContext fmc = new FloodlightModuleContext();
		MastershipManager mm = new MastershipManager();
		
		if (args.length < 1){
			log.error("Need to supply ID");
			System.exit(1);
		}
		String id = args[0];
		
		try {
			mm.init(fmc);
			mm.startUp(fmc);
			
			mm.setMastershipId(id);
			
			mm.acquireMastership(1L, 
				new MastershipCallback(){
					@Override
					public void changeCallback(long dpid, boolean isMaster) {
						if (isMaster){
							log.debug("I'm master for {}", HexString.toHexString(dpid));
						}
						else {
							log.debug("NOT master for {}", HexString.toHexString(dpid));
						}
					}
				});
			
			//"Server" loop
			while (true) {
				Thread.sleep(60000);
			}
			
		} catch (FloodlightModuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.debug("is master: {}", mm.amMaster(1L));
	}
}
