package net.onrc.onos.ofcontroller.core;

import java.util.List;

import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.protocol.OFPhysicalPort;

public interface ISwitchStorage extends INetMapStorage {
	
	public enum SwitchState {
		INACTIVE,
		ACTIVE
	}
	
	/*
	 * Initialize
	 */
	public void init(String conf);
	/*
	 * Update the switch details
	 */
	public boolean updateSwitch(String dpid, SwitchState state, DM_OPERATION op);
	/*
	 * Add a switch and all its associated ports
	 */
	public boolean addSwitch(IOFSwitch sw);
	/*
	 * Add a switch
	 */
	public boolean addSwitch(String dpid);
	/*
	 * Delete switch and associated ports
	 */
	public boolean deleteSwitch(String dpid);
	/*
	 * Update the port details
	 */
	public boolean updatePort(String dpid, short port, int state, String desc);
	/*
	 * Associate a port on switch
	 */
	public boolean addPort(String dpid, OFPhysicalPort port);
	/*
	 * Delete a port on a switch by num
	 */
	public boolean deletePort(String dpid, short port);

	/**
	 * Get list of all ports on the switch specified by given DPID.
	 *
	 * @param dpid DPID of desired switch.
	 * @return List of port IDs. Empty list if no port was found.
	 */
	public List<Short> getPorts(String dpid);
}
