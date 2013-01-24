package net.floodlightcontroller.core;

import java.util.Collection;
import java.util.List;

import org.openflow.protocol.OFPhysicalPort;

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
	
	public List<String> getActiveSwitches();
	public List<String> getAllSwitches();
	public List<String> getInactiveSwitches();
	
	/*
	 * Initialize
	 */
	public void init(String conf);
	

}
