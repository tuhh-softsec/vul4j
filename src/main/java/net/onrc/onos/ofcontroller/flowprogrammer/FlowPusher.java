package net.onrc.onos.ofcontroller.flowprogrammer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import org.openflow.protocol.*;
import org.openflow.protocol.action.*;
import org.openflow.protocol.factory.BasicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.OFMessageFuture;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.FlowEntryAction;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.*;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryActions;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;
import net.onrc.onos.ofcontroller.util.FlowEntryUserState;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.IPv4Net;
import net.onrc.onos.ofcontroller.util.Port;

/**
 * FlowPusher is a implementation of FlowPusherService.
 * FlowPusher assigns one message queue instance for each one switch.
 * Number of message processing threads is configurable by constructor, and
 * one thread can handle multiple message queues. Each queue will be assigned to 
 * a thread according to hash function defined by getHash().
 * Each processing thread reads messages from queues and sends it to switches
 * in round-robin. Processing thread also calculates rate of sending to suppress
 * excessive message sending.
 * @author Naoki Shiota
 *
 */
public class FlowPusher implements IFlowPusherService, IOFMessageListener {
    private final static Logger log = LoggerFactory.getLogger(FlowPusher.class);

    // NOTE: Below are moved from FlowManager.
    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    protected static final int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static final int OFMESSAGE_DAMPER_TIMEOUT = 250;	// ms
    
    // Number of messages sent to switch at once
    protected static final int MAX_MESSAGE_SEND = 100;

    public static final short PRIORITY_DEFAULT = 100;
    public static final short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0;	// infinity
    public static final short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;	// infinite

	public enum QueueState {
		READY,
		SUSPENDED,
	}
	
	/**
	 * SwitchQueue represents message queue attached to a switch.
	 * This consists of queue itself and variables used for limiting sending rate.
	 * @author Naoki Shiota
	 *
	 */
	@SuppressWarnings("serial")
	private class SwitchQueue extends ArrayDeque<OFMessage> {
		QueueState state;
		
		// Max rate of sending message (bytes/ms). 0 implies no limitation.
		long max_rate = 0;	// 0 indicates no limitation
		long last_sent_time = 0;
		long last_sent_size = 0;
		
		// "To be deleted" flag
		boolean toBeDeleted = false;
		
		/**
		 * Check if sending rate is within the rate
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
		 * @param current Time to be sent.
		 * @param size Size of sent data (in bytes).
		 */
		void logSentData(long current, long size) {
			last_sent_time = current;
			last_sent_size = size;
		}
		
	}
	
	private OFMessageDamper messageDamper = null;
	private IThreadPoolService threadPool = null;

	private FloodlightContext context = null;
	private BasicFactory factory = null;
	
	// Map of threads versus dpid
	private Map<Long, FlowPusherThread> threadMap = null;
	// Map of Future objects versus dpid and transaction ID.
	private Map<Long, Map<Integer, OFBarrierReplyFuture>>
		barrierFutures = new HashMap<Long, Map<Integer, OFBarrierReplyFuture>>();
	
	private int number_thread = 1;
	
	/**
	 * Main thread that reads messages from queues and sends them to switches.
	 * @author Naoki Shiota
	 *
	 */
	private class FlowPusherThread extends Thread {
		private Map<IOFSwitch,SwitchQueue> queues
			= new HashMap<IOFSwitch,SwitchQueue>();
		
		private Semaphore mutex = new Semaphore(0);
		
		@Override
		public void run() {
			while (true) {
				try {
					// wait for message pushed to queue
					mutex.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
					log.debug("FlowPusherThread is interrupted");
					return;
				}
				
				Set< Map.Entry<IOFSwitch,SwitchQueue> > entries;
				synchronized (queues) {
					entries = queues.entrySet();
				}
				
				for (Map.Entry<IOFSwitch,SwitchQueue> entry : entries) {
					IOFSwitch sw = entry.getKey();
					SwitchQueue queue = entry.getValue();

					// Skip if queue is suspended
					if (sw == null || queue == null ||
							queue.state != QueueState.READY) {
						continue;
					}
					
					// check sending rate and determine it to be sent or not
					long current_time = System.currentTimeMillis();
					long size = 0;
					
					synchronized (queue) {
						if (queue.isSendable(current_time)) {
							int i = 0;
							while (! queue.isEmpty()) {
								// Number of messages excess the limit
								if (i >= MAX_MESSAGE_SEND) {
									// Messages remains in queue
									mutex.release();
									break;
								}
								++i;
								
								OFMessage msg = queue.poll();
								try {
									messageDamper.write(sw, msg, context);
									log.debug("Pusher sends message : {}", msg);
									size += msg.getLength();
								} catch (IOException e) {
									e.printStackTrace();
									log.error("Exception in sending message ({}) : {}", msg, e);
								}
							}
							sw.flush();
							queue.logSentData(current_time, size);
							
							if (queue.isEmpty()) {
								// remove queue if flagged to be.
								if (queue.toBeDeleted) {
									synchronized (queues) {
										queues.remove(sw);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Initialize object with one thread.
	 */
	public FlowPusher() {
	}
	
	/**
	 * Initialize object with threads of given number.
	 * @param number_thread Number of threads to handle messages.
	 */
	public FlowPusher(int number_thread) {
		this.number_thread = number_thread;
	}
	
	/**
	 * Set parameters needed for sending messages.
	 * @param context FloodlightContext used for sending messages.
	 *        If null, FlowPusher uses default context.
	 * @param modContext FloodlightModuleContext used for acquiring
	 *        ThreadPoolService and registering MessageListener.
	 * @param factory Factory object to create OFMessage objects.
	 * @param damper Message damper used for sending messages.
	 *        If null, FlowPusher creates its own damper object.
	 */
	public void init(FloodlightContext context,
			FloodlightModuleContext modContext,
			BasicFactory factory,
			OFMessageDamper damper) {
		this.context = context;
		this.factory = factory;
		this.threadPool = modContext.getServiceImpl(IThreadPoolService.class);
		IFloodlightProviderService flservice = modContext.getServiceImpl(IFloodlightProviderService.class);
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
		
		threadMap = new HashMap<Long,FlowPusherThread>();
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
			return false;
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
			return false;
		}
		
		synchronized (queue) {
			if (queue.state == QueueState.SUSPENDED) {
				queue.state = QueueState.READY;
				return true;
			}
			return false;
		}
	}
	
	@Override
	public boolean isSuspended(IOFSwitch sw) {
		SwitchQueue queue = getQueue(sw);
		
		if (queue == null) {
			// TODO Is true suitable for this case?
			return true;
		}
		
		return (queue.state == QueueState.SUSPENDED);
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
			return;
		}
		
		if (rate > 0) {
			log.debug("rate for {} is set to {}", sw.getId(), rate);
			queue.max_rate = rate;
		}
	}

	@Override
	public boolean createQueue(IOFSwitch sw) {
		SwitchQueue queue = getQueue(sw);
		if (queue != null) {
			return false;
		}
		
		FlowPusherThread proc = getProcess(sw);
		queue = new SwitchQueue();
		queue.state = QueueState.READY;
		synchronized (proc) {
			proc.queues.put(sw, queue);
		}
		
		return true;
	}

	@Override
	public boolean deleteQueue(IOFSwitch sw) {
		return deleteQueue(sw, false);
	}
	
	@Override
	public boolean deleteQueue(IOFSwitch sw, boolean forceStop) {
		FlowPusherThread proc = getProcess(sw);
		
		if (forceStop) {
			synchronized (proc.queues) {
				SwitchQueue queue = proc.queues.remove(sw);
				if (queue == null) {
					return false;
				}
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
		FlowPusherThread proc = getProcess(sw);
		SwitchQueue queue = proc.queues.get(sw);

		// create queue at first addition of message
		if (queue == null) {
			createQueue(sw);
			queue = getQueue(sw);
		}
		
		synchronized (queue) {
			queue.add(msg);
			log.debug("Message is pushed : {}", msg);
		}
		
		if (proc.mutex.availablePermits() == 0) {
			proc.mutex.release();
		}

		return true;
	}
	
	@Override
	public boolean add(IOFSwitch sw, IFlowPath flowObj, IFlowEntry flowEntryObj) {
		log.debug("sending : {}, {}", sw, flowObj);
		String flowEntryIdStr = flowEntryObj.getFlowEntryId();
		if (flowEntryIdStr == null)
		    return false;
		FlowEntryId flowEntryId = new FlowEntryId(flowEntryIdStr);
		String userState = flowEntryObj.getUserState();
		if (userState == null)
		    return false;

		//
		// Create the Open Flow Flow Modification Entry to push
		//
		OFFlowMod fm = (OFFlowMod)factory.getMessage(OFType.FLOW_MOD);
		long cookie = flowEntryId.value();

		short flowModCommand = OFFlowMod.OFPFC_ADD;
		if (userState.equals("FE_USER_ADD")) {
		    flowModCommand = OFFlowMod.OFPFC_ADD;
		} else if (userState.equals("FE_USER_MODIFY")) {
		    flowModCommand = OFFlowMod.OFPFC_MODIFY_STRICT;
		} else if (userState.equals("FE_USER_DELETE")) {
		    flowModCommand = OFFlowMod.OFPFC_DELETE_STRICT;
		} else {
		    // Unknown user state. Ignore the entry
		    log.debug("Flow Entry ignored (FlowEntryId = {}): unknown user state {}",
			      flowEntryId.toString(), userState);
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

		// Match the Incoming Port
		Short matchInPort = flowEntryObj.getMatchInPort();
		if (matchInPort != null) {
		    match.setInputPort(matchInPort);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
		}

		// Match the Source MAC address
		String matchSrcMac = flowEntryObj.getMatchSrcMac();
		if (matchSrcMac == null)
		    matchSrcMac = flowObj.getMatchSrcMac();
		if (matchSrcMac != null) {
		    match.setDataLayerSource(matchSrcMac);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
		}

		// Match the Destination MAC address
		String matchDstMac = flowEntryObj.getMatchDstMac();
		if (matchDstMac == null)
		    matchDstMac = flowObj.getMatchDstMac();
		if (matchDstMac != null) {
		    match.setDataLayerDestination(matchDstMac);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
		}

		// Match the Ethernet Frame Type
		Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
		if (matchEthernetFrameType == null)
		    matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
		if (matchEthernetFrameType != null) {
		    match.setDataLayerType(matchEthernetFrameType);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
		}

		// Match the VLAN ID
		Short matchVlanId = flowEntryObj.getMatchVlanId();
		if (matchVlanId == null)
		    matchVlanId = flowObj.getMatchVlanId();
		if (matchVlanId != null) {
		    match.setDataLayerVirtualLan(matchVlanId);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN);
		}

		// Match the VLAN priority
		Byte matchVlanPriority = flowEntryObj.getMatchVlanPriority();
		if (matchVlanPriority == null)
		    matchVlanPriority = flowObj.getMatchVlanPriority();
		if (matchVlanPriority != null) {
		    match.setDataLayerVirtualLanPriorityCodePoint(matchVlanPriority);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN_PCP);
		}

		// Match the Source IPv4 Network prefix
		String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
		if (matchSrcIPv4Net == null)
		    matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
		if (matchSrcIPv4Net != null) {
		    match.setFromCIDR(matchSrcIPv4Net, OFMatch.STR_NW_SRC);
		}

		// Match the Destination IPv4 Network prefix
		String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
		if (matchDstIPv4Net == null)
		    matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
		if (matchDstIPv4Net != null) {
		    match.setFromCIDR(matchDstIPv4Net, OFMatch.STR_NW_DST);
		}

		// Match the IP protocol
		Byte matchIpProto = flowEntryObj.getMatchIpProto();
		if (matchIpProto == null)
		    matchIpProto = flowObj.getMatchIpProto();
		if (matchIpProto != null) {
		    match.setNetworkProtocol(matchIpProto);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_PROTO);
		}

		// Match the IP ToS (DSCP field, 6 bits)
		Byte matchIpToS = flowEntryObj.getMatchIpToS();
		if (matchIpToS == null)
		    matchIpToS = flowObj.getMatchIpToS();
		if (matchIpToS != null) {
		    match.setNetworkTypeOfService(matchIpToS);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_TOS);
		}

		// Match the Source TCP/UDP port
		Short matchSrcTcpUdpPort = flowEntryObj.getMatchSrcTcpUdpPort();
		if (matchSrcTcpUdpPort == null)
		    matchSrcTcpUdpPort = flowObj.getMatchSrcTcpUdpPort();
		if (matchSrcTcpUdpPort != null) {
		    match.setTransportSource(matchSrcTcpUdpPort);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
		}

		// Match the Destination TCP/UDP port
		Short matchDstTcpUdpPort = flowEntryObj.getMatchDstTcpUdpPort();
		if (matchDstTcpUdpPort == null)
		    matchDstTcpUdpPort = flowObj.getMatchDstTcpUdpPort();
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
		FlowEntryActions flowEntryActions = null;
		String actionsStr = flowEntryObj.getActions();
		if (actionsStr != null)
		    flowEntryActions = new FlowEntryActions(actionsStr);
		else
		    flowEntryActions = new FlowEntryActions();
		for (FlowEntryAction action : flowEntryActions.actions()) {
		    ActionOutput actionOutput = action.actionOutput();
		    ActionSetVlanId actionSetVlanId = action.actionSetVlanId();
		    ActionSetVlanPriority actionSetVlanPriority = action.actionSetVlanPriority();
		    ActionStripVlan actionStripVlan = action.actionStripVlan();
		    ActionSetEthernetAddr actionSetEthernetSrcAddr = action.actionSetEthernetSrcAddr();
		    ActionSetEthernetAddr actionSetEthernetDstAddr = action.actionSetEthernetDstAddr();
		    ActionSetIPv4Addr actionSetIPv4SrcAddr = action.actionSetIPv4SrcAddr();
		    ActionSetIPv4Addr actionSetIPv4DstAddr = action.actionSetIPv4DstAddr();
		    ActionSetIpToS actionSetIpToS = action.actionSetIpToS();
		    ActionSetTcpUdpPort actionSetTcpUdpSrcPort = action.actionSetTcpUdpSrcPort();
		    ActionSetTcpUdpPort actionSetTcpUdpDstPort = action.actionSetTcpUdpDstPort();
		    ActionEnqueue actionEnqueue = action.actionEnqueue();

		    if (actionOutput != null) {
				actionOutputPort = actionOutput.port().value();
				// XXX: The max length is hard-coded for now
				OFActionOutput ofa =
				    new OFActionOutput(actionOutput.port().value(),
						       (short)0xffff);
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetVlanId != null) {
				OFActionVirtualLanIdentifier ofa =
				    new OFActionVirtualLanIdentifier(actionSetVlanId.vlanId());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetVlanPriority != null) {
				OFActionVirtualLanPriorityCodePoint ofa =
				    new OFActionVirtualLanPriorityCodePoint(actionSetVlanPriority.vlanPriority());
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
				OFActionDataLayerSource ofa = 
				    new OFActionDataLayerSource(actionSetEthernetSrcAddr.addr().toBytes());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetEthernetDstAddr != null) {
				OFActionDataLayerDestination ofa =
				    new OFActionDataLayerDestination(actionSetEthernetDstAddr.addr().toBytes());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetIPv4SrcAddr != null) {
				OFActionNetworkLayerSource ofa =
				    new OFActionNetworkLayerSource(actionSetIPv4SrcAddr.addr().value());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetIPv4DstAddr != null) {
				OFActionNetworkLayerDestination ofa =
				    new OFActionNetworkLayerDestination(actionSetIPv4DstAddr.addr().value());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetIpToS != null) {
				OFActionNetworkTypeOfService ofa =
				    new OFActionNetworkTypeOfService(actionSetIpToS.ipToS());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetTcpUdpSrcPort != null) {
				OFActionTransportLayerSource ofa =
				    new OFActionTransportLayerSource(actionSetTcpUdpSrcPort.port());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionSetTcpUdpDstPort != null) {
				OFActionTransportLayerDestination ofa =
				    new OFActionTransportLayerDestination(actionSetTcpUdpDstPort.port());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }

		    if (actionEnqueue != null) {
				OFActionEnqueue ofa =
				    new OFActionEnqueue(actionEnqueue.port().value(),
							actionEnqueue.queueId());
				openFlowActions.add(ofa);
				actionsLen += ofa.getLength();
		    }
		}

		fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
		    .setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
		    .setPriority(PRIORITY_DEFAULT)
		    .setBufferId(OFPacketOut.BUFFER_ID_NONE)
		    .setCookie(cookie)
		    .setCommand(flowModCommand)
		    .setMatch(match)
		    .setActions(openFlowActions)
		    .setLengthU(OFFlowMod.MINIMUM_LENGTH + actionsLen);
		fm.setOutPort(OFPort.OFPP_NONE.getValue());
		if ((flowModCommand == OFFlowMod.OFPFC_DELETE) ||
		    (flowModCommand == OFFlowMod.OFPFC_DELETE_STRICT)) {
		    if (actionOutputPort != null)
			fm.setOutPort(actionOutputPort);
		}

		//
		// TODO: Set the following flag
		// fm.setFlags(OFFlowMod.OFPFF_SEND_FLOW_REM);
		// See method ForwardingBase::pushRoute()
		//

		//
		// Write the message to the switch
		//
		log.debug("MEASUREMENT: Installing flow entry " + userState +
			  " into switch DPID: " +
			  sw.getStringId() +
			  " flowEntryId: " + flowEntryId.toString() +
			  " srcMac: " + matchSrcMac + " dstMac: " + matchDstMac +
			  " inPort: " + matchInPort + " outPort: " + actionOutputPort
			  );
		add(sw,fm);
	    //
	    // TODO: We should use the OpenFlow Barrier mechanism
	    // to check for errors, and update the SwitchState
	    // for a flow entry after the Barrier message is
	    // is received.
	    //
	    flowEntryObj.setSwitchState("FE_SWITCH_UPDATED");

		return true;
	}
	
	@Override
	public boolean add(IOFSwitch sw, FlowPath flowPath, FlowEntry flowEntry) {
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
					flowEntry.flowEntryId().toString(),
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
		FlowEntryMatch flowPathMatch = flowPath.flowEntryMatch();
		FlowEntryMatch flowEntryMatch = flowEntry.flowEntryMatch();

		// Match the Incoming Port
		Port matchInPort = flowEntryMatch.inPort();
		if (matchInPort != null) {
			match.setInputPort(matchInPort.value());
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
		}

		// Match the Source MAC address
		MACAddress matchSrcMac = flowEntryMatch.srcMac();
		if ((matchSrcMac == null) && (flowPathMatch != null)) {
			matchSrcMac = flowPathMatch.srcMac();
		}
		if (matchSrcMac != null) {
			match.setDataLayerSource(matchSrcMac.toString());
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
		}

		// Match the Destination MAC address
		MACAddress matchDstMac = flowEntryMatch.dstMac();
		if ((matchDstMac == null) && (flowPathMatch != null)) {
			matchDstMac = flowPathMatch.dstMac();
		}
		if (matchDstMac != null) {
			match.setDataLayerDestination(matchDstMac.toString());
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
		}

		// Match the Ethernet Frame Type
		Short matchEthernetFrameType = flowEntryMatch.ethernetFrameType();
		if ((matchEthernetFrameType == null) && (flowPathMatch != null)) {
			matchEthernetFrameType = flowPathMatch.ethernetFrameType();
		}
		if (matchEthernetFrameType != null) {
			match.setDataLayerType(matchEthernetFrameType);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
		}

		// Match the VLAN ID
		Short matchVlanId = flowEntryMatch.vlanId();
		if ((matchVlanId == null) && (flowPathMatch != null)) {
			matchVlanId = flowPathMatch.vlanId();
		}
		if (matchVlanId != null) {
			match.setDataLayerVirtualLan(matchVlanId);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN);
		}

		// Match the VLAN priority
		Byte matchVlanPriority = flowEntryMatch.vlanPriority();
		if ((matchVlanPriority == null) && (flowPathMatch != null)) {
			matchVlanPriority = flowPathMatch.vlanPriority();
		}
		if (matchVlanPriority != null) {
			match.setDataLayerVirtualLanPriorityCodePoint(matchVlanPriority);
			match.setWildcards(match.getWildcards()
					& ~OFMatch.OFPFW_DL_VLAN_PCP);
		}

		// Match the Source IPv4 Network prefix
		IPv4Net matchSrcIPv4Net = flowEntryMatch.srcIPv4Net();
		if ((matchSrcIPv4Net == null) && (flowPathMatch != null)) {
			matchSrcIPv4Net = flowPathMatch.srcIPv4Net();
		}
		if (matchSrcIPv4Net != null) {
			match.setFromCIDR(matchSrcIPv4Net.toString(), OFMatch.STR_NW_SRC);
		}

		// Natch the Destination IPv4 Network prefix
		IPv4Net matchDstIPv4Net = flowEntryMatch.dstIPv4Net();
		if ((matchDstIPv4Net == null) && (flowPathMatch != null)) {
			matchDstIPv4Net = flowPathMatch.dstIPv4Net();
		}
		if (matchDstIPv4Net != null) {
			match.setFromCIDR(matchDstIPv4Net.toString(), OFMatch.STR_NW_DST);
		}

		// Match the IP protocol
		Byte matchIpProto = flowEntryMatch.ipProto();
		if ((matchIpProto == null) && (flowPathMatch != null)) {
			matchIpProto = flowPathMatch.ipProto();
		}
		if (matchIpProto != null) {
			match.setNetworkProtocol(matchIpProto);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_PROTO);
		}

		// Match the IP ToS (DSCP field, 6 bits)
		Byte matchIpToS = flowEntryMatch.ipToS();
		if ((matchIpToS == null) && (flowPathMatch != null)) {
			matchIpToS = flowPathMatch.ipToS();
		}
		if (matchIpToS != null) {
			match.setNetworkTypeOfService(matchIpToS);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_TOS);
		}

		// Match the Source TCP/UDP port
		Short matchSrcTcpUdpPort = flowEntryMatch.srcTcpUdpPort();
		if ((matchSrcTcpUdpPort == null) && (flowPathMatch != null)) {
			matchSrcTcpUdpPort = flowPathMatch.srcTcpUdpPort();
		}
		if (matchSrcTcpUdpPort != null) {
			match.setTransportSource(matchSrcTcpUdpPort);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
		}

		// Match the Destination TCP/UDP port
		Short matchDstTcpUdpPort = flowEntryMatch.dstTcpUdpPort();
		if ((matchDstTcpUdpPort == null) && (flowPathMatch != null)) {
			matchDstTcpUdpPort = flowPathMatch.dstTcpUdpPort();
		}
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

		fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
				.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
				.setPriority(PRIORITY_DEFAULT)
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
		// TODO: Set the following flag
		// fm.setFlags(OFFlowMod.OFPFF_SEND_FLOW_REM);
		// See method ForwardingBase::pushRoute()
		//

		//
		// Write the message to the switch
		//
		log.debug("MEASUREMENT: Installing flow entry "
				+ flowEntry.flowEntryUserState() + " into switch DPID: "
				+ sw.getStringId() + " flowEntryId: "
				+ flowEntry.flowEntryId().toString() + " srcMac: "
				+ matchSrcMac + " dstMac: " + matchDstMac + " inPort: "
				+ matchInPort + " outPort: " + actionOutputPort);
		
		//
		// TODO: We should use the OpenFlow Barrier mechanism
		// to check for errors, and update the SwitchState
		// for a flow entry after the Barrier message is
		// is received.
		//
		// TODO: The FlowEntry Object in Titan should be set
		// to FE_SWITCH_UPDATED.
		//
		
		return add(sw,fm);
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
		
		OFBarrierRequest msg = (OFBarrierRequest) factory.getMessage(OFType.BARRIER_REQUEST);
		msg.setXid(sw.getNextTransactionId());

		OFBarrierReplyFuture future = new OFBarrierReplyFuture(threadPool, sw, msg.getXid());
		synchronized (barrierFutures) {
			Map<Integer,OFBarrierReplyFuture> map = barrierFutures.get(sw.getId());
			if (map == null) {
				map = new HashMap<Integer,OFBarrierReplyFuture>();
				barrierFutures.put(sw.getId(), map);
			}
			map.put(msg.getXid(), future);
		}
		
		add(sw, msg);
		
		return future;
	}

	/**
	 * Get a queue attached to a switch.
	 * @param sw Switch object
	 * @return Queue object
	 */
	protected SwitchQueue getQueue(IOFSwitch sw) {
		if (sw == null)  {
			return null;
		}
		
		return getProcess(sw).queues.get(sw);
	}
	
	/**
	 * Get a hash value correspondent to a switch.
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
	 * @param sw Switch object
	 * @return Thread object
	 */
	protected FlowPusherThread getProcess(IOFSwitch sw) {
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
		Map<Integer,OFBarrierReplyFuture> map = barrierFutures.get(sw.getId());
		if (map == null) {
			log.debug("null map for {} : {}", sw.getId(), barrierFutures);
			return Command.CONTINUE;
		}
		
		OFBarrierReplyFuture future = map.get(msg.getXid());
		if (future == null) {
			log.debug("null future for {} : {}", msg.getXid(), map);
			return Command.CONTINUE;
		}
		
		log.debug("Received BARRIER_REPLY : {}", msg);
		future.deliverFuture(sw, msg);
		
		return Command.CONTINUE;
	}

}
