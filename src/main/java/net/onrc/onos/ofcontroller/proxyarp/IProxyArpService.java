package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;

import net.floodlightcontroller.packet.ARP;

public interface IProxyArpService {
	
	public final int ARP_REQUEST_TIMEOUT = 2000; //ms
	
	/**
	 * Tell the IProxyArpService to send an ARP reply with the targetMac to 
	 * the host on the specified switchport.
	 * @param arpRequest
	 * @param dpid
	 * @param port
	 * @param targetMac
	 */
	public void sendArpReply(ARP arpRequest, long dpid, short port, byte[] targetMac);
	
	/**
	 * Returns the mac address if there is a valid entry in the cache.
	 * Otherwise returns null.
	 * @param ipAddress
	 * @return
	 */
	public byte[] getMacAddress(InetAddress ipAddress);
	
	/**
	 * Tell the IProxyArpService to send an ARP request for the IP address.
	 * The request will be broadcast out all edge ports in the network.
	 * As an optimization, the IProxyArpService will first check its cache and
	 * return the MAC address if it is already known. If not, the request will be
	 * sent and the callback will be called when the MAC address is known
	 * (or if the request times out). 
	 * @param ipAddress
	 * @param requester
	 * @return
	 */
	public byte[] sendArpRequest(InetAddress ipAddress, IArpRequester requester);
}
