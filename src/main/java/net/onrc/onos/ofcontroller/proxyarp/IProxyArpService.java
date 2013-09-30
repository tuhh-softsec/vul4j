package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

public interface IProxyArpService {
	/**
	 * Returns the MAC address if there is a valid entry in the cache.
	 * Otherwise returns null.
	 * @param ipAddress
	 * @return
	 */
	public MACAddress getMacAddress(InetAddress ipAddress);
	
	/**
	 * Tell the IProxyArpService to send an ARP request for the IP address.
	 * The request will be broadcast out all edge ports in the network.
	 * @param ipAddress
	 * @param requester
	 * @param retry Whether to keep sending requests until the MAC is learnt
	 */
	public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
			boolean retry);
}
