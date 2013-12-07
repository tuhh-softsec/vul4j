package net.onrc.onos.ofcontroller.forwarding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
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

public class Forwarding implements IOFMessageListener {
	private final static Logger log = LoggerFactory.getLogger(Forwarding.class);

	private IFloodlightProviderService floodlightProvider;
	private IFlowService flowService;
	private IDatagridService datagridService;
	
	private IDeviceStorage deviceStorage;
	private TopologyManager topologyService;
	
	public Forwarding() {
		
	}
	
	public void init(IFloodlightProviderService floodlightProvider, 
			IFlowService flowService, IDatagridService datagridService) {
		this.floodlightProvider = floodlightProvider;
		this.flowService = flowService;
		this.datagridService = datagridService;
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("","");
		topologyService = new TopologyManager();
		topologyService.init("","");
	}
	
	public void startUp() {
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
		String destinationMac = HexString.toHexString(eth.getDestinationMACAddress()); 
		
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
		
		if (flowExists(srcSwitchPort, srcMacAddress, 
				dstSwitchPort, dstMacAddress)) {
			log.debug("Not adding flow because it already exists");
			
			// TODO check reverse flow as well
			
			DataPath shortestPath = 
					topologyService.getDatabaseShortestPath(srcSwitchPort, dstSwitchPort);
			
			if (shortestPath == null || shortestPath.flowEntries().isEmpty()) {
				log.warn("No path found between {} and {} - not handling packet",
						srcSwitchPort, dstSwitchPort);
				return;
			}
			
			Port outPort = shortestPath.flowEntries().get(0).outPort();
			forwardPacket(pi, sw, outPort.value());
			return;
		}
		
		// Calculate a shortest path before pushing flow mods.
		// This will be used later by the packet-out processing, but it uses
		// the database so will be slow, and we should do it before flow mods.
		DataPath shortestPath = 
				topologyService.getDatabaseShortestPath(srcSwitchPort, dstSwitchPort);
		
		if (shortestPath == null || shortestPath.flowEntries().isEmpty()) {
			log.warn("No path found between {} and {} - not handling packet",
					srcSwitchPort, dstSwitchPort);
			return;
		}
		
		log.debug("Adding new flow between {} at {} and {} at {}",
				new Object[]{srcMacAddress, srcSwitchPort, dstMacAddress, dstSwitchPort});
		
		
		DataPath dataPath = new DataPath();
		dataPath.setSrcPort(srcSwitchPort);
		dataPath.setDstPort(dstSwitchPort);
		
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
		flowPath.setDataPath(dataPath);
			
		FlowId flowId = flowService.addFlow(flowPath);
		//flowService.addFlow(flowPath, flowId);
		
		
		DataPath reverseDataPath = new DataPath();
		// Reverse the ports for the reverse path
		reverseDataPath.setSrcPort(dstSwitchPort);
		reverseDataPath.setDstPort(srcSwitchPort);
		
		//FlowId reverseFlowId = new FlowId(flowService.getNextFlowEntryId());
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
		//flowService.addFlow(reverseFlowPath, reverseFlowId);
		FlowId reverseFlowId = flowService.addFlow(reverseFlowPath);
		
		Port outPort = shortestPath.flowEntries().get(0).outPort();
		forwardPacket(pi, sw, outPort.value());
	}
	
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

	private void forwardPacket(OFPacketIn pi, IOFSwitch sw, short port) {
		List<OFAction> actions = new ArrayList<OFAction>(1);
		actions.add(new OFActionOutput(port));
		
		OFPacketOut po = new OFPacketOut();
		po.setInPort(OFPort.OFPP_NONE)
		.setInPort(pi.getInPort())
		.setActions(actions)
		.setActionsLength((short)OFActionOutput.MINIMUM_LENGTH)
		.setLengthU(OFPacketOut.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH);
		
		if (sw.getBuffers() == 0) {
			po.setBufferId(OFPacketOut.BUFFER_ID_NONE)
			.setPacketData(pi.getPacketData())
			.setLengthU(po.getLengthU() + po.getPacketData().length);
		}
		else {
			po.setBufferId(pi.getBufferId());
		}
		
		try {
			sw.write(po, null);
			sw.flush();
		} catch (IOException e) {
			log.error("Error writing packet out to switch: {}", e);
		}
	}
}
