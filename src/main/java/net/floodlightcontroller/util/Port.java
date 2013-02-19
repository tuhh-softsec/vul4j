package net.floodlightcontroller.util;

/**
 * The class representing a network port of a switch.
 */
public class Port {
    private short value;

    /**
     * Default constructor.
     */
    public Port() {
	this.value = 0;
    }

    /**
     * Constructor from a long value.
     *
     * @param value the value to use.
     */
    public Port(short value) {
	this.value = value;
    }

    /**
     * Get the value of the port.
     *
     * @return the value of the port.
     */
    public short value() { return value; }

    /**
     * Set the value of the port.
     *
     * @param value the value to set.
     */
    public void setValue(short value) {
	this.value = value;
    }

    /**
     * Convert the port value to a string.
     *
     * @return the port value as a string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
