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
	public Double getCapacity();

	// Not sure if we want to expose these northbound
	// Toshi: I think these are unnecessary because we can get them
	// Toshi: like "this.getSourcePort().getSwitch()" etc.
	public Long getSourceSwitchDpid();
	public Long getSourcePortNumber();
	public Long getDestinationSwitchDpid();
	public Long getDestinationPortNumber();
}
