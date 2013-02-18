package net.floodlightcontroller.util;

/**
 * The class representing a network switch DPID.
 */
public class Dpid {
    static public long UNKNOWN = 0;

    private long value;

    /**
     * Default constructor.
     */
    public Dpid() {
	this.value = Dpid.UNKNOWN;
    }

    /**
     * Constructor from a long value.
     *
     * @param value the value to use.
     */
    public Dpid(long value) {
	this.value = value;
    }

    /**
     * Get the value of the DPID.
     *
     * @return the value of the DPID.
     */
    public long value() { return value; }

    /**
     * Set the value of the DPID.
     *
     * @param value the value to set.
     */
    public void setValue(long value) {
	this.value = value;
    }

    /**
     * Convert the DPID value to a ':' separated hex string.
     *
     * @return the DPID value as a ':' separated hex string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
