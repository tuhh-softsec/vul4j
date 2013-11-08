package net.onrc.onos.ofcontroller.forwarding;

import java.util.Iterator;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.util.MACAddress;
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
import org.openflow.protocol.OFType;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forwarding implements IOFMessageListener {
	private final static Logger log = LoggerFactory.getLogger(Forwarding.class);

	private IFloodlightProviderService floodlightProvider;
	private IFlowService flowService;
	
	private IDeviceStorage deviceStorage;
	private TopologyManager topologyService;
	
	// TODO Flow IDs should be globally managed
	private long currentId = 1;
	
	public Forwarding() {
		
	}
	
	public void init(IFloodlightProviderService floodlightProvider, 
			IFlowService flowService) {
		this.floodlightProvider = floodlightProvider;
		this.flowService = flowService;
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("");
		topologyService = new TopologyManager();
		topologyService.init("");
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
		
		SwitchPort srcSwitchPort = new SwitchPort(
				new Dpid(sw.getId()), new Port(pi.getInPort())); 
		SwitchPort dstSwitchPort = new SwitchPort(
				new Dpid(destinationDpid), new Port(destinationPort)); 
		DataPath shortestPath = 
				topologyService.getDatabaseShortestPath(srcSwitchPort, dstSwitchPort);
		
		if (shortestPath == null) {
			log.debug("Shortest path not found between {} and {}", 
					srcSwitchPort, dstSwitchPort);
			return;
		}
		
		MACAddress srcMacAddress = MACAddress.valueOf(eth.getSourceMACAddress());
		MACAddress dstMacAddress = MACAddress.valueOf(eth.getDestinationMACAddress());
		
		DataPath dataPath = new DataPath();
		dataPath.setSrcPort(srcSwitchPort);
		dataPath.setDstPort(dstSwitchPort);
		
		FlowId flowId = new FlowId(currentId++); //dummy flow ID
		FlowPath flowPath = new FlowPath();
		flowPath.setFlowId(flowId);
		flowPath.setInstallerId(new CallerId("Forwarding"));
		flowPath.setFlowPathType(FlowPathType.FP_TYPE_SHORTEST_PATH);
		flowPath.setFlowPathUserState(FlowPathUserState.FP_USER_ADD);
		flowPath.setFlowEntryMatch(new FlowEntryMatch());
		flowPath.flowEntryMatch().enableSrcMac(srcMacAddress);
		flowPath.flowEntryMatch().enableDstMac(dstMacAddress);
		// For now just forward IPv4 packets. This prevents accidentally
		// other stuff like ARP.
		flowPath.flowEntryMatch().enableEthernetFrameType(Ethernet.TYPE_IPv4);
		flowPath.setDataPath(dataPath);
		
		flowService.addFlow(flowPath, flowId);
	}

}
