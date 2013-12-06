package net.onrc.onos.ofcontroller.flowprogrammer;

import java.util.Collection;

import org.openflow.protocol.OFBarrierReply;
import org.openflow.protocol.OFMessage;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.OFMessageFuture;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.Pair;

/**
 * FlowPusherService is a service to send message to switches in proper rate.
 * Conceptually a queue is attached to each switch, and FlowPusherService
 * read a message from queue and send it to switch in order.
 * To guarantee message has been installed, FlowPusherService can add barrier
 * message to queue and can notify when barrier message is sent to switch.
 * @author Naoki Shiota
 *
 */
public interface IFlowPusherService extends IFloodlightService {
	/**
	 * Create a queue correspondent to the switch.
	 * @param sw Switch to which new queue is attached.
	 * @return true if new queue is successfully created.
	 */
	boolean createQueue(IOFSwitch sw);

	/**
	 * Delete a queue correspondent to the switch.
	 * Messages remains in queue will be all sent before queue is deleted.
	 * @param sw Switch of which queue is deleted.
	 * @return true if queue is successfully deleted.
	 */
	boolean deleteQueue(IOFSwitch sw);
	
	/**
	 * Delete a queue correspondent to the switch.
	 * By setting force flag on, queue will be deleted immediately.
	 * @param sw Switch of which queue is deleted.
	 * @param forceStop If this flag is set to true, queue will be deleted
	 *        immediately regardless of any messages in the queue.
	 *        If false, all messages will be sent to switch and queue will
	 *        be deleted after that.
	 * @return true if queue is successfully deleted or flagged to be deleted.
	 */
	boolean deleteQueue(IOFSwitch sw, boolean forceStop);
	
	/**
	 * Add a message to the queue of the switch.
	 *
	 * Note: Notification is NOT delivered for the pushed message.
	 *
	 * @param sw Switch to which message is pushed.
	 * @param msg Message object to be added.
	 * @return true if message is successfully added to a queue.
	 */
	boolean add(IOFSwitch sw, OFMessage msg);

	/**
	 * Push a collection of Flow Entries to the corresponding switches.
	 *
	 * Note: Notification is delivered for the Flow Entries that
	 * are pushed successfully.
	 *
	 * @param entries the collection of <IOFSwitch, FlowEntry> pairs
	 * to push.
	 */
	void pushFlowEntries(Collection<Pair<IOFSwitch, FlowEntry>> entries);

	/**
	 * Create a message from FlowEntry and add it to the queue of the
	 * switch.
	 *
	 * Note: Notification is delivered for the Flow Entries that
	 * are pushed successfully.
	 *
	 * @param sw Switch to which message is pushed.
	 * @param flowEntry FlowEntry object used for creating message.
	 * @return true if message is successfully added to a queue.
	 */
	void pushFlowEntry(IOFSwitch sw, FlowEntry flowEntry);

	/**
	 * Set sending rate to a switch.
	 * @param sw Switch.
	 * @param rate Rate in bytes/ms.
	 */
	public void setRate(IOFSwitch sw, long rate);
	
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
