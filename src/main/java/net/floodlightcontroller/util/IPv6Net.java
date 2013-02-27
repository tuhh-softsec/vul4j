package net.floodlightcontroller.util;

import net.floodlightcontroller.util.IPv6;
import net.floodlightcontroller.util.serializers.IPv6NetDeserializer;
import net.floodlightcontroller.util.serializers.IPv6NetSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing an IPv6 network address.
 */
@JsonDeserialize(using=IPv6NetDeserializer.class)
@JsonSerialize(using=IPv6NetSerializer.class)
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
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public IPv6Net(String value) {
	String[] splits = value.split("/");
	if (splits.length != 2) {
	    throw new IllegalArgumentException("Specified IPv6Net address must contain an IPv6 " +
					       "address and a prefix length separated by '/'");
	}
	this.address = new IPv6(splits[0]);
	this.prefixLen = Short.decode(splits[1]);
    }

    /**
     * Get the address value of the IPv6Net address.
     *
     * @return the address value of the IPv6Net address.
     */
    public IPv6 address() { return address; }

    /**
     * Set the address value of the IPv6Net address.
     *
     * @param address the address to use.
     */
    public void setAddress(IPv6 address) {
	this.address = address;
    }

    /**
     * Get the prefix length value of the IPv6Net address.
     *
     * @return the prefix length value of the IPv6Net address.
     */
    public short prefixLen() { return prefixLen; }

    /**
     * Set the prefix length value of the IPv6Net address.
     *
     * @param prefixLen the prefix length to use.
     */
    public void setPrefixLen(short prefixLen) {
	this.prefixLen = prefixLen;
    }

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
	return this.address.toString() + "/" + this.prefixLen;
    }
}
