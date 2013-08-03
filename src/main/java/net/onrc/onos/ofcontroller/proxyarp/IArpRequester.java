package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;

public interface IArpRequester {
	public void arpResponse(InetAddress ipAddress, byte[] macAddress);
}
