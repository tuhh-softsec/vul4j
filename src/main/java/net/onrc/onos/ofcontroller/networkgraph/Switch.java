package net.onrc.onos.ofcontroller.networkgraph;

import java.util.Collection;

/**
 * Interface of Switch Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Switch {
	public Long getDpid();

	public Collection<Port> getPorts();

	public Port getPort(Long number);


	// Graph traversal API
	public Iterable<Switch> getNeighbors();

	public Iterable<Link> getOutgoingLinks();
	public Iterable<Link> getIncomingLinks();

	public Link getLinkToNeighbor(Long dpid);

	public Collection<Device> getDevices();
}
