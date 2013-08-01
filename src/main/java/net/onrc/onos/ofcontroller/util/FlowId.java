package net.onrc.onos.ofcontroller.util;

import java.math.BigInteger;

import net.onrc.onos.ofcontroller.util.serializers.FlowIdDeserializer;
import net.onrc.onos.ofcontroller.util.serializers.FlowIdSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a Flow ID.
 */
@JsonDeserialize(using=FlowIdDeserializer.class)
@JsonSerialize(using=FlowIdSerializer.class)
public class FlowId implements Comparable<FlowId> {
    private long value;

    /**
     * Default constructor.
     */
    public FlowId() {
	this.value = 0;
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
	if (value.length() > 2)
	    c = value.charAt(1);
	if ((c == 'x') || (c == 'X'))
	    this.value = new BigInteger(value.substring(2), 16).longValue();
	else
	    this.value = Long.decode(value);
    }

    /**
     * Get the value of the Flow ID.
     *
     * @return the value of the Flow ID.
     */
    public long value() { return value; }

    /**
     * Set the value of the Flow ID.
     *
     * @param value the value to set.
     */
    public void setValue(long value) {
	this.value = value;
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
     *         a value less than 0 if the Flow ID is numerically less than the argument's Flow ID;
     *         and a value greater than 0 if the Flow ID is numerically greater than the argument's Flow ID.
     */
 	@Override
	public int compareTo(FlowId o) {
		return Long.valueOf(this.value).compareTo(o.value());
	}
}
