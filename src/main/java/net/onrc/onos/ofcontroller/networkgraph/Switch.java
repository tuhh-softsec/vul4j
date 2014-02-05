package net.onrc.onos.ofcontroller.networkgraph;

import java.util.Collection;

import net.onrc.onos.ofcontroller.util.FlowEntry;

/**
 * Interface of Switch Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Switch {
	public long getDpid();

	public Collection<Port> getPorts();

	public Port getPort(short number);


	// Flows
	public Collection<FlowEntry> getFlowEntries();

	// Graph traversal API
	public Iterable<Switch> getNeighbors();

	public Iterable<Link> getLinks();

	public Link getLinkToNeighbor(long dpid);

	public Collection<Device> getDevices();
}
