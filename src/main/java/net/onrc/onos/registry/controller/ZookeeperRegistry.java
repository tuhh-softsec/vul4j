package net.onrc.onos.registry.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;

import org.apache.zookeeper.CreateMode;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.ChildData;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.netflix.curator.framework.recipes.leader.LeaderLatch;
import com.netflix.curator.framework.recipes.leader.LeaderLatchEvent;
import com.netflix.curator.framework.recipes.leader.LeaderLatchListener;
import com.netflix.curator.framework.recipes.leader.Participant;
import com.netflix.curator.retry.ExponentialBackoffRetry;

/**
 * A registry service that uses Zookeeper. All data is stored in Zookeeper,
 * so this can be used as a global registry in a multi-node ONOS cluster.
 * @author jono
 *
 */
public class ZookeeperRegistry implements IFloodlightModule, IControllerRegistryService {

	protected static Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);
	protected String controllerId = null;
	
	protected IRestApiService restApi;
	
	//This is the default, it's overwritten by the connectionString configuration parameter
	protected String connectionString = "localhost:2181";
	
	private final String namespace = "onos";
	private final String switchLatchesPath = "/switches";
	private final String controllerPath = "/controllers";
	
	protected CuratorFramework client;
	
	protected PathChildrenCache controllerCache;
	protected PathChildrenCache switchCache;

	protected ConcurrentHashMap<String, SwitchLeadershipData> switches;
	protected Map<String, PathChildrenCache> switchPathCaches;
	
	//Zookeeper performance-related configuration
	protected static final int sessionTimeout = 2000;
	protected static final int connectionTimeout = 4000;
	

	protected class SwitchLeaderListener implements LeaderLatchListener{
		String dpid;
		LeaderLatch latch;
		
		public SwitchLeaderListener(String dpid, LeaderLatch latch){
			this.dpid = dpid;
			this.latch = latch;
		}
		
		@Override
		public void leaderLatchEvent(CuratorFramework arg0,
				LeaderLatchEvent arg1) {
			log.debug("Leadership changed for {}, now {}",
					dpid, latch.hasLeadership());
			
			//Check that the leadership request is still active - the client
			//may have since released the request or even begun another request
			//(this is why we use == to check the object instance is the same)
			SwitchLeadershipData swData = switches.get(dpid);
			if (swData != null && swData.getLatch() == latch){
				swData.getCallback().controlChanged(
						HexString.toLong(dpid), latch.hasLeadership());
			}
			else {
				log.debug("Latch for {} has changed", dpid);
			}
		}
	}
	
	
	/**
	 * Listens for changes to the switch znodes in Zookeeper. This maintains
	 * the second level of PathChildrenCaches that hold the controllers 
	 * contending for each switch - there's one for each switch.
	 */
	PathChildrenCacheListener switchPathCacheListener = new PathChildrenCacheListener() {
		@Override
		public void childEvent(CuratorFramework client,
				PathChildrenCacheEvent event) throws Exception {
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
		log.info("Requesting control for {}", HexString.toHexString(dpid));
		
		if (controllerId == null){
			throw new RuntimeException("Must register a controller before calling requestControl");
		}
		
		String dpidStr = HexString.toHexString(dpid);
		String latchPath = switchLatchesPath + "/" + dpidStr;
		
		if (switches.get(dpidStr) != null){
			log.debug("Already contesting {}, returning", HexString.toHexString(dpid));
			throw new RegistryException("Already requested control for " + dpidStr);
		}
		
		LeaderLatch latch = new LeaderLatch(client, latchPath, controllerId);
		latch.addListener(new SwitchLeaderListener(dpidStr, latch));
		

		SwitchLeadershipData swData = new SwitchLeadershipData(latch, cb);
		SwitchLeadershipData oldData = switches.putIfAbsent(dpidStr, swData);
		
		if (oldData != null){
			//There was already data for that key in the map
			//i.e. someone else got here first so we can't succeed
			log.debug("Already requested control for {}", dpidStr);
			throw new RegistryException("Already requested control for " + dpidStr);
		}
		
		//Now that we know we were able to add our latch to the collection,
		//we can start the leader election in Zookeeper. However I don't know
		//how to handle if the start fails - the latch is already in our
		//switches list.
		//TODO seems like there's a Curator bug when latch.start is called when
		//there's no Zookeeper connection which causes two znodes to be put in 
		//Zookeeper at the latch path when we reconnect to Zookeeper.
		try {
			latch.start();
		} catch (Exception e) {
			log.warn("Error starting leader latch: {}", e.getMessage());
			throw new RegistryException("Error starting leader latch for " + dpidStr, e);
		}
		
	}

	@Override
	public void releaseControl(long dpid) {
		log.info("Releasing control for {}", HexString.toHexString(dpid));
		
		String dpidStr = HexString.toHexString(dpid);
		
		SwitchLeadershipData swData = switches.remove(dpidStr);
		
		if (swData == null) {
			log.debug("Trying to release control of a switch we are not contesting");
			return;
		}

		LeaderLatch latch = swData.getLatch();
		
		try {
			latch.close();
		} catch (IOException e) {
			//I think it's OK not to do anything here. Either the node got 
			//deleted correctly, or the connection went down and the node got deleted.
		}
	}

	@Override
	public boolean hasControl(long dpid) {
		String dpidStr = HexString.toHexString(dpid);
		
		SwitchLeadershipData swData = switches.get(dpidStr);
		
		if (swData == null) {
			log.warn("No leader latch for dpid {}", dpidStr);
			return false;
		}
		
		return swData.getLatch().hasLeadership();
	}

	@Override
	public String getControllerId() {
		return controllerId;
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
		if (controllerId != null) {
			throw new RegistryException(
					"Controller already registered with id " + controllerId);
		}
		
		controllerId = id;
		
		byte bytes[] = id.getBytes(Charsets.UTF_8);
		
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
		// TODO work out synchronization
		
		String dpidStr = HexString.toHexString(dpid);

		
		LeaderLatch latch = (switches.get(dpidStr) != null)?switches.get(dpidStr).getLatch():null;
		
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
		throw new RuntimeException("Not yet implemented");
	}
	

	//TODO what should happen when there's no ZK connection? Currently we just return
	//the cache but this may lead to false impressions - i.e. we don't actually know
	//what's in ZK so we shouldn't say we do
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
			
				String controllerId = new String(d.getData(), Charsets.UTF_8);
				
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
	
	//TODO currently blocks startup when it can't get a Zookeeper connection.
	//Do we support starting up with no Zookeeper connection?
	@Override
	public void init (FloodlightModuleContext context) throws FloodlightModuleException {
		log.info("Initialising the Zookeeper Registry - Zookeeper connection required");
		
		//Read the Zookeeper connection string from the config
		Map<String, String> configParams = context.getConfigParams(this);
		String connectionString = configParams.get("connectionString");
		if (connectionString != null){
			this.connectionString = connectionString;
		}
		log.info("Setting Zookeeper connection string to {}", this.connectionString);
		
		restApi = context.getServiceImpl(IRestApiService.class);

		switches = new ConcurrentHashMap<String, SwitchLeadershipData>();
		switchPathCaches = new HashMap<String, PathChildrenCache>();
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		client = CuratorFrameworkFactory.newClient(this.connectionString, 
				sessionTimeout, connectionTimeout, retryPolicy);
		
		client.start();
		client = client.usingNamespace(namespace);

		
		controllerCache = new PathChildrenCache(client, controllerPath, true);
		switchCache = new PathChildrenCache(client, switchLatchesPath, true);
		switchCache.getListenable().addListener(switchPathCacheListener);
		
		try {
			controllerCache.start(StartMode.BUILD_INITIAL_CACHE);
			
			//Don't prime the cache, we want a notification for each child node in the path
			switchCache.start(StartMode.NORMAL);
		} catch (Exception e) {
			throw new FloodlightModuleException("Error initialising ZookeeperRegistry: " 
					+ e.getMessage());
		}
	}
	
	@Override
	public void startUp (FloodlightModuleContext context) {
		restApi.addRestletRoutable(new RegistryWebRoutable());
	}
}
