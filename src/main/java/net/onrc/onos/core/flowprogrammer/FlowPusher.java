package net.onrc.onos.core.flowprogrammer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.OFMessageFuture;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;
import net.onrc.onos.core.util.FlowEntry;
import net.onrc.onos.core.util.FlowEntryAction;
import net.onrc.onos.core.util.FlowEntryAction.ActionEnqueue;
import net.onrc.onos.core.util.FlowEntryAction.ActionOutput;
import net.onrc.onos.core.util.FlowEntryAction.ActionSetEthernetAddr;
import net.onrc.onos.core.util.FlowEntryAction.ActionSetIPv4Addr;
import net.onrc.onos.core.util.FlowEntryAction.ActionSetIpToS;
import net.onrc.onos.core.util.FlowEntryAction.ActionSetTcpUdpPort;
import net.onrc.onos.core.util.FlowEntryAction.ActionSetVlanId;
import net.onrc.onos.core.util.FlowEntryAction.ActionSetVlanPriority;
import net.onrc.onos.core.util.FlowEntryAction.ActionStripVlan;
import net.onrc.onos.core.util.FlowEntryActions;
import net.onrc.onos.core.util.FlowEntryMatch;
import net.onrc.onos.core.util.FlowEntryUserState;
import net.onrc.onos.core.util.IPv4Net;
import net.onrc.onos.core.util.Pair;
import net.onrc.onos.core.util.Port;

import org.openflow.protocol.OFBarrierReply;
import org.openflow.protocol.OFBarrierRequest;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionDataLayerSource;
import org.openflow.protocol.action.OFActionEnqueue;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerSource;
import org.openflow.protocol.action.OFActionNetworkTypeOfService;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionStripVirtualLan;
import org.openflow.protocol.action.OFActionTransportLayerDestination;
import org.openflow.protocol.action.OFActionTransportLayerSource;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;
import org.openflow.protocol.action.OFActionVirtualLanPriorityCodePoint;
import org.openflow.protocol.factory.BasicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlowPusher is a implementation of FlowPusherService.
 * FlowPusher assigns one message queue instance for each one switch.
 * Number of message processing threads is configurable by constructor, and
 * one thread can handle multiple message queues. Each queue will be assigned to
 * a thread according to hash function defined by getHash().
 * Each processing thread reads messages from queues and sends it to switches
 * in round-robin. Processing thread also calculates rate of sending to suppress
 * excessive message sending.
 *
 * @author Naoki Shiota
 */
public class FlowPusher implements IFlowPusherService, IOFMessageListener {
    private final static Logger log = LoggerFactory.getLogger(FlowPusher.class);
    protected static final int DEFAULT_NUMBER_THREAD = 1;

    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    protected static final int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static final int OFMESSAGE_DAMPER_TIMEOUT = 250;    // ms

    // Number of messages sent to switch at once
    protected static final int MAX_MESSAGE_SEND = 100;

    private static class SwitchQueueEntry {
        OFMessage msg;

        public SwitchQueueEntry(OFMessage msg) {
            this.msg = msg;
        }

        public OFMessage getOFMessage() {
            return msg;
        }
    }

    /**
     * SwitchQueue represents message queue attached to a switch.
     * This consists of queue itself and variables used for limiting sending rate.
     *
     * @author Naoki Shiota
     */
    private class SwitchQueue {
        List<Queue<SwitchQueueEntry>> raw_queues;
        QueueState state;

        // Max rate of sending message (bytes/ms). 0 implies no limitation.
        long max_rate = 0;    // 0 indicates no limitation
        long last_sent_time = 0;
        long last_sent_size = 0;

        // "To be deleted" flag
        boolean toBeDeleted = false;

        SwitchQueue() {
            raw_queues = new ArrayList<Queue<SwitchQueueEntry>>(
                    MsgPriority.values().length);
            for (int i = 0; i < MsgPriority.values().length; ++i) {
                raw_queues.add(i, new ArrayDeque<SwitchQueueEntry>());
            }

            state = QueueState.READY;
        }

        /**
         * Check if sending rate is within the rate
         *
         * @param current Current time
         * @return true if within the rate
         */
        boolean isSendable(long current) {
            if (max_rate == 0) {
                // no limitation
                return true;
            }

            if (current == last_sent_time) {
                return false;
            }

            // Check if sufficient time (from aspect of rate) elapsed or not.
            long rate = last_sent_size / (current - last_sent_time);
            return (rate < max_rate);
        }

        /**
         * Log time and size of last sent data.
         *
         * @param current Time to be sent.
         * @param size    Size of sent data (in bytes).
         */
        void logSentData(long current, long size) {
            last_sent_time = current;
            last_sent_size = size;
        }

        boolean add(SwitchQueueEntry entry, MsgPriority priority) {
            Queue<SwitchQueueEntry> queue = getQueue(priority);
            if (queue == null) {
                log.error("Unexpected priority : ", priority);
                return false;
            }
            return queue.add(entry);
        }

        /**
         * Poll single appropriate entry object according to QueueState.
         *
         * @return Entry object.
         */
        SwitchQueueEntry poll() {
            switch (state) {
                case READY: {
                    for (int i = 0; i < raw_queues.size(); ++i) {
                        SwitchQueueEntry entry = raw_queues.get(i).poll();
                        if (entry != null) {
                            return entry;
                        }
                    }

                    return null;
                }
                case SUSPENDED: {
                    // Only polling from high priority queue
                    SwitchQueueEntry entry = getQueue(MsgPriority.HIGH).poll();
                    return entry;
                }
                default:
                    log.error("Unexpected QueueState : ", state);
                    return null;
            }
        }

        /**
         * Check if this object has any messages in the queues to be sent
         *
         * @return True if there are some messages to be sent.
         */
        boolean hasMessageToSend() {
            switch (state) {
                case READY:
                    for (Queue<SwitchQueueEntry> queue : raw_queues) {
                        if (!queue.isEmpty()) {
                            return true;
                        }
                    }
                    break;
                case SUSPENDED:
                    // Only checking high priority queue
                    return (!getQueue(MsgPriority.HIGH).isEmpty());
                default:
                    log.error("Unexpected QueueState : ", state);
                    return false;
            }

            return false;
        }

        Queue<SwitchQueueEntry> getQueue(MsgPriority priority) {
            return raw_queues.get(priority.ordinal());
        }
    }

    /**
     * BarrierInfo holds information to specify barrier message sent to switch.
     *
     * @author Naoki
     */
    private static class BarrierInfo {
        final long dpid;
        final int xid;

        static BarrierInfo create(IOFSwitch sw, OFBarrierRequest req) {
            return new BarrierInfo(sw.getId(), req.getXid());
        }

        static BarrierInfo create(IOFSwitch sw, OFBarrierReply rpy) {
            return new BarrierInfo(sw.getId(), rpy.getXid());
        }

        private BarrierInfo(long dpid, int xid) {
            this.dpid = dpid;
            this.xid = xid;
        }

        // Auto generated code by Eclipse
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (dpid ^ (dpid >>> 32));
            result = prime * result + xid;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;

            BarrierInfo other = (BarrierInfo) obj;
            return (this.dpid == other.dpid) && (this.xid == other.xid);
        }


    }

    private OFMessageDamper messageDamper = null;
    private IThreadPoolService threadPool = null;

    private FloodlightContext context = null;
    private BasicFactory factory = null;

    // Map of threads versus dpid
    private Map<Long, FlowPusherThread> threadMap = null;
    // Map from (DPID and transaction ID) to Future objects.
    private Map<BarrierInfo, OFBarrierReplyFuture> barrierFutures
            = new ConcurrentHashMap<BarrierInfo, OFBarrierReplyFuture>();

    private int number_thread;

    /**
     * Main thread that reads messages from queues and sends them to switches.
     *
     * @author Naoki Shiota
     */
    private class FlowPusherThread extends Thread {
        private Map<IOFSwitch, SwitchQueue> assignedQueues
                = new ConcurrentHashMap<IOFSwitch, SwitchQueue>();

        final Lock queuingLock = new ReentrantLock();
        final Condition messagePushed = queuingLock.newCondition();

        @Override
        public void run() {
            this.setName("FlowPusherThread " + this.getId());
            while (true) {
                while (!queuesHasMessageToSend()) {
                    queuingLock.lock();

                    try {
                        // wait for message pushed to queue
                        messagePushed.await();
                    } catch (InterruptedException e) {
                        // Interrupted to be shut down (not an error)
                        log.debug("FlowPusherThread is interrupted");
                        return;
                    } finally {
                        queuingLock.unlock();
                    }
                }

                // for safety of concurrent access, copy set of key objects
                Set<IOFSwitch> keys = new HashSet<IOFSwitch>(assignedQueues.size());
                for (IOFSwitch sw : assignedQueues.keySet()) {
                    keys.add(sw);
                }

                for (IOFSwitch sw : keys) {
                    SwitchQueue queue = assignedQueues.get(sw);

                    if (sw == null || queue == null) {
                        continue;
                    }

                    synchronized (queue) {
                        processQueue(sw, queue, MAX_MESSAGE_SEND);
                        if (queue.toBeDeleted && !queue.hasMessageToSend()) {
                            // remove queue if flagged to be.
                            assignedQueues.remove(sw);
                        }
                    }
                }
            }
        }

        /**
         * Read messages from queue and send them to the switch.
         * If number of messages excess the limit, stop sending messages.
         *
         * @param sw      Switch to which messages will be sent.
         * @param queue   Queue of messages.
         * @param max_msg Limitation of number of messages to be sent. If set to 0,
         *                all messages in queue will be sent.
         */
        private void processQueue(IOFSwitch sw, SwitchQueue queue, int max_msg) {
            // check sending rate and determine it to be sent or not
            long current_time = System.currentTimeMillis();
            long size = 0;

            if (queue.isSendable(current_time)) {
                int i = 0;
                while (queue.hasMessageToSend()) {
                    // Number of messages excess the limit
                    if (0 < max_msg && max_msg <= i) {
                        break;
                    }
                    ++i;

                    SwitchQueueEntry queueEntry;
                    synchronized (queue) {
                        queueEntry = queue.poll();
                    }

                    OFMessage msg = queueEntry.getOFMessage();
                    try {
                        messageDamper.write(sw, msg, context);
                        if (log.isTraceEnabled()) {
                            log.trace("Pusher sends message : {}", msg);
                        }
                        size += msg.getLength();
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error("Exception in sending message ({}) : {}", msg, e);
                    }
                }

                sw.flush();
                queue.logSentData(current_time, size);
            }
        }

        private boolean queuesHasMessageToSend() {
            for (SwitchQueue queue : assignedQueues.values()) {
                if (queue.hasMessageToSend()) {
                    return true;
                }
            }

            return false;
        }

        private void notifyMessagePushed() {
            queuingLock.lock();
            try {
                messagePushed.signal();
            } finally {
                queuingLock.unlock();
            }
        }
    }

    /**
     * Initialize object with one thread.
     */
    public FlowPusher() {
        number_thread = DEFAULT_NUMBER_THREAD;
    }

    /**
     * Initialize object with threads of given number.
     *
     * @param number_thread Number of threads to handle messages.
     */
    public FlowPusher(int number_thread) {
        if (number_thread > 0) {
            this.number_thread = number_thread;
        } else {
            this.number_thread = DEFAULT_NUMBER_THREAD;
        }
    }

    /**
     * Set parameters needed for sending messages.
     *
     * @param context    FloodlightContext used for sending messages.
     *                   If null, FlowPusher uses default context.
     * @param modContext FloodlightModuleContext used for acquiring
     *                   ThreadPoolService and registering MessageListener.
     * @param factory    Factory object to create OFMessage objects.
     * @param damper     Message damper used for sending messages.
     *                   If null, FlowPusher creates its own damper object.
     */
    public void init(FloodlightContext context,
                     FloodlightModuleContext modContext,
                     BasicFactory factory,
                     OFMessageDamper damper) {
        this.context = context;
        this.factory = factory;
        this.threadPool = modContext.getServiceImpl(IThreadPoolService.class);
        IFloodlightProviderService flservice
                = modContext.getServiceImpl(IFloodlightProviderService.class);
        flservice.addOFMessageListener(OFType.BARRIER_REPLY, this);

        if (damper != null) {
            messageDamper = damper;
        } else {
            // use default values
            messageDamper = new OFMessageDamper(OFMESSAGE_DAMPER_CAPACITY,
                    EnumSet.of(OFType.FLOW_MOD),
                    OFMESSAGE_DAMPER_TIMEOUT);
        }
    }

    /**
     * Begin processing queue.
     */
    public void start() {
        if (factory == null) {
            log.error("FlowPusher not yet initialized.");
            return;
        }

        threadMap = new HashMap<Long, FlowPusherThread>();
        for (long i = 0; i < number_thread; ++i) {
            FlowPusherThread thread = new FlowPusherThread();

            threadMap.put(i, thread);
            thread.start();
        }
    }

    @Override
    public boolean suspend(IOFSwitch sw) {
        SwitchQueue queue = getQueue(sw);

        if (queue == null) {
            // create queue in case suspend is called before first message addition
            queue = createQueueImpl(sw);
        }

        synchronized (queue) {
            if (queue.state == QueueState.READY) {
                queue.state = QueueState.SUSPENDED;
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean resume(IOFSwitch sw) {
        SwitchQueue queue = getQueue(sw);

        if (queue == null) {
            log.error("No queue is attached to DPID : {}", sw.getId());
            return false;
        }

        synchronized (queue) {
            if (queue.state == QueueState.SUSPENDED) {
                queue.state = QueueState.READY;

                // Free the latch if queue has any messages
                FlowPusherThread thread = getProcessingThread(sw);
                if (queue.hasMessageToSend()) {
                    thread.notifyMessagePushed();
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public QueueState getState(IOFSwitch sw) {
        SwitchQueue queue = getQueue(sw);

        if (queue == null) {
            return QueueState.UNKNOWN;
        }

        return queue.state;
    }

    /**
     * Stop processing queue and exit thread.
     */
    public void stop() {
        if (threadMap == null) {
            return;
        }

        for (FlowPusherThread t : threadMap.values()) {
            t.interrupt();
        }
    }

    @Override
    public void setRate(IOFSwitch sw, long rate) {
        SwitchQueue queue = getQueue(sw);
        if (queue == null) {
            queue = createQueueImpl(sw);
        }

        if (rate > 0) {
            log.debug("rate for {} is set to {}", sw.getId(), rate);
            synchronized (queue) {
                queue.max_rate = rate;
            }
        }
    }

    @Override
    public boolean createQueue(IOFSwitch sw) {
        SwitchQueue queue = createQueueImpl(sw);

        return (queue != null);
    }

    protected SwitchQueue createQueueImpl(IOFSwitch sw) {
        SwitchQueue queue = getQueue(sw);
        if (queue != null) {
            return queue;
        }

        FlowPusherThread proc = getProcessingThread(sw);
        queue = new SwitchQueue();
        queue.state = QueueState.READY;
        proc.assignedQueues.put(sw, queue);

        return queue;
    }

    @Override
    public boolean deleteQueue(IOFSwitch sw) {
        return deleteQueue(sw, false);
    }

    @Override
    public boolean deleteQueue(IOFSwitch sw, boolean forceStop) {
        FlowPusherThread proc = getProcessingThread(sw);

        if (forceStop) {
            SwitchQueue queue = proc.assignedQueues.remove(sw);
            if (queue == null) {
                return false;
            }
            return true;
        } else {
            SwitchQueue queue = getQueue(sw);
            if (queue == null) {
                return false;
            }
            synchronized (queue) {
                queue.toBeDeleted = true;
            }
            return true;
        }
    }

    @Override
    public boolean add(IOFSwitch sw, OFMessage msg) {
        return add(sw, msg, MsgPriority.NORMAL);
    }

    @Override
    public boolean add(IOFSwitch sw, OFMessage msg, MsgPriority priority) {
        return addMessageImpl(sw, msg, priority);
    }

    @Override
    public void pushFlowEntries(
            Collection<Pair<IOFSwitch, FlowEntry>> entries) {
        pushFlowEntries(entries, MsgPriority.NORMAL);
    }

    @Override
    public void pushFlowEntries(
            Collection<Pair<IOFSwitch, FlowEntry>> entries, MsgPriority priority) {

        for (Pair<IOFSwitch, FlowEntry> entry : entries) {
            add(entry.first, entry.second, priority);
        }
    }

    @Override
    public void pushFlowEntry(IOFSwitch sw, FlowEntry flowEntry) {
        pushFlowEntry(sw, flowEntry, MsgPriority.NORMAL);
    }

    @Override
    public void pushFlowEntry(IOFSwitch sw, FlowEntry flowEntry, MsgPriority priority) {
        Collection<Pair<IOFSwitch, FlowEntry>> entries =
                new LinkedList<Pair<IOFSwitch, FlowEntry>>();

        entries.add(new Pair<IOFSwitch, FlowEntry>(sw, flowEntry));
        pushFlowEntries(entries, priority);
    }

    /**
     * Create a message from FlowEntry and add it to the queue of the switch.
     *
     * @param sw        Switch to which message is pushed.
     * @param flowEntry FlowEntry object used for creating message.
     * @return true if message is successfully added to a queue.
     */
    private boolean add(IOFSwitch sw, FlowEntry flowEntry, MsgPriority priority) {
        //
        // Create the OpenFlow Flow Modification Entry to push
        //
        OFFlowMod fm = (OFFlowMod) factory.getMessage(OFType.FLOW_MOD);
        long cookie = flowEntry.flowEntryId().value();

        short flowModCommand = OFFlowMod.OFPFC_ADD;
        if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_ADD) {
            flowModCommand = OFFlowMod.OFPFC_ADD;
        } else if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_MODIFY) {
            flowModCommand = OFFlowMod.OFPFC_MODIFY_STRICT;
        } else if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_DELETE) {
            flowModCommand = OFFlowMod.OFPFC_DELETE_STRICT;
        } else {
            // Unknown user state. Ignore the entry
            log.debug(
                    "Flow Entry ignored (FlowEntryId = {}): unknown user state {}",
                    flowEntry.flowEntryId(),
                    flowEntry.flowEntryUserState());
            return false;
        }

        //
        // Fetch the match conditions.
        //
        // NOTE: The Flow matching conditions common for all Flow Entries are
        // used ONLY if a Flow Entry does NOT have the corresponding matching
        // condition set.
        //
        OFMatch match = new OFMatch();
        match.setWildcards(OFMatch.OFPFW_ALL);
        FlowEntryMatch flowEntryMatch = flowEntry.flowEntryMatch();

        // Match the Incoming Port
        Port matchInPort = flowEntryMatch.inPort();
        if (matchInPort != null) {
            match.setInputPort(matchInPort.value());
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
        }

        // Match the Source MAC address
        MACAddress matchSrcMac = flowEntryMatch.srcMac();
        if (matchSrcMac != null) {
            match.setDataLayerSource(matchSrcMac.toString());
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
        }

        // Match the Destination MAC address
        MACAddress matchDstMac = flowEntryMatch.dstMac();
        if (matchDstMac != null) {
            match.setDataLayerDestination(matchDstMac.toString());
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
        }

        // Match the Ethernet Frame Type
        Short matchEthernetFrameType = flowEntryMatch.ethernetFrameType();
        if (matchEthernetFrameType != null) {
            match.setDataLayerType(matchEthernetFrameType);
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
        }

        // Match the VLAN ID
        Short matchVlanId = flowEntryMatch.vlanId();
        if (matchVlanId != null) {
            match.setDataLayerVirtualLan(matchVlanId);
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN);
        }

        // Match the VLAN priority
        Byte matchVlanPriority = flowEntryMatch.vlanPriority();
        if (matchVlanPriority != null) {
            match.setDataLayerVirtualLanPriorityCodePoint(matchVlanPriority);
            match.setWildcards(match.getWildcards()
                    & ~OFMatch.OFPFW_DL_VLAN_PCP);
        }

        // Match the Source IPv4 Network prefix
        IPv4Net matchSrcIPv4Net = flowEntryMatch.srcIPv4Net();
        if (matchSrcIPv4Net != null) {
            match.setFromCIDR(matchSrcIPv4Net.toString(), OFMatch.STR_NW_SRC);
        }

        // Natch the Destination IPv4 Network prefix
        IPv4Net matchDstIPv4Net = flowEntryMatch.dstIPv4Net();
        if (matchDstIPv4Net != null) {
            match.setFromCIDR(matchDstIPv4Net.toString(), OFMatch.STR_NW_DST);
        }

        // Match the IP protocol
        Byte matchIpProto = flowEntryMatch.ipProto();
        if (matchIpProto != null) {
            match.setNetworkProtocol(matchIpProto);
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_PROTO);
        }

        // Match the IP ToS (DSCP field, 6 bits)
        Byte matchIpToS = flowEntryMatch.ipToS();
        if (matchIpToS != null) {
            match.setNetworkTypeOfService(matchIpToS);
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_TOS);
        }

        // Match the Source TCP/UDP port
        Short matchSrcTcpUdpPort = flowEntryMatch.srcTcpUdpPort();
        if (matchSrcTcpUdpPort != null) {
            match.setTransportSource(matchSrcTcpUdpPort);
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
        }

        // Match the Destination TCP/UDP port
        Short matchDstTcpUdpPort = flowEntryMatch.dstTcpUdpPort();
        if (matchDstTcpUdpPort != null) {
            match.setTransportDestination(matchDstTcpUdpPort);
            match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_DST);
        }

        //
        // Fetch the actions
        //
        Short actionOutputPort = null;
        List<OFAction> openFlowActions = new ArrayList<OFAction>();
        int actionsLen = 0;
        FlowEntryActions flowEntryActions = flowEntry.flowEntryActions();
        //
        for (FlowEntryAction action : flowEntryActions.actions()) {
            ActionOutput actionOutput = action.actionOutput();
            ActionSetVlanId actionSetVlanId = action.actionSetVlanId();
            ActionSetVlanPriority actionSetVlanPriority = action
                    .actionSetVlanPriority();
            ActionStripVlan actionStripVlan = action.actionStripVlan();
            ActionSetEthernetAddr actionSetEthernetSrcAddr = action
                    .actionSetEthernetSrcAddr();
            ActionSetEthernetAddr actionSetEthernetDstAddr = action
                    .actionSetEthernetDstAddr();
            ActionSetIPv4Addr actionSetIPv4SrcAddr = action
                    .actionSetIPv4SrcAddr();
            ActionSetIPv4Addr actionSetIPv4DstAddr = action
                    .actionSetIPv4DstAddr();
            ActionSetIpToS actionSetIpToS = action.actionSetIpToS();
            ActionSetTcpUdpPort actionSetTcpUdpSrcPort = action
                    .actionSetTcpUdpSrcPort();
            ActionSetTcpUdpPort actionSetTcpUdpDstPort = action
                    .actionSetTcpUdpDstPort();
            ActionEnqueue actionEnqueue = action.actionEnqueue();

            if (actionOutput != null) {
                actionOutputPort = actionOutput.port().value();
                // XXX: The max length is hard-coded for now
                OFActionOutput ofa = new OFActionOutput(actionOutput.port()
                        .value(), (short) 0xffff);
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetVlanId != null) {
                OFActionVirtualLanIdentifier ofa = new OFActionVirtualLanIdentifier(
                        actionSetVlanId.vlanId());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetVlanPriority != null) {
                OFActionVirtualLanPriorityCodePoint ofa = new OFActionVirtualLanPriorityCodePoint(
                        actionSetVlanPriority.vlanPriority());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionStripVlan != null) {
                if (actionStripVlan.stripVlan() == true) {
                    OFActionStripVirtualLan ofa = new OFActionStripVirtualLan();
                    openFlowActions.add(ofa);
                    actionsLen += ofa.getLength();
                }
            }

            if (actionSetEthernetSrcAddr != null) {
                OFActionDataLayerSource ofa = new OFActionDataLayerSource(
                        actionSetEthernetSrcAddr.addr().toBytes());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetEthernetDstAddr != null) {
                OFActionDataLayerDestination ofa = new OFActionDataLayerDestination(
                        actionSetEthernetDstAddr.addr().toBytes());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetIPv4SrcAddr != null) {
                OFActionNetworkLayerSource ofa = new OFActionNetworkLayerSource(
                        actionSetIPv4SrcAddr.addr().value());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetIPv4DstAddr != null) {
                OFActionNetworkLayerDestination ofa = new OFActionNetworkLayerDestination(
                        actionSetIPv4DstAddr.addr().value());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetIpToS != null) {
                OFActionNetworkTypeOfService ofa = new OFActionNetworkTypeOfService(
                        actionSetIpToS.ipToS());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetTcpUdpSrcPort != null) {
                OFActionTransportLayerSource ofa = new OFActionTransportLayerSource(
                        actionSetTcpUdpSrcPort.port());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionSetTcpUdpDstPort != null) {
                OFActionTransportLayerDestination ofa = new OFActionTransportLayerDestination(
                        actionSetTcpUdpDstPort.port());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }

            if (actionEnqueue != null) {
                OFActionEnqueue ofa = new OFActionEnqueue(actionEnqueue.port()
                        .value(), actionEnqueue.queueId());
                openFlowActions.add(ofa);
                actionsLen += ofa.getLength();
            }
        }

        fm.setIdleTimeout((short) flowEntry.idleTimeout())
                .setHardTimeout((short) flowEntry.hardTimeout())
                .setPriority((short) flowEntry.priority())
                .setBufferId(OFPacketOut.BUFFER_ID_NONE).setCookie(cookie)
                .setCommand(flowModCommand).setMatch(match)
                .setActions(openFlowActions)
                .setLengthU(OFFlowMod.MINIMUM_LENGTH + actionsLen);
        fm.setOutPort(OFPort.OFPP_NONE.getValue());
        if ((flowModCommand == OFFlowMod.OFPFC_DELETE)
                || (flowModCommand == OFFlowMod.OFPFC_DELETE_STRICT)) {
            if (actionOutputPort != null)
                fm.setOutPort(actionOutputPort);
        }

        //
        // Set the OFPFF_SEND_FLOW_REM flag if the Flow Entry is not
        // permanent.
        //
        if ((flowEntry.idleTimeout() != 0) ||
                (flowEntry.hardTimeout() != 0)) {
            fm.setFlags(OFFlowMod.OFPFF_SEND_FLOW_REM);
        }

        if (log.isTraceEnabled()) {
            log.trace("Installing flow entry {} into switch DPID: {} flowEntryId: {} srcMac: {} dstMac: {} inPort: {} outPort: {}"
                    , flowEntry.flowEntryUserState()
                    , sw.getStringId()
                    , flowEntry.flowEntryId()
                    , matchSrcMac
                    , matchDstMac
                    , matchInPort
                    , actionOutputPort
            );
        }

        return addMessageImpl(sw, fm, priority);
    }

    /**
     * Add message to queue
     *
     * @param sw
     * @param msg
     * @param flowEntryId
     * @return
     */
    protected boolean addMessageImpl(IOFSwitch sw, OFMessage msg, MsgPriority priority) {
        FlowPusherThread thread = getProcessingThread(sw);

        SwitchQueue queue = getQueue(sw);

        // create queue at first addition of message
        if (queue == null) {
            queue = createQueueImpl(sw);
        }

        SwitchQueueEntry entry = new SwitchQueueEntry(msg);

        synchronized (queue) {
            queue.add(entry, priority);
            if (log.isTraceEnabled()) {
                log.trace("Message is pushed : {}", entry.getOFMessage());
            }
        }

        thread.notifyMessagePushed();

        return true;
    }

    @Override
    public OFBarrierReply barrier(IOFSwitch sw) {
        OFMessageFuture<OFBarrierReply> future = barrierAsync(sw);
        if (future == null) {
            return null;
        }

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("InterruptedException: {}", e);
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.error("ExecutionException: {}", e);
            return null;
        }
    }

    @Override
    public OFBarrierReplyFuture barrierAsync(IOFSwitch sw) {
        // TODO creation of message and future should be moved to OFSwitchImpl

        if (sw == null) {
            return null;
        }

        OFBarrierRequest msg = createBarrierRequest(sw);

        OFBarrierReplyFuture future = new OFBarrierReplyFuture(threadPool, sw, msg.getXid());
        barrierFutures.put(BarrierInfo.create(sw, msg), future);

        addMessageImpl(sw, msg, MsgPriority.NORMAL);

        return future;
    }

    protected OFBarrierRequest createBarrierRequest(IOFSwitch sw) {
        OFBarrierRequest msg = (OFBarrierRequest) factory.getMessage(OFType.BARRIER_REQUEST);
        msg.setXid(sw.getNextTransactionId());

        return msg;
    }

    /**
     * Get a queue attached to a switch.
     *
     * @param sw Switch object
     * @return Queue object
     */
    protected SwitchQueue getQueue(IOFSwitch sw) {
        if (sw == null) {
            return null;
        }

        FlowPusherThread th = getProcessingThread(sw);
        if (th == null) {
            return null;
        }

        return th.assignedQueues.get(sw);
    }

    /**
     * Get a hash value correspondent to a switch.
     *
     * @param sw Switch object
     * @return Hash value
     */
    protected long getHash(IOFSwitch sw) {
        // This code assumes DPID is sequentially assigned.
        // TODO consider equalization algorithm
        return sw.getId() % number_thread;
    }

    /**
     * Get a Thread object which processes the queue attached to a switch.
     *
     * @param sw Switch object
     * @return Thread object
     */
    protected FlowPusherThread getProcessingThread(IOFSwitch sw) {
        long hash = getHash(sw);

        return threadMap.get(hash);
    }

    @Override
    public String getName() {
        return "flowpusher";
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        if (log.isTraceEnabled()) {
            log.trace("Received BARRIER_REPLY from : {}", sw.getId());
        }

        if (msg.getType() != OFType.BARRIER_REPLY) {
            log.error("Unexpected reply message : {}", msg.getType());
            return Command.CONTINUE;
        }

        OFBarrierReply reply = (OFBarrierReply) msg;
        BarrierInfo info = BarrierInfo.create(sw, reply);

        // Deliver future if exists
        OFBarrierReplyFuture future = barrierFutures.get(info);
        if (future != null) {
            future.deliverFuture(sw, msg);
            barrierFutures.remove(info);
        }

        return Command.CONTINUE;
    }
}
