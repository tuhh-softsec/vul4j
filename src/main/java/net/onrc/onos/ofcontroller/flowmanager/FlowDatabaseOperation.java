package net.onrc.onos.ofcontroller.flowmanager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

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
class FlowDatabaseOperation {
    private static Logger log = LoggerFactory.getLogger(FlowDatabaseOperation.class);

    /**
     * Add a flow.
     *
     * @param flowManager the Flow Manager to use.
     * @param dbHandler the Graph Database handler to use.
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @param dataPathSummaryStr the data path summary string if the added
     * flow will be maintained internally, otherwise null.
     * @return true on success, otherwise false.
     */
    static boolean addFlow(FlowManager flowManager,
			   GraphDBOperation dbHandler,
			   FlowPath flowPath, FlowId flowId,
			   String dataPathSummaryStr) {
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
		      flowPath.flowId().toString(),
		      stacktrace);
	    return false;
	}
	if (flowObj == null) {
	    log.error(":addFlow FlowId:{} failed: Flow object not created",
		      flowPath.flowId().toString());
	    dbHandler.rollback();
	    return false;
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
	// - flowPath.flowPathFlags()
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
	flowObj.setFlowPathFlags(flowPath.flowPathFlags().flags());
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

	if (dataPathSummaryStr != null) {
	    flowObj.setDataPathSummary(dataPathSummaryStr);
	} else {
	    flowObj.setDataPathSummary("");
	}

	if (found)
	    flowObj.setUserState("FE_USER_MODIFY");
	else
	    flowObj.setUserState("FE_USER_ADD");

	// Flow edges:
	//   HeadFE


	//
	// Flow Entries:
	// flowPath.dataPath().flowEntries()
	//
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    if (addFlowEntry(flowManager, dbHandler, flowObj, flowEntry) == null) {
		dbHandler.rollback();
		return false;
	    }
	}
	dbHandler.commit();

	//
	// TODO: We need a proper Flow ID allocation mechanism.
	//
	flowId.setValue(flowPath.flowId().value());

	return true;
    }

    /**
     * Add a flow entry to the Network MAP.
     *
     * @param flowManager the Flow Manager to use.
     * @param dbHandler the Graph Database handler to use.
     * @param flowObj the corresponding Flow Path object for the Flow Entry.
     * @param flowEntry the Flow Entry to install.
     * @return the added Flow Entry object on success, otherwise null.
     */
    static IFlowEntry addFlowEntry(FlowManager flowManager,
				   GraphDBOperation dbHandler,
				   IFlowPath flowObj,
				   FlowEntry flowEntry) {
	// Flow edges
	//   HeadFE (TODO)

	//
	// Assign the FlowEntry ID.
	//
	if ((flowEntry.flowEntryId() == null) ||
	    (flowEntry.flowEntryId().value() == 0)) {
	    long id = flowManager.getNextFlowEntryId();
	    flowEntry.setFlowEntryId(new FlowEntryId(id));
	}

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
		      flowEntry.flowEntryId().toString());
	    return null;
	}
	if (flowEntryObj == null) {
	    log.error(":addFlow FlowEntryId:{} failed: FlowEntry object not created",
		      flowEntry.flowEntryId().toString());
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
	flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
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
     * Delete all previously added flows.
     *
     * @param dbHandler the Graph Database handler to use.
     * @return true on success, otherwise false.
     */
    static boolean deleteAllFlows(GraphDBOperation dbHandler) {
	final ConcurrentLinkedQueue<FlowId> concurrentAllFlowIds =
	    new ConcurrentLinkedQueue<FlowId>();

	// Get all Flow IDs
	Iterable<IFlowPath> allFlowPaths = dbHandler.getAllFlowPaths();
	for (IFlowPath flowPathObj : allFlowPaths) {
	    if (flowPathObj == null)
		continue;
	    String flowIdStr = flowPathObj.getFlowId();
	    if (flowIdStr == null)
		continue;
	    FlowId flowId = new FlowId(flowIdStr);
	    concurrentAllFlowIds.add(flowId);
	}

	// Delete all flows one-by-one
	for (FlowId flowId : concurrentAllFlowIds)
	    deleteFlow(dbHandler, flowId);

	/*
	 * TODO: A faster mechanism to delete the Flow Paths by using
	 * a number of threads. Commented-out for now.
	 */
	/*
	//
	// Create the threads to delete the Flow Paths
	//
	List<Thread> threads = new LinkedList<Thread>();
	for (int i = 0; i < 10; i++) {
	    Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
		    while (true) {
			FlowId flowId = concurrentAllFlowIds.poll();
			if (flowId == null)
			    return;
			deleteFlow(dbHandler, flowId);
		    }
		}}, "Delete All Flow Paths");
	    threads.add(thread);
	}

	// Start processing
	for (Thread thread : threads) {
	    thread.start();
	}

	// Wait for all threads to complete
	for (Thread thread : threads) {
	    try {
		thread.join();
	    } catch (InterruptedException e) {
		log.debug("Exception waiting for a thread to delete a Flow Path: ", e);
	    }
	}
	*/

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
	//
	// We just mark the entries for deletion,
	// and let the switches remove each individual entry after
	// it has been removed from the switches.
	//
	try {
	    flowObj = dbHandler.searchFlowPath(flowId);
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":deleteFlow FlowId:{} failed", flowId.toString());
	    return false;
	}
	if (flowObj == null) {
	    dbHandler.commit();
	    return true;		// OK: No such flow
	}

	//
	// Find and mark for deletion all Flow Entries,
	// and the Flow itself.
	//
	flowObj.setUserState("FE_USER_DELETE");
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	boolean empty = true;	// TODO: an ugly hack
	for (IFlowEntry flowEntryObj : flowEntries) {
	    empty = false;
	    // flowObj.removeFlowEntry(flowEntryObj);
	    // conn.utils().removeFlowEntry(conn, flowEntryObj);
	    flowEntryObj.setUserState("FE_USER_DELETE");
	    flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
	}
	// Remove from the database empty flows
	if (empty)
	    dbHandler.removeFlowPath(flowObj);
	dbHandler.commit();

	return true;
    }

    /**
     * Clear the state for all previously added flows.
     *
     * @param dbHandler the Graph Database handler to use.
     * @return true on success, otherwise false.
     */
    static boolean clearAllFlows(GraphDBOperation dbHandler) {
	List<FlowId> allFlowIds = new LinkedList<FlowId>();

	// Get all Flow IDs
	Iterable<IFlowPath> allFlowPaths = dbHandler.getAllFlowPaths();
	for (IFlowPath flowPathObj : allFlowPaths) {
	    if (flowPathObj == null)
		continue;
	    String flowIdStr = flowPathObj.getFlowId();
	    if (flowIdStr == null)
		continue;
	    FlowId flowId = new FlowId(flowIdStr);
	    allFlowIds.add(flowId);
	}

	// Clear all flows one-by-one
	for (FlowId flowId : allFlowIds) {
	    clearFlow(dbHandler, flowId);
	}

	return true;
    }

    /**
     * Clear the state for a previously added flow.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the flow to clear.
     * @return true on success, otherwise false.
     */
    static boolean clearFlow(GraphDBOperation dbHandler, FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    flowObj = dbHandler.searchFlowPath(flowId);
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":clearFlow FlowId:{} failed", flowId.toString());
	    return false;
	}
	if (flowObj == null) {
	    dbHandler.commit();
	    return true;		// OK: No such flow
	}

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
	dbHandler.commit();

	return true;
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
	    log.error(":getFlow FlowId:{} failed", flowId.toString());
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
     * Get all previously added flows by a specific installer for a given
     * data path endpoints.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @return the Flow Paths if found, otherwise null.
     */
    static ArrayList<FlowPath> getAllFlows(GraphDBOperation dbHandler,
					   CallerId installerId,
					   DataPathEndpoints dataPathEndpoints) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
	ArrayList<FlowPath> allFlows = getAllFlows(dbHandler);
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	if (allFlows == null)
	    return flowPaths;

	for (FlowPath flow : allFlows) {
	    //
	    // TODO: String-based comparison is sub-optimal.
	    // We are using it for now to save us the extra work of
	    // implementing the "equals()" and "hashCode()" methods.
	    //
	    if (! flow.installerId().toString().equals(installerId.toString()))
		continue;
	    if (! flow.dataPath().srcPort().toString().equals(dataPathEndpoints.srcPort().toString())) {
		continue;
	    }
	    if (! flow.dataPath().dstPort().toString().equals(dataPathEndpoints.dstPort().toString())) {
		continue;
	    }
	    flowPaths.add(flow);
	}

	return flowPaths;
    }

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    static ArrayList<FlowPath> getAllFlows(GraphDBOperation dbHandler,
					   DataPathEndpoints dataPathEndpoints) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
	ArrayList<FlowPath> allFlows = getAllFlows(dbHandler);

	if (allFlows == null)
	    return flowPaths;

	for (FlowPath flow : allFlows) {
	    //
	    // TODO: String-based comparison is sub-optimal.
	    // We are using it for now to save us the extra work of
	    // implementing the "equals()" and "hashCode()" methods.
	    //
	    if (! flow.dataPath().srcPort().toString().equals(dataPathEndpoints.srcPort().toString())) {
		continue;
	    }
	    if (! flow.dataPath().dstPort().toString().equals(dataPathEndpoints.dstPort().toString())) {
		continue;
	    }
	    flowPaths.add(flow);
	}

	return flowPaths;
    }

    /**
     * Get summary of all installed flows by all installers in a given range.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the first flow in the flow range to get.
     * @param maxFlows the maximum number of flows to be returned.
     * @return the Flow Paths if found, otherwise null.
     */
    static ArrayList<IFlowPath> getAllFlowsSummary(GraphDBOperation dbHandler,
						   FlowId flowId,
						   int maxFlows) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
    	ArrayList<IFlowPath> flowPathsWithoutFlowEntries =
	    getAllFlowsWithoutFlowEntries(dbHandler);

    	Collections.sort(flowPathsWithoutFlowEntries, 
			 new Comparator<IFlowPath>() {
			     @Override
			     public int compare(IFlowPath first, IFlowPath second) {
				 long result =
				     new FlowId(first.getFlowId()).value()
				     - new FlowId(second.getFlowId()).value();
				 if (result > 0) {
				     return 1;
				 } else if (result < 0) {
				     return -1;
				 } else {
				     return 0;
				 }
			     }
			 }
			 );
    	
    	return flowPathsWithoutFlowEntries;
    }

    /**
     * Get all Flows information, without the associated Flow Entries.
     *
     * @param dbHandler the Graph Database handler to use.
     * @return all Flows information, without the associated Flow Entries.
     */
    static ArrayList<IFlowPath> getAllFlowsWithoutFlowEntries(GraphDBOperation dbHandler) {
    	Iterable<IFlowPath> flowPathsObj = null;
    	ArrayList<IFlowPath> flowPathsObjArray = new ArrayList<IFlowPath>();

	// TODO: Remove this op.commit() flow, because it is not needed?
    	dbHandler.commit();

    	try {
    	    flowPathsObj = dbHandler.getAllFlowPaths();
    	} catch (Exception e) {
    	    // TODO: handle exceptions
    	    dbHandler.rollback();
    	    log.error(":getAllFlowPaths failed");
	    return flowPathsObjArray;		// No Flows found
    	}
    	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
    	    return flowPathsObjArray;		// No Flows found
    	}

    	for (IFlowPath flowObj : flowPathsObj)
	    flowPathsObjArray.add(flowObj);

    	// conn.endTx(Transaction.COMMIT);

    	return flowPathsObjArray;
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
	Long flowPathFlags = flowObj.getFlowPathFlags();
	String srcSwitchStr = flowObj.getSrcSwitch();
	Short srcPortShort = flowObj.getSrcPort();
	String dstSwitchStr = flowObj.getDstSwitch();
	Short dstPortShort = flowObj.getDstPort();

	if ((flowIdStr == null) ||
	    (installerIdStr == null) ||
	    (flowPathFlags == null) ||
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
	flowPath.setFlowPathFlags(new FlowPathFlags(flowPathFlags));
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
    private static FlowEntry extractFlowEntry(IFlowEntry flowEntryObj) {
	String flowEntryIdStr = flowEntryObj.getFlowEntryId();
	String switchDpidStr = flowEntryObj.getSwitchDpid();
	String userState = flowEntryObj.getUserState();
	String switchState = flowEntryObj.getSwitchState();

	if ((flowEntryIdStr == null) ||
	    (switchDpidStr == null) ||
	    (userState == null) ||
	    (switchState == null)) {
	    // TODO: A work-around, becauuse of some bogus database objects
	    return null;
	}

	FlowEntry flowEntry = new FlowEntry();
	flowEntry.setFlowEntryId(new FlowEntryId(flowEntryIdStr));
	flowEntry.setDpid(new Dpid(switchDpidStr));

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
