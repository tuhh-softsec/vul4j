package net.onrc.onos.core.util;

import net.onrc.onos.core.util.serializers.IPv6Deserializer;
import net.onrc.onos.core.util.serializers.IPv6Serializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openflow.util.HexString;

/**
 * The class representing an IPv6 address.
 * This class is immutable.
 */
@JsonDeserialize(using = IPv6Deserializer.class)
@JsonSerialize(using = IPv6Serializer.class)
public final class IPv6 {
    private final long valueHigh;    // The higher (more significant) 64 bits
    private final long valueLow;     // The lower (less significant) 64 bits

    /**
     * Default constructor.
     */
    public IPv6() {
        this.valueHigh = 0;
        this.valueLow = 0;
    }

    /**
     * Copy constructor.
     *
     * @param other the object to copy from.
     */
    public IPv6(IPv6 other) {
        this.valueHigh = other.valueHigh;
        this.valueLow = other.valueLow;
    }

    /**
     * Constructor from integer values.
     *
     * @param valueHigh the higher (more significant) 64 bits of the address.
     * @param valueLow  the lower (less significant) 64 bits of the address.
     */
    public IPv6(long valueHigh, long valueLow) {
        this.valueHigh = valueHigh;
        this.valueLow = valueLow;
    }

    /**
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public IPv6(String value) {
        // TODO: Implement it!
        this.valueHigh = 0;
        this.valueLow = 0;
    }

    /**
     * Get the value of the higher (more significant) 64 bits of the address.
     *
     * @return the value of the higher (more significant) 64 bits of the
     * address.
     */
    public long valueHigh() {
        return valueHigh;
    }

    /**
     * Get the value of the lower (less significant) 64 bits of the address.
     *
     * @return the value of the lower (less significant) 64 bits of the
     * address.
     */
    public long valueLow() {
        return valueLow;
    }

    /**
     * Convert the IPv6 value to a ':' separated string.
     *
     * @return the IPv6 value as a ':' separated string.
     */
    @Override
    public String toString() {
        return HexString.toHexString(this.valueHigh) + ":" +
                HexString.toHexString(this.valueLow);
    }
}
