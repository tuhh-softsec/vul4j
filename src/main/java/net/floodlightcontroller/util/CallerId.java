package net.floodlightcontroller.util;

import net.floodlightcontroller.util.serializers.CallerIdSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a Caller ID for an ONOS component.
 */
@JsonSerialize(using=CallerIdSerializer.class)
public class CallerId {
    private String value;

    /**
     * Default constructor.
     */
    public CallerId() {}

    /**
     * Constructor from a string value.
     *
     * @param value the value to use.
     */
    public CallerId(String value) {
	this.value = value;
    }

    /**
     * Get the value of the Caller ID.
     *
     * @return the value of the Caller ID.
     */
    public String value() { return value; }

    /**
     * Set the value of the Caller ID.
     *
     * @param value the value to set.
     */
    public void setValue(String value) {
	this.value = value;
    }

    /**
     * Convert the Caller ID value to a string.
     *
     * @return the Caller ID value to a string.
     */
    @Override
    public String toString() {
	return value;
    }
}
