package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;
import java.util.List;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.MACAddress;

//Extends IFloodlightService so we can access it from REST API resources
public interface IProxyArpService extends IFloodlightService{
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
	
	/**
	 * Returns a snapshot of the entire ARP cache.
	 * @return
	 */
	public List<String> getMappings();
}
