package net.onrc.onos.ofcontroller.networkgraph;



/**
 * Interface of Port Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Port {
	public short getNumber();
	public String getName();
	public long getHardwareAddress();

	public Switch getSwitch();

	public Link getLink();
}
