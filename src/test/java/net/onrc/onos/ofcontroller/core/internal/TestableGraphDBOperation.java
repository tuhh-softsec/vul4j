package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.easymock.EasyMock;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;

import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.graph.IDBConnection;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

/**
 * Mock class of GraphDBOperation which provides additional setter to construct a graph for test.
 * This object simply caches parameters set up by override interfaces and reflect them when commit().
 * *ForTest() methods are exempt from cache, parameters through those methods are reflected soon.
 * @author Naoki Shiota
 *
 */
public class TestableGraphDBOperation extends GraphDBOperation {
	protected static Logger log = LoggerFactory.getLogger(TestableGraphDBOperation.class);

	protected List<TestSwitchObject> switches;
	protected List<TestPortObject> ports;
	protected List<TestDeviceObject> devices;
	protected List<TestFlowPath> paths;
	protected List<TestFlowEntry> entries;

	protected List<TestSwitchObject> switchesToAdd;
	protected List<TestPortObject> portsToAdd;
	protected List<TestDeviceObject> devicesToAdd;
	protected List<TestFlowPath> pathsToAdd;
	protected List<TestFlowEntry> entriesToAdd;

	protected List<TestSwitchObject> switchesToRemove;
	protected List<TestPortObject> portsToRemove;
	protected List<TestDeviceObject> devicesToRemove;
	protected List<TestFlowPath> pathsToRemove;
	protected List<TestFlowEntry> entriesToRemove;

	// Testable implementations of INetMapTopologyObject interfaces

	public static class TestSwitchObject implements ISwitchObject {
		private String state,type,dpid;
		private List<IPortObject> ports;
		private List<IDeviceObject> devices;
		private List<IFlowEntry> entries;

		private String stateToUpdate, typeToUpdate, dpidToUpdate;
		private List<IPortObject> portsToAdd;
		private List<IPortObject> portsToRemove;

		public TestSwitchObject() {
			type = "switch";
			
			ports = new ArrayList<IPortObject>();
			portsToAdd = new ArrayList<IPortObject>();
			portsToRemove = new ArrayList<IPortObject>();
			devices = new ArrayList<IDeviceObject>();
			entries = new ArrayList<IFlowEntry>();
			
			clearUncommitedData();
		}
		
		public void commit() {
			for(IPortObject port : portsToAdd) {
				ports.add(port);
			}
			for(IPortObject port : portsToRemove) {
				ports.remove(port);
			}
			if(stateToUpdate != null) { state = stateToUpdate; }
			if(typeToUpdate != null) { type = typeToUpdate; }
			if(dpidToUpdate != null) { dpid = dpidToUpdate; }

			clearUncommitedData();
		}
		
		public void rollback() {
			clearUncommitedData();
		}
		
		public void clearUncommitedData() {
			portsToAdd.clear();
			portsToRemove.clear();
			stateToUpdate = typeToUpdate = dpidToUpdate = null;
		}
		
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void setDpidForTest(String dpid) { this.dpid = dpid; }
		public void addPortForTest(TestPortObject port) { ports.add(port);  }
		public void addDeviceForTest(TestDeviceObject dev) { devices.add(dev); }
		public void addEntryForTest(TestFlowEntry entry) { entries.add(entry); }
		
		@Override
		public String getState() { return state; }
	
		@Override
		public void setState(String state) { this.stateToUpdate = state; }
	
		@Override
		public String getType() { return type ; }
	
		@Override
		public void setType(String type) { this.typeToUpdate = type; }
	
		// Not support for test
		@Override
		public Vertex asVertex() { return null; }
	
		@Override
		public String getDPID() { return dpid; }
	
		@Override
		public void setDPID(String dpid) { this.dpidToUpdate = dpid; }
	
		@Override
		public Iterable<IPortObject> getPorts() { return ports; }
	
		@Override
		public IPortObject getPort(@GremlinParam("port_num") short port_num) {
			for(IPortObject port : ports) {
				if(port.getNumber() == port_num) {
					return port;
				}
			}
			return null;
		}
	
		@Override
		public void addPort(IPortObject port) { portsToAdd.add(port); }
	
		@Override
		public void removePort(IPortObject port) { portsToRemove.add(port); }
	
		@Override
		public Iterable<IDeviceObject> getDevices() { return devices; }
	
		@Override
		public Iterable<IFlowEntry> getFlowEntries() { return entries; }
	}
	
	public static class TestPortObject implements IPortObject {
		private String state,type,desc;
		private Short number;
		private Integer port_state;
		private ISwitchObject sw;
		
		private List<IPortObject> linkedPorts;
		private List<IDeviceObject> devices;
		private List<IFlowEntry> inflows,outflows;
		
		private String stateToUpdate,typeToUpdate,descToUpdate;
		private Short numberToUpdate;
		private Integer port_stateToUpdate;
		
		private List<IPortObject> linkedPortsToAdd;
		private List<IPortObject> linkedPortsToRemove;
		private List<IDeviceObject> devicesToAdd;
		private List<IDeviceObject> devicesToRemove;
		

		public TestPortObject() {
			type = "port";

			linkedPorts = new ArrayList<IPortObject>();
			linkedPortsToAdd = new ArrayList<IPortObject>();
			linkedPortsToRemove = new ArrayList<IPortObject>();
			devices = new ArrayList<IDeviceObject>();
			devicesToAdd = new ArrayList<IDeviceObject>();
			devicesToRemove = new ArrayList<IDeviceObject>();
			inflows = new ArrayList<IFlowEntry>();
			outflows = new ArrayList<IFlowEntry>();
			
			clearUncommitedData();
		}
		
		public void commit() {
			for(IPortObject port : linkedPortsToAdd) { linkedPorts.add(port); }
			for(IPortObject port : linkedPortsToRemove) { linkedPorts.remove(port); }
			for(IDeviceObject dev : devicesToAdd) { devices.add(dev); }
			for(IDeviceObject dev : devicesToRemove) { devices.remove(dev); }
			
			if(stateToUpdate != null) { state = stateToUpdate; }
			if(typeToUpdate != null) { type = typeToUpdate; }
			if(descToUpdate != null) { desc = descToUpdate; }
			if(numberToUpdate != null) { number = numberToUpdate; }
			if(port_stateToUpdate != null) { port_state = port_stateToUpdate; }
			
			clearUncommitedData();
		}
		
		public void rollback() {
			clearUncommitedData();
		}
		
		public void clearUncommitedData() {
			linkedPortsToAdd.clear();
			linkedPortsToRemove.clear();
			devicesToAdd.clear();
			devicesToRemove.clear();
			stateToUpdate = typeToUpdate = descToUpdate = null;
			port_stateToUpdate = null;
			numberToUpdate = null;
		}
		
		// Setter methods for test
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void setDescForTest(String desc) { this.desc = desc; }
		public void setNumberForTest(Short number) { this.number = number; }
		public void setPortStateForTest(Integer state) { this.port_state = state; }
		public void setSwitchForTest(ISwitchObject sw) { this.sw = sw; }
		public void addLinkedPortForTest(TestPortObject port) { this.linkedPorts.add(port); }
		public void addInflowForTest(TestFlowEntry entry) { inflows.add(entry); }
		public void addOutflowForTest(TestFlowEntry entry) { outflows.add(entry); }
		
		// Override methods for mock IPortObject
		@Override
		public String getState() { return state; }

		@Override
		public void setState(String state) { this.stateToUpdate = state; }

		@Override
		public String getType() { return type; }

		@Override
		public void setType(String type) { this.typeToUpdate = type; }

		// not support for test
		@Override
		public Vertex asVertex() {
			return null;
		}

		@Override
		public Short getNumber() { return number; }

		@Override
		public void setNumber(Short n) { this.numberToUpdate = n; }

		@Override
		public String getDesc() { return desc; }

		@Override
		public void setDesc(String s) { this.descToUpdate = s; }

		@Override
		public Integer getPortState() { return port_state; }

		@Override
		public void setPortState(Integer s) { this.port_stateToUpdate = s; }

		@Override
		public ISwitchObject getSwitch() { return sw; }

		@Override
		public Iterable<IDeviceObject> getDevices() { return devices; }

		@Override
		public void setDevice(IDeviceObject device) { devicesToAdd.add(device); }

		@Override
		public void removeDevice(IDeviceObject device) { devicesToRemove.add(device); }

		@Override
		public Iterable<IFlowEntry> getInFlowEntries() { return inflows; }

		@Override
		public Iterable<IFlowEntry> getOutFlowEntries() { return outflows; }

		@Override
		public Iterable<IPortObject> getLinkedPorts() { return linkedPorts; }

		@Override
		public void removeLink(IPortObject dest_port) { linkedPortsToRemove.add(dest_port); }

		@Override
		public void setLinkPort(IPortObject dest_port) { linkedPortsToAdd.add(dest_port); }

		@Override
		@JsonIgnore
		@Property("port_id")
		public void setPortId(String id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		@JsonIgnore
		@Property("port_id")
		public String getPortId() {
			// TODO Auto-generated method stub
			return null;
		}
	}
		
	public static class TestDeviceObject implements IDeviceObject {
		private String state,type,mac,ipaddr;
		private List<IPortObject> ports;
		private List<ISwitchObject> switches;
		
		private String stateToUpdate,typeToUpdate,macToUpdate,ipaddrToUpdate;
		private List<IPortObject> portsToAdd;
		private List<IPortObject> portsToRemove;
	
		public TestDeviceObject() {
			type = "device";
			
			ports = new ArrayList<IPortObject>();
			portsToAdd = new ArrayList<IPortObject>();
			portsToRemove = new ArrayList<IPortObject>();
			switches = new ArrayList<ISwitchObject>();
			
			clearUncommitedData();
		}
		
		public void commit() {
			for(IPortObject port : portsToAdd) {
				ports.add(port);
			}
			for(IPortObject port : portsToRemove) {
				ports.remove(port);
			}
			
			if(stateToUpdate != null) { state = stateToUpdate; }
			if(typeToUpdate != null) { type = typeToUpdate; }
			if(macToUpdate != null) { mac = macToUpdate; }
			if(ipaddrToUpdate != null) { ipaddr = ipaddrToUpdate; }
			
			clearUncommitedData();
		}
		
		public void rollback() {
			clearUncommitedData();
		}
		
		public void clearUncommitedData() {
			ports.clear();
			portsToAdd.clear();
			portsToRemove.clear();
			
			stateToUpdate = typeToUpdate = macToUpdate = ipaddrToUpdate = null;
		}
		
		// Setter methods for test
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void setMacForTest(String mac) { this.mac = mac; }
		public void setIpaddrForTest(String ipaddr) { this.ipaddr = ipaddr; }
		public void addSwitchForTest(ISwitchObject sw) { switches.add(sw); }
		public void addPortForTest(IPortObject port) { ports.add(port); }
		
		
		// Override methods
		@Override
		public String getState() { return state; }
	
		@Override
		public void setState(String state) { stateToUpdate = state; }
	
		@Override
		public String getType() { return type; }
	
		@Override
		public void setType(String type) { typeToUpdate = type; }
	
		@Override
		public Vertex asVertex() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public String getMACAddress() { return mac; }
	
		@Override
		public void setMACAddress(String macaddr) { macToUpdate = macaddr; }
	
		@Override
		public String getIPAddress() { return ipaddr; }
	
		@Override
		public void setIPAddress(String ipaddr) { ipaddrToUpdate = ipaddr; }
	
		@Override
		public Iterable<IPortObject> getAttachedPorts() {
			return ports; }
	
		@Override
		public void setHostPort(IPortObject port) { portsToAdd.add(port); }
	
		@Override
		public void removeHostPort(IPortObject port) { portsToRemove.add(port); }
	
		@Override
		public Iterable<ISwitchObject> getSwitch() { return switches; }
	}
	
	public static class TestFlowPath implements IFlowPath {
		private String state,type,flowId,installerId,srcSw,dstSw;
		private Long flowPathFlags;
		private String dataPathSummary,userState;
		private Short srcPort,dstPort;
		private String matchSrcMac,matchDstMac;
		private Short matchEthernetFrameType;
		private Short matchVlanId;
		private Byte matchVlanPriority;
		private String matchSrcIpaddr,matchDstIpaddr;
		private Byte matchIpProto, matchIpToS;
		private Short matchSrcTcpUdpPort, matchDstTcpUdpPort;
		
		private List<IFlowEntry> entries;
		private List<ISwitchObject> switches;

		private String stateToUpdate,typeToUpdate,flowIdToUpdate,installerIdToUpdate,srcSwToUpdate,dstSwToUpdate;
		private Long flowPathFlagsToUpdate;
		private String dataPathSummaryToUpdate,userStateToUpdate;
		private Short srcPortToUpdate,dstPortToUpdate;
		private String matchSrcMacToUpdate,matchDstMacToUpdate;
		private Short matchEthernetFrameTypeToUpdate;
		private Short matchVlanIdToUpdate;
		private Byte matchVlanPriorityToUpdate;
		private String matchSrcIpaddrToUpdate,matchDstIpaddrToUpdate;
		private Byte matchIpProtoToUpdate, matchIpToSToUpdate;
		private Short matchSrcTcpUdpPortToUpdate, matchDstTcpUdpPortToUpdate;

		private List<IFlowEntry> flowsToAdd;
		private List<IFlowEntry> flowsToRemove;

		public TestFlowPath() {
			type = "flow";
			
			entries = new ArrayList<IFlowEntry>();
			flowsToAdd = new ArrayList<IFlowEntry>();
			flowsToRemove = new ArrayList<IFlowEntry>();
			
			switches = new ArrayList<ISwitchObject>();
			
			clear();
		}
		
		public void commit() {
			for(IFlowEntry flow : flowsToAdd) {
				entries.add(flow);
			}
			for(IFlowEntry flow : flowsToRemove) {
				entries.remove(flow);
			}
			if(stateToUpdate != null) { state = stateToUpdate; }
			if(typeToUpdate != null) { type = typeToUpdate; }
			if(flowIdToUpdate != null) { flowId = flowIdToUpdate; }
			if(installerIdToUpdate != null) { installerId = installerIdToUpdate; }
			if(flowPathFlagsToUpdate != null) { flowPathFlags = flowPathFlagsToUpdate; }
			if(srcSwToUpdate != null) { srcSw = srcSwToUpdate; }
			if(dstSwToUpdate != null) { dstSw = dstSwToUpdate; }
			if(dataPathSummaryToUpdate != null) { dataPathSummary = dataPathSummaryToUpdate; }
			if(userStateToUpdate != null) { userState = userStateToUpdate; }
			if(srcPortToUpdate != null) { srcPort = srcPortToUpdate; }
			if(dstPortToUpdate != null) { dstPort = dstPortToUpdate; }
			if(matchSrcMacToUpdate != null) { matchSrcMac = matchSrcMacToUpdate; }
			if(matchDstMacToUpdate != null) { matchDstMac = matchDstMacToUpdate; }
			if(matchEthernetFrameTypeToUpdate != null) { matchEthernetFrameType = matchEthernetFrameTypeToUpdate; }
			if(matchVlanIdToUpdate != null) { matchVlanId = matchVlanIdToUpdate; }
			if(matchVlanPriorityToUpdate != null) { matchVlanPriority = matchVlanPriorityToUpdate; }
			if(matchSrcIpaddrToUpdate != null) { matchSrcIpaddr = matchSrcIpaddrToUpdate; }
			if(matchDstIpaddrToUpdate != null) { matchDstIpaddr = matchDstIpaddrToUpdate; }
			if(matchIpProtoToUpdate != null) { matchIpProto = matchIpProtoToUpdate; }
			if(matchIpToSToUpdate != null) { matchIpToS = matchIpToSToUpdate; }
			if(matchSrcTcpUdpPortToUpdate != null) { matchSrcTcpUdpPort = matchSrcTcpUdpPortToUpdate; }
			if(matchDstTcpUdpPortToUpdate != null) { matchDstTcpUdpPort = matchDstTcpUdpPortToUpdate; }
		}
		
		public void rollback() {
			clear();
		}
		
		public void clear() {
			flowsToAdd.clear();
			flowsToRemove.clear();
			
			stateToUpdate = typeToUpdate = flowIdToUpdate = installerIdToUpdate = null;
			flowPathFlagsToUpdate = null;
			srcSwToUpdate = dstSwToUpdate = dataPathSummaryToUpdate = userStateToUpdate = null;
			srcPortToUpdate = dstPortToUpdate = null;
			matchSrcMacToUpdate = matchDstMacToUpdate = null;
			matchEthernetFrameTypeToUpdate = null;
			matchVlanIdToUpdate = null;
			matchVlanPriorityToUpdate = null;
			matchSrcIpaddrToUpdate = matchDstIpaddrToUpdate = null;
			matchIpProtoToUpdate = matchIpToSToUpdate = null;
			matchSrcTcpUdpPortToUpdate = matchDstTcpUdpPortToUpdate = null;
		}
		
		// Setter methods for test
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void setFlowIdForTest(String flowId) { this.flowId = flowId; }
		public void setInstallerIdForTest(String installerId) { this.installerId = installerId; }
		public void setFlowPathFlagsForTest(Long flowPathFlags) { this.flowPathFlags = flowPathFlags; }
		public void setSrcSwForTest(String srcSw) { this.srcSw = srcSw; }
		public void setDstSwForTest(String dstSw) { this.dstSw = dstSw; }
		public void setDataPathSummaryForTest(String dataPathSummary) { this.dataPathSummary = dataPathSummary; }
		public void setUserStateForTest(String userState) { this.userState = userState; }
		public void setSrcPortForTest(Short srcPort) { this.srcPort = srcPort; }
		public void setDstPortForTest(Short dstPort) { this.dstPort = dstPort; }
		public void setMatchSrcMacForTest(String matchSrcMac) { this.matchSrcMac = matchSrcMac; }
		public void setMatchDstMacForTest(String matchDstMac) { this.matchDstMac = matchDstMac; }
		public void setMatchEthernetFrameTypeForTest(Short matchEthernetFrameType) { this.matchEthernetFrameType = matchEthernetFrameType; }
		public void setMatchVlanIdForTest(Short matchVlanId) { this.matchVlanId = matchVlanId; }
		public void setMatchVlanPriorityForTest(Byte matchVlanPriority) { this.matchVlanPriority = matchVlanPriority; }
		public void setMatchSrcIpaddrForTest(String matchSrcIpaddr) { this.matchSrcIpaddr = matchSrcIpaddr; }
		public void setMatchDstIpaddrForTest(String matchDstIpaddr) { this.matchDstIpaddr = matchDstIpaddr; }
		public void setMatchIpProtoForTest(Byte matchIpProto) { this.matchIpProto = matchIpProto; }
		public void setMatchIpToSForTest(Byte matchIpToS) { this.matchIpToS = matchIpToS; }
		public void setMatchSrcTcpUdpPortForTest(Short matchSrcTcpUdpPort) { this.matchSrcTcpUdpPort = matchSrcTcpUdpPort; }
		public void setMatchDstTcpUdpPortForTest(Short matchDstTcpUdpPort) { this.matchDstTcpUdpPort = matchDstTcpUdpPort; }
		public void addFlowEntryForTest(IFlowEntry entry) { entries.add(entry); }
		public void addSwitchForTest(ISwitchObject sw) { switches.add(sw); }

		@Override
		public String getState() { return state; }

		@Override
		public void setState(String state) { stateToUpdate = state; }

		@Override
		public String getType() { return type; }

		@Override
		public void setType(String type) { typeToUpdate = type; }

		@Override
		public Vertex asVertex() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getFlowId() { return flowId; }

		@Override
		public void setFlowId(String flowId) { flowIdToUpdate = flowId; }

		@Override
		public String getInstallerId() { return installerId; }

		@Override
		public void setInstallerId(String installerId) { installerIdToUpdate = installerId; }

		@Override
		public Long getFlowPathFlags() { return flowPathFlags; }

		@Override
		public void setFlowPathFlags(Long flowPathFlags) { flowPathFlagsToUpdate = flowPathFlags; }

		@Override
		public String getSrcSwitch() { return srcSw; }

		@Override
		public void setSrcSwitch(String srcSwitch) { srcSwToUpdate = srcSwitch; }

		@Override
		public Short getSrcPort() { return srcPort; }

		@Override
		public void setSrcPort(Short srcPort) { srcPortToUpdate = srcPort; }

		@Override
		public String getDstSwitch() { return dstSw; }

		@Override
		public void setDstSwitch(String dstSwitch) { dstSwToUpdate = dstSwitch; }

		@Override
		public Short getDstPort() { return dstPort; }

		@Override
		public void setDstPort(Short dstPort) { dstPortToUpdate = dstPort; }

		@Override
		public String getDataPathSummary() { return dataPathSummary; }

		@Override
		public void setDataPathSummary(String dataPathSummary) { dataPathSummaryToUpdate = dataPathSummary; }

		@Override
		public Iterable<IFlowEntry> getFlowEntries() { return entries; }

		@Override
		public void addFlowEntry(IFlowEntry flowEntry) {
			if(! entries.contains(flowEntry)) {
				flowsToAdd.add(flowEntry);
			}
		}

		@Override
		public void removeFlowEntry(IFlowEntry flowEntry) {
			if(entries.contains(flowEntry)) {
				flowsToAdd.add(flowEntry);
			}
		}

		@Override
		public String getMatchSrcMac() { return matchSrcMac; }

		@Override
		public void setMatchSrcMac(String matchSrcMac) { matchSrcMacToUpdate = matchSrcMac; }

		@Override
		public String getMatchDstMac() { return matchDstMac; }

		@Override
		public void setMatchDstMac(String matchDstMac) { matchDstMacToUpdate = matchDstMac; }

		@Override
		public Short getMatchEthernetFrameType() { return matchEthernetFrameType; }

		@Override
		public void setMatchEthernetFrameType(Short matchEthernetFrameType) {
			matchEthernetFrameTypeToUpdate = matchEthernetFrameType; }

		@Override
		public Short getMatchVlanId() { return matchVlanId; }

		@Override
		public void setMatchVlanId(Short matchVlanId) {
			matchVlanIdToUpdate = matchVlanId; }

		@Override
		public Byte getMatchVlanPriority() { return matchVlanPriority; }

		@Override
		public void setMatchVlanPriority(Byte matchVlanPriority) {
			matchVlanPriorityToUpdate = matchVlanPriority; }

		@Override
		public String getMatchSrcIPv4Net() { return matchSrcIpaddr; }

		@Override
		public void setMatchSrcIPv4Net(String matchSrcIPv4Net) {
			matchSrcIpaddrToUpdate = matchSrcIPv4Net; }

		@Override
		public String getMatchDstIPv4Net() { return matchDstIpaddr; }

		@Override
		public void setMatchDstIPv4Net(String matchDstIPv4Net) {
			matchDstIpaddrToUpdate = matchDstIPv4Net; }

		@Override
		public Byte getMatchIpProto() { return matchIpProto; }

		@Override
		public void setMatchIpProto(Byte matchIpProto) {
			matchIpProtoToUpdate = matchIpProto; }

		@Override
		public Byte getMatchIpToS() { return matchIpToS; }

		@Override
		public void setMatchIpToS(Byte matchIpToS) {
			matchIpToSToUpdate = matchIpToS; }

		@Override
		public Short getMatchSrcTcpUdpPort() { return matchSrcTcpUdpPort; }

		@Override
		public void setMatchSrcTcpUdpPort(Short matchSrcTcpUdpPort) {
			matchSrcTcpUdpPortToUpdate = matchSrcTcpUdpPort; }

		@Override
		public Short getMatchDstTcpUdpPort() { return matchDstTcpUdpPort; }

		@Override
		public void setMatchDstTcpUdpPort(Short matchDstTcpUdpPort) {
			matchDstTcpUdpPortToUpdate = matchDstTcpUdpPort; }

		@Override
		public Iterable<ISwitchObject> getSwitches() { return switches; }

		@Override
		public String getUserState() { return userState; }

		@Override
		public void setUserState(String userState) { userStateToUpdate = userState; }
	}

	public static class TestFlowEntry implements IFlowEntry {
		private String state,type,entryId,dpid,userState,switchState,errorStateType,errorStateCode;
		private Short matchInPort;
		private String matchSrcMac,matchDstMac;
		private Short matchEtherFrameType;
		private Short matchVlanId;
		private Byte matchVlanPriority;
		private String matchSrcIpaddr,matchDstIpaddr;
		private Byte matchIpProto, matchIpToS;
		private Short matchSrcTcpUdpPort, matchDstTcpUdpPort;
		private Short actionOutputPort;
		
		private IFlowPath flowPath;
		private ISwitchObject sw;
		private IPortObject inport,outport;
	
		private String stateToUpdate,typeToUpdate,entryIdToUpdate,dpidToUpdate,
			userStateToUpdate,switchStateToUpdate,errorStateTypeToUpdate,errorStateCodeToUpdate;
		private Short matchInPortToUpdate;
		private String matchSrcMacToUpdate,matchDstMacToUpdate;
		private Short matchEtherFrameTypeToUpdate;
		private Short matchVlanIdToUpdate;
		private Byte matchVlanPriorityToUpdate;
		private String matchSrcIpaddrToUpdate,matchDstIpaddrToUpdate;
		private Byte matchIpProtoToUpdate, matchIpToSToUpdate;
		private Short matchSrcTcpUdpPortToUpdate, matchDstTcpUdpPortToUpdate;
		private Short actionOutputPortToUpdate;
	
		private IFlowPath flowPathToUpdate;
		private ISwitchObject swToUpdate;
		private IPortObject inportToUpdate,outportToUpdate;
	
		public TestFlowEntry() {
			type = "flow_entry";
			
			clearUncommitedData();
		}
		
		public void commit() {
			if(stateToUpdate != null) { state = stateToUpdate; }
			if(typeToUpdate != null) { type = typeToUpdate; }
			if(entryIdToUpdate != null) { entryId = entryIdToUpdate; }
			if(dpidToUpdate != null) { dpid = dpidToUpdate; }
			if(userStateToUpdate != null) { userState = userStateToUpdate; }
			if(switchStateToUpdate != null) { switchState = switchStateToUpdate; }
			if(errorStateTypeToUpdate != null) { errorStateType = errorStateTypeToUpdate; }
			if(errorStateCodeToUpdate != null) { errorStateCode = errorStateCodeToUpdate; }
			if(matchInPortToUpdate != null) { matchInPort = matchInPortToUpdate; }
			if(matchSrcMacToUpdate != null) { matchSrcMac = matchSrcMacToUpdate; }
			if(matchDstMacToUpdate != null) { matchDstMac = matchDstMacToUpdate; }
			if(matchEtherFrameTypeToUpdate != null) { matchEtherFrameType = matchEtherFrameTypeToUpdate; }
			if(matchVlanIdToUpdate != null) { matchVlanId = matchVlanIdToUpdate; }
			if(matchVlanPriorityToUpdate != null) { matchVlanPriority = matchVlanPriorityToUpdate; }
			if(matchSrcIpaddrToUpdate != null) { matchSrcIpaddr = matchSrcIpaddrToUpdate; }
			if(matchDstIpaddrToUpdate != null) { matchDstIpaddr = matchDstIpaddrToUpdate; }
			if(matchIpProtoToUpdate != null) { matchIpProto = matchIpProtoToUpdate; }
			if(matchIpToSToUpdate != null) { matchIpToS = matchIpToSToUpdate; }
			if(matchSrcTcpUdpPortToUpdate != null) { matchSrcTcpUdpPort = matchSrcTcpUdpPortToUpdate; }
			if(matchDstTcpUdpPortToUpdate != null) { matchDstTcpUdpPort = matchDstTcpUdpPortToUpdate; }
			if(actionOutputPortToUpdate != null) { actionOutputPort = actionOutputPortToUpdate; }
			
			if(flowPathToUpdate != null) { flowPath = flowPathToUpdate; }
			if(swToUpdate != null) { sw = swToUpdate; }
			if(inportToUpdate != null) { inport = inportToUpdate; }
			if(outportToUpdate != null) { outport = outportToUpdate; }
	
			clearUncommitedData();
		}
		
		public void rollback() {
			clearUncommitedData();
		}
		
		public void clearUncommitedData() {
			stateToUpdate = typeToUpdate = entryIdToUpdate = dpidToUpdate = null;
			userStateToUpdate = switchStateToUpdate = errorStateTypeToUpdate = errorStateCodeToUpdate = null;
			matchInPortToUpdate = null;
			matchSrcMacToUpdate = matchDstMacToUpdate = null;
			matchEtherFrameTypeToUpdate = null;
			matchVlanIdToUpdate = null;
			matchVlanPriorityToUpdate = null;
			matchSrcIpaddrToUpdate = matchDstIpaddrToUpdate = null;
			matchIpProtoToUpdate = matchIpToSToUpdate = null;
			matchSrcTcpUdpPortToUpdate = matchDstTcpUdpPortToUpdate = null;
			actionOutputPortToUpdate = null;
			flowPathToUpdate = null;
			swToUpdate = null;
			inportToUpdate = outportToUpdate = null;
		}
		
		// Setter methods for test
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void setEntryIdForTest(String entryId) { this.entryId = entryId; }
		public void setDpidForTest(String dpid) { this.dpid = dpid; }
		public void setUserStateForTest(String userState) { this.userState = userState; }
		public void setSwitchStateForTest(String switchState) { this.switchState = switchState; }
		public void setErrorStateTypeForTest(String errorStateType) { this.errorStateType = errorStateType; }
		public void setErrorStateCodeForTest(String errorStateCode) { this.errorStateCode = errorStateCode; }
		public void setMatchInPortForTest(Short matchInPort) { this.matchInPort = matchInPort; }
		public void setMatchSrcMacForTest(String matchSrcMac) { this.matchSrcMac = matchSrcMac; }
		public void setMatchDstMacForTest(String matchDstMac) { this.matchDstMac = matchDstMac; }
		public void setMatchEtherFrameTypeForTest(Short matchEtherFrameType) { this.matchEtherFrameType = matchEtherFrameType; }
		public void setMatchVlanIdForTest(Short matchVlanId) { this.matchVlanId = matchVlanId; }
		public void setMatchVlanPriorityForTest(Byte matchVlanPriority) { this.matchVlanPriority = matchVlanPriority; }
		public void setMatchSrcIpaddrForTest(String matchSrcIpaddr) { this.matchSrcIpaddr = matchSrcIpaddr; }
		public void setMatchDstIpaddrForTest(String matchDstIpaddr) { this.matchDstIpaddr = matchDstIpaddr; }
		public void setMatchIpProtoForTest(Byte matchIpProto) { this.matchIpProto = matchIpProto; }
		public void setMatchIpToSForTest(Byte matchIpToS) { this.matchIpToS = matchIpToS; }
		public void setMatchSrcTcpUdpPortForTest(Short matchSrcTcpUdpPort) { this.matchSrcTcpUdpPort = matchSrcTcpUdpPort; }
		public void setMatchDstTcpUdpPortForTest(Short matchDstTcpUdpPort) { this.matchDstTcpUdpPort = matchDstTcpUdpPort; }
		public void setActionOutputPortForTest(Short actionOutputPort) { this.actionOutputPort = actionOutputPort; }
		public void setFlowPathForTest(IFlowPath flowPath) { this.flowPath = flowPath; }
		public void setSwitchForTest(ISwitchObject sw) { this.sw = sw; }
		public void setInportForTest(IPortObject inport) { this.inport = inport; }
		public void setOutportForTest(IPortObject outport) { this.outport = outport; }
		
		@Override
		public String getState() { return state; }
	
		@Override
		public void setState(String state) { stateToUpdate = state; }
	
		@Override
		public String getType() { return type; }
	
		@Override
		public void setType(String type) { typeToUpdate = type; }
	
		@Override
		public Vertex asVertex() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public String getFlowEntryId() { return entryId; }
	
		@Override
		public void setFlowEntryId(String flowEntryId) { entryIdToUpdate = flowEntryId; }
	
		@Override
		public String getSwitchDpid() { return dpid; }
	
		@Override
		public void setSwitchDpid(String switchDpid) { dpidToUpdate = switchDpid; }
	
		@Override
		public String getUserState() { return userState; }
	
		@Override
		public void setUserState(String userState) { userStateToUpdate = userState; }
	
		@Override
		public String getSwitchState() { return switchState; }
	
		@Override
		public void setSwitchState(String switchState) { switchStateToUpdate = switchState; }
	
		@Override
		public String getErrorStateType() { return errorStateType; }
	
		@Override
		public void setErrorStateType(String errorStateType) { errorStateTypeToUpdate = errorStateType; }
	
		@Override
		public String getErrorStateCode() { return errorStateCode; }
	
		@Override
		public void setErrorStateCode(String errorStateCode) { errorStateCodeToUpdate = errorStateCode; }
	
		@Override
		public Short getMatchInPort() { return matchInPort; }
	
		@Override
		public void setMatchInPort(Short matchInPort) { matchInPortToUpdate = matchInPort; }
	
		@Override
		public String getMatchSrcMac() { return matchSrcMac; }
	
		@Override
		public void setMatchSrcMac(String matchSrcMac) { matchSrcMacToUpdate = matchSrcMac; }
	
		@Override
		public String getMatchDstMac() { return matchDstMac; }
	
		@Override
		public void setMatchDstMac(String matchDstMac) { matchDstMacToUpdate = matchDstMac; }

		@Override
		public Short getMatchEthernetFrameType() {return matchEtherFrameType; }
	
		@Override
		public void setMatchEthernetFrameType(Short matchEthernetFrameType) { matchEtherFrameTypeToUpdate = matchEthernetFrameType; }

		@Override
		public Short getMatchVlanId() {return matchVlanId; }
	
		@Override
		public void setMatchVlanId(Short matchVlanId) { matchVlanId = matchVlanId; }

		@Override
		public Byte getMatchVlanPriority() {return matchVlanPriority; }
	
		@Override
		public void setMatchVlanPriority(Byte matchVlanPriority) { matchVlanPriority = matchVlanPriority; }
		
		@Override
		public String getMatchSrcIPv4Net() { return matchSrcIpaddr; }
	
		@Override
		public void setMatchSrcIPv4Net(String matchSrcIPv4Net) { matchSrcIpaddrToUpdate = matchSrcIPv4Net; }
	
		@Override
		public String getMatchDstIPv4Net() { return matchDstIpaddr; }
	
		@Override
		public void setMatchDstIPv4Net(String matchDstIPv4Net) { matchDstIpaddrToUpdate = matchDstIPv4Net; }

		@Override
		public Byte getMatchIpProto() {return matchIpProto; }
	
		@Override
		public void setMatchIpProto(Byte matchIpProto) { matchIpProto = matchIpProto; }

		@Override
		public Byte getMatchIpToS() {return matchIpToS; }
	
		@Override
		public void setMatchIpToS(Byte matchIpToS) { matchIpToS = matchIpToS; }

		@Override
		public Short getMatchSrcTcpUdpPort() {return matchSrcTcpUdpPort; }
	
		@Override
		public void setMatchSrcTcpUdpPort(Short matchSrcTcpUdpPort) { matchSrcTcpUdpPortToUpdate = matchSrcTcpUdpPort; }

		@Override
		public Short getMatchDstTcpUdpPort() {return matchDstTcpUdpPort; }
	
		@Override
		public void setMatchDstTcpUdpPort(Short matchDstTcpUdpPort) { matchDstTcpUdpPortToUpdate = matchDstTcpUdpPort; }
	
		@Override
		public Short getActionOutputPort() { return actionOutputPort; }
	
		@Override
		public void setActionOutputPort(Short actionOutputPort) { actionOutputPortToUpdate = actionOutputPort; }
	
		@Override
		public IFlowPath getFlow() { return flowPath; }
	
		@Override
		public void setFlow(IFlowPath flow) { flowPathToUpdate = flow; }
	
		@Override
		public ISwitchObject getSwitch() { return sw; }
	
		@Override
		public void setSwitch(ISwitchObject sw) { swToUpdate = sw; }
	
		@Override
		public IPortObject getInPort() { return inport; }
	
		@Override
		public void setInPort(IPortObject port) { inportToUpdate = port; }
	
		@Override
		public IPortObject getOutPort() { return outport; }
	
		@Override
		public void setOutPort(IPortObject port) { outportToUpdate = port; }
	}


	public TestableGraphDBOperation() {
		super(EasyMock.createNiceMock(GraphDBConnection.class));
		
		switches = new ArrayList<TestSwitchObject>();
		ports = new ArrayList<TestPortObject>();
		devices = new ArrayList<TestDeviceObject>();
		paths = new ArrayList<TestFlowPath>();
		entries = new ArrayList<TestFlowEntry>();
		
		switchesToAdd = new ArrayList<TestSwitchObject>();
		portsToAdd = new ArrayList<TestPortObject>();
		devicesToAdd = new ArrayList<TestDeviceObject>();
		pathsToAdd = new ArrayList<TestFlowPath>();
		entriesToAdd = new ArrayList<TestFlowEntry>();

		switchesToRemove = new ArrayList<TestSwitchObject>();
		portsToRemove = new ArrayList<TestPortObject>();
		devicesToRemove = new ArrayList<TestDeviceObject>();
		pathsToRemove = new ArrayList<TestFlowPath>();
		entriesToRemove = new ArrayList<TestFlowEntry>();
		
		clearUncommitedData();
	}
	
	private void clearUncommitedData() {
		for(TestFlowEntry flow : entries) {
			flow.clearUncommitedData();
		}
		for(TestFlowEntry flow : entriesToAdd) {
			flow.clearUncommitedData();
		}
		
		for(TestDeviceObject dev : devices) {
			dev.clearUncommitedData();
		}
		for(TestDeviceObject dev : devicesToAdd) {
			dev.clearUncommitedData();
		}
		
		for(TestSwitchObject sw : switches) {
			sw.clearUncommitedData();
		}
		for(TestSwitchObject sw : switchesToAdd) {
			sw.clearUncommitedData();
		}
		
		for(TestPortObject port : ports) {
			port.clearUncommitedData();
		}
		for(TestPortObject port : portsToAdd) {
			port.clearUncommitedData();
		}
		
		entriesToAdd.clear();
		entriesToRemove.clear();
		devicesToAdd.clear();
		devicesToRemove.clear();
		switchesToAdd.clear();
		switchesToRemove.clear();
		portsToAdd.clear();
		portsToRemove.clear();
	}
	
	
	// this.*ForTest() methods below are supposed to be used for creation of test topology.
	/**
	 * Create new empty TestSwitchObject.
	 * @return New TestSwitchObject
	 */
	public TestSwitchObject createNewSwitchForTest() {
		TestSwitchObject sw = new TestSwitchObject();
		switches.add(sw);
		return sw;
	}
	
	/**
	 * Create new TestSwitchObject with specific DPID.
	 * @param dpid DPID to be set
	 * @return New TestSwitchObject
	 */
	public TestSwitchObject createNewSwitchForTest(String dpid) {
		for(TestSwitchObject sw_loop : switches) {
			if(sw_loop.getDPID().equals(dpid)) {
				// Already created
				log.error("switch already exists : " + dpid);
				return sw_loop;
			}
		}

		TestSwitchObject sw = new TestSwitchObject();
		
		sw.setDpidForTest(dpid);
		switches.add(sw);
		
		return sw;
	}
	
	/**
	 * Create new empty TestPortObject.
	 * @return New TestPortObject
	 */
	public TestPortObject createNewPortForTest() {
		TestPortObject port = new TestPortObject();
		ports.add(port);
		return port;
	}
	
	/**
	 * Create new TestPortObject with specific DPID and port number.
	 * @param dpid DPID to be set
	 * @param number Port number to be set
	 * @return New TestPortObject
	 */
	public TestPortObject createNewPortForTest(String dpid, Short number) {
		TestSwitchObject sw = null;
		
		for(TestSwitchObject sw_loop : switches) {
			if(sw_loop.getDPID().equals(dpid)) {
				sw = sw_loop;
			}
		}
		
		if(sw != null) {
			TestPortObject port = new TestPortObject();
			port.setNumberForTest(number);
			port.setSwitchForTest(sw);
			sw.addPortForTest(port);
			
			ports.add(port);

			return port;
		} else {
			return  null;
		}
	}
	
	/**
	 * Link a TestPortObject to other TestPortObject.
	 * @param src TestPortObjecgt of source port.
	 * @param dst TestPortObjecgt of destination port.
	 */
	public void setLinkBetweenPortsForTest(TestPortObject src, TestPortObject dst) {
		src.addLinkedPortForTest(dst);
	}
	
	/**
	 * Create new empty TestDeviceObject.
	 * @return New TestDeviceObject
	 */
	public TestDeviceObject createNewDeviceForTest() {
		TestDeviceObject dev = new TestDeviceObject();
		
		return dev;
	}
	
	/**
	 * Create new empty TestFlowPathObject.
	 * @return New TestFlowPathObject
	 */
	public TestFlowPath createNewFlowPathForTest() {
		TestFlowPath path = new TestFlowPath();
		paths.add(path);
		return path;
	}

	/**
	 * Create new empty TestFlowEntryObject.
	 * @return New TestFlowEntryObject
	 */
	public TestFlowEntry createNewFlowEntryForTest() {
		TestFlowEntry entry = new TestFlowEntry();
		entries.add(entry);
		return entry;
	}

	
	public boolean hasLinkBetween(String srcSw_str, Short srcNumber, String dstSw_str, Short dstNumber) {
		IPortObject srcPort = null, dstPort = null;
		long srcSw = HexString.toLong(srcSw_str);
		long dstSw = HexString.toLong(dstSw_str);
		
		for(TestSwitchObject sw : switches) {
			long swLong = HexString.toLong(sw.getDPID());
			if(swLong == srcSw) {
				for(IPortObject port : sw.getPorts()) {
					if(port.getNumber().equals(srcNumber)) {
						srcPort = port;
					}
				}
			} else if(swLong == dstSw) {
				for(IPortObject port : sw.getPorts()) {
					if(port.getNumber().equals(dstNumber)) {
						dstPort = port;
					}
				}
			}
		}
		
		if(srcPort != null && dstPort != null) {
			for(IPortObject port : srcPort.getLinkedPorts()) {
				if(port.equals(dstPort)) {
					return true;
				}
			}
		}
		
		return false;
	}

	// Overriding methods below are to mock GraphDBOperation class.
	@Override
	public ISwitchObject newSwitch(String dpid) {
		TestSwitchObject sw = new TestSwitchObject();
		sw.setDPID(dpid);
		switchesToAdd.add(sw);
		
		return sw;
	}

	@Override
	public ISwitchObject searchSwitch(String dpid_str) {
		Long dpid = HexString.toLong(dpid_str);
		
		for(ISwitchObject sw : switches) {
			if(HexString.toLong(sw.getDPID()) == dpid) {
				return sw;
			}
		}
		return null;
	}

	@Override
	public ISwitchObject searchActiveSwitch(String dpid_str) {
		Long dpid = HexString.toLong(dpid_str);

		for(ISwitchObject sw : switches) {
			if(HexString.toLong(sw.getDPID()) == dpid && sw.getState().equals("ACTIVE")) {
				return sw;
			}
		}
		return null;
	}

	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
		List<ISwitchObject> list = new ArrayList<ISwitchObject>();
		
		for(ISwitchObject sw : switches) {
			if(sw.getState() != null && sw.getState().equals("ACTIVE")) {
				list.add(sw);
			}
		}
		return list.isEmpty() ? null : list;
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
		List<ISwitchObject> list = new ArrayList<ISwitchObject>();
		
		for(ISwitchObject sw : switches) {
			list.add(sw);
		}
		
		return list.isEmpty() ? null : list;
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
		List<ISwitchObject> list = new ArrayList<ISwitchObject>();
		
		for(ISwitchObject sw : switches) {
			if(! sw.getState().equals("ACTIVE")) {
				list.add(sw);
			}
		}
		return list.isEmpty() ? null : list;
	}

	@Override
	public Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries() {
		List<IFlowEntry> list = new ArrayList<IFlowEntry>();
		
		for(TestFlowEntry entry : entries) {
			if(entry.getSwitchState().equals("FE_SWITCH_NOT_UPDATED")) {
				list.add(entry);
			}
		}
		return list;
	}

	@Override
	public void removeSwitch(ISwitchObject sw) {
		if(switches.contains(sw)) {
			switchesToRemove.add((TestSwitchObject)sw);
		}
	}

	@Override
	public IPortObject newPort(Short portNumber) {
		TestPortObject port = new TestPortObject();
		port.setNumber(portNumber);
		
		return port;
	}

	public IPortObject newPort(Long dpid, Short portNumber) {
		TestPortObject port = null;
		TestSwitchObject sw = (TestSwitchObject)searchSwitch(HexString.toHexString(dpid));
		
		if(sw != null) {
			port = (TestPortObject)newPort(portNumber);
			portsToAdd.add(port);
			sw.addPort(port);
		}
		
		return port;
	}
	
	@Override
	public IPortObject searchPort(String dpid_str, Short number) {
		long dpid = HexString.toLong(dpid_str);
		
		for(TestSwitchObject sw : switches) {
			if(HexString.toLong(sw.getDPID()) == dpid) {
				for(IPortObject port : sw.getPorts()) {
					if(port.getNumber().equals(number)) {
						return port;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void removePort(IPortObject port) {
		for(TestSwitchObject sw : switches) {
			for(IPortObject pt : sw.getPorts()) {
				if(pt.equals(port)) {
					sw.removePort(port);
				}
			}
		}
		portsToRemove.add((TestPortObject)port);
	}

	@Override
	public IDeviceObject newDevice() {
		TestDeviceObject dev = new TestDeviceObject();
		devicesToAdd.add(dev);
		
		return dev;
	}

	@Override
	public IDeviceObject searchDevice(String macAddr) {
		for(IDeviceObject dev : devices) {
			if(dev.getMACAddress().equals(macAddr)) {
				return dev;
			}
		}
		return null;
	}

	@Override
	public Iterable<IDeviceObject> getDevices() {
		List<IDeviceObject> list = new ArrayList<IDeviceObject>();
		
		for(TestDeviceObject dev : devices) {
			list.add(dev);
		}
		
		return list;
	}

	@Override
	public void removeDevice(IDeviceObject dev) {
		if(devices.contains((TestDeviceObject)dev)) {
			devicesToRemove.add((TestDeviceObject)dev);
		}
	}

	@Override
	public IFlowPath newFlowPath() {
		TestFlowPath path = new TestFlowPath();
		pathsToAdd.add(path);
		
		return path;
	}

	@Override
	public IFlowPath searchFlowPath(FlowId flowId) {
		for(IFlowPath path : paths) {
			if(path.getFlowId().equals(flowId)) {
				return path;
			}
		}
		return null;
	}

	@Override
	public IFlowPath getFlowPathByFlowEntry(IFlowEntry flowEntry) {
		for(IFlowPath path : paths) {
			for(IFlowEntry entry : path.getFlowEntries()) {
				if(entry.equals(flowEntry)) {
					return path;
				}
			}

		}
		return null;
	}

	@Override
	public Iterable<IFlowPath> getAllFlowPaths() {
		List<IFlowPath> list = new ArrayList<IFlowPath>();
		
		for(IFlowPath path : paths) {
			list.add(path);
		}
		
		return list;
	}

	@Override
	public void removeFlowPath(IFlowPath flowPath) {
		if(paths.contains((TestFlowPath)flowPath)) {
			pathsToRemove.add((TestFlowPath)flowPath);
		}
	}

	@Override
	public IFlowEntry newFlowEntry() {
		TestFlowEntry entry = new TestFlowEntry();
		entriesToAdd.add(entry);
		return entry;
	}

	@Override
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
		for(TestFlowEntry entry : entries) {
			// TODO check if this matching works
			if(entry.getFlowEntryId().equals(flowEntryId)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public Iterable<IFlowEntry> getAllFlowEntries() {
		List<IFlowEntry> list = new ArrayList<IFlowEntry>();
		
		for(TestFlowEntry entry : entries) {
			list.add(entry);
		}
		
		return list;
	}

	@Override
	public void removeFlowEntry(IFlowEntry flowEntry) {
		if(entries.contains((TestFlowEntry)flowEntry)) {
			entriesToRemove.add((TestFlowEntry)flowEntry);
		}
	}

	@Override
	public IDBConnection getDBConnection() {
		return super.getDBConnection();
	}

	@Override
	public void commit() {
		for(TestSwitchObject sw : switchesToAdd) {
			switches.add(sw);
		}
		for(TestSwitchObject sw : switchesToRemove) {
			sw.commit();
			switches.remove(sw);
		}
		for(TestSwitchObject sw : switches) {
			sw.commit();
		}
		
		for(TestPortObject port : portsToAdd) {
			ports.add(port);
		}
		for(TestPortObject port : portsToRemove) {
			port.commit();
			ports.remove(port);
		}
		for(TestPortObject port : ports) {
			port.commit();
		}
		
		for(TestDeviceObject dev : devicesToAdd) {
			devices.add(dev);
		}
		for(TestDeviceObject dev : devicesToRemove) {
			dev.commit();
			devices.remove(dev);
		}
		for(TestDeviceObject dev : devices) {
			dev.commit();
		}
		
		clearUncommitedData();
	}

	@Override
	public void rollback() {
		clearUncommitedData();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
