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
	public void update(long dpid, DM_OPERATION op) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#addPort(long, org.openflow.protocol.OFPhysicalPort)
	 */
	@Override
	public void addPort(long dpid, OFPhysicalPort port) {
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
	public OFPhysicalPort getPort(long dpid, short portnum) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#getPort(long, java.lang.String)
	 */
	@Override
	public OFPhysicalPort getPort(long dpid, String portName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#addSwitch(long)
	 */
	@Override
	public void addSwitch(long dpid) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#deleteSwitch(long)
	 */
	@Override
	public void deleteSwitch(long dpid) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#deletePort(long, short)
	 */
	@Override
	public void deletePort(long dpid, short port) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.ISwitchStorage#deletePort(long, java.lang.String)
	 */
	@Override
	public void deletePort(long dpid, String portName) {
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
