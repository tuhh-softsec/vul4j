package net.floodlightcontroller.core;

import java.util.Collection;

import org.openflow.protocol.OFPhysicalPort;

public interface ISwitchStorage extends INetMapStorage {
	
	/*
	 * Update the switch details
	 */
	public void update(long dpid,DM_OPERATION op);
	/*
	 * Associate a port on switch
	 */
	public void addPort(long dpid, OFPhysicalPort port);
	/*
	 * Get all ports associated on a switch
	 */
	public Collection<OFPhysicalPort> getPorts(long dpid);
	/*
	 * Get Port by Number
	 */
	public OFPhysicalPort getPort(long dpid, short portnum);
	/*
	 * Get port by name
	 */
	public OFPhysicalPort getPort(long dpid, String portName);
	/*
	 * Delete switch and associated ports
	 */
	public void deleteSwitch(long dpid);
	/*
	 * Delete a port on a switch by num
	 */
	public void deletePort(long dpid, short port);
	/*
	 * Delete port on a switch by name
	 */
	public void deletePort(long dpid, String portName);
}
