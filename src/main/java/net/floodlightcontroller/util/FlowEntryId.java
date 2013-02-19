package net.floodlightcontroller.util;

/**
 * The class representing a Flow Entry ID.
 */
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
     * Convert the Flow Entry ID value to a string.
     *
     * @return the Flow Entry ID value to a string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
