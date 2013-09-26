package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

/**
 * Provides information about the layer 3 properties of the network.
 * This is based on IP addresses configured on ports in the network.
 *
 */
public interface ILayer3InfoService {
	public boolean isInterfaceAddress(InetAddress address);
	public boolean inConnectedNetwork(InetAddress address);
	public boolean fromExternalNetwork(long inDpid, short inPort);
	
	/**
	 * Retrieves the {@link Interface} object for the interface that packets
	 * to dstIpAddress will be sent out of. Returns null if dstIpAddress is not
	 * in a directly connected network, or if no interfaces are configured.
	 * @param dstIpAddress Destination IP address that we want to match to
	 * an outgoing interface
	 * @return The {@link Interface} object if found, null if not
	 */
	public Interface getOutgoingInterface(InetAddress dstIpAddress);
	
	/**
	 * Returns whether this controller has a layer 3 configuration 
	 * (i.e. interfaces and IP addresses)
	 * @return True if IP addresses are configured, false if not
	 */
	public boolean hasLayer3Configuration();
	
	public MACAddress getRouterMacAddress();
}
