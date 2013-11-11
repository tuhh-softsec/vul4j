package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.floodlightlistener.INetworkGraphService;
import net.onrc.onos.ofcontroller.flowmanager.web.FlowWebRoutable;
import net.onrc.onos.ofcontroller.topology.ITopologyNetService;
import net.onrc.onos.ofcontroller.topology.Topology;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.*;

import org.openflow.protocol.OFMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager class for handling the network flows.
 */
public class FlowManager implements IFloodlightModule, IFlowService, INetMapStorage,
	IFlowPusherService {

    protected GraphDBOperation dbHandler;

    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile ITopologyNetService topologyNetService;
    protected volatile IDatagridService datagridService;
    protected IRestApiService restApi;
    protected FloodlightModuleContext context;
    protected PathComputation pathComputation;

    protected FlowPusher pusher;
    
    // Flow Entry ID generation state
    private static Random randomGenerator = new Random();
    private static int nextFlowEntryIdPrefix = 0;
    private static int nextFlowEntryIdSuffix = 0;
    private static long nextFlowEntryId = 0;

    /** The logger. */
    private final static Logger log = LoggerFactory.getLogger(FlowManager.class);

    // The periodic task(s)
    private ScheduledExecutorService mapReaderScheduler;
    private ScheduledExecutorService shortestPathReconcileScheduler;

    /**
     * Periodic task for reading the Flow Entries and pushing changes
     * into the switches.
     */
    final Runnable mapReader = new Runnable() {
	    public void run() {
		try {
		    runImpl();
		} catch (Exception e) {
		    log.debug("Exception processing All Flow Entries from the Network MAP: ", e);
		    dbHandler.rollback();
		    return;
		}
	    }

	    private void runImpl() {
		long startTime = System.nanoTime();
		int counterAllFlowEntries = 0;
		int counterMyNotUpdatedFlowEntries = 0;

		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}
		Map<Long, IOFSwitch> mySwitches =
		    floodlightProvider.getSwitches();
		if (mySwitches.isEmpty()) {
			log.trace("No switches controlled");
			return;
		}
		LinkedList<IFlowEntry> addFlowEntries =
		    new LinkedList<IFlowEntry>();
		LinkedList<IFlowEntry> deleteFlowEntries =
		    new LinkedList<IFlowEntry>();

		//
		// Fetch all Flow Entries which need to be updated and select
		// only my Flow Entries that need to be updated into the
		// switches.
		//
		Iterable<IFlowEntry> allFlowEntries =
		    dbHandler.getAllSwitchNotUpdatedFlowEntries();
		for (IFlowEntry flowEntryObj : allFlowEntries) {
			log.debug("flowEntryobj : {}", flowEntryObj);
			
		    counterAllFlowEntries++;

		    String dpidStr = flowEntryObj.getSwitchDpid();
		    if (dpidStr == null)
			continue;
		    Dpid dpid = new Dpid(dpidStr);
		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null)
			continue;	// Ignore the entry: not my switch

		    IFlowPath flowObj =
			dbHandler.getFlowPathByFlowEntry(flowEntryObj);
		    if (flowObj == null)
			continue;		// Should NOT happen
		    if (flowObj.getFlowId() == null)
			continue;		// Invalid entry

		    //
		    // NOTE: For now we process the DELETE before the ADD
		    // to cover the more common scenario.
		    // TODO: This is error prone and needs to be fixed!
		    //
		    String userState = flowEntryObj.getUserState();
		    if (userState == null)
			continue;
		    if (userState.equals("FE_USER_DELETE")) {
			// An entry that needs to be deleted.
			deleteFlowEntries.add(flowEntryObj);
			installFlowEntry(mySwitch, flowObj, flowEntryObj);
		    } else {
			addFlowEntries.add(flowEntryObj);
		    }
		    counterMyNotUpdatedFlowEntries++;
		}
		
		log.debug("addFlowEntries : {}", addFlowEntries);

		//
		// Process the Flow Entries that need to be added
		//
		for (IFlowEntry flowEntryObj : addFlowEntries) {
		    IFlowPath flowObj =
			dbHandler.getFlowPathByFlowEntry(flowEntryObj);
		    if (flowObj == null)
			continue;		// Should NOT happen
		    if (flowObj.getFlowId() == null)
			continue;		// Invalid entry

		    Dpid dpid = new Dpid(flowEntryObj.getSwitchDpid());
		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null)
			continue;		// Shouldn't happen
		    installFlowEntry(mySwitch, flowObj, flowEntryObj);
		}

		//
		// Delete all Flow Entries marked for deletion from the
		// Network MAP.
		//
		// TODO: We should use the OpenFlow Barrier mechanism
		// to check for errors, and delete the Flow Entries after the
		// Barrier message is received.
		//
		while (! deleteFlowEntries.isEmpty()) {
		    IFlowEntry flowEntryObj = deleteFlowEntries.poll();
		    IFlowPath flowObj =
			dbHandler.getFlowPathByFlowEntry(flowEntryObj);
		    if (flowObj == null) {
			log.debug("Did not find FlowPath to be deleted");
			continue;
		    }
		    flowObj.removeFlowEntry(flowEntryObj);
		    dbHandler.removeFlowEntry(flowEntryObj);
		}

		dbHandler.commit();

		long estimatedTime = System.nanoTime() - startTime;
		double rate = 0.0;
		if (estimatedTime > 0)
		    rate = ((double)counterAllFlowEntries * 1000000000) / estimatedTime;
		String logMsg = "MEASUREMENT: Processed AllFlowEntries: " +
		    counterAllFlowEntries + " MyNotUpdatedFlowEntries: " +
		    counterMyNotUpdatedFlowEntries + " in " +
		    (double)estimatedTime / 1000000000 + " sec: " +
		    rate + " paths/s";
		log.debug(logMsg);
	    }
	};

    /**
     * Periodic task for reading the Flow Paths and recomputing the
     * shortest paths.
     */
    final Runnable shortestPathReconcile = new Runnable() {
	    public void run() {
		try {
		    runImpl();
		} catch (Exception e) {
		    log.debug("Exception processing All Flows from the Network MAP: ", e);
		    dbHandler.rollback();
		    return;
		}
	    }

	    private void runImpl() {
		long startTime = System.nanoTime();
		int counterAllFlowPaths = 0;
		int counterMyFlowPaths = 0;

		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}
		Map<Long, IOFSwitch> mySwitches =
		    floodlightProvider.getSwitches();
		if (mySwitches.isEmpty()) {
			log.trace("No switches controlled");
			return;
		}
		LinkedList<IFlowPath> deleteFlows = new LinkedList<IFlowPath>();

		//
		// Fetch and recompute the Shortest Path for those
		// Flow Paths this controller is responsible for.
		//
		Topology topology = topologyNetService.newDatabaseTopology();
		Iterable<IFlowPath> allFlowPaths = dbHandler.getAllFlowPaths();
		for (IFlowPath flowPathObj : allFlowPaths) {
		    counterAllFlowPaths++;
		    if (flowPathObj == null)
			continue;

		    String srcDpidStr = flowPathObj.getSrcSwitch();
		    if (srcDpidStr == null)
			continue;
		    Dpid srcDpid = new Dpid(srcDpidStr);
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

		    // Test whether we need to maintain this flow
		    String flowPathTypeStr = flowPathObj.getFlowPathType();
		    if (flowPathTypeStr == null)
			continue;	// Could be invalid entry?
		    if (! flowPathTypeStr.equals("FP_TYPE_SHORTEST_PATH"))
			continue;	// No need to maintain this flow

		    //
		    // Test whether we need to complete the Flow cleanup,
		    // if the Flow has been deleted by the user.
		    //
		    String flowPathUserStateStr = flowPathObj.getFlowPathUserState();
		    if ((flowPathUserStateStr != null)
			&& flowPathUserStateStr.equals("FP_USER_DELETE")) {
			Iterable<IFlowEntry> flowEntries = flowPathObj.getFlowEntries();
			final boolean empty = !flowEntries.iterator().hasNext();
			if (empty)
			    deleteFlows.add(flowPathObj);
		    }

		    // Fetch the fields needed to recompute the shortest path
		    String dataPathSummaryStr = flowPathObj.getDataPathSummary();
		    Short srcPortShort = flowPathObj.getSrcPort();
		    String dstDpidStr = flowPathObj.getDstSwitch();
		    Short dstPortShort = flowPathObj.getDstPort();
		    Long flowPathFlagsLong = flowPathObj.getFlowPathFlags();
		    if ((dataPathSummaryStr == null) ||
			(srcPortShort == null) ||
			(dstDpidStr == null) ||
			(dstPortShort == null) ||
			(flowPathFlagsLong == null)) {
			continue;
		    }

		    Port srcPort = new Port(srcPortShort);
		    Dpid dstDpid = new Dpid(dstDpidStr);
		    Port dstPort = new Port(dstPortShort);
		    SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
		    SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);
		    FlowPathType flowPathType = FlowPathType.valueOf(flowPathTypeStr);
		    FlowPathUserState flowPathUserState = FlowPathUserState.valueOf(flowPathUserStateStr);
		    FlowPathFlags flowPathFlags = new FlowPathFlags(flowPathFlagsLong);

		    counterMyFlowPaths++;

		    //
		    // NOTE: Using here the regular getDatabaseShortestPath()
		    // method won't work here, because that method calls
		    // internally "conn.endTx(Transaction.COMMIT)", and that
		    // will invalidate all handlers to the Titan database.
		    // If we want to experiment with calling here
		    // getDatabaseShortestPath(), we need to refactor that code
		    // to avoid closing the transaction.
		    //
		    DataPath dataPath =
			topologyNetService.getTopologyShortestPath(
				topology,
				srcSwitchPort,
				dstSwitchPort);
		    if (dataPath == null) {
			// We need the DataPath to compare the paths
			dataPath = new DataPath();
			dataPath.setSrcPort(srcSwitchPort);
			dataPath.setDstPort(dstSwitchPort);
		    }
		    dataPath.applyFlowPathFlags(flowPathFlags);

		    String newDataPathSummaryStr = dataPath.dataPathSummary();
		    if (dataPathSummaryStr.equals(newDataPathSummaryStr))
			continue;	// Nothing changed

		    reconcileFlow(flowPathObj, dataPath);
		}

		//
		// Delete all leftover Flows marked for deletion from the
		// Network MAP.
		//
		while (! deleteFlows.isEmpty()) {
		    IFlowPath flowPathObj = deleteFlows.poll();
		    dbHandler.removeFlowPath(flowPathObj);
		}

		topologyNetService.dropTopology(topology);

		dbHandler.commit();

		long estimatedTime = System.nanoTime() - startTime;
		double rate = 0.0;
		if (estimatedTime > 0)
		    rate = ((double)counterAllFlowPaths * 1000000000) / estimatedTime;
		String logMsg = "MEASUREMENT: Processed AllFlowPaths: " +
		    counterAllFlowPaths + " MyFlowPaths: " +
		    counterMyFlowPaths + " in " +
		    (double)estimatedTime / 1000000000 + " sec: " +
		    rate + " paths/s";
		log.debug(logMsg);
	    }
	};


    /**
     * Initialize the Flow Manager.
     *
     * @param conf the Graph Database configuration string.
     */
    @Override
    public void init(String conf) {
    	dbHandler = new GraphDBOperation(conf);
    }

    /**
     * Shutdown the Flow Manager operation.
     */
    public void finalize() {
    	close();
    }

    /**
     * Shutdown the Flow Manager operation.
     */
    @Override
    public void close() {
	datagridService.deregisterPathComputationService(pathComputation);
    	dbHandler.close();
    	pusher.stop();
    }

    /**
     * Get the collection of offered module services.
     *
     * @return the collection of offered module services.
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = 
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFlowService.class);
        return l;
    }

    /**
     * Get the collection of implemented services.
     *
     * @return the collection of implemented services.
     */
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

    /**
     * Get the collection of modules this module depends on.
     *
     * @return the collection of modules this module depends on.
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> 
				      getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
	    new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	l.add(INetworkGraphService.class);
	l.add(IDatagridService.class);
	l.add(IRestApiService.class);
        return l;
    }

    /**
     * Initialize the module.
     *
     * @param context the module context to use for the initialization.
     */
    @Override
    public void init(FloodlightModuleContext context)
	throws FloodlightModuleException {
	this.context = context;
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	topologyNetService = context.getServiceImpl(ITopologyNetService.class);
	datagridService = context.getServiceImpl(IDatagridService.class);
	restApi = context.getServiceImpl(IRestApiService.class);
	
	pusher = new FlowPusher();
	pusher.init(null, floodlightProvider.getOFMessageFactory(), null);
	
	this.init("");

	mapReaderScheduler = Executors.newScheduledThreadPool(1);
	shortestPathReconcileScheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Get the next Flow Entry ID to use.
     *
     * @return the next Flow Entry ID to use.
     */
    public synchronized long getNextFlowEntryId() {
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

    /**
     * Startup module operation.
     *
     * @param context the module context to use for the startup.
     */
    @Override
    public void startUp(FloodlightModuleContext context) {
	restApi.addRestletRoutable(new FlowWebRoutable());

	// Initialize the Flow Entry ID generator
	nextFlowEntryIdPrefix = randomGenerator.nextInt();

	pusher.start();

	//
	// Create the Path Computation thread and register it with the
	// Datagrid Service
	//
	pathComputation = new PathComputation(this, datagridService);
	datagridService.registerPathComputationService(pathComputation);

	// Schedule the threads and periodic tasks
	pathComputation.start();
	mapReaderScheduler.scheduleAtFixedRate(
			mapReader, 3, 3, TimeUnit.SECONDS);
	shortestPathReconcileScheduler.scheduleAtFixedRate(
			shortestPathReconcile, 3, 3, TimeUnit.SECONDS);
    }

    /**
     * Add a flow.
     *
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean addFlow(FlowPath flowPath, FlowId flowId) {
	//
	// NOTE: We need to explicitly initialize the Flow Entry Switch State,
	// in case the application didn't do it.
	//
	for (FlowEntry flowEntry : flowPath.flowEntries()) {
	    if (flowEntry.flowEntrySwitchState() ==
		FlowEntrySwitchState.FE_SWITCH_UNKNOWN) {
		flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
	    }
	}

	if (FlowDatabaseOperation.addFlow(this, dbHandler, flowPath, flowId)) {
	    datagridService.notificationSendFlowAdded(flowPath);
	    return true;
	}
	return false;
    }

    /**
     * Add a flow entry to the Network MAP.
     *
     * @param flowObj the corresponding Flow Path object for the Flow Entry.
     * @param flowEntry the Flow Entry to install.
     * @return the added Flow Entry object on success, otherwise null.
     */
    private IFlowEntry addFlowEntry(IFlowPath flowObj, FlowEntry flowEntry) {
	return FlowDatabaseOperation.addFlowEntry(this, dbHandler, flowObj,
						  flowEntry);
    }

    /**
     * Delete all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteAllFlows() {
	if (FlowDatabaseOperation.deleteAllFlows(dbHandler)) {
	    datagridService.notificationSendAllFlowsRemoved();
	    return true;
	}
	return false;
    }

    /**
     * Delete a previously added flow.
     *
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteFlow(FlowId flowId) {
	if (FlowDatabaseOperation.deleteFlow(dbHandler, flowId)) {
	    datagridService.notificationSendFlowRemoved(flowId);
	    return true;
	}
	return false;
    }

    /**
     * Clear the state for all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean clearAllFlows() {
	if (FlowDatabaseOperation.clearAllFlows(dbHandler)) {
	    datagridService.notificationSendAllFlowsRemoved();
	    return true;
	}
	return false;
    }

    /**
     * Clear the state for a previously added flow.
     *
     * @param flowId the Flow ID of the flow to clear.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean clearFlow(FlowId flowId) {
	if (FlowDatabaseOperation.clearFlow(dbHandler, flowId)) {
	    datagridService.notificationSendFlowRemoved(flowId);
	    return true;
	}
	return false;
    }

    /**
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    @Override
    public FlowPath getFlow(FlowId flowId) {
	return FlowDatabaseOperation.getFlow(dbHandler, flowId);
    }

    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows() {
	return FlowDatabaseOperation.getAllFlows(dbHandler);
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
	return FlowDatabaseOperation.getAllFlows(dbHandler, installerId,
						 dataPathEndpoints);
    }

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows(DataPathEndpoints dataPathEndpoints) {
	return FlowDatabaseOperation.getAllFlows(dbHandler, dataPathEndpoints);
    }

    /**
     * Get summary of all installed flows by all installers in a given range.
     *
     * @param flowId the Flow ID of the first flow in the flow range to get.
     * @param maxFlows the maximum number of flows to be returned.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<IFlowPath> getAllFlowsSummary(FlowId flowId,
						   int maxFlows) {
	return FlowDatabaseOperation.getAllFlowsSummary(dbHandler, flowId,
							maxFlows);
    }
    
    /**
     * Get all Flows information, without the associated Flow Entries.
     *
     * @return all Flows information, without the associated Flow Entries.
     */
    public ArrayList<IFlowPath> getAllFlowsWithoutFlowEntries() {
	return FlowDatabaseOperation.getAllFlowsWithoutFlowEntries(dbHandler);
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
	//
	// Don't do the shortest path computation here.
	// Instead, let the Flow reconciliation thread take care of it.
	//

	FlowId flowId = new FlowId();
	if (! addFlow(flowPath, flowId))
	    return null;

	return (flowPath);
    }

    /**
     * Reconcile a flow.
     *
     * @param flowObj the flow that needs to be reconciliated.
     * @param newDataPath the new data path to use.
     * @return true on success, otherwise false.
     */
    private boolean reconcileFlow(IFlowPath flowObj, DataPath newDataPath) {

	//
	// Set the incoming port matching and the outgoing port output
	// actions for each flow entry.
	//
	int idx = 0;
	for (FlowEntry flowEntry : newDataPath.flowEntries()) {
	    // Mark the Flow Entry as not updated in the switch
	    flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
	    // Set the incoming port matching
	    FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
	    flowEntry.setFlowEntryMatch(flowEntryMatch);
	    flowEntryMatch.enableInPort(flowEntry.inPort());

	    //
	    // Set the actions
	    //
	    FlowEntryActions flowEntryActions = flowEntry.flowEntryActions();
	    //
	    // If the first Flow Entry, copy the Flow Path actions to it
	    //
	    if (idx == 0) {
		String actionsStr = flowObj.getActions();
		if (actionsStr != null) {
		    FlowEntryActions flowActions = new FlowEntryActions(actionsStr);
		    for (FlowEntryAction action : flowActions.actions())
			flowEntryActions.addAction(action);
		}
	    }
	    idx++;
	    //
	    // Add the outgoing port output action
	    //
	    FlowEntryAction flowEntryAction = new FlowEntryAction();
	    flowEntryAction.setActionOutput(flowEntry.outPort());
	    flowEntryActions.addAction(flowEntryAction);
	}

	//
	// Remove the old Flow Entries, and add the new Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    flowEntryObj.setUserState("FE_USER_DELETE");
	    flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
	}
	for (FlowEntry flowEntry : newDataPath.flowEntries()) {
	    addFlowEntry(flowObj, flowEntry);
	}

	//
	// Set the Data Path Summary
	//
	String dataPathSummaryStr = newDataPath.dataPathSummary();
	flowObj.setDataPathSummary(dataPathSummaryStr);

	return true;
    }

    /**
     * Reconcile all flows in a set.
     *
     * @param flowObjSet the set of flows that need to be reconciliated.
     */
    private void reconcileFlows(Iterable<IFlowPath> flowObjSet) {
	if (! flowObjSet.iterator().hasNext())
	    return;
	// TODO: Not implemented/used yet.
    }

    /**
     * Install a Flow Entry on a switch.
     *
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowObj the flow path object for the flow entry to install.
     * @param flowEntryObj the flow entry object to install.
     * @return true on success, otherwise false.
     */
    private boolean installFlowEntry(IOFSwitch mySwitch, IFlowPath flowObj,
				    IFlowEntry flowEntryObj) {
    	return pusher.send(mySwitch, flowObj, flowEntryObj);
    }

    /**
     * Install a Flow Entry on a switch.
     *
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    private boolean installFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				    FlowEntry flowEntry) {
    	log.debug("Flow is sent to pusher : dpid({}) flow_id({})", mySwitch.getId(), flowEntry.getFlowId());
    	// TODO  handle this installation by FlowPusher
    	
//	return FlowSwitchOperation.installFlowEntry(
//		floodlightProvider.getOFMessageFactory(),
//		messageDamper, mySwitch, flowPath, flowEntry);
    	
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
    private boolean removeFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				   FlowEntry flowEntry) {
	//
	// The installFlowEntry() method implements both installation
	// and removal of flow entries.
	//
	return (installFlowEntry(mySwitch, flowPath, flowEntry));
    }

    /**
     * Push the modified Flow Entries of a collection of Flow Paths.
     * Only the Flow Entries to switches controlled by this instance
     * are pushed.
     *
     * NOTE: Currently, we write to both the Network MAP and the switches.
     *
     * @param modifiedFlowPaths the collection of Flow Paths with the modified
     * Flow Entries.
     */
    public void pushModifiedFlowEntries(Collection<FlowPath> modifiedFlowPaths) {

	// TODO: For now, the pushing of Flow Entries is disabled
	if (true)
	    return;

	Map<Long, IOFSwitch> mySwitches = floodlightProvider.getSwitches();

	for (FlowPath flowPath : modifiedFlowPaths) {
	    IFlowPath flowObj = dbHandler.searchFlowPath(flowPath.flowId());
	    if (flowObj == null) {
		String logMsg = "Cannot find Network MAP entry for Flow Path " +
		    flowPath.flowId();
		log.error(logMsg);
		continue;
	    }

	    for (FlowEntry flowEntry : flowPath.flowEntries()) {
		if (flowEntry.flowEntrySwitchState() !=
		    FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED) {
		    continue;		// No need to update the entry
		}

		IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
		if (mySwitch == null)
		    continue;		// Ignore the entry: not my switch

		//
		// Assign the FlowEntry ID if needed
		//
		if ((flowEntry.flowEntryId() == null) ||
		    (flowEntry.flowEntryId().value() == 0)) {
		    long id = getNextFlowEntryId();
		    flowEntry.setFlowEntryId(new FlowEntryId(id));
		}

		//
		// Install the Flow Entry into the switch
		//
		if (! installFlowEntry(mySwitch, flowPath, flowEntry)) {
		    String logMsg = "Cannot install Flow Entry " +
			flowEntry.flowEntryId() +
			" from Flow Path " + flowPath.flowId() +
			" on switch " + flowEntry.dpid();
		    log.error(logMsg);
		    continue;
		}

		//
		// NOTE: Here we assume that the switch has been successfully
		// updated.
		//
		flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_UPDATED);

		//
		// Write the Flow Entry to the Network Map
		//
		try {
		    if (addFlowEntry(flowObj, flowEntry) == null) {
			String logMsg = "Cannot write to Network MAP Flow Entry " +
			    flowEntry.flowEntryId() +
			    " from Flow Path " + flowPath.flowId() +
			    " on switch " + flowEntry.dpid();
			log.error(logMsg);
			continue;
		    }
		} catch (Exception e) {
		    String logMsg = "Exception writing Flow Entry to Network MAP";
		    log.debug(logMsg);
		    dbHandler.rollback();
		    continue;
		}
	    }
	}

	dbHandler.commit();
    }

	@Override
	public void addMessage(long dpid, OFMessage msg) {
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		if (sw == null) {
			return;
		}
		
		pusher.send(sw, msg);
	}

	@Override
	public boolean suspend(long dpid) {
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		if (sw == null) {
			return false;
		}
		
		return pusher.suspend(sw);
	}

	@Override
	public boolean resume(long dpid) {
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		if (sw == null) {
			return false;
		}
		
		return pusher.resume(sw);
	}

	@Override
	public boolean isSuspended(long dpid) {
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		if (sw == null) {
			return false;
		}
		
		return pusher.isSuspended(sw);
	}
}
