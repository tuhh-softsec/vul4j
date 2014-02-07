package net.onrc.onos.ofcontroller.networkgraph;



/**
 * Interface of Port Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Port {
	public Long getNumber();
	public Long getHardwareAddress();

	public Switch getSwitch();

	public Link getOutgoingLink();
	public Link getIncomingLink();

	public Iterable<Device> getDevices();
}
