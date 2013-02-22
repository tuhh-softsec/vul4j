package net.floodlightcontroller.util;

import net.floodlightcontroller.util.serializers.FlowEntryErrorStateSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing the Flow Entry error state.
 */
@JsonSerialize(using=FlowEntryErrorStateSerializer.class)
public class FlowEntryErrorState {
    private short type;	// The error type (e.g., see OF-1.3.1 spec, pp. 95)
    private short code;	// The error code (e.g., see OF-1.3.1 spec, pp. 95)

    /**
     * Default constructor.
     */
    public FlowEntryErrorState() {
	this.type = 0;
	this.code = 0;
    }

    /**
     * Constructor for a given error type and code.
     *
     * @param type the error type to use.
     * @param code the error code to use.
     */
    public FlowEntryErrorState(short type, short code) {
	this.type = type;
	this.code = code;
    }

    /**
     * Get the error type.
     *
     * @return the error type.
     */
    public short type() { return type; }

    /**
     * Get the error code.
     *
     * @return the error code.
     */
    public short code() { return code; }

    /**
     * Set the values of the error type and code.
     *
     * @param type the error type to use.
     * @param code the error code to use.
     */
    public void setValue(short type, short code) {
	this.type = type;
	this.code = code;
    }

    /**
     * Convert the error type and code to a string.
     *
     * The string has the following form:
     * [type=1 code=2]
     *
     * @return the error type and code as a string.
     */
    @Override
    public String toString() {
	String ret = "[type=" + this.type + " code=" + code + "]";
	return ret;
    }
}
