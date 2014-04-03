/**
 * 
 */
package net.onrc.onos.core.main;

import net.floodlightcontroller.core.IOFSwitch;

/**
 * @author y-higuchi
 *
 */
public interface IOnosRemoteSwitch extends IOFSwitch {

	/**
	 * Setup an unconnected switch with the info required.
	 * @param dpid of the switch
	 */
	public void setupRemoteSwitch(Long dpid);

}
