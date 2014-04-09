package net.onrc.onos.apps.bgproute;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.apps.bgproute.RibUpdate.Operation;
import net.onrc.onos.apps.proxyarp.IArpRequester;
import net.onrc.onos.apps.proxyarp.IProxyArpService;
import net.onrc.onos.core.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.onrc.onos.core.linkdiscovery.ILinkDiscoveryService;
import net.onrc.onos.core.main.config.IConfigInfoService;
import net.onrc.onos.core.packet.Ethernet;
import net.onrc.onos.core.packet.IPv4;
import net.onrc.onos.core.util.CallerId;
import net.onrc.onos.core.util.DataPath;
import net.onrc.onos.core.util.Dpid;
import net.onrc.onos.core.util.FlowEntryAction;
import net.onrc.onos.core.util.FlowEntryActions;
import net.onrc.onos.core.util.FlowEntryMatch;
import net.onrc.onos.core.util.FlowId;
import net.onrc.onos.core.util.FlowPath;
import net.onrc.onos.core.util.FlowPathFlags;
import net.onrc.onos.core.util.FlowPathType;
import net.onrc.onos.core.util.FlowPathUserState;
import net.onrc.onos.core.util.IPv4Net;
import net.onrc.onos.core.util.Port;
import net.onrc.onos.core.util.SwitchPort;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.action.OFAction;
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
        IArpRequester,
        IOFSwitchListener, IConfigInfoService {

    private static final Logger log = LoggerFactory.getLogger(BgpRoute.class);

    private IFloodlightProviderService floodlightProvider;
    private ILinkDiscoveryService linkDiscoveryService;
    private IRestApiService restApi;
    private IProxyArpService proxyArp;

    private IPatriciaTrie<RibEntry> ptree;
    private IPatriciaTrie<Interface> interfacePtrie;
    private BlockingQueue<RibUpdate> ribUpdates;

    private String bgpdRestIp;
    private String routerId;
    private String configFilename = "config.json";

    private static final short ARP_PRIORITY = 20;

    // The fields below are unused after the move to FlowManager.
    // Remove them if no longer needed.
    /*
    // We need to identify our flows somehow, in lieu of an OS-wide mechanism
    // to hand out cookie IDs to prevent conflicts.
    private static final long APP_COOKIE = 0xa0000000000000L;
    // Cookie for flows that do L2 forwarding within SDN domain to egress routers
    private static final long L2_FWD_COOKIE = APP_COOKIE + 1;
    // Cookie for flows in ingress switches that rewrite the MAC address
    private static final long MAC_RW_COOKIE = APP_COOKIE + 2;
    // Cookie for flows that setup BGP paths
    private static final long BGP_COOKIE = APP_COOKIE + 3;
    // Forwarding uses priority 0, and the mac rewrite entries in ingress switches
    // need to be higher priority than this otherwise the rewrite may not get done
    private static final short SDNIP_PRIORITY = 10;
    */

    private static final short BGP_PORT = 179;

    private static final int TOPO_DETECTION_WAIT = 2; // seconds

    // Configuration stuff
    private List<String> switches;
    private Map<String, Interface> interfaces;
    private Map<InetAddress, BgpPeer> bgpPeers;
    private SwitchPort bgpdAttachmentPoint;
    private MACAddress bgpdMacAddress;
    private short vlan;

    // True when all switches have connected
    private volatile boolean switchesConnected = false;
    // True when we have a full mesh of shortest paths between gateways
    private volatile boolean topologyReady = false;

    private List<LDUpdate> linkUpdates;
    private SingletonTask topologyChangeDetectorTask;

    private SetMultimap<InetAddress, RibUpdate> prefixesWaitingOnArp;

    private Map<InetAddress, Path> pathsWaitingOnArp;

    private ExecutorService bgpUpdatesExecutor;

    private Map<InetAddress, Path> pushedPaths;
    private Map<Prefix, Path> prefixToPath;
    //  private Multimap<Prefix, PushedFlowMod> pushedFlows;
    private Multimap<Prefix, FlowId> pushedFlowIds;

    private FlowCache flowCache;

    // TODO: Fix for the new Topology Network Graph
    // private volatile Topology topology = null;

    private class TopologyChangeDetector implements Runnable {
        @Override
        public void run() {
            log.debug("Running topology change detection task");
            // TODO: Fix the code below after topoLinkService was removed
            /*
            synchronized (linkUpdates) {

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
            */

            if (!topologyReady) {
                if (linkUpdates.isEmpty()) {
                    // All updates have been seen in network map.
                    // We can check if topology is ready
                    log.debug("No known changes outstanding. Checking topology now");
                    checkStatus();
                } else {
                    // We know of some link updates that haven't propagated to the database yet
                    log.debug("Some changes not found in network map - {} links missing", linkUpdates.size());
                    topologyChangeDetectorTask.reschedule(TOPO_DETECTION_WAIT, TimeUnit.SECONDS);
                }
            }
        }
    }

    private void readConfiguration(String configFilename) {
        File gatewaysFile = new File(configFilename);
        ObjectMapper mapper = new ObjectMapper();

        try {
            Configuration config = mapper.readValue(gatewaysFile, Configuration.class);

            switches = config.getSwitches();
            interfaces = new HashMap<String, Interface>();
            for (Interface intf : config.getInterfaces()) {
                interfaces.put(intf.getName(), intf);
            }
            bgpPeers = new HashMap<InetAddress, BgpPeer>();
            for (BgpPeer peer : config.getPeers()) {
                bgpPeers.put(peer.getIpAddress(), peer);
            }

            bgpdAttachmentPoint = new SwitchPort(
                    new Dpid(config.getBgpdAttachmentDpid()),
                    new Port(config.getBgpdAttachmentPort()));

            bgpdMacAddress = config.getBgpdMacAddress();
            vlan = config.getVlan();
        } catch (JsonParseException e) {
            log.error("Error in JSON file", e);
            throw new ConfigurationRuntimeException("Error in JSON file", e);
        } catch (JsonMappingException e) {
            log.error("Error in JSON file", e);
            throw new ConfigurationRuntimeException("Error in JSON file", e);
        } catch (IOException e) {
            log.error("Error reading JSON file", e);
            throw new ConfigurationRuntimeException("Error in JSON file", e);
        }

        // Populate the interface Patricia Trie
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
        l.add(IConfigInfoService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m
                = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(IBgpRouteService.class, this);
        m.put(IConfigInfoService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l
                = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
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
        linkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);
        restApi = context.getServiceImpl(IRestApiService.class);
        proxyArp = context.getServiceImpl(IProxyArpService.class);

        linkUpdates = new ArrayList<LDUpdate>();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        topologyChangeDetectorTask = new SingletonTask(executor, new TopologyChangeDetector());

        pathsWaitingOnArp = new HashMap<InetAddress, Path>();
        prefixesWaitingOnArp = Multimaps.synchronizedSetMultimap(
                HashMultimap.<InetAddress, RibUpdate>create());

        pushedPaths = new HashMap<InetAddress, Path>();
        prefixToPath = new HashMap<Prefix, Path>();
//              pushedFlows = HashMultimap.<Prefix, PushedFlowMod>create();
        pushedFlowIds = HashMultimap.<Prefix, FlowId>create();

        flowCache = new FlowCache(floodlightProvider);

        bgpUpdatesExecutor = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder().setNameFormat("bgp-updates-%d").build());

        // Read in config values
        bgpdRestIp = context.getConfigParams(this).get("BgpdRestIp");
        if (bgpdRestIp == null) {
            log.error("BgpdRestIp property not found in config file");
            throw new ConfigurationRuntimeException(
                    "BgpdRestIp property not found in config file");
        } else {
            log.info("BgpdRestIp set to {}", bgpdRestIp);
        }

        routerId = context.getConfigParams(this).get("RouterId");
        if (routerId == null) {
            log.error("RouterId property not found in config file");
            throw new ConfigurationRuntimeException(
                    "RouterId property not found in config file");
        } else {
            log.info("RouterId set to {}", routerId);
        }

        String configFilenameParameter = context.getConfigParams(this).get("configfile");
        if (configFilenameParameter != null) {
            configFilename = configFilenameParameter;
        }
        log.debug("Config file set to {}", configFilename);

        readConfiguration(configFilename);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        restApi.addRestletRoutable(new BgpRouteWebRoutable());
        floodlightProvider.addOFSwitchListener(this);

        // Retrieve the RIB from BGPd during startup
        retrieveRib();
    }

    @Override
    public IPatriciaTrie<RibEntry> getPtree() {
        return ptree;
    }

    @Override
    public void clearPtree() {
        ptree = new PatriciaTrie<RibEntry>(32);
    }

    @Override
    public String getBGPdRestIp() {
        return bgpdRestIp;
    }

    @Override
    public String getRouterId() {
        return routerId;
    }

    private void retrieveRib() {
        String url = "http://" + bgpdRestIp + "/wm/bgp/" + routerId;
        String response = RestClient.get(url);

        if ("".equals(response)) {
            return;
        }

        response = response.replaceAll("\"", "'");
        JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(response);
        JSONArray ribArray = jsonObj.getJSONArray("rib");
        String routerId = jsonObj.getString("router-id");

        int size = ribArray.size();

        log.info("Retrived RIB of {} entries from BGPd", size);

        for (int j = 0; j < size; j++) {
            JSONObject ribEntry = ribArray.getJSONObject(j);
            String prefix = ribEntry.getString("prefix");
            String nexthop = ribEntry.getString("nexthop");

            // Insert each rib entry into the local rib
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

            RibEntry rib = new RibEntry(routerId, nexthop);

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

    public void processRibAdd(RibUpdate update) {
        synchronized (this) {
            Prefix prefix = update.getPrefix();

            log.debug("Processing prefix add {}", prefix);

            RibEntry rib = ptree.put(prefix, update.getRibEntry());

            if (rib != null && !rib.equals(update.getRibEntry())) {
                // There was an existing nexthop for this prefix. This update supersedes that,
                // so we need to remove the old flows for this prefix from the switches
                _processDeletePrefix(prefix, rib);
            }

            if (update.getRibEntry().getNextHop().equals(
                    InetAddresses.forString("0.0.0.0"))) {
                // Route originated by SDN domain
                // We don't handle these at the moment
                log.debug("Own route {} to {}", prefix,
                        update.getRibEntry().getNextHop().getHostAddress());
                return;
            }

            _processRibAdd(update);
        }
    }

    private void _processRibAdd(RibUpdate update) {
        Prefix prefix = update.getPrefix();
        RibEntry rib = update.getRibEntry();

        InetAddress dstIpAddress = rib.getNextHop();
        MACAddress nextHopMacAddress = null;

        // See if we know the MAC address of the next hop
        // TODO if we do not treat the next hop as a device in the future, we need to update this
        // TODO: Fix the code below after deviceStorage was removed
        /*
        IDeviceObject nextHopDevice =
                deviceStorage.getDeviceByIP(InetAddresses.coerceToInteger(dstIpAddress));

        if (nextHopDevice == null){
            log.debug("NextHopDevice for IP: {} is null", dstIpAddress);
            prefixesWaitingOnArp.put(dstIpAddress,
                    new RibUpdate(Operation.UPDATE, prefix, rib));
            proxyArp.sendArpRequest(dstIpAddress, this, true);
            return;

        }
        nextHopMacAddress = MACAddress.valueOf(nextHopDevice.getMACAddress());
        */

        // Find the attachment point (egress interface) of the next hop
        Interface egressInterface = null;
        if (bgpPeers.containsKey(dstIpAddress)) {
            //Route to a peer
            log.debug("Route to peer {}", dstIpAddress);
            BgpPeer peer = bgpPeers.get(dstIpAddress);
            egressInterface = interfaces.get(peer.getInterfaceName());
        } else {
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
        } else {
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

    /**
     * Add a flow to match dst-IP prefix and rewrite MAC for one IP prefix
     * to all other border switches
     */
    private void addPrefixFlows(Prefix prefix, Interface egressInterface,
                                MACAddress nextHopMacAddress) {
        log.debug("Adding flows for prefix {}, next hop mac {}",
                prefix, nextHopMacAddress);

        FlowPath flowPath = new FlowPath();
        flowPath.setInstallerId(new CallerId("SDNIP"));

        // Set flowPath FlowPathType and FlowPathUserState
        flowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
        flowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);

        // Insert dst-ip prefix based forwarding and MAC rewrite flow entry
        // only to the first-host switches
        FlowPathFlags flowPathFlags = new FlowPathFlags();
        flowPathFlags.setFlags(FlowPathFlags.KEEP_ONLY_FIRST_HOP_ENTRY);
        flowPath.setFlowPathFlags(flowPathFlags);

        // Create the DataPath object: dstSwitchPort
        SwitchPort dstPort =
            new SwitchPort(new Dpid(egressInterface.getDpid()),
                           new Port(egressInterface.getPort()));

        // We only need one flow mod per switch, so pick one interface on each switch
        Map<Long, Interface> srcInterfaces = new HashMap<Long, Interface>();
        for (Interface intf : interfaces.values()) {
            if (!srcInterfaces.containsKey(intf.getDpid())
                    && !intf.equals(egressInterface)) {
                srcInterfaces.put(intf.getDpid(), intf);
            }
        }
        for (Interface srcInterface : srcInterfaces.values()) {

            if (egressInterface.equals(srcInterface)) {
                continue;
            }

            // Create flowPath FlowId
            flowPath.setFlowId(new FlowId());

            // Create DataPath object: srcSwitchPort
            SwitchPort srcPort =
                new SwitchPort(new Dpid(srcInterface.getDpid()),
                               new Port(srcInterface.getPort()));

            DataPath dataPath = new DataPath();
            dataPath.setSrcPort(srcPort);
            dataPath.setDstPort(dstPort);
            flowPath.setDataPath(dataPath);

            // Create flow path matching condition(s): IPv4 Prefix
            FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
            flowEntryMatch.enableEthernetFrameType(Ethernet.TYPE_IPV4);
            IPv4Net dstIPv4Net = new IPv4Net(prefix.toString());
            flowEntryMatch.enableDstIPv4Net(dstIPv4Net);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            /*
             * Create the Flow Entry Action(s): dst-MAC rewrite action
             */
            FlowEntryActions flowEntryActions = new FlowEntryActions();
            FlowEntryAction flowEntryAction1 = new FlowEntryAction();
            flowEntryAction1.setActionSetEthernetDstAddr(nextHopMacAddress);
            // flowEntryAction1.actionSetEthernetDstAddr(nextHopMacAddress);
            flowEntryActions.addAction(flowEntryAction1);
            flowPath.setFlowEntryActions(flowEntryActions);

            // Flow Path installation, only to first hop switches
            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {
                log.error("Failed to install flow path to the first hop for " +
                        "prefix: {}, nextHopMacAddress: {}", prefix.getAddress(),
                        nextHopMacAddress);
            }
            else {
                log.debug("Successfully installed flow path to the first hop " +
                        "for prefix: {}, nextHopMacAddress: {}", prefix.getAddress(),
                        nextHopMacAddress);

                pushedFlowIds.put(prefix, flowPath.flowId());
            }
            */
        }
    }

    public void processRibDelete(RibUpdate update) {
        synchronized (this) {
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

    // TODO have not tested this module
    private void deletePrefixFlows(Prefix prefix) {
        log.debug("Deleting flows for prefix {}", prefix);

        //
        // TODO: Delete the flow by using the new Path Intent framework
        // NOTE: During the refactoring of the code below, if obtaining
        // the values of the removed flowIds is needed, the first
        // removeAll() statement should be replaced with the second removeAll()
        // statement.
        //
        pushedFlowIds.removeAll(prefix);
        /*
        Collection<FlowId> flowIds = pushedFlowIds.removeAll(prefix);
        for (FlowId flowId : flowIds) {
            if (log.isTraceEnabled()) {
                //Trace the flow status by flowPath in the switch before deleting it
                log.trace("Pushing a DELETE flow mod to flowPath : {}",
                        flowManagerService.getFlow(flowId).toString());
            }

            if( flowManagerService.deleteFlow(flowId))
            {
                log.debug("Successfully deleted FlowId: {}",flowId);
            }
            else
            {
                log.debug("Failed to delete FlowId: {}",flowId);
            }
        }
        */
    }

    // TODO need to record the path and then delete here
    private void deletePath(Path path) {
        log.debug("Deleting flows for path to {}",
                path.getDstIpAddress().getHostAddress());

        // TODO need update
        /*for (PushedFlowMod pfm : path.getFlowMods()) {
            if (log.isTraceEnabled()) {
                log.trace("Pushing a DELETE flow mod to {}, dst MAC {}",
                        new Object[] {HexString.toHexString(pfm.getDpid()),
                        HexString.toHexString(pfm.getFlowMod().getMatch().getDataLayerDestination())
                });
            }

            sendDeleteFlowMod(pfm.getFlowMod(), pfm.getDpid());
        }*/
    }


    //TODO test next-hop changes
    //TODO check delete/add synchronization

    /**
     * On startup, we need to calculate a full mesh of paths between all gateway
     * switches
     */
    private void setupFullMesh() {
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
            // TODO: Fix the code below after deviceStorage was removed
            MACAddress macAddress = null;
            /*
            IDeviceObject nextHopDevice =
                    deviceStorage.getDeviceByIP(InetAddresses.coerceToInteger(peer.getIpAddress()));

            if(nextHopDevice == null){
                log.debug("There is no DeviceObject for {}", peer.getIpAddress().getHostAddress());
                //Put in the pending paths list first
                pathsWaitingOnArp.put(peer.getIpAddress(), path);
                proxyArp.sendArpRequest(peer.getIpAddress(), this, true);
                continue;
            }

            macAddress = MACAddress.valueOf(nextHopDevice.getMACAddress());
            */

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

        FlowPath flowPath = new FlowPath();

        flowPath.setInstallerId(new CallerId("SDNIP"));

        // Set flowPath FlowPathType and FlowPathUserState
        flowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
        flowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);

        // Insert the dest-mac based forwarding flow entry to the non-first-hop switches
        FlowPathFlags flowPathFlags = new FlowPathFlags();
        flowPathFlags.setFlags(FlowPathFlags.DISCARD_FIRST_HOP_ENTRY);
        flowPath.setFlowPathFlags(flowPathFlags);

        // Create the DataPath object: dstSwitchPort
        SwitchPort dstPort =
            new SwitchPort(new Dpid(dstInterface.getDpid()),
                           new Port(dstInterface.getPort()));

        for (Interface srcInterface : interfaces.values()) {

            if (dstInterface.equals(srcInterface)) {
                continue;
            }

            // Create flowPath FlowId
            flowPath.setFlowId(new FlowId());

            // Create the DataPath object: srcSwitchPort
            SwitchPort srcPort =
                new SwitchPort(new Dpid(srcInterface.getDpid()),
                               new Port(srcInterface.getPort()));

            DataPath dataPath = new DataPath();
            dataPath.setSrcPort(srcPort);
            dataPath.setDstPort(dstPort);
            flowPath.setDataPath(dataPath);

            // Create the Flow Path Match condition(s)
            FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
            flowEntryMatch.enableEthernetFrameType(Ethernet.TYPE_IPV4);
            flowEntryMatch.enableDstMac(dstMacAddress);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            // NOTE: No need to add ACTION_OUTPUT. It is implied when creating
            // Shortest Path Flow, and is always the last action for the Flow Entries
            log.debug("FlowPath of MAC based forwarding: {}", flowPath.toString());
            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {
                log.error("Failed to set up MAC based forwarding path to {}, {}",
                        path.getDstIpAddress().getHostAddress(),dstMacAddress);
            }
            else {
                log.debug("Successfully set up MAC based forwarding path to {}, {}",
                        path.getDstIpAddress().getHostAddress(),dstMacAddress);
            }
            */
        }
    }

    /**
     * Proactively install all BGP traffic paths from BGP host attachment point
     * in SDN network to all the virtual gateways to BGP peers in other networks
     */
    private void setupBgpPaths() {

        for (BgpPeer bgpPeer : bgpPeers.values()) {

            FlowPath flowPath = new FlowPath();
            flowPath.setInstallerId(new CallerId("SDNIP"));

            // Set flowPath FlowPathType and FlowPathUserState
            flowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
            flowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);

            // Install flow paths between BGPd and its peers
            // There is no need to set the FlowPathFlags
            flowPath.setFlowPathFlags(new FlowPathFlags(0));

            Interface peerInterface = interfaces.get(bgpPeer.getInterfaceName());

            // Create the Flow Path Match condition(s)
            FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
            flowEntryMatch.enableEthernetFrameType(Ethernet.TYPE_IPV4);

            // Match both source address and dest address
            IPv4Net dstIPv4Net = new IPv4Net(bgpPeer.getIpAddress().getHostAddress() + "/32");
            flowEntryMatch.enableDstIPv4Net(dstIPv4Net);

            IPv4Net srcIPv4Net = new IPv4Net(peerInterface.getIpAddress().getHostAddress() + "/32");
            flowEntryMatch.enableSrcIPv4Net(srcIPv4Net);

            // Match TCP protocol
            flowEntryMatch.enableIpProto(IPv4.PROTOCOL_TCP);

            // Match destination TCP port
            flowEntryMatch.enableDstTcpUdpPort(BGP_PORT);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            /**
             * Create the DataPath: BGP -> BGP peer
             */
            // Flow path for src-TCP-port
            DataPath dataPath = new DataPath();

            SwitchPort srcPort =
                new SwitchPort(bgpdAttachmentPoint.dpid(),
                               bgpdAttachmentPoint.port());
            dataPath.setSrcPort(srcPort);

            SwitchPort dstPort =
                new SwitchPort(new Dpid(peerInterface.getDpid()),
                               new Port(peerInterface.getSwitchPort().port()));
            dataPath.setDstPort(dstPort);

            flowPath.setDataPath(dataPath);

            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {
                log.error("Failed to set up path BGP -> peer {}"+"; dst-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            else {
                log.debug("Successfully set up path BGP -> peer {}"+"; dst-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            */

            // Disable dst-TCP-port, and set src-TCP-port
            flowEntryMatch.disableDstTcpUdpPort();
            flowEntryMatch.enableSrcTcpUdpPort(BGP_PORT);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            // Create a new FlowId
            flowPath.setFlowId(new FlowId());

            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {
                log.error("Failed to set up path BGP -> Peer {}" + "; src-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            else {
                log.debug("Successfully set up path BGP -> Peer {}" + "; src-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            */

            /**
             * Create the DataPath: BGP <-BGP peer
             */
            // Reversed BGP flow path for src-TCP-port
            flowPath.setFlowId(new FlowId());

            DataPath reverseDataPath = new DataPath();

            SwitchPort reverseDstPort =
                new SwitchPort(bgpdAttachmentPoint.dpid(),
                               bgpdAttachmentPoint.port());
            reverseDataPath.setDstPort(reverseDstPort);

            SwitchPort reverseSrcPort =
                new SwitchPort(new Dpid(peerInterface.getDpid()),
                               new Port(peerInterface.getSwitchPort().port()));
            reverseDataPath.setSrcPort(reverseSrcPort);
            flowPath.setDataPath(reverseDataPath);

            // Reverse the dst IP and src IP addresses
            flowEntryMatch.enableDstIPv4Net(srcIPv4Net);
            flowEntryMatch.enableSrcIPv4Net(dstIPv4Net);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            log.debug("Reversed BGP FlowPath: {}", flowPath.toString());

            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {

                log.error("Failed to set up path BGP <- Peer {}" + "; src-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            else {
                log.debug("Successfully set up path BGP <- Peer {}" + "; src-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            */

            // Reversed BGP flow path for dst-TCP-port
            flowPath.setFlowId(new FlowId());

            // Disable src-TCP-port, and set the dst-TCP-port
            flowEntryMatch.disableSrcTcpUdpPort();
            flowEntryMatch.enableDstTcpUdpPort(BGP_PORT);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            log.debug("Reversed BGP FlowPath: {}", flowPath.toString());

            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {
                log.error("Failed to setting up path BGP <- Peer {}" + "; dst-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            else {
                log.debug("Successfully setting up path BGP <- Peer {}" + "; dst-TCP-port:179",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            */

            /**
             * ICMP paths between BGPd and its peers
             */
            // match ICMP protocol BGP <- Peer
            flowPath.setFlowId(new FlowId());

            flowEntryMatch.enableIpProto(IPv4.PROTOCOL_ICMP);
            flowEntryMatch.disableSrcTcpUdpPort();
            flowEntryMatch.disableDstTcpUdpPort();

            flowPath.setFlowEntryMatch(flowEntryMatch);

            flowPath.setDataPath(reverseDataPath);

            log.debug("Reversed ICMP FlowPath: {}", flowPath.toString());

            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {

                log.error("Failed to set up ICMP path BGP <- Peer {}",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            else {
                log.debug("Successfully set up ICMP path BGP <- Peer {}",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            */

            // match ICMP protocol BGP -> Peer
            flowPath.setFlowId(new FlowId());

            flowEntryMatch.enableDstIPv4Net(dstIPv4Net);
            flowEntryMatch.enableSrcIPv4Net(srcIPv4Net);
            flowPath.setFlowEntryMatch(flowEntryMatch);

            flowPath.setDataPath(dataPath);

            log.debug("ICMP flowPath: {}", flowPath.toString());

            // TODO: Add the flow by using the new Path Intent framework
            /*
            if (flowManagerService.addFlow(flowPath) == null) {

                log.error("Failed to set up ICMP path BGP -> Peer {}",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            else {
                log.debug("Successfully set up ICMP path BGP -> Peer {}",
                        bgpPeer.getIpAddress().getHostAddress());
            }
            */
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
                log.debug("Pushing path to {} at {} on {}", new Object[]{
                        path.getDstIpAddress().getHostAddress(), macAddress,
                        path.getDstInterface().getSwitchPort()});
                // These paths should always be to BGP peers. Paths to non-peers are
                // handled once the first prefix is ready to push
                if (pushedPaths.containsKey(path.getDstIpAddress())) {
                    // A path already got pushed to this endpoint while we were waiting
                    // for ARP. We'll copy over the permanent attribute if it is set on this path.
                    if (path.isPermanent()) {
                        pushedPaths.get(path.getDstIpAddress()).setPermanent();
                    }
                } else {
                    calculateAndPushPath(path, macAddress);
                    pushedPaths.put(path.getDstIpAddress(), path);
                }
            }

            Set<RibUpdate> prefixesToPush = prefixesWaitingOnArp.removeAll(ipAddress);

            for (RibUpdate update : prefixesToPush) {
                // These will always be adds

                RibEntry rib = ptree.lookup(update.getPrefix());
                if (rib != null && rib.equals(update.getRibEntry())) {
                    log.debug("Pushing prefix {} next hop {}", update.getPrefix(),
                            rib.getNextHop().getHostAddress());
                    // We only push prefix flows if the prefix is still in the ptree
                    // and the next hop is the same as our update. The prefix could
                    // have been removed while we were waiting for the ARP, or the
                    // next hop could have changed.
                    _processRibAdd(update);
                } else {
                    log.debug("Received ARP response, but {},{} is no longer in ptree",
                            update.getPrefix(), update.getRibEntry());
                }
            }
        }
    }

    // TODO wait the priority module of the flow Manager
    private void setupArpFlows() {
        OFMatch match = new OFMatch();
        match.setDataLayerType(Ethernet.TYPE_ARP);
        match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);

        OFFlowMod fm = new OFFlowMod();
        fm.setMatch(match);

        OFActionOutput action = new OFActionOutput();
        action.setPort(OFPort.OFPP_CONTROLLER.getValue());
        action.setMaxLength((short) 0xffff);
        List<OFAction> actions = new ArrayList<OFAction>(1);
        actions.add(action);
        fm.setActions(actions);

        fm.setIdleTimeout((short) 0)
                .setHardTimeout((short) 0)
                .setBufferId(OFPacketOut.BUFFER_ID_NONE)
                .setCookie(0)
                .setCommand(OFFlowMod.OFPFC_ADD)
                .setPriority(ARP_PRIORITY)
                .setLengthU(OFFlowMod.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH);

        for (String strdpid : switches) {
            flowCache.write(HexString.toLong(strdpid), fm);
        }
    }

    // TODO need update, waiting for the priority feature from flow Manager
    private void setupDefaultDropFlows() {
        OFFlowMod fm = new OFFlowMod();
        fm.setMatch(new OFMatch());
        fm.setActions(new ArrayList<OFAction>()); // No action means drop

        fm.setIdleTimeout((short) 0)
                .setHardTimeout((short) 0)
                .setBufferId(OFPacketOut.BUFFER_ID_NONE)
                .setCookie(0)
                .setCommand(OFFlowMod.OFPFC_ADD)
                .setPriority((short) 0)
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
        matchLLDP.setDataLayerType((short) 0x88cc);
        matchLLDP.setWildcards(matchLLDP.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
        fmLLDP.setMatch(matchLLDP);

        OFMatch matchBDDP = new OFMatch();
        matchBDDP.setDataLayerType((short) 0x8942);
        matchBDDP.setWildcards(matchBDDP.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
        fmBDDP.setMatch(matchBDDP);

        OFActionOutput action = new OFActionOutput();
        action.setPort(OFPort.OFPP_CONTROLLER.getValue());
        action.setMaxLength((short) 0xffff);
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

        for (String strdpid : switches) {
            flowCache.write(HexString.toLong(strdpid), flowModList);
        }
    }

    private void beginRouting() {
        log.debug("Topology is now ready, beginning routing function");
        // TODO: Fix for the new Topology Network Graph
        // topology = topologyNetService.newDatabaseTopology();

        // Wait Pavlin's API. We need the following functions.
        /*setupArpFlows();
        setupDefaultDropFlows();*/

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

    // Before inserting the paths for BGP traffic, we should check whether
    // all the switches in the configuration file are discovered by ONOS
    private void checkSwitchesConnected() {
        // TODO: Fix the code below after topoSwitchSerice was removed
        /*
        for (String dpid : switches) {

            Iterator<ISwitchObject> activeSwitches = topoSwitchService.
                    getActiveSwitches().iterator();
            while(activeSwitches.hasNext())
            {
                ISwitchObject switchObject = activeSwitches.next();
                if (switchObject.getDPID().equals(dpid)) {
                    break;
                }
                if(activeSwitches.hasNext() == false) {
                    log.debug("Not all switches are here yet");
                    return;
                }
            }
        }
        switchesConnected = true;
        */
    }

    // Actually we only need to go half way round to verify full mesh connectivity
    private void checkTopologyReady() {
        for (Interface dstInterface : interfaces.values()) {
            for (Interface srcInterface : interfaces.values()) {
                if (dstInterface.equals(srcInterface)) {
                    continue;
                }

                // TODO: Fix for the new Topology Network Graph
                /*
                DataPath shortestPath = topologyNetService.getDatabaseShortestPath(
                        srcInterface.getSwitchPort(), dstInterface.getSwitchPort());

                if (shortestPath == null){
                    log.debug("Shortest path between {} and {} not found",
                            srcInterface.getSwitchPort(), dstInterface.getSwitchPort());
                    return;
                }
                */
            }
        }
        topologyReady = true;
    }

    private void checkStatus() {
        if (!switchesConnected) {
            checkSwitchesConnected();
        }
        boolean oldTopologyReadyStatus = topologyReady;
        if (switchesConnected && !topologyReady) {
            checkTopologyReady();
        }
        if (!oldTopologyReadyStatus && topologyReady) {
            beginRouting();
        }
    }

    private void doUpdatesThread() {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    RibUpdate update = ribUpdates.take();
                    switch (update.getOperation()) {
                        case UPDATE:
                            if (validateUpdate(update)) {
                                processRibAdd(update);
                            } else {
                                log.debug("Rib UPDATE out of order: {} via {}",
                                        update.getPrefix(), update.getRibEntry().getNextHop());
                            }
                            break;
                        case DELETE:
                            if (validateUpdate(update)) {
                                processRibDelete(update);
                            } else {
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
        } else if (newEntry.getSysUpTime() == oldEntry.getSysUpTime()) {
            if (newEntry.getSequenceNum() > oldEntry.getSequenceNum()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // The code below should be reimplemented after removal of Floodlight's
    // ITopologyService API. It should be implemented on top of network graph
    // notifications. (It was pretty hacky anyway...)
    /*
    @Override
    public void topologyChanged() {
        if (topologyReady) {
            return;
        }

        boolean refreshNeeded = false;
        for (LDUpdate ldu : topologyService.getLastLinkUpdates()){
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
    */

    @Override
    public void addedSwitch(IOFSwitch sw) {
        if (!topologyReady) {
            sw.clearAllFlowMods();
        }

        flowCache.switchConnected(sw);
    }

    @Override
    public void removedSwitch(IOFSwitch sw) {
        // Not used
    }

    @Override
    public void switchPortChanged(Long switchId) {
        // Not used
    }

    @Override
    public String getName() {
        return "BgpRoute";
    }

    /*
     * IConfigInfoService methods
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

    @Override
    public short getVlan() {
        return vlan;
    }
}
