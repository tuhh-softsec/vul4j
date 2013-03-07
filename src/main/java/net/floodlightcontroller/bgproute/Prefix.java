package net.floodlightcontroller.bgproute;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Prefix {
	public int masklen;
	protected InetAddress address;

	Prefix(byte[] addr, int masklen) {
		try {
			address = InetAddress.getByAddress(addr);
		} catch (UnknownHostException e) {
			System.out.println("InetAddress exception");
			return;
		}
		this.masklen = masklen;
		System.out.println(address.toString() + "/" + masklen);
	}
	
	Prefix(String str, int masklen) {
		try {
			address = InetAddress.getByName(str);
			//System.out.println(address.toString());
		} catch (UnknownHostException e) {
			System.out.println("InetAddress exception");
			return;
		}
		this.masklen = masklen;
	}
	
	public byte [] getAddress() {
		return address.getAddress();
	}
}
