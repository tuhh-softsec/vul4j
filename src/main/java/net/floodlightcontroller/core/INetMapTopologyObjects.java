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
		
		@JsonIgnore
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
		
		@JsonProperty("AttachmentPoint")
		@GremlinGroovy("_().in('host').in('on').path(){it.number}{it.dpid}")
		public List<SwitchPort> getAttachmentPoints();
	}
}
