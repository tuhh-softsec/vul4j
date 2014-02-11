package net.onrc.onos.ofcontroller.networkgraph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

/**
 * Switch Object stored in In-memory Topology.
 *
 * TODO REMOVE following design memo: This object itself may hold the DBObject,
 * but this Object itself will not issue any read/write to the DataStore.
 */
public class SwitchImpl extends NetworkGraphObject implements Switch {

	private Long dpid;
	// These needs to be ConcurrentCollecton if allowing Graph to be accessed Concurrently
	private final Map<Long, Port> ports;

	public SwitchImpl(NetworkGraph graph, Long dpid) {
		super(graph);
		this.dpid = dpid;
		ports = new HashMap<Long, Port>();
	}

	@Override
	public Long getDpid() {
		return dpid;
	}

	@Override
	public Collection<Port> getPorts() {
		return Collections.unmodifiableCollection(ports.values());
	}

	@Override
	public Port getPort(Long number) {
		return ports.get(number);
	}

	@Override
	public Collection<FlowEntry> getFlowEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Switch> getNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Link getLinkToNeighbor(Long neighborDpid) {
		for (Link link : graph.getOutgoingLinksFromSwitch(dpid)) {
			if (link.getDestinationSwitch().getDpid() == neighborDpid) {
				return link;
			}
		}
		return null;
	}

	@Override
	public Collection<Device> getDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPort(Port port) {
		this.ports.put(port.getNumber(), port);
	}

	public Port removePort(Port port) {
	    Port p = this.ports.remove(port.getNumber());
	    // XXX Do we need to validate instance equality?
	    assert( p == port );
	    return p;
	}

	public Port addPort(Long portNumber) {
		PortImpl port = new PortImpl(graph, this, portNumber);
		ports.put(port.getNumber(), port);
		return port;
	}

	public void store() {
		RCSwitch rcSwitch = new RCSwitch(dpid);

		for (Port port : ports.values()) {
			RCPort rcPort = new RCPort(dpid, (long)port.getNumber());
			rcSwitch.addPortId(rcPort.getId());
		}


		try {
			rcSwitch.update();

		} catch (ObjectDoesntExistException | WrongVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Iterable<Link> getOutgoingLinks() {
		LinkedList<Link> links = new LinkedList<Link>();
		for (Port port: getPorts()) {
			Link link = port.getOutgoingLink();
			if (link != null) {
				links.add(link);
			}
		}
		return links;
	}

	@Override
	public Iterable<Link> getIncomingLinks() {
		LinkedList<Link> links = new LinkedList<Link>();
		for (Port port: getPorts()) {
			Link link = port.getIncomingLink();
			if (link != null) {
				links.add(link);
			}
		}
		return links;
	}
}
