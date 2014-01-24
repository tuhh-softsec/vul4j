package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import net.onrc.onos.ofcontroller.floodlightlistener.INetworkGraphService;
import net.onrc.onos.ofcontroller.flowmanager.web.FlowWebRoutable;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.forwarding.IForwardingService;
import net.onrc.onos.ofcontroller.topology.Topology;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntrySwitchState;
import net.onrc.onos.ofcontroller.util.FlowEntryUserState;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.FlowPathUserState;
import net.onrc.onos.ofcontroller.util.Pair;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import com.thinkaurelius.titan.core.TitanException;

import com.esotericsoftware.kryo.Kryo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager class for handling the network flows.
 */
public class FlowManager implements IFloodlightModule, IFlowService, INetMapStorage {

    private boolean enableOnrc2014MeasurementsFlows = true;

    protected DBOperation dbHandlerApi;
    protected DBOperation dbHandlerInner;

    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile IDatagridService datagridService;
    protected IRestApiService restApi;
    protected FloodlightModuleContext context;
    protected FlowEventHandler flowEventHandler;

    protected IFlowPusherService pusher;
    protected IForwardingService forwardingService;

    private KryoFactory kryoFactory = new KryoFactory();

    // Flow Entry ID generation state
    private static Random randomGenerator = new Random();
    private static int nextFlowEntryIdPrefix = 0;
    private static int nextFlowEntryIdSuffix = 0;

    /** The logger. */
    private final static Logger log = LoggerFactory.getLogger(FlowManager.class);

    // The queue to write Flow Entries to the database
    private BlockingQueue<FlowPath> flowPathsToDatabaseQueue =
	new LinkedBlockingQueue<FlowPath>();
    FlowDatabaseWriter flowDatabaseWriter;

    /**
     * Initialize the Flow Manager.
     *
     * @param conf the Graph Database configuration string.
     */
    @Override
    public void init(final String dbStore, final String conf) {
	dbHandlerApi = GraphDBManager.getDBOperation("ramcloud", "/tmp/ramcloud.conf");
	dbHandlerInner = GraphDBManager.getDBOperation("ramcloud", "/tmp/ramcloud.conf");
    }

    /**
     * Shutdown the Flow Manager operation.
     */
    @Override
    protected void finalize() {
    	close();
    }

    /**
     * Shutdown the Flow Manager operation.
     */
    @Override
    public void close() {
	floodlightProvider.removeOFSwitchListener(flowEventHandler);
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
	l.add(IFlowPusherService.class);
	//
	// TODO: Comment-out the dependency on the IForwardingService,
	// because it is an optional module. Apparently, adding the dependency
	// here automatically enables the module.
	//
	// l.add(IForwardingService.class);
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
	pusher = context.getServiceImpl(IFlowPusherService.class);
	forwardingService = context.getServiceImpl(IForwardingService.class);

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
						flowPathsToDatabaseQueue);
	flowDatabaseWriter.start();

	//
	// The Flow Event Handler thread:
	//  - create
	//  - register with the Datagrid Service
	//  - startup
	//
	flowEventHandler = new FlowEventHandler(this, datagridService);
	floodlightProvider.addOFSwitchListener(flowEventHandler);
	datagridService.registerFlowEventHandlerService(flowEventHandler);
	flowEventHandler.start();
    }

    /**
     * Add a flow.
     *
     * @param flowPath the Flow Path to install.
     * @return the Flow ID on success, otherwise null.
     */
    @Override
    public FlowId addFlow(FlowPath flowPath) {

	// Allocate the Flow ID if necessary
	if (! flowPath.isValidFlowId()) {
	    long id = getNextFlowEntryId();
	    flowPath.setFlowId(new FlowId(id));
	}

	//
	// NOTE: We need to explicitly initialize some of the state,
	// in case the application didn't do it.
	//
	for (FlowEntry flowEntry : flowPath.flowEntries()) {
	    // The Flow Entry switch state
	    if (flowEntry.flowEntrySwitchState() ==
		FlowEntrySwitchState.FE_SWITCH_UNKNOWN) {
		flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
	    }
	    // The Flow Entry ID
	    if (! flowEntry.isValidFlowEntryId()) {
		long id = getNextFlowEntryId();
		flowEntry.setFlowEntryId(new FlowEntryId(id));
	    }
	    // The Flow ID
	    if (! flowEntry.isValidFlowId())
		flowEntry.setFlowId(new FlowId(flowPath.flowId().value()));
	}

	if (FlowDatabaseOperation.addFlow(dbHandlerApi, flowPath)) {
	    if (enableOnrc2014MeasurementsFlows) {
		datagridService.notificationSendFlowIdAdded(flowPath.flowId(),
							    flowPath.dataPath().srcPort().dpid());
	    } else {
		datagridService.notificationSendFlowAdded(flowPath);
	    }

	    return flowPath.flowId();
	}
	return null;
    }

    /**
     * Delete all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteAllFlows() {
	if (FlowDatabaseOperation.deleteAllFlows(dbHandlerApi)) {
	    if (enableOnrc2014MeasurementsFlows) {
		datagridService.notificationSendAllFlowIdsRemoved();
	    } else {
		datagridService.notificationSendAllFlowsRemoved();
	    }
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
	    if (enableOnrc2014MeasurementsFlows) {
		datagridService.notificationSendFlowIdRemoved(flowId);
	    } else {
		datagridService.notificationSendFlowRemoved(flowId);
	    }
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
	log.debug("FlowID: {}", flowId);
	if(flowId.value() == -100) {
	    log.debug("Printing results...");
	    PerformanceMonitor.report();
	    PerformanceMonitor.clear();
	}
	else if(flowId.value() == -200) {
	    log.debug("Clearing results...");
	    PerformanceMonitor.clear();
	}
	return FlowDatabaseOperation.getFlow(dbHandlerApi, flowId);
    }

    /**
     * Get a previously added flow entry.
     *
     * @param flowEntryId the Flow Entry ID of the flow entry to get.
     * @return the Flow Entry if found, otherwise null.
     */
    public FlowEntry getFlowEntry(FlowEntryId flowEntryId) {
	return FlowDatabaseOperation.getFlowEntry(dbHandlerApi, flowEntryId);
    }

    /**
     * Get the source switch DPID of a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the source switch DPID if found, otherwise null.
     */
    public Dpid getFlowSourceDpid(FlowId flowId) {
	return FlowDatabaseOperation.getFlowSourceDpid(dbHandlerApi, flowId);
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
     * Get all installed flows whose Source Switch is controlled by this
     * instance.
     *
     * @param mySwitches the collection of the switches controlled by this
     * instance.
     * @return the Flow Paths if found, otherwise null.
     */
    public ArrayList<FlowPath> getAllMyFlows(Map<Long, IOFSwitch> mySwitches) {
	return FlowDatabaseOperation.getAllMyFlows(dbHandlerApi, mySwitches);
    }

    /**
     * Get summary of all installed flows by all installers in a given range.
     *
     * @param flowId the Flow ID of the first flow in the flow range to get.
     * @param maxFlows the maximum number of flows to be returned.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlowsSummary(FlowId flowId,
						  int maxFlows) {
    	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
	SortedMap<Long, FlowPath> sortedFlowPaths =
	    new TreeMap<Long, FlowPath>();

	if (enableOnrc2014MeasurementsFlows) {
	    Collection<FlowPath> databaseFlowPaths =
		ParallelFlowDatabaseOperation.getAllFlows(dbHandlerApi);
	    for (FlowPath flowPath : databaseFlowPaths) {
		sortedFlowPaths.put(flowPath.flowId().value(), flowPath);
	    }
	} else {
	    sortedFlowPaths = flowEventHandler.getAllFlowPathsCopy();
	}

	//
	// Truncate each Flow Path and Flow Entry
	//
	for (FlowPath flowPath : sortedFlowPaths.values()) {
	    //
	    // TODO: Add only the Flow Paths that have been successfully
	    // installed.
	    //
	    flowPath.setFlowEntryMatch(null);
	    flowPath.setFlowEntryActions(null);
	    for (FlowEntry flowEntry : flowPath.flowEntries()) {
		flowEntry.setFlowEntryMatch(null);
		flowEntry.setFlowEntryActions(null);
	    }
	    flowPaths.add(flowPath);
	}

	return flowPaths;
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
    @Override
    public Topology getTopology() {
	return flowEventHandler.getTopology();
    }

    /**
     * Inform the Flow Manager that a Flow Entry on switch expired.
     *
     * @param sw the switch the Flow Entry expired on.
     * @param flowEntryId the Flow Entry ID of the expired Flow Entry.
     */
    @Override
    public void flowEntryOnSwitchExpired(IOFSwitch sw,
					 FlowEntryId flowEntryId) {
	// Find the Flow Entry
	FlowEntry flowEntry = datagridService.getFlowEntry(flowEntryId);
	if (flowEntry == null)
	    return;		// Flow Entry not found

	// Find the Flow Path
	FlowPath flowPath = datagridService.getFlow(flowEntry.flowId());
	if (flowPath == null)
	    return;		// Flow Path not found

	//
	// Remove the Flow if the Flow Entry expired on the first switch
	//
	Dpid srcDpid = flowPath.dataPath().srcPort().dpid();
	if (srcDpid.value() != sw.getId())
	    return;
	deleteFlow(flowPath.flowId());
	
	// Send flow deleted notification to the Forwarding module
	// TODO This is a quick fix for flow-removed notifications. We
	// should think more about the design of these notifications.
	notificationFlowPathRemoved(flowPath);
    }

    /**
     * Inform the Flow Manager that a collection of Flow Entries have been
     * pushed to a switch.
     *
     * @param entries the collection of <IOFSwitch, FlowEntry> pairs
     * that have been pushed.
     */
    @Override
    public void flowEntriesPushedToSwitch(
		Collection<Pair<IOFSwitch, FlowEntry>> entries) {

	if (enableOnrc2014MeasurementsFlows)
	    return;

	//
	// Process all entries
	//
	// TODO: For now we have to create an explicit FlowEntry copy so
	// we don't modify the original FlowEntry.
	// This should go away after we start using the OpenFlow Barrier
	// mechnanism in the FlowPusher.
	//
	Kryo kryo = kryoFactory.newKryo();
	for (Pair<IOFSwitch, FlowEntry> entry : entries) {
	    FlowEntry flowEntry = entry.second;

	    //
	    // Mark the Flow Entry that it has been pushed to the switch
	    //
	    FlowEntry copyFlowEntry = kryo.copy(flowEntry);
	    copyFlowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_UPDATED);

	    //
	    // Write the Flow Entry to the Datagrid
	    //
	    switch (copyFlowEntry.flowEntryUserState()) {
	    case FE_USER_ADD:
		datagridService.notificationSendFlowEntryAdded(copyFlowEntry);
		break;
	    case FE_USER_MODIFY:
		datagridService.notificationSendFlowEntryUpdated(copyFlowEntry);
		break;
	    case FE_USER_DELETE:
		datagridService.notificationSendFlowEntryRemoved(copyFlowEntry.flowEntryId());
		break;
	    case FE_USER_UNKNOWN:
		assert(false);
		break;
	    }
	}
	kryoFactory.deleteKryo(kryo);
    }

    /**
     * Generate a notification that a collection of Flow Paths has been
     * installed in the network.
     *
     * @param flowPaths the collection of installed Flow Paths.
     */
    void notificationFlowPathsInstalled(Collection<FlowPath> flowPaths) {
	//
	// TODO: Add an explicit check for null pointer, because
	// the IForwardingService is optional. Remove the "if" statement
	// after hte Forwarding Module becomes mandatory.
	//
	if (forwardingService != null)
	    forwardingService.flowsInstalled(flowPaths);
    }

    /**
     * Generate a notification that a FlowPath has been removed from the 
     * network. This means we've received an expiry message for the flow
     * from the switch, and send flowmods to remove any remaining parts of
     * the path.
     * 
     * @param flowPath FlowPath object that was removed from the network.
     */
    void notificationFlowPathRemoved(FlowPath flowPath) {
	if (forwardingService != null) {
		forwardingService.flowRemoved(flowPath);
	}
    }

    /**
     * Push modified Flow-related state as appropriate.
     *
     * @param modifiedFlowPaths the collection of modified Flow Paths.
     * @param modifiedFlowEntries the collection of modified Flow Entries.
     */
    void pushModifiedFlowState(Collection<FlowPath> modifiedFlowPaths,
			       Collection<FlowEntry> modifiedFlowEntries) {
	//
	// Push the modified Flow state:
	//  - Flow Entries to switches and the datagrid
	//  - Flow Paths to the database
	//
	pushModifiedFlowEntriesToSwitches(modifiedFlowEntries);
	if (enableOnrc2014MeasurementsFlows) {
	    writeModifiedFlowPathsToDatabase(modifiedFlowPaths);
	} else {
	    pushModifiedFlowPathsToDatabase(modifiedFlowPaths);
	    cleanupDeletedFlowEntriesFromDatagrid(modifiedFlowEntries);
	}
    }

    /**
     * Push modified Flow Entries to switches.
     *
     * NOTE: Only the Flow Entries to switches controlled by this instance
     * are pushed.
     *
     * @param modifiedFlowEntries the collection of modified Flow Entries.
     */
    void pushModifiedFlowEntriesToSwitches(
			Collection<FlowEntry> modifiedFlowEntries) {
	if (modifiedFlowEntries.isEmpty())
	    return;

	List<Pair<IOFSwitch, FlowEntry>> entries =
	    new LinkedList<Pair<IOFSwitch, FlowEntry>>();

	Map<Long, IOFSwitch> mySwitches = getMySwitches();

	//
	// Create a collection of my Flow Entries to push
	//
	for (FlowEntry flowEntry : modifiedFlowEntries) {
	    IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
	    if (mySwitch == null)
		continue;

	    if (flowEntry.flowEntrySwitchState() ==
		FlowEntrySwitchState.FE_SWITCH_UPDATED) {
		//
		// Don't push again Flow Entries that were already already
		// installed into the switches.
		//
		continue;
	    }

	    //
	    // Assign Flow Entry IDs if missing.
	    //
	    // NOTE: This is an additional safeguard, in case the
	    // mySwitches set has changed (after the Flow Entry IDs
	    // assignments by the caller).
	    //
	    if (! flowEntry.isValidFlowEntryId()) {
		long id = getNextFlowEntryId();
		flowEntry.setFlowEntryId(new FlowEntryId(id));
	    }

	    log.debug("Pushing Flow Entry To Switch: {}", flowEntry);
	    entries.add(new Pair<IOFSwitch, FlowEntry>(mySwitch, flowEntry));
	}

	pusher.pushFlowEntries(entries);
    }

    /**
     * Cleanup deleted Flow Entries from the datagrid.
     *
     * NOTE: We cleanup only the Flow Entries that are not for our switches.
     * This is needed to handle the case a switch going down:
     * It has no Master controller instance, hence no controller instance
     * will cleanup its flow entries.
     * This is sub-optimal: we need to elect a controller instance to handle
     * the cleanup of such orphaned flow entries.
     *
     * @param modifiedFlowEntries the collection of modified Flow Entries.
     */
    private void cleanupDeletedFlowEntriesFromDatagrid(
			Collection<FlowEntry> modifiedFlowEntries) {
	if (modifiedFlowEntries.isEmpty())
	    return;

	Map<Long, IOFSwitch> mySwitches = getMySwitches();

	for (FlowEntry flowEntry : modifiedFlowEntries) {
	    //
	    // Process only Flow Entries that should be deleted and have
	    // a valid Flow Entry ID.
	    //
	    if (! flowEntry.isValidFlowEntryId())
		continue;
	    if (flowEntry.flowEntryUserState() !=
		FlowEntryUserState.FE_USER_DELETE) {
		continue;
	    }

	    //
	    // NOTE: The deletion of Flow Entries for my switches is handled
	    // elsewhere.
	    //
	    IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
	    if (mySwitch != null)
		continue;

	    log.debug("Pushing cleanup of Flow Entry To Datagrid: {}", flowEntry);

	    //
	    // Write the Flow Entry to the Datagrid
	    //
	    datagridService.notificationSendFlowEntryRemoved(flowEntry.flowEntryId());
	}
    }

    /**
     * Class to implement writing to the database in a separate thread.
     */
    class FlowDatabaseWriter extends Thread {
	private FlowManager flowManager;
	private BlockingQueue<FlowPath> blockingQueue;

	/**
	 * Constructor.
	 *
	 * @param flowManager the Flow Manager to use.
	 * @param blockingQueue the blocking queue to use.
	 */
	FlowDatabaseWriter(FlowManager flowManager,
			   BlockingQueue<FlowPath> blockingQueue) {
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
	    Collection<FlowPath> collection = new LinkedList<FlowPath>();
	    this.setName("FlowDatabaseWriter " + this.getId() );
	    try {
		while (true) {
		    FlowPath flowPath = blockingQueue.take();
		    collection.add(flowPath);
		    blockingQueue.drainTo(collection);
		    flowManager.writeModifiedFlowPathsToDatabase(collection);
		    collection.clear();
		}
	    } catch (Exception exception) {
		log.debug("Exception writing to the Database: ", exception);
	    }
	}
    }

    /**
     * Push Flow Paths to the Network MAP.
     *
     * NOTE: The complete Flow Paths are pushed only on the instance
     * responsible for the first switch. This is to avoid database errors
     * when multiple instances are writing Flow Entries for the same Flow Path.
     *
     * @param modifiedFlowPaths the collection of Flow Paths to push.
     */
    private void pushModifiedFlowPathsToDatabase(
		Collection<FlowPath> modifiedFlowPaths) {
	List<FlowPath> copiedFlowPaths = new LinkedList<FlowPath>();

	//
	// Create a copy of the Flow Paths to push, because the pushing
	// itself will happen on a separate thread.
	//
	Kryo kryo = kryoFactory.newKryo();
	for (FlowPath flowPath : modifiedFlowPaths) {
	    FlowPath copyFlowPath = kryo.copy(flowPath);
	    copiedFlowPaths.add(copyFlowPath);
	}
	kryoFactory.deleteKryo(kryo);

	//
	// We only add the Flow Paths to the Database Queue.
	// The FlowDatabaseWriter thread is responsible for the actual writing.
	//
	flowPathsToDatabaseQueue.addAll(copiedFlowPaths);
    }

    /**
     * Write Flow Paths to the Network MAP.
     *
     * NOTE: The complete Flow Paths are pushed only on the instance
     * responsible for the first switch. This is to avoid database errors
     * when multiple instances are writing Flow Entries for the same Flow Path.
     *
     * @param modifiedFlowPaths the collection of Flow Paths to write.
     */
    void writeModifiedFlowPathsToDatabase(
		Collection<FlowPath> modifiedFlowPaths) {
	if (modifiedFlowPaths.isEmpty())
	    return;

	Map<Long, IOFSwitch> mySwitches = getMySwitches();

	for (FlowPath flowPath : modifiedFlowPaths) {
	    //
	    // Push the changes only on the instance responsible for the
	    // first switch.
	    //
	    Dpid srcDpid = flowPath.dataPath().srcPort().dpid();
	    IOFSwitch mySrcSwitch = mySwitches.get(srcDpid.value());
	    if (mySrcSwitch == null)
		continue;

	    //
	    // Delete the Flow Path from the Network Map
	    //
	    if (flowPath.flowPathUserState() ==
		FlowPathUserState.FP_USER_DELETE) {
		log.debug("Deleting Flow Path From Database: {}", flowPath);

		boolean retry = false;
		do {
		    retry = false;
		    try {
			if (! FlowDatabaseOperation.deleteFlow(
						dbHandlerInner,
						flowPath.flowId())) {
			    log.error("Cannot delete Flow Path {} from Network Map",
				      flowPath.flowId());
			    retry = true;
			}
		    } catch (TitanException te) {
			log.error("Titan Exception deleting Flow Path from Network MAP: {}", te);
			retry = true;
		    } catch (Exception e) {
			log.error("Exception deleting Flow Path from Network MAP: {}", e);
		    }
		} while (retry);

		continue;
	    }

	    //
	    // Test whether all Flow Entries are valid
	    //
	    boolean allValid = true;
	    for (FlowEntry flowEntry : flowPath.flowEntries()) {
		if (flowEntry.flowEntryUserState() ==
		    FlowEntryUserState.FE_USER_DELETE) {
		    continue;
		}
		if (! flowEntry.isValidFlowEntryId()) {
		    allValid = false;
		    break;
		}
		if (! enableOnrc2014MeasurementsFlows) {
		    if (flowEntry.flowEntrySwitchState() !=
			FlowEntrySwitchState.FE_SWITCH_UPDATED) {
			allValid = false;
			break;
		    }
		}
	    }
	    if (! allValid)
		continue;

	    log.debug("Pushing Flow Path To Database: {}", flowPath);

	    //
	    // Write the Flow Path to the Network Map
	    //
	    boolean retry = false;
	    do {
		retry = false;
		try {
                    long startTime = System.nanoTime();
		    if (! FlowDatabaseOperation.addFlow(dbHandlerInner, flowPath)) {
			log.error("Cannot write to Network Map Flow Path {}", flowPath.flowId());
			retry = true;
		    }
 		    // FIXME Flag to turn ON logging
                    //long endTime = System.nanoTime();
                    //log.error("Performance %% Flow path total time {} : {}", endTime - startTime, flowPath.toString());
		} catch (TitanException te) {
		    log.error("Titan Exception writing Flow Path to Network MAP: ", te);
		    retry = true;
 		    // FIXME Flag to turn ON logging
                    //long endTime = System.nanoTime();
                    //log.error("Performance %% Flow path total time {} : {}", endTime - startTime, flowPath.toString());
		} catch (Exception e) {
		    log.error("Exception writing Flow Path to Network MAP: ", e);
 		    // FIXME Flag to turn ON logging
                    //long endTime = System.nanoTime();
                    //log.error("Performance %% Flow path total time {} : {}", endTime - startTime, flowPath.toString());
		}
	    } while (retry);

	    if (enableOnrc2014MeasurementsFlows) {
		// Send the notifications

		for (FlowEntry flowEntry : flowPath.flowEntries()) {
		    if (flowEntry.flowEntrySwitchState() !=
			FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED) {
			continue;
		    }
		    // datagridService.notificationSendFlowEntryIdAdded(flowEntry.flowEntryId(), flowEntry.dpid());

		    //
		    // Write the Flow Entry to the Datagrid
		    //
		    switch (flowEntry.flowEntryUserState()) {
		    case FE_USER_ADD:
			datagridService.notificationSendFlowEntryAdded(flowEntry);
			break;
		    case FE_USER_MODIFY:
			datagridService.notificationSendFlowEntryUpdated(flowEntry);
			break;
		    case FE_USER_DELETE:
			datagridService.notificationSendFlowEntryRemoved(flowEntry.flowEntryId());
			break;
		    case FE_USER_UNKNOWN:
			assert(false);
			break;
		    }
		}
	    }
	}
    }
}
