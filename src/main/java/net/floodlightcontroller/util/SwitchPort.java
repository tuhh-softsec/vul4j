package net.floodlightcontroller.util;

import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.Port;
import net.floodlightcontroller.util.serializers.SwitchPortSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a Switch-Port.
 */
@JsonSerialize(using=SwitchPortSerializer.class)
public class SwitchPort {
    private Dpid dpid;		// The DPID of the switch
    private Port port;		// The port of the switch

    /**
     * Default constructor.
     */
    public SwitchPort() {
    }

    /**
     * Constructor for a given DPID and a port.
     *
     * @param dpid the DPID to use.
     * @param port the port to use.
     */
    public SwitchPort(Dpid dpid, Port port) {
	this.dpid = dpid;
	this.port = port;
    }

    /**
     * Get the DPID value of the Switch-Port.
     *
     * @return the DPID value of the Switch-Port.
     */
    public Dpid dpid() { return dpid; }

    /**
     * Get the port value of the Switch-Port.
     *
     * @return the port value of the Switch-Port.
     */
    public Port port() { return port; }

    /**
     * Set the DPID and port values of the Switch-Port.
     *
     * @param dpid the DPID to use.
     * @param port the port to use.
     */
    public void setValue(Dpid dpid, Port port) {
	this.dpid = dpid;
	this.port = port;
    }

    /**
     * Convert the Switch-Port value to a string.
     *
     * The string has the following form:
     *  01:02:03:04:05:06:07:08/1234
     *
     * @return the Switch-Port value as a string.
     */
    @Override
    public String toString() {
	return this.dpid.toString() + "/" + this.port;
    }
}
