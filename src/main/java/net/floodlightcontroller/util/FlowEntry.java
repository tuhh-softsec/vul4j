package net.floodlightcontroller.util;

import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.FlowEntryActions;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowEntryMatch;
import net.floodlightcontroller.util.FlowEntrySwitchState;
import net.floodlightcontroller.util.FlowEntryUserState;
import net.floodlightcontroller.util.Port;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing the Flow Entry.
 *
 * NOTE: The specification is incomplete. E.g., the entry needs to
 * support multiple in-ports and multiple out-ports.
 */
public class FlowEntry {
    private FlowEntryId flowEntryId;		// The Flow Entry ID
    private FlowEntryMatch flowEntryMatch;	// The Flow Entry Match
    private FlowEntryActions flowEntryActions;	// The Flow Entry Actions
    private Dpid dpid;				// The Switch DPID
    private Port inPort;			// The Switch incoming port
    private Port outPort;			// The Switch outgoing port
    private FlowEntryUserState flowEntryUserState; // The Flow Entry User state
    private FlowEntrySwitchState flowEntrySwitchState; // The Flow Entry Switch state
    // The Flow Entry Error state (if FlowEntrySwitchState is FE_SWITCH_FAILED)
    private FlowEntryErrorState flowEntryErrorState;

    /**
     * Default constructor.
     */
    public FlowEntry() {
	flowEntryUserState = FlowEntryUserState.FE_USER_UNKNOWN;
	flowEntrySwitchState = FlowEntrySwitchState.FE_SWITCH_UNKNOWN;
    }

    /**
     * Get the Flow Entry ID.
     *
     * @return the Flow Entry ID.
     */
    @JsonProperty("flowEntryId")
    public FlowEntryId flowEntryId() { return flowEntryId; }

    /**
     * Set the Flow Entry ID.
     *
     * @param flowEntryId the Flow Entry ID to set.
     */
    @JsonProperty("flowEntryId")
    public void setFlowEntryId(FlowEntryId flowEntryId) {
	this.flowEntryId = flowEntryId;
    }

    /**
     * Get the Flow Entry Match.
     *
     * @return the Flow Entry Match.
     */
    @JsonProperty("flowEntryMatch")
    public FlowEntryMatch flowEntryMatch() { return flowEntryMatch; }

    /**
     * Set the Flow Entry Match.
     *
     * @param flowEntryMatch the Flow Entry Match to set.
     */
    @JsonProperty("flowEntryMatch")
    public void setFlowEntryMatch(FlowEntryMatch flowEntryMatch) {
	this.flowEntryMatch = flowEntryMatch;
    }

    /**
     * Get the Flow Entry Actions.
     *
     * @return the Flow Entry Actions.
     */
    @JsonProperty("flowEntryActions")
    public FlowEntryActions flowEntryActions() { return flowEntryActions; }

    /**
     * Set the Flow Entry Actions.
     *
     * @param flowEntryActions the Flow Entry Actions to set.
     */
    @JsonProperty("flowEntryActions")
    public void setFlowEntryActions(FlowEntryActions flowEntryActions) {
	this.flowEntryActions = flowEntryActions;
    }

    /**
     * Get the Switch DPID.
     *
     * @return the Switch DPID.
     */
    @JsonProperty("dpid")
    public Dpid dpid() { return dpid; }

    /**
     * Set the Switch DPID.
     *
     * @param dpid the Switch DPID to set.
     */
    @JsonProperty("dpid")
    public void setDpid(Dpid dpid) {
	this.dpid = dpid;
    }

    /**
     * Get the Switch incoming port.
     *
     * @return the Switch incoming port.
     */
    @JsonProperty("inPort")
    public Port inPort() { return inPort; }

    /**
     * Set the Switch incoming port.
     *
     * @param inPort the Switch incoming port to set.
     */
    @JsonProperty("inPort")
    public void setInPort(Port inPort) {
	this.inPort = inPort;
    }

    /**
     * Get the Switch outgoing port.
     *
     * @return the Switch outgoing port.
     */
    @JsonProperty("outPort")
    public Port outPort() { return outPort; }

    /**
     * Set the Switch outgoing port.
     *
     * @param outPort the Switch outgoing port to set.
     */
    @JsonProperty("outPort")
    public void setOutPort(Port outPort) {
	this.outPort = outPort;
    }

    /**
     * Get the Flow Entry User state.
     *
     * @return the Flow Entry User state.
     */
    @JsonProperty("flowEntryUserState")
    public FlowEntryUserState flowEntryUserState() {
	return flowEntryUserState;
    }

    /**
     * Set the Flow Entry User state.
     *
     * @param flowEntryUserState the Flow Entry User state to set.
     */
    @JsonProperty("flowEntryUserState")
    public void setFlowEntryUserState(FlowEntryUserState flowEntryUserState) {
	this.flowEntryUserState = flowEntryUserState;
    }

    /**
     * Get the Flow Entry Switch state.
     *
     * The Flow Entry Error state is used if FlowEntrySwitchState is
     * FE_SWITCH_FAILED.
     *
     * @return the Flow Entry Switch state.
     */
    @JsonProperty("flowEntrySwitchState")
    public FlowEntrySwitchState flowEntrySwitchState() {
	return flowEntrySwitchState;
    }

    /**
     * Set the Flow Entry Switch state.
     *
     * The Flow Entry Error state is used if FlowEntrySwitchState is
     * FE_SWITCH_FAILED.
     *
     * @param flowEntrySwitchState the Flow Entry Switch state to set.
     */
    @JsonProperty("flowEntrySwitchState")
    public void setFlowEntrySwitchState(FlowEntrySwitchState flowEntrySwitchState) {
	this.flowEntrySwitchState = flowEntrySwitchState;
    }

    /**
     * Get the Flow Entry Error state.
     *
     * @return the Flow Entry Error state.
     */
    @JsonProperty("flowEntryErrorState")
    public FlowEntryErrorState flowEntryErrorState() {
	return flowEntryErrorState;
    }

    /**
     * Set the Flow Entry Error state.
     *
     * @param flowEntryErrorState the Flow Entry Error state to set.
     */
    @JsonProperty("flowEntryErrorState")
    public void setFlowEntryErrorState(FlowEntryErrorState flowEntryErrorState) {
	this.flowEntryErrorState = flowEntryErrorState;
    }

    /**
     * Convert the flow entry to a string.
     *
     * The string has the following form:
     *  [flowEntryId=XXX flowEntryMatch=XXX flowEntryActions=XXX dpid=XXX
     *   inPort=XXX outPort=XXX flowEntryUserState=XXX flowEntrySwitchState=XXX
     *   flowEntryErrorState=XXX]
     * @return the flow entry as a string.
     */
    @Override
    public String toString() {
	String ret = "[flowEntryId=" + this.flowEntryId.toString();
	ret += " flowEntryMatch=" + this.flowEntryMatch.toString();
	ret += " flowEntryActions=" + this.flowEntryActions.toString();
	ret += " dpid=" + this.dpid.toString();
	ret += " inPort=" + this.inPort.toString();
	ret += " outPort=" + this.outPort.toString();
	ret += " flowEntryUserState=" + this.flowEntryUserState;
	ret += " flowEntrySwitchState=" + this.flowEntrySwitchState;
	ret += " flowEntryErrorState=" + this.flowEntryErrorState.toString();
	ret += "]";

	return ret;
    }
}
