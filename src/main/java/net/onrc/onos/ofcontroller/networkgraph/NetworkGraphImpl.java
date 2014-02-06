package net.onrc.onos.ofcontroller.networkgraph;

import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;

/**
 * The "NB" read-only Network Map.
 *
 * TODO Current implementation directly read from DB, but eventually, it should
 * read from In-memory shared Network Map instance within ONOS process.
 *
 */
public class NetworkGraphImpl extends AbstractNetworkGraph {

    private static final Logger log = LoggerFactory
	    .getLogger(NetworkGraphImpl.class);

    public NetworkGraphImpl() {
	super();
    }

    void addSwitch(Switch sw) {
	if ( sw == null ) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}
	switches.put(sw.getDpid(), sw);

    }

    /**
     * Deactivate Switch (and its Ports)
     * @param sw
     */
    void deactivateSwitch(Switch sw) {
	if ( sw == null ) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}
	SwitchImpl s = getSwitchImpl(sw);
	// TODO Deactivate Switch

	// XXX Are we sure we want to deactivate Ports also?

	// TODO Auto-generated method stub

    }

    void addPort(Port port) {
	if ( port == null ) {
	    throw new IllegalArgumentException("Port cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void deactivatePort(Port port) {
	if ( port == null ) {
	    throw new IllegalArgumentException("Port cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void addLink(Link link) {
	if ( link == null ) {
	    throw new IllegalArgumentException("Link cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void removeLink(Link link) {
	if ( link == null ) {
	    throw new IllegalArgumentException("Link cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void updateDevice(Device device) {
	if ( device == null ) {
	    throw new IllegalArgumentException("Device cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void removeDevice(Device device) {
	if ( device == null ) {
	    throw new IllegalArgumentException("Device cannot be null");
	}
	// TODO Auto-generated method stub

    }

    private SwitchImpl getSwitchImpl(Switch sw) {
	if( sw instanceof SwitchImpl ) {
	    return (SwitchImpl) sw;
	}
	throw new ClassCastException("SwitchImpl expected, but found:" + sw.getClass().getName()  );
    }

    // FIXME To be removed later this class should never read from DB.
    public void readSwitchFromTopology(long dpid) {
	SwitchImpl sw = new SwitchImpl(this);

	RCSwitch rcSwitch = new RCSwitch(dpid);
	try {
	    rcSwitch.read();
	} catch (ObjectDoesntExistException e) {
	    log.warn("Tried to get a switch that doesn't exist {}", dpid);
	    return;
	}

	sw.setDpid(rcSwitch.getDpid());

	addSwitch(sw);

	for (byte[] portId : rcSwitch.getAllPortIds()) {
	    RCPort rcPort = RCPort.createFromKey(portId);
	    try {
		rcPort.read();

		PortImpl port = new PortImpl(this);
		// port.setDpid(dpid);

		// TODO why are port numbers long?
		// port.setPortNumber((short)rcPort.getNumber());

		port.setSwitch(sw);
		sw.addPort(port);

		addPort(port);

	    } catch (ObjectDoesntExistException e) {
		log.warn("Tried to read port that doesn't exist", rcPort);
	    }
	}

    }

}
