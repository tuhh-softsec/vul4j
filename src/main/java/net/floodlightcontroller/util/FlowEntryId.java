package net.floodlightcontroller.util;

import net.floodlightcontroller.util.serializers.FlowEntryIdDeserializer;
import net.floodlightcontroller.util.serializers.FlowEntryIdSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a Flow Entry ID.
 */
@JsonDeserialize(using=FlowEntryIdDeserializer.class)
@JsonSerialize(using=FlowEntryIdSerializer.class)
public class FlowEntryId {
    private long value;

    /**
     * Default constructor.
     */
    public FlowEntryId() {
	this.value = 0;
    }

    /**
     * Constructor from an integer value.
     *
     * @param value the value to use.
     */
    public FlowEntryId(long value) {
	this.value = value;
    }

    /**
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public FlowEntryId(String value) {
	this.value = Long.decode(value);
    }

    /**
     * Get the value of the Flow Entry ID.
     *
     * @return the value of the Flow Entry ID.
     */
    public long value() { return value; }

    /**
     * Set the value of the Flow Entry ID.
     *
     * @param value the value to set.
     */
    public void setValue(long value) {
	this.value = value;
    }

    /**
     * Convert the Flow Entry ID value to a hexadecimal string.
     *
     * @return the Flow Entry ID value to a hexadecimal string.
     */
    @Override
    public String toString() {
	return Long.toHexString(this.value);
    }
}
