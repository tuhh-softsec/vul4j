package net.onrc.onos.ofcontroller.bgproute;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.SingletonTask;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.bgproute.RibUpdate.Operation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoLinkService;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.onrc.onos.ofcontroller.core.internal.TopoLinkServiceImpl;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscovery;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.onrc.onos.ofcontroller.proxyarp.IArpRequester;
import net.onrc.onos.ofcontroller.proxyarp.ProxyArpManager;
import net.onrc.onos.ofcontroller.routing.TopoRouteService;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class BgpRoute implements IFloodlightModule, IBgpRouteService, 
									ITopologyListener, IOFSwitchListener,
									IArpRequester {
	
	protected static Logger log = LoggerFactory.getLogger(BgpRoute.class);

	protected IFloodlightProviderService floodlightProvider;
	protected ITopologyService topology;
	protected ITopoRouteService topoRouteService;
	protected IDeviceService devices;
	protected IRestApiService restApi;
	
	protected ProxyArpManager proxyArp;
	
	//protected static Ptree ptree;
	protected IPatriciaTrie<RibEntry> ptree;
	protected IPatriciaTrie<Interface> interfacePtrie;
	protected BlockingQueue<RibUpdate> ribUpdates;
	
	protected String bgpdRestIp;
	protected String routerId;
	protected String configFilename = "config.json";
	
	//We need to identify our flows somehow. But like it says in LearningSwitch.java,
	//the controller/OS should hand out cookie IDs to prevent conflicts.
	protected final long APP_COOKIE = 0xa0000000000000L;
	//Cookie for flows that do L2 forwarding within SDN domain to egress routers
	protected final long L2_FWD_COOKIE = APP_COOKIE + 1;
	//Cookie for flows in ingress switches that rewrite the MAC address
	protected final long MAC_RW_COOKIE = APP_COOKIE + 2;
	//Cookie for flows that setup BGP paths
	protected final long BGP_COOKIE = APP_COOKIE + 3;
	//Forwarding uses priority 0, and the mac rewrite entries in ingress switches
	//need to be higher priority than this otherwise the rewrite may not get done
	protected final short SDNIP_PRIORITY = 10;
	
	protected final short BGP_PORT = 179;
	
	protected final int TOPO_DETECTION_WAIT = 2; //seconds
	
	//Configuration stuff
	protected List<String> switches;
	protected Map<String, Interface> interfaces;
	protected Map<InetAddress, BgpPeer> bgpPeers;
	protected SwitchPort bgpdAttachmentPoint;
	
	//True when all switches have connected
	protected volatile boolean switchesConnected = false;
	//True when we have a full mesh of shortest paths between gateways
	protected volatile boolean topologyReady = false;

	protected ArrayList<LDUpdate> linkUpdates;
	protected SingletonTask topologyChangeDetectorTask;
	
	protected SetMultimap<InetAddress, RibUpdate> prefixesWaitingOnArp;
	
	//TODO should this really be a Multimap?
	//Es kann nur einen geben per IP address?
	protected SetMultimap<InetAddress, PathUpdate> pathsWaitingOnArp;
	
	protected ExecutorService bgpUpdatesExecutor;
	
	protected Map<InetAddress, PathUpdate> pushedPaths;
	protected Map<Prefix, PathUpdate> prefixToPath;
	protected Multimap<Prefix, PushedFlowMod> pushedFlows;
		
	protected class TopologyChangeDetector implements Runnable {
		@Override
		public void run() {
			log.debug("Running topology change detection task");
			synchronized (linkUpdates) {
				//This is the model the REST API uses to retrieve network graph info
				ITopoLinkService topoLinkService = new TopoLinkServiceImpl();
				
				List<Link> activeLinks = topoLinkService.getActiveLinks();
				
				Iterator<LDUpdate> it = linkUpdates.iterator();
				while (it.hasNext()){
					LDUpdate ldu = it.next();
					Link l = new Link(ldu.getSrc(), ldu.getSrcPort(), 
							ldu.getDst(), ldu.getDstPort());
					
					if (activeLinks.contains(l)){
						log.debug("Not found: {}", l);
						it.remove();
					}
				}
			}
			
			if (linkUpdates.isEmpty()){
				//All updates have been seen in network map.
				//We can check if topology is ready
				log.debug("No known changes outstanding. Checking topology now");
				checkStatus();
			}
			else {
				//We know of some link updates that haven't propagated to the database yet
				log.debug("Some changes not found in network map - {} links missing", linkUpdates.size());
				topologyChangeDetectorTask.reschedule(TOPO_DETECTION_WAIT, TimeUnit.SECONDS);
			}
		}
	}
	
	private void readGatewaysConfiguration(String gatewaysFilename){
		File gatewaysFile = new File(gatewaysFilename);
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Configuration config = mapper.readValue(gatewaysFile, Configuration.class);
			
			switches = config.getSwitches();
			interfaces = new HashMap<String, Interface>();
			for (Interface intf : config.getInterfaces()){
				interfaces.put(intf.getName(), intf);
			}
			bgpPeers = new HashMap<InetAddress, BgpPeer>();
			for (BgpPeer peer : config.getPeers()){
				bgpPeers.put(peer.getIpAddress(), peer);
			}
			
			bgpdAttachmentPoint = new SwitchPort(
					new Dpid(config.getBgpdAttachmentDpid()),
					new Port(config.getBgpdAttachmentPort()));
			
		} catch (JsonParseException e) {
			log.error("Error in JSON file", e);
			System.exit(1);
		} catch (JsonMappingException e) {
			log.error("Error in JSON file", e);
			System.exit(1);
		} catch (IOException e) {
			log.error("Error reading JSON file", e);
			System.exit(1);
		}
		
		//Populate the interface Patricia Trie
		for (Interface intf : interfaces.values()) {
			Prefix prefix = new Prefix(intf.getIpAddress().getAddress(), intf.getPrefixLength());
			interfacePtrie.put(prefix, intf);
		}
		
		/*
		Iterator<IPatriciaTrie.Entry<Interface>> it = interfacePtrie.iterator();
		while (it.hasNext()) {
			IPatriciaTrie.Entry<Interface> entry = it.next();
			Interface intf = entry.getValue();
			log.debug("Interface at prefix {}, switchport {}/{}",
					new Object[] {entry.getPrefix(), HexString.toHexString(intf.getDpid()), intf.getPort()});
		}
		*/
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l 
			= new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IBgpRouteService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m 
			= new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IBgpRouteService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l 
			= new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(ITopologyService.class);
		l.add(IDeviceService.class);
		l.add(IRestApiService.class);
		return l;
	}
	
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
	    
	    //ptree = new Ptree(32);
		ptree = new PatriciaTrie<RibEntry>(32);
		interfacePtrie = new PatriciaTrie<Interface>(32);
	    
	    ribUpdates = new LinkedBlockingQueue<RibUpdate>();
	    	
		// Register floodlight provider and REST handler.
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		topology = context.getServiceImpl(ITopologyService.class);
		devices = context.getServiceImpl(IDeviceService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		
		//TODO We'll initialise this here for now, but it should really be done as
		//part of the controller core
		proxyArp = new ProxyArpManager(floodlightProvider, topology, devices);
		
		linkUpdates = new ArrayList<LDUpdate>();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		topologyChangeDetectorTask = new SingletonTask(executor, new TopologyChangeDetector());

		topoRouteService = new TopoRouteService("");
		
		pathsWaitingOnArp = Multimaps.synchronizedSetMultimap(
				HashMultimap.<InetAddress, PathUpdate>create());
		prefixesWaitingOnArp = Multimaps.synchronizedSetMultimap(
				HashMultimap.<InetAddress, RibUpdate>create());
		
		pushedPaths = new HashMap<InetAddress, PathUpdate>();
		prefixToPath = new HashMap<Prefix, PathUpdate>();
		pushedFlows = HashMultimap.<Prefix, PushedFlowMod>create();
		
		bgpUpdatesExecutor = Executors.newSingleThreadExecutor(
				new ThreadFactoryBuilder().setNameFormat("bgp-updates-%d").build());
		
		//Read in config values
		bgpdRestIp = context.getConfigParams(this).get("BgpdRestIp");
		if (bgpdRestIp == null){
			log.error("BgpdRestIp property not found in config file");
			System.exit(1);
		}
		else {
			log.info("BgpdRestIp set to {}", bgpdRestIp);
		}
		
		routerId = context.getConfigParams(this).get("RouterId");
		if (routerId == null){
			log.error("RouterId property not found in config file");
			System.exit(1);
		}
		else {
			log.info("RouterId set to {}", routerId);
		}
		
		String configFilenameParameter = context.getConfigParams(this).get("configfile");
		if (configFilenameParameter != null){
			configFilename = configFilenameParameter;
		}
		log.debug("Config file set to {}", configFilename);
		
		readGatewaysConfiguration(configFilename);
		// Test.
		//test();
	}

	//public Ptree getPtree() {
	public IPatriciaTrie<RibEntry> getPtree() {
		return ptree;
	}
	
	public void clearPtree() {
		//ptree = null;
		//ptree = new Ptree(32);
		ptree = new PatriciaTrie<RibEntry>(32);
	}
	
	public String getBGPdRestIp() {
		return bgpdRestIp;
	}
	
	public String getRouterId() {
		return routerId;
	}
	
	// Return nexthop address as byte array.
	/*
	public RibEntry lookupRib(byte[] dest) {
		if (ptree == null) {
		    log.debug("lookupRib: ptree null");
		    return null;
		}
		
		PtreeNode node = ptree.match(dest, 32);
		if (node == null) {
            log.debug("lookupRib: ptree node null");
			return null;
		}
		
		if (node.rib == null) {
            log.debug("lookupRib: ptree rib null");
			return null;
		}
		
		ptree.delReference(node);
		
		return node.rib;
	}
	*/
	
	/*
	//TODO looks like this should be a unit test
	@SuppressWarnings("unused")
    private void test() throws UnknownHostException {
		System.out.println("Here it is");
		Prefix p = new Prefix("128.0.0.0", 8);
		Prefix q = new Prefix("8.0.0.0", 8);
		Prefix r = new Prefix("10.0.0.0", 24);
		Prefix a = new Prefix("10.0.0.1", 32);
	
		ptree.acquire(p.getAddress(), p.getPrefixLength());
		ptree.acquire(q.getAddress(), q.getPrefixLength());
		ptree.acquire(r.getAddress(), r.getPrefixLength());
	
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
	
		PtreeNode n = ptree.match(a.getAddress(), a.getPrefixLength());
		if (n != null) {
			System.out.println("Matched prefix for 10.0.0.1:");
			Prefix x = new Prefix(n.key, n.keyBits);
			ptree.delReference(n);
		}
		
		n = ptree.lookup(p.getAddress(), p.getPrefixLength());
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
		
		n = ptree.lookup(q.getAddress(), q.getPrefixLength());
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
		
		n = ptree.lookup(r.getAddress(), r.getPrefixLength());
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}

	}
	*/
	
	//TODO once the Ptree is object oriented this can go
	/*
	private String getPrefixFromPtree(PtreeNode node){
        InetAddress address = null;
        try {
			address = InetAddress.getByAddress(node.key);
		} catch (UnknownHostException e1) {
			//Should never happen is the reverse conversion has already been done
			log.error("Malformed IP address");
			return "";
		}
        return address.toString() + "/" + node.rib.masklen;
	}
	*/
	
	private void retrieveRib(){
		String url = "http://" + bgpdRestIp + "/wm/bgp/" + routerId;
		String response = RestClient.get(url);
		
		if (response.equals("")){
			return;
		}
		
		response = response.replaceAll("\"", "'");
		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(response);  
		JSONArray rib_json_array = jsonObj.getJSONArray("rib");
		String router_id = jsonObj.getString("router-id");

		int size = rib_json_array.size();

		log.info("Retrived RIB of {} entries from BGPd", size);
		
		for (int j = 0; j < size; j++) {
			JSONObject second_json_object = rib_json_array.getJSONObject(j);
			String prefix = second_json_object.getString("prefix");
			String nexthop = second_json_object.getString("nexthop");

			//insert each rib entry into the local rib;
			String[] substring = prefix.split("/");
			String prefix1 = substring[0];
			String mask1 = substring[1];

			Prefix p;
			try {
				p = new Prefix(prefix1, Integer.valueOf(mask1));
			} catch (NumberFormatException e) {
				log.warn("Wrong mask format in RIB JSON: {}", mask1);
				continue;
			} catch (IllegalArgumentException e1) {
				log.warn("Wrong prefix format in RIB JSON: {}", prefix1);
				continue;
			}
			
			//PtreeNode node = ptree.acquire(p.getAddress(), p.getPrefixLength());
			RibEntry rib = new RibEntry(router_id, nexthop);
			
			/*
			if (node.rib != null) {
				node.rib = null;
				ptree.delReference(node);
			}
			
			node.rib = rib;
			*/
			
			//ptree.put(p, rib);
			
			//addPrefixFlows(p, rib);
			try {
				ribUpdates.put(new RibUpdate(Operation.UPDATE, p, rib));
			} catch (InterruptedException e) {
				log.debug("Interrupted while pushing onto update queue");
			}
		} 
	}
	
	@Override
	public void newRibUpdate(RibUpdate update) {
		try {
			ribUpdates.put(update);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.debug(" ", e);
		}
	}
	
	public synchronized void processRibAdd(RibUpdate update) {
		Prefix prefix = update.getPrefix();
		
		log.debug("Processing prefix add {}", prefix);
		
		//PtreeNode node = ptree.acquire(prefix.getAddress(), prefix.getPrefixLength());
		RibEntry rib = ptree.put(prefix, update.getRibEntry());
		
		//if (node.rib != null) {
		if (rib != null && !rib.equals(update.getRibEntry())) {
			//There was an existing nexthop for this prefix. This update supersedes that,
			//so we need to remove the old flows for this prefix from the switches
			//deletePrefixFlows(prefix);
			_processDeletePrefix(prefix, rib);
			
			//Then remove the old nexthop from the Ptree
			//node.rib = null;
			//ptree.delReference(node);
		}
		
		//Put the new nexthop in the Ptree
		//node.rib = update.getRibEntry();
		
		//log.debug("hurro {}", InetAddresses.forString("0.0.0.0").getHostAddress()); 
				//InetAddresses.forString("0.0.0.0").getHostAddress()});//, rib.getNextHop().equals(InetAddresses.forString("0.0.0.0"))});
		if (update.getRibEntry().getNextHop().equals(InetAddresses.forString("0.0.0.0"))) {
			//Route originated by SDN domain
			//We don't handle these at the moment
			log.debug("Own route {} to {}", prefix, update.getRibEntry().getNextHop().getHostAddress());
			return;
		}
		
		_processRibAdd(update);
		
		//Push flows for the new <prefix, nexthop>
		//addPrefixFlows(prefix, update.getRibEntry());
	}
	
	private void _processRibAdd(RibUpdate update) {
		Prefix prefix = update.getPrefix();
		RibEntry rib = update.getRibEntry();
		
		InetAddress dstIpAddress = rib.getNextHop();
		
		byte[] nextHopMacAddress = proxyArp.getMacAddress(rib.getNextHop());
		//Interface egressInterface = null;
		Interface egressInterface = null;//getEgressInterface(prefix, rib.getNextHop());
		
		if (bgpPeers.containsKey(dstIpAddress)) {
			//Route to a peer
			log.debug("Route to peer {}", dstIpAddress);
			BgpPeer peer = bgpPeers.get(dstIpAddress);
			egressInterface = interfaces.get(peer.getInterfaceName());
			
			/*
			if (nextHopMacAddress == null) {
				//A RibUpdate is still a nice way to package them up
				prefixesWaitingOnArp.put(rib.getNextHop(), 
						new RibUpdate(Operation.UPDATE, prefix, rib));
				proxyArp.sendArpRequest(rib.getNextHop(), this, true);
				return;
			}
			else {
				addPrefixFlows(prefix, egressInterface, nextHopMacAddress);
			}
			*/
		}
		else {
			//Route to someone else
			log.debug("Route to non-peer {}", dstIpAddress);
			egressInterface = interfacePtrie.match(
					new Prefix(dstIpAddress.getAddress(), 32));
			if (egressInterface == null) {
				log.warn("No outgoing interface found for {}", dstIpAddress.getHostAddress());
				return;
			}
			/*
			if (nextHopMacAddress == null) {
				//A RibUpdate is still a nice way to package them up
				prefixesWaitingOnArp.put(rib.getNextHop(), 
						new RibUpdate(Operation.UPDATE, prefix, rib));
				//pathsWaitingOnArp.put(rib.getNextHop(), 
					//	new PathUpdate(egressInterface, rib.getNextHop()));
				
				proxyArp.sendArpRequest(rib.getNextHop(), this, true);
				return;
			}
			else {
				//calculateAndPushPath(egressInterface, new MACAddress(nextHopMacAddress));
				setUpDataPath(egressInterface, rib.getNextHop(), MACAddress.valueOf(nextHopMacAddress));
				//installPathToNextHop(egressInterface, rib.getNextHop());
				addPrefixFlows(prefix, egressInterface, nextHopMacAddress);
			}*/
		}
		
		if (nextHopMacAddress == null) {
			prefixesWaitingOnArp.put(dstIpAddress, 
					new RibUpdate(Operation.UPDATE, prefix, rib));
			proxyArp.sendArpRequest(dstIpAddress, this, true);
			return;
		}
		else {
			if (!bgpPeers.containsKey(dstIpAddress)) {
				//setUpDataPath(new PathUpdate(egressInterface, dstIpAddress),
				//PathUpdate path = new PathUpdate(egressInterface, dstIpAddress);
				PathUpdate path = pushedPaths.get(dstIpAddress);
				if (path == null) {
					path = new PathUpdate(egressInterface, dstIpAddress);
					pushedPaths.put(dstIpAddress, path);
				}
				//PathUpdate path = setUpDataPath(egressInterface, dstIpAddress,
				//		MACAddress.valueOf(nextHopMacAddress));
				setUpDataPath(path, MACAddress.valueOf(nextHopMacAddress));
				
				path.incrementUsers();
				prefixToPath.put(prefix, path);
			}
			addPrefixFlows(prefix, egressInterface, nextHopMacAddress);
		}
	}
	
	/*
	private Interface getEgressInterface(Prefix prefix, InetAddress nextHop) {
		if (bgpPeers.containsKey(nextHop)) {
			//Route to a peer
			log.debug("Route to peer {}", nextHop);
			BgpPeer peer = bgpPeers.get(nextHop);
			return interfaces.get(peer.getInterfaceName());			
		}
		else {
			//Route to someone else
			log.debug("Route to non-peer {}", nextHop);
			return interfacePtrie.match(prefix);
		}
	}
	*/
	
	public synchronized void processRibDelete(RibUpdate update) {
		Prefix prefix = update.getPrefix();
		
		//PtreeNode node = ptree.lookup(prefix.getAddress(), prefix.getPrefixLength());
		
		/* 
		 * Remove the flows from the switches before the rib is lost
		 * Theory: we could get a delete for a prefix not in the Ptree.
		 * This would result in a null node being returned. We could get a delete for
		 * a node that's not actually there, but is a aggregate node. This would result
		 * in a non-null node with a null rib. Only a non-null node with a non-null
		 * rib is an actual prefix in the Ptree.
		 */

		/*
		if (node != null && node.rib != null) {
			if (update.getRibEntry().equals(node.rib)) {
				node.rib = null;
				ptree.delReference(node);
				
				deletePrefixFlows(update.getPrefix());
			}
		}
		*/
		
		if (ptree.remove(prefix, update.getRibEntry())) {
			/*
			 * Only delete flows if an entry was actually removed from the trie.
			 * If no entry was removed, the <prefix, nexthop> wasn't there so
			 * it's probably already been removed and we don't need to do anything
			 */
			_processDeletePrefix(prefix, update.getRibEntry());
			
			//TODO may need to delete a path here too
		}
	}
	
	private void _processDeletePrefix(Prefix prefix, RibEntry ribEntry) {
		deletePrefixFlows(prefix);
		
		log.debug("Deleting {} to {}", prefix, ribEntry.getNextHop());
		log.debug("is peer {}", bgpPeers.containsKey(ribEntry.getNextHop()));
		if (!bgpPeers.containsKey(ribEntry.getNextHop())) {
			log.debug("Getting path for route with non-peer nexthop");
			PathUpdate path = prefixToPath.get(prefix);
			
			if (path == null) {
				log.error("No path found for non-peer path");
			}
			
			path.decrementUsers();
			log.debug("users {}, permanent {}", path.getUsers(), path.isPermanent());
			if (path.getUsers() <= 0 && !path.isPermanent()) {
				deletePath(path);
			}
		}
	}
	
	private void deletePath(PathUpdate path) {
		for (PushedFlowMod pfm : path.getFlowMods()) {
			log.debug("Pushing a DELETE flow mod to {}, dst MAC {}",
					new Object[] {HexString.toHexString(pfm.getDpid()),
					HexString.toHexString(pfm.getFlowMod().getMatch().getDataLayerDestination())
			});
			
			sendDeleteFlowMod(pfm.getFlowMod(), pfm.getDpid());
		}
	}
	
	private void sendDeleteFlowMod(OFFlowMod addFlowMod, long dpid) {
		addFlowMod.setCommand(OFFlowMod.OFPFC_DELETE_STRICT)
		.setOutPort(OFPort.OFPP_NONE)
		.setLengthU(OFFlowMod.MINIMUM_LENGTH);
		
		addFlowMod.getActions().clear();
		
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		if (sw == null) {
        	log.warn("Switch not found when pushing delete flow mod");
        	return;
		}
		
		try {
			sw.write(addFlowMod, null);
			sw.flush();
		} catch (IOException e) {
			log.error("Failure writing flow mod", e);
		}
	}
	
	//TODO compatibility layer, used by beginRouting()
	/*public void prefixAdded(PtreeNode node) {
		Prefix prefix = null;
		try {
			prefix = new Prefix(node.key, node.rib.masklen);
		} catch (IllegalArgumentException e) {
			log.error(" ", e);
		}

		addPrefixFlows(prefix, node.rib);
	}*/

	private void addPrefixFlows(Prefix prefix, Interface egressInterface, byte[] nextHopMacAddress) {
		//TODO get rid of this
		/*if (!topologyReady){
			return;
		}*/
		
		//TODO before we do anything, we have to check that the RIB entry is still in the
		//Ptree because it could have been removed while we were waiting for ARP.
		//I think we'll have to make prefixAdded and prefixDelete atomic as well
		//to protect against the prefix getting deleted while where trying to add it

		log.debug("Adding flows for prefix {} added, next hop mac {}",
				prefix, HexString.toHexString(nextHopMacAddress));
				//prefix, rib.getNextHop().getHostAddress());
		

		
		//TODO this is wrong, we shouldn't be dealing with BGP peers here.
		//We need to figure out where the device is attached and what its
		//mac address is by learning. 
		//The next hop is not necessarily the peer, and the peer's attachment
		//point is not necessarily the next hop's attachment point.

		
		
		//if (peer == null){
			//TODO local router isn't in peers list so this will get thrown
			//Need to work out what to do about local prefixes with next hop 0.0.0.0.
			
			//The other scenario is this is a route server route. In that
			//case the next hop is not in our configuration
			//log.error("Couldn't find next hop router in router {} in config",
			//		rib.getNextHop().getHostAddress());
			//return; //just quit out here? This is probably a configuration error
		//}
		
		//Get MAC address for peer from the ARP module
		//TODO separate out the 'ask for MAC' bit to another method
		//byte[] peerMacAddress = proxyArp.getMacAddress(peer.getIpAddress());
		//byte[] nextHopMacAddress = proxyArp.getMacAddress(rib.getNextHop());
		//if (nextHopMacAddress == null) {

		//}
		
		//Interface peerInterface = interfaces.get(peer.getInterfaceName());

		//Add a flow to rewrite mac for this prefix to all border switches
		for (Interface srcInterface : interfaces.values()) {
			if (srcInterface == egressInterface) {
				//Don't push a flow for the switch where this peer is attached
				continue;
			}
						
			DataPath shortestPath = topoRouteService.getShortestPath(
					srcInterface.getSwitchPort(),
					egressInterface.getSwitchPort());
			
			if (shortestPath == null){
				log.debug("Shortest path between {} and {} not found",
						srcInterface.getSwitchPort(),
						egressInterface.getSwitchPort());
				return; // just quit here?
			}
			
			//Set up the flow mod
			OFFlowMod fm =
	                (OFFlowMod) floodlightProvider.getOFMessageFactory()
	                                              .getMessage(OFType.FLOW_MOD);
			
	        fm.setIdleTimeout((short)0)
	        .setHardTimeout((short)0)
	        .setBufferId(OFPacketOut.BUFFER_ID_NONE)
	        .setCookie(MAC_RW_COOKIE)
	        .setCommand(OFFlowMod.OFPFC_ADD)
	        .setPriority(SDNIP_PRIORITY)
	        .setLengthU(OFFlowMod.MINIMUM_LENGTH
	        		+ OFActionDataLayerDestination.MINIMUM_LENGTH
	        		+ OFActionOutput.MINIMUM_LENGTH);
	        
	        OFMatch match = new OFMatch();
	        match.setDataLayerType(Ethernet.TYPE_IPv4);
	        match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
	        
	        /*
	        InetAddress address = null;
	        try {
	        	address = InetAddress.getByAddress(prefix.getAddress());
			} catch (UnknownHostException e1) {
				//Should never happen is the reverse conversion has already been done
				log.error("Malformed IP address");
				return;
			}*/
	        
	        //match.setFromCIDR(address.getHostAddress() + "/" + 
	        //		prefix.getPrefixLength(), OFMatch.STR_NW_DST);
	        match.setFromCIDR(prefix.toString(), OFMatch.STR_NW_DST);
	        fm.setMatch(match);
	        
	        //Set up MAC rewrite action
	        OFActionDataLayerDestination macRewriteAction = new OFActionDataLayerDestination();
	        //TODO the peer's mac address is not necessarily the next hop's...
	        macRewriteAction.setDataLayerAddress(nextHopMacAddress);
	        
	        //Set up output action
	        OFActionOutput outputAction = new OFActionOutput();
	        outputAction.setMaxLength((short)0xffff);
	        
	        Port outputPort = shortestPath.flowEntries().get(0).outPort();
	        outputAction.setPort(outputPort.value());
	        
	        List<OFAction> actions = new ArrayList<OFAction>();
	        actions.add(macRewriteAction);
	        actions.add(outputAction);
	        fm.setActions(actions);
	        
	        //Write to switch
	        IOFSwitch sw = floodlightProvider.getSwitches()
	        			.get(srcInterface.getDpid());
	        
            if (sw == null){
            	log.warn("Switch not found when pushing flow mod");
            	continue;
            }
            
            //TODO if prefix Added/Deleted are synchronized this shouldn't have to be
            pushedFlows.put(prefix, new PushedFlowMod(sw.getId(), fm));
            
            List<OFMessage> msglist = new ArrayList<OFMessage>();
            msglist.add(fm);
            try {
				sw.write(msglist, null);
				sw.flush();
			} catch (IOException e) {
				log.error("Failure writing flow mod", e);
			}
		}
	}
	
	//TODO test next-hop changes
	//TODO check delete/add synchronization
		
	private void deletePrefixFlows(Prefix prefix) {
		/*if (!topologyReady) {
			return;
		}*/
				
		/*for (Map.Entry<Prefix, PushedFlowMod> entry : pushedFlows.entries()) {
			log.debug("Pushed flow: {} => {}", entry.getKey(), entry.getValue());
		}*/
		
		Collection<PushedFlowMod> pushedFlowMods 
				= pushedFlows.removeAll(prefix);
		
		for (PushedFlowMod pfm : pushedFlowMods) {
			log.debug("Pushing a DELETE flow mod to {}, matches prefix {} with mac-rewrite {}",
					new Object[] {HexString.toHexString(pfm.getDpid()),
					pfm.getFlowMod().getMatch().getNetworkDestination() + 
					pfm.getFlowMod().getMatch().getNetworkDestinationMaskLen(),
					HexString.toHexString(((OFActionDataLayerDestination)pfm.getFlowMod().getActions().get(0))
							.getDataLayerAddress())});
			
			sendDeleteFlowMod(pfm.getFlowMod(), pfm.getDpid());
			/*
			OFFlowMod fm = pfm.getFlowMod();
			
			fm.setCommand(OFFlowMod.OFPFC_DELETE)
			.setOutPort(OFPort.OFPP_NONE)
			.setLengthU(OFFlowMod.MINIMUM_LENGTH);
			
			fm.getActions().clear();
			
			IOFSwitch sw = floodlightProvider.getSwitches().get(pfm.getDpid());
			if (sw == null) {
            	log.warn("Switch not found when pushing delete flow mod");
            	continue;
			}
			
			try {
				sw.write(fm, null);
				sw.flush();
			} catch (IOException e) {
				log.error("Failure writing flow mod", e);
			}
			*/
		}
	}
	
	/*
	 * On startup we need to calculate a full mesh of paths between all gateway
	 * switches
	 */
	private void setupFullMesh(){
		//For each border router, calculate and install a path from every other
		//border switch to said border router. However, don't install the entry
		//in to the first hop switch, as we need to install an entry to rewrite
		//for each prefix received. This will be done later when prefixes have 
		//actually been received.
		
		for (BgpPeer peer : bgpPeers.values()) {
			Interface peerInterface = interfaces.get(peer.getInterfaceName());
			
			//We know there's not already a Path here pushed, because this is
			//called before all other routing
			PathUpdate path = new PathUpdate(peerInterface, peer.getIpAddress());
			path.setPermanent();
			
			//See if we know the MAC address of the peer. If not we can't
			//do anything until we learn it
			byte[] mac = proxyArp.getMacAddress(peer.getIpAddress());
			if (mac == null) {
				log.debug("Don't know MAC for {}", peer.getIpAddress().getHostAddress());
				//Put in the pending paths list first
				pathsWaitingOnArp.put(peer.getIpAddress(),
						//new PathUpdate(peerInterface, peer.getIpAddress()));
						path);
				
				proxyArp.sendArpRequest(peer.getIpAddress(), this, true);
				continue;
			}
			
			//If we know the MAC, lets go ahead and push the paths to this peer
			//calculateAndPushPath(peerInterface, MACAddress.valueOf(mac));
			//setUpDataPath(peerInterface, peer.getIpAddress(), MACAddress.valueOf(mac));
			setUpDataPath(path, MACAddress.valueOf(mac));
		}
	}
	
	//private void setUpDataPath(Interface dstInterface, InetAddress dstInetAddress,
	//private PathUpdate setUpDataPath(Interface dstInterface, InetAddress dstInetAddress, MACAddress dstMacAddress) {
	private void setUpDataPath(PathUpdate path, MACAddress dstMacAddress) {
		//PathUpdate path = pushedPaths.get(dstInterface);
		
		
		//if (!pushedPaths.containsKey(path.getDstIpAddress())) {
		//if (path == null) {
			//PathUpdate path = new PathUpdate(dstInterface, dstInetAddress);
			//path = new PathUpdate(dstInterface, dstInetAddress);
			calculateAndPushPath(path, dstMacAddress);
			//pushedPaths.put(path.getDstIpAddress(), path);
			//calculateAndPushPath(dstInterface, dstMacAddress);
			//pushedPaths.put(dstInetAddress, new PathUpdate(dstInterface, dstInetAddress));
		//}
		
		//return path;
		
		/*
		else {
			existingPath.incrementUsers();
		}
		*/
	}
	
	//private void calculateAndPushPath(Interface dstInterface, MACAddress dstMacAddress) {
	private void calculateAndPushPath(PathUpdate path, MACAddress dstMacAddress) {
		Interface dstInterface = path.getDstInterface();
		
		List<PushedFlowMod> pushedFlows = new ArrayList<PushedFlowMod>();
		
		for (Interface srcInterface : interfaces.values()) {
			if (dstInterface.equals(srcInterface.getName())){
				continue;
			}
			
			DataPath shortestPath = topoRouteService.getShortestPath(
						srcInterface.getSwitchPort(), dstInterface.getSwitchPort()); 
			
			if (shortestPath == null){
				log.debug("Shortest path between {} and {} not found",
						srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
				return; // just quit here?
			}
			
			//List<PushedFlowMod> pushedFlows 
				//	= installPath(shortestPath.flowEntries(), dstMacAddress);
			pushedFlows.addAll(installPath(shortestPath.flowEntries(), dstMacAddress));
		}
		
		path.setFlowMods(pushedFlows);
	}
	
	private List<PushedFlowMod> installPath(List<FlowEntry> flowEntries, MACAddress dstMacAddress){
		List<PushedFlowMod> flowMods = new ArrayList<PushedFlowMod>();
		
		//Set up the flow mod
		OFFlowMod fm =
                (OFFlowMod) floodlightProvider.getOFMessageFactory()
                                              .getMessage(OFType.FLOW_MOD);
		
        OFActionOutput action = new OFActionOutput();
        action.setMaxLength((short)0xffff);
        List<OFAction> actions = new ArrayList<OFAction>();
        actions.add(action);
        
        fm.setIdleTimeout((short)0)
        .setHardTimeout((short)0)
        .setBufferId(OFPacketOut.BUFFER_ID_NONE)
        .setCookie(L2_FWD_COOKIE)
        .setCommand(OFFlowMod.OFPFC_ADD)
        .setActions(actions)
        .setLengthU(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH);
        
        //Don't push the first flow entry. We need to push entries in the
		//first switch based on IP prefix which we don't know yet.
        for (int i = 1; i < flowEntries.size(); i++){        	
        	FlowEntry flowEntry = flowEntries.get(i);
           
            OFMatch match = new OFMatch();
            match.setDataLayerDestination(dstMacAddress.toBytes());
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
            ((OFActionOutput) fm.getActions().get(0)).setPort(flowEntry.outPort().value());
            
            fm.setMatch(match);
            
            IOFSwitch sw = floodlightProvider.getSwitches().get(flowEntry.dpid().value());
            
            if (sw == null){
            	log.warn("Switch not found when pushing flow mod");
            	continue;
            }
            
            flowMods.add(new PushedFlowMod(sw.getId(), fm));
            
            List<OFMessage> msglist = new ArrayList<OFMessage>();
            msglist.add(fm);
            try {
				sw.write(msglist, null);
				sw.flush();
			} catch (IOException e) {
				log.error("Failure writing flow mod", e);
			}
            
            try {
                fm = fm.clone();
            } catch (CloneNotSupportedException e1) {
                log.error("Failure cloning flow mod", e1);
            }
		}
        
        return flowMods;
	}
	
	private void setupBgpPaths(){
		for (BgpPeer bgpPeer : bgpPeers.values()){
			Interface peerInterface = interfaces.get(bgpPeer.getInterfaceName());
			
			DataPath path = topoRouteService.getShortestPath(
					peerInterface.getSwitchPort(), bgpdAttachmentPoint);
			
			if (path == null){
				log.debug("Unable to compute path for BGP traffic for {}",
							bgpPeer.getIpAddress());
				continue;
			}
			
			//Set up the flow mod
			OFFlowMod fm =
	                (OFFlowMod) floodlightProvider.getOFMessageFactory()
	                                              .getMessage(OFType.FLOW_MOD);
			
	        OFActionOutput action = new OFActionOutput();
	        action.setMaxLength((short)0xffff);
	        List<OFAction> actions = new ArrayList<OFAction>();
	        actions.add(action);
	        
	        fm.setIdleTimeout((short)0)
	        .setHardTimeout((short)0)
	        .setBufferId(OFPacketOut.BUFFER_ID_NONE)
	        .setCookie(BGP_COOKIE)
	        .setCommand(OFFlowMod.OFPFC_ADD)
	        .setPriority(SDNIP_PRIORITY)
	        .setActions(actions)
	        .setLengthU(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH);

	        //Forward = gateway -> bgpd, reverse = bgpd -> gateway
	        OFMatch forwardMatchSrc = new OFMatch();
	        
	        String interfaceCidrAddress = peerInterface.getIpAddress().getHostAddress() 
	        					+ "/32";
	        String peerCidrAddress = bgpPeer.getIpAddress().getHostAddress()
	        					+ "/32";
	        
	        //Common match fields
	        forwardMatchSrc.setDataLayerType(Ethernet.TYPE_IPv4);
	        //forwardMatch.setWildcards(forwardMatch.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
	        forwardMatchSrc.setNetworkProtocol(IPv4.PROTOCOL_TCP);
	        forwardMatchSrc.setTransportDestination(BGP_PORT);
	        forwardMatchSrc.setWildcards(forwardMatchSrc.getWildcards() & ~OFMatch.OFPFW_IN_PORT
	        				& ~OFMatch.OFPFW_DL_TYPE & ~OFMatch.OFPFW_NW_PROTO);
	        
	        
	        OFMatch reverseMatchSrc = forwardMatchSrc.clone();
	        
	        forwardMatchSrc.setFromCIDR(peerCidrAddress, OFMatch.STR_NW_SRC);
	        forwardMatchSrc.setFromCIDR(interfaceCidrAddress, OFMatch.STR_NW_DST);
	        
	        OFMatch forwardMatchDst = forwardMatchSrc.clone();
	        
	        forwardMatchSrc.setTransportSource(BGP_PORT);
	        forwardMatchSrc.setWildcards(forwardMatchSrc.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
	        forwardMatchDst.setTransportDestination(BGP_PORT);
	        forwardMatchDst.setWildcards(forwardMatchDst.getWildcards() & ~OFMatch.OFPFW_TP_DST);
	        
	        reverseMatchSrc.setFromCIDR(interfaceCidrAddress, OFMatch.STR_NW_SRC);
	        reverseMatchSrc.setFromCIDR(peerCidrAddress, OFMatch.STR_NW_DST);
	        
	        OFMatch reverseMatchDst = reverseMatchSrc.clone();
	        
	        reverseMatchSrc.setTransportSource(BGP_PORT);
	        reverseMatchSrc.setWildcards(forwardMatchSrc.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
	        reverseMatchDst.setTransportDestination(BGP_PORT);
	        reverseMatchDst.setWildcards(forwardMatchDst.getWildcards() & ~OFMatch.OFPFW_TP_DST);
	        
	        fm.setMatch(forwardMatchSrc);
	        
	        OFMatch forwardIcmpMatch = new OFMatch();
	        forwardIcmpMatch.setDataLayerType(Ethernet.TYPE_IPv4);
	        forwardIcmpMatch.setNetworkProtocol(IPv4.PROTOCOL_ICMP);
	        forwardIcmpMatch.setWildcards(forwardIcmpMatch.getWildcards() &
	        		~OFMatch.OFPFW_DL_TYPE & ~OFMatch.OFPFW_NW_PROTO);
	        
	        OFMatch reverseIcmpMatch = forwardIcmpMatch.clone();
	        forwardIcmpMatch.setFromCIDR(interfaceCidrAddress, OFMatch.STR_NW_DST);
	        reverseIcmpMatch.setFromCIDR(interfaceCidrAddress, OFMatch.STR_NW_SRC);
	        
			for (FlowEntry flowEntry : path.flowEntries()){
				OFFlowMod forwardFlowModSrc, forwardFlowModDst;
				OFFlowMod reverseFlowModSrc, reverseFlowModDst;
				OFFlowMod forwardIcmp, reverseIcmp;
				try {
					forwardFlowModSrc = fm.clone();
					forwardFlowModDst = fm.clone();
					reverseFlowModSrc = fm.clone();
					reverseFlowModDst = fm.clone();
					forwardIcmp = fm.clone();
					reverseIcmp = fm.clone();
				} catch (CloneNotSupportedException e) {
					log.warn("Clone failed", e);
					continue;
				}
				
				forwardMatchSrc.setInputPort(flowEntry.inPort().value());
				forwardFlowModSrc.setMatch(forwardMatchSrc);
				((OFActionOutput)forwardFlowModSrc.getActions().get(0))
						.setPort(flowEntry.outPort().value());
				
				forwardMatchDst.setInputPort(flowEntry.inPort().value());
				forwardFlowModDst.setMatch(forwardMatchDst);
				((OFActionOutput)forwardFlowModDst.getActions().get(0))
						.setPort(flowEntry.outPort().value());
				
				reverseMatchSrc.setInputPort(flowEntry.outPort().value());
				reverseFlowModSrc.setMatch(reverseMatchSrc);
				((OFActionOutput)reverseFlowModSrc.getActions().get(0))
						.setPort(flowEntry.inPort().value());
				
				reverseMatchDst.setInputPort(flowEntry.outPort().value());
				reverseFlowModDst.setMatch(reverseMatchDst);
				((OFActionOutput)reverseFlowModDst.getActions().get(0))
						.setPort(flowEntry.inPort().value());
				
				((OFActionOutput)forwardIcmp.getActions().get(0))
						.setPort(flowEntry.outPort().value());
				forwardIcmp.setMatch(forwardIcmpMatch);
				
				((OFActionOutput)reverseIcmp.getActions().get(0))
						.setPort(flowEntry.inPort().value());
				reverseIcmp.setMatch(reverseIcmpMatch);
		

				IOFSwitch sw = floodlightProvider.getSwitches().get(flowEntry.dpid().value());
				
				if (sw == null) {
					log.warn("Switch not found when pushing BGP paths");
					return;
				}
				
				List<OFMessage> msgList = new ArrayList<OFMessage>(2);
				msgList.add(forwardFlowModSrc);
				msgList.add(forwardFlowModDst);
				msgList.add(reverseFlowModSrc);
				msgList.add(reverseFlowModDst);
				msgList.add(forwardIcmp);
				msgList.add(reverseIcmp);
				
				try {
					sw.write(msgList, null);
					sw.flush();
				} catch (IOException e) {
					log.error("Failure writing flow mod", e);
				}
			}
		}
	}
	
	@Override
	public void arpResponse(InetAddress ipAddress, byte[] macAddress) {
		log.debug("Received ARP response: {} => {}", ipAddress.getHostAddress(), 
				MACAddress.valueOf(macAddress).toString());
		
		/*
		 * We synchronize on this to prevent changes to the ptree while we're pushing
		 * flows to the switches. If the ptree changes, the ptree and switches
		 * could get out of sync. 
		 */
		synchronized (this) {
			Set<PathUpdate> pathsToPush = pathsWaitingOnArp.removeAll(ipAddress);
			
			for (PathUpdate update : pathsToPush) {
				log.debug("Pushing path to {} at {} on {}", new Object[] {
						update.getDstIpAddress().getHostAddress(), 
						MACAddress.valueOf(macAddress),
						update.getDstInterface().getSwitchPort()});
				//calculateAndPushPath(update.getDstInterface(), 
						//MACAddress.valueOf(macAddress));
				//setUpDataPath(update.getDstInterface(), update.getDstIpAddress(), 
				if (pushedPaths.containsKey(update.getDstInterface())) {
					//A path already got pushed to this endpoint while we were waiting
					//for ARP. We'll copy over the permanent attribute if it is set on this path.
					if (update.isPermanent()) {
						pushedPaths.get(update.getDstInterface()).setPermanent();
					}
				}
				else {
					setUpDataPath(update, MACAddress.valueOf(macAddress));
					pushedPaths.put(update.getDstIpAddress(), update);
				}
			}
			
			
			
			Set<RibUpdate> prefixesToPush = prefixesWaitingOnArp.removeAll(ipAddress);
			
			for (RibUpdate update : prefixesToPush) {
				//These will always be adds
				
				//addPrefixFlows(update.getPrefix(), update.getRibEntry());
				//processRibAdd(update);
				RibEntry rib = ptree.lookup(update.getPrefix()); 
				if (rib != null && rib.equals(update.getRibEntry())) {
					log.debug("Pushing prefix {} next hop {}", update.getPrefix(), 
							rib.getNextHop().getHostAddress());
					//We only push prefix flows if the prefix is still in the ptree
					//and the next hop is the same as our update. The prefix could 
					//have been removed while we were waiting for the ARP, or the 
					//next hop could have changed.
					//addPrefixFlows(update.getPrefix(), rib);
					_processRibAdd(update);
				} else {
					log.debug("Received ARP response, but {},{} is no longer in ptree", 
							update.getPrefix(), update.getRibEntry());
				}
			}
		}
	}
	
	private void beginRouting(){
		log.debug("Topology is now ready, beginning routing function");
		setupBgpPaths();
		setupFullMesh();
		
		//Traverse ptree and create flows for all routes
		/*
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)){
			if (node.rib != null){
				prefixAdded(node);
			}
		}
		*/
		
		/*
		synchronized (ptree) {
			Iterator<IPatriciaTrie.Entry> it = ptree.iterator();
			while (it.hasNext()) {
				IPatriciaTrie.Entry entry = it.next();
				addPrefixFlows(entry.getPrefix(), entry.getRib());
			}
		}
		*/
		
		bgpUpdatesExecutor.execute(new Runnable() {
			@Override
			public void run() {
				doUpdatesThread();
			}
		});
		
	}
	
	private void checkSwitchesConnected(){
		for (String dpid : switches){
			if (floodlightProvider.getSwitches().get(HexString.toLong(dpid)) == null){
				log.debug("Not all switches are here yet");
				return;
			}
		}
		switchesConnected = true;
	}
	
	//Actually we only need to go half way round to verify full mesh connectivity
	//(n^2)/2
	private void checkTopologyReady(){
		for (Interface dstInterface : interfaces.values()) {
			for (Interface srcInterface : interfaces.values()) {			
				if (dstInterface == srcInterface) {
					continue;
				}
				
				DataPath shortestPath = topoRouteService.getShortestPath(
						srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
				
				if (shortestPath == null){
					log.debug("Shortest path between {} and {} not found",
							srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
					return;
				}
			}
		}
		topologyReady = true;
	}
	
	private void checkStatus(){
		//log.debug("In checkStatus, swC {}, toRe {}", switchesConnected, topologyReady);
		
		if (!switchesConnected){
			checkSwitchesConnected();
		}
		boolean oldTopologyReadyStatus = topologyReady;
		if (switchesConnected && !topologyReady){
			checkTopologyReady();
		}
		if (!oldTopologyReadyStatus && topologyReady){
			beginRouting();
		}
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new BgpRouteWebRoutable());
		floodlightProvider.addOFSwitchListener(this);
		topology.addListener(this);
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, proxyArp);
		
		//Retrieve the RIB from BGPd during startup
		retrieveRib();
	}
	
	private void doUpdatesThread() {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					RibUpdate update = ribUpdates.take();
					switch (update.getOperation()){
					case UPDATE:
						processRibAdd(update);
						break;
					case DELETE:
						processRibDelete(update);
						break;
					}
				} catch (InterruptedException e) {
					log.debug("interrupted", e);
					interrupted = true;
				} catch (Exception e) {
					log.debug("exception", e);
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void topologyChanged() {		
		boolean refreshNeeded = false;
		for (LDUpdate ldu : topology.getLastLinkUpdates()){
			if (!ldu.getOperation().equals(ILinkDiscovery.UpdateOperation.LINK_UPDATED)){
				//We don't need to recalculate anything for just link updates
				//They happen very frequently
				refreshNeeded = true;
			}
			
			log.debug("Topo change {}", ldu.getOperation());
			
			if (ldu.getOperation().equals(ILinkDiscovery.UpdateOperation.LINK_ADDED)){
				synchronized (linkUpdates) {
					linkUpdates.add(ldu);
				}
			}
		}
		
		if (refreshNeeded){
			topologyChangeDetectorTask.reschedule(TOPO_DETECTION_WAIT, TimeUnit.SECONDS);
		}
	}

	//TODO determine whether we need to listen for switch joins
	@Override
	public void addedSwitch(IOFSwitch sw) {
		//checkStatus();
	}

	@Override
	public void removedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void switchPortChanged(Long switchId) {}

	@Override
	public String getName() {
		return "BgpRoute";
	}
}
