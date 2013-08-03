package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

/*
 * A path is always assumed to be from all other interfaces (external-facing
 * switchports) to the destination interface.
 */

public class PathUpdate {

	private Interface dstInterface;
	private InetAddress dstIpAddress;
	
	public PathUpdate(Interface dstInterface, InetAddress dstIpAddress) {
		this.dstInterface = dstInterface;
		this.dstIpAddress = dstIpAddress;
	}

	public Interface getDstInterface() {
		return dstInterface;
	}

	public InetAddress getDstIpAddress() {
		return dstIpAddress;
	}
}
