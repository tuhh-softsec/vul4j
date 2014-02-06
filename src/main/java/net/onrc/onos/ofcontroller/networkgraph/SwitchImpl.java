package net.onrc.onos.ofcontroller.networkgraph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	private long dpid;
	private final Map<Short, Port> ports;

	public SwitchImpl(NetworkGraph graph) {
		super(graph);

		ports = new HashMap<Short, Port>();
	}

	@Override
	public long getDpid() {
		return dpid;
	}

	@Override
	public Collection<Port> getPorts() {
		return Collections.unmodifiableCollection(ports.values());
	}

	@Override
	public Port getPort(short number) {
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
	public Link getLinkToNeighbor(long neighborDpid) {
		for (Link link : graph.getLinksFromSwitch(dpid)) {
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

	public void setDpid(long dpid) {
		this.dpid = dpid;
	}

	public void addPort(Port port) {
		this.ports.put(port.getNumber(), port);
	}

	@Override
	public Iterable<Link> getLinks() {
		Set<Link> links = new HashSet<>();
		for( Port p : ports.values()) {
		    links.add(p.getLink());
		}
		return links;
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
}
