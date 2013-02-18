package net.floodlightcontroller.util;

import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.Port;

/**
 * The class representing a Switch-Port.
 */
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
     * @return the Switch-Port value as a string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
