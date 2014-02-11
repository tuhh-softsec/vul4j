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
	// XXX What is the Definition of neighbor? Link exist in both direction or one-way is sufficient to be a neighbor, etc.
	public Iterable<Switch> getNeighbors();

	public Iterable<Link> getOutgoingLinks();
	public Iterable<Link> getIncomingLinks();

	public Link getLinkToNeighbor(Long dpid);

	// XXX Iterable or Collection?
	public Collection<Device> getDevices();
}
