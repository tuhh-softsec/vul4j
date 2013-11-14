package net.onrc.onos.ofcontroller.flowprogrammer;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.IFloodlightService;

import org.openflow.protocol.OFMessage;

public interface IFlowPusherService extends IFloodlightService {
	/**
	 * Add a message to the queue of a switch.
	 * @param sw
	 * @param msg
	 * @return
	 */
	void addMessage(long dpid, OFMessage msg);
	
	/**
	 * Suspend pushing message to a switch.
	 * @param sw
	 * @return true if success
	 */
	boolean suspend(long dpid);
	
	/**
	 * Resume pushing message to a switch.
	 * @param sw
	 * @return true if success
	 */
	boolean resume(long dpid);
	
	/**
	 * Get whether pushing of message is suspended or not.
	 * @param sw
	 * @return true if suspended
	 */
	boolean isSuspended(long dpid);
}
