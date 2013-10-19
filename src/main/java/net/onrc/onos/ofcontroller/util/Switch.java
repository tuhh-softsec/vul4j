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

    private Dpid dpid;			// The DPID of the switch
    private SwitchState state;		// The state of the switch

    /**
     * Default constructor.
     *
     * NOTE: The default state for the switch is INACTIVE.
     */
    public Switch() {
	this.dpid = new Dpid();
	this.state = SwitchState.INACTIVE;
    }

    /**
     * Constructor for a given DPID.
     *
     * NOTE: The state for the switch with a given DPID is ACTIVE.
     *
     * @param dpid the DPID to use.
     */
    public Switch(Dpid dpid) {
	this.dpid = dpid;
	this.state = SwitchState.ACTIVE;
    }

    /**
     * Constructor for a given DPID and Switch State.
     *
     * @param dpid the DPID to use.
     * @param state the Switch State to use.
     */
    public Switch(Dpid dpid, SwitchState state) {
	this.dpid = dpid;
	this.state = state;
    }

    /**
     * Get the DPID.
     *
     * @return the DPID.
     */
    @JsonProperty("dpid")
    public Dpid dpid() { return dpid; }

    /**
     * Set the DPID.
     *
     * @param dpid the DPID to use.
     */
    @JsonProperty("dpid")
    public void setDpid(Dpid dpid) {
	this.dpid = dpid;
    }

    /**
     * Get the state.
     *
     * @return the state.
     */
    @JsonProperty("state")
    public SwitchState state() { return state; }

    /**
     * Set the state.
     *
     * @param state the state to use.
     */
    @JsonProperty("state")
    public void setState(SwitchState state) {
	this.state = state;
    }

    /**
     * Set the Switch State to ACTIVE.
     */
    public void setStateActive() {
	this.state = SwitchState.ACTIVE;
    }

    /**
     * Set the Switch State to INACTIVE.
     */
    public void setStateInactive() {
	this.state = SwitchState.INACTIVE;
    }

    /**
     * Convert the Switch value to a string.
     *
     * The string has the following form:
     *  dpid/state
     *
     * @return the Switch value as a string.
     */
    @Override
    public String toString() {
	return this.dpid.toString() + "/" + this.state.toString();
    }
}
