package net.onrc.onos.ofcontroller.flowprogrammer;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.openflow.protocol.OFMessage;

public interface IFlowPusherService extends IFloodlightService {
	/**
	 * Add a message to the queue of a switch.
	 * @param sw
	 * @param msg
	 * @return
	 */
	boolean add(IOFSwitch sw, OFMessage msg);
	boolean add(IOFSwitch sw, FlowPath flowPath, FlowEntry flowEntry);
	boolean add(IOFSwitch sw, IFlowPath flowObj, IFlowEntry flowEntryObj);
	
	/**
	 * Suspend pushing message to a switch.
	 * @param sw
	 * @return true if success
	 */
	boolean suspend(IOFSwitch sw);
	
	/**
	 * Resume pushing message to a switch.
	 * @param sw
	 * @return true if success
	 */
	boolean resume(IOFSwitch sw);
	
	/**
	 * Get whether pushing of message is suspended or not.
	 * @param sw
	 * @return true if suspended
	 */
	boolean isSuspended(IOFSwitch sw);
}
