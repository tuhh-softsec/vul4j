package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.util.MACAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a basic ARP cache which maps IPv4 addresses to MAC addresses.
 * Mappings time out after a short period of time (currently 1 min). We don't
 * try and refresh the mapping before the entry times out because as a controller
 * we don't know if the mapping is still needed.
 *
 */

/* TODO clean out old ARP entries out of the cache periodically. We currently 
 * don't do this which means the cache memory size will never decrease. We already
 * have a periodic thread that can be used to do this in ProxyArpManager.
 */
public class ArpCache {
	private final static Logger log = LoggerFactory.getLogger(ArpCache.class);
	
	private final long ARP_ENTRY_TIMEOUT = 60000; //ms (1 min)
	
	//Protected by locking on the ArpCache object
	private final Map<InetAddress, ArpCacheEntry> arpCache;
	
	private static class ArpCacheEntry {
		private final MACAddress macAddress;
		private long timeLastSeen;	

		public ArpCacheEntry(MACAddress macAddress) {
			this.macAddress = macAddress;
			this.timeLastSeen = System.currentTimeMillis();
		}

		public MACAddress getMacAddress() {
			return macAddress;
		}

		public long getTimeLastSeen() {
			return timeLastSeen;
		}
		
		public void setTimeLastSeen(long time){
			timeLastSeen = time;
		}
	}

	public ArpCache() {
		arpCache = new HashMap<InetAddress, ArpCacheEntry>();
	}

	public synchronized MACAddress lookup(InetAddress ipAddress){	
		ArpCacheEntry arpEntry = arpCache.get(ipAddress);
		
		if (arpEntry == null){
			return null;
		}
		
		if (System.currentTimeMillis() - arpEntry.getTimeLastSeen() 
				> ARP_ENTRY_TIMEOUT){
			//Entry has timed out so we'll remove it and return null
			log.trace("Removing expired ARP entry for {}", ipAddress.getHostAddress());
			
			arpCache.remove(ipAddress);
			return null;
		}
		
		return arpEntry.getMacAddress();
	}

	public synchronized void update(InetAddress ipAddress, MACAddress macAddress){
		ArpCacheEntry arpEntry = arpCache.get(ipAddress);
		
		if (arpEntry != null && arpEntry.getMacAddress().equals(macAddress)){
			arpEntry.setTimeLastSeen(System.currentTimeMillis());
		}
		else {
			arpCache.put(ipAddress, new ArpCacheEntry(macAddress));
		}
	}
}
