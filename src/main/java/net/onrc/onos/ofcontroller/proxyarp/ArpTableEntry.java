package net.onrc.onos.ofcontroller.proxyarp;


public class ArpTableEntry {
	
	private byte[] macAddress;
	private long timeLastSeen;	

	public ArpTableEntry(byte[] macAddress, long timeLastSeen) {
		this.macAddress = macAddress;
		this.timeLastSeen = timeLastSeen;
	}

	public byte[] getMacAddress() {
		return macAddress;
	}

	public long getTimeLastSeen() {
		return timeLastSeen;
	}
	
	public void setTimeLastSeen(long time){
		//TODO thread safety issues?
		timeLastSeen = time;
	}

}
