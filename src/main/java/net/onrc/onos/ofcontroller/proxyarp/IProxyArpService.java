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
	 * @param ipAddress
	 * @param requester
	 * @param retry Whether to keep sending requests until the MAC is learnt
	 * @return
	 */
	public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
			boolean retry);
}
