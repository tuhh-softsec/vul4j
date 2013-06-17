package net.floodlightcontroller.linkdiscovery.internal;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.easymock.EasyMock;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBOperation;
import net.onrc.onos.util.IDBConnection;

public class TestGraphDBOperation extends GraphDBOperation {
	protected static Logger log = LoggerFactory.getLogger(TestGraphDBOperation.class);

	protected List<TestSwitchObject> switches;
	protected List<TestPortObject> ports;
	protected List<TestDeviceObject> devices;
//	protected List<TestFlowEntry> flows;

	protected List<TestSwitchObject> switchesToAdd;
	protected List<TestPortObject> portsToAdd;
	protected List<TestDeviceObject> devicesToAdd;
//	protected List<TestFlowEntry> flowsToAdd;

	protected List<TestSwitchObject> switchesToRemove;
	protected List<TestPortObject> portsToRemove;
	protected List<TestDeviceObject> devicesToRemove;
//	protected List<TestFlowEntry> flowsToRemove;


	// Testable implementations of INetMapTopologyObject interfaces
	public static class TestDeviceObject implements IDeviceObject {
		private String state,type,mac,ipaddr;
		private List<IPortObject> ports;
		private List<ISwitchObject> switches;
		
		private String stateToUpdate,typeToUpdate,macToUpdate,ipaddrToUpdate;
		private List<IPortObject> portsToAdd;
		private List<IPortObject> portsToRemove;

		public TestDeviceObject() {
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
		
		public void addSwitchForTest(ISwitchObject sw) {
			switches.add(sw);
		}
		
		public void addPortForTest(IPortObject port) {
			ports.add(port);
		}
		
		@Override
		@JsonProperty("state")
		@Property("state")
		public String getState() { return state; }
	
		@Override
		@Property("state")
		public void setState(String state) { stateToUpdate = state; }
	
		@Override
		@JsonIgnore
		@Property("type")
		public String getType() { return type; }
	
		@Override
		@Property("type")
		public void setType(String type) { typeToUpdate = type; }
	
		@Override
		public Vertex asVertex() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		@JsonProperty("mac")
		@Property("dl_addr")
		public String getMACAddress() { return mac; }
	
		@Override
		@Property("dl_addr")
		public void setMACAddress(String macaddr) { macToUpdate = macaddr; }
	
		@Override
		@JsonProperty("ipv4")
		@Property("nw_addr")
		public String getIPAddress() { return ipaddr; }

		@Override
		@Property("dl_addr")
		public void setIPAddress(String ipaddr) { ipaddrToUpdate = ipaddr; }
	
		@Override
		@JsonIgnore
		@Incidence(label = "host", direction = Direction.IN)
		public Iterable<IPortObject> getAttachedPorts() { return ports; }
	
		@Override
		@JsonIgnore
		@Incidence(label = "host", direction = Direction.IN)
		public void setHostPort(IPortObject port) { portsToAdd.add(port); }
	
		@Override
		@JsonIgnore
		@Incidence(label = "host", direction = Direction.IN)
		public void removeHostPort(IPortObject port) { portsToRemove.add(port); }
	
		@Override
		@JsonIgnore
		@GremlinGroovy("_().in('host').in('on')")
		public Iterable<ISwitchObject> getSwitch() { return switches; }
	}
	
	public static class TestSwitchObject implements ISwitchObject {
		private String state,type,dpid;
		private List<IPortObject> ports;
		private List<IDeviceObject> devices;
		private List<IFlowEntry> flows;

		private String stateToUpdate, typeToUpdate, dpidToUpdate;
		private List<IPortObject> portsToAdd;
		private List<IPortObject> portsToRemove;

		public TestSwitchObject() {
			type = "switch";
			state = "ACTIVE";
			
			ports = new ArrayList<IPortObject>();
			portsToAdd = new ArrayList<IPortObject>();
			portsToRemove = new ArrayList<IPortObject>();
			devices = new ArrayList<IDeviceObject>();
			flows = new ArrayList<IFlowEntry>();
			
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
		
		public void setDpidForTest(String dpid) { this.dpid = dpid; }
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void addPortForTest(TestPortObject port) { ports.add(port);  }
		
		@Override
		@JsonProperty("state")
		@Property("state")
		public String getState() { return state; }
	
		@Override
		@Property("state")
		public void setState(String state) { this.stateToUpdate = state; }
	
		@Override
		@JsonIgnore
		@Property("type")
		public String getType() { return type ; }
	
		@Override
		@Property("type")
		public void setType(String type) { this.typeToUpdate = type; }
	
		// Not support for test
		@Override
		public Vertex asVertex() { return null; }
	
		@Override
		@JsonProperty("dpid")
		@Property("dpid")
		public String getDPID() { return dpid; }
	
		@Override
		@Property("dpid")
		public void setDPID(String dpid) { this.dpidToUpdate = dpid; }
	
		@Override
		@JsonProperty("ports")
		@Adjacency(label = "on")
		public Iterable<IPortObject> getPorts() { return ports; }
	
		@Override
		@JsonIgnore
		@GremlinGroovy("_().out('on').has('number',port_num)")
		public IPortObject getPort(@GremlinParam("port_num") short port_num) {
			for(IPortObject port : ports) {
				if(port.getNumber() == port_num) {
					return port;
				}
			}
			return null;
		}
	
		@Override
		@Adjacency(label = "on")
		public void addPort(IPortObject port) { portsToAdd.add(port); }
	
		@Override
		@Adjacency(label = "on")
		public void removePort(IPortObject port) { portsToRemove.add(port); }
	
		@Override
		@JsonIgnore
		@GremlinGroovy("_().out('on').out('host')")
		public Iterable<IDeviceObject> getDevices() { return devices; }
	
		@Override
		@JsonIgnore
		@Incidence(label = "switch", direction = Direction.IN)
		public Iterable<IFlowEntry> getFlowEntries() { return flows; }
	}
	
	public static class TestPortObject implements IPortObject {
		private String state,type,desc;
		private Short number;
		private Integer port_state;
		private ISwitchObject sw;
		private List<IPortObject> linkedPorts;
		private List<IDeviceObject> devices;
		private List<IFlowEntry> flows;
		
		private String stateToUpdate,typeToUpdate,descToUpdate;
		private Short numberToUpdate;
		private Integer port_stateToUpdate;
		private List<IPortObject> linkedPortsToAdd;
		private List<IPortObject> linkedPortsToRemove;
		private List<IDeviceObject> devicesToAdd;
		private List<IDeviceObject> devicesToRemove;
		

		public TestPortObject() {
			type = "port";
			state = "ACTIVE";

			linkedPorts = new ArrayList<IPortObject>();
			linkedPortsToAdd = new ArrayList<IPortObject>();
			linkedPortsToRemove = new ArrayList<IPortObject>();
			devices = new ArrayList<IDeviceObject>();
			devicesToAdd = new ArrayList<IDeviceObject>();
			devicesToRemove = new ArrayList<IDeviceObject>();
			flows = new ArrayList<IFlowEntry>();
			
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
		
		public void setStateForTest(String state) { this.state = state; }
		public void setTypeForTest(String type) { this.type = type; }
		public void setDescForTest(String desc) { this.desc = desc; }
		public void setNumberForTest(Short number) { this.number = number; }
		public void setPortStateForTest(Integer state) { this.port_state = state; }
		public void setSwitchForTest(ISwitchObject sw) { this.sw = sw; }
		public void addLinkedPortForTest(TestPortObject port) { this.linkedPorts.add(port); }
		
		@Override
		@JsonProperty("state")
		@Property("state")
		public String getState() { return state; }

		@Override
		@Property("state")
		public void setState(String state) { this.stateToUpdate = state; }

		@Override
		@JsonIgnore
		@Property("type")
		public String getType() { return type; }

		@Override
		@Property("type")
		public void setType(String type) { this.typeToUpdate = type; }

		// not support for test
		@Override
		public Vertex asVertex() {
			return null;
		}

		@Override
		@JsonProperty("number")
		@Property("number")
		public Short getNumber() { return number; }

		@Override
		@Property("number")
		public void setNumber(Short n) { this.numberToUpdate = n; }

		@Override
		@JsonProperty("desc")
		@Property("desc")
		public String getDesc() { return desc; }

		@Override
		@Property("desc")
		public void setDesc(String s) { this.descToUpdate = s; }

		@Override
		@JsonIgnore
		@Property("port_state")
		public Integer getPortState() { return port_state; }

		@Override
		@Property("port_state")
		public void setPortState(Integer s) { this.port_stateToUpdate = s; }

		@Override
		@JsonIgnore
		@Incidence(label = "on", direction = Direction.IN)
		public ISwitchObject getSwitch() { return sw; }

		@Override
		@JsonProperty("devices")
		@Adjacency(label = "host")
		public Iterable<IDeviceObject> getDevices() { return devices; }

		@Override
		@Adjacency(label = "host")
		public void setDevice(IDeviceObject device) { devicesToAdd.add(device); }

		@Override
		@Adjacency(label = "host")
		public void removeDevice(IDeviceObject device) { devicesToRemove.add(device); }

		@Override
		@JsonIgnore
		@Incidence(label = "inport", direction = Direction.IN)
		public Iterable<IFlowEntry> getInFlowEntries() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		@JsonIgnore
		@Incidence(label = "outport", direction = Direction.IN)
		public Iterable<IFlowEntry> getOutFlowEntries() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		@JsonIgnore
		@Adjacency(label = "link")
		public Iterable<IPortObject> getLinkedPorts() {
			return linkedPorts; }

		@Override
		@Adjacency(label = "link")
		public void removeLink(IPortObject dest_port) { linkedPortsToRemove.add(dest_port); }

		@Override
		@Adjacency(label = "link")
		public void setLinkPort(IPortObject dest_port) { linkedPortsToAdd.add(dest_port); }
	}
	
	
	public TestGraphDBOperation() {
		super(EasyMock.createNiceMock(GraphDBConnection.class));
		
		switches = new ArrayList<TestSwitchObject>();
		ports = new ArrayList<TestPortObject>();
		devices = new ArrayList<TestDeviceObject>();
//		flows = new ArrayList<TestFlowEntry>();
		
		switchesToAdd = new ArrayList<TestSwitchObject>();
		portsToAdd = new ArrayList<TestPortObject>();
		devicesToAdd = new ArrayList<TestDeviceObject>();
//		flowsToAdd = new ArrayList<TestFlowEntry>();

		switchesToRemove = new ArrayList<TestSwitchObject>();
		portsToRemove = new ArrayList<TestPortObject>();
		devicesToRemove = new ArrayList<TestDeviceObject>();
//		flowsToRemove = new ArrayList<TestFlowEntry>();
		
		clearUncommitedData();
	}
	
	private void clearUncommitedData() {
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
		
		devicesToAdd.clear();
		devicesToRemove.clear();
		switchesToAdd.clear();
		switchesToRemove.clear();
		portsToAdd.clear();
		portsToRemove.clear();
	}
	
	
	// this.*ForTest() methods below are supposed to be used for creation of test topology.
	/**
	 * 
	 * @param dpid
	 * @return
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
	
	public void setLinkBetweenPortsForTest(TestPortObject src, TestPortObject dst) {
		src.addLinkedPortForTest(dst);
		//dst.addLinkedPortForTest(src);
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
			if(sw.getState().equals("ACTIVE")) {
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
		// TODO Auto-generated method stub
		return null;
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
	public IPortObject searchPort(String dpid_str, short number) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceObject searchDevice(String macAddr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<IDeviceObject> getDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDevice(IDeviceObject dev) {
		// TODO Auto-generated method stub

	}

	@Override
	public IFlowPath newFlowPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFlowPath searchFlowPath(FlowId flowId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFlowPath getFlowPathByFlowEntry(IFlowEntry flowEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<IFlowPath> getAllFlowPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFlowPath(IFlowPath flowPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public IFlowEntry newFlowEntry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<IFlowEntry> getAllFlowEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFlowEntry(IFlowEntry flowEntry) {
		// TODO Auto-generated method stub

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
