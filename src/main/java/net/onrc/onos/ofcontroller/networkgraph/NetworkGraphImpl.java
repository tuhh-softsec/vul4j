package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Set;

import net.onrc.onos.datastore.topology.RCLink;
import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;
import net.onrc.onos.ofcontroller.util.Dpid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;

/**
 * The "NB" read-only Network Map.
 *
 * - Maintain Invariant/Relationships between Topology Objects.
 *
 * TODO To be synchronized based on TopologyEvent Notification.
 *
 * TODO TBD: Caller is expected to maintain parent/child calling order. Parent
 * Object must exist before adding sub component(Add Switch -> Port). Child
 * Object need to be removed before removing parent (Delete Port->Switch)
 *
 * TODO TBD: This class may delay the requested change to handle event
 * re-ordering. e.g.) Link Add came in, but Switch was not there.
 *
 */
public class NetworkGraphImpl extends AbstractNetworkGraph
								implements NetworkGraphDiscoveryInterface {

    private static final Logger log = LoggerFactory
	    .getLogger(NetworkGraphImpl.class);
    
    private final NetworkGraphDatastore datastore;

    public NetworkGraphImpl() {
	super();
	datastore = new NetworkGraphDatastore(this);
    }

    /**
     * put Switch
     *
     * XXX Internal Invariant-maintenance method. Will not write to DB. Will not
     * fire Notification.
     *
     * @param swEvt
     */
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

	// Add upate when more attributes are added to Event object
	// no attribute to update for now
    }

    /**
     * remove Switch.
     *
     * XXX Internal Invariant-maintenance method. Will not write to DB. Will not
     * fire Notification.
     *
     * @param swEvt
     */
    void removeSwitch(SwitchEvent swEvt) {
	if (swEvt == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}

	Switch sw = switches.get(swEvt.getDpid());

	if (sw == null) {
	    log.warn("Switch {} already removed, ignoreing", swEvt);
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

    /**
     * put Port
     *
     * XXX Internal Invariant-maintenance method. Will not write to DB. Will not
     * fire Notification.
     *
     * @param portEvt
     */
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

    /**
     * remove Port
     *
     * XXX Internal Invariant-maintenance method. Will not write to DB. Will not
     * fire Notification.
     *
     * @param portEvt
     */
    void removePort(PortEvent portEvt) {
	if (portEvt == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}

	Switch sw = switches.get(portEvt.getDpid());
	if (sw == null) {
	    log.warn("Parent Switch for Port {} already removed, ignoreing", portEvt);
	    return;
	}

	Port p = sw.getPort(portEvt.getNumber());
	if (p == null) {
	    log.warn("Port {} already removed, ignoreing", portEvt);
	    return;
	}
	// null check

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

    /**
     * put Link
     *
     * XXX Internal Invariant-maintenance method. Will not write to DB. Will not
     * fire Notification.
     *
     * @param linkEvt
     */
    void putLink(LinkEvent linkEvt) {
	if (linkEvt == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}
	// TODO Auto-generated method stub

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

	Link l = dstPort.getIncomingLink();
	LinkImpl link = null;
	assert( l == srcPort.getOutgoingLink() );
	if (l != null) {
//	    link = getLink
	}

	// TODO update Switch
	// TODO update Link


	// XXX check Existing Link first?
//	srcPort.setOutgoingLink(link);
//	dstPort.setIncomingLink(link);
    }

    /**
     * removeLink
     *
     * XXX Internal Invariant-maintenance method. Will not write to DB. Will not
     * fire Notification.
     *
     * @param link
     */
    void removeLink(LinkEvent link) {
	if (link == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}
	// TODO Auto-generated method stub

	// Switch srcSw = link.getSourceSwitch();
	// if (!isSwitchInstanceInTopology(srcSw)) {
	// // XXX Define or choose more appropriate Exception.
	// throw new RuntimeException(
	// String.format(
	// "Switch with dpid %s did not exist or different instance registered.",
	// new Dpid(srcSw.getDpid())));
	// }
	//
	// Switch dstSw = link.getDestinationSwitch();
	// if (!isSwitchInstanceInTopology(dstSw)) {
	// // XXX Define or choose more appropriate Exception.
	// throw new RuntimeException(
	// String.format(
	// "Switch with dpid %s did not exist or different instance registered.",
	// new Dpid(dstSw.getDpid())));
	// }
	//
	// PortImpl srcPort = getPortImpl(link.getSourcePort());
	// PortImpl dstPort = getPortImpl(link.getDestinationPort());
	//
	// // XXX check Existing Link first?
	// if (srcPort.getOutgoingLink() != link
	// || dstPort.getIncomingLink() != link) {
	// // XXX Define or choose more appropriate Exception.
	// throw new RuntimeException(String.format(
	// "Link %s did not belong to Topology", link.toString()));
	// }
	// // remove Link
	// srcPort.setOutgoingLink(null);
	// dstPort.setIncomingLink(null);
    }

    // XXX Need to rework Device related
    /**
     * Add new device to DB
     *
     * @param device
     */
    void updateDevice(DeviceEvent deviceToUpdate,
	    Set<InetAddress> updatedIpAddrs, Set<Port> updatedAttachmentPoints) {
	if (deviceToUpdate == null) {
	    throw new IllegalArgumentException("Device cannot be null");
	}
	// TODO Auto-generated method stub

	// Device existingDevice =
	// getDeviceByMac(deviceToUpdate.getMacAddress());
	// if (existingDevice != deviceToUpdate) {
	// throw new IllegalArgumentException(
	// "Must supply Device Object in this NetworkGraph");
	// }
	//
	// DeviceImpl device = getDeviceImpl(deviceToUpdate);
	//
	// // Update IP Addr
	// // uniq
	// Set<InetAddress> prevAddrs = new HashSet<>(
	// deviceToUpdate.getIpAddress());
	// Set<InetAddress> newAddrs = updatedIpAddrs;
	//
	// // delta
	// @SuppressWarnings("unchecked")
	// Collection<InetAddress> delAddr = CollectionUtils.subtract(newAddrs,
	// prevAddrs);
	// @SuppressWarnings("unchecked")
	// Collection<InetAddress> addAddr = CollectionUtils.subtract(prevAddrs,
	// newAddrs);
	//
	// for (InetAddress addr : delAddr) {
	// Set<Device> devices = addr2Device.get(addr);
	// if (devices == null) {
	// continue;
	// }
	// devices.remove(device);
	// device.removeIpAddress(addr);
	// }
	// for (InetAddress addr : addAddr) {
	// Set<Device> devices = addr2Device.get(addr);
	// if (devices == null) {
	// devices = new HashSet<>();
	// addr2Device.put(addr, devices);
	// }
	// devices.add(device);
	// device.addIpAddress(addr);
	// }
	//
	// // Update Attachment Point
	// // uniq
	// Set<Port> prevPorts = new HashSet<>();
	// CollectionUtils.addAll(prevAddrs,
	// deviceToUpdate.getAttachmentPoints()
	// .iterator());
	// Set<Port> newPorts = updatedAttachmentPoints;
	// // delta
	// @SuppressWarnings("unchecked")
	// Collection<Port> delPorts = CollectionUtils.subtract(newPorts,
	// prevPorts);
	// @SuppressWarnings("unchecked")
	// Collection<Port> addPorts = CollectionUtils.subtract(prevPorts,
	// newPorts);
	//
	// for (Port p : delPorts) {
	// device.removeAttachmentPoint(p);
	// getPortImpl(p).removeDevice(device);
	// }
	//
	// for (Port p : addPorts) {
	// device.addAttachmentPoint(p);
	// getPortImpl(p).addDevice(device);
	// }

	// TODO Auto-generated method stub

    }

    void removeDevice(DeviceEvent device) {
	if (device == null) {
	    throw new IllegalArgumentException("Device cannot be null");
	}
	// TODO Auto-generated method stub

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

    private DeviceImpl getDeviceImpl(Device d) {
	if (d instanceof DeviceImpl) {
	    return (DeviceImpl) d;
	}
	throw new ClassCastException("DeviceImpl expected, but found: " + d);
    }

    public boolean isSwitchInstanceInTopology(Switch sw) {
	// check if the sw instance is valid in Topology
	if (sw != switches.get(sw.getDpid())) {
	    return false;
	}
	return true;
    }

    public void loadWholeTopologyFromDB() {
	// XXX clear everything first?

	for (RCSwitch sw : RCSwitch.getAllSwitches()) {
	    try {
		sw.read();
		// TODO if there is going to be inactive Switch in DB, skip
		// TODO update other attributes if there exist any
		putSwitch(new SwitchEvent(sw.getDpid()));
	    } catch (ObjectDoesntExistException e) {
		log.error("Read Switch Failed, skipping", e);
	    }
	}

	for (RCPort p : RCPort.getAllPorts()) {
	    try {
		p.read();

		Switch sw = this.getSwitch(p.getDpid());
		if (sw == null) {
		    log.error("Switch {} missing when adding Port {}",
			    new Dpid(p.getDpid()), p);
		    continue;
		}
		PortEvent portEvent = new PortEvent(p.getDpid(), p.getNumber());
		// TODO update other attributes if there exist any
		putPort(portEvent);
	    } catch (ObjectDoesntExistException e) {
		log.error("Read Port Failed, skipping", e);
	    }
	}

	// TODO Is Device going to be in DB? If so, read from DB.
	// for (RCDevice d : RCDevice.getAllDevices()) {
	// try {
	// d.read();
	//
	// } catch (ObjectDoesntExistException e) {
	// log.debug("Read Device Failed, skipping", e);
	// }
	// }

	for (RCLink l : RCLink.getAllLinks()) {
	    try {
		l.read();

		Switch srcSw = this.getSwitch(l.getSrc().dpid);
		if (srcSw == null) {
		    log.error("Switch {} missing when adding Link {}",
			    new Dpid(l.getSrc().dpid), l);
		    continue;
		}

		Switch dstSw = this.getSwitch(l.getDst().dpid);
		if (dstSw == null) {
		    log.error("Switch {} missing when adding Link {}",
			    new Dpid(l.getDst().dpid), l);
		    continue;
		}

		LinkEvent linkEvent = new LinkEvent(l.getSrc().dpid,
			l.getSrc().number, l.getDst().dpid, l.getDst().number);
		// TODO update other attributes if there exist any
		putLink(linkEvent);
	    } catch (ObjectDoesntExistException e) {
		log.debug("Delete Link Failed", e);
	    }
	}
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
		if (checkAddSwitchInvariant()) {
			datastore.addSwitch(switchEvent);
			putSwitch(switchEvent);
		}
		// TODO handle invariant violation
	}

	@Override
	public void removeSwitchEvent(SwitchEvent switchEvent) {
		if (checkRemoveSwitchInvariant()) {
			datastore.deactivateSwitch(switchEvent);
			removeSwitch(switchEvent);
		}
		// TODO handle invariant violation
	}

	@Override
	public void putPortEvent(PortEvent portEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePortEvent(PortEvent portEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putLinkEvent(LinkEvent linkEvent) {
		if (checkAddLinkInvariant()) {
			datastore.addLink(linkEvent);
			putLink(linkEvent);
		}
		// TODO handle invariant violation
	}

	@Override
	public void removeLinkEvent(LinkEvent linkEvent) {
		if (checkRemoveLinkInvariant()) {
			datastore.removeLink(linkEvent);
			removeLink(linkEvent);
		}
		// TODO handle invariant violation
	}

	@Override
	public void updateDeviceEvent(DeviceEvent deviceToUpdate,
			Set<InetAddress> updatedIpAddrs, Set<Port> updatedAttachmentPoints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDeviceEvent(DeviceEvent deviceEvent) {
		// TODO Auto-generated method stub
		
	}
	
	/* *****************
	 * Internal methods to check invariants of the network graph
	 * *****************/
	
	private boolean checkAddSwitchInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkRemoveSwitchInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkAddPortInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkRemovePortInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkAddLinkInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkRemoveLinkInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkAddDeviceInvariant() {
		// TODO implement
		return true;
	}
	
	private boolean checkRemoveDeviceInvariant() {
		// TODO implement
		return true;
	}
}
