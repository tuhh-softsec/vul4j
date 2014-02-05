package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Interface of Link Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Link {
	public Port getSourcePort();
	public Port getDestinationPort();
	public Switch getSourceSwitch();
	public Switch getDestinationSwitch();

	public long getLastSeenTime();
	public int getCost();

	// Not sure if we want to expose these northbound
	public long getSourceSwitchDpid();
	public short getSourcePortNumber();
	public long getDestinationSwitchDpid();
	public short getDestinationPortNumber();
}
