package net.floodlightcontroller.core;

import java.util.Collection;

import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openflow.protocol.OFPhysicalPort;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface ISwitchStorage extends INetMapStorage {
	
	public enum SwitchState {
		INACTIVE,
		ACTIVE
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
