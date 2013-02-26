package net.floodlightcontroller.util;

import net.floodlightcontroller.util.serializers.FlowIdDeserializer;
import net.floodlightcontroller.util.serializers.FlowIdSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a Flow ID.
 */
@JsonDeserialize(using=FlowIdDeserializer.class)
@JsonSerialize(using=FlowIdSerializer.class)
public class FlowId {
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
}
