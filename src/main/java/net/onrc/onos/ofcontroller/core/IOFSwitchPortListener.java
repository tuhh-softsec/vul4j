/**
 * 
 */
package net.onrc.onos.ofcontroller.core;

import org.openflow.protocol.OFPhysicalPort;

import net.floodlightcontroller.core.IOFSwitchListener;

/**
 * @author y-higuchi
 *
 */
public interface IOFSwitchPortListener extends IOFSwitchListener {

	/**
	 * Fired when ports on a switch area added
	 */
	public void switchPortAdded(Long switchId, OFPhysicalPort port);

	/**
	 * Fired when ports on a switch area removed
	 */
	public void switchPortRemoved(Long switchId, OFPhysicalPort port);

}
