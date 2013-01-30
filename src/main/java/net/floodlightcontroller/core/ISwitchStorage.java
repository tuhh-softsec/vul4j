package net.floodlightcontroller.core;

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.StdSerializers;
import org.openflow.protocol.OFPhysicalPort;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;

public interface ISwitchStorage extends INetMapStorage {
	
	public enum SwitchState {
		INACTIVE,
		ACTIVE
	}

	public interface ISwitchObject {
		
		@JsonProperty("dpid")
		@Property("dpid")
		public String getDPID();
		
		@JsonProperty("state")
		@Property("state")
		public String getState();
		
		@JsonIgnore
		@Property("type")
		public String getType();
		
		@JsonProperty("ports")
		@Adjacency(label="on")
		public Iterable<IPortObject> getPorts();
	}
	
	public interface IPortObject {
		
		@JsonProperty("state")
		@Property("state")
		public int getState();
		
		@JsonIgnore
		@Property("type")
		public String getType();
		
		@JsonProperty("number")
		@Property("number")
		public Short getNumber();
		
		@JsonProperty("desc")
		@Property("desc")
		public String getDesc();
		
		@JsonIgnore
		@Incidence(label="on",direction = Direction.IN)
		public ISwitchObject getSwitch();
	}
	/*
	 * Update the switch details
	 */
	public void update(String dpid,SwitchState state, DM_OPERATION op);
	/*
	 * Associate a port on switch
	 */
	public void addPort(String dpid, OFPhysicalPort port);
	/*
	 * Get all ports associated on a switch
	 */
	public Collection<OFPhysicalPort> getPorts(long dpid);
	/*
	 * Get Port by Number
	 */
	public OFPhysicalPort getPort(String dpid, short portnum);
	/*
	 * Get port by name
	 */
	public OFPhysicalPort getPort(String dpid, String portName);
	/*
	 * Add a switch
	 */
	public void addSwitch(String dpid);
	/*
	 * Delete switch and associated ports
	 */
	public void deleteSwitch(String dpid);
	/*
	 * Delete a port on a switch by num
	 */
	public void deletePort(String dpid, short port);
	/*
	 * Delete port on a switch by name
	 */
	public void deletePort(String dpid, String portName);
	
	public Iterable<ISwitchObject> getActiveSwitches();
	public Iterable<ISwitchObject> getAllSwitches();
	public Iterable<ISwitchObject> getInactiveSwitches();
	
	/*
	 * Initialize
	 */
	public void init(String conf);
	

}
