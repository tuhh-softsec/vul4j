package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

/**
 * The northbound interface to the topology network graph. This interface
 * is presented to the rest of ONOS. It is currently read-only, as we want
 * only the discovery modules to be allowed to modify the topology.
 *
 */
public interface NetworkGraph {
	public Switch getSwitch(long dpid);
	public Iterable<Switch> getSwitches();
	
	public Iterable<Link> getLinks();
	public Iterable<Link> getLinksFromSwitch(long dpid);
	
	public Iterable<Device> getDeviceByIp(InetAddress ipAddress);
	public Iterable<Device> getDeviceByMac(MACAddress address);
}
