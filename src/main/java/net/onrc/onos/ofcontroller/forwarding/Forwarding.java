package net.onrc.onos.ofcontroller.forwarding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.devicemanager.IOnosDeviceService;
import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.proxyarp.BroadcastPacketOutNotification;
import net.onrc.onos.ofcontroller.proxyarp.IProxyArpService;
import net.onrc.onos.ofcontroller.topology.TopologyManager;
import net.onrc.onos.ofcontroller.util.CallerId;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.FlowPathType;
import net.onrc.onos.ofcontroller.util.FlowPathUserState;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

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
									IForwardingService {
	private final static Logger log = LoggerFactory.getLogger(Forwarding.class);

	private final int IDLE_TIMEOUT = 5; // seconds
	private final int HARD_TIMEOUT = 0; // seconds
	
	private final CallerId callerId = new CallerId("Forwarding");
	
	private IFloodlightProviderService floodlightProvider;
	private IFlowService flowService;
	private IFlowPusherService flowPusher;
	private IDatagridService datagrid;
	
	private IDeviceStorage deviceStorage;
	private TopologyManager topologyService;
	
	// TODO it seems there is a Guava collection that will time out entries.
	// We should see if this will work here.
	private Map<Path, PushedFlow> pendingFlows;
	private ListMultimap<Long, PacketToPush> waitingPackets;
	
	private final Object lock = new Object();
	
	private class PacketToPush {
		public final OFPacketOut packet;
		public final long dpid;
		
		public PacketToPush(OFPacketOut packet, long dpid) {
			this.packet = packet;
			this.dpid = dpid;
		}
	}
	
	private class PushedFlow {
		public final long flowId;
		public boolean installed = false;
		
		public PushedFlow(long flowId) {
			this.flowId = flowId;
		}
	}
	
	private final class Path {
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
		dependencies.add(IFlowService.class);
		dependencies.add(IFlowPusherService.class);
		dependencies.add(IOnosDeviceService.class);
		// We don't use the IProxyArpService directly, but reactive forwarding
		// requires it to be loaded and answering ARP requests
		dependencies.add(IProxyArpService.class);
		return dependencies;
	}
	
	@Override
	public void init(FloodlightModuleContext context) {
		floodlightProvider = 
				context.getServiceImpl(IFloodlightProviderService.class);
		flowService = context.getServiceImpl(IFlowService.class);
		flowPusher = context.getServiceImpl(IFlowPusherService.class);
		datagrid = context.getServiceImpl(IDatagridService.class);
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

		pendingFlows = new HashMap<Path, PushedFlow>();
		waitingPackets = LinkedListMultimap.create();
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("");
		topologyService = new TopologyManager();
		topologyService.init("");
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) {
		// no-op
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
		
		if (msg.getType() != OFType.PACKET_IN) {
			return Command.CONTINUE;
		}
		
		OFPacketIn pi = (OFPacketIn) msg;
		
		Ethernet eth = IFloodlightProviderService.bcStore.
				get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		if (eth.getEtherType() != Ethernet.TYPE_IPv4) {
			return Command.CONTINUE;
		}
		
		if (eth.isBroadcast() || eth.isMulticast()) {
			handleBroadcast(sw, pi, eth);
		}
		else {
			// Unicast
			handlePacketIn(sw, pi, eth);
		}
		
		return Command.STOP;
	}
	
	private void handleBroadcast(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
		if (log.isTraceEnabled()) {
			log.trace("Sending broadcast packet to other ONOS instances");
		}

		 datagrid.sendPacketOutNotification(new BroadcastPacketOutNotification(
				 eth.serialize(), sw.getId(), pi.getInPort()));
	}
	
	private void handlePacketIn(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
		String destinationMac = 
				HexString.toHexString(eth.getDestinationMACAddress()); 
		
		IDeviceObject deviceObject = deviceStorage.getDeviceByMac(
				destinationMac);
		
		if (deviceObject == null) {
			log.debug("No device entry found for {} - broadcasting packet", 
					destinationMac);
			handleBroadcast(sw, pi, eth);
			return;
		}
		
		Iterator<IPortObject> ports = deviceObject.getAttachedPorts().iterator();	
		if (!ports.hasNext()) {
			log.debug("No attachment point found for device {} - broadcasting packet", 
					destinationMac);
			handleBroadcast(sw, pi, eth);
			return;
		}
		IPortObject portObject = ports.next();
		short destinationPort = portObject.getNumber();
		ISwitchObject switchObject = portObject.getSwitch();
		long destinationDpid = HexString.toLong(switchObject.getDPID());
		
		// TODO SwitchPort, Dpid and Port should probably be immutable
		SwitchPort srcSwitchPort = new SwitchPort(
				new Dpid(sw.getId()), new Port(pi.getInPort())); 
		SwitchPort dstSwitchPort = new SwitchPort(
				new Dpid(destinationDpid), new Port(destinationPort)); 
				
		MACAddress srcMacAddress = MACAddress.valueOf(eth.getSourceMACAddress());
		MACAddress dstMacAddress = MACAddress.valueOf(eth.getDestinationMACAddress());
		
		FlowPath flowPath, reverseFlowPath;
		
		Path pathspec = new Path(srcMacAddress, dstMacAddress);
		// TODO check concurrency
		synchronized (lock) {
			PushedFlow existingFlow = pendingFlows.get(pathspec);

			if (existingFlow != null) {
				// We've already installed a flow for this pair of MAC addresses
				log.debug("Found existing flow {}", 
						HexString.toHexString(existingFlow.flowId));
				
				OFPacketOut po = constructPacketOut(pi, sw);
				
				// Find the correct port here. We just assume the PI is from 
				// the first hop switch, but this is definitely not always
				// the case. We'll have to retrieve the flow from HZ every time
				// because it could change (be rerouted) sometimes.
				if (existingFlow.installed) {
					// Flow has been sent to the switches so it is safe to
					// send a packet out now
					FlowPath flow = datagrid.getFlow(new FlowId(existingFlow.flowId));
					FlowEntry flowEntryForThisSwitch = null;
					
					if (flow != null) {
						for (FlowEntry flowEntry : flow.flowEntries()) {
							if (flowEntry.dpid().equals(new Dpid(sw.getId()))) {
								flowEntryForThisSwitch = flowEntry;
								break;
							}
						}
					}
					
					if (flowEntryForThisSwitch == null) {
						// If we don't find a flow entry for that switch, then we're
						// in the middle of a rerouting (or something's gone wrong). 
						// This packet will be dropped as a victim of the rerouting.
						log.debug("Dropping packet on flow {} between {}-{}, flow path {}",
								new Object[] {new FlowId(existingFlow.flowId),
								srcMacAddress, dstMacAddress, flow});
					}
					else {
						sendPacketOut(sw, po, flowEntryForThisSwitch.outPort().value());
					}
				}
				else {
					// Flow has not yet been sent to switches so save the
					// packet out for later
					waitingPackets.put(existingFlow.flowId, 
							new PacketToPush(po, sw.getId()));
				}
				return;
			}

			log.debug("Adding new flow between {} at {} and {} at {}",
					new Object[]{srcMacAddress, srcSwitchPort, dstMacAddress, dstSwitchPort});
			
			DataPath datapath = new DataPath();
			datapath.setSrcPort(srcSwitchPort);
			datapath.setDstPort(dstSwitchPort);
			
			flowPath = new FlowPath();
			flowPath.setInstallerId(new CallerId(callerId));
	
			flowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
			flowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);
			flowPath.setFlowEntryMatch(new FlowEntryMatch());
			flowPath.setIdleTimeout(IDLE_TIMEOUT);
			flowPath.setHardTimeout(HARD_TIMEOUT);
			flowPath.flowEntryMatch().enableSrcMac(srcMacAddress);
			flowPath.flowEntryMatch().enableDstMac(dstMacAddress);
			flowPath.flowEntryMatch().enableEthernetFrameType(Ethernet.TYPE_IPv4);
			flowPath.setDataPath(datapath);
			
			
			DataPath reverseDataPath = new DataPath();
			// Reverse the ports for the reverse path
			reverseDataPath.setSrcPort(dstSwitchPort);
			reverseDataPath.setDstPort(srcSwitchPort);
			
			// TODO implement copy constructor for FlowPath
			reverseFlowPath = new FlowPath();
			reverseFlowPath.setInstallerId(new CallerId(callerId));
			reverseFlowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
			reverseFlowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);
			reverseFlowPath.setIdleTimeout(IDLE_TIMEOUT);
			reverseFlowPath.setHardTimeout(HARD_TIMEOUT);
			reverseFlowPath.setFlowEntryMatch(new FlowEntryMatch());
			// Reverse the MAC addresses for the reverse path
			reverseFlowPath.flowEntryMatch().enableSrcMac(dstMacAddress);
			reverseFlowPath.flowEntryMatch().enableDstMac(srcMacAddress);
			reverseFlowPath.flowEntryMatch().enableEthernetFrameType(Ethernet.TYPE_IPv4);
			reverseFlowPath.setDataPath(reverseDataPath);
			reverseFlowPath.dataPath().srcPort().dpid().toString();

			FlowId flowId = new FlowId(flowService.getNextFlowEntryId());
			FlowId reverseFlowId = new FlowId(flowService.getNextFlowEntryId());
			
			flowPath.setFlowId(flowId);
			reverseFlowPath.setFlowId(reverseFlowId);
			
			OFPacketOut po = constructPacketOut(pi, sw);
			Path reversePathSpec = new Path(dstMacAddress, srcMacAddress);
			
			// Add to waiting lists
			pendingFlows.put(pathspec, new PushedFlow(flowId.value()));
			pendingFlows.put(reversePathSpec, new PushedFlow(reverseFlowId.value()));
			waitingPackets.put(flowId.value(), new PacketToPush(po, sw.getId()));
		
		}
		
		log.debug("Adding reverse {} to {} flowid {}", new Object[] {
				dstMacAddress, srcMacAddress, reverseFlowPath.flowId()});
		flowService.addFlow(reverseFlowPath);
		log.debug("Adding forward {} to {} flowid {}", new Object[] {
				srcMacAddress, dstMacAddress, flowPath.flowId()});
		flowService.addFlow(flowPath);
		
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
		}
		else {
			po.setBufferId(pi.getBufferId());
		}
		
		return po;
	}

	@Override
	public void flowsInstalled(Collection<FlowPath> installedFlowPaths) {
		for (FlowPath flowPath : installedFlowPaths) {
			flowInstalled(flowPath);
		}
	}
	
	@Override
	public void flowRemoved(FlowPath removedFlowPath) {
		if (!removedFlowPath.installerId().equals(callerId)) {
			// Not our flow path, ignore
			return;
		}
		
		MACAddress srcMacAddress = removedFlowPath.flowEntryMatch().srcMac();
		MACAddress dstMacAddress = removedFlowPath.flowEntryMatch().dstMac();
		
		Path removedPath = new Path(srcMacAddress, dstMacAddress);
		
		synchronized (lock) {
			pendingFlows.remove(removedPath);
			
			// There *shouldn't* be any packets queued if the flow has 
			// just been removed. 
			List<PacketToPush> packets = 
					waitingPackets.removeAll(removedFlowPath.flowId().value());
			if (!packets.isEmpty()) {
				log.warn("Removed flow {} has packets queued", 
						removedFlowPath.flowId());
			}
		}
	}

	private void flowInstalled(FlowPath installedFlowPath) {
		long flowId = installedFlowPath.flowId().value();
		
		if (!installedFlowPath.installerId().equals(callerId)) {
			// Not our flow path, ignore
			return;
		}
		
		// TODO waiting packets should time out. We could request a path that
		// can't be installed right now because of a network partition. The path
		// may eventually be installed, but we may have received thousands of 
		// packets in the meantime and probably don't want to send very old packets.
		short outPort = 
				installedFlowPath.flowEntries().get(0).outPort().value();
		
		MACAddress srcMacAddress = installedFlowPath.flowEntryMatch().srcMac();
		MACAddress dstMacAddress = installedFlowPath.flowEntryMatch().dstMac();
		
		Collection<PacketToPush> packets;
		synchronized (lock) {
			packets = waitingPackets.removeAll(flowId);
			
			log.debug("Flow {} has been installed, sending {} queued packets",
					installedFlowPath.flowId(), packets.size());
			
			// remove pending flows entry
			Path installedPath = new Path(srcMacAddress, dstMacAddress);
			PushedFlow existingFlow = pendingFlows.get(installedPath);
			if (existingFlow != null) {
			    existingFlow.installed = true;
			}
		}
		
		for (PacketToPush packet : packets) {
			IOFSwitch sw = floodlightProvider.getSwitches().get(packet.dpid);
			
			sendPacketOut(sw, packet.packet, outPort);
		}
	}
	
	private void sendPacketOut(IOFSwitch sw, OFPacketOut po, short outPort) {
		po.getActions().add(new OFActionOutput(outPort));
		po.setActionsLength((short)
				(po.getActionsLength() + OFActionOutput.MINIMUM_LENGTH));
		po.setLengthU(po.getLengthU() + OFActionOutput.MINIMUM_LENGTH);
		
		flowPusher.add(sw, po);
	}

}
