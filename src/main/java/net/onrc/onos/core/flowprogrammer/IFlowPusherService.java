package net.onrc.onos.core.flowprogrammer;

import java.util.Collection;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.OFMessageFuture;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.core.util.FlowEntry;
import net.onrc.onos.core.util.Pair;

import org.openflow.protocol.OFBarrierReply;
import org.openflow.protocol.OFMessage;

/**
 * FlowPusherService is a service to send message to switches in proper rate.
 * Conceptually a queue is attached to each switch, and FlowPusherService
 * read a message from queue and send it to switch in order.
 * To guarantee message has been installed, FlowPusherService can add barrier
 * message to queue and can notify when barrier message is sent to switch.
 *
 * @author Naoki Shiota
 */
public interface IFlowPusherService extends IFloodlightService {
    public static enum MsgPriority {
        HIGH,        // High priority: e.g. flow synchronization
        NORMAL,        // Normal priority
//		LOW,		// Low priority, not needed for now
    }

    public static enum QueueState {
        READY,        // Queues with all priority are at work
        SUSPENDED,    // Only prior queue is at work
        UNKNOWN
    }

    /**
     * Create a queue correspondent to the switch.
     *
     * @param sw Switch to which new queue is attached.
     * @return true if new queue is successfully created.
     */
    boolean createQueue(IOFSwitch sw);

    /**
     * Delete a queue correspondent to the switch.
     * Messages remains in queue will be all sent before queue is deleted.
     *
     * @param sw Switch of which queue is deleted.
     * @return true if queue is successfully deleted.
     */
    boolean deleteQueue(IOFSwitch sw);

    /**
     * Delete a queue correspondent to the switch.
     * By setting force flag on, queue will be deleted immediately.
     *
     * @param sw        Switch of which queue is deleted.
     * @param forceStop If this flag is set to true, queue will be deleted
     *                  immediately regardless of any messages in the queue.
     *                  If false, all messages will be sent to switch and queue will
     *                  be deleted after that.
     * @return true if queue is successfully deleted or flagged to be deleted.
     */
    boolean deleteQueue(IOFSwitch sw, boolean forceStop);

    /**
     * Add a message to the queue of the switch with normal priority.
     * <p/>
     * Note: Notification is NOT delivered for the pushed message.
     *
     * @param sw  Switch to which message is pushed.
     * @param msg Message object to be added.
     * @return true if message is successfully added to a queue.
     */
    boolean add(IOFSwitch sw, OFMessage msg);

    /**
     * Add a message to the queue of the switch with specific priority.
     *
     * @param sw       Switch to which message is pushed.
     * @param msg      Message object to be added.
     * @param priority Sending priority of the message.
     * @return true if message is successfully added to a queue.
     */
    boolean add(IOFSwitch sw, OFMessage msg, MsgPriority priority);

    /**
     * Push a collection of Flow Entries to the corresponding switches
     * with normal priority.
     * <p/>
     * Note: Notification is delivered for the Flow Entries that
     * are pushed successfully.
     *
     * @param entries the collection of <IOFSwitch, FlowEntry> pairs
     *                to push.
     */
    void pushFlowEntries(Collection<Pair<IOFSwitch, FlowEntry>> entries);

    /**
     * Push a collection of Flow Entries to the corresponding switches
     * with specific priority.
     * <p/>
     * Note: Notification is delivered for the Flow Entries that
     * are pushed successfully.
     *
     * @param entries  the collection of <IOFSwitch, FlowEntry> pairs
     *                 to push.
     * @param priority Sending priority of flow entries.
     */
    void pushFlowEntries(Collection<Pair<IOFSwitch, FlowEntry>> entries,
                         MsgPriority priority);

    /**
     * Create a message from FlowEntry and add it to the queue of the
     * switch with normal priority.
     * <p/>
     * Note: Notification is delivered for the Flow Entries that
     * are pushed successfully.
     *
     * @param sw        Switch to which message is pushed.
     * @param flowEntry FlowEntry object used for creating message.
     * @return true if message is successfully added to a queue.
     */
    void pushFlowEntry(IOFSwitch sw, FlowEntry flowEntry);

    /**
     * Create a message from FlowEntry and add it to the queue of the
     * switch with specific priority.
     * <p/>
     * Note: Notification is delivered for the Flow Entries that
     * are pushed successfully.
     *
     * @param sw        Switch to which message is pushed.
     * @param flowEntry FlowEntry object used for creating message.
     * @return true if message is successfully added to a queue.
     */
    void pushFlowEntry(IOFSwitch sw, FlowEntry flowEntry,
                       MsgPriority priority);

    /**
     * Set sending rate to a switch.
     *
     * @param sw   Switch.
     * @param rate Rate in bytes/ms.
     */
    public void setRate(IOFSwitch sw, long rate);

    /**
     * Add BARRIER message to queue and wait for reply.
     *
     * @param sw Switch to which barrier message is pushed.
     * @return BARRIER_REPLY message sent from switch.
     */
    OFBarrierReply barrier(IOFSwitch sw);

    /**
     * Add BARRIER message to queue asynchronously.
     *
     * @param sw Switch to which barrier message is pushed.
     * @return Future object of BARRIER_REPLY message which will be sent from switch.
     */
    OFMessageFuture<OFBarrierReply> barrierAsync(IOFSwitch sw);

    /**
     * Suspend pushing message to a switch.
     *
     * @param sw Switch to be suspended pushing message.
     * @return true if success
     */
    boolean suspend(IOFSwitch sw);

    /**
     * Resume pushing message to a switch.
     *
     * @param sw Switch to be resumed pushing message.
     * @return true if success
     */
    boolean resume(IOFSwitch sw);

    /**
     * Get state of queue attached to a switch.
     *
     * @param sw Switch to be checked.
     * @return State of queue.
     */
    QueueState getState(IOFSwitch sw);
}
