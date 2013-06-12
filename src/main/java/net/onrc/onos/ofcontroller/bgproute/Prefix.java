package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Prefix {
	public int masklen;
	protected InetAddress address;

	public Prefix(byte[] addr, int masklen) throws UnknownHostException {
		//try {
		address = InetAddress.getByAddress(addr);
		//} catch (UnknownHostException e) {
		//	System.out.println("InetAddress exception");
		//	return;
		//}
		this.masklen = masklen;
		//System.out.println(address.toString() + "/" + masklen);
	}

	public Prefix(String str, int masklen) throws UnknownHostException {
		//try {
		address = InetAddress.getByName(str);
		//} catch (UnknownHostException e) {
		//	System.out.println("InetAddress exception");
		//	return;
		//}
		this.masklen = masklen;
	}

	public byte [] getAddress() {
		return address.getAddress();
	}
}
