/**
 * 
 */
package net.floodlightcontroller.core.internal;

import java.util.Collection;

import org.openflow.protocol.OFPhysicalPort;

import net.floodlightcontroller.core.ISwitchStorage;

/**
 * @author pankaj
 *
 */
public class SwitchStorageImplStubs implements ISwitchStorage {

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#update(long, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(String dpid, DM_OPERATION op) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#addPort(long, org.openflow.protocol.OFPhysicalPort)
	 */
	@Override
	public void addPort(String dpid, OFPhysicalPort port) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#getPorts(long)
	 */
	@Override
	public Collection<OFPhysicalPort> getPorts(long dpid) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#getPort(long, short)
	 */
	@Override
	public OFPhysicalPort getPort(String dpid, short portnum) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#getPort(long, java.lang.String)
	 */
	@Override
	public OFPhysicalPort getPort(String dpid, String portName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#addSwitch(long)
	 */
	@Override
	public void addSwitch(String dpid) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#deleteSwitch(long)
	 */
	@Override
	public void deleteSwitch(String dpid) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#deletePort(long, short)
	 */
	@Override
	public void deletePort(String dpid, short port) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#deletePort(long, java.lang.String)
	 */
	@Override
	public void deletePort(String dpid, String portName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#init(java.lang.String)
	 */
	@Override
	public void init(String conf) {
		// TODO Auto-generated method stub

	}

}
