package net.onrc.onos.ofcontroller.util;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing a Switch.
 * NOTE: Currently this class is (almost) not used.
 */
public class Switch {
    public enum SwitchState {
	INACTIVE,
	ACTIVE,
    }

    private String type;	// The type from the database. TODO: Not needed
    private String state;	// The state of the switch
    private String dpid;	// The DPID of the switch

    /**
     * Default constructor.
     */
    public Switch() {
    }

    /**
     * Get the type.
     *
     * @return the type.
     */
    @JsonProperty("type")
    public String type() { return type; }

    /**
     * Set the type.
     *
     * @param type the type to use.
     */
    @JsonProperty("type")
    public void setType(String type) {
	this.type = type;
    }

    /**
     * Get the state.
     *
     * @return the state.
     */
    @JsonProperty("state")
    public String state() { return state; }

    /**
     * Set the state.
     *
     * @param state the state to use.
     */
    @JsonProperty("state")
    public void setState(String state) {
	this.state = state;
    }

    /**
     * Get the DPID.
     *
     * @return the DPID.
     */
    @JsonProperty("dpid")
    public String dpid() { return dpid; }

    /**
     * Set the DPID.
     *
     * @param dpid the DPID to use.
     */
    @JsonProperty("dpid")
    public void setDpid(String dpid) {
	this.dpid = dpid;
    }

    /**
     * Convert the Switch value to a string.
     *
     * The string has the following form:
     *  type/state/dpid
     *
     * @return the Switch value as a string.
     */
    @Override
    public String toString() {
	return this.type + "/" + this.state + "/" + this.dpid;
    }
}
