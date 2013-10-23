package net.onrc.onos.ofcontroller.flowmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.*;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.*;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.*;
import org.openflow.protocol.factory.BasicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for performing Flow-related operations on the Switch.
 */
class FlowSwitchOperation {
    private static Logger log = LoggerFactory.getLogger(FlowSwitchOperation.class);
    //
    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    public static final short PRIORITY_DEFAULT = 100;
    public static final short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0;	// infinity
    public static final short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;	// infinite

    /**
     * Install a Flow Entry on a switch.
     *
     * @param messageFactory the OpenFlow message factory to use.
     * @maram messageDamper the OpenFlow message damper to use.
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowObj the flow path object for the flow entry to install.
     * @param flowEntryObj the flow entry object to install.
     * @return true on success, otherwise false.
     */
    static boolean installFlowEntry(BasicFactory messageFactory,
				    OFMessageDamper messageDamper,
				    IOFSwitch mySwitch, IFlowPath flowObj,
				    IFlowEntry flowEntryObj) {
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
	OFFlowMod fm = (OFFlowMod)messageFactory.getMessage(OFType.FLOW_MOD);
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

	// Natch the Destination IPv4 Network prefix
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
		  mySwitch.getStringId() +
		  " flowEntryId: " + flowEntryId.toString() +
		  " srcMac: " + matchSrcMac + " dstMac: " + matchDstMac +
		  " inPort: " + matchInPort + " outPort: " + actionOutputPort
		  );
	try {
	    messageDamper.write(mySwitch, fm, null);
	    mySwitch.flush();
	    //
	    // TODO: We should use the OpenFlow Barrier mechanism
	    // to check for errors, and update the SwitchState
	    // for a flow entry after the Barrier message is
	    // is received.
	    //
	    flowEntryObj.setSwitchState("FE_SWITCH_UPDATED");
	} catch (IOException e) {
	    log.error("Failure writing flow mod from network map", e);
	    return false;
	}

	return true;
    }

    /**
     * Install a Flow Entry on a switch.
     *
     * @param messageFactory the OpenFlow message factory to use.
     * @maram messageDamper the OpenFlow message damper to use.
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    static boolean installFlowEntry(BasicFactory messageFactory,
				    OFMessageDamper messageDamper,
				    IOFSwitch mySwitch, FlowPath flowPath,
				    FlowEntry flowEntry) {
	//
	// Create the OpenFlow Flow Modification Entry to push
	//
	OFFlowMod fm = (OFFlowMod)messageFactory.getMessage(OFType.FLOW_MOD);
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
	    log.debug("Flow Entry ignored (FlowEntryId = {}): unknown user state {}",
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
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN_PCP);
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
	try {
	    messageDamper.write(mySwitch, fm, null);
	    mySwitch.flush();
	    //
	    // TODO: We should use the OpenFlow Barrier mechanism
	    // to check for errors, and update the SwitchState
	    // for a flow entry after the Barrier message is
	    // is received.
	    //
	    // TODO: The FlowEntry Object in Titan should be set
	    // to FE_SWITCH_UPDATED.
	    //
	} catch (IOException e) {
	    log.error("Failure writing flow mod from network map", e);
	    return false;
	}
	return true;
    }
}
