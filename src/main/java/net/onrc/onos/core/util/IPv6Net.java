package net.onrc.onos.core.util;

import net.onrc.onos.core.util.serializers.IPv6NetDeserializer;
import net.onrc.onos.core.util.serializers.IPv6NetSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing an IPv6 network address.
 * This class is immutable.
 */
@JsonDeserialize(using = IPv6NetDeserializer.class)
@JsonSerialize(using = IPv6NetSerializer.class)
public final class IPv6Net {
    private final IPv6 address;         // The IPv6 address
    private final short prefixLen;      // The prefix length

    /**
     * Default constructor.
     */
    public IPv6Net() {
        this.address = null;
        this.prefixLen = 0;
    }

    /**
     * Copy constructor.
     *
     * @param other the object to copy from.
     */
    public IPv6Net(IPv6Net other) {
        if (other.address != null) {
            this.address = new IPv6(other.address);
        } else {
            this.address = null;
        }
        this.prefixLen = other.prefixLen;
    }

    /**
     * Constructor for a given address and prefix length.
     *
     * @param address   the address to use.
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
    public IPv6 address() {
        return address;
    }

    /**
     * Get the prefix length value of the IPv6Net address.
     *
     * @return the prefix length value of the IPv6Net address.
     */
    public short prefixLen() {
        return prefixLen;
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
