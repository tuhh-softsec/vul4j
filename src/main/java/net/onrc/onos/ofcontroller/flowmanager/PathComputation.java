package net.onrc.onos.ofcontroller.flowmanager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.EventEntry;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for implementing the Path Computation and Path Maintenance.
 */
class PathComputation extends Thread implements IPathComputationService {
    /** The logger. */
    private final static Logger log = LoggerFactory.getLogger(PathComputation.class);

    private FlowManager flowManager;		// The Flow Manager to use
    private IDatagridService datagridService;	// The Datagrid Service to use

    // The queue with Flow Path and Topology Element updates
    private BlockingQueue<EventEntry<?>> networkEvents =
	new LinkedBlockingQueue<EventEntry<?>>();

    // The pending Topology and Flow Path events
    private List<EventEntry<TopologyElement>> topologyEvents =
	new LinkedList<EventEntry<TopologyElement>>();
    private List<EventEntry<FlowPath>> flowPathEvents =
	new LinkedList<EventEntry<FlowPath>>();

    /**
     * Constructor for a given Flow Manager and Datagrid Service.
     *
     * @param flowManager the Flow Manager to use.
     * @param datagridService the Datagrid Service to use.
     */
    PathComputation(FlowManager flowManager,
		    IDatagridService datagridService) {
	this.flowManager = flowManager;
	this.datagridService = datagridService;
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

		// Demultiplex all events
		for (EventEntry<?> event : collection) {
		    if (event.eventData() instanceof TopologyElement) {
			EventEntry<TopologyElement> topologyEventEntry =
			    (EventEntry<TopologyElement>)event;
			topologyEvents.add(topologyEventEntry);
		    } else if (event.eventData() instanceof FlowPath) {
			EventEntry<FlowPath> flowPathEventEntry =
			    (EventEntry<FlowPath>)event;
			flowPathEvents.add(flowPathEventEntry);
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
	if (topologyEvents.isEmpty() && flowPathEvents.isEmpty())
	    return;		// Nothing to do

	// TODO: Implement it!

	System.out.println("PAVPAV: Topology Events = " + topologyEvents.size() + " Flow Path Events = " + flowPathEvents.size());

	topologyEvents.clear();
	flowPathEvents.clear();
    }

    /**
     * Receive a notification that a Flow is added.
     *
     * @param flowPath the flow that is added.
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
     * @param flowPath the flow that is removed.
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
     * @param flowPath the flow that is updated.
     */
    @Override
    public void notificationRecvFlowUpdated(FlowPath flowPath) {
	// NOTE: The ADD and UPDATE events are processed in same way
	EventEntry<FlowPath> eventEntry =
	    new EventEntry<FlowPath>(EventEntry.Type.ENTRY_ADD, flowPath);
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
