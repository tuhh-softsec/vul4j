package net.onrc.onos.ofcontroller.util;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing a network port of a switch.
 */

public class Port {
    /**
     * Special port values.
     *
     * Those values are taken as-is from the OpenFlow-v1.0.0 specification
     * (pp 18-19).
     */
    public enum PortValues {
	/* Maximum number of physical switch ports. */
	PORT_MAX		((short)0xff00),

	/* Fake output "ports". */

	/* Send the packet out the input port. This
	   virtual port must be explicitly used
	   in order to send back out of the input
	   port. */
	PORT_IN_PORT		((short)0xfff8),

	/* Perform actions in flow table.
	   NB: This can only be the destination
	   port for packet-out messages. */
	PORT_TABLE		((short)0xfff9),

	/* Process with normal L2/L3 switching. */
	PORT_NORMAL		((short)0xfffa),

	/* All physical ports except input port and
	   those disabled by STP. */
	PORT_FLOOD		((short)0xfffb),

	/* All physical ports except input port. */
	PORT_ALL		((short)0xfffc),

	/* Send to controller. */
	PORT_CONTROLLER		((short)0xfffd),

	/* Local openflow "port". */
	PORT_LOCAL		((short)0xfffe),

	/* Not associated with a physical port. */
	PORT_NONE		((short)0xffff);

	private final short value;	// The value

	/**
	 * Constructor for a given value.
	 *
	 * @param value the value to use for the initialization.
	 */
	private PortValues(short value) {
	    this.value = value;
	}

	/**
	 * Get the value as a short integer.
	 *
	 * @return the value as a short integer.
	 */
	private short value() { return this.value; }
    }

    private short value;

    /**
     * Default constructor.
     */
    public Port() {
	this.value = 0;
    }

    /**
     * Copy constructor.
     *
     * @param other the object to copy from.
     */
    public Port(Port other) {
	this.value = other.value();
    }

    /**
     * Constructor from a short integer value.
     *
     * @param value the value to use.
     */
    public Port(short value) {
	this.value = value;
    }

    /**
     * Constructor from a PortValues enum value.
     *
     * @param value the value to use.
     */
    public Port(PortValues value) {
	this.value = value.value();
    }

    /**
     * Get the value of the port.
     *
     * @return the value of the port.
     */
    @JsonProperty("value")
    public short value() { return value; }

    /**
     * Set the value of the port.
     *
     * @param value the value to set.
     */
    @JsonProperty("value")
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
	return Short.toString(this.value);
    }
}
