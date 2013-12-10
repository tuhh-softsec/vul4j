package net.onrc.onos.ofcontroller.forwarding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class Forwarding implements IOFMessageListener, IFloodlightModule,
									IForwardingService {
	private final static Logger log = LoggerFactory.getLogger(Forwarding.class);

	private IFloodlightProviderService floodlightProvider;
	private IFlowService flowService;
	@SuppressWarnings("unused")
	private IDatagridService datagridService;
	
	private IDeviceStorage deviceStorage;
	private TopologyManager topologyService;
	
	private Map<Path, Long> pendingFlows;
	private Multimap<Long, PacketToPush> waitingPackets;
	
	public class PacketToPush {
		public final OFPacketOut packet;
		public final long dpid;
		
		public PacketToPush(OFPacketOut packet, long dpid) {
			this.packet = packet;
			this.dpid = dpid;
		}
	}
	
	public final class Path {
		public final SwitchPort srcPort;
		public final SwitchPort dstPort;
		
		public Path(SwitchPort src, SwitchPort dst) {
			srcPort = new SwitchPort(new Dpid(src.dpid().value()), 
					new Port(src.port().value()));
			dstPort = new SwitchPort(new Dpid(dst.dpid().value()), 
					new Port(dst.port().value()));
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Path)) {
				return false;
			}
			
			Path otherPath = (Path) other;
			return srcPort.equals(otherPath.srcPort) && 
					dstPort.equals(otherPath.dstPort);
		}
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash = 31 * hash + srcPort.hashCode();
			hash = 31 * hash + dstPort.hashCode();
			return hash;
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
		dependencies.add(IDatagridService.class);
		return dependencies;
	}
	
	@Override
	public void init(FloodlightModuleContext context) {
		this.floodlightProvider = 
				context.getServiceImpl(IFloodlightProviderService.class);
		this.flowService = context.getServiceImpl(IFlowService.class);
		this.datagridService = context.getServiceImpl(IDatagridService.class);
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		
		pendingFlows = new ConcurrentHashMap<Path, Long>();
		waitingPackets = Multimaps.synchronizedSetMultimap(
				HashMultimap.<Long, PacketToPush>create());
		
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
				(name.equals("devicemanager") || name.equals("proxyarpmanager"));
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
		
		// We only want to handle unicast IPv4
		if (eth.isBroadcast() || eth.isMulticast() || 
				eth.getEtherType() != Ethernet.TYPE_IPv4) {
			return Command.CONTINUE;
		}
		
		handlePacketIn(sw, pi, eth);
		
		return Command.STOP;
	}
	
	private void handlePacketIn(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
		String destinationMac = 
				HexString.toHexString(eth.getDestinationMACAddress()); 
		
		IDeviceObject deviceObject = deviceStorage.getDeviceByMac(
				destinationMac);
		
		if (deviceObject == null) {
			log.debug("No device entry found for {}", destinationMac);
			return;
		}
		
		Iterator<IPortObject> ports = deviceObject.getAttachedPorts().iterator();	
		if (!ports.hasNext()) {
			log.debug("No attachment point found for device {}", destinationMac);
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
		
		
		DataPath datapath = new DataPath();
		datapath.setSrcPort(srcSwitchPort);
		datapath.setDstPort(dstSwitchPort);
		
		
		
		Path pathspec = new Path(srcSwitchPort, dstSwitchPort);
		// TODO check concurrency
		Long existingFlowId = pendingFlows.get(pathspec);
		
		if (existingFlowId != null) {
			log.debug("Found existing flow {}", 
					HexString.toHexString(existingFlowId));
			
			// TODO do stuff.
			OFPacketOut po = constructPacketOut(datapath, pi, sw);
			waitingPackets.put(existingFlowId, new PacketToPush(po, sw.getId()));
			return;
		}
		
		
		log.debug("Adding new flow between {} at {} and {} at {}",
				new Object[]{srcMacAddress, srcSwitchPort, dstMacAddress, dstSwitchPort});
		
		
		CallerId callerId = new CallerId("Forwarding");
		
		//FlowId flowId = new FlowId(flowService.getNextFlowEntryId());
		FlowPath flowPath = new FlowPath();
		//flowPath.setFlowId(flowId);
		flowPath.setInstallerId(callerId);

		flowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
		flowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);
		flowPath.setFlowEntryMatch(new FlowEntryMatch());
		flowPath.flowEntryMatch().enableSrcMac(srcMacAddress);
		flowPath.flowEntryMatch().enableDstMac(dstMacAddress);
		// For now just forward IPv4 packets. This prevents accidentally
		// forwarding other stuff like ARP.
		flowPath.flowEntryMatch().enableEthernetFrameType(Ethernet.TYPE_IPv4);
		flowPath.setDataPath(datapath);
			
		DataPath reverseDataPath = new DataPath();
		// Reverse the ports for the reverse path
		reverseDataPath.setSrcPort(dstSwitchPort);
		reverseDataPath.setDstPort(srcSwitchPort);
		
		// TODO implement copy constructor for FlowPath
		FlowPath reverseFlowPath = new FlowPath();
		//reverseFlowPath.setFlowId(reverseFlowId);
		reverseFlowPath.setInstallerId(callerId);
		reverseFlowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
		reverseFlowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);
		reverseFlowPath.setFlowEntryMatch(new FlowEntryMatch());
		// Reverse the MAC addresses for the reverse path
		reverseFlowPath.flowEntryMatch().enableSrcMac(dstMacAddress);
		reverseFlowPath.flowEntryMatch().enableDstMac(srcMacAddress);
		reverseFlowPath.flowEntryMatch().enableEthernetFrameType(Ethernet.TYPE_IPv4);
		reverseFlowPath.setDataPath(reverseDataPath);
		reverseFlowPath.dataPath().srcPort().dpid().toString();
		
		// TODO what happens if no path exists?
		FlowId flowId = new FlowId(flowService.getNextFlowEntryId());
		FlowId reverseFlowId = new FlowId(flowService.getNextFlowEntryId());
		
		flowPath.setFlowId(flowId);
		reverseFlowPath.setFlowId(reverseFlowId);
		
		OFPacketOut po = constructPacketOut(datapath, pi, sw);
		Path reversePathSpec = new Path(dstSwitchPort, srcSwitchPort);
		
		// Add to waiting lists
		pendingFlows.put(pathspec, flowId.value());
		pendingFlows.put(reversePathSpec, reverseFlowId.value());
		waitingPackets.put(flowId.value(), new PacketToPush(po, sw.getId()));
		
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

	private OFPacketOut constructPacketOut(DataPath datapath, OFPacketIn pi, 
			IOFSwitch sw) {
		//List<OFAction> actions = new ArrayList<OFAction>(1);
		//actions.add(new OFActionOutput(port));
		
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
	public void flowInstalled(FlowPath installedFlowPath) {
		// TODO check concurrency
		// will need to sync and access both collections at once.
		long flowId = installedFlowPath.flowId().value();
		Collection<PacketToPush> packets = waitingPackets.removeAll(flowId);
		
		//remove pending flows entry
		Path pathToRemove = new Path(installedFlowPath.dataPath().srcPort(),
				installedFlowPath.dataPath().dstPort());
		pendingFlows.remove(pathToRemove);
		
		for (PacketToPush packet : packets) {
			IOFSwitch sw = floodlightProvider.getSwitches().get(packet.dpid);
			
			OFPacketOut po = packet.packet;
			short outPort = 
					installedFlowPath.flowEntries().get(0).outPort().value();
			po.getActions().add(new OFActionOutput(outPort));
			po.setActionsLength((short)
					(po.getActionsLength() + OFActionOutput.MINIMUM_LENGTH));
			po.setLengthU(po.getLengthU() + OFActionOutput.MINIMUM_LENGTH);
			
			try {
				sw.write(packet.packet, null);
				sw.flush();
			} catch (IOException e) {
				log.error("Error writing packet out to switch {}:", 
						sw.getId(), e);
			}
		}
	}
}
