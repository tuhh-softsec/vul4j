package net.floodlightcontroller.util;

import net.floodlightcontroller.util.IPv4;
import net.floodlightcontroller.util.serializers.IPv4NetSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing an IPv4 network address.
 */
@JsonSerialize(using=IPv4NetSerializer.class)
public class IPv4Net {
    private IPv4 address;		// The IPv4 address
    private short prefixLen;		// The prefix length

    /**
     * Default constructor.
     */
    public IPv4Net() {
	this.prefixLen = 0;
    }

    /**
     * Constructor for a given address and prefix length.
     *
     * @param address the address to use.
     * @param prefixLen the prefix length to use.
     */
    public IPv4Net(IPv4 address, short prefixLen) {
	this.address = address;
	this.prefixLen = prefixLen;
    }

    /**
     * Get the address value of the IPv4Net address.
     *
     * @return the address value of the IPv4Net address.
     */
    public IPv4 address() { return address; }

    /**
     * Get the prefix length value of the IPv4Net address.
     *
     * @return the prefix length value of the IPv4Net address.
     */
    public short prefixLen() { return prefixLen; }

    /**
     * Set the value of the IPv4Net address.
     *
     * @param address the address to use.
     * @param prefixLen the prefix length to use.
     */
    public void setValue(IPv4 address, short prefixLen) {
	this.address = address;
	this.prefixLen = prefixLen;
    }

    /**
     * Convert the IPv4Net value to an "address/prefixLen" string.
     *
     * @return the IPv4Net value as an "address/prefixLen" string.
     */
    @Override
    public String toString() {
	return this.address.toString() + "/" + this.prefixLen;
    }
}
