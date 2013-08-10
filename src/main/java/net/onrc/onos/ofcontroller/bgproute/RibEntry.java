package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RibEntry {
	private InetAddress routerId;
	private InetAddress nextHop;
	
	public RibEntry(InetAddress routerId, InetAddress nextHop) {
		this.routerId = routerId;
		this.nextHop = nextHop;
	}
	
	public RibEntry(String routerId, String nextHop) {
		try {
			this.routerId = InetAddress.getByName(routerId);
			this.nextHop = InetAddress.getByName(nextHop);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid address format");
		}
	}
	
	public InetAddress getNextHop() {
	    return nextHop;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof RibEntry)) {
			return false;
		}
		
		RibEntry otherRibEntry = (RibEntry) other;
		
		return this.routerId.equals(otherRibEntry.routerId) 
				&& this.nextHop.equals(otherRibEntry.nextHop);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + routerId.hashCode();
		hash = 31 * hash + nextHop.hashCode();
		return hash;
	}
}
