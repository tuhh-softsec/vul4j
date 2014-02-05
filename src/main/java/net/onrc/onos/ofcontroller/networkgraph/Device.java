package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;

import net.floodlightcontroller.util.MACAddress;

/**
 * Interface of Device Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Device {
	public MACAddress getMacAddress();

	public Collection<InetAddress> getIpAddress();

	public Iterable<Port> getAttachmentPoints();

	public long getLastSeenTime();
}
