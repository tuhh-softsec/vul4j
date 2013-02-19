package net.floodlightcontroller.util;

import net.floodlightcontroller.util.IPv6;

/**
 * The class representing an IPv6 network address.
 */
public class IPv6Net {
    private IPv6 address;		// The IPv6 address
    private short prefixLen;		// The prefix length

    /**
     * Default constructor.
     */
    public IPv6Net() {
	this.prefixLen = 0;
    }

    /**
     * Constructor for a given address and prefix length.
     *
     * @param address the address to use.
     * @param prefixLen the prefix length to use.
     */
    public IPv6Net(IPv6 address, short prefixLen) {
	this.address = address;
	this.prefixLen = prefixLen;
    }

    /**
     * Get the address value of the IPv6Net address.
     *
     * @return the address value of the IPv6Net address.
     */
    public IPv6 address() { return address; }

    /**
     * Get the prefix length value of the IPv6Net address.
     *
     * @return the prefix length value of the IPv6Net address.
     */
    public short prefixLen() { return prefixLen; }

    /**
     * Set the value of the IPv6Net address.
     *
     * @param address the address to use.
     * @param prefixLen the prefix length to use.
     */
    public void setValue(IPv6 address, short prefixLen) {
	this.address = address;
	this.prefixLen = prefixLen;
    }

    /**
     * Convert the IPv6Net value to an "address/prefixLen" string.
     *
     * @return the IPv6Net value as an "address/prefixLen" string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
