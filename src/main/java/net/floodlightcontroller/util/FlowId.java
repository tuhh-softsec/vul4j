package net.floodlightcontroller.util;

/**
 * The class representing a Flow ID.
 */
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
     * Convert the Flow ID value to a string.
     *
     * @return the Flow ID value to a string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
