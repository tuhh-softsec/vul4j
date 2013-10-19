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
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.bgproute.RibUpdate.Operation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoLinkService;
import net.onrc.onos.ofcontroller.core.internal.TopoLinkServiceImpl;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscovery;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryService;
import net.onrc.onos.ofcontroller.proxyarp.IArpRequester;
import net.onrc.onos.ofcontroller.proxyarp.IProxyArpService;
import net.onrc.onos.ofcontroller.proxyarp.ProxyArpManager;
import net.onrc.onos.ofcontroller.topology.ITopologyNetService;
import net.onrc.onos.ofcontroller.topology.TopologyManager;
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
									ITopologyListener, IArpRequester,
									IOFSwitchListener, ILayer3InfoService,
									IProxyArpService {
	
	private static Logger log = LoggerFactory.getLogger(BgpRoute.class);

	private IFloodlightProviderService floodlightProvider;
	private ITopologyService topology;
	private ITopologyNetService topologyNetService;
	private ILinkDiscoveryService linkDiscoveryService;
	private IRestApiService restApi;
	
	private ProxyArpManager proxyArp;
	
	private IPatriciaTrie<RibEntry> ptree;
	private IPatriciaTrie<Interface> interfacePtrie;
	private BlockingQueue<RibUpdate> ribUpdates;
	
	private String bgpdRestIp;
	private String routerId;
	private String configFilename = "config.json";
	
	//We need to identify our flows somehow. But like it says in LearningSwitch.java,
	//the controller/OS should hand out cookie IDs to prevent conflicts.
	private final long APP_COOKIE = 0xa0000000000000L;
	//Cookie for flows that do L2 forwarding within SDN domain to egress routers
	private final long L2_FWD_COOKIE = APP_COOKIE + 1;
	//Cookie for flows in ingress switches that rewrite the MAC address
	private final long MAC_RW_COOKIE = APP_COOKIE + 2;
	//Cookie for flows that setup BGP paths
	private final long BGP_COOKIE = APP_COOKIE + 3;
	//Forwarding uses priority 0, and the mac rewrite entries in ingress switches
	//need to be higher priority than this otherwise the rewrite may not get done
	private final short SDNIP_PRIORITY = 10;
	private final short ARP_PRIORITY = 20;
	
	private final short BGP_PORT = 179;
	
	private final int TOPO_DETECTION_WAIT = 2; //seconds
	
	//Configuration stuff
	private List<String> switches;
	private Map<String, Interface> interfaces;
	private Map<InetAddress, BgpPeer> bgpPeers;
	private SwitchPort bgpdAttachmentPoint;
	private MACAddress bgpdMacAddress;
	
	//True when all switches have connected
	private volatile boolean switchesConnected = false;
	//True when we have a full mesh of shortest paths between gateways
	private volatile boolean topologyReady = false;

	private ArrayList<LDUpdate> linkUpdates;
	private SingletonTask topologyChangeDetectorTask;
	
	private SetMultimap<InetAddress, RibUpdate> prefixesWaitingOnArp;
	
	private Map<InetAddress, Path> pathsWaitingOnArp;
	
	private ExecutorService bgpUpdatesExecutor;
	
	private Map<InetAddress, Path> pushedPaths;
	private Map<Prefix, Path> prefixToPath;
	private Multimap<Prefix, PushedFlowMod> pushedFlows;
	
	private FlowCache flowCache;
	
	private volatile Map<Long, ?> shortestPathTopo = null;

	private class TopologyChangeDetector implements Runnable {
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
						it.remove();
					}
				}
			}
			
			if (!topologyReady) {
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
			
			bgpdMacAddress = config.getBgpdMacAddress();
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
		m.put(IProxyArpService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l 
			= new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(ITopologyService.class);
		l.add(IRestApiService.class);
		return l;
	}
	
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
	    
		ptree = new PatriciaTrie<RibEntry>(32);
		interfacePtrie = new PatriciaTrie<Interface>(32);
	    
	    ribUpdates = new LinkedBlockingQueue<RibUpdate>();
	    	
		// Register floodlight provider and REST handler.
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		topology = context.getServiceImpl(ITopologyService.class);
		linkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		
		//TODO We'll initialise this here for now, but it should really be done as
		//part of the controller core
		proxyArp = new ProxyArpManager(floodlightProvider, topology, this, restApi);
		
		linkUpdates = new ArrayList<LDUpdate>();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		topologyChangeDetectorTask = new SingletonTask(executor, new TopologyChangeDetector());

		topologyNetService = new TopologyManager("");
		
		pathsWaitingOnArp = new HashMap<InetAddress, Path>();
		prefixesWaitingOnArp = Multimaps.synchronizedSetMultimap(
				HashMultimap.<InetAddress, RibUpdate>create());
		
		pushedPaths = new HashMap<InetAddress, Path>();
		prefixToPath = new HashMap<Prefix, Path>();
		pushedFlows = HashMultimap.<Prefix, PushedFlowMod>create();
		
		flowCache = new FlowCache(floodlightProvider);
		
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
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new BgpRouteWebRoutable());
		topology.addListener(this);
		floodlightProvider.addOFSwitchListener(this);
		
		proxyArp.startUp();
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, proxyArp);
		
		//Retrieve the RIB from BGPd during startup
		retrieveRib();
	}

	public IPatriciaTrie<RibEntry> getPtree() {
		return ptree;
	}
	
	public void clearPtree() {
		ptree = new PatriciaTrie<RibEntry>(32);
	}
	
	public String getBGPdRestIp() {
		return bgpdRestIp;
	}
	
	public String getRouterId() {
		return routerId;
	}
	
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
			
			RibEntry rib = new RibEntry(router_id, nexthop);

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
			log.debug("Interrupted while putting on ribUpdates queue", e);
			Thread.currentThread().interrupt();
		}
	}
	
	public synchronized void processRibAdd(RibUpdate update) {
		Prefix prefix = update.getPrefix();
		
		log.debug("Processing prefix add {}", prefix);
		
		RibEntry rib = ptree.put(prefix, update.getRibEntry());
		
		if (rib != null && !rib.equals(update.getRibEntry())) {
			//There was an existing nexthop for this prefix. This update supersedes that,
			//so we need to remove the old flows for this prefix from the switches
			_processDeletePrefix(prefix, rib);
		}
		
		if (update.getRibEntry().getNextHop().equals(
				InetAddresses.forString("0.0.0.0"))) {
			//Route originated by SDN domain
			//We don't handle these at the moment
			log.debug("Own route {} to {}", prefix, 
					update.getRibEntry().getNextHop().getHostAddress());
			return;
		}
		
		_processRibAdd(update);
	}
	
	private void _processRibAdd(RibUpdate update) {
		Prefix prefix = update.getPrefix();
		RibEntry rib = update.getRibEntry();
		
		InetAddress dstIpAddress = rib.getNextHop();
		
		//See if we know the MAC address of the next hop
		MACAddress nextHopMacAddress = proxyArp.getMacAddress(rib.getNextHop());
		
		//Find the attachment point (egress interface) of the next hop
		Interface egressInterface = null;
		if (bgpPeers.containsKey(dstIpAddress)) {
			//Route to a peer
			log.debug("Route to peer {}", dstIpAddress);
			BgpPeer peer = bgpPeers.get(dstIpAddress);
			egressInterface = interfaces.get(peer.getInterfaceName());
		}
		else {
			//Route to non-peer
			log.debug("Route to non-peer {}", dstIpAddress);
			egressInterface = interfacePtrie.match(
					new Prefix(dstIpAddress.getAddress(), 32));
			if (egressInterface == null) {
				log.warn("No outgoing interface found for {}", dstIpAddress.getHostAddress());
				return;
			}
		}
		
		if (nextHopMacAddress == null) {
			prefixesWaitingOnArp.put(dstIpAddress, 
					new RibUpdate(Operation.UPDATE, prefix, rib));
			proxyArp.sendArpRequest(dstIpAddress, this, true);
			return;
		}
		else {
			if (!bgpPeers.containsKey(dstIpAddress)) {
				//If the prefix is for a non-peer we need to ensure there's a path,
				//and push one if there isn't.
				Path path = pushedPaths.get(dstIpAddress);
				if (path == null) {
					path = new Path(egressInterface, dstIpAddress);
					calculateAndPushPath(path, nextHopMacAddress);
					pushedPaths.put(dstIpAddress, path);
				}
				
				path.incrementUsers();
				prefixToPath.put(prefix, path);
			}
			
			//For all prefixes we need to add the first-hop mac-rewriting flows
			addPrefixFlows(prefix, egressInterface, nextHopMacAddress);
		}
	}
	
	private void addPrefixFlows(Prefix prefix, Interface egressInterface, MACAddress nextHopMacAddress) {		
		log.debug("Adding flows for prefix {}, next hop mac {}",
				prefix, nextHopMacAddress);
		
		//We only need one flow mod per switch, so pick one interface on each switch
		Map<Long, Interface> srcInterfaces = new HashMap<Long, Interface>();
		for (Interface intf : interfaces.values()) {
			if (!srcInterfaces.containsKey(intf.getDpid()) 
					&& intf != egressInterface) {
				srcInterfaces.put(intf.getDpid(), intf);
			}
		}
		
		//Add a flow to rewrite mac for this prefix to all other border switches
		for (Interface srcInterface : srcInterfaces.values()) {
			DataPath shortestPath; 
			if (shortestPathTopo == null) {
				shortestPath = topologyNetService.getShortestPath(
						srcInterface.getSwitchPort(),
						egressInterface.getSwitchPort());
			}
			else {
				shortestPath = topologyNetService.getTopoShortestPath(
						shortestPathTopo, srcInterface.getSwitchPort(),
						egressInterface.getSwitchPort());
			}
			
			if (shortestPath == null){
				log.debug("Shortest path between {} and {} not found",
						srcInterface.getSwitchPort(),
						egressInterface.getSwitchPort());
				return; // just quit here?
			}
			
			//Set up the flow mod
			OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory()
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
	        
	        match.setFromCIDR(prefix.toString(), OFMatch.STR_NW_DST);
	        fm.setMatch(match);
	        
	        //Set up MAC rewrite action
	        OFActionDataLayerDestination macRewriteAction = new OFActionDataLayerDestination();
	        macRewriteAction.setDataLayerAddress(nextHopMacAddress.toBytes());
	        
	        //Set up output action
	        OFActionOutput outputAction = new OFActionOutput();
	        outputAction.setMaxLength((short)0xffff);
	        Port outputPort = shortestPath.flowEntries().get(0).outPort();
	        outputAction.setPort(outputPort.value());
	        
	        List<OFAction> actions = new ArrayList<OFAction>();
	        actions.add(macRewriteAction);
	        actions.add(outputAction);
	        fm.setActions(actions);
	        
	        pushedFlows.put(prefix, new PushedFlowMod(srcInterface.getDpid(), fm));
	        flowCache.write(srcInterface.getDpid(), fm);

			/*
			 * XXX Rate limit hack!
			 * This should be solved properly by adding a rate limiting
			 * layer on top of the switches if we know they need it.
			 */
	        try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO handle this properly
				log.error("Interrupted", e);
			}
		}
	}
	
	public synchronized void processRibDelete(RibUpdate update) {
		Prefix prefix = update.getPrefix();
		
		if (ptree.remove(prefix, update.getRibEntry())) {
			/*
			 * Only delete flows if an entry was actually removed from the trie.
			 * If no entry was removed, the <prefix, nexthop> wasn't there so
			 * it's probably already been removed and we don't need to do anything
			 */
			_processDeletePrefix(prefix, update.getRibEntry());
		}
	}
	
	private void _processDeletePrefix(Prefix prefix, RibEntry ribEntry) {
		deletePrefixFlows(prefix);
		
		log.debug("Deleting {} to {}", prefix, ribEntry.getNextHop());
		
		if (!bgpPeers.containsKey(ribEntry.getNextHop())) {
			log.debug("Getting path for route with non-peer nexthop");
			Path path = prefixToPath.remove(prefix);
			
			if (path != null) {
				//path could be null if we added to the Ptree but didn't push
				//flows yet because we were waiting to resolve ARP
			
				path.decrementUsers();
				if (path.getUsers() <= 0 && !path.isPermanent()) {
					deletePath(path);
					pushedPaths.remove(path.getDstIpAddress());
				}
			}
		}
	}
	
	private void deletePrefixFlows(Prefix prefix) {
		log.debug("Deleting flows for prefix {}", prefix);
		
		Collection<PushedFlowMod> pushedFlowMods 
				= pushedFlows.removeAll(prefix);
		
		for (PushedFlowMod pfm : pushedFlowMods) {
			if (log.isTraceEnabled()) {
				log.trace("Pushing a DELETE flow mod to {}, matches prefix {} with mac-rewrite {}",
						new Object[] {HexString.toHexString(pfm.getDpid()),
						pfm.getFlowMod().getMatch().getNetworkDestination() + 
						pfm.getFlowMod().getMatch().getNetworkDestinationMaskLen(),
						HexString.toHexString(((OFActionDataLayerDestination)pfm.getFlowMod().getActions().get(0))
								.getDataLayerAddress())});
			}
			
			sendDeleteFlowMod(pfm.getFlowMod(), pfm.getDpid());
		}
	}
	
	private void deletePath(Path path) {
		log.debug("Deleting flows for path to {}", 
				path.getDstIpAddress().getHostAddress());
		
		for (PushedFlowMod pfm : path.getFlowMods()) {
			if (log.isTraceEnabled()) {
				log.trace("Pushing a DELETE flow mod to {}, dst MAC {}",
						new Object[] {HexString.toHexString(pfm.getDpid()),
						HexString.toHexString(pfm.getFlowMod().getMatch().getDataLayerDestination())
				});
			}
			
			sendDeleteFlowMod(pfm.getFlowMod(), pfm.getDpid());
		}
	}
	
	private void sendDeleteFlowMod(OFFlowMod addFlowMod, long dpid) {
		flowCache.delete(dpid, addFlowMod);
	}
	
	//TODO test next-hop changes
	//TODO check delete/add synchronization
	
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
			Path path = new Path(peerInterface, peer.getIpAddress());
			path.setPermanent();
			
			//See if we know the MAC address of the peer. If not we can't
			//do anything until we learn it
			MACAddress macAddress = proxyArp.getMacAddress(peer.getIpAddress());
			if (macAddress == null) {
				log.debug("Don't know MAC for {}", peer.getIpAddress().getHostAddress());
				//Put in the pending paths list first
				pathsWaitingOnArp.put(peer.getIpAddress(), path);
				
				proxyArp.sendArpRequest(peer.getIpAddress(), this, true);
				continue;
			}
			
			//If we know the MAC, lets go ahead and push the paths to this peer
			calculateAndPushPath(path, macAddress);
		}
	}
	
	private void calculateAndPushPath(Path path, MACAddress dstMacAddress) {
		Interface dstInterface = path.getDstInterface();
		
		log.debug("Setting up path to {}, {}", path.getDstIpAddress().getHostAddress(),
				dstMacAddress);
		
		List<PushedFlowMod> pushedFlows = new ArrayList<PushedFlowMod>();
		
		for (Interface srcInterface : interfaces.values()) {
			if (dstInterface.equals(srcInterface.getName())){
				continue;
			}
			
			DataPath shortestPath;
			if (shortestPathTopo == null) {
				shortestPath = topologyNetService.getShortestPath(
						srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
			}
			else {
				shortestPath = topologyNetService.getTopoShortestPath(shortestPathTopo, 
						srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
			}
			
			if (shortestPath == null){
				log.warn("Shortest path between {} and {} not found",
						srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
				return;
			}
			
			List<PushedFlowMod> pushedFlowMods = installPath(shortestPath.flowEntries(), dstMacAddress);
			pushedFlows.addAll(pushedFlowMods);
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
        .setPriority(SDNIP_PRIORITY)
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
            
            flowMods.add(new PushedFlowMod(flowEntry.dpid().value(), fm));
            
            flowCache.write(flowEntry.dpid().value(), fm);
                        
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
			
			DataPath path = topologyNetService.getShortestPath(
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
	        forwardMatchSrc.setNetworkProtocol(IPv4.PROTOCOL_TCP);
	        //forwardMatchSrc.setTransportDestination(BGP_PORT);
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
				
				List<OFFlowMod> flowModList = new ArrayList<OFFlowMod>(6);
				flowModList.add(forwardFlowModSrc);
				flowModList.add(forwardFlowModDst);
				flowModList.add(reverseFlowModSrc);
				flowModList.add(reverseFlowModDst);
				flowModList.add(forwardIcmp);
				flowModList.add(reverseIcmp);
				flowCache.write(flowEntry.dpid().value(), flowModList);
			}
		}
	}
	
	@Override
	public void arpResponse(InetAddress ipAddress, MACAddress macAddress) {
		log.debug("Received ARP response: {} => {}", 
				ipAddress.getHostAddress(), macAddress);
		
		/*
		 * We synchronize on this to prevent changes to the ptree while we're pushing
		 * flows to the switches. If the ptree changes, the ptree and switches
		 * could get out of sync. 
		 */
		synchronized (this) {
			Path path = pathsWaitingOnArp.remove(ipAddress);
			
			if (path != null) {
				log.debug("Pushing path to {} at {} on {}", new Object[] {
						path.getDstIpAddress().getHostAddress(), macAddress,
						path.getDstInterface().getSwitchPort()});
				//These paths should always be to BGP peers. Paths to non-peers are
				//handled once the first prefix is ready to push
				if (pushedPaths.containsKey(path.getDstInterface())) {
					//A path already got pushed to this endpoint while we were waiting
					//for ARP. We'll copy over the permanent attribute if it is set on this path.
					if (path.isPermanent()) {
						pushedPaths.get(path.getDstInterface()).setPermanent();
					}
				}
				else {
					calculateAndPushPath(path, macAddress);
					pushedPaths.put(path.getDstIpAddress(), path);
				}
			}
			
			Set<RibUpdate> prefixesToPush = prefixesWaitingOnArp.removeAll(ipAddress);
			
			for (RibUpdate update : prefixesToPush) {
				//These will always be adds
				
				RibEntry rib = ptree.lookup(update.getPrefix()); 
				if (rib != null && rib.equals(update.getRibEntry())) {
					log.debug("Pushing prefix {} next hop {}", update.getPrefix(), 
							rib.getNextHop().getHostAddress());
					//We only push prefix flows if the prefix is still in the ptree
					//and the next hop is the same as our update. The prefix could 
					//have been removed while we were waiting for the ARP, or the 
					//next hop could have changed.
					_processRibAdd(update);
				} else {
					log.debug("Received ARP response, but {},{} is no longer in ptree", 
							update.getPrefix(), update.getRibEntry());
				}
			}
		}
	}
	
	private void setupArpFlows() {
		OFMatch match = new OFMatch();
		match.setDataLayerType(Ethernet.TYPE_ARP);
		match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
		
		OFFlowMod fm = new OFFlowMod();
		fm.setMatch(match);
		
		OFActionOutput action = new OFActionOutput();
		action.setPort(OFPort.OFPP_CONTROLLER.getValue());
		action.setMaxLength((short)0xffff);
		List<OFAction> actions = new ArrayList<OFAction>(1);
		actions.add(action);
		fm.setActions(actions);
		
		fm.setIdleTimeout((short)0)
        .setHardTimeout((short)0)
        .setBufferId(OFPacketOut.BUFFER_ID_NONE)
        .setCookie(0)
        .setCommand(OFFlowMod.OFPFC_ADD)
        .setPriority(ARP_PRIORITY)
		.setLengthU(OFFlowMod.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH);
		
		for (String strdpid : switches){
			flowCache.write(HexString.toLong(strdpid), fm);
		}
	}
	
	private void setupDefaultDropFlows() {
		OFFlowMod fm = new OFFlowMod();
		fm.setMatch(new OFMatch());
		fm.setActions(new ArrayList<OFAction>()); //No action means drop
		
		fm.setIdleTimeout((short)0)
        .setHardTimeout((short)0)
        .setBufferId(OFPacketOut.BUFFER_ID_NONE)
        .setCookie(0)
        .setCommand(OFFlowMod.OFPFC_ADD)
        .setPriority((short)0)
		.setLengthU(OFFlowMod.MINIMUM_LENGTH);
		
		OFFlowMod fmLLDP;
		OFFlowMod fmBDDP;
		try {
			 fmLLDP = fm.clone();
			 fmBDDP = fm.clone();
		} catch (CloneNotSupportedException e1) {
			log.error("Error cloning flow mod", e1);
			return;
		}
		
		OFMatch matchLLDP = new OFMatch();
		matchLLDP.setDataLayerType((short)0x8942);
		matchLLDP.setWildcards(matchLLDP.getWildcards() & ~ OFMatch.OFPFW_DL_TYPE);
		fmLLDP.setMatch(matchLLDP);
		
		OFMatch matchBDDP = new OFMatch();
		matchBDDP.setDataLayerType((short)0x88cc);
		matchBDDP.setWildcards(matchBDDP.getWildcards() & ~ OFMatch.OFPFW_DL_TYPE);
		fmBDDP.setMatch(matchBDDP);
		
		OFActionOutput action = new OFActionOutput();
		action.setPort(OFPort.OFPP_CONTROLLER.getValue());
		action.setMaxLength((short)0xffff);
		List<OFAction> actions = new ArrayList<OFAction>(1);
		actions.add(action);
		
		fmLLDP.setActions(actions);
		fmBDDP.setActions(actions);
		
		fmLLDP.setPriority(ARP_PRIORITY);
		fmLLDP.setLengthU(OFFlowMod.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH);
		fmBDDP.setPriority(ARP_PRIORITY);
		fmBDDP.setLengthU(OFFlowMod.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH);
		
		List<OFFlowMod> flowModList = new ArrayList<OFFlowMod>(3); 
		flowModList.add(fm);
		flowModList.add(fmLLDP);
		flowModList.add(fmBDDP);
		
		for (String strdpid : switches){
			flowCache.write(HexString.toLong(strdpid), flowModList);
		}
	}
	
	private void beginRouting(){
		log.debug("Topology is now ready, beginning routing function");
		shortestPathTopo = topologyNetService.prepareShortestPathTopo();
		
		setupArpFlows();
		setupDefaultDropFlows();
		
		setupBgpPaths();
		setupFullMesh();
		
		//Suppress link discovery on external-facing router ports
		for (Interface intf : interfaces.values()) {
			linkDiscoveryService.AddToSuppressLLDPs(intf.getDpid(), intf.getPort());
		}
		
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
				
				DataPath shortestPath = topologyNetService.getShortestPath(
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

	private void doUpdatesThread() {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					RibUpdate update = ribUpdates.take();
					switch (update.getOperation()){
					case UPDATE:
						if (validateUpdate(update)) {
							processRibAdd(update);
						}
						else {
							log.debug("Rib UPDATE out of order: {} via {}",
									update.getPrefix(), update.getRibEntry().getNextHop());
						}
						break;
					case DELETE:
						if (validateUpdate(update)) {
							processRibDelete(update);
						}
						else {
							log.debug("Rib DELETE out of order: {} via {}",
									update.getPrefix(), update.getRibEntry().getNextHop());
						}
						break;
					}
				} catch (InterruptedException e) {
					log.debug("Interrupted while taking from updates queue", e);
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
	
	private boolean validateUpdate(RibUpdate update) {
		RibEntry newEntry = update.getRibEntry();
		RibEntry oldEntry = ptree.lookup(update.getPrefix());
		
		//If there is no existing entry we must assume this is the most recent
		//update. However this might not always be the case as we might have a
		//POST then DELETE reordering.
		//if (oldEntry == null || !newEntry.getNextHop().equals(oldEntry.getNextHop())) {
		if (oldEntry == null) {
			return true;
		}
		
		// This handles the case where routes are gathered in the initial
		// request because they don't have sequence number info
		if (newEntry.getSysUpTime() == -1 && newEntry.getSequenceNum() == -1) {
			return true;
		}
		
		if (newEntry.getSysUpTime() > oldEntry.getSysUpTime()) {
			return true;
		}
		else if (newEntry.getSysUpTime() == oldEntry.getSysUpTime()) {
			if (newEntry.getSequenceNum() > oldEntry.getSequenceNum()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	public void topologyChanged() {
		if (topologyReady) {
			return;
		}
		
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
		
		if (refreshNeeded && !topologyReady){
			topologyChangeDetectorTask.reschedule(TOPO_DETECTION_WAIT, TimeUnit.SECONDS);
		}
	}

	@Override
	public void addedSwitch(IOFSwitch sw) {
		if (!topologyReady) {
			sw.clearAllFlowMods();
		}
		
		flowCache.switchConnected(sw);
	}

	@Override
	public void removedSwitch(IOFSwitch sw) {}

	@Override
	public void switchPortChanged(Long switchId) {}

	@Override
	public String getName() {
		return "BgpRoute";
	}
	
	/*
	 * ILayer3InfoService methods
	 */
	
	@Override
	public boolean isInterfaceAddress(InetAddress address) {
		Interface intf = interfacePtrie.match(new Prefix(address.getAddress(), 32));
		return (intf != null && intf.getIpAddress().equals(address));
	}
	
	@Override
	public boolean inConnectedNetwork(InetAddress address) {
		Interface intf = interfacePtrie.match(new Prefix(address.getAddress(), 32));
		return (intf != null && !intf.getIpAddress().equals(address));
	}
	
	@Override
	public boolean fromExternalNetwork(long inDpid, short inPort) {
		for (Interface intf : interfaces.values()) {
			if (intf.getDpid() == inDpid && intf.getPort() == inPort) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Interface getOutgoingInterface(InetAddress dstIpAddress) {
		return interfacePtrie.match(new Prefix(dstIpAddress.getAddress(), 32));
	}
	
	@Override
	public boolean hasLayer3Configuration() {
		return !interfaces.isEmpty();
	}
	
	@Override
	public MACAddress getRouterMacAddress() {
		return bgpdMacAddress;
	}

	/*
	 * TODO This is a hack to get the REST API to work for ProxyArpManager.
	 * The REST API is currently tied to the Floodlight module system and we
	 * need to separate it to allow ONOS modules to use it. For now we will 
	 * proxy calls through to the ProxyArpManager (which is not a Floodlight 
	 * module) through this class which is a module.
	 */
	@Override
	public MACAddress getMacAddress(InetAddress ipAddress) {
		return proxyArp.getMacAddress(ipAddress);
	}

	@Override
	public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
			boolean retry) {
		proxyArp.sendArpRequest(ipAddress, requester, retry);		
	}

	@Override
	public List<String> getMappings() {
		return proxyArp.getMappings();
	}
}
