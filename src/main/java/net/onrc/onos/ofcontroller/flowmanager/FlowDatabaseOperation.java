package net.onrc.onos.ofcontroller.flowmanager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.impls.ramcloud.PerfMon;

/**
 * Class for performing Flow-related operations on the Database.
 */
public class FlowDatabaseOperation {
    private final static Logger log = LoggerFactory.getLogger(FlowDatabaseOperation.class);
    private static final boolean measureONOSFlowTimeProp = Long.valueOf(System.getProperty("benchmark.measureONOSFlow", "0")) != 0;
    private static final boolean measureONOSFlowEntryTimeProp = Long.valueOf(System.getProperty("benchmark.measureONOSFlowEntry", "0")) != 0;

    /**
     * Add a flow.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowPath the Flow Path to install.
     * @return true on success, otherwise false.
     */
    static boolean addFlow(DBOperation dbHandler, FlowPath flowPath) {
	IFlowPath flowObj = null;
	boolean found = false;
	long startAddFlow = 0;
	long endAddFlow = 0;
	long endSearchExistingFlowPathTime = 0;
	long startCreateNewFlowPathTime = 0;
	long endCreateNewFlowPathTime = 0;
	long startFollowExistingFlowEntries = 0;
	long endFollowExistingFlowEntries = 0;
	long accTimeRemovingFlowEntriesFromFlowPath = 0;
	long accTimeRemovingFlowEntriesFromDB = 0;
	long startSettingFlowPathProps = 0;
	long endSettingFlowPathProps = 0;
	int numPropsSet = 0;
	long accTimeAddFlowEntries = 0;
	int numNewFlowEntries = 0;
	LinkedList<long[]> flowEntryTimes = new LinkedList<>();
	PerfMon pm = PerfMon.getInstance();

	pm.addflowpath_start();
	try {
	    if ( measureONOSFlowTimeProp ) {
		startAddFlow = System.nanoTime();
	    }
	    flowObj = dbHandler.searchFlowPath(flowPath.flowId());
	    if ( measureONOSFlowTimeProp ) {
		endSearchExistingFlowPathTime = System.nanoTime();
	    }
	    if (flowObj != null) {
		found = true;
	    } else {
		if ( measureONOSFlowTimeProp ) {
			startCreateNewFlowPathTime = System.nanoTime();
		}
		flowObj = dbHandler.newFlowPath();
		if ( measureONOSFlowTimeProp ) {
			endCreateNewFlowPathTime = System.nanoTime();
		}
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
	    if ( measureONOSFlowTimeProp ) {
		startFollowExistingFlowEntries = System.nanoTime();
	    }
	    Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	    if ( measureONOSFlowTimeProp ) {
		endFollowExistingFlowEntries = System.nanoTime();
	    }
	    LinkedList<IFlowEntry> deleteFlowEntries =
		new LinkedList<IFlowEntry>();
	    for (IFlowEntry flowEntryObj : flowEntries)
		deleteFlowEntries.add(flowEntryObj);
	    if( measureONOSFlowTimeProp ) {
		    for (IFlowEntry flowEntryObj : deleteFlowEntries) {
			long start = System.nanoTime();
			flowObj.removeFlowEntry(flowEntryObj);
			accTimeRemovingFlowEntriesFromFlowPath += System.nanoTime() - start;
			start = System.nanoTime();
			dbHandler.removeFlowEntry(flowEntryObj);
			accTimeRemovingFlowEntriesFromDB += System.nanoTime() - start;
		    }
	    } else {
		    for (IFlowEntry flowEntryObj : deleteFlowEntries) {
			flowObj.removeFlowEntry(flowEntryObj);
			dbHandler.removeFlowEntry(flowEntryObj);
		    }
	    }
	}

	if ( measureONOSFlowTimeProp ) {
		startSettingFlowPathProps = System.nanoTime();
	}
	//
	// Set the Flow key:
	// - flowId
	//
	flowObj.setFlowId(flowPath.flowId().toString());
	flowObj.setType("flow");
	if ( measureONOSFlowTimeProp ) {
	    numPropsSet += 2;
	}

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
	if ( measureONOSFlowTimeProp ) {
	    numPropsSet += 10;
	}

	if (flowPath.flowEntryMatch().matchSrcMac()) {
	    flowObj.setMatchSrcMac(flowPath.flowEntryMatch().srcMac().toString());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchDstMac()) {
	    flowObj.setMatchDstMac(flowPath.flowEntryMatch().dstMac().toString());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchEthernetFrameType()) {
	    flowObj.setMatchEthernetFrameType(flowPath.flowEntryMatch().ethernetFrameType());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchVlanId()) {
	    flowObj.setMatchVlanId(flowPath.flowEntryMatch().vlanId());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchVlanPriority()) {
	    flowObj.setMatchVlanPriority(flowPath.flowEntryMatch().vlanPriority());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchSrcIPv4Net()) {
	    flowObj.setMatchSrcIPv4Net(flowPath.flowEntryMatch().srcIPv4Net().toString());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchDstIPv4Net()) {
	    flowObj.setMatchDstIPv4Net(flowPath.flowEntryMatch().dstIPv4Net().toString());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchIpProto()) {
	    flowObj.setMatchIpProto(flowPath.flowEntryMatch().ipProto());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchIpToS()) {
	    flowObj.setMatchIpToS(flowPath.flowEntryMatch().ipToS());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchSrcTcpUdpPort()) {
	    flowObj.setMatchSrcTcpUdpPort(flowPath.flowEntryMatch().srcTcpUdpPort());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (flowPath.flowEntryMatch().matchDstTcpUdpPort()) {
	    flowObj.setMatchDstTcpUdpPort(flowPath.flowEntryMatch().dstTcpUdpPort());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	if (! flowPath.flowEntryActions().actions().isEmpty()) {
	    flowObj.setActions(flowPath.flowEntryActions().toString());
		if ( measureONOSFlowTimeProp ) {
		    ++numPropsSet;
		}
	}
	flowObj.setDataPathSummary(flowPath.dataPath().dataPathSummary());
	if ( measureONOSFlowTimeProp ) {
	    ++numPropsSet;
	}

	if (found)
	    flowObj.setFlowPathUserState("FP_USER_MODIFY");
	else
	    flowObj.setFlowPathUserState("FP_USER_ADD");

	if ( measureONOSFlowTimeProp ) {
	    ++numPropsSet;
	}

	if ( measureONOSFlowTimeProp ) {
		endSettingFlowPathProps = System.nanoTime();
	}
	pm.addflowpath_end();
	// Flow edges:
	//   HeadFE


	//
	// Flow Entries:
	// flowPath.dataPath().flowEntries()
	//
	pm.addflowentry_start();
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_DELETE)
		continue;	// Skip: all Flow Entries were deleted earlier

	    pm.addflowentry_incr();

	    long startAddFlowEntry = 0, endAddFlowEntry;
	    if( measureONOSFlowTimeProp ) {
		startAddFlowEntry = System.nanoTime();
	    }
	    IFlowEntry iFlowEntry = addFlowEntry(dbHandler, flowObj, flowEntry);
	    if( measureONOSFlowTimeProp ) {
		endAddFlowEntry = System.nanoTime();
		accTimeAddFlowEntries += endAddFlowEntry - startAddFlowEntry;

		flowEntryTimes.addLast( new long[]{flowEntry.flowId().value(), endAddFlowEntry - startAddFlowEntry} );
	    }
	    if ( iFlowEntry == null) {
		dbHandler.rollback();
		return false;
	    }
	}
	pm.addflowentry_end();
	dbHandler.commit();


	if ( measureONOSFlowTimeProp ) {
	    endAddFlow = System.nanoTime();

	    log.error("Performance addFlow(_,{}) -- "
		    + "GrandTotal: {} "
		    + "only FlowPathTotal: {} "
		    + "searchExistingFlowPath: {} "
		    + "createNewFlowPathTime: {}"
		    + "followExistingFlowEntries: {} "
		    + "accTimeRemovingFlowEntriesFromFlowPath: {} "
		    + "accTimeRemovingFlowEntriesFromDB: {} "
		    + "settingFlowPathProps: {} #Props: {} "
		    + "accFlowEntries: {} #FEs: {}",
		    flowPath.flowId(),
		    (endAddFlow - startAddFlow),
		    (endSettingFlowPathProps - startAddFlow),
		    (endSearchExistingFlowPathTime - startAddFlow),
		    (endCreateNewFlowPathTime - startCreateNewFlowPathTime),
		    (endFollowExistingFlowEntries - startFollowExistingFlowEntries),
		    (accTimeRemovingFlowEntriesFromFlowPath),
		    (accTimeRemovingFlowEntriesFromDB),
		    (endSettingFlowPathProps - startSettingFlowPathProps), numPropsSet,
		    accTimeAddFlowEntries, numNewFlowEntries
		    );

	    // Each FlowEntries
	    final String strFlowId = flowPath.flowId().toString();
	    for ( long[] idFE_Time : flowEntryTimes ) {
		log.error("Performance addFlowEntry(_,{},{})@addFlow -- FlowEntryTotal: {}",
			strFlowId,
			"0x" + Long.toHexString(idFE_Time[0]),
			idFE_Time[1]);
	    }
	}

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
    static IFlowEntry addFlowEntry(DBOperation dbHandler,
				   IFlowPath flowObj,
				   FlowEntry flowEntry) {
	// Flow edges
	//   HeadFE (TODO)
	long startAddFlowEntry = 0;
	long endAddFlowEntry = 0;

	long endSearchFlowEntry = 0;

	long startCreateNewFlowEntry = 0;
	long endCreateNewFlowEntry = 0;

	long startSetProperties = 0;
	long endSetProperties = 0;
	int numProperties = 0;

	long startSearchSwitch = 0;
	long endSearchSwitch = 0;

	long startAddEdgeToSwitch =0;
	long endAddEdgeToSwitch =0;

	long startSearchInPort = 0;
	long endSearchInPort = 0;

	long startAddEdgeToInPort =0;
	long endAddEdgeToInPort =0;

	long startSearchOutPort = 0;
	long endSearchOutPort = 0;

	long startAddEdgeToOutPort =0;
	long endAddEdgeToOutPort =0;

	long startAddEdgeBetweenFlowPath = 0;
	long endAddEdgeBetweenFlowPath = 0;

	if (measureONOSFlowEntryTimeProp) {
		startAddFlowEntry = System.nanoTime();
	}

	IFlowEntry flowEntryObj = null;
	boolean found = false;
	try {
	    if ((flowEntryObj =
		dbHandler.searchFlowEntry(flowEntry.flowEntryId())) != null) {
		if (measureONOSFlowEntryTimeProp) {
			endSearchFlowEntry = System.nanoTime();
		}
		found = true;
	    } else {
		if (measureONOSFlowEntryTimeProp) {
		    startCreateNewFlowEntry = System.nanoTime();
		}
		flowEntryObj = dbHandler.newFlowEntry();
		if (measureONOSFlowEntryTimeProp) {
		    endCreateNewFlowEntry = System.nanoTime();
		}
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

	if (measureONOSFlowEntryTimeProp) {
	    startSetProperties = System.nanoTime();
	}
	//
	// Set the Flow Entry key:
	// - flowEntry.flowEntryId()
	//
	flowEntryObj.setFlowEntryId(flowEntry.flowEntryId().toString());
	flowEntryObj.setType("flow_entry");
	if (measureONOSFlowEntryTimeProp) {
	    numProperties += 2;
	}

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
	if (measureONOSFlowEntryTimeProp) {
	    startSearchSwitch = System.nanoTime();
	}
	ISwitchObject sw = dbHandler.searchSwitch(flowEntry.dpid().toString());
	if (measureONOSFlowEntryTimeProp) {
	    endSearchSwitch = System.nanoTime();
	}

	flowEntryObj.setIdleTimeout(flowEntry.idleTimeout());
	flowEntryObj.setHardTimeout(flowEntry.hardTimeout());
	flowEntryObj.setSwitchDpid(flowEntry.dpid().toString());
	if (measureONOSFlowEntryTimeProp) {
	    numProperties += 3;
	}

	if (measureONOSFlowEntryTimeProp) {
	    startAddEdgeToSwitch = System.nanoTime();
	}
	flowEntryObj.setSwitch(sw);
	if (measureONOSFlowEntryTimeProp) {
	    endAddEdgeToSwitch = System.nanoTime();
	}
	if (flowEntry.flowEntryMatch().matchInPort()) {
	    if (measureONOSFlowEntryTimeProp) {
		startSearchInPort = System.nanoTime();
	    }
	    IPortObject inport =
		    dbHandler.searchPort(flowEntry.dpid().toString(),
			    flowEntry.flowEntryMatch().inPort().value());
	    if (measureONOSFlowEntryTimeProp) {
		endSearchInPort = System.nanoTime();
	    }

	    flowEntryObj.setMatchInPort(flowEntry.flowEntryMatch().inPort().value());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }

	    if (measureONOSFlowEntryTimeProp) {
		startAddEdgeToInPort = System.nanoTime();
	    }
	    flowEntryObj.setInPort(inport);
	    if (measureONOSFlowEntryTimeProp) {
		endAddEdgeToInPort = System.nanoTime();
	    }
	}
	if (flowEntry.flowEntryMatch().matchSrcMac()) {
	    flowEntryObj.setMatchSrcMac(flowEntry.flowEntryMatch().srcMac().toString());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchDstMac()) {
	    flowEntryObj.setMatchDstMac(flowEntry.flowEntryMatch().dstMac().toString());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchEthernetFrameType()) {
	    flowEntryObj.setMatchEthernetFrameType(flowEntry.flowEntryMatch().ethernetFrameType());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchVlanId()) {
	    flowEntryObj.setMatchVlanId(flowEntry.flowEntryMatch().vlanId());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchVlanPriority()) {
	    flowEntryObj.setMatchVlanPriority(flowEntry.flowEntryMatch().vlanPriority());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchSrcIPv4Net()) {
	    flowEntryObj.setMatchSrcIPv4Net(flowEntry.flowEntryMatch().srcIPv4Net().toString());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchDstIPv4Net()) {
	    flowEntryObj.setMatchDstIPv4Net(flowEntry.flowEntryMatch().dstIPv4Net().toString());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchIpProto()) {
	    flowEntryObj.setMatchIpProto(flowEntry.flowEntryMatch().ipProto());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchIpToS()) {
	    flowEntryObj.setMatchIpToS(flowEntry.flowEntryMatch().ipToS());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchSrcTcpUdpPort()) {
	    flowEntryObj.setMatchSrcTcpUdpPort(flowEntry.flowEntryMatch().srcTcpUdpPort());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}
	if (flowEntry.flowEntryMatch().matchDstTcpUdpPort()) {
	    flowEntryObj.setMatchDstTcpUdpPort(flowEntry.flowEntryMatch().dstTcpUdpPort());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}

	for (FlowEntryAction fa : flowEntry.flowEntryActions().actions()) {
	    if (fa.actionOutput() != null) {
		if (measureONOSFlowEntryTimeProp) {
		    if ( startSearchOutPort != 0 ) log.error("Performance addFlowEntry(_,{},{}) -- Multiple output port action unexpected.", flowEntry.flowId(), flowEntry.flowEntryId());
		    startSearchOutPort = System.nanoTime();
		}
		IPortObject outport =
			dbHandler.searchPort(flowEntry.dpid().toString(),
				fa.actionOutput().port().value());
		if (measureONOSFlowEntryTimeProp) {
		    endSearchOutPort = System.nanoTime();
		}

		flowEntryObj.setActionOutputPort(fa.actionOutput().port().value());
		if (measureONOSFlowEntryTimeProp) {
		    ++numProperties;
		}

		if (measureONOSFlowEntryTimeProp) {
		    startAddEdgeToOutPort = System.nanoTime();
		}
		flowEntryObj.setOutPort(outport);
		if (measureONOSFlowEntryTimeProp) {
		    endAddEdgeToOutPort = System.nanoTime();
		}
	    }
	}
	if (! flowEntry.flowEntryActions().isEmpty()) {
	    flowEntryObj.setActions(flowEntry.flowEntryActions().toString());
	    if (measureONOSFlowEntryTimeProp) {
		++numProperties;
	    }
	}

	// TODO: Hacks with hard-coded state names!
	if (found)
	    flowEntryObj.setUserState("FE_USER_MODIFY");
	else
	    flowEntryObj.setUserState("FE_USER_ADD");
	flowEntryObj.setSwitchState(flowEntry.flowEntrySwitchState().toString());
	if (measureONOSFlowEntryTimeProp) {
	    numProperties += 2;
	}
	//
	// TODO: Take care of the FlowEntryErrorState.
	//
	if (measureONOSFlowEntryTimeProp) {
	    endSetProperties = System.nanoTime();
	}

	// Flow Entries edges:
	//   Flow
	//   NextFE (TODO)
	if (! found) {
	    if (measureONOSFlowEntryTimeProp) {
		startAddEdgeBetweenFlowPath = System.nanoTime();
	    }
	    flowObj.addFlowEntry(flowEntryObj);
	    flowEntryObj.setFlow(flowObj);
	    if (measureONOSFlowEntryTimeProp) {
		endAddEdgeBetweenFlowPath = System.nanoTime();
	    }
	}
	if (measureONOSFlowEntryTimeProp) {
	    endAddFlowEntry = System.nanoTime();

	    log.error("Performance addFlowEntry(_,{},{}) -- "
		    + "GrandTotal: {} "
		    + "SearchExistingFE: {} "
		    + "CreateNewFE: {} "
		    + "SetProp+Edge: {} #Props: {} "
		    + "SearchSwitch: {} "
		    + "AddEdgeToSwitch: {} "
		    + "SearchInPort: {} "
		    + "AddEdgeToInPort: {} "
		    + "SearchOutPort: {} "
		    + "AddEdgeToOutPort: {} "
		    + "AddEdgeBetweenFlowPath: {} "
		    , flowEntry.flowId(), flowEntry.flowEntryId()
		    , endAddFlowEntry - startAddFlowEntry
		    , endSearchFlowEntry - startAddFlowEntry
		    , endCreateNewFlowEntry - startCreateNewFlowEntry
		    , endSetProperties - startSetProperties, numProperties
		    , endSearchSwitch - startSearchSwitch
		    , endAddEdgeToSwitch - startAddEdgeToSwitch
		    , endSearchInPort - startSearchInPort
		    , endAddEdgeToInPort - startAddEdgeToInPort
		    , endSearchOutPort - startSearchOutPort
		    , endAddEdgeToOutPort - startAddEdgeToOutPort
		    , endAddEdgeBetweenFlowPath - startAddEdgeBetweenFlowPath
		    );
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
    static boolean deleteFlowEntry(DBOperation dbHandler,
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
    static boolean deleteAllFlows(DBOperation dbHandler) {
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

	// Delete all flows one-by-one
	for (FlowId flowId : allFlowIds) {
	    deleteFlow(dbHandler, flowId);
	}

	return true;
    }

    /**
     * Delete a previously added flow.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    static boolean deleteFlow(DBOperation dbHandler, FlowId flowId) {
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
    static FlowPath getFlow(DBOperation dbHandler, FlowId flowId) {
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
    static ArrayList<FlowPath> getAllFlows(DBOperation dbHandler) {
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
