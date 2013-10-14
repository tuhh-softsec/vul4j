package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import com.google.common.net.InetAddresses;

public class RibEntry {
	private final InetAddress routerId;
	private final InetAddress nextHop;
	
	public RibEntry(InetAddress routerId, InetAddress nextHop) {
		this.routerId = routerId;
		this.nextHop = nextHop;
	}
	
	public RibEntry(String routerId, String nextHop) {
		this.routerId = InetAddresses.forString(routerId);
		this.nextHop = InetAddresses.forString(nextHop);
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
