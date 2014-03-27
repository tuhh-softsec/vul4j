package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;
import java.util.List;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.MACAddress;

// Extends IFloodlightService so we can access it from REST API resources
/**
 * Provides ARP services to other modules.
 */
public interface IProxyArpService extends IFloodlightService {
    /**
     * Returns the MAC address if there is a valid entry in the cache. Otherwise
     * returns null.
     *
     * @param ipAddress the IP address to request the ARP mapping for
     * @return the MACAddress that maps to the specified IP address, or null if
     *         no mapping is found
     */
    public MACAddress getMacAddress(InetAddress ipAddress);

    /**
     * Tell the IProxyArpService to send an ARP request for the IP address. The
     * request will be broadcast out all edge ports in the network.
     *
     * @param ipAddress the IP address to send an ARP request for
     * @param requester the {@link IArpRequester} object that will be called if
     *                  a reply is received
     * @param retry whether to keep sending requests until the MAC is learnt
     */
    public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
            boolean retry);

    /**
     * Returns a snapshot of the entire ARP cache.
     *
     * @return a list of mappings formatted as a human-readable string
     */
    public List<String> getMappings();
}
