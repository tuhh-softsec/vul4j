package net.onrc.onos.ofcontroller.networkgraph;

import java.util.NoSuchElementException;

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
 * TODO TBD: This class may delay the requested change to handle event
 * re-ordering. e.g.) Link Add came in, but Switch was not there.
 */
public class NetworkGraphImpl extends AbstractNetworkGraph {

    private static final Logger log = LoggerFactory
	    .getLogger(NetworkGraphImpl.class);

    public NetworkGraphImpl() {
	super();
    }

    /**
     * Add Switch to Topology.
     *
     * Fails with an Exception if a switch with same DPID already exist in
     * Topology.
     *
     * @param sw
     */
    void addSwitch(Switch sw) {
	if (sw == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}
	Switch oldSw = switches.putIfAbsent(sw.getDpid(), sw);
	if (oldSw != null) {
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException("Switch already exists");
	}
    }

    /**
     * Deactivate and remove Switch.
     *
     * XXX Should it deactivate or delete its Ports also?
     *
     * @param sw
     */
    void deactivateSwitch(Switch sw) {
	if (sw == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}

	if( !isSwitchInstanceInTopology(sw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(sw.getDpid())));
	}

	// XXX Are we sure we want to deactivate Ports also?
	for (Port p : sw.getPorts()) {
	    deactivatePort(p);
	}

	// TODO Deactivate Switch: What to do simply remove?
	for (Link l : sw.getIncomingLinks()) {
	    removeLink(l);
	}

	for (Link l : sw.getOutgoingLinks()) {
	    removeLink(l);
	}

	if (!switches.containsKey(sw.getDpid())) {
	    throw new NoSuchElementException(String.format(
		    "Switch with dpid %s not found.", new Dpid(sw.getDpid())));
	}
	boolean removed = switches.remove(sw.getDpid(), sw);

	if (!removed) {
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(sw.getDpid())));
	}
    }

    /**
     * Add Port to Topology.
     *
     * @param port
     */
    void addPort(Port port) {
	if (port == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}
	Switch sw = port.getSwitch();

	if( !isSwitchInstanceInTopology(sw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(sw.getDpid())));
	}

	SwitchImpl s = getSwitchImpl(sw);

	s.addPort(port);
	// XXX Check If port already exist, if so then what? deactivate old?
    }

    /**
     * Deactivate and remove Ports.
     *
     * @param port
     */
    void deactivatePort(Port port) {
	if (port == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}

	Switch sw = port.getSwitch();

	if( !isSwitchInstanceInTopology(sw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(sw.getDpid())));
	}


	// remove Link
	removeLink(port.getIncomingLink());
	removeLink(port.getOutgoingLink());

	// remove Device
	for(Device d: port.getDevices()) {
	    removeDevice(d);
	}

	// remove Port from Switch
	SwitchImpl s = getSwitchImpl(sw);
	s.removePort(port);
    }

    void addLink(Link link) {
	if (link == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}

	Switch srcSw = link.getSourceSwitch();
	if( !isSwitchInstanceInTopology(srcSw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(srcSw.getDpid())));
	}

	Switch dstSw = link.getDestinationSwitch();
	if( !isSwitchInstanceInTopology(dstSw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(dstSw.getDpid())));
	}

	PortImpl srcPort = getPortImpl( link.getSourcePort() );
	PortImpl dstPort = getPortImpl( link.getDestinationPort() );

	// XXX check Existing Link first?
	srcPort.setOutgoingLink(link);
	dstPort.setIncomingLink(link);
    }

    void removeLink(Link link) {
	if (link == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}

	Switch srcSw = link.getSourceSwitch();
	if( !isSwitchInstanceInTopology(srcSw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(srcSw.getDpid())));
	}

	Switch dstSw = link.getDestinationSwitch();
	if( !isSwitchInstanceInTopology(dstSw) ){
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format(
			    "Switch with dpid %s did not exist or different instance registered.",
			    new Dpid(dstSw.getDpid())));
	}

	PortImpl srcPort = getPortImpl( link.getSourcePort() );
	PortImpl dstPort = getPortImpl( link.getDestinationPort() );

	// XXX check Existing Link first?
	if( srcPort.getOutgoingLink() != link || dstPort.getIncomingLink() != link) {
	    // XXX Define or choose more appropriate Exception.
	    throw new RuntimeException(
		    String.format("Link %s did not belong to Topology", link.toString())
		    );
	}
	// remove Link
	srcPort.setOutgoingLink(null);
	dstPort.setIncomingLink(null);
    }

    void updateDevice(Device device) {
	if (device == null) {
	    throw new IllegalArgumentException("Device cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void removeDevice(Device device) {
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
		// TODO SwitchImpl probably should have a constructor from
		// RCSwitch
		SwitchImpl memSw = new SwitchImpl(this, sw.getDpid());

		addSwitch(memSw);
	    } catch (ObjectDoesntExistException e) {
		log.error("Read Switch Failed, skipping", e);
	    }
	}

	for (RCPort p : RCPort.getAllPorts()) {
	    try {
		p.read();

		// TODO PortImpl probably should have a constructor from RCPort
		Switch sw = this.getSwitch(p.getDpid());
		if (sw == null) {
		    log.error("Switch {} missing when adding Port {}",
			    new Dpid(p.getDpid()), p);
		    continue;
		}
		PortImpl memPort = new PortImpl(this, sw, p.getNumber());

		addPort(memPort);
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

		LinkImpl memLink = new LinkImpl(this,
			srcSw.getPort(l.getSrc().number), dstSw.getPort(l
				.getDst().number));

		addLink(memLink);
	    } catch (ObjectDoesntExistException e) {
		log.debug("Delete Link Failed", e);
	    }
	}
    }
}
