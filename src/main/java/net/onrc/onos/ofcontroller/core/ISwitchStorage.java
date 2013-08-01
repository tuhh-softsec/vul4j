package net.onrc.onos.ofcontroller.core;

import java.util.Collection;


import net.floodlightcontroller.core.IOFSwitch;

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
	 * Add a switch and all its associated ports
	 */
	public void addSwitch(IOFSwitch sw);
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
	 * Initialize
	 */
	public void init(String conf);
	

}
