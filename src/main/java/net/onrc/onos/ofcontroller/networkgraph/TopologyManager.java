package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.datagrid.IEventChannelListener;
import net.onrc.onos.datastore.topology.RCLink;
import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;
import net.onrc.onos.ofcontroller.util.EventEntry;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.registry.controller.IControllerRegistryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The "NB" read-only Network Map.
 *
 * - Maintain Invariant/Relationships between Topology Objects.
 *
 * TODO To be synchronized based on TopologyEvent Notification.
 *
 * TODO TBD: Caller is expected to maintain parent/child calling order. Parent
 * Object must exist before adding sub component(Add Switch -> Port).
 *
 * TODO TBD: This class may delay the requested change to handle event
 * re-ordering. e.g.) Link Add came in, but Switch was not there.
 *
 */
public class TopologyManager implements NetworkGraphDiscoveryInterface {

    private static final Logger log = LoggerFactory
	    .getLogger(TopologyManager.class);

    private IEventChannel<byte[], TopologyEvent> eventChannel;
    private static final String EVENT_CHANNEL_NAME = "onos.topology";
    private EventHandler eventHandler = new EventHandler();

    private final NetworkGraphDatastore datastore;
    private final NetworkGraphImpl networkGraph = new NetworkGraphImpl();
    private final IControllerRegistryService registryService;
    private CopyOnWriteArrayList<INetworkGraphListener> networkGraphListeners;

    /**
     * Constructor.
     *
     * @param registryService the Registry Service to use.
     * @param networkGraphListeners the collection of Network Graph Listeners
     * to use.
     */
    public TopologyManager(IControllerRegistryService registryService,
			   CopyOnWriteArrayList<INetworkGraphListener> networkGraphListeners) {
	datastore = new NetworkGraphDatastore();
	this.registryService = registryService;
	this.networkGraphListeners = networkGraphListeners;
    }

    /**
     * Get the Network Graph.
     *
     * @return the Network Graph.
     */
    NetworkGraph getNetworkGraph() {
	return networkGraph;
    }

    /**
     * Event handler class.
     */
    private class EventHandler extends Thread implements
	IEventChannelListener<byte[], TopologyEvent> {
	private BlockingQueue<EventEntry<TopologyEvent>> topologyEvents =
	    new LinkedBlockingQueue<EventEntry<TopologyEvent>>();

	/**
	 * Startup processing.
	 */
	private void startup() {
	    //
	    // TODO: Read all state from the database
	    // For now, as a shortcut we read it from the datagrid
	    //
	    Collection<TopologyEvent> topologyEvents =
		eventChannel.getAllEntries();
	    Collection<EventEntry<TopologyEvent>> collection =
		new LinkedList<EventEntry<TopologyEvent>>();

	    for (TopologyEvent topologyEvent : topologyEvents) {
		EventEntry<TopologyEvent> eventEntry =
		    new EventEntry<TopologyEvent>(EventEntry.Type.ENTRY_ADD,
						  topologyEvent);
		collection.add(eventEntry);
	    }
	    processEvents(collection);
	}

	/**
	 * Run the thread.
	 */
	@Override
	public void run() {
	    Collection<EventEntry<TopologyEvent>> collection =
		new LinkedList<EventEntry<TopologyEvent>>();

	    this.setName("TopologyManager.EventHandler " + this.getId());
	    startup();

	    //
	    // The main loop
	    //
	    try {
		while (true) {
		    EventEntry<TopologyEvent> eventEntry = topologyEvents.take();
		    collection.add(eventEntry);
		    topologyEvents.drainTo(collection);

		    processEvents(collection);
		    collection.clear();
		}
	    } catch (Exception exception) {
		log.debug("Exception processing Topology Events: ", exception);
	    }
	}

	/**
	 * Process all topology events.
	 *
	 * @param events the events to process.
	 */
	private void processEvents(Collection<EventEntry<TopologyEvent>> events) {
	    for (EventEntry<TopologyEvent> event : events) {
		TopologyEvent topologyEvent = event.eventData();
		switch (event.eventType()) {
		case ENTRY_ADD:
		    log.debug("Topology event ENTRY_ADD: {}", topologyEvent);
		    if (topologyEvent.switchEvent != null)
			putSwitchReplicationEvent(topologyEvent.switchEvent);
		    if (topologyEvent.portEvent != null)
			putPortReplicationEvent(topologyEvent.portEvent);
		    if (topologyEvent.linkEvent != null)
			putLinkReplicationEvent(topologyEvent.linkEvent);
		    if (topologyEvent.deviceEvent != null)
			putDeviceReplicationEvent(topologyEvent.deviceEvent);
		    break;
		case ENTRY_REMOVE:
		    log.debug("Topology event ENTRY_REMOVE: {}", topologyEvent);
		    if (topologyEvent.switchEvent != null)
			removeSwitchReplicationEvent(topologyEvent.switchEvent);
		    if (topologyEvent.portEvent != null)
			removePortReplicationEvent(topologyEvent.portEvent);
		    if (topologyEvent.linkEvent != null)
			removeLinkReplicationEvent(topologyEvent.linkEvent);
		    if (topologyEvent.deviceEvent != null)
			removeDeviceReplicationEvent(topologyEvent.deviceEvent);
		    break;
		}
	    }
	}

	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param value the value for the entry.
	 */
	@Override
	public void entryAdded(TopologyEvent value) {
	    EventEntry<TopologyEvent> eventEntry =
		new EventEntry<TopologyEvent>(EventEntry.Type.ENTRY_ADD,
					      value);
	    topologyEvents.add(eventEntry);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param value the value for the entry.
	 */
	@Override
	public void entryRemoved(TopologyEvent value) {
	    EventEntry<TopologyEvent> eventEntry =
		new EventEntry<TopologyEvent>(EventEntry.Type.ENTRY_REMOVE,
					      value);
	    topologyEvents.add(eventEntry);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param value the value for the entry.
	 */
	@Override
	public void entryUpdated(TopologyEvent value) {
	    // NOTE: The ADD and UPDATE events are processed in same way
	    entryAdded(value);
	}
    }

    /**
     * Startup processing.
     *
     * @param datagridService the datagrid service to use.
     */
    void startup(IDatagridService datagridService) {
	eventChannel = datagridService.addListener(EVENT_CHANNEL_NAME,
						   eventHandler,
						   byte[].class,
						   TopologyEvent.class);
	eventHandler.start();
    }

    /**
     * Exception to be thrown when Modification to the Network Graph cannot be
     * continued due to broken invariant.
     *
     * XXX Should this be checked exception or RuntimeException?
     */
    public static class BrokenInvariantException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BrokenInvariantException() {
	    super();
	}

	public BrokenInvariantException(String message) {
	    super(message);
	}
    }

    /* ******************************
     * NetworkGraphDiscoveryInterface methods
     * ******************************/

    @Override
    public void putSwitchDiscoveryEvent(SwitchEvent switchEvent) {
	if (datastore.addSwitch(switchEvent)) {
	    // Send out notification
	    TopologyEvent topologyEvent = new TopologyEvent(switchEvent);
	    eventChannel.addEntry(topologyEvent.getID(), topologyEvent);
	}
    }

    @Override
    public void removeSwitchDiscoveryEvent(SwitchEvent switchEvent) {
	if (datastore.deactivateSwitch(switchEvent)) {
	    // Send out notification
	    eventChannel.removeEntry(switchEvent.getID());
	}
    }

    @Override
    public void putPortDiscoveryEvent(PortEvent portEvent) {
	if (datastore.addPort(portEvent)) {
	    // Send out notification
	    TopologyEvent topologyEvent = new TopologyEvent(portEvent);
	}
    }

    @Override
    public void removePortDiscoveryEvent(PortEvent portEvent) {
	if (datastore.deactivatePort(portEvent)) {
	    // Send out notification
	    eventChannel.removeEntry(portEvent.getID());
	}
    }

    @Override
    public void putLinkDiscoveryEvent(LinkEvent linkEvent) {
	if (datastore.addLink(linkEvent)) {
	    // Send out notification
	    TopologyEvent topologyEvent = new TopologyEvent(linkEvent);
	    eventChannel.addEntry(topologyEvent.getID(), topologyEvent);
	}
    }

    @Override
    public void removeLinkDiscoveryEvent(LinkEvent linkEvent) {
	if (datastore.removeLink(linkEvent)) {
	    // Send out notification
	    eventChannel.removeEntry(linkEvent.getID());
	}
    }

    @Override
    public void putDeviceDiscoveryEvent(DeviceEvent deviceEvent) {
	if (datastore.addDevice(deviceEvent)) {
	    // Send out notification
	    TopologyEvent topologyEvent = new TopologyEvent(deviceEvent);
	    eventChannel.addEntry(topologyEvent.getID(), topologyEvent);
	}
    }

    @Override
    public void removeDeviceDiscoveryEvent(DeviceEvent deviceEvent) {
	if (datastore.removeDevice(deviceEvent)) {
	    // Send out notification
	    eventChannel.removeEntry(deviceEvent.getID());
	}
    }

    /* ************************************************
     * Internal methods for processing the replication events
     * ************************************************/

    private void putSwitchReplicationEvent(SwitchEvent switchEvent) {
	if (prepareForAddSwitchEvent(switchEvent)) {
	    putSwitch(switchEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchPutSwitchEvent(switchEvent);
    }

    private void removeSwitchReplicationEvent(SwitchEvent switchEvent) {
	if (prepareForRemoveSwitchEvent(switchEvent)) {
	    removeSwitch(switchEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchRemoveSwitchEvent(switchEvent);
    }

    private void putPortReplicationEvent(PortEvent portEvent) {
	if (prepareForAddPortEvent(portEvent)) {
	    putPort(portEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchPutPortEvent(portEvent);
    }

    private void removePortReplicationEvent(PortEvent portEvent) {
	if (prepareForRemovePortEvent(portEvent)) {
	    removePort(portEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchRemovePortEvent(portEvent);
    }

    private void putLinkReplicationEvent(LinkEvent linkEvent) {
	if (prepareForAddLinkEvent(linkEvent)) {
	    putLink(linkEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchPutLinkEvent(linkEvent);
    }

    private void removeLinkReplicationEvent(LinkEvent linkEvent) {
	if (prepareForRemoveLinkEvent(linkEvent)) {
	    removeLink(linkEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchRemoveLinkEvent(linkEvent);
    }

    private void putDeviceReplicationEvent(DeviceEvent deviceEvent) {
	if (prepareForAddDeviceEvent(deviceEvent)) {
	    putDevice(deviceEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchPutDeviceEvent(deviceEvent);
    }

    private void removeDeviceReplicationEvent(DeviceEvent deviceEvent) {
	if (prepareForRemoveDeviceEvent(deviceEvent)) {
	    removeDevice(deviceEvent);
	}
	// TODO handle invariant violation
	// trigger instance local topology event handler
	dispatchRemoveDeviceEvent(deviceEvent);
    }

    /* *****************
     * Internal methods to maintain invariants of the network graph
     * *****************/

    /**
     *
     * @param swEvent
     * @return true if ready to accept event.
     */
    private boolean prepareForAddSwitchEvent(SwitchEvent swEvent) {
	// No show stopping precondition
	// Prep: remove(deactivate) Ports on Switch, which is not on event
	removePortsNotOnEvent(swEvent);
	return true;
    }

    private boolean prepareForRemoveSwitchEvent(SwitchEvent swEvent) {
	// No show stopping precondition
	// Prep: remove(deactivate) Ports on Switch, which is not on event
	// XXX may be remove switch should imply wipe all ports
	removePortsNotOnEvent(swEvent);
	return true;
    }

    private void removePortsNotOnEvent(SwitchEvent swEvent) {
	Switch sw = networkGraph.getSwitch(swEvent.getDpid());
	if (sw != null) {
	    Set<Long> port_noOnEvent = new HashSet<>();
	    for (PortEvent portEvent : swEvent.getPorts()) {
		port_noOnEvent.add(portEvent.getNumber());
	    }
	    // Existing ports not on event should be removed.
	    // TODO Should batch eventually for performance?
	    List<Port> portsToRemove = new ArrayList<Port>();
	    for (Port p : sw.getPorts()) {
		if (!port_noOnEvent.contains(p.getNumber())) {
		    //PortEvent rmEvent = new PortEvent(p.getSwitch().getDpid(), p.getNumber());
		    // calling Discovery removePort() API to wipe from DB, etc.
		    //removePortDiscoveryEvent(rmEvent);

		    // We can't remove ports here because this will trigger a remove
		    // from the switch's port list, which we are currently iterating
		    // over.
		    portsToRemove.add(p);
		}
	    }
	    for (Port p : portsToRemove) {
		PortEvent rmEvent = new PortEvent(p.getSwitch().getDpid(),
						  p.getNumber());
		// calling Discovery removePort() API to wipe from DB, etc.
		removePortDiscoveryEvent(rmEvent);
	    }
	}
    }

    private boolean prepareForAddPortEvent(PortEvent portEvent) {
	// Parent Switch must exist
	if (networkGraph.getSwitch(portEvent.getDpid()) == null) {
	    log.warn("Dropping add port event because switch doesn't exist: {}",
		     portEvent);
	    return false;
	}
	// Prep: None
	return true;
    }

    private boolean prepareForRemovePortEvent(PortEvent portEvent) {
	Port port = networkGraph.getPort(portEvent.getDpid(),
					 portEvent.getNumber());
	if (port == null) {
	    log.debug("Port already removed? {}", portEvent);
	    // let it pass
	    return true;
	}

	// Prep: Remove Link and Device Attachment
	ArrayList<DeviceEvent> deviceEvents = new ArrayList<>();
	for (Device device : port.getDevices()) {
	    log.debug("Removing Device {} on Port {}", device, portEvent);
	    DeviceEvent devEvent = new DeviceEvent(device.getMacAddress());
	    devEvent.addAttachmentPoint(new SwitchPort(port.getSwitch().getDpid(),
						     port.getNumber()));
	    deviceEvents.add(devEvent);
	}
	for (DeviceEvent devEvent : deviceEvents) {
	    // calling Discovery API to wipe from DB, etc.
	    removeDeviceDiscoveryEvent(devEvent);
	}

	Set<Link> links = new HashSet<>();
	links.add(port.getOutgoingLink());
	links.add(port.getIncomingLink());
	for (Link link : links) {
	    if (link == null) {
		continue;
	    }
	    log.debug("Removing Link {} on Port {}", link, portEvent);
	    LinkEvent linkEvent =
		new LinkEvent(link.getSrcSwitch().getDpid(),
			      link.getSrcPort().getNumber(),
			      link.getDstSwitch().getDpid(),
			      link.getDstPort().getNumber());
	    // calling Discovery API to wipe from DB, etc.

	    // Call internal remove Link, which will check
	    // ownership of DST dpid and modify DB only if it is the owner
	    removeLinkDiscoveryEvent(linkEvent, true);
	}
	return true;
    }

    private boolean prepareForAddLinkEvent(LinkEvent linkEvent) {
	// Src/Dst Port must exist
	Port srcPort = networkGraph.getPort(linkEvent.getSrc().dpid,
					    linkEvent.getSrc().number);
	Port dstPort = networkGraph.getPort(linkEvent.getDst().dpid,
					    linkEvent.getDst().number);
	if (srcPort == null || dstPort == null) {
	    log.warn("Dropping add link event because port doesn't exist: {}",
		     linkEvent);
	    return false;
	}

	// Prep: remove Device attachment on both Ports
	ArrayList<DeviceEvent> deviceEvents = new ArrayList<>();
	for (Device device : srcPort.getDevices()) {
	    DeviceEvent devEvent = new DeviceEvent(device.getMacAddress());
	    devEvent.addAttachmentPoint(new SwitchPort(srcPort.getSwitch().getDpid(), srcPort.getNumber()));
	    deviceEvents.add(devEvent);
	}
	for (Device device : dstPort.getDevices()) {
	    DeviceEvent devEvent = new DeviceEvent(device.getMacAddress());
	    devEvent.addAttachmentPoint(new SwitchPort(dstPort.getSwitch().getDpid(),
						     dstPort.getNumber()));
	    deviceEvents.add(devEvent);
	}
	for (DeviceEvent devEvent : deviceEvents) {
	    // calling Discovery API to wipe from DB, etc.
	    removeDeviceDiscoveryEvent(devEvent);
	}

	return true;
    }

    private boolean prepareForRemoveLinkEvent(LinkEvent linkEvent) {
	// Src/Dst Port must exist
	Port srcPort = networkGraph.getPort(linkEvent.getSrc().dpid,
					    linkEvent.getSrc().number);
	Port dstPort = networkGraph.getPort(linkEvent.getDst().dpid,
					    linkEvent.getDst().number);
	if (srcPort == null || dstPort == null) {
	    log.warn("Dropping remove link event because port doesn't exist {}", linkEvent);
	    return false;
	}

	Link link = srcPort.getOutgoingLink();

	// Link is already gone, or different Link exist in memory
	// XXX Check if we should reject or just accept these cases.
	// it should be harmless to remove the Link on event from DB anyways
	if (link == null ||
	    !link.getDstPort().getNumber().equals(linkEvent.getDst().number)
	    || !link.getDstSwitch().getDpid().equals(linkEvent.getDst().dpid)) {
	    log.warn("Dropping remove link event because link doesn't exist: {}", linkEvent);
	    return false;
	}
	// Prep: None
	return true;
    }

    /**
     *
     * @param deviceEvent Event will be modified to remove inapplicable attachemntPoints/ipAddress
     * @return false if this event should be dropped.
     */
    private boolean prepareForAddDeviceEvent(DeviceEvent deviceEvent) {
	boolean preconditionBroken = false;
	ArrayList<PortEvent.SwitchPort> failedSwitchPort = new ArrayList<>();
	for ( PortEvent.SwitchPort swp : deviceEvent.getAttachmentPoints() ) {
	    // Attached Ports must exist
	    Port port = networkGraph.getPort(swp.dpid, swp.number);
	    if (port == null) {
		preconditionBroken = true;
		failedSwitchPort.add(swp);
		continue;
	    }
	    // Attached Ports must not have Link
	    if (port.getOutgoingLink() != null ||
		port.getIncomingLink() != null) {
		preconditionBroken = true;
		failedSwitchPort.add(swp);
		continue;
	    }
	}

	// Rewriting event to exclude failed attachmentPoint
	// XXX Assumption behind this is that inapplicable device event should
	// be dropped, not deferred. If we decide to defer Device event,
	// rewriting can become a problem
	List<SwitchPort>  attachmentPoints = deviceEvent.getAttachmentPoints();
	attachmentPoints.removeAll(failedSwitchPort);
	deviceEvent.setAttachmentPoints(attachmentPoints);

	if (deviceEvent.getAttachmentPoints().isEmpty() &&
	    deviceEvent.getIpAddresses().isEmpty()) {
	    // return false to represent: Nothing left to do for this event.
	    // Caller should drop event
	    return false;
	}

	// Should we return false to tell caller that the event was trimmed?
	// if ( preconditionBroken ) {
	//     return false;
	// }

	return true;
    }

    private boolean prepareForRemoveDeviceEvent(DeviceEvent deviceEvent) {
	// No show stopping precondition?
	// Prep: none
	return true;
    }

    /* ************************************************
     * Internal In-memory object mutation methods.
     * ************************************************/

    void putSwitch(SwitchEvent swEvent) {
	if (swEvent == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}

	Switch sw = networkGraph.getSwitch(swEvent.getDpid());

	if (sw == null) {
	    sw = new SwitchImpl(networkGraph, swEvent.getDpid());
	    networkGraph.putSwitch(sw);
	}

	// Update when more attributes are added to Event object
	// no attribute to update for now

	// TODO handle child Port event properly for performance
	for (PortEvent portEvent : swEvent.getPorts() ) {
	    putPort(portEvent);
	}
    }

    void removeSwitch(SwitchEvent swEvent) {
	if (swEvent == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}

	// TODO handle child Port event properly for performance
	for (PortEvent portEvent : swEvent.getPorts() ) {
	    removePort(portEvent);
	}

	Switch sw = networkGraph.getSwitch(swEvent.getDpid());
	if (sw == null) {
	    log.warn("Switch {} already removed, ignoring", swEvent);
	    return;
	}

	// remove all ports if there still exist
	ArrayList<PortEvent> portsToRemove = new ArrayList<>();
	for (Port port : sw.getPorts()) {
	    log.warn("Port {} on Switch {} should be removed prior to removing Switch. Removing Port now",
		     port, swEvent);
	    PortEvent portEvent = new PortEvent(port.getDpid(),
						port.getNumber());
	    portsToRemove.add(portEvent);
	}
	for (PortEvent portEvent : portsToRemove) {
	    // XXX calling removePortDiscoveryEvent() may trigger duplicate
	    // event, once at prepare phase, second time here
	    // If event can be squashed, ignored etc. at receiver side it
	    // shouldn't be a problem, but if not need to re-visit this issue.

	    // Note: removePortDiscoveryEvent() implies removal of attached
	    // Device, etc. if we decide not to call
	    // removePortDiscoveryEvent(), Device needs to be handled properly.
	    removePortDiscoveryEvent(portEvent);
	}

	networkGraph.removeSwitch(swEvent.getDpid());
    }

    void putPort(PortEvent portEvent) {
	if (portEvent == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}
	Switch sw = networkGraph.getSwitch(portEvent.getDpid());
	if (sw == null) {
	    throw new BrokenInvariantException(String.format(
			"Switch with dpid %s did not exist.",
			new Dpid(portEvent.getDpid())));
	}
	Port p = sw.getPort(portEvent.getNumber());
	PortImpl port = null;
	if (p != null) {
	    port = getPortImpl(p);
	}

	if (port == null) {
	    port = new PortImpl(networkGraph, sw, portEvent.getNumber());
	}

	// TODO update attributes

	SwitchImpl s = getSwitchImpl(sw);
	s.addPort(port);
    }

    void removePort(PortEvent portEvent) {
	if (portEvent == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}

	Switch sw = networkGraph.getSwitch(portEvent.getDpid());
	if (sw == null) {
	    log.warn("Parent Switch for Port {} already removed, ignoring",
		     portEvent);
	    return;
	}

	Port p = sw.getPort(portEvent.getNumber());
	if (p == null) {
	    log.warn("Port {} already removed, ignoring", portEvent);
	    return;
	}

	// Remove Link and Device Attachment
	for (Device device : p.getDevices()) {
	    log.debug("Removing Device {} on Port {}", device, portEvent);
	    DeviceEvent devEvent = new DeviceEvent(device.getMacAddress());
	    devEvent.addAttachmentPoint(new SwitchPort(p.getSwitch().getDpid(),
						       p.getNumber()));

	    // XXX calling removeDeviceDiscoveryEvent() may trigger duplicate
	    // event, once at prepare phase, second time here.
	    // If event can be squashed, ignored etc. at receiver side it
	    // shouldn't be a problem, but if not need to re-visit

	    // calling Discovery API to wipe from DB, etc.
	    removeDeviceDiscoveryEvent(devEvent);
	}
	Set<Link> links = new HashSet<>();
	links.add(p.getOutgoingLink());
	links.add(p.getIncomingLink());
	ArrayList<LinkEvent> linksToRemove = new ArrayList<>();
	for (Link link : links) {
	    if (link == null) {
		continue;
	    }
	    log.debug("Removing Link {} on Port {}", link, portEvent);
	    LinkEvent linkEvent = new LinkEvent(link.getSrcSwitch().getDpid(),
						link.getSrcPort().getNumber(),
						link.getDstSwitch().getDpid(),
						link.getDstPort().getNumber());
	    linksToRemove.add(linkEvent);
	}
	for (LinkEvent linkEvent : linksToRemove) {
	    // XXX calling removeLinkDiscoveryEvent() may trigger duplicate
	    // event, once at prepare phase, second time here.
	    // If event can be squashed, ignored etc. at receiver side it
	    // shouldn't be a problem, but if not need to re-visit

	    // calling Discovery API to wipe from DB, etc.
	    removeLinkDiscoveryEvent(linkEvent);
	}

	// remove Port from Switch
	SwitchImpl s = getSwitchImpl(sw);
	s.removePort(p);
    }

    void putLink(LinkEvent linkEvent) {
	if (linkEvent == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}

	Port srcPort = networkGraph.getPort(linkEvent.getSrc().dpid,
					    linkEvent.getSrc().number);
	if (srcPort == null) {
	    throw new BrokenInvariantException(
			String.format(
				"Src Port %s of a Link did not exist.",
				linkEvent.getSrc() ));
	}

	Port dstPort = networkGraph.getPort(linkEvent.getDst().dpid,
					    linkEvent.getDst().number);
	if (dstPort == null) {
	    throw new BrokenInvariantException(
			String.format(
				"Dst Port %s of a Link did not exist.",
				linkEvent.getDst()));
	}

	// getting Link instance from destination port incoming Link
	Link l = dstPort.getIncomingLink();
	LinkImpl link = null;
	assert(l == srcPort.getOutgoingLink());
	if (l != null) {
	    link = getLinkImpl(l);
	}

	if (link == null) {
	    link = new LinkImpl(networkGraph, srcPort, dstPort);
	}

	PortImpl dstPortMem = getPortImpl(dstPort);
	PortImpl srcPortMem = getPortImpl(srcPort);

	// Add Link first to avoid further Device addition

	// add Link to Port
	dstPortMem.setIncomingLink(link);
	srcPortMem.setOutgoingLink(link);

	// remove Device Pointing to Port if any
	for (Device d : dstPortMem.getDevices() ) {
	    log.error("Device {} on Port {} should have been removed prior to adding Link {}",
		      d, dstPort, linkEvent);
	    DeviceImpl dev = getDeviceImpl(d);
	    dev.removeAttachmentPoint(dstPort);
	    // This implies that change is made to Device Object.
	    // sending Device attachment point removed event
	    DeviceEvent rmEvent = new DeviceEvent(d.getMacAddress());
	    rmEvent.addAttachmentPoint(new SwitchPort(dstPort.getDpid(),
						      dstPort.getNumber()));
	    removeDeviceDiscoveryEvent(rmEvent);
	}
	dstPortMem.removeAllDevice();
	for (Device d : srcPortMem.getDevices() ) {
	    log.error("Device {} on Port {} should have been removed prior to adding Link {}",
		      d, srcPort, linkEvent);
	    DeviceImpl dev = getDeviceImpl(d);
	    dev.removeAttachmentPoint(srcPort);
	    // This implies that change is made to Device Object.
	    // sending Device attachment point removed event
	    DeviceEvent rmEvent = new DeviceEvent(d.getMacAddress());
	    rmEvent.addAttachmentPoint(new SwitchPort(dstPort.getDpid(),
						      dstPort.getNumber()));
	    removeDeviceDiscoveryEvent(rmEvent);
	}
	srcPortMem.removeAllDevice();
    }

    void removeLink(LinkEvent linkEvent) {
	if (linkEvent == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}

	Port srcPort = networkGraph.getPort(linkEvent.getSrc().dpid,
					    linkEvent.getSrc().number);
	if (srcPort == null) {
	    log.warn("Src Port for Link {} already removed, ignoring",
		     linkEvent);
	    return;
	}

	Port dstPort = networkGraph.getPort(linkEvent.getDst().dpid,
					    linkEvent.getDst().number);
	if (dstPort == null) {
	    log.warn("Dst Port for Link {} already removed, ignoring",
		     linkEvent);
	    return;
	}

	Link l = dstPort.getIncomingLink();
	if ( l == null) {
	    log.warn("Link {} already removed on destination Port", linkEvent);
	}
	l = srcPort.getOutgoingLink();
	if (l == null) {
	    log.warn("Link {} already removed on src Port", linkEvent);
	}

	getPortImpl(dstPort).setIncomingLink(null);
	getPortImpl(srcPort).setOutgoingLink(null);
    }

    // XXX Need to rework Device related
    void putDevice(DeviceEvent deviceEvent) {
	if (deviceEvent == null) {
	    throw new IllegalArgumentException("Device cannot be null");
	}

	Device device = networkGraph.getDeviceByMac(deviceEvent.getMac());
	if (device == null) {
	    device = new DeviceImpl(networkGraph, deviceEvent.getMac());
	}
	DeviceImpl memDevice = getDeviceImpl(device);

	// for each IP address
	for (InetAddress ipAddr : deviceEvent.getIpAddresses()) {
	    memDevice.addIpAddress(ipAddr);
	}

	networkGraph.putDevice(device);

	// for each attachment point
	for (SwitchPort swp : deviceEvent.getAttachmentPoints()) {
	    // Attached Ports must exist
	    Port port = networkGraph.getPort(swp.dpid, swp.number);
	    if (port == null) {
		log.warn("Port for the attachment point {} did not exist. skipping mutation", swp);
		continue;
	    }
	    // Attached Ports must not have Link
	    if (port.getOutgoingLink() != null ||
		port.getIncomingLink() != null) {
		log.warn("Link (Out:{},In:{}) exist on the attachment point, skipping mutation.",
			 port.getOutgoingLink(),
			 port.getIncomingLink());
		continue;
	    }

	    // finally add Device <-> Port on In-memory structure
	    PortImpl memPort = getPortImpl(port);
	    memPort.addDevice(device);
	    memDevice.addAttachmentPoint(port);
	}
    }

    void removeDevice(DeviceEvent deviceEvent) {
	if (deviceEvent == null) {
	    throw new IllegalArgumentException("Device cannot be null");
	}

	Device device = networkGraph.getDeviceByMac(deviceEvent.getMac());
	if (device == null) {
	    log.warn("Device {} already removed, ignoring", deviceEvent);
	    return;
	}
	DeviceImpl memDevice = getDeviceImpl(device);

	// for each attachment point
	for (SwitchPort swp : deviceEvent.getAttachmentPoints()) {
	    // Attached Ports must exist
	    Port port = networkGraph.getPort(swp.dpid, swp.number);
	    if (port == null) {
		log.warn("Port for the attachment point {} did not exist. skipping attachment point mutation", swp);
		continue;
	    }

	    // finally remove Device <-> Port on In-memory structure
	    PortImpl memPort = getPortImpl(port);
	    memPort.removeDevice(device);
	    memDevice.removeAttachmentPoint(port);
	}
	networkGraph.removeDevice(device);
    }

    private void dispatchPutSwitchEvent(SwitchEvent switchEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.putSwitchEvent(switchEvent);
	}
    }

    private void dispatchRemoveSwitchEvent(SwitchEvent switchEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.removeSwitchEvent(switchEvent);
	}
    }

    private void dispatchPutPortEvent(PortEvent portEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.putPortEvent(portEvent);
	}
    }

    private void dispatchRemovePortEvent(PortEvent portEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.removePortEvent(portEvent);
	}
    }

    private void dispatchPutLinkEvent(LinkEvent linkEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.putLinkEvent(linkEvent);
	}
    }

    private void dispatchRemoveLinkEvent(LinkEvent linkEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.removeLinkEvent(linkEvent);
	}
    }

    private void dispatchPutDeviceEvent(DeviceEvent deviceEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.putDeviceEvent(deviceEvent);;
	}
    }

    private void dispatchRemoveDeviceEvent(DeviceEvent deviceEvent) {
	for (INetworkGraphListener listener : this.networkGraphListeners) {
	    // TODO Should copy before handing them over to listener
	    listener.removeDeviceEvent(deviceEvent);
	}
    }

    private SwitchImpl getSwitchImpl(Switch sw) {
	if (sw instanceof SwitchImpl) {
	    return (SwitchImpl) sw;
	}
	throw new ClassCastException("SwitchImpl expected, but found: " + sw);
    }

    private PortImpl getPortImpl(Port p) {
	if (p instanceof PortImpl) {
	    return (PortImpl) p;
	}
	throw new ClassCastException("PortImpl expected, but found: " + p);
    }

    private LinkImpl getLinkImpl(Link l) {
	if (l instanceof LinkImpl) {
	    return (LinkImpl) l;
	}
	throw new ClassCastException("LinkImpl expected, but found: " + l);
    }

    private DeviceImpl getDeviceImpl(Device d) {
	if (d instanceof DeviceImpl) {
	    return (DeviceImpl) d;
	}
	throw new ClassCastException("DeviceImpl expected, but found: " + d);
    }

    @Deprecated
    public void loadWholeTopologyFromDB() {
	// XXX May need to clear whole topology first, depending on
	// how we initially subscribe to replication events

	for (RCSwitch sw : RCSwitch.getAllSwitches()) {
	    if (sw.getStatus() != RCSwitch.STATUS.ACTIVE) {
		continue;
	    }
	    putSwitchReplicationEvent(new SwitchEvent(sw.getDpid()));
	}

	for (RCPort p : RCPort.getAllPorts()) {
	    if (p.getStatus() != RCPort.STATUS.ACTIVE) {
		continue;
	    }
	    putPortReplicationEvent(new PortEvent(p.getDpid(), p.getNumber()));
	}

	// TODO Is Device going to be in DB? If so, read from DB.
	//	for (RCDevice d : RCDevice.getAllDevices()) {
	//	    DeviceEvent devEvent = new DeviceEvent( MACAddress.valueOf(d.getMac()) );
	//	    for (byte[] portId : d.getAllPortIds() ) {
	//		devEvent.addAttachmentPoint( new SwitchPort( RCPort.getDpidFromKey(portId), RCPort.getNumberFromKey(portId) ));
	//	    }
	//	}

	for (RCLink l : RCLink.getAllLinks()) {
	    // check if src/dst switch/port exist before triggering event
	    Port srcPort = networkGraph.getPort(l.getSrc().dpid,
						l.getSrc().number);
	    Port dstPort = networkGraph.getPort(l.getDst().dpid,
						l.getDst().number);
	    if (srcPort == null || dstPort == null) {
		continue;
	    }
	    putLinkReplicationEvent(new LinkEvent(l.getSrc().dpid,
						  l.getSrc().number,
						  l.getDst().dpid,
						  l.getDst().number));
	}
    }

    @Deprecated
    private void removeLinkDiscoveryEvent(LinkEvent linkEvent,
					  boolean dstCheckBeforeDBmodify) {
	if (prepareForRemoveLinkEvent(linkEvent)) {
	    if (dstCheckBeforeDBmodify) {
		// write to DB only if it is owner of the dst dpid
	    // XXX this will cause link remove events to be dropped
		// if the dst switch just disconnected
		if (registryService.hasControl(linkEvent.getDst().dpid)) {
		    datastore.removeLink(linkEvent);
		}
	    } else {
		datastore.removeLink(linkEvent);
	    }
	    removeLink(linkEvent);
	    // Send out notification
	    eventChannel.removeEntry(linkEvent.getID());
	}
	// TODO handle invariant violation
    }
}
