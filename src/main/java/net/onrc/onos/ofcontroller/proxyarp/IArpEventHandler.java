package net.onrc.onos.ofcontroller.proxyarp;

public interface IArpEventHandler {

	/**
	 * Notify the ARP event handler that an ARP request has been received.
	 * @param id The string ID of the ARP request
	 * @param arpRequest The ARP request packet
	 */
	public void arpRequestNotification(byte[] arpRequest);
}
