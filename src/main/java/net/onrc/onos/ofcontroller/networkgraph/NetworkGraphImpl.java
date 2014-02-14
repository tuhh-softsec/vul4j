package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.onrc.onos.datastore.topology.RCLink;
import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;
import net.onrc.onos.ofcontroller.util.Dpid;

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
public class NetworkGraphImpl extends AbstractNetworkGraph implements
	NetworkGraphDiscoveryInterface, NetworkGraphReplicationInterface {

    private static final Logger log = LoggerFactory
	    .getLogger(NetworkGraphImpl.class);

    private final NetworkGraphDatastore datastore;

    public NetworkGraphImpl() {
	super();
	datastore = new NetworkGraphDatastore(this);
    }

    /**
     * Exception to be thrown when Modification to the Network Graph cannot be continued due to broken invariant.
     *
     * XXX Should this be checked exception or RuntimeException
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
	public void putSwitchEvent(SwitchEvent switchEvent) {
		if (prepareForAddSwitchEvent(switchEvent)) {
			datastore.addSwitch(switchEvent);
			putSwitch(switchEvent);
			// TODO send out notification
		}
		// TODO handle invariant violation
	}

	@Override
	public void removeSwitchEvent(SwitchEvent switchEvent) {
		if (prepareForRemoveSwitchEvent(switchEvent)) {
			datastore.deactivateSwitch(switchEvent);
			removeSwitch(switchEvent);
			// TODO send out notification
		}
		// TODO handle invariant violation
	}

	@Override
	public void putPortEvent(PortEvent portEvent) {
		if (prepareForAddPortEvent(portEvent)) {
			datastore.addPort(portEvent);
			putPort(portEvent);
			// TODO send out notification
		}
	}

	@Override
	public void removePortEvent(PortEvent portEvent) {
		if (prepareForRemovePortEvent(portEvent)) {
			datastore.deactivatePort(portEvent);
			removePort(portEvent);
			// TODO send out notification
		}
	}

	@Override
	public void putLinkEvent(LinkEvent linkEvent) {
		if (prepareForAddLinkEvent(linkEvent)) {
			datastore.addLink(linkEvent);
			putLink(linkEvent);
			// TODO send out notification
		}
		// TODO handle invariant violation
	}

	@Override
	public void removeLinkEvent(LinkEvent linkEvent) {
		if (prepareForRemoveLinkEvent(linkEvent)) {
			datastore.removeLink(linkEvent);
			removeLink(linkEvent);
			// TODO send out notification
		}
		// TODO handle invariant violation
	}

	@Override
	public void putDeviceEvent(DeviceEvent device) {
	    // XXX if prepareFor~ method returned false, event should be dropped
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDeviceEvent(DeviceEvent deviceEvent) {
		// TODO Auto-generated method stub

	}

	/* *****************
	 * Internal methods to maintain invariants of the network graph
	 * *****************/

	/**
	 *
	 * @param swEvt
	 * @return true if ready to accept event.
	 */
	private boolean prepareForAddSwitchEvent(SwitchEvent swEvt) {
	    // No show stopping precondition
	    // Prep: remove(deactivate) Ports on Switch, which is not on event
	    removePortsNotOnEvent(swEvt);
	    return true;
	}

	private boolean prepareForRemoveSwitchEvent(SwitchEvent swEvt) {
	    // No show stopping precondition
	    // Prep: remove(deactivate) Ports on Switch, which is not on event
	    // XXX may be remove switch should imply wipe all ports
	    removePortsNotOnEvent(swEvt);
	    return true;
	}

	private void removePortsNotOnEvent(SwitchEvent swEvt) {
	    Switch sw = switches.get( swEvt.getDpid() );
	    if ( sw != null ) {
		Set<Long> port_noOnEvent = new HashSet<>();
		for( PortEvent portEvent : swEvt.getPorts()) {
		    port_noOnEvent.add(portEvent.getNumber());
		}
		// Existing ports not on event should be removed.
		// TODO Should batch eventually for performance?
		for( Port p : sw.getPorts() ) {
		    if ( !port_noOnEvent.contains(p.getNumber()) ) {
			PortEvent rmEvent = new PortEvent(p.getSwitch().getDpid(), p.getNumber());
			// calling Discovery removePort() API to wipe from DB, etc.
			removePortEvent(rmEvent);
		    }
		}
	    }
	}

	private boolean prepareForAddPortEvent(PortEvent portEvt) {
		// Parent Switch must exist
		if ( getSwitch(portEvt.getDpid()) == null) {
		    return false;
		}
		// Prep: None
		return true;
	}

	private boolean prepareForRemovePortEvent(PortEvent portEvt) {
		// Parent Switch must exist
		Switch sw = getSwitch(portEvt.getDpid());
		if ( sw ==  null ) {
		    return false;
		}
		Port port = sw.getPort(portEvt.getNumber());
		if ( port == null ) {
		    log.debug("Port already removed? {}", portEvt);
		    // let it pass
		    return true;
		}

		// Prep: Remove Link and Device Attachment
		for (Device device : port.getDevices()) {
		    DeviceEvent devEvt = new DeviceEvent(device.getMacAddress());
		    devEvt.addAttachmentPoint(new SwitchPort(port.getSwitch().getDpid(), port.getNumber()));
		    // calling Discovery API to wipe from DB, etc.
		    removeDeviceEvent(devEvt);
		}
		Set<Link> links = new HashSet<>();
		links.add(port.getOutgoingLink());
		links.add(port.getIncomingLink());
		for ( Link link : links) {
		    if (link == null ) {
			continue;
		    }
		    LinkEvent linkEvent = new LinkEvent(link.getSourceSwitchDpid(), link.getSourcePortNumber(), link.getDestinationSwitchDpid(), link.getDestinationPortNumber());
		    // calling Discovery API to wipe from DB, etc.
		    removeLinkEvent(linkEvent);
		}
		return true;
	}

	private boolean prepareForAddLinkEvent(LinkEvent linkEvt) {
	    // Src/Dst Switch must exist
	    Switch srcSw = getSwitch(linkEvt.getSrc().dpid);
	    Switch dstSw = getSwitch(linkEvt.getDst().dpid);
	    if ( srcSw == null || dstSw == null ) {
		return false;
	    }
	    // Src/Dst Port must exist
	    Port srcPort = srcSw.getPort(linkEvt.getSrc().number);
	    Port dstPort = dstSw.getPort(linkEvt.getDst().number);
	    if ( srcPort == null || dstPort == null ) {
		return false;
	    }

	    // Prep: remove Device attachment on both Ports
	    for (Device device : srcPort.getDevices()) {
		DeviceEvent devEvt = new DeviceEvent(device.getMacAddress());
		devEvt.addAttachmentPoint(new SwitchPort(srcPort.getSwitch().getDpid(), srcPort.getNumber()));
		// calling Discovery API to wipe from DB, etc.
		removeDeviceEvent(devEvt);
	    }
	    for (Device device : dstPort.getDevices()) {
		DeviceEvent devEvt = new DeviceEvent(device.getMacAddress());
		devEvt.addAttachmentPoint(new SwitchPort(dstPort.getSwitch().getDpid(), dstPort.getNumber()));
		// calling Discovery API to wipe from DB, etc.
		removeDeviceEvent(devEvt);
	    }

	    return true;
	}

	private boolean prepareForRemoveLinkEvent(LinkEvent linkEvt) {
	    // Src/Dst Switch must exist
	    Switch srcSw = getSwitch(linkEvt.getSrc().dpid);
	    Switch dstSw = getSwitch(linkEvt.getDst().dpid);
	    if ( srcSw == null || dstSw == null ) {
	    log.warn("Rejecting addLink {} because switch doesn't exist", linkEvt);
		return false;
	    }
	    // Src/Dst Port must exist
	    Port srcPort = srcSw.getPort(linkEvt.getSrc().number);
	    Port dstPort = srcSw.getPort(linkEvt.getDst().number);
	    if ( srcPort == null || dstPort == null ) {
	    log.warn("Rejecting addLink {} because port doesn't exist", linkEvt);
		return false;
	    }
	    
	    Link link = srcPort.getOutgoingLink();
	    
	    if (link == null || 
	    		!link.getDestinationPortNumber().equals(linkEvt.getDst().number)
	    		|| !link.getDestinationSwitchDpid().equals(linkEvt.getDst().dpid)) {
	    log.warn("Rejecting removeLink {} because link doesn't exist", linkEvt);
	  	return false;
	    }

	    // Prep: None
	    return true;
	}

	/**
	 *
	 * @param deviceEvt Event will be modified to remove inapplicable attachemntPoints/ipAddress
	 * @return false if this event should be dropped.
	 */
	private boolean prepareForAddDeviceEvent(DeviceEvent deviceEvt) {
	    boolean preconditionBroken = false;
	    ArrayList<PortEvent.SwitchPort> failedSwitchPort = new ArrayList<>();
	    for ( PortEvent.SwitchPort swp : deviceEvt.getAttachmentPoints() ) {
		// Attached Ports' Parent Switch must exist
		Switch sw = getSwitch(swp.dpid);
		if ( sw ==  null ) {
		    preconditionBroken = true;
		    failedSwitchPort.add(swp);
		    continue;
		}
		// Attached Ports must exist
		Port port = sw.getPort(swp.number);
		if ( port == null ) {
		    preconditionBroken = true;
		    failedSwitchPort.add(swp);
		    continue;
		}
		// Attached Ports must not have Link
		if ( port.getOutgoingLink() != null || port.getIncomingLink() != null ) {
		    preconditionBroken = true;
		    failedSwitchPort.add(swp);
		    continue;
		}
	    }

	    // Rewriting event to exclude failed attachmentPoint
	    // XXX Assumption behind this is that inapplicable device event should
	    // be dropped, not deferred. If we decide to defer Device event,
	    // rewriting can become a problem
	    List<SwitchPort>  attachmentPoints = deviceEvt.getAttachmentPoints();
	    attachmentPoints.removeAll(failedSwitchPort);
	    deviceEvt.setAttachmentPoints(attachmentPoints);

	    if ( deviceEvt.getAttachmentPoints().isEmpty() && deviceEvt.getIpAddresses().isEmpty() ) {
		// XXX return false to represent: Nothing left to do for this event. Caller should drop event
		return false;
	    }

	// Should we return false to tell caller that the event was trimmed?
	// if ( preconditionBroken ) {
	//     return false;
	// }

	    return true;
	}

	private boolean prepareForRemoveDeviceEvent(DeviceEvent deviceEvt) {
		// No show stopping precondition?
		// Prep: none
		return true;
	}

	/* ******************************
	 * NetworkGraphReplicationInterface methods
	 * ******************************/

	@Override
	public void putSwitchReplicationEvent(SwitchEvent switchEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForAddSwitchEvent(switchEvent)) {
		putSwitch(switchEvent);
	    }
	    // TODO handle invariant violation
	}

	@Override
	public void removeSwitchReplicationEvent(SwitchEvent switchEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForRemoveSwitchEvent(switchEvent)) {
		removeSwitch(switchEvent);
	    }
	    // TODO Auto-generated method stub
	}

	@Override
	public void putPortReplicationEvent(PortEvent portEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForAddPortEvent(portEvent)) {
		putPort(portEvent);
	    }
	    // TODO Auto-generated method stub
	}

	@Override
	public void removePortReplicationEvent(PortEvent portEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForRemovePortEvent(portEvent)) {
		removePort(portEvent);
	    }
	    // TODO Auto-generated method stub
	}

	@Override
	public void putLinkReplicationEvent(LinkEvent linkEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForAddLinkEvent(linkEvent)) {
		putLink(linkEvent);
	    }
	    // TODO Auto-generated method stub
	}

	@Override
	public void removeLinkReplicationEvent(LinkEvent linkEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForRemoveLinkEvent(linkEvent)) {
		removeLink(linkEvent);
	    }
	    // TODO Auto-generated method stub
	}

	@Override
	public void putDeviceReplicationEvent(DeviceEvent deviceEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForAddDeviceEvent(deviceEvent)) {
		putDevice(deviceEvent);
	    }
	    // TODO Auto-generated method stub
	}

	@Override
	public void removeDeviceReplicationEvent(DeviceEvent deviceEvent) {
	    // TODO who is in charge of ignoring event triggered by my self?
	    // This method or caller?
	    if (prepareForRemoveDeviceEvent(deviceEvent)) {
		removeDevice(deviceEvent);
	    }
	    // TODO Auto-generated method stub
	}

	/* ************************************************
	 * Internal In-memory object mutation methods.
	 * ************************************************/

	void putSwitch(SwitchEvent swEvt) {
	    if (swEvt == null) {
		throw new IllegalArgumentException("Switch cannot be null");
	    }

	    Switch sw = switches.get(swEvt.getDpid());

	    if (sw == null) {
		sw = new SwitchImpl(this, swEvt.getDpid());
		Switch existing = switches.putIfAbsent(swEvt.getDpid(), sw);
		if (existing != null) {
		    log.warn(
			    "Concurrent putSwitch not expected. Continuing updating {}",
			    existing);
		    sw = existing;
		}
	    }

	    // Update when more attributes are added to Event object
	    // no attribute to update for now

	    // TODO handle child Port event properly for performance
	    for (PortEvent portEvt : swEvt.getPorts() ) {
		putPort(portEvt);
	    }

	}

	void removeSwitch(SwitchEvent swEvt) {
	    if (swEvt == null) {
		throw new IllegalArgumentException("Switch cannot be null");
	    }

	    // TODO handle child Port event properly for performance
	    for (PortEvent portEvt : swEvt.getPorts() ) {
		removePort(portEvt);
	    }

	    Switch sw = switches.get(swEvt.getDpid());

	    if (sw == null) {
		log.warn("Switch {} already removed, ignoring", swEvt);
		return;
	    }

	    // Sanity check
	    if (!sw.getPorts().isEmpty()) {
		log.warn(
			"Ports on Switch {} should be removed prior to removing Switch. Removing Switch anyways",
			swEvt);
		// XXX Should we remove Port?
	    }
	    if (!sw.getDevices().isEmpty()) {
		log.warn(
			"Devices on Switch {} should be removed prior to removing Switch. Removing Switch anyways",
			swEvt);
		// XXX Should we remove Device to Switch relation?
	    }
	    if (!sw.getIncomingLinks().iterator().hasNext()) {
		log.warn(
			"IncomingLinks on Switch {} should be removed prior to removing Switch. Removing Switch anyways",
			swEvt);
		// XXX Should we remove Link?
	    }
	    if (!sw.getOutgoingLinks().iterator().hasNext()) {
		log.warn(
			"OutgoingLinks on Switch {} should be removed prior to removing Switch. Removing Switch anyways",
			swEvt);
		// XXX Should we remove Link?
	    }

	    boolean removed = switches.remove(swEvt.getDpid(), sw);
	    if (removed) {
		log.warn(
			"Switch instance was replaced concurrently while removing {}. Something is not right.",
			sw);
	    }
	}

	void putPort(PortEvent portEvt) {
	    if (portEvt == null) {
		throw new IllegalArgumentException("Port cannot be null");
	    }
	    Switch sw = switches.get(portEvt.getDpid());
	    if (sw == null) {
		throw new BrokenInvariantException(String.format(
			"Switch with dpid %s did not exist.",
			new Dpid(portEvt.getDpid())));
	    }
	    Port p = sw.getPort(portEvt.getNumber());
	    PortImpl port = null;
	    if (p != null) {
		port = getPortImpl(p);
	    }

	    if (port == null) {
		port = new PortImpl(this, sw, portEvt.getNumber());
	    }

	    // TODO update attributes

	    SwitchImpl s = getSwitchImpl(sw);
	    s.addPort(port);
	}

	void removePort(PortEvent portEvt) {
	    if (portEvt == null) {
		throw new IllegalArgumentException("Port cannot be null");
	    }

	    Switch sw = switches.get(portEvt.getDpid());
	    if (sw == null) {
		log.warn("Parent Switch for Port {} already removed, ignoring", portEvt);
		return;
	    }

	    Port p = sw.getPort(portEvt.getNumber());
	    if (p == null) {
		log.warn("Port {} already removed, ignoring", portEvt);
		return;
	    }

	    // check if there is something referring to this Port

	    if (!p.getDevices().iterator().hasNext()) {
		log.warn(
			"Devices on Port {} should be removed prior to removing Port. Removing Port anyways",
			portEvt);
		// XXX Should we remove Device to Port relation?
	    }
	    if (p.getIncomingLink() != null) {
		log.warn(
			"IncomingLinks on Port {} should be removed prior to removing Port. Removing Port anyways",
			portEvt);
		// XXX Should we remove Link?
	    }
	    if (p.getOutgoingLink() != null) {
		log.warn(
			"OutgoingLinks on Port {} should be removed prior to removing Port. Removing Port anyways",
			portEvt);
		// XXX Should we remove Link?
	    }

	    // remove Port from Switch
	    SwitchImpl s = getSwitchImpl(sw);
	    s.removePort(p);
	}

	void putLink(LinkEvent linkEvt) {
	    if (linkEvt == null) {
		throw new IllegalArgumentException("Link cannot be null");
	    }

	    Switch srcSw = switches.get(linkEvt.getSrc().dpid);
	    if (srcSw == null) {
		throw new BrokenInvariantException(
			String.format(
				"Switch with dpid %s did not exist.",
				new Dpid(linkEvt.getSrc().dpid)));
	    }

	    Switch dstSw = switches.get(linkEvt.getDst().dpid);
	    if (dstSw == null) {
		throw new BrokenInvariantException(
			String.format(
				"Switch with dpid %s did not exist.",
				new Dpid(linkEvt.getDst().dpid)));
	    }

	    Port srcPort = srcSw.getPort(linkEvt.getSrc().number);
	    if (srcPort == null) {
		throw new BrokenInvariantException(
			String.format(
				"Src Port %s of a Link did not exist.",
				linkEvt.getSrc() ));
	    }

	    Port dstPort = dstSw.getPort(linkEvt.getDst().number);
	    if (dstPort == null) {
		throw new BrokenInvariantException(
			String.format(
				"Dst Port %s of a Link did not exist.",
				linkEvt.getDst() ));
	    }

	    // getting Link instance from destination port incoming Link
	    Link l = dstPort.getIncomingLink();
	    LinkImpl link = null;
	    assert( l == srcPort.getOutgoingLink() );
	    if (l != null) {
		link = getLinkImpl(l);
	    }

	    if (link == null) {
		link = new LinkImpl(this, srcPort, dstPort);
	    }


	    PortImpl dstPortMem = getPortImpl(dstPort);
	    PortImpl srcPortMem = getPortImpl(srcPort);

	    // Add Link first to avoid further Device addition

	    // add Link to Port
	    dstPortMem.setIncomingLink(link);
	    srcPortMem.setOutgoingLink(link);

	    // remove Device Pointing to Port if any
	    for(Device d : dstPortMem.getDevices() ) {
		log.error("Device {} on Port {} should have been removed prior to adding Link {}", d, dstPort, linkEvt);
		DeviceImpl dev = getDeviceImpl(d);
		dev.removeAttachmentPoint(dstPort);
		// This implies that change is made to Device Object.
		// sending Device attachment point removed event
		DeviceEvent rmEvent = new DeviceEvent(d.getMacAddress());
		rmEvent.addAttachmentPoint(new SwitchPort(dstPort.getDpid(), dstPort.getNumber()));
		removeDeviceEvent(rmEvent);
	    }
	    dstPortMem.removeAllDevice();
	    for(Device d : srcPortMem.getDevices() ) {
		log.error("Device {} on Port {} should have been removed prior to adding Link {}", d, srcPort, linkEvt);
		DeviceImpl dev = getDeviceImpl(d);
		dev.removeAttachmentPoint(srcPort);
		// This implies that change is made to Device Object.
		// sending Device attachment point removed event
		DeviceEvent rmEvent = new DeviceEvent(d.getMacAddress());
		rmEvent.addAttachmentPoint(new SwitchPort(dstPort.getDpid(), dstPort.getNumber()));
		removeDeviceEvent(rmEvent);
	    }
	    srcPortMem.removeAllDevice();

	}

	void removeLink(LinkEvent linkEvt) {
	    if (linkEvt == null) {
		throw new IllegalArgumentException("Link cannot be null");
	    }

	    Switch srcSw = switches.get(linkEvt.getSrc().dpid);
	    if (srcSw == null) {
		log.warn("Src Switch for Link {} already removed, ignoring", linkEvt);
		return;
	    }

	    Switch dstSw = switches.get(linkEvt.getDst().dpid);
	    if (dstSw == null) {
		log.warn("Dst Switch for Link {} already removed, ignoring", linkEvt);
		return;
	    }

	    Port srcPort = srcSw.getPort(linkEvt.getSrc().number);
	    if (srcPort == null) {
		log.warn("Src Port for Link {} already removed, ignoring", linkEvt);
		return;
	    }

	    Port dstPort = dstSw.getPort(linkEvt.getDst().number);
	    if (dstPort == null) {
		log.warn("Dst Port for Link {} already removed, ignoring", linkEvt);
		return;
	    }

	    Link l = dstPort.getIncomingLink();
	    if (  l == null ) {
		log.warn("Link {} already removed on destination Port", linkEvt);
	    }
	    l = srcPort.getOutgoingLink();
	    if (  l == null ) {
		log.warn("Link {} already removed on src Port", linkEvt);
	    }

	    getPortImpl(dstPort).setIncomingLink(null);
	    getPortImpl(srcPort).setOutgoingLink(null);
	}

	// XXX Need to rework Device related
	void putDevice(DeviceEvent deviceEvt) {
	    if (deviceEvt == null) {
		throw new IllegalArgumentException("Device cannot be null");
	    }

	    Device device = getDeviceByMac(deviceEvt.getMac());
	    if ( device == null ) {
		device = new DeviceImpl(this, deviceEvt.getMac());
		Device existing = mac2Device.putIfAbsent(deviceEvt.getMac(), device);
		if (existing != null) {
		    log.warn(
			    "Concurrent putDevice seems to be in action. Continuing updating {}",
			    existing);
		    device = existing;
		}
	    }
	    DeviceImpl memDevice = getDeviceImpl(device);

	    // for each attachment point
	    for (SwitchPort swp : deviceEvt.getAttachmentPoints() ) {
		// Attached Ports' Parent Switch must exist
		Switch sw = getSwitch(swp.dpid);
		if ( sw ==  null ) {
		    log.warn("Switch for the attachment point {} did not exist. skipping mutation", swp);
		    continue;
		}
		// Attached Ports must exist
		Port port = sw.getPort(swp.number);
		if ( port == null ) {
		    log.warn("Port for the attachment point {} did not exist. skipping mutation", swp);
		    continue;
		}
		// Attached Ports must not have Link
		if ( port.getOutgoingLink() != null || port.getIncomingLink() != null ) {
		    log.warn("Link (Out:{},In:{}) exist on the attachment point, skipping mutation.", port.getOutgoingLink(), port.getIncomingLink());
		    continue;
		}

		// finally add Device <-> Port on In-memory structure
		PortImpl memPort = getPortImpl(port);
		memPort.addDevice(device);
		memDevice.addAttachmentPoint(port);
	    }

	    // for each IP address
	    for( InetAddress ipAddr : deviceEvt.getIpAddresses() ) {
		// Add Device -> IP
		memDevice.addIpAddress(ipAddr);

		// Add IP -> Set<Device>
		boolean updated = false;
		do {
		    Set<Device> devices = this.addr2Device.get(ipAddr);
		    if ( devices == null ) {
			devices = new HashSet<>();
			Set<Device> existing = this.addr2Device.putIfAbsent(ipAddr, devices);
			if ( existing == null ) {
			    // success
			    updated = true;
			}
		    } else {
			Set<Device> updateDevices = new HashSet<>(devices);
			updateDevices.add(device);
			updated = this.addr2Device.replace(ipAddr, devices, updateDevices);
		    }
		    if (!updated) {
			log.debug("Collision detected, updating IP to Device mapping retrying.");
		    }
		} while( !updated );
	    }
	}

	void removeDevice(DeviceEvent deviceEvt) {
	    if (deviceEvt == null) {
		throw new IllegalArgumentException("Device cannot be null");
	    }

	    Device device = getDeviceByMac(deviceEvt.getMac());
	    if ( device == null ) {
		log.warn("Device {} already removed, ignoring", deviceEvt);
		return;
	    }
	    DeviceImpl memDevice = getDeviceImpl(device);

	    // for each attachment point
	    for (SwitchPort swp : deviceEvt.getAttachmentPoints() ) {
		// Attached Ports' Parent Switch must exist
		Switch sw = getSwitch(swp.dpid);
		if ( sw ==  null ) {
		    log.warn("Switch for the attachment point {} did not exist. skipping attachment point mutation", swp);
		    continue;
		}
		// Attached Ports must exist
		Port port = sw.getPort(swp.number);
		if ( port == null ) {
		    log.warn("Port for the attachment point {} did not exist. skipping attachment point mutation", swp);
		    continue;
		}

		// finally remove Device <-> Port on In-memory structure
		PortImpl memPort = getPortImpl(port);
		memPort.removeDevice(device);
		memDevice.removeAttachmentPoint(port);
	    }

	    // for each IP address
	    for( InetAddress ipAddr : deviceEvt.getIpAddresses() ) {
		// Remove Device -> IP
		memDevice.removeIpAddress(ipAddr);

		// Remove IP -> Set<Device>
		boolean updated = false;
		do {
		    Set<Device> devices = this.addr2Device.get(ipAddr);
		    if ( devices == null ) {
			// already empty set, nothing to do
			updated = true;
		    } else {
			Set<Device> updateDevices = new HashSet<>(devices);
			updateDevices.remove(device);
			updated = this.addr2Device.replace(ipAddr, devices, updateDevices);
		    }
		    if (!updated) {
			log.debug("Collision detected, updating IP to Device mapping retrying.");
		    }
		} while( !updated );
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
	    // XXX clear everything first?

	    for (RCSwitch sw : RCSwitch.getAllSwitches()) {
		if ( sw.getStatus() != RCSwitch.STATUS.ACTIVE ) {
		    continue;
		}
		putSwitchReplicationEvent(new SwitchEvent(sw.getDpid()));
	    }

	    for (RCPort p : RCPort.getAllPorts()) {
		if (p.getStatus() != RCPort.STATUS.ACTIVE) {
		    continue;
		}
		putPortReplicationEvent(new PortEvent(p.getDpid(), p.getNumber() ));
	    }

	    // TODO Is Device going to be in DB? If so, read from DB.
	    //	for (RCDevice d : RCDevice.getAllDevices()) {
	    //	    DeviceEvent devEvent = new DeviceEvent( MACAddress.valueOf(d.getMac()) );
	    //	    for (byte[] portId : d.getAllPortIds() ) {
	    //		devEvent.addAttachmentPoint( new SwitchPort( RCPort.getDpidFromKey(portId), RCPort.getNumberFromKey(portId) ));
	    //	    }
	    //	}

	    for (RCLink l : RCLink.getAllLinks()) {
		putLinkReplicationEvent( new LinkEvent(l.getSrc().dpid, l.getSrc().number, l.getDst().dpid, l.getDst().number));
	    }
	}
}
