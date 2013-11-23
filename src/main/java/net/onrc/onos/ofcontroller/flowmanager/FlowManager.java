package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.OFMessageDamper;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.graph.GraphDBManager;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.core.INetMapStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.floodlightlistener.INetworkGraphService;
import net.onrc.onos.ofcontroller.flowmanager.web.FlowWebRoutable;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.topology.Topology;
import net.onrc.onos.ofcontroller.util.*;

import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager class for handling the network flows.
 */
public class FlowManager implements IFloodlightModule, IFlowService, INetMapStorage {
    // flag to use FlowPusher instead of FlowSwitchOperation/MessageDamper
    private final static boolean enableFlowPusher = false;
    protected DBOperation dbHandlerApi;
    protected DBOperation dbHandlerInner;

    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile IDatagridService datagridService;
    protected IRestApiService restApi;
    protected FloodlightModuleContext context;
    protected FlowEventHandler flowEventHandler;

    protected IFlowPusherService pusher;
    
    protected OFMessageDamper messageDamper;
    
    //
    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    protected static final int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static final int OFMESSAGE_DAMPER_TIMEOUT = 250;	// ms

    // Flow Entry ID generation state
    private static Random randomGenerator = new Random();
    private static int nextFlowEntryIdPrefix = 0;
    private static int nextFlowEntryIdSuffix = 0;

    /** The logger. */
    private final static Logger log = LoggerFactory.getLogger(FlowManager.class);

    // The queue to write Flow Entries to the database
    private BlockingQueue<FlowPathEntryPair> flowEntriesToDatabaseQueue =
	new LinkedBlockingQueue<FlowPathEntryPair>();
    FlowDatabaseWriter flowDatabaseWriter;

    /**
     * Initialize the Flow Manager.
     *
     * @param conf the Graph Database configuration string.
     */
    @Override
    public void init(final String dbStore, final String conf) {
	dbHandlerApi = GraphDBManager.getDBOperation(dbStore, conf);
	dbHandlerInner = GraphDBManager.getDBOperation(dbStore, conf);
	
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
	datagridService.deregisterFlowEventHandlerService(flowEventHandler);
    	dbHandlerApi.close();
    	dbHandlerInner.close();
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
	datagridService = context.getServiceImpl(IDatagridService.class);
	restApi = context.getServiceImpl(IRestApiService.class);

	if (enableFlowPusher) {
	    pusher = context.getServiceImpl(IFlowPusherService.class);
	} else {
	    messageDamper = new OFMessageDamper(OFMESSAGE_DAMPER_CAPACITY,
	    EnumSet.of(OFType.FLOW_MOD),
	    OFMESSAGE_DAMPER_TIMEOUT);
	}

	this.init("","");
    }

    /**
     * Get the next Flow Entry ID to use.
     *
     * @return the next Flow Entry ID to use.
     */
    @Override
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

	//
	// The thread to write to the database
	//
	flowDatabaseWriter = new FlowDatabaseWriter(this,
						flowEntriesToDatabaseQueue);
	flowDatabaseWriter.start();

	//
	// The Flow Event Handler thread:
	//  - create
	//  - register with the Datagrid Service
	//  - startup
	//
	flowEventHandler = new FlowEventHandler(this, datagridService);
	datagridService.registerFlowEventHandlerService(flowEventHandler);
	flowEventHandler.start();
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
	// NOTE: We need to explicitly initialize some of the state,
	// in case the application didn't do it.
	//
	for (FlowEntry flowEntry : flowPath.flowEntries()) {
	    if (flowEntry.flowEntrySwitchState() ==
		FlowEntrySwitchState.FE_SWITCH_UNKNOWN) {
		flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
	    }
	    if (! flowEntry.isValidFlowId())
		flowEntry.setFlowId(new FlowId(flowPath.flowId().value()));
	}

	if (FlowDatabaseOperation.addFlow(this, dbHandlerApi, flowPath, flowId)) {
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
	return FlowDatabaseOperation.addFlowEntry(this, dbHandlerInner,
						  flowObj, flowEntry);
    }

    /**
     * Delete a flow entry from the Network MAP.
     *
     * @param flowObj the corresponding Flow Path object for the Flow Entry.
     * @param flowEntry the Flow Entry to delete.
     * @return true on success, otherwise false.
     */
    private boolean deleteFlowEntry(IFlowPath flowObj, FlowEntry flowEntry) {
	return FlowDatabaseOperation.deleteFlowEntry(dbHandlerInner,
						     flowObj, flowEntry);
    }

    /**
     * Delete all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteAllFlows() {
	if (FlowDatabaseOperation.deleteAllFlows(dbHandlerApi)) {
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
	if (FlowDatabaseOperation.deleteFlow(dbHandlerApi, flowId)) {
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
	return FlowDatabaseOperation.getFlow(dbHandlerApi, flowId);
    }

    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows() {
	return FlowDatabaseOperation.getAllFlows(dbHandlerApi);
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
	return FlowDatabaseOperation.getAllFlows(dbHandlerApi, installerId,
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
	return FlowDatabaseOperation.getAllFlows(dbHandlerApi,
						 dataPathEndpoints);
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
	return FlowDatabaseOperation.getAllFlowsSummary(dbHandlerApi, flowId,
							maxFlows);
    }
    
    /**
     * Get all Flows information, without the associated Flow Entries.
     *
     * @return all Flows information, without the associated Flow Entries.
     */
    public ArrayList<IFlowPath> getAllFlowsWithoutFlowEntries() {
	return FlowDatabaseOperation.getAllFlowsWithoutFlowEntries(dbHandlerApi);
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
     * Get the collection of my switches.
     *
     * @return the collection of my switches.
     */
    public Map<Long, IOFSwitch> getMySwitches() {
	return floodlightProvider.getSwitches();
    }

    /**
     * Get the network topology.
     *
     * @return the network topology.
     */
    public Topology getTopology() {
	return flowEventHandler.getTopology();
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
    	if (enableFlowPusher) {
	    return pusher.add(mySwitch, flowObj, flowEntryObj);
    	} else {
	    return FlowSwitchOperation.installFlowEntry(
		floodlightProvider.getOFMessageFactory(),
		messageDamper, mySwitch, flowObj, flowEntryObj);
    	}
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
    	if (enableFlowPusher) {
	    return pusher.add(mySwitch, flowPath, flowEntry);
    	} else {
	    return FlowSwitchOperation.installFlowEntry(
		floodlightProvider.getOFMessageFactory(),
		messageDamper, mySwitch, flowPath, flowEntry);
    	}
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
     * Push modified Flow Entries to switches.
     *
     * NOTE: Only the Flow Entries to switches controlled by this instance
     * are pushed.
     *
     * @param modifiedFlowEntries the collection of modified Flow Entries.
     */
    public void pushModifiedFlowEntriesToSwitches(
			Collection<FlowPathEntryPair> modifiedFlowEntries) {
	if (modifiedFlowEntries.isEmpty())
	    return;

	Map<Long, IOFSwitch> mySwitches = getMySwitches();

	for (FlowPathEntryPair flowPair : modifiedFlowEntries) {
	    FlowPath flowPath = flowPair.flowPath;
	    FlowEntry flowEntry = flowPair.flowEntry;

	    IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
	    if (mySwitch == null)
		continue;

	    log.debug("Pushing Flow Entry To Switch: {}", flowEntry.toString());

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
	    // NOTE: Here we assume that the switch has been
	    // successfully updated.
	    //
	    flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_UPDATED);
	}
    }

    /**
     * Push modified Flow Entries to the datagrid.
     *
     * @param modifiedFlowEntries the collection of modified Flow Entries.
     */
    public void pushModifiedFlowEntriesToDatagrid(
			Collection<FlowPathEntryPair> modifiedFlowEntries) {
	if (modifiedFlowEntries.isEmpty())
	    return;

	Map<Long, IOFSwitch> mySwitches = getMySwitches();

	for (FlowPathEntryPair flowPair : modifiedFlowEntries) {
	    FlowEntry flowEntry = flowPair.flowEntry;

	    IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());

	    //
	    // TODO: For now Flow Entries are removed by all instances,
	    // even if this Flow Entry is not for our switches.
	    //
	    // This is needed to handle the case a switch going down:
	    // it has no Master controller instance, hence no
	    // controller instance will cleanup its flow entries.
	    // This is sub-optimal: we need to elect a controller
	    // instance to handle the cleanup of such orphaned flow
	    // entries.
	    //
	    if (mySwitch == null) {
		if (flowEntry.flowEntryUserState() !=
		    FlowEntryUserState.FE_USER_DELETE) {
		    continue;
		}
		if (! flowEntry.isValidFlowEntryId())
		    continue;
	    }

	    log.debug("Pushing Flow Entry To Datagrid: {}", flowEntry.toString());
	    //
	    // Write the Flow Entry to the Datagrid
	    //
	    switch (flowEntry.flowEntryUserState()) {
	    case FE_USER_ADD:
		if (mySwitch == null)
		    break;	// Install only flow entries for my switches
		datagridService.notificationSendFlowEntryAdded(flowEntry);
		break;
	    case FE_USER_MODIFY:
		if (mySwitch == null)
		    break;	// Install only flow entries for my switches
		datagridService.notificationSendFlowEntryUpdated(flowEntry);
		break;
	    case FE_USER_DELETE:
		datagridService.notificationSendFlowEntryRemoved(flowEntry.flowEntryId());
		break;
	    }
	}
    }

    /**
     * Class to implement writing to the database in a separate thread.
     */
    class FlowDatabaseWriter extends Thread {
	private FlowManager flowManager;
	private BlockingQueue<FlowPathEntryPair> blockingQueue;

	/**
	 * Constructor.
	 *
	 * @param flowManager the Flow Manager to use.
	 * @param blockingQueue the blocking queue to use.
	 */
	FlowDatabaseWriter(FlowManager flowManager,
			   BlockingQueue<FlowPathEntryPair> blockingQueue) {
	    this.flowManager = flowManager;
	    this.blockingQueue = blockingQueue;
	}

	/**
	 * Run the thread.
	 */
	@Override
	public void run() {
	    //
	    // The main loop
	    //
	    Collection<FlowPathEntryPair> collection =
		new LinkedList<FlowPathEntryPair>();
	    try {
		while (true) {
		    FlowPathEntryPair entryPair = blockingQueue.take();
		    collection.add(entryPair);
		    blockingQueue.drainTo(collection);
		    flowManager.writeModifiedFlowEntriesToDatabase(collection);
		    collection.clear();
		}
	    } catch (Exception exception) {
		log.debug("Exception writing to the Database: ", exception);
	    }
	}
    }

    /**
     * Push Flow Entries to the Network MAP.
     *
     * NOTE: The Flow Entries are pushed only on the instance responsible
     * for the first switch. This is to avoid database errors when multiple
     * instances are writing Flow Entries for the same Flow Path.
     *
     * @param modifiedFlowEntries the collection of Flow Entries to push.
     */
    void pushModifiedFlowEntriesToDatabase(
		Collection<FlowPathEntryPair> modifiedFlowEntries) {
	//
	// We only add the Flow Entries to the Database Queue.
	// The FlowDatabaseWriter thread is responsible for the actual writing.
	//
	flowEntriesToDatabaseQueue.addAll(modifiedFlowEntries);
    }

    /**
     * Write Flow Entries to the Network MAP.
     *
     * NOTE: The Flow Entries are written only on the instance responsible
     * for the first switch. This is to avoid database errors when multiple
     * instances are writing Flow Entries for the same Flow Path.
     *
     * @param modifiedFlowEntries the collection of Flow Entries to write.
     */
    private void writeModifiedFlowEntriesToDatabase(
		Collection<FlowPathEntryPair> modifiedFlowEntries) {
	if (modifiedFlowEntries.isEmpty())
	    return;

	Map<Long, IOFSwitch> mySwitches = getMySwitches();

	for (FlowPathEntryPair flowPair : modifiedFlowEntries) {
	    FlowPath flowPath = flowPair.flowPath;
	    FlowEntry flowEntry = flowPair.flowEntry;

	    if (! flowEntry.isValidFlowEntryId())
		continue;

	    //
	    // Push the changes only on the instance responsible for the
	    // first switch.
	    //
	    Dpid srcDpid = flowPath.dataPath().srcPort().dpid();
	    IOFSwitch mySrcSwitch = mySwitches.get(srcDpid.value());
	    if (mySrcSwitch == null)
		continue;

	    log.debug("Pushing Flow Entry To Database: {}", flowEntry.toString());
	    //
	    // Write the Flow Entry to the Network Map
	    //
	    // NOTE: We try a number of times, in case somehow some other
	    // instances are writing at the same time.
	    // Apparently, if other instances are writing at the same time
	    // this will trigger an error.
	    //
	    for (int i = 0; i < 6; i++) {
		try {
		    //
		    // Find the Flow Path in the Network MAP.
		    //
		    // NOTE: The Flow Path might not be found if the Flow was
		    // just removed by some other controller instance.
		    //
		    IFlowPath flowObj =
			dbHandlerInner.searchFlowPath(flowEntry.flowId());
		    if (flowObj == null) {
			String logMsg = "Cannot find Network MAP entry for Flow Path " + flowEntry.flowId();
			log.error(logMsg);
			break;
		    }

		    // Write the Flow Entry
		    switch (flowEntry.flowEntryUserState()) {
		    case FE_USER_ADD:
			// FALLTHROUGH
		    case FE_USER_MODIFY:
			if (addFlowEntry(flowObj, flowEntry) == null) {
			    String logMsg = "Cannot write to Network MAP Flow Entry " +
				flowEntry.flowEntryId() +
				" from Flow Path " + flowEntry.flowId() +
				" on switch " + flowEntry.dpid();
			    log.error(logMsg);
			}
			break;
		    case FE_USER_DELETE:
			if (deleteFlowEntry(flowObj, flowEntry) == false) {
			    String logMsg = "Cannot remove from Network MAP Flow Entry " +
				flowEntry.flowEntryId() +
				" from Flow Path " + flowEntry.flowId() +
				" on switch " + flowEntry.dpid();
			    log.error(logMsg);
			}
			break;
		    }

		    // Commit to the database
		    dbHandlerInner.commit();
		    break;	// Success

		} catch (Exception e) {
		    log.debug("Exception writing Flow Entry to Network MAP: ", e);
		    dbHandlerInner.rollback();
		    // Wait a bit (random value [1ms, 20ms] and try again
		    int delay = 1 + randomGenerator.nextInt() % 20;
		    try {
			Thread.sleep(delay);
		    } catch (Exception e0) {
		    }
		}
	    }
	}
    }
}
