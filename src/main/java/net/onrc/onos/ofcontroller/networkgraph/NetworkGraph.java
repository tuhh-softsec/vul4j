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
	public Switch getSwitch(Long dpid);
	public Iterable<Switch> getSwitches();

	// TODO Not sure about the use-case of this method? Remove if not used at the end.
	public Iterable<Link> getLinks();
	// XXX next 2 method can be removed. getSwitch(dpid).getOutgoingLinks() is equivalent
	public Iterable<Link> getOutgoingLinksFromSwitch(Long dpid); // Toshi: unnecessary
	public Iterable<Link> getIncomingLinksFromSwitch(Long dpid); // Toshi: unnecessary

	public Iterable<Device> getDeviceByIp(InetAddress ipAddress);
	public Device getDeviceByMac(MACAddress address);
}
