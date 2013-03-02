package net.floodlightcontroller.core;

import java.util.List;

import net.floodlightcontroller.devicemanager.SwitchPort;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.VertexFrame;

public interface INetMapTopologyObjects {
	
public interface IBaseObject extends VertexFrame {
	
	@JsonProperty("state")
	@Property("state")
	public String getState();
	
	@Property("state")
	public void setState(final String state);
	
	@JsonIgnore
	@Property("type")
	public String getType();
	@Property("type")
	public void setType(final String type);
	
}
	
public interface ISwitchObject extends IBaseObject{
		
		@JsonProperty("dpid")
		@Property("dpid")
		public String getDPID();
		
		@Property("dpid")
		public void setDPID(String dpid);
				
		@JsonProperty("ports")
		@Adjacency(label="on")
		public Iterable<IPortObject> getPorts();
		
		@Adjacency(label="on")
		public IPortObject getPort(final short port_num);
		
		@Adjacency(label="on")
		public void addPort(final IPortObject port);
		
		@Adjacency(label="on")
		public void removePort(final IPortObject port);
		
		@JsonIgnore
		@GremlinGroovy("_().out('on').out('host')")
		public Iterable<IDeviceObject> getDevices();
	}
	
	public interface IPortObject extends IBaseObject{
				
		@JsonProperty("number")
		@Property("number")
		public Short getNumber();
		
		@JsonProperty("desc")
		@Property("desc")
		public String getDesc();
		
		@JsonIgnore
		@Incidence(label="on",direction = Direction.IN)
		public ISwitchObject getSwitch();
		
		
		@JsonProperty("devices")
		@Adjacency(label="host")
		public Iterable<IDeviceObject> getDevices();
		
		@Adjacency(label="host")
		public void setDevice(final IDeviceObject device);
		
		@Adjacency(label="host")
		public void removeDevice(final IDeviceObject device);
		
//		@JsonIgnore
//		@Adjacency(label="link")
//		public Iterable<ILinkObject> getLinks();
	}
	
	public interface IDeviceObject extends IBaseObject {
		
		@JsonProperty("mac")
		@Property("dl_addr")
		public String getMACAddress();
		@Property("dl_addr")
		public void setMACAddress(String macaddr);
		
		@JsonProperty("ipv4")
		@Property("nw_addr")
		public String getIPAddress();
		@Property("dl_addr")
		public void setIPAddress(String ipaddr);
		
		@JsonIgnore
		@Incidence(label="host",direction = Direction.IN)
		public Iterable<IPortObject> getAttachedPorts();
			
		@JsonIgnore
		@Incidence(label="host",direction=Direction.IN)
		public void setHostPort(final IPortObject port);
		
		@JsonIgnore
		@Incidence(label="host",direction=Direction.IN)
		public void removeHostPort(final IPortObject port);
		
		@JsonIgnore
		@GremlinGroovy("_().in('host').in('on')")
		public Iterable<ISwitchObject> getSwitch();
		
/*		@JsonProperty("dpid")
		@GremlinGroovy("_().in('host').in('on').next().getProperty('dpid')")
		public Iterable<String> getSwitchDPID();
		
		@JsonProperty("number")
		@GremlinGroovy("_().in('host').transform{it.number}")
		public Iterable<Short> getPortNumber();
		
		@JsonProperty("AttachmentPoint")
		@GremlinGroovy("_().in('host').in('on').path(){it.number}{it.dpid}")
		public Iterable<SwitchPort> getAttachmentPoints();*/
	}

public interface IFlowPath extends IBaseObject {
		@Property("flow_id")
		public String getFlowId();

		@Property("flow_id")
		public void setFlowId(String flowId);

		@Property("installer_id")
		public String getInstallerId();

		@Property("installer_id")
		public void setInstallerId(String installerId);

		@Property("src_switch")
		public String getSrcSwitch();

		@Property("src_switch")
		public void setSrcSwitch(String srcSwitch);

		@Property("src_port")
		public Short getSrcPort();

		@Property("src_port")
		public void setSrcPort(Short srcPort);

		@Property("dst_switch")
		public String getDstSwitch();

		@Property("dst_switch")
		public void setDstSwitch(String dstSwitch);

		@Property("dst_port")
		public Short getDstPort();

		@Property("dst_port")
		public void setDstPort(Short dstPort);

		@Adjacency(label="flow", direction=Direction.IN)
		public Iterable<IFlowEntry> getFlowEntries();

		@Adjacency(label="flow", direction=Direction.IN)
		public void addFlowEntry(final IFlowEntry flowEntry);

		@Adjacency(label="flow", direction=Direction.IN)
		public void removeFlowEntry(final IFlowEntry flowEntry);
	}

public interface IFlowEntry extends IBaseObject {
		@Property("flow_entry_id")
		public String getFlowEntryId();

		@Property("flow_entry_id")
		public void setFlowEntryId(String flowEntryId);

		@Property("switch_dpid")
		public String getSwitchDpid();

		@Property("switch_dpid")
		public void setSwitchDpid(String switchDpid);

		@Property("in_port")
		public Short getInPort();

		@Property("in_port")
		public void setInPort(Short inPort);

		@Property("out_port")
		public Short getOutPort();

		@Property("out_port")
		public void setOutPort(Short outPort);

		@Property("user_state")
		public String getUserState();

		@Property("user_state")
		public void setUserState(String userState);

		@Property("switch_state")
		public String getSwitchState();

		@Property("switch_state")
		public void setSwitchState(String switchState);

		@Property("error_state_type")
		public String getErrorStateType();

		@Property("error_state_type")
		public void setErrorStateType(String errorStateType);

		@Property("error_state_code")
		public String getErrorStateCode();

		@Property("error_state_code")
		public void setErrorStateCode(String errorStateCode);
	}
}
