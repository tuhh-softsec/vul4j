package net.onrc.onos.apps.proxyarp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.util.MACAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * TODO clean out old ARP entries out of the cache periodically. We currently
 * don't do this which means the cache memory size will never decrease. We
 * already have a periodic thread that can be used to do this in
 * ProxyArpManager.
 */

/**
 * Implements a basic ARP cache which maps IPv4 addresses to MAC addresses.
 * Mappings time out after a short period of time (currently 1 min). We don't
 * try and refresh the mapping before the entry times out because as a
 * controller we don't know if the mapping is still needed.
 */
class ArpCache {
    private static final Logger log = LoggerFactory.getLogger(ArpCache.class);

    private static final long ARP_ENTRY_TIMEOUT = 60000; // ms (1 min)

    // Protected by locking on the ArpCache object (this)
    private final Map<InetAddress, ArpCacheEntry> arpCache;

    /**
     * Represents a MAC address entry with a timestamp in the ARP cache.
     * ARP cache entries are considered invalid if their timestamp is older
     * than a timeout value.
     */
    private static class ArpCacheEntry {
        private final MACAddress macAddress;
        private long timeLastSeen;

        /**
         * Class constructor, specifying the MAC address for the entry.
         *
         * @param macAddress MAC address for the entry
         */
        public ArpCacheEntry(MACAddress macAddress) {
            this.macAddress = macAddress;
            this.timeLastSeen = System.currentTimeMillis();
        }

        /**
         * Returns the MAC address this entry represents.
         *
         * @return this entry's MAC address
         */
        public MACAddress getMacAddress() {
            return macAddress;
        }

        /**
         * Update the timestamp for this entry.
         *
         * @param time the new timestamp to update the entry with
         */
        public void setTimeLastSeen(long time) {
            timeLastSeen = time;
        }

        /**
         * Returns whether the entry has timed out or not.
         *
         * @return true if the entry has timed out.
         */
        public boolean isExpired() {
            return System.currentTimeMillis() - timeLastSeen > ARP_ENTRY_TIMEOUT;
        }
    }

    /**
     * Class constructor.
     */
    ArpCache() {
        arpCache = new HashMap<InetAddress, ArpCacheEntry>();
    }

    /**
     * Get the MAC address that is mapped to an IP address in the ARP cache.
     *
     * @param ipAddress the IP address to look up
     * @return the MAC address if found in the cache, null if not
     */
    synchronized MACAddress lookup(InetAddress ipAddress) {
        ArpCacheEntry arpEntry = arpCache.get(ipAddress);

        if (arpEntry == null) {
            return null;
        }

        if (arpEntry.isExpired()) {
            // Entry has timed out so we'll remove it and return null
            log.trace("Removing expired ARP entry for {}",
                    ipAddress.getHostAddress());

            arpCache.remove(ipAddress);
            return null;
        }

        return arpEntry.getMacAddress();
    }

    /**
     * Update an entry in the ARP cache. If the IP to MAC mapping is already
     * in the cache, its timestamp will be updated. If not, the entry will
     * be added with a new timestamp of the current time.
     *
     * @param ipAddress  the IP address that will be mapped in the cache
     * @param macAddress the MAC address that maps to {@code ipAddress}
     */
    synchronized void update(InetAddress ipAddress, MACAddress macAddress) {
        ArpCacheEntry arpEntry = arpCache.get(ipAddress);

        if (arpEntry != null && arpEntry.getMacAddress().equals(macAddress)) {
            arpEntry.setTimeLastSeen(System.currentTimeMillis());
        } else {
            arpCache.put(ipAddress, new ArpCacheEntry(macAddress));
        }
    }

    /**
     * Retrieve a list of all mappings in the ARP cache.
     *
     * @return list of all ARP mappings, formatted as a human-readable string
     */
    synchronized List<String> getMappings() {
        List<String> result = new ArrayList<String>(arpCache.size());

        for (Map.Entry<InetAddress, ArpCacheEntry> entry : arpCache.entrySet()) {
            result.add(entry.getKey().getHostAddress()
                    + " => "
                    + entry.getValue().getMacAddress().toString()
                    + (entry.getValue().isExpired() ? " : EXPIRED" : " : VALID"));
        }

        return result;
    }
}
