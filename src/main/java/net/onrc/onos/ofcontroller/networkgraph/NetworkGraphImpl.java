package net.onrc.onos.ofcontroller.networkgraph;

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
 * TODO To be synchronized based on TopologyEvent Notification.
 *
 */
public class NetworkGraphImpl extends AbstractNetworkGraph {

    private static final Logger log = LoggerFactory
	    .getLogger(NetworkGraphImpl.class);

    public NetworkGraphImpl() {
	super();
    }

    void addSwitch(Switch sw) {
	if (sw == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}
	switches.put(sw.getDpid(), sw);
    }

    /**
     * Deactivate Switch (and its Ports?)
     *
     * @param sw
     */
    void deactivateSwitch(Switch sw) {
	if (sw == null) {
	    throw new IllegalArgumentException("Switch cannot be null");
	}
	SwitchImpl s = getSwitchImpl(sw);
	// XXX When modifying existing object should we change the object itself
	// or create a modified copy and switch them?

	// TODO Deactivate Switch

	// XXX Are we sure we want to deactivate Ports also?

	// TODO Auto-generated method stub
    }

    void addPort(Port port) {
	if (port == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void deactivatePort(Port port) {
	if (port == null) {
	    throw new IllegalArgumentException("Port cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void addLink(Link link) {
	if (link == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}
	// TODO Auto-generated method stub

    }

    void removeLink(Link link) {
	if (link == null) {
	    throw new IllegalArgumentException("Link cannot be null");
	}
	// TODO Auto-generated method stub

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
	throw new ClassCastException("SwitchImpl expected, but found:"
	        + sw.getClass().getName());
    }

    public void loadWholeTopologyFromDB() {
	// XXX clear everything first?

	for (RCSwitch sw : RCSwitch.getAllSwitches()) {
	    try {
		sw.read();
		// TODO SwitchImpl probably should have a constructor from
		// RCSwitch
		SwitchImpl memSw = new SwitchImpl(this);
		memSw.setDpid(sw.getDpid());

		addSwitch(memSw);
	    } catch (ObjectDoesntExistException e) {
		log.error("Read Switch Failed, skipping", e);
	    }
	}

	for (RCPort p : RCPort.getAllPorts()) {
	    try {
		p.read();

		// TODO PortImpl probably should have a constructor from RCPort
		PortImpl memPort = new PortImpl(this);
		// FIXME remove shortValue()
		memPort.setPortNumber(p.getNumber().shortValue());
		Switch sw = this.getSwitch(p.getDpid());
		if (sw == null) {
		    log.error("Switch {} missing when adding Port {}",
			    new Dpid(p.getDpid()), p);
		    continue;
		}
		memPort.setSwitch(sw);

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

		LinkImpl memLink = new LinkImpl(this);

		Switch srcSw = this.getSwitch(l.getSrc().dpid);
		if (srcSw == null) {
		    log.error("Switch {} missing when adding Link {}",
			    new Dpid(l.getSrc().dpid), l);
		    continue;
		}
		memLink.setSrcSwitch(srcSw);
		// FIXME remove shortValue()
		memLink.setSrcPort(srcSw.getPort(l.getSrc().number.shortValue()));

		Switch dstSw = this.getSwitch(l.getDst().dpid);
		if (dstSw == null) {
		    log.error("Switch {} missing when adding Link {}",
			    new Dpid(l.getDst().dpid), l);
		    continue;
		}
		memLink.setDstSwitch(dstSw);
		// FIXME remove shortValue()
		memLink.setDstPort(srcSw.getPort(l.getDst().number.shortValue()));

		addLink(memLink);
	    } catch (ObjectDoesntExistException e) {
		log.debug("Delete Link Failed", e);
	    }
	}
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
