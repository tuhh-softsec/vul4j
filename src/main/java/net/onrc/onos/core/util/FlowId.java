package net.onrc.onos.core.util;

import java.math.BigInteger;

import net.onrc.onos.core.util.serializers.FlowIdDeserializer;
import net.onrc.onos.core.util.serializers.FlowIdSerializer;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a Flow ID.
 * This class is immutable.
 */
@JsonDeserialize(using = FlowIdDeserializer.class)
@JsonSerialize(using = FlowIdSerializer.class)
public final class FlowId implements Comparable<FlowId> {
    private static final long INVALID = -1;
    private final long value;

    /**
     * Default constructor.
     */
    public FlowId() {
        this.value = FlowId.INVALID;
    }

    /**
     * Constructor from an integer value.
     *
     * @param value the value to use.
     */
    public FlowId(long value) {
        this.value = value;
    }

    /**
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public FlowId(String value) {
        //
        // Use the help of BigInteger to parse strings representing
        // large unsigned hex long values.
        //
        char c = 0;
        if (value.length() > 2) {
            c = value.charAt(1);
        }
        if ((c == 'x') || (c == 'X')) {
            this.value = new BigInteger(value.substring(2), 16).longValue();
        } else {
            this.value = Long.decode(value);
        }
    }

    /**
     * Get the value of the Flow ID.
     *
     * @return the value of the Flow ID.
     */
    public long value() {
        return value;
    }

    /**
     * Test whether the Flow ID is valid.
     *
     * @return true if the Flow ID is valid, otherwise false.
     */
    @JsonIgnore
    public boolean isValid() {
        return (this.value() != FlowId.INVALID);
    }

    /**
     * Convert the Flow ID value to a hexadecimal string.
     *
     * @return the Flow ID value to a hexadecimal string.
     */
    @Override
    public String toString() {
        return "0x" + Long.toHexString(this.value);
    }

    /**
     * Compare two FlowId objects numerically using their Flow IDs.
     *
     * @return the value 0 if the Flow ID is equal to the argument's Flow ID;
     * a value less than 0 if the Flow ID is numerically less than the
     * argument's Flow ID; and a value greater than 0 if the Flow ID is
     * numerically greater than the argument's Flow ID.
     */
    @Override
    public int compareTo(FlowId o) {
        return Long.valueOf(this.value).compareTo(o.value());
    }

    /**
     * Test whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false
     * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlowId) {
            FlowId other = (FlowId) obj;
            return (this.value == other.value);
        }
        return false;
    }

    /**
     * Get the hash code for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Long.valueOf(this.value).hashCode();
    }
}
