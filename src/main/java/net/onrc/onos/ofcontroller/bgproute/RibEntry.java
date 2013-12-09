package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import com.google.common.net.InetAddresses;

public class RibEntry {
	private final InetAddress routerId;
	private final InetAddress nextHop;

	/*
	 * Store the sequence number information provided on the update here for
	 * now. I think this *should* really be in the RibUpdate, and we should
	 * store RibUpdates in the Ptrie. But, that's a bigger change to change
	 * what the Ptrie stores.
	 */
	private final long sysUpTime;
	private final long sequenceNum;
	
	/*
	 * Marker for RibEntries where we don't have sequence number info.
	 * The user of this class should make sure they don't check this data
	 * if they don't provide it.
	 */
	private final static long NULL_TIME = -1;
	
	public RibEntry(InetAddress routerId, InetAddress nextHop) {
		this.routerId = routerId;
		this.nextHop = nextHop;
		sequenceNum = NULL_TIME;
		sysUpTime = NULL_TIME;
	}
	
	public RibEntry(String routerId, String nextHop) {
		this.routerId = InetAddresses.forString(routerId);
		this.nextHop = InetAddresses.forString(nextHop);
		sequenceNum = NULL_TIME;
		sysUpTime = NULL_TIME;
	}
	
	public RibEntry(String routerId, String nextHop, long sysUpTime
			, long sequenceNum) {
		this.routerId = InetAddresses.forString(routerId);
		this.nextHop = InetAddresses.forString(nextHop);
		this.sequenceNum = sequenceNum;
		this.sysUpTime = sysUpTime;
	}
	
	public InetAddress getNextHop() {
	    return nextHop;
	}
	
	public long getSysUpTime() {
		return sysUpTime;
	}
	
	public long getSequenceNum() {
		return sequenceNum;
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
