package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

/**
 * The northbound interface to the topology network graph. This interface
 * is presented to the rest of ONOS. It is currently read-only, as we want
 * only the discovery modules to be allowed to modify the topology.
 */
public interface NetworkGraph {
    /**
     * Get the switch for a given switch DPID.
     *
     * @param dpid the switch dpid.
     * @return the switch if found, otherwise, null.
     */
    public Switch getSwitch(Long dpid);

    /**
     * Get all switches in the network.
     *
     * @return all switches in the network.
     */
    public Iterable<Switch> getSwitches();

    /**
     * Get the port on a switch.
     *
     * @param dpid the switch DPID.
     * @param number the switch port number.
     * @return the switch port if found, otherwise, null.
     */
    public Port getPort(Long dpid, Long number);

    /**
     * Get all links in the network.
     *
     * TODO: Not clear if this method is needed. Remove if not used.
     *
     * @return all links in the network.
     */
    public Iterable<Link> getLinks();

    /**
     * Get all outgoing links for a switch.
     *
     * TODO: Not clear if this method is needed. Remove if not used.
     * E.g, getSwitch(dpid).getOutgoingLinks() is equivalent.
     *
     * @param dpid the switch DPID.
     * @return all outgoing links for a switch.
     */
    public Iterable<Link> getOutgoingLinksFromSwitch(Long dpid);

    /**
     * Get all incoming links for a switch.
     *
     * TODO: Not clear if this method is needed. Remove if not used.
     * E.g, getSwitch(dpid).getIncomingLinks() is equivalent.
     *
     * @param dpid the switch DPID.
     * @return all incoming links for a switch.
     */
    public Iterable<Link> getIncomingLinksFromSwitch(Long dpid);

    /**
     * Get the network devices for a given IP address.
     *
     * @param ipAddress the IP address to use.
     * @return the network devices for the IP address.
     */
    public Iterable<Device> getDevicesByIp(InetAddress ipAddress);

    /**
     * Get the network device for a given MAC address.
     *
     * @param address the MAC address to use.
     * @return the network device for the MAC address if found, otherwise null.
     */
    public Device getDeviceByMac(MACAddress address);
	
    /**
     * Acquire a read lock on the entire topology. The topology will not 
     * change while readers have the lock. Must be released using 
     * {@link releaseLock()}. This method will block until a read lock is
     * available.
     */
	public void acquireLock();
	
	/**
	 * Release the read lock on the topology.
	 */
	public void releaseLock();
}
