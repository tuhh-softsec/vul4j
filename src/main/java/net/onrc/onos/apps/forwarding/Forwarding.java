package net.onrc.onos.apps.forwarding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.apps.proxyarp.BroadcastPacketOutNotification;
import net.onrc.onos.apps.proxyarp.IProxyArpService;
import net.onrc.onos.core.datagrid.IDatagridService;
import net.onrc.onos.core.datagrid.IEventChannel;
import net.onrc.onos.core.datagrid.IEventChannelListener;
import net.onrc.onos.core.devicemanager.IOnosDeviceService;
import net.onrc.onos.core.flowprogrammer.IFlowPusherService;
import net.onrc.onos.core.intent.Intent;
import net.onrc.onos.core.intent.Intent.IntentState;
import net.onrc.onos.core.intent.IntentMap;
import net.onrc.onos.core.intent.IntentOperation;
import net.onrc.onos.core.intent.IntentOperationList;
import net.onrc.onos.core.intent.PathIntent;
import net.onrc.onos.core.intent.ShortestPathIntent;
import net.onrc.onos.core.intent.runtime.IPathCalcRuntimeService;
import net.onrc.onos.core.intent.runtime.IntentStateList;
import net.onrc.onos.core.packet.Ethernet;
import net.onrc.onos.core.registry.IControllerRegistryService;
import net.onrc.onos.core.topology.Device;
import net.onrc.onos.core.topology.INetworkGraphService;
import net.onrc.onos.core.topology.LinkEvent;
import net.onrc.onos.core.topology.NetworkGraph;
import net.onrc.onos.core.topology.Switch;
import net.onrc.onos.core.util.Dpid;
import net.onrc.onos.core.util.FlowPath;
import net.onrc.onos.core.util.Port;
import net.onrc.onos.core.util.SwitchPort;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public class Forwarding implements IOFMessageListener, IFloodlightModule,
        IForwardingService, IEventChannelListener<Long, IntentStateList> {
    private static final Logger log = LoggerFactory.getLogger(Forwarding.class);

    private static final int SLEEP_TIME_FOR_DB_DEVICE_INSTALLED = 100; // milliseconds
    private static final int NUMBER_OF_THREAD_FOR_EXECUTOR = 1;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(NUMBER_OF_THREAD_FOR_EXECUTOR);

    private final String callerId = "Forwarding";

    private IFloodlightProviderService floodlightProvider;
    private IFlowPusherService flowPusher;
    private IDatagridService datagrid;

    private IEventChannel<Long, BroadcastPacketOutNotification> eventChannel;
    private static final String SINGLE_PACKET_OUT_CHANNEL_NAME = "onos.forwarding.packet_out";

    private IControllerRegistryService controllerRegistryService;

    private INetworkGraphService networkGraphService;
    private NetworkGraph networkGraph;
    private IPathCalcRuntimeService pathRuntime;
    private IntentMap intentMap;

    // TODO it seems there is a Guava collection that will time out entries.
    // We should see if this will work here.
    private Map<Path, PushedFlow> pendingFlows;
    private ListMultimap<String, PacketToPush> waitingPackets;

    private final Object lock = new Object();

    private static class PacketToPush {
        public final OFPacketOut packet;
        public final long dpid;

        public PacketToPush(OFPacketOut packet, long dpid) {
            this.packet = packet;
            this.dpid = dpid;
        }
    }

    private static class PushedFlow {
        public final String intentId;
        public boolean installed = false;
        public short firstOutPort;

        public PushedFlow(String flowId) {
            this.intentId = flowId;
        }
    }

    private static final class Path {
        public final MACAddress srcMac;
        public final MACAddress dstMac;

        public Path(MACAddress srcMac, MACAddress dstMac) {
            this.srcMac = srcMac;
            this.dstMac = dstMac;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Path)) {
                return false;
            }

            Path otherPath = (Path) other;
            return srcMac.equals(otherPath.srcMac) &&
                    dstMac.equals(otherPath.dstMac);
        }

        @Override
        public int hashCode() {
            int hash = 17;
            hash = 31 * hash + srcMac.hashCode();
            hash = 31 * hash + dstMac.hashCode();
            return hash;
        }

        @Override
        public String toString() {
            return "(" + srcMac + ") => (" + dstMac + ")";
        }
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        List<Class<? extends IFloodlightService>> services =
                new ArrayList<Class<? extends IFloodlightService>>(1);
        services.add(IForwardingService.class);
        return services;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> impls =
                new HashMap<Class<? extends IFloodlightService>, IFloodlightService>(1);
        impls.put(IForwardingService.class, this);
        return impls;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        List<Class<? extends IFloodlightService>> dependencies =
                new ArrayList<Class<? extends IFloodlightService>>();
        dependencies.add(IFloodlightProviderService.class);
        dependencies.add(IFlowPusherService.class);
        dependencies.add(IControllerRegistryService.class);
        dependencies.add(IOnosDeviceService.class);
        dependencies.add(IDatagridService.class);
        dependencies.add(INetworkGraphService.class);
        dependencies.add(IPathCalcRuntimeService.class);
        // We don't use the IProxyArpService directly, but reactive forwarding
        // requires it to be loaded and answering ARP requests
        dependencies.add(IProxyArpService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) {
        floodlightProvider =
                context.getServiceImpl(IFloodlightProviderService.class);
        flowPusher = context.getServiceImpl(IFlowPusherService.class);
        datagrid = context.getServiceImpl(IDatagridService.class);
        controllerRegistryService = context.getServiceImpl(IControllerRegistryService.class);
        networkGraphService = context.getServiceImpl(INetworkGraphService.class);
        pathRuntime = context.getServiceImpl(IPathCalcRuntimeService.class);

        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

        pendingFlows = new HashMap<Path, PushedFlow>();
        waitingPackets = LinkedListMultimap.create();
    }

    @Override
    public void startUp(FloodlightModuleContext context) {

        eventChannel = datagrid.createChannel(SINGLE_PACKET_OUT_CHANNEL_NAME,
                Long.class,
                BroadcastPacketOutNotification.class);
        networkGraph = networkGraphService.getNetworkGraph();
        intentMap = pathRuntime.getPathIntents();
        datagrid.addListener("onos.pathintent_state", this, Long.class, IntentStateList.class);
    }

    @Override
    public String getName() {
        return "onosforwarding";
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return (type == OFType.PACKET_IN) &&
                (name.equals("devicemanager") || name.equals("proxyarpmanager")
                        || name.equals("onosdevicemanager"));
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

    @Override
    public Command receive(
            IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

        if (msg.getType() != OFType.PACKET_IN || !(msg instanceof OFPacketIn)) {
            return Command.CONTINUE;
        }

        OFPacketIn pi = (OFPacketIn) msg;

        Ethernet eth = IFloodlightProviderService.bcStore.
                get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        log.debug("Receive PACKET_IN swId {}, portId {}", sw.getId(), pi.getInPort());

        if (eth.getEtherType() != Ethernet.TYPE_IPV4) {
            return Command.CONTINUE;
        }

        if (eth.isBroadcast() || eth.isMulticast()) {
            handleBroadcast(sw, pi, eth);
        } else {
            // Unicast
            handlePacketIn(sw, pi, eth);
        }

        return Command.STOP;
    }

    private void handleBroadcast(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
        if (log.isTraceEnabled()) {
            log.trace("Sending broadcast packet to other ONOS instances");
        }

        //We don't use address information, so 0 is put into the third argument.
        BroadcastPacketOutNotification key =
                new BroadcastPacketOutNotification(
                        eth.serialize(),
                        0, sw.getId(),
                        pi.getInPort());
        eventChannel.addTransientEntry(eth.getDestinationMAC().toLong(), key);
    }

    private void handlePacketIn(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
        log.debug("Start handlePacketIn swId {}, portId {}", sw.getId(), pi.getInPort());

        String destinationMac =
                HexString.toHexString(eth.getDestinationMACAddress());

        //FIXME getDeviceByMac() is a blocking call, so it may be better way to handle it to avoid the condition.
        Device deviceObject = networkGraph.getDeviceByMac(MACAddress.valueOf(destinationMac));

        if (deviceObject == null) {
            log.debug("No device entry found for {}",
                    destinationMac);

            //Device is not in the DB, so wait it until the device is added.
            EXECUTOR_SERVICE.schedule(new WaitDeviceArp(sw, pi, eth), SLEEP_TIME_FOR_DB_DEVICE_INSTALLED, TimeUnit.MILLISECONDS);
            return;
        }

        continueHandlePacketIn(sw, pi, eth, deviceObject);
    }

    private class WaitDeviceArp implements Runnable {
        IOFSwitch sw;
        OFPacketIn pi;
        Ethernet eth;

        public WaitDeviceArp(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
            super();
            this.sw = sw;
            this.pi = pi;
            this.eth = eth;
        }

        @Override
        public void run() {
            Device deviceObject = networkGraph.getDeviceByMac(MACAddress.valueOf(eth.getDestinationMACAddress()));
            if (deviceObject == null) {
                log.debug("wait {}ms and device was not found. Send broadcast packet and the thread finish.", SLEEP_TIME_FOR_DB_DEVICE_INSTALLED);
                handleBroadcast(sw, pi, eth);
                return;
            }
            log.debug("wait {}ms and device {} was found, continue", SLEEP_TIME_FOR_DB_DEVICE_INSTALLED, deviceObject.getMacAddress());
            continueHandlePacketIn(sw, pi, eth, deviceObject);
        }
    }

    private void continueHandlePacketIn(IOFSwitch sw, OFPacketIn pi, Ethernet eth, Device deviceObject) {

        log.debug("Start continuehandlePacketIn");

        //Iterator<IPortObject> ports = deviceObject.getAttachedPorts().iterator();
        Iterator<net.onrc.onos.core.topology.Port> ports = deviceObject.getAttachmentPoints().iterator();
        if (!ports.hasNext()) {
            log.debug("No attachment point found for device {} - broadcasting packet",
                    deviceObject.getMacAddress());
            handleBroadcast(sw, pi, eth);
            return;
        }

        //This code assumes the device has only one port. It should be problem.
        net.onrc.onos.core.topology.Port portObject = ports.next();
        short destinationPort = portObject.getNumber().shortValue();
        Switch switchObject = portObject.getSwitch();
        long destinationDpid = switchObject.getDpid();

        // TODO SwitchPort, Dpid and Port should probably be immutable
        SwitchPort srcSwitchPort = new SwitchPort(
                new Dpid(sw.getId()), new Port(pi.getInPort()));
        SwitchPort dstSwitchPort = new SwitchPort(
                new Dpid(destinationDpid), new Port(destinationPort));

        MACAddress srcMacAddress = MACAddress.valueOf(eth.getSourceMACAddress());
        MACAddress dstMacAddress = MACAddress.valueOf(eth.getDestinationMACAddress());

        synchronized (lock) {
            //TODO check concurrency
            Path pathspec = new Path(srcMacAddress, dstMacAddress);
            PushedFlow existingFlow = pendingFlows.get(pathspec);

            //A path is installed side by side to reduce a path timeout and a wrong state.
            if (existingFlow != null) {
                // We've already start to install a flow for this pair of MAC addresses
                if (log.isDebugEnabled()) {
                    log.debug("Found existing the same pathspec {}, intent ID is {}",
                            pathspec,
                            existingFlow.intentId);
                }

                OFPacketOut po = constructPacketOut(pi, sw);

                // Find the correct port here. We just assume the PI is from
                // the first hop switch, but this is definitely not always
                // the case. We'll have to retrieve the flow from HZ every time
                // because it could change (be rerouted) sometimes.
                if (existingFlow.installed) {
                    // Flow has been sent to the switches so it is safe to
                    // send a packet out now

                    Intent intent = intentMap.getIntent(existingFlow.intentId);
                    PathIntent pathIntent = null;
                    if (intent instanceof PathIntent) {
                        pathIntent = (PathIntent) intent;
                    } else {
                        log.debug("Intent {} is not PathIntent. Return.", intent.getId());
                        return;
                    }

                    Boolean isflowEntryForThisSwitch = false;
                    net.onrc.onos.core.topology.Path path = pathIntent.getPath();

                    for (Iterator<LinkEvent> i = path.iterator(); i.hasNext();) {
                        LinkEvent le = (LinkEvent) i.next();
                        if (le.getSrc().dpid == sw.getId()) {
                            log.debug("src {} dst {}", le.getSrc(), le.getDst());
                            isflowEntryForThisSwitch = true;
                            break;
                        }
                    }

                    if (isflowEntryForThisSwitch == false) {
                        // If we don't find a flow entry for that switch, then we're
                        // in the middle of a rerouting (or something's gone wrong).
                        // This packet will be dropped as a victim of the rerouting.
                        log.debug("Dropping packet on flow {} between {}-{}",
                                existingFlow.intentId,
                                srcMacAddress, dstMacAddress);
                    } else {
                        log.debug("Sending packet out from sw {}, outport{}", sw, existingFlow.firstOutPort);
                        sendPacketOut(sw, po, existingFlow.firstOutPort);
                    }
                } else {
                    // Flow path has not yet been installed to switches so save the
                    // packet out for later
                    log.debug("Put a packet into the waitng list. flowId {}", existingFlow.intentId);
                    waitingPackets.put(existingFlow.intentId, new PacketToPush(po, sw.getId()));
                }
                return;
            }

            log.debug("Adding new flow between {} at {} and {} at {}",
                    new Object[]{srcMacAddress, srcSwitchPort, dstMacAddress, dstSwitchPort});

            String intentId = callerId + ":" + controllerRegistryService.getNextUniqueId();
            IntentOperationList operations = new IntentOperationList();
            ShortestPathIntent intent = new ShortestPathIntent(intentId,
                    sw.getId(), pi.getInPort(), srcMacAddress.toLong(),
                    destinationDpid, destinationPort, dstMacAddress.toLong());
            IntentOperation.Operator operator = IntentOperation.Operator.ADD;
            operations.add(operator, intent);
            pathRuntime.executeIntentOperations(operations);

            OFPacketOut po = constructPacketOut(pi, sw);

            // Add to waiting lists
            pendingFlows.put(pathspec, new PushedFlow(intentId));
            log.debug("Put a Path {} in the pending flow, intent ID {}", pathspec, intentId);
            waitingPackets.put(intentId, new PacketToPush(po, sw.getId()));
            log.debug("Put a Packet in the wating list. related pathspec {}", pathspec);

        }
    }

    private OFPacketOut constructPacketOut(OFPacketIn pi, IOFSwitch sw) {
        OFPacketOut po = new OFPacketOut();
        po.setInPort(OFPort.OFPP_NONE)
                .setInPort(pi.getInPort())
                .setActions(new ArrayList<OFAction>())
                .setLengthU(OFPacketOut.MINIMUM_LENGTH);

        if (sw.getBuffers() == 0) {
            po.setBufferId(OFPacketOut.BUFFER_ID_NONE)
                    .setPacketData(pi.getPacketData())
                    .setLengthU(po.getLengthU() + po.getPacketData().length);
        } else {
            po.setBufferId(pi.getBufferId());
        }

        return po;
    }

    @Override
    public void flowsInstalled(Collection<FlowPath> installedFlowPaths) {
    }

    @Override
    public void flowRemoved(FlowPath removedFlowPath) {
    }

    public void flowRemoved(PathIntent removedIntent) {
        if (log.isTraceEnabled()) {
            log.trace("Path {} was removed", removedIntent.getParentIntent().getId());
        }

        ShortestPathIntent spfIntent = (ShortestPathIntent) removedIntent.getParentIntent();
        MACAddress srcMacAddress = MACAddress.valueOf(spfIntent.getSrcMac());
        MACAddress dstMacAddress = MACAddress.valueOf(spfIntent.getDstMac());
        Path removedPath = new Path(srcMacAddress, dstMacAddress);

        synchronized (lock) {
            // There *shouldn't* be any packets queued if the flow has
            // just been removed.
            List<PacketToPush> packets = waitingPackets.removeAll(spfIntent.getId());
            if (!packets.isEmpty()) {
                log.warn("Removed flow {} has packets queued.", spfIntent.getId());
            }
            pendingFlows.remove(removedPath);
            log.debug("Removed from the pendingFlow: Path {}, Flow ID {}", removedPath, spfIntent.getId());
        }
    }

    private void flowInstalled(PathIntent installedPath) {
        if (log.isTraceEnabled()) {
            log.trace("Path {} was installed", installedPath.getParentIntent().getId());
        }

        ShortestPathIntent spfIntent = (ShortestPathIntent) installedPath.getParentIntent();
        MACAddress srcMacAddress = MACAddress.valueOf(spfIntent.getSrcMac());
        MACAddress dstMacAddress = MACAddress.valueOf(spfIntent.getDstMac());
        Path path = new Path(srcMacAddress, dstMacAddress);
        log.debug("Path spec {}", path);

        // TODO waiting packets should time out. We could request a path that
        // can't be installed right now because of a network partition. The path
        // may eventually be installed, but we may have received thousands of
        // packets in the meantime and probably don't want to send very old packets.

        List<PacketToPush> packets = null;
        net.onrc.onos.core.topology.Path graphPath = installedPath.getPath();

        log.debug("path{}", graphPath);
        Short outPort = graphPath.get(0).getSrc().getNumber().shortValue();

        PushedFlow existingFlow = null;

        synchronized (lock) {
            existingFlow = pendingFlows.get(path);

            if (existingFlow != null) {
                existingFlow.installed = true;
                existingFlow.firstOutPort = outPort;
            } else {
                log.debug("ExistingFlow {} is null", path);
                return;
            }

            //Check both existing flow are installed status.
            if (existingFlow.installed) {
                packets = waitingPackets.removeAll(existingFlow.intentId);
                if (log.isDebugEnabled()) {
                    log.debug("removed my packets {} to push from waitingPackets. outPort {} size {}",
                            existingFlow.intentId, existingFlow.firstOutPort, packets.size());
                }
            } else {
                log.debug("Forward or reverse flows hasn't been pushed yet. return");
                return;
            }
        }

        for (PacketToPush packet : packets) {
            log.debug("Start packetToPush to sw {}, outPort {}, path {}", packet.dpid, existingFlow.firstOutPort, path);
            IOFSwitch sw = floodlightProvider.getSwitches().get(packet.dpid);
            sendPacketOut(sw, packet.packet, existingFlow.firstOutPort);
        }
    }

    private void sendPacketOut(IOFSwitch sw, OFPacketOut po, short outPort) {
        po.getActions().add(new OFActionOutput(outPort));
        po.setActionsLength((short)
                (po.getActionsLength() + OFActionOutput.MINIMUM_LENGTH));
        po.setLengthU(po.getLengthU() + OFActionOutput.MINIMUM_LENGTH);

        flowPusher.add(sw, po);
    }

    @Override
    public void entryAdded(IntentStateList value) {
        entryUpdated(value);

    }

    @Override
    public void entryRemoved(IntentStateList value) {
        //no-op
    }

    @Override
    public void entryUpdated(IntentStateList value) {
        for (Entry<String, IntentState> entry : value.entrySet()) {
            log.debug("path intent key {}, value {}", entry.getKey(), entry.getValue());
            PathIntent pathIntent = (PathIntent) intentMap.getIntent(entry.getKey());
            if (pathIntent == null)
                continue;

            if (!(pathIntent.getParentIntent() instanceof ShortestPathIntent))
                continue;

            IntentState state = entry.getValue();
            switch (state) {
                case INST_REQ:
                    break;
                case INST_ACK:
                    flowInstalled(pathIntent);
                    break;
                case INST_NACK:
                    break;
                case DEL_REQ:
                    break;
                case DEL_ACK:
                    flowRemoved(pathIntent);
                    break;
                case DEL_PENDING:
                    break;
                default:
                    break;
            }
        }
    }
}
