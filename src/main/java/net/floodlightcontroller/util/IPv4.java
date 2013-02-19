package net.floodlightcontroller.util;

/**
 * The class representing an IPv4 address.
 */
public class IPv4 {
    private int value;

    /**
     * Default constructor.
     */
    public IPv4() {
	this.value = 0;
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
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
