package net.onrc.onos.ofcontroller.proxyarp;

public interface IArpRequester {
	public void arpResponse(byte[] mac);
}
