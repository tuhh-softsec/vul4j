package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.topology.ShortestPath;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for implementing the Path Computation and Path Maintenance.
 */
class FlowEventHandler extends Thread implements IFlowEventHandlerService {
    /** The logger. */
    private final static Logger log = LoggerFactory.getLogger(FlowEventHandler.class);

    private FlowManager flowManager;		// The Flow Manager to use
    private IDatagridService datagridService;	// The Datagrid Service to use
    private Topology topology;			// The network topology
    private Map<Long, FlowPath> allFlowPaths = new HashMap<Long, FlowPath>();
    private List<FlowEntry> unmatchedFlowEntryUpdates =
	new LinkedList<FlowEntry>();

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
     * Run the thread.
     */
    @Override
    public void run() {
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

	// Process the events (if any)
	processEvents();

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
		    if (event.eventData() instanceof TopologyElement) {
			EventEntry<TopologyElement> topologyEventEntry =
			    (EventEntry<TopologyElement>)event;
			topologyEvents.add(topologyEventEntry);
		    } else if (event.eventData() instanceof FlowPath) {
			EventEntry<FlowPath> flowPathEventEntry =
			    (EventEntry<FlowPath>)event;
			flowPathEvents.add(flowPathEventEntry);
		    } else if (event.eventData() instanceof FlowEntry) {
			EventEntry<FlowEntry> flowEntryEventEntry =
			    (EventEntry<FlowEntry>)event;
			flowEntryEvents.add(flowEntryEventEntry);
		    }
		}
		collection.clear();

		// Process the events (if any)
		processEvents();
	    }
	} catch (Exception exception) {
	    log.debug("Exception processing Network Events: ", exception);
	}
    }

    /**
     * Process the events (if any)
     */
    private void processEvents() {
	List<FlowPath> newFlowPaths = new LinkedList<FlowPath>();
	List<FlowPath> recomputeFlowPaths = new LinkedList<FlowPath>();
	List<FlowPath> modifiedFlowPaths = new LinkedList<FlowPath>();

	if (topologyEvents.isEmpty() && flowPathEvents.isEmpty() &&
	    flowEntryEvents.isEmpty()) {
	    return;		// Nothing to do
	}

	//
	// Process the Flow Path events
	//
	for (EventEntry<FlowPath> eventEntry : flowPathEvents) {
	    FlowPath flowPath = eventEntry.eventData();

	    switch (eventEntry.eventType()) {
	    case ENTRY_ADD: {
		//
		// Add a new Flow Path
		//
		if (allFlowPaths.get(flowPath.flowId().value()) != null) {
		    //
		    // TODO: What to do if the Flow Path already exists?
		    // Remove and then re-add it, or merge the info?
		    // For now, we don't have to do anything.
		    //
		    break;
		}

		switch (flowPath.flowPathType()) {
		case FP_TYPE_SHORTEST_PATH:
		    //
		    // Reset the Data Path, in case it was set already, because
		    // we are going to recompute it anyway.
		    //
		    flowPath.flowEntries().clear();
		    recomputeFlowPaths.add(flowPath);
		    break;
		case FP_TYPE_EXPLICIT_PATH:
		    //
		    // Mark all Flow Entries for installation in the switches.
		    //
		    for (FlowEntry flowEntry : flowPath.flowEntries()) {
			flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED);
		    }
		    modifiedFlowPaths.add(flowPath);
		    break;
		}
		newFlowPaths.add(flowPath);

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

		allFlowPaths.remove(existingFlowPath.flowId());
		modifiedFlowPaths.add(existingFlowPath);

		break;
	    }
	    }
	}

	//
	// Process the topology events
	//
	boolean isTopologyModified = false;
	for (EventEntry<TopologyElement> eventEntry : topologyEvents) {
	    TopologyElement topologyElement = eventEntry.eventData();
	    switch (eventEntry.eventType()) {
	    case ENTRY_ADD:
		isTopologyModified = topology.addTopologyElement(topologyElement);
		break;
	    case ENTRY_REMOVE:
		isTopologyModified = topology.removeTopologyElement(topologyElement);
		break;
	    }
	}
	if (isTopologyModified) {
	    // TODO: For now, if the topology changes, we recompute all Flows
	    recomputeFlowPaths.addAll(allFlowPaths.values());
	}

	// Add all new Flows
	for (FlowPath flowPath : newFlowPaths) {
	    allFlowPaths.put(flowPath.flowId().value(), flowPath);
	}

	// Recompute all affected Flow Paths and keep only the modified
	for (FlowPath flowPath : recomputeFlowPaths) {
	    if (recomputeFlowPath(flowPath))
		modifiedFlowPaths.add(flowPath);
	}

	//
	// Process previously unmatched Flow Entry updates
	//
	if ((! flowPathEvents.isEmpty()) && (! unmatchedFlowEntryUpdates.isEmpty())) {
	    List<FlowEntry> remainingUpdates = new LinkedList<FlowEntry>();
	    for (FlowEntry flowEntry : unmatchedFlowEntryUpdates) {
		if (! updateFlowEntry(flowEntry))
		    remainingUpdates.add(flowEntry);
	    }
	    unmatchedFlowEntryUpdates = remainingUpdates;
	}

	//
	// Process the Flow Entry events
	//
	for (EventEntry<FlowEntry> eventEntry : flowEntryEvents) {
	    FlowEntry flowEntry = eventEntry.eventData();
	    switch (eventEntry.eventType()) {
	    case ENTRY_ADD:
		//
		// Find the corresponding Flow Entry and update it.
		// If not found, then keep it in a local cache for
		// later matching.
		//
		if (! updateFlowEntry(flowEntry))
		    unmatchedFlowEntryUpdates.add(flowEntry);
		break;
	    case ENTRY_REMOVE:
		//
		// NOTE: For now we remove the Flow Entries based on
		// local decisions, so no need to remove them because of an
		// external event.
		//
		break;
	    }
	}

	//
	// Push the Flow Entries that have been modified
	//
	flowManager.pushModifiedFlowEntries(modifiedFlowPaths);

	// Cleanup
	topologyEvents.clear();
	flowPathEvents.clear();
	flowEntryEvents.clear();
    }

    /**
     * Update a Flow Entry because of an external event.
     *
     * @param flowEntry the FlowEntry with the new state.
     * @return true if the Flow Entry was found and updated, otherwise false.
     */
    private boolean updateFlowEntry(FlowEntry flowEntry) {
	if ((! flowEntry.isValidFlowId()) ||
	    (! flowEntry.isValidFlowEntryId())) {
	    //
	    // Ignore events for Flow Entries with invalid Flow ID or
	    // Flow Entry ID.
	    // This shouldn't happen.
	    //
	    return true;
	}

	FlowPath flowPath = allFlowPaths.get(flowEntry.flowId().value());
	if (flowPath == null)
	    return false;

	//
	// Iterate over all Flow Entries and find a match based on the DPID
	//
	for (FlowEntry localFlowEntry : flowPath.flowEntries()) {
	    if (localFlowEntry.dpid().value() != flowEntry.dpid().value())
		continue;
	    //
	    // TODO: We might want to check the FlowEntryMatch and
	    // FlowEntryActions to double-check it is the same Flow Entry
	    //

	    //
	    // Local Flow Entry match found
	    //
	    if (localFlowEntry.isValidFlowEntryId()) {
		if (localFlowEntry.flowEntryId().value() !=
		    flowEntry.flowEntryId().value()) {
		    //
		    // Find a local Flow Entry, but the Flow Entry ID doesn't
		    // match. Ignore the event.
		    //
		    return true;
		}
	    } else {
		// Update the Flow Entry ID
		FlowEntryId flowEntryId =
		    new FlowEntryId(flowEntry.flowEntryId().value());
		localFlowEntry.setFlowEntryId(flowEntryId);
	    }

	    //
	    // Update the local Flow Entry.
	    // For now we update only the Flow Entry Switch State
	    //
	    localFlowEntry.setFlowEntrySwitchState(flowEntry.flowEntrySwitchState());
	    return true;
	}

	return false;		// Entry not found
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
		// The old Flow Entry should be deleted
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
		// The old Flow Entry should be deleted
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

	    // Set the incoming port matching
	    FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
	    newFlowEntry.setFlowEntryMatch(flowEntryMatch);
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
	for (FlowEntry flowEntry : deletedFlowEntries)
	    finalFlowEntries.add(flowEntry);
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
}
