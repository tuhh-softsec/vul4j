package net.onrc.onos.ofcontroller.flowmanager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import net.floodlightcontroller.util.MACAddress;

import net.onrc.onos.graph.GraphDBOperation;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for performing Flow-related operations on the Database.
 */
public class FlowDatabaseOperation {
    private final static Logger log = LoggerFactory.getLogger(FlowDatabaseOperation.class);

    /**
     * Add a flow.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowPath the Flow Path to install.
     * @return true on success, otherwise false.
     */
    static boolean addFlow(GraphDBOperation dbHandler, FlowPath flowPath) {
	IFlowPath flowObj = null;
	boolean found = false;
	try {
	    if ((flowObj = dbHandler.searchFlowPath(flowPath.flowId())) != null) {
		found = true;
	    } else {
		flowObj = dbHandler.newFlowPath();
	    }
	} catch (Exception e) {
	    dbHandler.rollback();

	    StringWriter sw = new StringWriter();
	    e.printStackTrace(new PrintWriter(sw));
	    String stacktrace = sw.toString();

	    log.error(":addFlow FlowId:{} failed: {}",
		      flowPath.flowId(),
		      stacktrace);
	    return false;
	}
	if (flowObj == null) {
	    log.error(":addFlow FlowId:{} failed: Flow object not created",
		      flowPath.flowId());
	    dbHandler.rollback();
	    return false;
	}

	//
	// Remove the old Flow Entries
	//
	if (found) {
	    Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	    LinkedList<IFlowEntry> deleteFlowEntries =
		new LinkedList<IFlowEntry>();
	    for (IFlowEntry flowEntryObj : flowEntries)
		deleteFlowEntries.add(flowEntryObj);
	    for (IFlowEntry flowEntryObj : deleteFlowEntries) {
		flowObj.removeFlowEntry(flowEntryObj);
		dbHandler.removeFlowEntry(flowEntryObj);
	    }
	}

	//
	// Set the Flow key:
	// - flowId
	//
	flowObj.setFlowId(flowPath.flowId().toString());
	flowObj.setType("flow");

	//
	// Set the Flow attributes:
	// - flowPath.installerId()
	// - flowPath.flowPathType()
	// - flowPath.flowPathUserState()
	// - flowPath.flowPathFlags()
	// - flowPath.idleTimeout()
	// - flowPath.hardTimeout()
	// - flowPath.dataPath().srcPort()
	// - flowPath.dataPath().dstPort()
	// - flowPath.matchSrcMac()
	// - flowPath.matchDstMac()
	// - flowPath.matchEthernetFrameType()
	// - flowPath.matchVlanId()
	// - flowPath.matchVlanPriority()
	// - flowPath.matchSrcIPv4Net()
	// - flowPath.matchDstIPv4Net()
	// - flowPath.matchIpProto()
	// - flowPath.matchIpToS()
	// - flowPath.matchSrcTcpUdpPort()
	// - flowPath.matchDstTcpUdpPort()
	// - flowPath.flowEntryActions()
	//
	flowObj.setInstallerId(flowPath.installerId().toString());
	flowObj.setFlowPathType(flowPath.flowPathType().toString());
	flowObj.setFlowPathUserState(flowPath.flowPathUserState().toString());
	flowObj.setFlowPathFlags(flowPath.flowPathFlags().flags());
	flowObj.setIdleTimeout(flowPath.idleTimeout());
	flowObj.setHardTimeout(flowPath.hardTimeout());
	flowObj.setSrcSwitch(flowPath.dataPath().srcPort().dpid().toString());
	flowObj.setSrcPort(flowPath.dataPath().srcPort().port().value());
	flowObj.setDstSwitch(flowPath.dataPath().dstPort().dpid().toString());
	flowObj.setDstPort(flowPath.dataPath().dstPort().port().value());
	if (flowPath.flowEntryMatch().matchSrcMac()) {
	    flowObj.setMatchSrcMac(flowPath.flowEntryMatch().srcMac().toString());
	}
	if (flowPath.flowEntryMatch().matchDstMac()) {
	    flowObj.setMatchDstMac(flowPath.flowEntryMatch().dstMac().toString());
	}
	if (flowPath.flowEntryMatch().matchEthernetFrameType()) {
	    flowObj.setMatchEthernetFrameType(flowPath.flowEntryMatch().ethernetFrameType());
	}
	if (flowPath.flowEntryMatch().matchVlanId()) {
	    flowObj.setMatchVlanId(flowPath.flowEntryMatch().vlanId());
	}
	if (flowPath.flowEntryMatch().matchVlanPriority()) {
	    flowObj.setMatchVlanPriority(flowPath.flowEntryMatch().vlanPriority());
	}
	if (flowPath.flowEntryMatch().matchSrcIPv4Net()) {
	    flowObj.setMatchSrcIPv4Net(flowPath.flowEntryMatch().srcIPv4Net().toString());
	}
	if (flowPath.flowEntryMatch().matchDstIPv4Net()) {
	    flowObj.setMatchDstIPv4Net(flowPath.flowEntryMatch().dstIPv4Net().toString());
	}
	if (flowPath.flowEntryMatch().matchIpProto()) {
	    flowObj.setMatchIpProto(flowPath.flowEntryMatch().ipProto());
	}
	if (flowPath.flowEntryMatch().matchIpToS()) {
	    flowObj.setMatchIpToS(flowPath.flowEntryMatch().ipToS());
	}
	if (flowPath.flowEntryMatch().matchSrcTcpUdpPort()) {
	    flowObj.setMatchSrcTcpUdpPort(flowPath.flowEntryMatch().srcTcpUdpPort());
	}
	if (flowPath.flowEntryMatch().matchDstTcpUdpPort()) {
	    flowObj.setMatchDstTcpUdpPort(flowPath.flowEntryMatch().dstTcpUdpPort());
	}
	if (! flowPath.flowEntryActions().actions().isEmpty()) {
	    flowObj.setActions(flowPath.flowEntryActions().toString());
	}
	flowObj.setDataPathSummary(flowPath.dataPath().dataPathSummary());

	if (found)
	    flowObj.setFlowPathUserState("FP_USER_MODIFY");
	else
	    flowObj.setFlowPathUserState("FP_USER_ADD");

	// Flow edges:
	//   HeadFE


	//
	// Flow Entries:
	// flowPath.dataPath().flowEntries()
	//
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_DELETE)
		continue;	// Skip: all Flow Entries were deleted earlier

	    if (addFlowEntry(dbHandler, flowObj, flowEntry) == null) {
		dbHandler.rollback();
		return false;
	    }
	}
	dbHandler.commit();

	return true;
    }

    /**
     * Add a flow entry to the Network MAP.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowObj the corresponding Flow Path object for the Flow Entry.
     * @param flowEntry the Flow Entry to install.
     * @return the added Flow Entry object on success, otherwise null.
     */
    static IFlowEntry addFlowEntry(GraphDBOperation dbHandler,
				   IFlowPath flowObj,
				   FlowEntry flowEntry) {
	// Flow edges
	//   HeadFE (TODO)

	IFlowEntry flowEntryObj = null;
	boolean found = false;
	try {
	    if ((flowEntryObj =
		 dbHandler.searchFlowEntry(flowEntry.flowEntryId())) != null) {
		found = true;
	    } else {
		flowEntryObj = dbHandler.newFlowEntry();
	    }
	} catch (Exception e) {
	    log.error(":addFlow FlowEntryId:{} failed",
		      flowEntry.flowEntryId());
	    return null;
	}
	if (flowEntryObj == null) {
	    log.error(":addFlow FlowEntryId:{} failed: FlowEntry object not created",
		      flowEntry.flowEntryId());
	    return null;
	}

	//
	// Set the Flow Entry key:
	// - flowEntry.flowEntryId()
	//
	flowEntryObj.setFlowEntryId(flowEntry.flowEntryId().toString());
	flowEntryObj.setType("flow_entry");

	//
	// Set the Flow Entry Edges and attributes:
	// - Switch edge
	// - InPort edge
	// - OutPort edge
	//
	// - flowEntry.idleTimeout()
	// - flowEntry.hardTimeout()
	// - flowEntry.dpid()
	// - flowEntry.flowEntryUserState()
	// - flowEntry.flowEntrySwitchState()
	// - flowEntry.flowEntryErrorState()
	// - flowEntry.matchInPort()
	// - flowEntry.matchSrcMac()
	// - flowEntry.matchDstMac()
	// - flowEntry.matchEthernetFrameType()
	// - flowEntry.matchVlanId()
	// - flowEntry.matchVlanPriority()
	// - flowEntry.matchSrcIPv4Net()
	// - flowEntry.matchDstIPv4Net()
	// - flowEntry.matchIpProto()
	// - flowEntry.matchIpToS()
	// - flowEntry.matchSrcTcpUdpPort()
	// - flowEntry.matchDstTcpUdpPort()
	// - flowEntry.actionOutputPort()
	// - flowEntry.actions()
	//
	ISwitchObject sw = dbHandler.searchSwitch(flowEntry.dpid().toString());
	flowEntryObj.setIdleTimeout(flowEntry.idleTimeout());
	flowEntryObj.setHardTimeout(flowEntry.hardTimeout());
	flowEntryObj.setSwitchDpid(flowEntry.dpid().toString());
	flowEntryObj.setSwitch(sw);
	if (flowEntry.flowEntryMatch().matchInPort()) {
	    IPortObject inport =
		dbHandler.searchPort(flowEntry.dpid().toString(),
					flowEntry.flowEntryMatch().inPort().value());
	    flowEntryObj.setMatchInPort(flowEntry.flowEntryMatch().inPort().value());
	    flowEntryObj.setInPort(inport);
	}
	if (flowEntry.flowEntryMatch().matchSrcMac()) {
	    flowEntryObj.setMatchSrcMac(flowEntry.flowEntryMatch().srcMac().toString());
	}
	if (flowEntry.flowEntryMatch().matchDstMac()) {
	    flowEntryObj.setMatchDstMac(flowEntry.flowEntryMatch().dstMac().toString());
	}
	if (flowEntry.flowEntryMatch().matchEthernetFrameType()) {
	    flowEntryObj.setMatchEthernetFrameType(flowEntry.flowEntryMatch().ethernetFrameType());
	}
	if (flowEntry.flowEntryMatch().matchVlanId()) {
	    flowEntryObj.setMatchVlanId(flowEntry.flowEntryMatch().vlanId());
	}
	if (flowEntry.flowEntryMatch().matchVlanPriority()) {
	    flowEntryObj.setMatchVlanPriority(flowEntry.flowEntryMatch().vlanPriority());
	}
	if (flowEntry.flowEntryMatch().matchSrcIPv4Net()) {
	    flowEntryObj.setMatchSrcIPv4Net(flowEntry.flowEntryMatch().srcIPv4Net().toString());
	}
	if (flowEntry.flowEntryMatch().matchDstIPv4Net()) {
	    flowEntryObj.setMatchDstIPv4Net(flowEntry.flowEntryMatch().dstIPv4Net().toString());
	}
	if (flowEntry.flowEntryMatch().matchIpProto()) {
	    flowEntryObj.setMatchIpProto(flowEntry.flowEntryMatch().ipProto());
	}
	if (flowEntry.flowEntryMatch().matchIpToS()) {
	    flowEntryObj.setMatchIpToS(flowEntry.flowEntryMatch().ipToS());
	}
	if (flowEntry.flowEntryMatch().matchSrcTcpUdpPort()) {
	    flowEntryObj.setMatchSrcTcpUdpPort(flowEntry.flowEntryMatch().srcTcpUdpPort());
	}
	if (flowEntry.flowEntryMatch().matchDstTcpUdpPort()) {
	    flowEntryObj.setMatchDstTcpUdpPort(flowEntry.flowEntryMatch().dstTcpUdpPort());
	}

	for (FlowEntryAction fa : flowEntry.flowEntryActions().actions()) {
	    if (fa.actionOutput() != null) {
		IPortObject outport =
		    dbHandler.searchPort(flowEntry.dpid().toString(),
					      fa.actionOutput().port().value());
		flowEntryObj.setActionOutputPort(fa.actionOutput().port().value());
		flowEntryObj.setOutPort(outport);
	    }
	}
	if (! flowEntry.flowEntryActions().isEmpty()) {
	    flowEntryObj.setActions(flowEntry.flowEntryActions().toString());
	}

	// TODO: Hacks with hard-coded state names!
	if (found)
	    flowEntryObj.setUserState("FE_USER_MODIFY");
	else
	    flowEntryObj.setUserState("FE_USER_ADD");
	flowEntryObj.setSwitchState(flowEntry.flowEntrySwitchState().toString());
	//
	// TODO: Take care of the FlowEntryErrorState.
	//

	// Flow Entries edges:
	//   Flow
	//   NextFE (TODO)
	if (! found) {
	    flowObj.addFlowEntry(flowEntryObj);
	    flowEntryObj.setFlow(flowObj);
	}

	return flowEntryObj;
    }

    /**
     * Delete a flow entry from the Network MAP.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowObj the corresponding Flow Path object for the Flow Entry.
     * @param flowEntry the Flow Entry to delete.
     * @return true on success, otherwise false.
     */
    static boolean deleteFlowEntry(GraphDBOperation dbHandler,
				   IFlowPath flowObj,
				   FlowEntry flowEntry) {
	IFlowEntry flowEntryObj = null;
	try {
	    flowEntryObj = dbHandler.searchFlowEntry(flowEntry.flowEntryId());
	} catch (Exception e) {
	    log.error(":deleteFlowEntry FlowEntryId:{} failed",
		      flowEntry.flowEntryId());
	    return false;
	}
	//
	// TODO: Don't print an error for now, because multiple controller
	// instances might be deleting the same flow entry.
	//
	/*
	if (flowEntryObj == null) {
	    log.error(":deleteFlowEntry FlowEntryId:{} failed: FlowEntry object not found",
		      flowEntry.flowEntryId());
	    return false;
	}
	*/
	if (flowEntryObj == null)
	    return true;

	flowObj.removeFlowEntry(flowEntryObj);
	dbHandler.removeFlowEntry(flowEntryObj);
	return true;
    }

    /**
     * Delete all previously added flows.
     *
     * @param dbHandler the Graph Database handler to use.
     * @return true on success, otherwise false.
     */
    static boolean deleteAllFlows(GraphDBOperation dbHandler) {
	// Get all Flow IDs
	Iterable<IFlowPath> allFlowPaths = dbHandler.getAllFlowPaths();
	for (IFlowPath flowPathObj : allFlowPaths) {
	    if (flowPathObj == null)
		continue;

	    deleteIFlowPath(dbHandler, flowPathObj);
	}
	dbHandler.commit();

	return true;
    }

    /**
     * Delete a previously added flow.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    static boolean deleteFlow(GraphDBOperation dbHandler, FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    flowObj = dbHandler.searchFlowPath(flowId);
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":deleteFlow FlowId:{} failed", flowId);
	    return false;
	}
	if (flowObj == null) {
	    dbHandler.commit();
	    return true;		// OK: No such flow
	}

	deleteIFlowPath(dbHandler, flowObj);
	dbHandler.commit();
	return true;
    }

    /**
     * Delete a previously added flow.
     * @note You need to call commit after calling this method.
     * @param dbHandler the Graph Database handler to use.
     * @param flowObj IFlowPath object to delete.
     */
    private static void deleteIFlowPath(GraphDBOperation dbHandler, IFlowPath flowObj) {
	//
	// Remove all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    flowObj.removeFlowEntry(flowEntryObj);
	    dbHandler.removeFlowEntry(flowEntryObj);
	}
	// Remove the Flow itself
	dbHandler.removeFlowPath(flowObj);
    }

    /**
     * Get a previously added flow.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    static FlowPath getFlow(GraphDBOperation dbHandler, FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    flowObj = dbHandler.searchFlowPath(flowId);
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":getFlow FlowId:{} failed", flowId);
	    return null;
	}
	if (flowObj == null) {
	    dbHandler.commit();
	    return null;		// Flow not found
	}

	//
	// Extract the Flow state
	//
	FlowPath flowPath = extractFlowPath(flowObj);
	dbHandler.commit();

	return flowPath;
    }

    /**
     * Get all installed flows by all installers.
     *
     * @param dbHandler the Graph Database handler to use.
     * @return the Flow Paths if found, otherwise null.
     */
    static ArrayList<FlowPath> getAllFlows(GraphDBOperation dbHandler) {
	Iterable<IFlowPath> flowPathsObj = null;
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	try {
	    flowPathsObj = dbHandler.getAllFlowPaths();
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":getAllFlowPaths failed");
	    return flowPaths;
	}
	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
	    dbHandler.commit();
	    return flowPaths;	// No Flows found
	}

	for (IFlowPath flowObj : flowPathsObj) {
	    //
	    // Extract the Flow state
	    //
	    FlowPath flowPath = extractFlowPath(flowObj);
	    if (flowPath != null)
		flowPaths.add(flowPath);
	}

	dbHandler.commit();

	return flowPaths;
    }

    /**
     * Extract Flow Path State from a Titan Database Object @ref IFlowPath.
     *
     * @param flowObj the object to extract the Flow Path State from.
     * @return the extracted Flow Path State.
     */
    private static FlowPath extractFlowPath(IFlowPath flowObj) {
	//
	// Extract the Flow state
	//
	String flowIdStr = flowObj.getFlowId();
	String installerIdStr = flowObj.getInstallerId();
	String flowPathType = flowObj.getFlowPathType();
	String flowPathUserState = flowObj.getFlowPathUserState();
	Long flowPathFlags = flowObj.getFlowPathFlags();
	Integer idleTimeout = flowObj.getIdleTimeout();
	Integer hardTimeout = flowObj.getHardTimeout();
	String srcSwitchStr = flowObj.getSrcSwitch();
	Short srcPortShort = flowObj.getSrcPort();
	String dstSwitchStr = flowObj.getDstSwitch();
	Short dstPortShort = flowObj.getDstPort();

	if ((flowIdStr == null) ||
	    (installerIdStr == null) ||
	    (flowPathType == null) ||
	    (flowPathUserState == null) ||
	    (flowPathFlags == null) ||
	    (idleTimeout == null) ||
	    (hardTimeout == null) ||
	    (srcSwitchStr == null) ||
	    (srcPortShort == null) ||
	    (dstSwitchStr == null) ||
	    (dstPortShort == null)) {
	    // TODO: A work-around, becauuse of some bogus database objects
	    return null;
	}

	FlowPath flowPath = new FlowPath();
	flowPath.setFlowId(new FlowId(flowIdStr));
	flowPath.setInstallerId(new CallerId(installerIdStr));
	flowPath.setFlowPathType(FlowPathType.valueOf(flowPathType));
	flowPath.setFlowPathUserState(FlowPathUserState.valueOf(flowPathUserState));
	flowPath.setFlowPathFlags(new FlowPathFlags(flowPathFlags));
	flowPath.setIdleTimeout(idleTimeout);
	flowPath.setHardTimeout(hardTimeout);
	flowPath.dataPath().srcPort().setDpid(new Dpid(srcSwitchStr));
	flowPath.dataPath().srcPort().setPort(new Port(srcPortShort));
	flowPath.dataPath().dstPort().setDpid(new Dpid(dstSwitchStr));
	flowPath.dataPath().dstPort().setPort(new Port(dstPortShort));
	//
	// Extract the match conditions common for all Flow Entries
	//
	{
	    FlowEntryMatch match = new FlowEntryMatch();
	    String matchSrcMac = flowObj.getMatchSrcMac();
	    if (matchSrcMac != null)
		match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
	    String matchDstMac = flowObj.getMatchDstMac();
	    if (matchDstMac != null)
		match.enableDstMac(MACAddress.valueOf(matchDstMac));
	    Short matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
	    if (matchEthernetFrameType != null)
		match.enableEthernetFrameType(matchEthernetFrameType);
	    Short matchVlanId = flowObj.getMatchVlanId();
	    if (matchVlanId != null)
		match.enableVlanId(matchVlanId);
	    Byte matchVlanPriority = flowObj.getMatchVlanPriority();
	    if (matchVlanPriority != null)
		match.enableVlanPriority(matchVlanPriority);
	    String matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
	    if (matchSrcIPv4Net != null)
		match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
	    String matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
	    if (matchDstIPv4Net != null)
		match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
	    Byte matchIpProto = flowObj.getMatchIpProto();
	    if (matchIpProto != null)
		match.enableIpProto(matchIpProto);
	    Byte matchIpToS = flowObj.getMatchIpToS();
	    if (matchIpToS != null)
		match.enableIpToS(matchIpToS);
	    Short matchSrcTcpUdpPort = flowObj.getMatchSrcTcpUdpPort();
	    if (matchSrcTcpUdpPort != null)
		match.enableSrcTcpUdpPort(matchSrcTcpUdpPort);
	    Short matchDstTcpUdpPort = flowObj.getMatchDstTcpUdpPort();
	    if (matchDstTcpUdpPort != null)
		match.enableDstTcpUdpPort(matchDstTcpUdpPort);

	    flowPath.setFlowEntryMatch(match);
	}
	//
	// Extract the actions for the first Flow Entry
	//
	{
	    String actionsStr = flowObj.getActions();
	    if (actionsStr != null) {
		FlowEntryActions flowEntryActions = new FlowEntryActions(actionsStr);
		flowPath.setFlowEntryActions(flowEntryActions);
	    }
	}

	//
	// Extract all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    FlowEntry flowEntry = extractFlowEntry(flowEntryObj);
	    if (flowEntry == null)
		continue;
	    flowPath.dataPath().flowEntries().add(flowEntry);
	}

	return flowPath;
    }

    /**
     * Extract Flow Entry State from a Titan Database Object @ref IFlowEntry.
     *
     * @param flowEntryObj the object to extract the Flow Entry State from.
     * @return the extracted Flow Entry State.
     */
    public static FlowEntry extractFlowEntry(IFlowEntry flowEntryObj) {
	IFlowPath flowObj = flowEntryObj.getFlow();
	if (flowObj == null)
	    return null;

	String flowIdStr = flowObj.getFlowId();
	//
	String flowEntryIdStr = flowEntryObj.getFlowEntryId();
	Integer idleTimeout = flowEntryObj.getIdleTimeout();
	Integer hardTimeout = flowEntryObj.getHardTimeout();
	String switchDpidStr = flowEntryObj.getSwitchDpid();
	String userState = flowEntryObj.getUserState();
	String switchState = flowEntryObj.getSwitchState();

	if ((flowIdStr == null) ||
	    (flowEntryIdStr == null) ||
	    (idleTimeout == null) ||
	    (hardTimeout == null) ||
	    (switchDpidStr == null) ||
	    (userState == null) ||
	    (switchState == null)) {
	    // TODO: A work-around, because of some bogus database objects
	    return null;
	}

	FlowEntry flowEntry = new FlowEntry();
	flowEntry.setFlowEntryId(new FlowEntryId(flowEntryIdStr));
	flowEntry.setFlowId(new FlowId(flowIdStr));
	flowEntry.setDpid(new Dpid(switchDpidStr));
	flowEntry.setIdleTimeout(idleTimeout);
	flowEntry.setHardTimeout(hardTimeout);

	//
	// Extract the match conditions
	//
	FlowEntryMatch match = new FlowEntryMatch();
	Short matchInPort = flowEntryObj.getMatchInPort();
	if (matchInPort != null)
	    match.enableInPort(new Port(matchInPort));
	String matchSrcMac = flowEntryObj.getMatchSrcMac();
	if (matchSrcMac != null)
	    match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
	String matchDstMac = flowEntryObj.getMatchDstMac();
	if (matchDstMac != null)
	    match.enableDstMac(MACAddress.valueOf(matchDstMac));
	Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
	if (matchEthernetFrameType != null)
	    match.enableEthernetFrameType(matchEthernetFrameType);
	Short matchVlanId = flowEntryObj.getMatchVlanId();
	if (matchVlanId != null)
	    match.enableVlanId(matchVlanId);
	Byte matchVlanPriority = flowEntryObj.getMatchVlanPriority();
	if (matchVlanPriority != null)
	    match.enableVlanPriority(matchVlanPriority);
	String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
	if (matchSrcIPv4Net != null)
	    match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
	String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
	if (matchDstIPv4Net != null)
	    match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
	Byte matchIpProto = flowEntryObj.getMatchIpProto();
	if (matchIpProto != null)
	    match.enableIpProto(matchIpProto);
	Byte matchIpToS = flowEntryObj.getMatchIpToS();
	if (matchIpToS != null)
	    match.enableIpToS(matchIpToS);
	Short matchSrcTcpUdpPort = flowEntryObj.getMatchSrcTcpUdpPort();
	if (matchSrcTcpUdpPort != null)
	    match.enableSrcTcpUdpPort(matchSrcTcpUdpPort);
	Short matchDstTcpUdpPort = flowEntryObj.getMatchDstTcpUdpPort();
	if (matchDstTcpUdpPort != null)
	    match.enableDstTcpUdpPort(matchDstTcpUdpPort);
	flowEntry.setFlowEntryMatch(match);

	//
	// Extract the actions
	//
	FlowEntryActions actions = new FlowEntryActions();
	String actionsStr = flowEntryObj.getActions();
	if (actionsStr != null)
	    actions = new FlowEntryActions(actionsStr);
	flowEntry.setFlowEntryActions(actions);
	flowEntry.setFlowEntryUserState(FlowEntryUserState.valueOf(userState));
	flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.valueOf(switchState));
	//
	// TODO: Take care of FlowEntryErrorState.
	//
	return flowEntry;
    }
}
