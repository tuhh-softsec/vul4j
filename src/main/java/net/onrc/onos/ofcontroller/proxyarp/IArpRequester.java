package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

/**
 * Callback interface for modules using the {@link IProxyArpService} to
 * send ARP requests.
 *
 */
public interface IArpRequester {
	/**
	 * Callback method that will be called by the {@link IProxyArpService} 
	 * when it receives a reply for a request previously submitted by this
	 * {@code IArpRequester}.
	 * @param ipAddress The IP address than an ARP request was sent for
	 * @param macAddress The MAC address mapped to the requested IP address
	 */
	public void arpResponse(InetAddress ipAddress, MACAddress macAddress);
}
