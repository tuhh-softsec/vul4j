package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.topology.Topology;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.topology.TopologyManager;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.EventEntry;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryAction;
import net.onrc.onos.ofcontroller.util.FlowEntryActions;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;
import net.onrc.onos.ofcontroller.util.FlowEntrySwitchState;
import net.onrc.onos.ofcontroller.util.FlowEntryUserState;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.FlowPathUserState;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import com.esotericsoftware.kryo.Kryo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for FlowPath Maintenance.
 * This class listens for FlowEvents to:
 * - Maintain a local cache of the Network Topology.
 * - Detect FlowPaths impacted by Topology change.
 * - Recompute impacted FlowPath using cached Topology.
 */
class FlowEventHandler extends Thread implements IFlowEventHandlerService {
    /** The logger. */
    private final static Logger log = LoggerFactory.getLogger(FlowEventHandler.class);

    private FlowManager flowManager;		// The Flow Manager to use
    private IDatagridService datagridService;	// The Datagrid Service to use
    private Topology topology;			// The network topology
    private KryoFactory kryoFactory = new KryoFactory();

    // The queue with Flow Path and Topology Element updates
    private BlockingQueue<EventEntry<?>> networkEvents =
	new LinkedBlockingQueue<EventEntry<?>>();

    // The pending Topology, FlowPath, and FlowEntry events
    private List<EventEntry<TopologyElement>> topologyEvents =
	new LinkedList<EventEntry<TopologyElement>>();
    private List<EventEntry<FlowPath>> flowPathEvents =
	new LinkedList<EventEntry<FlowPath>>();
    private List<EventEntry<FlowEntry>> flowEntryEvents =
	new LinkedList<EventEntry<FlowEntry>>();

    // All internally computed Flow Paths
    private Map<Long, FlowPath> allFlowPaths = new HashMap<Long, FlowPath>();

    // The Flow Entries received as notifications with unmatched Flow Paths
    private Map<Long, FlowEntry> unmatchedFlowEntryAdd =
	new HashMap<Long, FlowEntry>();

    //
    // Transient state for processing the Flow Paths:
    //  - The Flow Paths that should be recomputed
    //  - The Flow Paths with modified Flow Entries
    //  - The Flow Paths that we should check if installed in all switches
    //
    private Map<Long, FlowPath> shouldRecomputeFlowPaths =
	new HashMap<Long, FlowPath>();
    private Map<Long, FlowPath> modifiedFlowPaths =
	new HashMap<Long, FlowPath>();
    private Map<Long, FlowPath> checkIfInstalledFlowPaths =
	new HashMap<Long, FlowPath>();

    /**
     * Constructor for a given Flow Manager and Datagrid Service.
     *
     * @param flowManager the Flow Manager to use.
     * @param datagridService the Datagrid Service to use.
     */
    FlowEventHandler(FlowManager flowManager,
		     IDatagridService datagridService) {
	this.flowManager = flowManager;
	this.datagridService = datagridService;
	this.topology = new Topology();
    }

    /**
     * Get the network topology.
     *
     * @return the network topology.
     */
    protected Topology getTopology() { return this.topology; }

    /**
     * Startup processing.
     */
    private void startup() {
	//
	// Obtain the initial Topology state
	//
	Collection<TopologyElement> topologyElements =
	    datagridService.getAllTopologyElements();
	for (TopologyElement topologyElement : topologyElements) {
	    EventEntry<TopologyElement> eventEntry =
		new EventEntry<TopologyElement>(EventEntry.Type.ENTRY_ADD, topologyElement);
	    topologyEvents.add(eventEntry);
	}
	//
	// Obtain the initial Flow Path state
	//
	Collection<FlowPath> flowPaths = datagridService.getAllFlows();
	for (FlowPath flowPath : flowPaths) {
	    EventEntry<FlowPath> eventEntry =
		new EventEntry<FlowPath>(EventEntry.Type.ENTRY_ADD, flowPath);
	    flowPathEvents.add(eventEntry);
	}
	//
	// Obtain the initial FlowEntry state
	//
	Collection<FlowEntry> flowEntries = datagridService.getAllFlowEntries();
	for (FlowEntry flowEntry : flowEntries) {
	    EventEntry<FlowEntry> eventEntry =
		new EventEntry<FlowEntry>(EventEntry.Type.ENTRY_ADD, flowEntry);
	    flowEntryEvents.add(eventEntry);
	}

	// Process the initial events (if any)
	synchronized (allFlowPaths) {
	    processEvents();
	}
    }

    /**
     * Run the thread.
     */
    @Override
    public void run() {
	this.setName("FlowEventHandler " + this.getId());
	startup();

	//
	// The main loop
	//
	Collection<EventEntry<?>> collection = new LinkedList<EventEntry<?>>();
	try {
	    while (true) {
		EventEntry<?> eventEntry = networkEvents.take();
		collection.add(eventEntry);
		networkEvents.drainTo(collection);

		//
		// Demultiplex all events:
		//  - EventEntry<TopologyElement>
		//  - EventEntry<FlowPath>
		//  - EventEntry<FlowEntry>
		//
		for (EventEntry<?> event : collection) {
		    // Topology event
		    if (event.eventData() instanceof TopologyElement) {
			EventEntry<TopologyElement> topologyEventEntry =
			    (EventEntry<TopologyElement>)event;
			topologyEvents.add(topologyEventEntry);
			continue;
		    }

		    // FlowPath event
		    if (event.eventData() instanceof FlowPath) {
			EventEntry<FlowPath> flowPathEventEntry =
			    (EventEntry<FlowPath>)event;
			flowPathEvents.add(flowPathEventEntry);
			continue;
		    }

		    // FlowEntry event
		    if (event.eventData() instanceof FlowEntry) {
			EventEntry<FlowEntry> flowEntryEventEntry =
			    (EventEntry<FlowEntry>)event;
			flowEntryEvents.add(flowEntryEventEntry);
			continue;
		    }
		}
		collection.clear();

		// Process the events (if any)
		synchronized (allFlowPaths) {
		    processEvents();
		}
	    }
	} catch (Exception exception) {
	    log.debug("Exception processing Network Events: ", exception);
	}
    }

    /**
     * Process the events (if any)
     */
    private void processEvents() {
	Collection<FlowEntry> modifiedFlowEntries;

	if (topologyEvents.isEmpty() && flowPathEvents.isEmpty() &&
	    flowEntryEvents.isEmpty()) {
	    return;		// Nothing to do
	}

	processFlowPathEvents();
	processTopologyEvents();
	processUnmatchedFlowEntryAdd();
	processFlowEntryEvents();

	// Recompute all affected Flow Paths and keep only the modified
	for (FlowPath flowPath : shouldRecomputeFlowPaths.values()) {
	    if (recomputeFlowPath(flowPath))
		modifiedFlowPaths.put(flowPath.flowId().value(), flowPath);
	}

	// Extract the modified Flow Entries
	modifiedFlowEntries = extractModifiedFlowEntries(modifiedFlowPaths.values());

	// Assign missing Flow Entry IDs
	assignFlowEntryId(modifiedFlowEntries);

	//
	// Push the modified state to the Flow Manager
	//
	flowManager.pushModifiedFlowState(modifiedFlowPaths.values(),
					  modifiedFlowEntries);

	//
	// Remove Flow Entries that were deleted
	//
	for (FlowPath flowPath : modifiedFlowPaths.values())
	    flowPath.dataPath().removeDeletedFlowEntries();

	//
	// Check if Flow Paths have been installed into all switches,
	// and generate the appropriate events.
	//
	checkInstalledFlowPaths(checkIfInstalledFlowPaths.values());

	// Cleanup
	topologyEvents.clear();
	flowPathEvents.clear();
	flowEntryEvents.clear();
	//
	shouldRecomputeFlowPaths.clear();
	modifiedFlowPaths.clear();
	checkIfInstalledFlowPaths.clear();
    }

    /**
     * Check if Flow Paths have been installed into all switches,
     * and generate the appropriate events.
     *
     * @param flowPaths the flowPaths to process.
     */
    private void checkInstalledFlowPaths(Collection<FlowPath> flowPaths) {
	List<FlowPath> installedFlowPaths = new LinkedList<FlowPath>();

	Kryo kryo = kryoFactory.newKryo();

	for (FlowPath flowPath : flowPaths) {
	    boolean isInstalled = true;
	    
	    if (flowPath.flowEntries().isEmpty()) {
	    	continue;
	    }

	    //
	    // Check whether all Flow Entries have been installed
	    //
	    for (FlowEntry flowEntry : flowPath.flowEntries()) {
		if (flowEntry.flowEntrySwitchState() !=
		    FlowEntrySwitchState.FE_SWITCH_UPDATED) {
		    isInstalled = false;
		    break;
		}
	    }

	    if (isInstalled) {
		// Create a copy and add it to the list
		FlowPath copyFlowPath = kryo.copy(flowPath);
		installedFlowPaths.add(copyFlowPath);
	    }
	}
	kryoFactory.deleteKryo(kryo);

	// Generate an event for the installed Flow Path.
	flowManager.notificationFlowPathsInstalled(installedFlowPaths);
    }

    /**
     * Extract the modified Flow Entries.
     *
     * @param modifiedFlowPaths the Flow Paths to process.
     * @return a collection with the modified Flow Entries.
     */
    private Collection<FlowEntry> extractModifiedFlowEntries(
			Collection<FlowPath> modifiedFlowPaths) {
	List<FlowEntry> modifiedFlowEntries = new LinkedList<FlowEntry>();

	// Extract only the modified Flow Entries
	for (FlowPath flowPath : modifiedFlowPaths) {
	    for (FlowEntry flowEntry : flowPath.flowEntries()) {
		if (flowEntry.flowEntrySwitchState() ==
		    FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED) {
		    modifiedFlowEntries.add(flowEntry);
		}
	    }
	}
	return modifiedFlowEntries;
    }

    /**
     * Assign the Flow Entry ID as needed.
     *
     * @param modifiedFlowEnries the collection of Flow Entries that need
     * Flow Entry ID assigned.
     */
    private void assignFlowEntryId(Collection<FlowEntry> modifiedFlowEntries) {
	if (modifiedFlowEntries.isEmpty())
	    return;

	Map<Long, IOFSwitch> mySwitches = flowManager.getMySwitches();

	//
	// Assign the Flow Entry ID only for Flow Entries for my switches
	//
	for (FlowEntry flowEntry : modifiedFlowEntries) {
	    IOFSwitch mySwitch = mySwitches.get(flowEntry.dpid().value());
	    if (mySwitch == null)
		continue;
	    if (! flowEntry.isValidFlowEntryId()) {
		long id = flowManager.getNextFlowEntryId();
		flowEntry.setFlowEntryId(new FlowEntryId(id));
	    }
	}
    }

    /**
     * Process the Flow Path events.
     */
    private void processFlowPathEvents() {
	//
	// Process all Flow Path events and update the appropriate state
	//
	for (EventEntry<FlowPath> eventEntry : flowPathEvents) {
	    FlowPath flowPath = eventEntry.eventData();

	    log.debug("Flow Event: {} {}", eventEntry.eventType(), flowPath);

	    switch (eventEntry.eventType()) {
	    case ENTRY_ADD: {
		//
		// Add a new Flow Path
		//
		if (allFlowPaths.get(flowPath.flowId().value()) != null) {
		    //
		    // TODO: What to do if the Flow Path already exists?
		    // Fow now, we just re-add it.
		    //
		}

		switch (flowPath.flowPathType()) {
		case FP_TYPE_SHORTEST_PATH:
		    //
		    // Reset the Data Path, in case it was set already, because
		    // we are going to recompute it anyway.
		    //
		    flowPath.flowEntries().clear();
		    shouldRecomputeFlowPaths.put(flowPath.flowId().value(),
						 flowPath);
		    break;
		case FP_TYPE_EXPLICIT_PATH:
		    //
		    // Mark all Flow Entries for installation in the switches.
		    //
		    for (FlowEntry flowEntry : flowPath.flowEntries()) {
			flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
		    }
		    modifiedFlowPaths.put(flowPath.flowId().value(), flowPath);
		    break;
		case FP_TYPE_UNKNOWN:
		    log.error("FlowPath event with unknown type");
		    break;
		}
		allFlowPaths.put(flowPath.flowId().value(), flowPath);

		break;
	    }

	    case ENTRY_REMOVE: {
		//
		// Remove an existing Flow Path.
		//
		// Find the Flow Path, and mark the Flow and its Flow Entries
		// for deletion.
		//
		FlowPath existingFlowPath =
		    allFlowPaths.get(flowPath.flowId().value());
		if (existingFlowPath == null)
		    continue;		// Nothing to do

		existingFlowPath.setFlowPathUserState(FlowPathUserState.FP_USER_DELETE);
		for (FlowEntry flowEntry : existingFlowPath.flowEntries()) {
		    flowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_DELETE);
		    flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
		}

		// Remove the Flow Path from the internal state
		Long key = existingFlowPath.flowId().value();
		allFlowPaths.remove(key);
		shouldRecomputeFlowPaths.remove(key);
		modifiedFlowPaths.put(key, existingFlowPath);

		break;
	    }
	    }
	}
    }

    /**
     * Process the Topology events.
     */
    private void processTopologyEvents() {
	//
	// Process all Topology events and update the appropriate state
	//
	boolean isTopologyModified = false;
	for (EventEntry<TopologyElement> eventEntry : topologyEvents) {
	    TopologyElement topologyElement = eventEntry.eventData();

	    log.debug("Topology Event: {} {}", eventEntry.eventType(),
		      topologyElement);

	    switch (eventEntry.eventType()) {
	    case ENTRY_ADD:
		isTopologyModified |= topology.addTopologyElement(topologyElement);
		break;
	    case ENTRY_REMOVE:
		isTopologyModified |= topology.removeTopologyElement(topologyElement);
		break;
	    }
	}
	if (isTopologyModified) {
	    // TODO: For now, if the topology changes, we recompute all Flows
	    shouldRecomputeFlowPaths.putAll(allFlowPaths);
	}
    }

    /**
     * Process previously received Flow Entries with unmatched Flow Paths.
     */
    private void processUnmatchedFlowEntryAdd() {
	FlowPath flowPath;
	FlowEntry localFlowEntry;

	//
	// Update Flow Entries with previously unmatched Flow Entry updates
	//
	if (! unmatchedFlowEntryAdd.isEmpty()) {
	    Map<Long, FlowEntry> remainingUpdates = new HashMap<Long, FlowEntry>();
	    for (FlowEntry flowEntry : unmatchedFlowEntryAdd.values()) {
		// log.debug("Processing Unmatched Flow Entry: {}",
		//	  flowEntry.toString());

		flowPath = allFlowPaths.get(flowEntry.flowId().value());
		if (flowPath == null) {
		    remainingUpdates.put(flowEntry.flowEntryId().value(),
					 flowEntry);
		    continue;
		}
		localFlowEntry = findFlowEntryAdd(flowPath, flowEntry);
		if (localFlowEntry == null) {
		    remainingUpdates.put(flowEntry.flowEntryId().value(),
					 flowEntry);
		    continue;
		}
		if (updateFlowEntryAdd(flowPath, localFlowEntry, flowEntry)) {
		    modifiedFlowPaths.put(flowPath.flowId().value(), flowPath);
		}
	    }
	    unmatchedFlowEntryAdd = remainingUpdates;
	}
    }

    /**
     * Process the Flow Entry events.
     */
    private void processFlowEntryEvents() {
	FlowPath flowPath;
	FlowEntry localFlowEntry;

	//
	// Process all Flow Entry events and update the appropriate state
	//
	for (EventEntry<FlowEntry> eventEntry : flowEntryEvents) {
	    FlowEntry flowEntry = eventEntry.eventData();

	    log.debug("Flow Entry Event: {} {}", eventEntry.eventType(),
		      flowEntry);

	    if ((! flowEntry.isValidFlowId()) ||
		(! flowEntry.isValidFlowEntryId())) {
		continue;
	    }

	    switch (eventEntry.eventType()) {
	    case ENTRY_ADD:
		flowPath = allFlowPaths.get(flowEntry.flowId().value());
		if (flowPath == null) {
		    // Flow Path not found: keep the entry for later matching
		    unmatchedFlowEntryAdd.put(flowEntry.flowEntryId().value(),
					      flowEntry);
		    break;
		}
		localFlowEntry = findFlowEntryAdd(flowPath, flowEntry);
		if (localFlowEntry == null) {
		    // Flow Entry not found: keep the entry for later matching
		    unmatchedFlowEntryAdd.put(flowEntry.flowEntryId().value(),
					      flowEntry);
		    break;
		}
		if (updateFlowEntryAdd(flowPath, localFlowEntry, flowEntry)) {
		    // Add the updated Flow Path to the list of updated paths
		    modifiedFlowPaths.put(flowPath.flowId().value(), flowPath);
		}
		break;

	    case ENTRY_REMOVE:
		flowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_DELETE);
		if (unmatchedFlowEntryAdd.remove(flowEntry.flowEntryId().value()) != null) {
		    continue;		// Removed previously unmatched entry
		}

		flowPath = allFlowPaths.get(flowEntry.flowId().value());
		if (flowPath == null) {
		    // Flow Path not found: ignore the update
		    break;
		}
		localFlowEntry = findFlowEntryRemove(flowPath, flowEntry);
		if (localFlowEntry == null) {
		    // Flow Entry not found: ignore it
		    break;
		}
		if (updateFlowEntryRemove(flowPath, localFlowEntry,
					  flowEntry)) {
		    // Add the updated Flow Path to the list of updated paths
		    modifiedFlowPaths.put(flowPath.flowId().value(), flowPath);
		}
		break;
	    }
	}
    }

    /**
     * Find a Flow Entry that should be updated because of an external
     * ENTRY_ADD event.
     *
     * @param flowPath the FlowPath for the Flow Entry to update.
     * @param newFlowEntry the FlowEntry with the new state.
     * @return the Flow Entry that should be updated if found, otherwise null.
     */
    private FlowEntry findFlowEntryAdd(FlowPath flowPath,
				       FlowEntry newFlowEntry) {
	//
	// Iterate over all Flow Entries and find a match.
	//
	for (FlowEntry localFlowEntry : flowPath.flowEntries()) {
	    if (! TopologyManager.isSameFlowEntryDataPath(localFlowEntry,
							  newFlowEntry)) {
		continue;
	    }

	    //
	    // Local Flow Entry match found
	    //
	    if (localFlowEntry.isValidFlowEntryId()) {
		if (localFlowEntry.flowEntryId().value() !=
		    newFlowEntry.flowEntryId().value()) {
		    //
		    // Find a local Flow Entry, but the Flow Entry ID doesn't
		    // match. Keep looking.
		    //
		    continue;
		}
	    }
	    return localFlowEntry;
	}

	return null;		// Entry not found
    }

    /**
     * Update a Flow Entry because of an external ENTRY_ADD event.
     *
     * @param flowPath the FlowPath for the Flow Entry to update.
     * @param localFlowEntry the local Flow Entry to update.
     * @param newFlowEntry the FlowEntry with the new state.
     * @return true if the local Flow Entry was updated, otherwise false.
     */
    private boolean updateFlowEntryAdd(FlowPath flowPath,
				       FlowEntry localFlowEntry,
				       FlowEntry newFlowEntry) {
	boolean updated = false;

	if (localFlowEntry.flowEntryUserState() ==
	    FlowEntryUserState.FE_USER_DELETE) {
	    // Don't add-back a Flow Entry that is already deleted
	    return false;
	}

	if (! localFlowEntry.isValidFlowEntryId()) {
	    // Update the Flow Entry ID
	    FlowEntryId flowEntryId =
		new FlowEntryId(newFlowEntry.flowEntryId().value());
	    localFlowEntry.setFlowEntryId(flowEntryId);
	    updated = true;
	}

	//
	// Update the local Flow Entry, and keep state to check
	// if the Flow Path has been installed.
	//
	if (localFlowEntry.flowEntryUserState() !=
	    newFlowEntry.flowEntryUserState()) {
	    localFlowEntry.setFlowEntryUserState(
			 newFlowEntry.flowEntryUserState());
	    updated = true;
	}
	if (localFlowEntry.flowEntrySwitchState() !=
	    newFlowEntry.flowEntrySwitchState()) {
	    localFlowEntry.setFlowEntrySwitchState(
			newFlowEntry.flowEntrySwitchState());
	    checkIfInstalledFlowPaths.put(flowPath.flowId().value(), flowPath);
	    updated = true;
	}

	return updated;
    }

    /**
     * Find a Flow Entry that should be updated because of an external
     * ENTRY_REMOVE event.
     *
     * @param flowPath the FlowPath for the Flow Entry to update.
     * @param newFlowEntry the FlowEntry with the new state.
     * @return the Flow Entry that should be updated if found, otherwise null.
     */
    private FlowEntry findFlowEntryRemove(FlowPath flowPath,
					  FlowEntry newFlowEntry) {
	//
	// Iterate over all Flow Entries and find a match based on
	// the Flow Entry ID.
	//
	for (FlowEntry localFlowEntry : flowPath.flowEntries()) {
	    if (! localFlowEntry.isValidFlowEntryId())
		continue;
	    if (localFlowEntry.flowEntryId().value() !=
		newFlowEntry.flowEntryId().value()) {
		continue;
	    }
	    return localFlowEntry;
	}

	return null;		// Entry not found
    }

    /**
     * Update a Flow Entry because of an external ENTRY_REMOVE event.
     *
     * @param flowPath the FlowPath for the Flow Entry to update.
     * @param localFlowEntry the local Flow Entry to update.
     * @param newFlowEntry the FlowEntry with the new state.
     * @return true if the local Flow Entry was updated, otherwise false.
     */
    private boolean updateFlowEntryRemove(FlowPath flowPath,
					  FlowEntry localFlowEntry,
					  FlowEntry newFlowEntry) {
	boolean updated = false;

	//
	// Update the local Flow Entry.
	//
	if (localFlowEntry.flowEntryUserState() !=
	    newFlowEntry.flowEntryUserState()) {
	    localFlowEntry.setFlowEntryUserState(
			newFlowEntry.flowEntryUserState());
	    updated = true;
	}
	if (localFlowEntry.flowEntrySwitchState() !=
	    newFlowEntry.flowEntrySwitchState()) {
	    localFlowEntry.setFlowEntrySwitchState(
			newFlowEntry.flowEntrySwitchState());
	    updated = true;
	}

	return updated;
    }

    /**
     * Recompute a Flow Path.
     *
     * @param flowPath the Flow Path to recompute.
     * @return true if the recomputed Flow Path has changed, otherwise false.
     */
    private boolean recomputeFlowPath(FlowPath flowPath) {
	boolean hasChanged = false;

	//
	// Test whether the Flow Path needs to be recomputed
	//
	switch (flowPath.flowPathType()) {
	case FP_TYPE_UNKNOWN:
	    return false;		// Can't recompute on Unknown FlowType
	case FP_TYPE_SHORTEST_PATH:
	    break;
	case FP_TYPE_EXPLICIT_PATH:
	    return false;		// An explicit path never changes
	}

	DataPath oldDataPath = flowPath.dataPath();

	// Compute the new path
	DataPath newDataPath = TopologyManager.computeNetworkPath(topology,
								  flowPath);
	if (newDataPath == null) {
	    // We need the DataPath to compare the paths
	    newDataPath = new DataPath();
	}
	newDataPath.applyFlowPathFlags(flowPath.flowPathFlags());

	//
	// Test whether the new path is same
	//
	if (oldDataPath.flowEntries().size() !=
	    newDataPath.flowEntries().size()) {
	    hasChanged = true;
	} else {
	    Iterator<FlowEntry> oldIter = oldDataPath.flowEntries().iterator();
	    Iterator<FlowEntry> newIter = newDataPath.flowEntries().iterator();
	    while (oldIter.hasNext() && newIter.hasNext()) {
		FlowEntry oldFlowEntry = oldIter.next();
		FlowEntry newFlowEntry = newIter.next();
		if (! TopologyManager.isSameFlowEntryDataPath(oldFlowEntry,
							      newFlowEntry)) {
		    hasChanged = true;
		    break;
		}
	    }
	}
	if (! hasChanged)
	    return hasChanged;

	//
	// Merge the changes in the path:
	//  - If a Flow Entry for a switch is in the old data path, but not
	//    in the new data path, then mark it for deletion.
	//  - If a Flow Entry for a switch is in the new data path, but not
	//    in the old data path, then mark it for addition.
	//  - If a Flow Entry for a switch is in both the old and the new
	//    data path, but it has changed, e.g., the incoming and/or outgoing
	//    port(s), then mark the old Flow Entry for deletion, and mark
	//    the new Flow Entry for addition.
	//  - If a Flow Entry for a switch is in both the old and the new
	//    data path, and it hasn't changed, then just keep it.
	//
	// NOTE: We use the Switch DPID of each entry to match the entries
	//
	Map<Long, FlowEntry> oldFlowEntriesMap = new HashMap<Long, FlowEntry>();
	Map<Long, FlowEntry> newFlowEntriesMap = new HashMap<Long, FlowEntry>();
	ArrayList<FlowEntry> finalFlowEntries = new ArrayList<FlowEntry>();
	List<FlowEntry> deletedFlowEntries = new LinkedList<FlowEntry>();

	// Prepare maps with the Flow Entries, so they are fast to lookup
	for (FlowEntry flowEntry : oldDataPath.flowEntries())
	    oldFlowEntriesMap.put(flowEntry.dpid().value(), flowEntry);
	for (FlowEntry flowEntry : newDataPath.flowEntries())
	    newFlowEntriesMap.put(flowEntry.dpid().value(), flowEntry);

	//
	// Find the old Flow Entries that should be deleted
	//
	for (FlowEntry oldFlowEntry : oldDataPath.flowEntries()) {
	    FlowEntry newFlowEntry =
		newFlowEntriesMap.get(oldFlowEntry.dpid().value());
	    if (newFlowEntry == null) {
		// The old Flow Entry should be deleted: not on the path
		oldFlowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_DELETE);
		oldFlowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
		deletedFlowEntries.add(oldFlowEntry);
	    }
	}

	//
	// Find the new Flow Entries that should be added or updated
	//
	int idx = 0;
	for (FlowEntry newFlowEntry : newDataPath.flowEntries()) {
	    FlowEntry oldFlowEntry =
		oldFlowEntriesMap.get(newFlowEntry.dpid().value());

	    if ((oldFlowEntry != null) &&
		TopologyManager.isSameFlowEntryDataPath(oldFlowEntry,
							newFlowEntry)) {
		//
		// Both Flow Entries are same
		//
		finalFlowEntries.add(oldFlowEntry);
		idx++;
		continue;
	    }

	    if (oldFlowEntry != null) {
		//
		// The old Flow Entry should be deleted: path diverges
		//
		oldFlowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_DELETE);
		oldFlowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
		deletedFlowEntries.add(oldFlowEntry);
	    }

	    //
	    // Add the new Flow Entry
	    //
	    //
	    // NOTE: Assign only the Flow ID.
	    // The Flow Entry ID is assigned later only for the Flow Entries
	    // this instance is responsible for.
	    //
	    newFlowEntry.setFlowId(new FlowId(flowPath.flowId().value()));

	    //
	    // Copy the Flow timeouts
	    //
	    newFlowEntry.setIdleTimeout(flowPath.idleTimeout());
	    newFlowEntry.setHardTimeout(flowPath.hardTimeout());

	    //
	    // Allocate the FlowEntryMatch by copying the default one
	    // from the FlowPath (if set).
	    //
	    FlowEntryMatch flowEntryMatch = null;
	    if (flowPath.flowEntryMatch() != null)
		flowEntryMatch = new FlowEntryMatch(flowPath.flowEntryMatch());
	    else
		flowEntryMatch = new FlowEntryMatch();
	    newFlowEntry.setFlowEntryMatch(flowEntryMatch);

	    // Set the incoming port matching
	    flowEntryMatch.enableInPort(newFlowEntry.inPort());

	    //
	    // Set the actions:
	    // If the first Flow Entry, copy the Flow Path actions to it.
	    //
	    FlowEntryActions flowEntryActions = newFlowEntry.flowEntryActions();
	    if ((idx == 0)  && (flowPath.flowEntryActions() != null)) {
		FlowEntryActions flowActions =
		    new FlowEntryActions(flowPath.flowEntryActions());
		for (FlowEntryAction action : flowActions.actions())
		    flowEntryActions.addAction(action);
	    }
	    idx++;

	    //
	    // Add the outgoing port output action
	    //
	    FlowEntryAction flowEntryAction = new FlowEntryAction();
	    flowEntryAction.setActionOutput(newFlowEntry.outPort());
	    flowEntryActions.addAction(flowEntryAction);

	    //
	    // Set the state of the new Flow Entry
	    //
	    newFlowEntry.setFlowEntryUserState(FlowEntryUserState.FE_USER_ADD);
	    newFlowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
	    finalFlowEntries.add(newFlowEntry);
	}

	//
	// Replace the old Flow Entries with the new Flow Entries.
	// Note that the Flow Entries that will be deleted are added at
	// the end.
	//
	finalFlowEntries.addAll(deletedFlowEntries);
	flowPath.dataPath().setFlowEntries(finalFlowEntries);

	return hasChanged;
    }

    /**
     * Receive a notification that a Flow is added.
     *
     * @param flowPath the Flow that is added.
     */
    @Override
    public void notificationRecvFlowAdded(FlowPath flowPath) {
	EventEntry<FlowPath> eventEntry =
	    new EventEntry<FlowPath>(EventEntry.Type.ENTRY_ADD, flowPath);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a Flow is removed.
     *
     * @param flowPath the Flow that is removed.
     */
    @Override
    public void notificationRecvFlowRemoved(FlowPath flowPath) {
	EventEntry<FlowPath> eventEntry =
	    new EventEntry<FlowPath>(EventEntry.Type.ENTRY_REMOVE, flowPath);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a Flow is updated.
     *
     * @param flowPath the Flow that is updated.
     */
    @Override
    public void notificationRecvFlowUpdated(FlowPath flowPath) {
	// NOTE: The ADD and UPDATE events are processed in same way
	EventEntry<FlowPath> eventEntry =
	    new EventEntry<FlowPath>(EventEntry.Type.ENTRY_ADD, flowPath);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a FlowEntry is added.
     *
     * @param flowEntry the FlowEntry that is added.
     */
    @Override
    public void notificationRecvFlowEntryAdded(FlowEntry flowEntry) {
	EventEntry<FlowEntry> eventEntry =
	    new EventEntry<FlowEntry>(EventEntry.Type.ENTRY_ADD, flowEntry);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a FlowEntry is removed.
     *
     * @param flowEntry the FlowEntry that is removed.
     */
    @Override
    public void notificationRecvFlowEntryRemoved(FlowEntry flowEntry) {
	EventEntry<FlowEntry> eventEntry =
	    new EventEntry<FlowEntry>(EventEntry.Type.ENTRY_REMOVE, flowEntry);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a FlowEntry is updated.
     *
     * @param flowEntry the FlowEntry that is updated.
     */
    @Override
    public void notificationRecvFlowEntryUpdated(FlowEntry flowEntry) {
	// NOTE: The ADD and UPDATE events are processed in same way
	EventEntry<FlowEntry> eventEntry =
	    new EventEntry<FlowEntry>(EventEntry.Type.ENTRY_ADD, flowEntry);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a Topology Element is added.
     *
     * @param topologyElement the Topology Element that is added.
     */
    @Override
    public void notificationRecvTopologyElementAdded(TopologyElement topologyElement) {
	EventEntry<TopologyElement> eventEntry =
	    new EventEntry<TopologyElement>(EventEntry.Type.ENTRY_ADD, topologyElement);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a Topology Element is removed.
     *
     * @param topologyElement the Topology Element that is removed.
     */
    @Override
    public void notificationRecvTopologyElementRemoved(TopologyElement topologyElement) {
	EventEntry<TopologyElement> eventEntry =
	    new EventEntry<TopologyElement>(EventEntry.Type.ENTRY_REMOVE, topologyElement);
	networkEvents.add(eventEntry);
    }

    /**
     * Receive a notification that a Topology Element is updated.
     *
     * @param topologyElement the Topology Element that is updated.
     */
    @Override
    public void notificationRecvTopologyElementUpdated(TopologyElement topologyElement) {
	// NOTE: The ADD and UPDATE events are processed in same way
	EventEntry<TopologyElement> eventEntry =
	    new EventEntry<TopologyElement>(EventEntry.Type.ENTRY_ADD, topologyElement);
	networkEvents.add(eventEntry);
    }

    /**
     * Get a sorted copy of all Flow Paths.
     *
     * @return a sorted copy of all Flow Paths.
     */
    synchronized SortedMap<Long, FlowPath> getAllFlowPathsCopy() {
	SortedMap<Long, FlowPath> sortedFlowPaths =
	    new TreeMap<Long, FlowPath>();

	//
	// TODO: For now we use serialization/deserialization to create
	// a copy of each Flow Path. In the future, we should use proper
	// copy constructors.
	//
	Kryo kryo = kryoFactory.newKryo();
	synchronized (allFlowPaths) {
	    for (Map.Entry<Long, FlowPath> entry : allFlowPaths.entrySet()) {
		FlowPath origFlowPath = entry.getValue();
		FlowPath copyFlowPath = kryo.copy(origFlowPath);
		sortedFlowPaths.put(entry.getKey(), copyFlowPath);
	    }
	}
	kryoFactory.deleteKryo(kryo);

	return sortedFlowPaths;
    }
}
