package net.onrc.onos.ofcontroller.bgproute;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
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
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoLinkService;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.onrc.onos.ofcontroller.core.internal.TopoLinkServiceImpl;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscovery;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
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

public class BgpRoute implements IFloodlightModule, IBgpRouteService, 
									ITopologyListener, IOFSwitchListener {
	
	protected static Logger log = LoggerFactory.getLogger(BgpRoute.class);

	protected IFloodlightProviderService floodlightProvider;
	protected ITopologyService topology;
	protected ITopoRouteService topoRouteService;
	protected IDeviceService devices;
	protected IRestApiService restApi;
	
	protected ProxyArpManager proxyArp;
	
	protected static Ptree ptree;
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
	
	protected class TopologyChangeDetector implements Runnable {
		@Override
		public void run() {
			log.debug("Running topology change detection task");
			synchronized (linkUpdates) {
				//This is the model the REST API uses to retrieve network graph info
				ITopoLinkService topoLinkService = new TopoLinkServiceImpl();
				
				List<Link> activeLinks = topoLinkService.getActiveLinks();
				for (Link l : activeLinks){
					log.debug("active link: {}", l);
				}
				
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
				log.debug("No know changes outstanding. Checking topology now");
				checkStatus();
			}
			else {
				//We know of some link updates that haven't propagated to the database yet
				log.debug("Some changes not found in network map- size {}", linkUpdates.size());
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
	    
	    ptree = new Ptree(32);
	    	
		// Register floodlight provider and REST handler.
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		topology = context.getServiceImpl(ITopologyService.class);
		devices = context.getServiceImpl(IDeviceService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		
		//TODO We'll initialise this here for now, but it should really be done as
		//part of the controller core
		proxyArp = new ProxyArpManager(floodlightProvider, topology);
		
		linkUpdates = new ArrayList<LDUpdate>();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		topologyChangeDetectorTask = new SingletonTask(executor, new TopologyChangeDetector());

		topoRouteService = new TopoRouteService("");
		
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

	public Ptree getPtree() {
		return ptree;
	}
	
	public void clearPtree() {
		//ptree = null;
		ptree = new Ptree(32);	
	}
	
	public String getBGPdRestIp() {
		return bgpdRestIp;
	}
	
	public String getRouterId() {
		return routerId;
	}
	
	// Return nexthop address as byte array.
	public Rib lookupRib(byte[] dest) {
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
	
	//TODO looks like this should be a unit test
	@SuppressWarnings("unused")
    private void test() throws UnknownHostException {
		System.out.println("Here it is");
		Prefix p = new Prefix("128.0.0.0", 8);
		Prefix q = new Prefix("8.0.0.0", 8);
		Prefix r = new Prefix("10.0.0.0", 24);
		Prefix a = new Prefix("10.0.0.1", 32);
	
		ptree.acquire(p.getAddress(), p.masklen);
		ptree.acquire(q.getAddress(), q.masklen);
		ptree.acquire(r.getAddress(), r.masklen);
	
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
	
		PtreeNode n = ptree.match(a.getAddress(), a.masklen);
		if (n != null) {
			System.out.println("Matched prefix for 10.0.0.1:");
			Prefix x = new Prefix(n.key, n.keyBits);
			ptree.delReference(n);
		}
		
		n = ptree.lookup(p.getAddress(), p.masklen);
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
		
		n = ptree.lookup(q.getAddress(), q.masklen);
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
		
		n = ptree.lookup(r.getAddress(), r.masklen);
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}

	}
	
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
			} catch (UnknownHostException e1) {
				log.warn("Wrong prefix format in RIB JSON: {}", prefix1);
				continue;
			}
			
			PtreeNode node = ptree.acquire(p.getAddress(), p.masklen);
			Rib rib = new Rib(router_id, nexthop, p.masklen);
			
			if (node.rib != null) {
				node.rib = null;
				ptree.delReference(node);
			}
			
			node.rib = rib;
			
			prefixAdded(node);
		} 
	}
	
	public void prefixAdded(PtreeNode node) {
		if (!topologyReady){
			return;
		}
		
		String prefix = getPrefixFromPtree(node);
		
		log.debug("New prefix {} added, next hop {}, routerId {}", 
				new Object[] {prefix, node.rib.nextHop.toString(), 
				node.rib.routerId.getHostAddress()});
		
		//TODO this is wrong, we shouldn't be dealing with BGP peers here.
		//We need to figure out where the device is attached and what it's
		//mac address is by learning. 
		//The next hop is not necessarily the peer, and the peer's attachment
		//point is not necessarily the next hop's attachment point.
		BgpPeer peer = bgpPeers.get(node.rib.nextHop);
		
		if (peer == null){
			//TODO local router isn't in peers list so this will get thrown
			//Need to work out what to do about local prefixes with next hop 0.0.0.0.
			
			//The other scenario is this is a route server route. In that
			//case the next hop is not in our configuration
			log.error("Couldn't find next hop router in router {} in config"
					, node.rib.nextHop.toString());
			return; //just quit out here? This is probably a configuration error
		}
		
		Interface peerInterface = interfaces.get(peer.getInterfaceName());

		//Add a flow to rewrite mac for this prefix to all border switches
		for (Interface srcInterface : interfaces.values()) {
			if (srcInterface == peerInterface) {
				//Don't push a flow for the switch where this peer is attached
				continue;
			}
						
			DataPath shortestPath = topoRouteService.getShortestPath(
					srcInterface.getSwitchPort(),
					peerInterface.getSwitchPort());
			
			if (shortestPath == null){
				log.debug("Shortest path between {} and {} not found",
						srcInterface.getSwitchPort(),
						peerInterface.getSwitchPort());
				return; // just quit here?
			}
			
			//TODO check the shortest path against the cached version we
			//calculated before. If they don't match up that's a problem
			
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
	        
	        //match.setDataLayerSource(ingressRouter.getRouterMac().toBytes());
	        //match.setDataLayerSource(peer.getMacAddress().toBytes());
	        //match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);

	        InetAddress address = null;
	        try {
				address = InetAddress.getByAddress(node.key);
			} catch (UnknownHostException e1) {
				//Should never happen is the reverse conversion has already been done
				log.error("Malformed IP address");
				return;
			}
	        
	        match.setFromCIDR(address.getHostAddress() + "/" + node.rib.masklen, OFMatch.STR_NW_DST);
	        fm.setMatch(match);
	        
	        //Set up MAC rewrite action
	        OFActionDataLayerDestination macRewriteAction = new OFActionDataLayerDestination();
	        //TODO use ARP module rather than configured mac addresses
	        //TODO the peer's mac address is not necessarily the next hop's...
	        macRewriteAction.setDataLayerAddress(peer.getMacAddress().toBytes());
	        
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
	
	//TODO this is largely untested
	public void prefixDeleted(PtreeNode node) {
		if (!topologyReady) {
			return;
		}
		
		String prefix = getPrefixFromPtree(node);
		
		log.debug("Prefix {} deleted, next hop {}", 
				prefix, node.rib.nextHop.toString());
		
		//Remove MAC rewriting flows from other border switches
		BgpPeer peer = bgpPeers.get(node.rib.nextHop);
		if (peer == null){
			//either a router server route or local route. Can't handle right now
			return;
		}
		
		Interface peerInterface = interfaces.get(peer.getInterfaceName());
		
		for (Interface srcInterface : interfaces.values()) {
			if (srcInterface == peerInterface) {
				continue;
			}
			
			//Set up the flow mod
			OFFlowMod fm =
	                (OFFlowMod) floodlightProvider.getOFMessageFactory()
	                                              .getMessage(OFType.FLOW_MOD);
			
	        fm.setIdleTimeout((short)0)
	        .setHardTimeout((short)0)
	        .setBufferId(OFPacketOut.BUFFER_ID_NONE)
	        .setCookie(MAC_RW_COOKIE)
	        .setCommand(OFFlowMod.OFPFC_DELETE)
	        .setOutPort(OFPort.OFPP_NONE)
	        .setPriority(SDNIP_PRIORITY)
	        .setLengthU(OFFlowMod.MINIMUM_LENGTH);
	        		//+ OFActionDataLayerDestination.MINIMUM_LENGTH
	        		//+ OFActionOutput.MINIMUM_LENGTH);
	        
	        OFMatch match = new OFMatch();
	        match.setDataLayerType(Ethernet.TYPE_IPv4);
	        match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
	        
	        //match.setDataLayerSource(ingressRouter.getRouterMac().toBytes());
	        //match.setDataLayerSource(peer.getMacAddress().toBytes());
	        //match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
	        
	        InetAddress address = null;
	        try {
				address = InetAddress.getByAddress(node.key);
			} catch (UnknownHostException e1) {
				//Should never happen is the reverse conversion has already been done
				log.error("Malformed IP address");
				return;
			}
	        
	        match.setFromCIDR(address.getHostAddress() + "/" + node.rib.masklen, OFMatch.STR_NW_DST);
	        fm.setMatch(match);
	        
	        //Write to switch
	        IOFSwitch sw = floodlightProvider.getSwitches()
	        		.get(srcInterface.getDpid());
	        
            if (sw == null){
            	log.warn("Switch not found when pushing flow mod");
            	continue;
            }
            
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
			//for (Map.Entry<String, Interface> intfEntry : interfaces.entrySet()) {
			for (Interface srcInterface : interfaces.values()) {
				//Interface srcInterface = intfEntry.getValue();
				//if (peer.getInterfaceName().equals(intfEntry.getKey())){
				if (peer.getInterfaceName().equals(srcInterface.getName())){
					continue;
				}
				
				DataPath shortestPath = topoRouteService.getShortestPath(
							srcInterface.getSwitchPort(), peerInterface.getSwitchPort()); 
				
				if (shortestPath == null){
					log.debug("Shortest path between {} and {} not found",
							srcInterface.getSwitchPort(), peerInterface.getSwitchPort());
					return; // just quit here?
				}
				
				//install flows
				installPath(shortestPath.flowEntries(), peer);
			}
		}
	}
	
	private void installPath(List<FlowEntry> flowEntries, BgpPeer peer){
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
            //TODO Again using MAC address from configuration
            match.setDataLayerDestination(peer.getMacAddress().toBytes());
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
            ((OFActionOutput) fm.getActions().get(0)).setPort(flowEntry.outPort().value());
            
            fm.setMatch(match);
            
            IOFSwitch sw = floodlightProvider.getSwitches().get(flowEntry.dpid().value());
            
            if (sw == null){
            	log.warn("Switch not found when pushing flow mod");
            	continue;
            }
            
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
	        
			for (FlowEntry flowEntry : path.flowEntries()){
				OFFlowMod forwardFlowModSrc, forwardFlowModDst;
				OFFlowMod reverseFlowModSrc, reverseFlowModDst;
				try {
					forwardFlowModSrc = fm.clone();
					forwardFlowModDst = fm.clone();
					reverseFlowModSrc = fm.clone();
					reverseFlowModDst = fm.clone();
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
				
				IOFSwitch sw = floodlightProvider.getSwitches().get(flowEntry.dpid().value());
				
				//Hopefully the switch is there
				List<OFMessage> msgList = new ArrayList<OFMessage>(2);
				msgList.add(forwardFlowModSrc);
				msgList.add(forwardFlowModDst);
				msgList.add(reverseFlowModSrc);
				msgList.add(reverseFlowModDst);
				
				try {
					sw.write(msgList, null);
					sw.flush();
				} catch (IOException e) {
					log.error("Failure writing flow mod", e);
				}
			}
		}
	}
	
	private void beginRouting(){
		log.debug("Topology is now ready, beginning routing function");
		setupBgpPaths();
		setupFullMesh();
		
		//Traverse ptree and create flows for all routes
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)){
			if (node.rib != null){
				prefixAdded(node);
			}
		}
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
		log.debug("In checkStatus, swC {}, toRe {}", switchesConnected, topologyReady);
		
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

	@Override
	public void topologyChanged() {		
		//There seems to be more topology events than there should be. Lots of link
		//updated, port up and switch updated on what should be a fairly static topology
		
		boolean refreshNeeded = false;
		for (LDUpdate ldu : topology.getLastLinkUpdates()){
			if (!ldu.getOperation().equals(ILinkDiscovery.UpdateOperation.LINK_UPDATED)){
				//We don't need to recalculate anything for just link updates
				//They happen way too frequently (may be a bug in our link discovery)
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
