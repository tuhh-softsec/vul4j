package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Prefix {
	private int prefixLength;
	private InetAddress address;

	public Prefix(byte[] addr, int prefixLength) throws UnknownHostException {
		//try {
		address = InetAddress.getByAddress(addr);
		//} catch (UnknownHostException e) {
		//	System.out.println("InetAddress exception");
		//	return;
		//}
		this.prefixLength = prefixLength;
		//System.out.println(address.toString() + "/" + prefixLength);
	}

	public Prefix(String str, int prefixLength) throws UnknownHostException {
		//try {
		address = InetAddress.getByName(str);
		//} catch (UnknownHostException e) {
		//	System.out.println("InetAddress exception");
		//	return;
		//}
		this.prefixLength = prefixLength;
	}

	public int getPrefixLength() {
		return prefixLength;
	}
	
	public byte[] getAddress() {
		return address.getAddress();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Prefix)) {
			return false;
		}
		
		Prefix otherPrefix = (Prefix) other;
		
		return (address.equals(otherPrefix.address)) && 
				(prefixLength == otherPrefix.prefixLength);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + prefixLength;
		hash = 31 * hash + (address == null ? 0 : address.hashCode());
		return hash;
	}
	
	@Override
	public String toString() {
		return address.getHostAddress() + "/" + prefixLength;
	}
}
