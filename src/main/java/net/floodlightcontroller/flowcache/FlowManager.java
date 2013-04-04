package net.floodlightcontroller.flowcache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.INetMapStorage;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.flowcache.web.FlowWebRoutable;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.CallerId;
import net.floodlightcontroller.util.DataPath;
import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.DataPathEndpoints;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.FlowEntryAction;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowEntryMatch;
import net.floodlightcontroller.util.FlowEntrySwitchState;
import net.floodlightcontroller.util.FlowEntryUserState;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;
import net.floodlightcontroller.util.IPv4Net;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;
import net.floodlightcontroller.util.Port;
import net.floodlightcontroller.util.SwitchPort;
import net.onrc.onos.flow.IFlowManager;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBConnection.Transaction;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowManager implements IFloodlightModule, IFlowService, IFlowManager, INetMapStorage {

    public GraphDBConnection conn;

    protected IRestApiService restApi;
    protected IFloodlightProviderService floodlightProvider;
    protected ITopoRouteService topoRouteService;
    protected FloodlightModuleContext context;

    protected OFMessageDamper messageDamper;

    //
    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    protected static final int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static final int OFMESSAGE_DAMPER_TIMEOUT = 250;	// ms
    public static final short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0;	// infinity
    public static final short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;	// infinite
    public static final short PRIORITY_DEFAULT = 100;

    // Flow Entry ID generation state
    private static Random randomGenerator = new Random();
    private static int nextFlowEntryIdPrefix = 0;
    private static int nextFlowEntryIdSuffix = 0;
    private static long nextFlowEntryId = 0;

    private static long measurementFlowId = 100000;
    private static String measurementFlowIdStr = "0x186a0";	// 100000
    private long modifiedMeasurementFlowTime = 0;

    /** The logger. */
    private static Logger log = LoggerFactory.getLogger(FlowManager.class);

    // The periodic task(s)
    private final ScheduledExecutorService measureShortestPathScheduler =
	Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService measureMapReaderScheduler =
	Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService mapReaderScheduler =
	Executors.newScheduledThreadPool(1);

    private BlockingQueue<Runnable> shortestPathQueue = new LinkedBlockingQueue<Runnable>();
    private ThreadPoolExecutor shortestPathExecutor =
	new ThreadPoolExecutor(10, 10, 5, TimeUnit.SECONDS, shortestPathQueue);

    class ShortestPathTask implements Runnable {
	private int hint;
	private ITopoRouteService topoRouteService;
	private ArrayList<DataPath> dpList;

	public ShortestPathTask(int hint,
				ITopoRouteService topoRouteService,
				ArrayList<DataPath> dpList) {
	    this.hint = hint;
	    this.topoRouteService = topoRouteService;
	    this.dpList = dpList;
	}

	@Override
	public void run() {
	    /*
	    String logMsg = "MEASUREMENT: Running Thread hint " + this.hint;
	    log.debug(logMsg);
	    long startTime = System.nanoTime();
	    */
	    for (DataPath dp : this.dpList) {
		topoRouteService.getTopoShortestPath(dp.srcPort(), dp.dstPort());
	    }
	    /*
	    long estimatedTime = System.nanoTime() - startTime;
	    double rate = (estimatedTime > 0)? ((double)dpList.size() * 1000000000) / estimatedTime: 0.0;
	    logMsg = "MEASUREMENT: Computed Thread hint " + hint + ": " + dpList.size() + " shortest paths in " + (double)estimatedTime / 1000000000 + " sec: " + rate + " flows/s";
	    log.debug(logMsg);
	    */
	}
    }

    final Runnable measureShortestPath = new Runnable() {
	    public void run() {
		log.debug("Recomputing Shortest Paths from the Network Map Flows...");
		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}

		if (topoRouteService == null) {
		    log.debug("Topology Route Service not found");
		    return;
		}

		int leftoverQueueSize = shortestPathExecutor.getQueue().size();
		if (leftoverQueueSize > 0) {
		    String logMsg = "MEASUREMENT: Leftover Shortest Path Queue Size: " + leftoverQueueSize;
		    log.debug(logMsg);
		    return;
		}
		log.debug("MEASUREMENT: Beginning Shortest Path Computation");

		//
		// Recompute the Shortest Paths for all Flows
		//
		int counter = 0;
		int hint = 0;
		ArrayList<DataPath> dpList = new ArrayList<DataPath>();
		long startTime = System.nanoTime();

		topoRouteService.prepareShortestPathTopo();

		Iterable<IFlowPath> allFlowPaths = conn.utils().getAllFlowPaths(conn);
		for (IFlowPath flowPathObj : allFlowPaths) {
		    FlowId flowId = new FlowId(flowPathObj.getFlowId());

		    // log.debug("Found Path {}", flowId.toString());
		    Dpid srcDpid = new Dpid(flowPathObj.getSrcSwitch());
		    Port srcPort = new Port(flowPathObj.getSrcPort());
		    Dpid dstDpid = new Dpid(flowPathObj.getDstSwitch());
		    Port dstPort = new Port(flowPathObj.getDstPort());
		    SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
		    SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);

		    /*
		    DataPath dp = new DataPath();
		    dp.setSrcPort(srcSwitchPort);
		    dp.setDstPort(dstSwitchPort);
		    dpList.add(dp);
		    if ((dpList.size() % 10) == 0) {
			shortestPathExecutor.execute(
				new ShortestPathTask(hint, topoRouteService,
						     dpList));
			dpList = new ArrayList<DataPath>();
			hint++;
		    }
		    */

		    DataPath dataPath =
			topoRouteService.getTopoShortestPath(srcSwitchPort,
							     dstSwitchPort);
		    counter++;
		}
		if (dpList.size() > 0) {
		    shortestPathExecutor.execute(
			new ShortestPathTask(hint, topoRouteService,
					     dpList));
		}

		/*
		// Wait for all tasks to finish
		try {
		    while (shortestPathExecutor.getQueue().size() > 0) {
			Thread.sleep(100);
		    }
		} catch (InterruptedException ex) {
		    log.debug("MEASUREMENT: Shortest Path Computation interrupted");
		}
		*/

		conn.endTx(Transaction.COMMIT);
		topoRouteService.dropShortestPathTopo();

		long estimatedTime = System.nanoTime() - startTime;
		double rate = (estimatedTime > 0)? ((double)counter * 1000000000) / estimatedTime: 0.0;
		String logMsg = "MEASUREMENT: Computed " + counter + " shortest paths in " + (double)estimatedTime / 1000000000 + " sec: " + rate + " flows/s";
		log.debug(logMsg);
	    }
	};

    final Runnable measureMapReader = new Runnable() {
	    public void run() {
		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}

		//
		// Fetch all Flow Entries
		//
		int counter = 0;
		long startTime = System.nanoTime();
		Iterable<IFlowEntry> allFlowEntries = conn.utils().getAllFlowEntries(conn);
		for (IFlowEntry flowEntryObj : allFlowEntries) {
		    counter++;
		    FlowEntryId flowEntryId =
			new FlowEntryId(flowEntryObj.getFlowEntryId());
		    String userState = flowEntryObj.getUserState();
		    String switchState = flowEntryObj.getSwitchState();
		}
		conn.endTx(Transaction.COMMIT);

		long estimatedTime = System.nanoTime() - startTime;
		double rate = (estimatedTime > 0)? ((double)counter * 1000000000) / estimatedTime: 0.0;
		String logMsg = "MEASUREMENT: Fetched " + counter + " flow entries in " + (double)estimatedTime / 1000000000 + " sec: " + rate + " entries/s";
		log.debug(logMsg);
	    }
	};

    final Runnable mapReader = new Runnable() {
	    public void run() {
		long startTime = System.nanoTime();
		int counterAllFlowEntries = 0;
		int counterMyNotUpdatedFlowEntries = 0;
		int counterAllFlowPaths = 0;
		int counterMyFlowPaths = 0;

		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}
		Map<Long, IOFSwitch> mySwitches =
		    floodlightProvider.getSwitches();
		Map<Long, IFlowEntry> myFlowEntries =
		    new TreeMap<Long, IFlowEntry>();
		LinkedList<IFlowEntry> deleteFlowEntries =
		    new LinkedList<IFlowEntry>();


		//
		// Fetch all Flow Entries and select only my Flow Entries
		// that need to be updated into the switches.
		//
		Iterable<IFlowEntry> allFlowEntries =
		    conn.utils().getAllFlowEntries(conn);
		for (IFlowEntry flowEntryObj : allFlowEntries) {
		    counterAllFlowEntries++;
		    String flowEntryIdStr = flowEntryObj.getFlowEntryId();
		    String userState = flowEntryObj.getUserState();
		    String switchState = flowEntryObj.getSwitchState();
		    String dpidStr = flowEntryObj.getSwitchDpid();
		    if ((flowEntryIdStr == null) ||
			(userState == null) ||
			(switchState == null) ||
			(dpidStr == null)) {
			log.debug("IGNORING Flow Entry entry with null fields");
			continue;
		    }
		    FlowEntryId flowEntryId = new FlowEntryId(flowEntryIdStr);
		    Dpid dpid = new Dpid(dpidStr);

		    /*
		    log.debug("Found Flow Entry Id = {} {}",
			      flowEntryId.toString(),
			      "DPID = " + dpid.toString() +
			      " User State: " + userState +
			      " Switch State: " + switchState);
		    */

		    if (! switchState.equals("FE_SWITCH_NOT_UPDATED"))
			continue;	// Ignore the entry: nothing to do

		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null)
			continue;	// Ignore the entry: not my switch

		    myFlowEntries.put(flowEntryId.value(), flowEntryObj);
		}

		log.debug("MEASUREMENT: Found {} My Flow Entries NOT_UPDATED",
			  myFlowEntries.size());

		//
		// Process my Flow Entries
		//
		boolean processed_measurement_flow = false;
		for (Map.Entry<Long, IFlowEntry> entry : myFlowEntries.entrySet()) {
		    counterMyNotUpdatedFlowEntries++;
		    IFlowEntry flowEntryObj = entry.getValue();
		    IFlowPath flowObj =
			conn.utils().getFlowPathByFlowEntry(conn,
							    flowEntryObj);
		    if (flowObj == null)
			continue;		// Should NOT happen

		    // Code for measurement purpose
		    {
			if (flowObj.getFlowId().equals(measurementFlowIdStr)) {
			    processed_measurement_flow = true;
			}
		    }

		    //
		    // TODO: Eliminate the re-fetching of flowEntryId,
		    // userState, switchState, and dpid from the flowEntryObj.
		    //
		    FlowEntryId flowEntryId =
			new FlowEntryId(flowEntryObj.getFlowEntryId());
		    Dpid dpid = new Dpid(flowEntryObj.getSwitchDpid());
		    String userState = flowEntryObj.getUserState();
		    String switchState = flowEntryObj.getSwitchState();
		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null)
			continue;		// Shouldn't happen

		    //
		    // Create the Open Flow Flow Modification Entry to push
		    //
		    OFFlowMod fm =
			(OFFlowMod) floodlightProvider.getOFMessageFactory()
			.getMessage(OFType.FLOW_MOD);
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
			continue;
		    }

		    //
		    // Fetch the match conditions.
		    //
		    // NOTE: The Flow matching conditions common for all
		    // Flow Entries are used ONLY if a Flow Entry does NOT
		    // have the corresponding matching condition set.
		    //
		    OFMatch match = new OFMatch();
		    match.setWildcards(OFMatch.OFPFW_ALL);
		    //
		    Short matchInPort = flowEntryObj.getMatchInPort();
		    if (matchInPort != null) {
			match.setInputPort(matchInPort);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
		    }
		    //
		    Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
		    if (matchEthernetFrameType == null)
			matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
		    if (matchEthernetFrameType != null) {
			match.setDataLayerType(matchEthernetFrameType);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
		    }
		    //
		    String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
		    if (matchSrcIPv4Net == null)
			matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
		    if (matchSrcIPv4Net != null) {
			match.setFromCIDR(matchSrcIPv4Net, OFMatch.STR_NW_SRC);
		    }
		    //
		    String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
		    if (matchDstIPv4Net == null)
			matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
		    if (matchDstIPv4Net != null) {
			match.setFromCIDR(matchDstIPv4Net, OFMatch.STR_NW_DST);
		    }
		    //
		    String matchSrcMac = flowEntryObj.getMatchSrcMac();
		    if (matchSrcMac == null)
			matchSrcMac = flowObj.getMatchSrcMac();
		    if (matchSrcMac != null) {
			match.setDataLayerSource(matchSrcMac);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
		    }
		    //
		    String matchDstMac = flowEntryObj.getMatchDstMac();
		    if (matchDstMac == null)
			matchDstMac = flowObj.getMatchDstMac();
		    if (matchDstMac != null) {
			match.setDataLayerDestination(matchDstMac);
			match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
		    }

		    //
		    // Fetch the actions
		    //
		    List<OFAction> actions = new ArrayList<OFAction>();
		    Short actionOutputPort = flowEntryObj.getActionOutput();
		    if (actionOutputPort != null) {
			OFActionOutput action = new OFActionOutput();
			// XXX: The max length is hard-coded for now
			action.setMaxLength((short)0xffff);
			action.setPort(actionOutputPort);
			actions.add(action);
		    }

		    fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
			.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
			.setPriority(PRIORITY_DEFAULT)
			.setBufferId(OFPacketOut.BUFFER_ID_NONE)
			.setCookie(cookie)
			.setCommand(flowModCommand)
			.setMatch(match)
			.setActions(actions)
			.setLengthU(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH);
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
			if (userState.equals("FE_USER_DELETE")) {
			    // An entry that needs to be deleted.
			    deleteFlowEntries.add(flowEntryObj);
			}
		    } catch (IOException e) {
			log.error("Failure writing flow mod from network map", e);
		    }
		}

		log.debug("MEASUREMENT: Found {} Flow Entries to delete",
			  deleteFlowEntries.size());

		//
		// Delete all entries marked for deletion
		//
		// TODO: We should use the OpenFlow Barrier mechanism
		// to check for errors, and delete the Flow Entries after the
		// Barrier message is received.
		//
		while (! deleteFlowEntries.isEmpty()) {
		    IFlowEntry flowEntryObj = deleteFlowEntries.poll();
		    IFlowPath flowObj =
			conn.utils().getFlowPathByFlowEntry(conn, flowEntryObj);
		    if (flowObj == null) {
			log.debug("Did not find FlowPath to be deleted");
			continue;
		    }
		    flowObj.removeFlowEntry(flowEntryObj);
		    conn.utils().removeFlowEntry(conn, flowEntryObj);

		    // Test whether the last flow entry
		    Iterable<IFlowEntry> tmpflowEntries =
			flowObj.getFlowEntries();
		    boolean found = false;
		    for (IFlowEntry tmpflowEntryObj : tmpflowEntries) {
			found = true;
			break;
		    }
		    if (! found) {
			// Remove the Flow Path as well
			conn.utils().removeFlowPath(conn, flowObj);
		    }
		}


		//
		// Fetch and recompute the Shortest Path for those
		// Flow Paths this controller is responsible for.
		//
		topoRouteService.prepareShortestPathTopo();
		Iterable<IFlowPath> allFlowPaths = conn.utils().getAllFlowPaths(conn);
		HashSet<IFlowPath> flowObjSet = new HashSet<IFlowPath>();
		for (IFlowPath flowPathObj : allFlowPaths) {
		    counterAllFlowPaths++;
		    if (flowPathObj == null)
			continue;
		    String dataPathSummaryStr = flowPathObj.getDataPathSummary();
		    if (dataPathSummaryStr == null)
			continue;	// Could be invalid entry?
		    if (dataPathSummaryStr.isEmpty())
			continue;	// No need to maintain this flow

		    // Fetch the fields needed to recompute the shortest path
		    String flowIdStr = flowPathObj.getFlowId();
		    String srcDpidStr = flowPathObj.getSrcSwitch();
		    Short srcPortShort = flowPathObj.getSrcPort();
		    String dstDpidStr = flowPathObj.getDstSwitch();
		    Short dstPortShort = flowPathObj.getDstPort();
		    if ((flowIdStr == null) ||
			(srcDpidStr == null) ||
			(srcPortShort == null) ||
			(dstDpidStr == null) ||
			(dstPortShort == null)) {
			log.debug("IGNORING Flow Path entry with null fields");
			continue;
		    }

		    FlowId flowId = new FlowId(flowIdStr);
		    Dpid srcDpid = new Dpid(srcDpidStr);
		    Port srcPort = new Port(srcPortShort);
		    Dpid dstDpid = new Dpid(dstDpidStr);
		    Port dstPort = new Port(dstPortShort);
		    SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
		    SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);

		    //
		    // Use the source DPID as a heuristic to decide
		    // which controller is responsible for maintaining the
		    // shortest path.
		    // NOTE: This heuristic is error-prone: if the switch
		    // goes away and no controller is responsible for that
		    // switch, then the original Flow Path is not cleaned-up
		    //
		    IOFSwitch mySwitch = mySwitches.get(srcDpid.value());
		    if (mySwitch == null)
			continue;	// Ignore: not my responsibility

		    counterMyFlowPaths++;

		    //
		    // NOTE: Using here the regular getShortestPath() method
		    // won't work here, because that method calls internally
		    //  "conn.endTx(Transaction.COMMIT)", and that will
		    // invalidate all handlers to the Titan database.
		    // If we want to experiment with calling here
		    // getShortestPath(), we need to refactor that code
		    // to avoid closing the transaction.
		    //
		    DataPath dataPath =
			topoRouteService.getTopoShortestPath(srcSwitchPort,
							     dstSwitchPort);
		    if (dataPath == null) {
			// We need the DataPath to compare the paths
			dataPath = new DataPath();
			dataPath.setSrcPort(srcSwitchPort);
			dataPath.setDstPort(dstSwitchPort);
		    }

		    String newDataPathSummaryStr = dataPath.dataPathSummary();
		    if (dataPathSummaryStr.equals(newDataPathSummaryStr))
			continue;	// Nothing changed

		    log.debug("RECONCILE: Need to Reconcile Shortest Path for FlowID {}",
			      flowId.toString());
		    flowObjSet.add(flowPathObj);
		}
		log.debug("MEASUREMENT: Found {} Flow Entries to reconcile",
			  flowObjSet.size());
		reconcileFlows(flowObjSet);
		topoRouteService.dropShortestPathTopo();


		conn.endTx(Transaction.COMMIT);

		if (processed_measurement_flow) {
		    long estimatedTime = System.nanoTime() - modifiedMeasurementFlowTime;
		    String logMsg = "MEASUREMENT: Pushed Flow delay: " +
			(double)estimatedTime / 1000000000 + " sec";
		    log.debug(logMsg);
		}

		long estimatedTime = System.nanoTime() - startTime;
		double rate = (estimatedTime > 0)? ((double)counterAllFlowPaths * 1000000000) / estimatedTime: 0.0;
		String logMsg = "MEASUREMENT: Processed AllFlowEntries: " + counterAllFlowEntries + " MyNotUpdatedFlowEntries: " + counterMyNotUpdatedFlowEntries + " AllFlowPaths: " + counterAllFlowPaths + " MyFlowPaths: " + counterMyFlowPaths + " in " + (double)estimatedTime / 1000000000 + " sec: " + rate + " paths/s";
		log.debug(logMsg);
	    }
	};

    /*
    final ScheduledFuture<?> measureShortestPathHandle =
	measureShortestPathScheduler.scheduleAtFixedRate(measureShortestPath, 10, 10, TimeUnit.SECONDS);
    */

    /*
    final ScheduledFuture<?> measureMapReaderHandle =
	measureMapReaderScheduler.scheduleAtFixedRate(measureMapReader, 10, 10, TimeUnit.SECONDS);
    */

    final ScheduledFuture<?> mapReaderHandle =
	mapReaderScheduler.scheduleAtFixedRate(mapReader, 3, 3, TimeUnit.SECONDS);

    @Override
    public void init(String conf) {
	conn = GraphDBConnection.getInstance(conf);
    }

    public void finalize() {
	close();
    }

    @Override
    public void close() {
	conn.close();
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = 
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFlowService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> 
			       getServiceImpls() {
        Map<Class<? extends IFloodlightService>,
        IFloodlightService> m = 
            new HashMap<Class<? extends IFloodlightService>,
                IFloodlightService>();
        m.put(IFlowService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> 
                                                    getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
	    new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	l.add(ITopoRouteService.class);
	l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
	throws FloodlightModuleException {
	this.context = context;
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	topoRouteService = context.getServiceImpl(ITopoRouteService.class);
	restApi = context.getServiceImpl(IRestApiService.class);
	messageDamper = new OFMessageDamper(OFMESSAGE_DAMPER_CAPACITY,
					    EnumSet.of(OFType.FLOW_MOD),
					    OFMESSAGE_DAMPER_TIMEOUT);
	// TODO: An ugly hack!
	String conf = "/tmp/cassandra.titan";
	this.init(conf);
    }

    private long getNextFlowEntryId() {
	//
	// Generate the next Flow Entry ID.
	// NOTE: For now, the higher 32 bits are random, and
	// the lower 32 bits are sequential.
	// In the future, we need a better allocation mechanism.
	//
	if ((nextFlowEntryIdSuffix & 0xffffffffL) == 0xffffffffL) {
	    nextFlowEntryIdPrefix = randomGenerator.nextInt();
	    nextFlowEntryIdSuffix = 0;
	} else {
	    nextFlowEntryIdSuffix++;
	}
	long result = (long)nextFlowEntryIdPrefix << 32;
	result = result | (0xffffffffL & nextFlowEntryIdSuffix);
	return result;
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	restApi.addRestletRoutable(new FlowWebRoutable());

	// Initialize the Flow Entry ID generator
	nextFlowEntryIdPrefix = randomGenerator.nextInt();
    }

    /**
     * Add a flow.
     *
     * Internally, ONOS will automatically register the installer for
     * receiving Flow Path Notifications for that path.
     *
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @param dataPathSummaryStr the data path summary string if the added
     * flow will be maintained internally, otherwise null.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean addFlow(FlowPath flowPath, FlowId flowId,
			   String dataPathSummaryStr) {
	if (flowPath.flowId().value() == measurementFlowId) {
	    modifiedMeasurementFlowTime = System.nanoTime();
	}

	//
	// Assign the FlowEntry IDs
	// Right now every new flow entry gets a new flow entry ID
	// TODO: This needs to be redesigned!
	//
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    long id = getNextFlowEntryId();
	    flowEntry.setFlowEntryId(new FlowEntryId(id));
	}

	IFlowPath flowObj = null;
	try {
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowPath.flowId()))
		!= null) {
		log.debug("Adding FlowPath with FlowId {}: found existing FlowPath",
			  flowPath.flowId().toString());
	    } else {
		flowObj = conn.utils().newFlowPath(conn);
		log.debug("Adding FlowPath with FlowId {}: creating new FlowPath",
			  flowPath.flowId().toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":addFlow FlowId:{} failed",
		      flowPath.flowId().toString());
	}
	if (flowObj == null) {
	    log.error(":addFlow FlowId:{} failed: Flow object not created",
		      flowPath.flowId().toString());
	    conn.endTx(Transaction.ROLLBACK);
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
	// - flowPath.dataPath().srcPort()
	// - flowPath.dataPath().dstPort()
	// - flowPath.matchEthernetFrameType()
	// - flowPath.matchSrcIPv4Net()
	// - flowPath.matchDstIPv4Net()
	// - flowPath.matchSrcMac()
	// - flowPath.matchDstMac()
	//
	flowObj.setInstallerId(flowPath.installerId().toString());
	flowObj.setSrcSwitch(flowPath.dataPath().srcPort().dpid().toString());
	flowObj.setSrcPort(flowPath.dataPath().srcPort().port().value());
	flowObj.setDstSwitch(flowPath.dataPath().dstPort().dpid().toString());
	flowObj.setDstPort(flowPath.dataPath().dstPort().port().value());
	if (flowPath.flowEntryMatch().matchEthernetFrameType()) {
	    flowObj.setMatchEthernetFrameType(flowPath.flowEntryMatch().ethernetFrameType());
	}
	if (flowPath.flowEntryMatch().matchSrcIPv4Net()) {
	    flowObj.setMatchSrcIPv4Net(flowPath.flowEntryMatch().srcIPv4Net().toString());
	}
	if (flowPath.flowEntryMatch().matchDstIPv4Net()) {
	    flowObj.setMatchDstIPv4Net(flowPath.flowEntryMatch().dstIPv4Net().toString());
	}
	if (flowPath.flowEntryMatch().matchSrcMac()) {
	    flowObj.setMatchSrcMac(flowPath.flowEntryMatch().srcMac().toString());
	}
	if (flowPath.flowEntryMatch().matchDstMac()) {
	    flowObj.setMatchDstMac(flowPath.flowEntryMatch().dstMac().toString());
	}

	if (dataPathSummaryStr != null) {
	    flowObj.setDataPathSummary(dataPathSummaryStr);
	} else {
	    flowObj.setDataPathSummary("");
	}

	// Flow edges:
	//   HeadFE


	//
	// Flow Entries:
	// flowPath.dataPath().flowEntries()
	//
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    IFlowEntry flowEntryObj = null;
	    boolean found = false;
	    try {
		if ((flowEntryObj = conn.utils().searchFlowEntry(conn, flowEntry.flowEntryId())) != null) {
		    log.debug("Adding FlowEntry with FlowEntryId {}: found existing FlowEntry",
			      flowEntry.flowEntryId().toString());
		    found = true;
		} else {
		    flowEntryObj = conn.utils().newFlowEntry(conn);
		    log.debug("Adding FlowEntry with FlowEntryId {}: creating new FlowEntry",
			      flowEntry.flowEntryId().toString());
		}
	    } catch (Exception e) {
		// TODO: handle exceptions
		conn.endTx(Transaction.ROLLBACK);
		log.error(":addFlow FlowEntryId:{} failed",
			  flowEntry.flowEntryId().toString());
	    }
	    if (flowEntryObj == null) {
		log.error(":addFlow FlowEntryId:{} failed: FlowEntry object not created",
		      flowEntry.flowEntryId().toString());
		conn.endTx(Transaction.ROLLBACK);
		return false;
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
	    // - flowEntry.flowEntryMatch()
	    // - flowEntry.flowEntryActions()
	    // - flowEntry.dpid()
	    // - flowEntry.flowEntryUserState()
	    // - flowEntry.flowEntrySwitchState()
	    // - flowEntry.flowEntryErrorState()
	    // - flowEntry.matchInPort()
	    // - flowEntry.matchEthernetFrameType()
	    // - flowEntry.matchSrcIPv4Net()
	    // - flowEntry.matchDstIPv4Net()
	    // - flowEntry.matchSrcMac()
	    // - flowEntry.matchDstMac()
	    // - flowEntry.actionOutput()
	    //
	    ISwitchObject sw =
		conn.utils().searchSwitch(conn, flowEntry.dpid().toString());
	    flowEntryObj.setSwitchDpid(flowEntry.dpid().toString());
	    flowEntryObj.setSwitch(sw);
	    if (flowEntry.flowEntryMatch().matchInPort()) {
	    	IPortObject inport =
		    conn.utils().searchPort(conn, flowEntry.dpid().toString(),
					    flowEntry.flowEntryMatch().inPort().value());
	    	flowEntryObj.setMatchInPort(flowEntry.flowEntryMatch().inPort().value());
	    	flowEntryObj.setInPort(inport);
	    }
	    if (flowEntry.flowEntryMatch().matchEthernetFrameType()) {
	    	flowEntryObj.setMatchEthernetFrameType(flowEntry.flowEntryMatch().ethernetFrameType());
	    }
	    if (flowEntry.flowEntryMatch().matchSrcIPv4Net()) {
	    	flowEntryObj.setMatchSrcIPv4Net(flowEntry.flowEntryMatch().srcIPv4Net().toString());
	    }
	    if (flowEntry.flowEntryMatch().matchDstIPv4Net()) {
	    	flowEntryObj.setMatchDstIPv4Net(flowEntry.flowEntryMatch().dstIPv4Net().toString());
	    }
	    if (flowEntry.flowEntryMatch().matchSrcMac()) {
	    	flowEntryObj.setMatchSrcMac(flowEntry.flowEntryMatch().srcMac().toString());
	    }
	    if (flowEntry.flowEntryMatch().matchDstMac()) {
	    	flowEntryObj.setMatchDstMac(flowEntry.flowEntryMatch().dstMac().toString());
	    }

	    for (FlowEntryAction fa : flowEntry.flowEntryActions()) {
	    	if (fa.actionOutput() != null) {
		    IPortObject outport =
			conn.utils().searchPort(conn,
						flowEntry.dpid().toString(),
						fa.actionOutput().port().value());
		    flowEntryObj.setActionOutput(fa.actionOutput().port().value());
		    flowEntryObj.setOutPort(outport);
	    	}
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
	}
	conn.endTx(Transaction.COMMIT);

	//
	// TODO: We need a proper Flow ID allocation mechanism.
	//
	flowId.setValue(flowPath.flowId().value());

	return true;
    }

    /**
     * Delete a previously added flow.
     *
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteFlow(FlowId flowId) {
	if (flowId.value() == measurementFlowId) {
	    modifiedMeasurementFlowTime = System.nanoTime();
	}

	IFlowPath flowObj = null;
	//
	// We just mark the entries for deletion,
	// and let the switches remove each individual entry after
	// it has been removed from the switches.
	//
	try {
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowId))
		!= null) {
		log.debug("Deleting FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Deleting FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":deleteFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null) {
	    conn.endTx(Transaction.COMMIT);
	    return true;		// OK: No such flow
	}

	//
	// Find and mark for deletion all Flow Entries
	//
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
	    conn.utils().removeFlowPath(conn, flowObj);
	conn.endTx(Transaction.COMMIT);

	return true;
    }

    /**
     * Clear the state for a previously added flow.
     *
     * @param flowId the Flow ID of the flow to clear.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean clearFlow(FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowId))
		!= null) {
		log.debug("Clearing FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Clearing FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":clearFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null) {
	    conn.endTx(Transaction.COMMIT);
	    return true;		// OK: No such flow
	}

	//
	// Remove all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    flowObj.removeFlowEntry(flowEntryObj);
	    conn.utils().removeFlowEntry(conn, flowEntryObj);
	}
	// Remove the Flow itself
	conn.utils().removeFlowPath(conn, flowObj);
	conn.endTx(Transaction.COMMIT);

	return true;
    }

    /**
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    @Override
    public FlowPath getFlow(FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowId))
		!= null) {
		log.debug("Get FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Get FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":getFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null) {
	    conn.endTx(Transaction.COMMIT);
	    return null;		// Flow not found
	}

	//
	// Extract the Flow state
	//
	FlowPath flowPath = extractFlowPath(flowObj);
	conn.endTx(Transaction.COMMIT);

	return flowPath;
    }

    /**
     * Get all previously added flows by a specific installer for a given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows(CallerId installerId,
					   DataPathEndpoints dataPathEndpoints) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
	ArrayList<FlowPath> allFlows = getAllFlows();
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	if (allFlows == null) {
	    log.debug("Get FlowPaths for installerId{} and dataPathEndpoints{}: no FlowPaths found", installerId, dataPathEndpoints);
	    return flowPaths;
	}

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

	if (flowPaths.isEmpty()) {
	    log.debug("Get FlowPaths for installerId{} and dataPathEndpoints{}: no FlowPaths found", installerId, dataPathEndpoints);
	} else {
	    log.debug("Get FlowPaths for installerId{} and dataPathEndpoints{}: FlowPaths are found", installerId, dataPathEndpoints);
	}

	return flowPaths;
    }

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows(DataPathEndpoints dataPathEndpoints) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
    ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
    ArrayList<FlowPath> allFlows = getAllFlows();

	if (allFlows == null) {
	    log.debug("Get FlowPaths for dataPathEndpoints{}: no FlowPaths found", dataPathEndpoints);
	    return flowPaths;
	}

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

	if (flowPaths.isEmpty()) {
	    log.debug("Get FlowPaths for dataPathEndpoints{}: no FlowPaths found", dataPathEndpoints);
	} else {
	    log.debug("Get FlowPaths for dataPathEndpoints{}: FlowPaths are found", dataPathEndpoints);
	}

	return flowPaths;
    }

    /**
     * Get summary of all installed flows by all installers in a given range
     *
     * @param flowId the data path endpoints of the flows to get.
     * @param maxFlows: the maximum number of flows to be returned
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlowsSummary(FlowId flowId, int maxFlows) {
		//
		// TODO: The implementation below is not optimal:
		// We fetch all flows, and then return only the subset that match
		// the query conditions.
		// We should use the appropriate Titan/Gremlin query to filter-out
		// the flows as appropriate.
		//
    	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
    	
    	ArrayList<FlowPath> allFlows = getAllFlows();
	
		if (allFlows == null) {
		    log.debug("Get FlowPathsSummary for {} {}: no FlowPaths found", flowId, maxFlows);
		    return flowPaths;
		}
	
		Collections.sort(allFlows);
		
		for (FlowPath flow : allFlows) {
		    flow.setFlowEntryMatch(null);
			
			// start from desired flowId
			//if (flow.flowId().value() < flowId.value()) {
			//	continue;
			//}
			
			// Summarize by making null flow entry fields that are not relevant to report
			for (FlowEntry flowEntry : flow.dataPath().flowEntries()) {
				flowEntry.setFlowEntryActions(null);
				flowEntry.setFlowEntryMatch(null);
			}
			
		    flowPaths.add(flow);
		    if (maxFlows != 0 && flowPaths.size() >= maxFlows) {
		    	break;
		    }
		}
	
		if (flowPaths.isEmpty()) {
		    log.debug("Get FlowPathsSummary {} {}: no FlowPaths found", flowId, maxFlows);
		} else {
		    log.debug("Get FlowPathsSummary for {} {}: FlowPaths were found", flowId, maxFlows);
		}
	
		return flowPaths;
    }
    
    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows() {
	Iterable<IFlowPath> flowPathsObj = null;
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	try {
	    if ((flowPathsObj = conn.utils().getAllFlowPaths(conn)) != null) {
		log.debug("Get all FlowPaths: found FlowPaths");
	    } else {
		log.debug("Get all FlowPaths: no FlowPaths found");
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":getAllFlowPaths failed");
	}
	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
	    conn.endTx(Transaction.COMMIT);
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

	conn.endTx(Transaction.COMMIT);

	return flowPaths;
    }

    /**
     * Extract Flow Path State from a Titan Database Object @ref IFlowPath.
     *
     * @param flowObj the object to extract the Flow Path State from.
     * @return the extracted Flow Path State.
     */
    private FlowPath extractFlowPath(IFlowPath flowObj) {
	FlowPath flowPath = new FlowPath();

	//
	// Extract the Flow state
	//
	String flowIdStr = flowObj.getFlowId();
	String installerIdStr = flowObj.getInstallerId();
	String srcSwitchStr = flowObj.getSrcSwitch();
	Short srcPortShort = flowObj.getSrcPort();
	String dstSwitchStr = flowObj.getDstSwitch();
	Short dstPortShort = flowObj.getDstPort();

	if ((flowIdStr == null) ||
	    (installerIdStr == null) ||
	    (srcSwitchStr == null) ||
	    (srcPortShort == null) ||
	    (dstSwitchStr == null) ||
	    (dstPortShort == null)) {
	    // TODO: A work-around, becauuse of some bogus database objects
	    return null;
	}

	flowPath.setFlowId(new FlowId(flowIdStr));
	flowPath.setInstallerId(new CallerId(installerIdStr));
	flowPath.dataPath().srcPort().setDpid(new Dpid(srcSwitchStr));
	flowPath.dataPath().srcPort().setPort(new Port(srcPortShort));
	flowPath.dataPath().dstPort().setDpid(new Dpid(dstSwitchStr));
	flowPath.dataPath().dstPort().setPort(new Port(dstPortShort));
	//
	// Extract the match conditions common for all Flow Entries
	//
	{
	    FlowEntryMatch match = new FlowEntryMatch();
	    Short matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
	    if (matchEthernetFrameType != null)
		match.enableEthernetFrameType(matchEthernetFrameType);
	    String matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
	    if (matchSrcIPv4Net != null)
		match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
	    String matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
	    if (matchDstIPv4Net != null)
		match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
	    String matchSrcMac = flowObj.getMatchSrcMac();
	    if (matchSrcMac != null)
		match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
	    String matchDstMac = flowObj.getMatchDstMac();
	    if (matchDstMac != null)
		match.enableDstMac(MACAddress.valueOf(matchDstMac));
	    flowPath.setFlowEntryMatch(match);
	}

	//
	// Extract all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    FlowEntry flowEntry = new FlowEntry();
	    String flowEntryIdStr = flowEntryObj.getFlowEntryId();
	    String switchDpidStr = flowEntryObj.getSwitchDpid();
	    String userState = flowEntryObj.getUserState();
	    String switchState = flowEntryObj.getSwitchState();

	    if ((flowEntryIdStr == null) ||
		(switchDpidStr == null) ||
		(userState == null) ||
		(switchState == null)) {
		// TODO: A work-around, becauuse of some bogus database objects
		continue;
	    }
	    flowEntry.setFlowEntryId(new FlowEntryId(flowEntryIdStr));
	    flowEntry.setDpid(new Dpid(switchDpidStr));

	    //
	    // Extract the match conditions
	    //
	    FlowEntryMatch match = new FlowEntryMatch();
	    Short matchInPort = flowEntryObj.getMatchInPort();
	    if (matchInPort != null)
		match.enableInPort(new Port(matchInPort));
	    Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
	    if (matchEthernetFrameType != null)
		match.enableEthernetFrameType(matchEthernetFrameType);
	    String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
	    if (matchSrcIPv4Net != null)
		match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
	    String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
	    if (matchDstIPv4Net != null)
		match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
	    String matchSrcMac = flowEntryObj.getMatchSrcMac();
	    if (matchSrcMac != null)
		match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
	    String matchDstMac = flowEntryObj.getMatchDstMac();
	    if (matchDstMac != null)
		match.enableDstMac(MACAddress.valueOf(matchDstMac));
	    flowEntry.setFlowEntryMatch(match);

	    //
	    // Extract the actions
	    //
	    ArrayList<FlowEntryAction> actions = new ArrayList<FlowEntryAction>();
	    Short actionOutputPort = flowEntryObj.getActionOutput();
	    if (actionOutputPort != null) {
		FlowEntryAction action = new FlowEntryAction();
		action.setActionOutput(new Port(actionOutputPort));
		actions.add(action);
	    }
	    flowEntry.setFlowEntryActions(actions);
	    flowEntry.setFlowEntryUserState(FlowEntryUserState.valueOf(userState));
	    flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.valueOf(switchState));
	    //
	    // TODO: Take care of the FlowEntryMatch, FlowEntryAction set,
	    // and FlowEntryErrorState.
	    //
	    flowPath.dataPath().flowEntries().add(flowEntry);
	}

	return flowPath;
    }

    /**
     * Add and maintain a shortest-path flow.
     *
     * NOTE: The Flow Path argument does NOT contain flow entries.
     *
     * @param flowPath the Flow Path with the endpoints and the match
     * conditions to install.
     * @return the added shortest-path flow on success, otherwise null.
     */
    @Override
    public FlowPath addAndMaintainShortestPathFlow(FlowPath flowPath) {
	String dataPathSummaryStr = null;

	//
	// Do the shortest path computation
	//
	DataPath dataPath =
	    topoRouteService.getShortestPath(flowPath.dataPath().srcPort(),
					     flowPath.dataPath().dstPort());
	if (dataPath == null) {
	    // We need the DataPath to populate the Network MAP
	    dataPath = new DataPath();
	    dataPath.setSrcPort(flowPath.dataPath().srcPort());
	    dataPath.setDstPort(flowPath.dataPath().dstPort());
	}

	// Compute the Data Path summary
	dataPathSummaryStr = dataPath.dataPathSummary();

	//
	// Set the incoming port matching and the outgoing port output
	// actions for each flow entry.
	//
	for (FlowEntry flowEntry : dataPath.flowEntries()) {
	    // Set the incoming port matching
	    FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
	    flowEntry.setFlowEntryMatch(flowEntryMatch);
	    flowEntryMatch.enableInPort(flowEntry.inPort());

	    // Set the outgoing port output action
	    ArrayList<FlowEntryAction> flowEntryActions = flowEntry.flowEntryActions();
	    if (flowEntryActions == null) {
		flowEntryActions = new ArrayList<FlowEntryAction>();
		flowEntry.setFlowEntryActions(flowEntryActions);
	    }
	    FlowEntryAction flowEntryAction = new FlowEntryAction();
	    flowEntryAction.setActionOutput(flowEntry.outPort());
	    flowEntryActions.add(flowEntryAction);
	}

	//
	// Prepare the computed Flow Path
	//
	FlowPath computedFlowPath = new FlowPath();
	computedFlowPath.setFlowId(new FlowId(flowPath.flowId().value()));
	computedFlowPath.setInstallerId(new CallerId(flowPath.installerId().value()));
	computedFlowPath.setDataPath(dataPath);
	computedFlowPath.setFlowEntryMatch(new FlowEntryMatch(flowPath.flowEntryMatch()));

	FlowId flowId = new FlowId();
	if (! addFlow(computedFlowPath, flowId, dataPathSummaryStr))
	    return null;

	// TODO: Mark the flow for maintenance purpose

	return (computedFlowPath);
    }

    /**
     * Create a Flow from port to port.
     *
     * TODO: We don't need it for now.
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     */
    @Override
    public void createFlow(IPortObject src_port, IPortObject dest_port) {
	// TODO: We don't need it for now.
    }

    /**
     * Get all Flows matching a source and a destination port.
     *
     * TODO: Pankaj might be implementing it later.
     *
     * @param src_port the source port to match.
     * @param dest_port the destination port to match.
     * @return all flows matching the source and the destination port.
     */
    @Override
    public Iterable<FlowPath> getFlows(IPortObject src_port,
				       IPortObject dest_port) {
	// TODO: Pankaj might be implementing it later.
	return null;
    }

    /**
     * Get all Flows going out from a port.
     *
     * TODO: We need it now: Pankaj
     *
     * @param port the port to match.
     * @return the list of flows that are going out from the port.
     */
    @Override
    public Iterable<FlowPath> getOutFlows(IPortObject port) {
	// TODO: We need it now: Pankaj
	return null;
    }

    /**
     * Reconcile all flows on inactive switch port.
     *
     * @param portObject the port that has become inactive.
     */
    @Override
    public void reconcileFlows(IPortObject portObject) {
	Iterable<IFlowEntry> inFlowEntries = portObject.getInFlowEntries();
	Iterable<IFlowEntry> outFlowEntries = portObject.getOutFlowEntries();

	//
	// Collect all affected Flow IDs from the affected flow entries
	//
	HashSet<IFlowPath> flowObjSet = new HashSet<IFlowPath>();
	for (IFlowEntry flowEntryObj: inFlowEntries) {
	    IFlowPath flowObj = flowEntryObj.getFlow();
	    if (flowObj != null)
		flowObjSet.add(flowObj);
	}
	for (IFlowEntry flowEntryObj: outFlowEntries) {
	    IFlowPath flowObj = flowEntryObj.getFlow();
	    if (flowObj != null)
		flowObjSet.add(flowObj);
	}

	// Reconcile the affected flows
	reconcileFlows(flowObjSet);
    }

    /**
     * Reconcile all flows in a set.
     *
     * @param flowObjSet the set of flows that need to be reconciliated.
     */
    public void reconcileFlows(Iterable<IFlowPath> flowObjSet) {
	if (! flowObjSet.iterator().hasNext())
	    return;

	//
	// Remove the old Flow Entries, and add the new Flow Entries
	//

	Map<Long, IOFSwitch> mySwitches = floodlightProvider.getSwitches();
	LinkedList<FlowPath> flowPaths = new LinkedList<FlowPath>();
	for (IFlowPath flowObj : flowObjSet) {
	    FlowPath flowPath = extractFlowPath(flowObj);
	    if (flowPath == null)
		continue;
	    flowPaths.add(flowPath);

	    //
	    // Remove the Flow Entries from the Network MAP
	    //
	    Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	    LinkedList<IFlowEntry> deleteFlowEntries = new LinkedList<IFlowEntry>();
	    for (IFlowEntry flowEntryObj : flowEntries) {
		String dpidStr = flowEntryObj.getSwitchDpid();
		if (dpidStr == null)
		    continue;
		Dpid dpid = new Dpid(dpidStr);
		/*
		IOFSwitch mySwitch = mySwitches.get(dpid.value());
		if (mySwitch == null)
		    continue;		// Ignore the entry: not my switch
		*/
		deleteFlowEntries.add(flowEntryObj);
	    }
	    for (IFlowEntry flowEntryObj : deleteFlowEntries) {
		flowObj.removeFlowEntry(flowEntryObj);
		conn.utils().removeFlowEntry(conn, flowEntryObj);
	    }
	}

	for (FlowPath flowPath : flowPaths) {
	    //
	    // Delete the flow entries from the switches
	    //
	    for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
		flowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_DELETE);
		IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
		if (mySwitch == null) {
		    // Not my switch
		    installRemoteFlowEntry(flowPath, flowEntry);
		} else {
		    installFlowEntry(mySwitch, flowPath, flowEntry);
		}
	    }

	    //
	    // Calculate the new shortest path and install it in the
	    // Network MAP.
	    //
	    FlowPath addedFlowPath = addAndMaintainShortestPathFlow(flowPath);
	    if (addedFlowPath == null) {
		log.error("Cannot add Shortest Path Flow from {} to {}: path not found?",
			  flowPath.dataPath().srcPort().toString(),
			  flowPath.dataPath().dstPort().toString());
		continue;
	    }

	    //
	    // Add the flow entries to the switches
	    //
	    for (FlowEntry flowEntry : addedFlowPath.dataPath().flowEntries()) {
		flowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_ADD);
		IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
		if (mySwitch == null) {
		    // Not my switch
		    installRemoteFlowEntry(addedFlowPath, flowEntry);
		    continue;
		}

		IFlowEntry flowEntryObj =
		    conn.utils().searchFlowEntry(conn, flowEntry.flowEntryId());
		if (flowEntryObj == null) {
		    //
		    // TODO: Remove the "new Object[] wrapper in the statement
		    // below after the SLF4J logger is upgraded to
		    // Version 1.7.5
		    //
		    log.error("Cannot add Flow Entry to switch {} for Path Flow from {} to {} : Flow Entry not in the Network MAP",
			      new Object[] {
				  flowEntry.dpid(),
				  flowPath.dataPath().srcPort(),
				  flowPath.dataPath().dstPort()
			      });
		    continue;
		}
		// Update the Flow Entry Switch State in the Network MAP
		if (installFlowEntry(mySwitch, addedFlowPath, flowEntry)) {
		    flowEntryObj.setSwitchState("FE_SWITCH_UPDATED");
		}
	    }
	}
    }

    /**
     * Reconcile all flows between a source and a destination port.
     *
     * TODO: We don't need it for now.
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     */
    @Override
    public void reconcileFlow(IPortObject src_port, IPortObject dest_port) {
	// TODO: We don't need it for now.
    }

    /**
     * Compute the shortest path between a source and a destination ports.
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     * @return the computed shortest path between the source and the
     * destination ports. The flow entries in the path itself would
     * contain the incoming port matching and the outgoing port output
     * actions set. However, the path itself will NOT have the Flow ID,
     * Installer ID, and any additional matching conditions for the
     * flow entries (e.g., source or destination MAC address, etc).
     */
    @Override
    public FlowPath computeFlowPath(IPortObject src_port,
				    IPortObject dest_port) {
	//
	// Prepare the arguments
	//
	String dpidStr = src_port.getSwitch().getDPID();
	Dpid srcDpid = new Dpid(dpidStr);
	Port srcPort = new Port(src_port.getNumber());

	dpidStr = dest_port.getSwitch().getDPID();
	Dpid dstDpid = new Dpid(dpidStr);
	Port dstPort = new Port(dest_port.getNumber());

	SwitchPort src = new SwitchPort(srcDpid, srcPort);
	SwitchPort dst = new SwitchPort(dstDpid, dstPort);

	//
	// Do the shortest path computation
	//
	DataPath dataPath = topoRouteService.getShortestPath(src, dst);
	if (dataPath == null)
	    return null;

	//
	// Set the incoming port matching and the outgoing port output
	// actions for each flow entry.
	//
	for (FlowEntry flowEntry : dataPath.flowEntries()) {
	    // Set the incoming port matching
	    FlowEntryMatch flowEntryMatch = flowEntry.flowEntryMatch();
	    if (flowEntryMatch == null) {
		flowEntryMatch = new FlowEntryMatch();
		flowEntry.setFlowEntryMatch(flowEntryMatch);
	    }
	    flowEntryMatch.enableInPort(flowEntry.inPort());

	    // Set the outgoing port output action
	    ArrayList<FlowEntryAction> flowEntryActions = flowEntry.flowEntryActions();
	    if (flowEntryActions == null) {
		flowEntryActions = new ArrayList<FlowEntryAction>();
		flowEntry.setFlowEntryActions(flowEntryActions);
	    }
	    FlowEntryAction flowEntryAction = new FlowEntryAction();
	    flowEntryAction.setActionOutput(flowEntry.outPort());
	    flowEntryActions.add(flowEntryAction);
	}

	//
	// Prepare the return result
	//
	FlowPath flowPath = new FlowPath();
	flowPath.setDataPath(dataPath);

	return flowPath;
    }

    /**
     * Get all Flow Entries of a Flow.
     *
     * @param flow the flow whose flow entries should be returned.
     * @return the flow entries of the flow.
     */
    @Override
    public Iterable<FlowEntry> getFlowEntries(FlowPath flow) {
	return flow.dataPath().flowEntries();
    }

    /**
     * Install a Flow Entry on a switch.
     *
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean installFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				    FlowEntry flowEntry) {
	//
	// Create the OpenFlow Flow Modification Entry to push
	//
	OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory()
	    .getMessage(OFType.FLOW_MOD);
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

	// Match the Ethernet Frame Type
	Short matchEthernetFrameType = flowEntryMatch.ethernetFrameType();
	if ((matchEthernetFrameType == null) && (flowPathMatch != null)) {
	    matchEthernetFrameType = flowPathMatch.ethernetFrameType();
	}
	if (matchEthernetFrameType != null) {
	    match.setDataLayerType(matchEthernetFrameType);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
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

	//
	// Fetch the actions
	//
	// TODO: For now we support only the "OUTPUT" actions.
	//
	fm.setOutPort(OFPort.OFPP_NONE.getValue());
	List<OFAction> actions = new ArrayList<OFAction>();
	ArrayList<FlowEntryAction> flowEntryActions =
	    flowEntry.flowEntryActions();
	for (FlowEntryAction flowEntryAction : flowEntryActions) {
	    FlowEntryAction.ActionOutput actionOutput =
		flowEntryAction.actionOutput();
	    if (actionOutput != null) {
		short actionOutputPort = actionOutput.port().value();
		OFActionOutput action = new OFActionOutput();
		// XXX: The max length is hard-coded for now
		action.setMaxLength((short)0xffff);
		action.setPort(actionOutputPort);
		actions.add(action);
		if ((flowModCommand == OFFlowMod.OFPFC_DELETE) ||
		    (flowModCommand == OFFlowMod.OFPFC_DELETE_STRICT)) {
		    fm.setOutPort(actionOutputPort);
		}
	    }
	}

	fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
	    .setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
	    .setPriority(PRIORITY_DEFAULT)
	    .setBufferId(OFPacketOut.BUFFER_ID_NONE)
	    .setCookie(cookie)
	    .setCommand(flowModCommand)
	    .setMatch(match)
	    .setActions(actions)
	    .setLengthU(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH);

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
	} catch (IOException e) {
	    log.error("Failure writing flow mod from network map", e);
	    return false;
	}
	return true;
    }

    /**
     * Remove a Flow Entry from a switch.
     *
     * @param mySwitch the switch to remove the Flow Entry from.
     * @param flowPath the flow path for the flow entry to remove.
     * @param flowEntry the flow entry to remove.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean removeFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				   FlowEntry flowEntry) {
	//
	// The installFlowEntry() method implements both installation
	// and removal of flow entries.
	//
	return (installFlowEntry(mySwitch, flowPath, flowEntry));
    }

    /**
     * Install a Flow Entry on a remote controller.
     *
     * TODO: We need it now: Jono
     * - For now it will make a REST call to the remote controller.
     * - Internally, it needs to know the name of the remote controller.
     *
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean installRemoteFlowEntry(FlowPath flowPath,
					  FlowEntry flowEntry) {
	// TODO: We need it now: Jono
	//  - For now it will make a REST call to the remote controller.
	//  - Internally, it needs to know the name of the remote controller.
	return true;
    }

    /**
     * Remove a flow entry on a remote controller.
     *
     * @param flowPath the flow path for the flow entry to remove.
     * @param flowEntry the flow entry to remove.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean removeRemoteFlowEntry(FlowPath flowPath,
					 FlowEntry flowEntry) {
	//
	// The installRemoteFlowEntry() method implements both installation
	// and removal of flow entries.
	//
	return (installRemoteFlowEntry(flowPath, flowEntry));
    }
}
