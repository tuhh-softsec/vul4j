package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;

import net.floodlightcontroller.util.MACAddress;

/**
 * Interface of Device Object exposed to the "NB" read-only Topology.
 *
 * TODO What a Device Object represent is unclear at the moment.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Device {
	public MACAddress getMacAddress();

	public Collection<InetAddress> getIpAddress();

	// Add requirement for Iteration order? Latest observed port first.
	public Iterable<Port> getAttachmentPoints();

	public long getLastSeenTime();
}
