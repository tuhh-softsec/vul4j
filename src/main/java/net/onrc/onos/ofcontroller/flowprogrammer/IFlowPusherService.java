package net.onrc.onos.ofcontroller.flowprogrammer;

import org.openflow.protocol.OFBarrierReply;
import org.openflow.protocol.OFMessage;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.OFMessageFuture;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowPath;

public interface IFlowPusherService extends IFloodlightService {
	/**
	 * Add a message to the queue of the switch.
	 * @param sw Switch to which message is pushed.
	 * @param msg Message object to be added.
	 * @return true if message is successfully added to a queue.
	 */
	boolean add(IOFSwitch sw, OFMessage msg);

	/**
	 * Create a message from FlowEntry and add it to the queue of the switch.
	 * @param sw Switch to which message is pushed.
	 * @param flowPath FlowPath object used for creating message.
	 * @param flowEntry FlowEntry object used for creating message.
	 * @return true if message is successfully added to a queue.
	 */
	boolean add(IOFSwitch sw, FlowPath flowPath, FlowEntry flowEntry);

	/**
	 * Create a message from IFlowEntry and add it to the queue of the switch.
	 * @param sw Switch to which message is pushed.
	 * @param flowObj IFlowPath object used for creating message.
	 * @param flowEntryObj IFlowEntry object used for creating message.
	 * @return true if message is successfully added to a queue.
	 */
	boolean add(IOFSwitch sw, IFlowPath flowObj, IFlowEntry flowEntryObj);

	/**
	 * Add BARRIER message to queue and wait for reply.
	 * @param sw Switch to which barrier message is pushed.
	 * @return BARRIER_REPLY message sent from switch.
	 */
	OFBarrierReply barrier(IOFSwitch sw);
	
	/**
	 * Add BARRIER message to queue asynchronously.
	 * @param sw Switch to which barrier message is pushed.
	 * @return Future object of BARRIER_REPLY message which will be sent from switch.
	 */
	OFMessageFuture<OFBarrierReply> barrierAsync(IOFSwitch sw);
	
	/**
	 * Suspend pushing message to a switch.
	 * @param sw Switch to be suspended pushing message.
	 * @return true if success
	 */
	boolean suspend(IOFSwitch sw);
	
	/**
	 * Resume pushing message to a switch.
	 * @param sw Switch to be resumed pushing message.
	 * @return true if success
	 */
	boolean resume(IOFSwitch sw);
	
	/**
	 * Get whether pushing of message is suspended or not.
	 * @param sw Switch to be checked.
	 * @return true if suspended.
	 */
	boolean isSuspended(IOFSwitch sw);
}
