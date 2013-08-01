package net.onrc.onos.ofcontroller.util;

import net.onrc.onos.ofcontroller.util.serializers.IPv4Deserializer;
import net.onrc.onos.ofcontroller.util.serializers.IPv4Serializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing an IPv4 address.
 */
@JsonDeserialize(using=IPv4Deserializer.class)
@JsonSerialize(using=IPv4Serializer.class)
public class IPv4 {
    private int value;

    /**
     * Default constructor.
     */
    public IPv4() {
	this.value = 0;
    }

    /**
     * Copy constructor.
     *
     * @param other the object to copy from.
     */
    public IPv4(IPv4 other) {
	this.value = other.value;
    }

    /**
     * Constructor from an integer value.
     *
     * @param value the value to use.
     */
    public IPv4(int value) {
	this.value = value;
    }

    /**
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public IPv4(String value) {
        String[] splits = value.split("\\.");
        if (splits.length != 4)
            throw new IllegalArgumentException("Specified IPv4 address must contain four " +
					       "numerical digits separated by '.'");

        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result |= Integer.valueOf(splits[i]) << ((3-i)*8);
        }
	this.value = result;
    }

    /**
     * Get the value of the IPv4 address.
     *
     * @return the value of the IPv4 address.
     */
    public int value() { return value; }

    /**
     * Set the value of the IPv4 address.
     *
     * @param value the value to set.
     */
    public void setValue(int value) {
	this.value = value;
    }

    /**
     * Convert the IPv4 value to a '.' separated string.
     *
     * @return the IPv4 value as a '.' separated string.
     */
    @Override
    public String toString() {
	return ((this.value >> 24) & 0xFF) + "." +
	    ((this.value >> 16) & 0xFF) + "." +
	    ((this.value >> 8) & 0xFF) + "." +
	    (this.value & 0xFF);
    }
}
