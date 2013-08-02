package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

/*
 * A path is always assumed to be from all other interfaces (external-facing
 * switchports) to the destination interface.
 */

public class PathUpdate {

	//private Set<Interface> srcInterfaces;
	private Interface dstInterface;
	private InetAddress dstIpAddress;
	
	public PathUpdate(//Collection<Interface> srcInterfaces, 
			Interface dstInterface, InetAddress dstIpAddress) {
		this.dstInterface = dstInterface;
		this.dstIpAddress = dstIpAddress;

		//this.srcInterfaces = new HashSet<Interface>(srcInterfaces.size());
		//for (Interface intf : srcInterfaces) {
		//	this.srcInterfaces.add(intf);
		//}
	}

	//public Set<Interface> getSrcInterfaces() {
	//	return Collections.unmodifiableSet(srcInterfaces);
	//}

	public Interface getDstInterface() {
		return dstInterface;
	}

	public InetAddress getDstIpAddress() {
		return dstIpAddress;
	}
}
