package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;

import net.floodlightcontroller.util.MACAddress;

public interface Device {
	public MACAddress getMacAddress();
	
	public Collection<InetAddress> getIpAddress();
	
	public Iterable<Port> getAttachmentPoints();
	
	public long getLastSeenTime();
}
