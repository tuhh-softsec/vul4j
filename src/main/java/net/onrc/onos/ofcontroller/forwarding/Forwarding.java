package net.onrc.onos.ofcontroller.forwarding;

import java.net.InetAddress;
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
import net.floodlightcontroller.packet.IPv4;
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
import net.onrc.onos.ofcontroller.proxyarp.ArpMessage;
import net.onrc.onos.ofcontroller.topology.TopologyManager;
import net.onrc.onos.ofcontroller.util.CallerId;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
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
import com.google.common.net.InetAddresses;

public class Forwarding implements IOFMessageListener, IFloodlightModule,
									IForwardingService {
	private final static Logger log = LoggerFactory.getLogger(Forwarding.class);

	private final int IDLE_TIMEOUT = 5; // seconds
	private final int HARD_TIMEOUT = 0; // seconds

	private final int PATH_PUSHED_TIMEOUT = 3000; // milliseconds
	
	private IFloodlightProviderService floodlightProvider;
	private IFlowService flowService;
	private IFlowPusherService flowPusher;
	private IDatagridService datagrid;
	
	private IDeviceStorage deviceStorage;
	private TopologyManager topologyService;
	
	//private Map<Path, Long> pendingFlows;
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
		private final long pushedTime;
		public short firstHopOutPort = OFPort.OFPP_NONE.getValue();
		
		public PushedFlow(long flowId) {
			this.flowId = flowId;
			pushedTime = System.currentTimeMillis();
		}
		
		public boolean isExpired() {
			return (System.currentTimeMillis() - pushedTime) > PATH_PUSHED_TIMEOUT;
		}
	}
	
	private final class Path {
		public final SwitchPort srcPort;
		public final SwitchPort dstPort;
		public final MACAddress srcMac;
		public final MACAddress dstMac;
		
		public Path(SwitchPort src, SwitchPort dst, 
				MACAddress srcMac, MACAddress dstMac) {
			srcPort = new SwitchPort(new Dpid(src.dpid().value()), 
					new Port(src.port().value()));
			dstPort = new SwitchPort(new Dpid(dst.dpid().value()), 
					new Port(dst.port().value()));
			this.srcMac = srcMac;
			this.dstMac = dstMac;
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Path)) {
				return false;
			}
			
			Path otherPath = (Path) other;
			return srcPort.equals(otherPath.srcPort) && 
					dstPort.equals(otherPath.dstPort) &&
					srcMac.equals(otherPath.srcMac) &&
					dstMac.equals(otherPath.dstMac);
		}
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash = 31 * hash + srcPort.hashCode();
			hash = 31 * hash + dstPort.hashCode();
			hash = 31 * hash + srcMac.hashCode();
			hash = 31 * hash + dstMac.hashCode();
			return hash;
		}
		
		@Override
		public String toString() {
			return "(" + srcMac + " at " + srcPort + ") => (" 
					+ dstPort + " at " + dstMac + ")";
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
		
		//pendingFlows = new ConcurrentHashMap<Path, Long>();
		pendingFlows = new HashMap<Path, PushedFlow>();
		//waitingPackets = Multimaps.synchronizedSetMultimap(
				//HashMultimap.<Long, PacketToPush>create());
		//waitingPackets = HashMultimap.create();
		waitingPackets = LinkedListMultimap.create();
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("","");
		topologyService = new TopologyManager();
		topologyService.init("","");
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
			//return Command.CONTINUE;
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
		
		IPv4 ipv4Packet = (IPv4) eth.getPayload();
		
		// TODO We'll put the destination address here, because the current
		// architecture needs an address. Addresses are only used for replies
		// however, which don't apply to non-ARP packets. The ArpMessage class
		// has become a bit too overloaded and should be refactored to 
		// handle all use cases nicely.
		 InetAddress targetAddress = 
				InetAddresses.fromInteger(ipv4Packet.getDestinationAddress());
		
		// Piggy-back on the ARP mechanism to broadcast this packet out the
		// edge. Luckily the ARP module doesn't check that the packet is
		// actually ARP before broadcasting, so we can trick it into sending
		// our non-ARP packets.
		// TODO This should be refactored later to account for the new use case.
		datagrid.sendArpRequest(ArpMessage.newRequest(targetAddress, eth.serialize(),
				-1L, (short)-1, sw.getId(), pi.getInPort()));
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
		// (also, are Dpid and Port are even necessary?)
		SwitchPort srcSwitchPort = new SwitchPort(
				new Dpid(sw.getId()), new Port(pi.getInPort())); 
		SwitchPort dstSwitchPort = new SwitchPort(
				new Dpid(destinationDpid), new Port(destinationPort)); 
				
		MACAddress srcMacAddress = MACAddress.valueOf(eth.getSourceMACAddress());
		MACAddress dstMacAddress = MACAddress.valueOf(eth.getDestinationMACAddress());
		
		
		FlowPath flowPath, reverseFlowPath;
		
		Path pathspec = new Path(srcSwitchPort, dstSwitchPort, 
				srcMacAddress, dstMacAddress);
		// TODO check concurrency
		synchronized (lock) {
			PushedFlow existingFlow = pendingFlows.get(pathspec);
			//Long existingFlowId = pendingFlows.get(pathspec);
			
			if (existingFlow != null && !existingFlow.isExpired()) {
				log.debug("Found existing flow {}", 
						HexString.toHexString(existingFlow.flowId));
				
				OFPacketOut po = constructPacketOut(pi, sw);
				
				if (existingFlow.firstHopOutPort != OFPort.OFPP_NONE.getValue()) {
					// Flow has been sent to the switches so it is safe to
					// send a packet out now
					sendPacketOut(sw, po, existingFlow.firstHopOutPort);
				}
				else {
					// Flow has not yet been sent to switches so save the
					// packet out for later
					waitingPackets.put(existingFlow.flowId, 
							new PacketToPush(po, sw.getId()));
				}
				return;
			}
			
			//log.debug("Couldn't match {} in {}", pathspec, pendingFlows);
			
			log.debug("Adding new flow between {} at {} and {} at {}",
					new Object[]{srcMacAddress, srcSwitchPort, dstMacAddress, dstSwitchPort});
			
			
			CallerId callerId = new CallerId("Forwarding");
			
			DataPath datapath = new DataPath();
			datapath.setSrcPort(srcSwitchPort);
			datapath.setDstPort(dstSwitchPort);
			
			flowPath = new FlowPath();
			flowPath.setInstallerId(callerId);
	
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
			reverseFlowPath.setInstallerId(callerId);
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
			
			// TODO what happens if no path exists? cleanup
			
			FlowId flowId = new FlowId(flowService.getNextFlowEntryId());
			FlowId reverseFlowId = new FlowId(flowService.getNextFlowEntryId());
			
			flowPath.setFlowId(flowId);
			reverseFlowPath.setFlowId(reverseFlowId);
			
			OFPacketOut po = constructPacketOut(pi, sw);
			Path reversePathSpec = new Path(dstSwitchPort, srcSwitchPort, 
					dstMacAddress, srcMacAddress);
			
			// Add to waiting lists
			//pendingFlows.put(pathspec, flowId.value());
			//pendingFlows.put(reversePathSpec, reverseFlowId.value());
			pendingFlows.put(pathspec, new PushedFlow(flowId.value()));
			pendingFlows.put(reversePathSpec, new PushedFlow(reverseFlowId.value()));
			waitingPackets.put(flowId.value(), new PacketToPush(po, sw.getId()));
		
		}
		
		flowService.addFlow(reverseFlowPath);
		flowService.addFlow(flowPath);
		
	}
	
	/*
	private boolean flowExists(SwitchPort srcPort, MACAddress srcMac, 
			SwitchPort dstPort, MACAddress dstMac) {
		for (FlowPath flow : datagridService.getAllFlows()) {
			FlowEntryMatch match = flow.flowEntryMatch();
			// TODO implement FlowEntryMatch.equals();
			// This is painful to do properly without support in the FlowEntryMatch
			boolean same = true;
			if (!match.srcMac().equals(srcMac) ||
				!match.dstMac().equals(dstMac)) {
				same = false;
			}
			if (!flow.dataPath().srcPort().equals(srcPort) || 
				!flow.dataPath().dstPort().equals(dstPort)) {
				same = false;
			}
			
			if (same) {
				log.debug("found flow entry that's the same {}-{}:::{}-{}",
						new Object[] {srcPort, srcMac, dstPort, dstMac});
				return true;
			}
		}
		
		return false;
	}
	*/

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

	private void flowInstalled(FlowPath installedFlowPath) {
		long flowId = installedFlowPath.flowId().value();
		
		short outPort = 
				installedFlowPath.flowEntries().get(0).outPort().value();
		
		MACAddress srcMacAddress = installedFlowPath.flowEntryMatch().srcMac();
		MACAddress dstMacAddress = installedFlowPath.flowEntryMatch().dstMac();
		
		if (srcMacAddress == null || dstMacAddress == null) {
			// Not our flow path, ignore
			return;
		}
		
		Collection<PacketToPush> packets;
		synchronized (lock) {
			log.debug("Flow {} has been installed, sending queued packets",
					installedFlowPath.flowId());
			
			packets = waitingPackets.removeAll(flowId);
			
			// remove pending flows entry
			Path installedPath = new Path(installedFlowPath.dataPath().srcPort(),
					installedFlowPath.dataPath().dstPort(),
					srcMacAddress, dstMacAddress);
			//pendingFlows.remove(pathToRemove);
			PushedFlow existingFlow = pendingFlows.get(installedPath);
			if (existingFlow != null)
			    existingFlow.firstHopOutPort = outPort;
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
