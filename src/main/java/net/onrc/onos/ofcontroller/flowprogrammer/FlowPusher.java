package net.onrc.onos.ofcontroller.flowprogrammer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openflow.protocol.*;
import org.openflow.protocol.action.*;
import org.openflow.protocol.factory.BasicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFSwitch;
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
 * FlowPusher intermediates FlowManager/FlowSynchronizer and switches to push OpenFlow
 * messages to switches in proper rate.
 * @author Naoki Shiota
 *
 */
public class FlowPusher implements IFlowPusherService {
    private final static Logger log = LoggerFactory.getLogger(FlowPusher.class);

    // NOTE: Below are moved from FlowManager.
    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    protected static final int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static final int OFMESSAGE_DAMPER_TIMEOUT = 250;	// ms
    
    // Interval of sleep when queue is empty
    protected static final long SLEEP_MILLI_SEC = 10;
    protected static final int SLEEP_NANO_SEC = 0;
    
    // Number of messages sent to switch at once
    protected static final int MAX_MESSAGE_SEND = 100;

    public static final short PRIORITY_DEFAULT = 100;
    public static final short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0;	// infinity
    public static final short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;	// infinite

	public enum QueueState {
		READY,
		SUSPENDED,
	}
	
	@SuppressWarnings("serial")
	private class SwitchQueue extends ArrayDeque<OFMessage> {
		QueueState state;
		
		// Max rate of sending message (bytes/sec). 0 implies no limitation.
		long max_rate = 0;	// 0 indicates no limitation
		long last_sent_time = 0;
		long last_sent_size = 0;
		
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
			
			long rate = last_sent_size / (current - last_sent_time);
			
			if (rate < max_rate) {
				return true;
			} else {
				return false;
			}
		}
		
		void logSentData(long current, long size) {
			last_sent_time = current;
			last_sent_size = size;
		}
		
	}
	
	private OFMessageDamper messageDamper;

	private FloodlightContext context = null;
	private BasicFactory factory = null;
	private Map<Long, FlowPusherProcess> threadMap = null;
	
	private int number_thread = 1;
	
	/**
	 * Main thread that reads messages from queues and sends them to switches.
	 * @author Naoki Shiota
	 *
	 */
	private class FlowPusherProcess implements Runnable {
		private Map<IOFSwitch,SwitchQueue> queues
		= new HashMap<IOFSwitch,SwitchQueue>();
		
		private boolean isStopped = false;
		private boolean isMsgAdded = false;
		
		@Override
		public void run() {
			log.debug("Begin Flow Pusher Process");
			
			while (true) {
				Set< Map.Entry<IOFSwitch,SwitchQueue> > entries;
				synchronized (queues) {
					entries = queues.entrySet();
				}
				
				// Set taint flag to false at this moment.
				isMsgAdded = false;
				
				for (Map.Entry<IOFSwitch,SwitchQueue> entry : entries) {
					IOFSwitch sw = entry.getKey();
					SwitchQueue queue = entry.getValue();

					// Skip if queue is suspended
					if (sw == null || queue == null ||
							queue.state != QueueState.READY) {
						continue;
					}
					
					// check sending rate and determine it to be sent or not
					long current_time = System.nanoTime();
					long size = 0;
					
					synchronized (queue) {
						if (queue.isSendable(current_time)) {
							int i = 0;
							while (! queue.isEmpty()) {
								// Number of messages excess the limit
								if (++i >= MAX_MESSAGE_SEND) {
									// Messages remains in queue
									isMsgAdded = true;
									break;
								}
								
								OFMessage msg = queue.poll();
								
								// if need to send, call IOFSwitch#write()
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
						}
					}
				}
				
				// sleep while all queues are empty
				while (! (isMsgAdded || isStopped)) {
					try {
						Thread.sleep(SLEEP_MILLI_SEC, SLEEP_NANO_SEC);
					} catch (InterruptedException e) {
						e.printStackTrace();
						log.error("Thread.sleep failed");
					}
				}
				
				log.debug("Exit sleep loop.");
				
				if (isStopped) {
					log.debug("Pusher Process finished.");
					return;
				}

			}
		}
	}
	
	public FlowPusher() {
		
	}
	
	public FlowPusher(int number_thread) {
		this.number_thread = number_thread;
	}
	
	public void init(FloodlightContext context, BasicFactory factory, OFMessageDamper damper) {
		this.context = context;
		this.factory = factory;
		
		if (damper != null) {
			messageDamper = damper;
		} else {
			// use default value
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
		
		threadMap = new HashMap<Long,FlowPusherProcess>();
		for (long i = 0; i < number_thread; ++i) {
			FlowPusherProcess runnable = new FlowPusherProcess();
			threadMap.put(i, runnable);
			
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}
	
	/**
	 * Suspend sending messages to switch.
	 * @param sw
	 */
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

	/**
	 * Resume sending messages to switch.
	 */
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
	
	/**
	 * Check if given switch is suspended.
	 */
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
		
		for (FlowPusherProcess runnable : threadMap.values()) {
			if (! runnable.isStopped) {
				runnable.isStopped = true;
			}
		}
	}
	
	/**
	 * Set sending rate to a switch.
	 * @param sw Switch.
	 * @param rate Rate in bytes/sec.
	 */
	public void setRate(IOFSwitch sw, long rate) {
		SwitchQueue queue = getQueue(sw);
		if (queue == null) {
			return;
		}
		
		if (rate > 0) {
			queue.max_rate = rate;
		}
	}
	
	/**
	 * Add OFMessage to the queue related to given switch.
	 * @param sw Switch to which message is sent.
	 * @param msg Message to be sent.
	 * @return true if succeed.
	 */
	@Override
	public boolean add(IOFSwitch sw, OFMessage msg) {
		FlowPusherProcess proc = getProcess(sw);
		SwitchQueue queue = proc.queues.get(sw);

		if (queue == null) {
			queue = new SwitchQueue();
			queue.state = QueueState.READY;
			synchronized (proc) {
				proc.queues.put(sw, queue);
			}
		}
		
		synchronized (queue) {
			queue.add(msg);
			log.debug("Message is pushed : {}", msg);
		}
		
		proc.isMsgAdded = true;
		
		return true;
	}
	
	/**
	 * Create OFMessage from given flow information and add it to the queue.
	 * @param sw Switch to which message is sent.
	 * @param flowObj FlowPath.
	 * @param flowEntryObj FlowEntry.
	 * @return true if succeed.
	 */
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
	
	/**
	 * Create OFMessage from given flow information and add it to the queue.
	 * @param sw Switch to which message is sent.
	 * @param flowPath FlowPath.
	 * @param flowEntry FlowEntry.
	 * @return true if secceed.
	 */
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

	private SwitchQueue getQueue(IOFSwitch sw) {
		if (sw == null)  {
			return null;
		}
		
		return getProcess(sw).queues.get(sw);
	}
	
	private long getHash(IOFSwitch sw) {
		// TODO should consider equalization algorithm
		return sw.getId() % number_thread;
	}
	
	private FlowPusherProcess getProcess(IOFSwitch sw) {
		long hash = getHash(sw);
		
		return threadMap.get(hash);
	}
}
