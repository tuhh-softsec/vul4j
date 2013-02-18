package net.floodlightcontroller.util;

import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.FlowEntryActions;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowEntryMatch;
import net.floodlightcontroller.util.Port;

/**
 * The Flow Entry state as set by the user (via the ONOS API).
 */
enum FlowEntryUserState {
	FE_USER_UNKNOWN,		// Initialization value: state unknown
	FE_USER_ADD,			// Flow entry that is added
	FE_USER_MODIFY,			// Flow entry that is modified
	FE_USER_DELETE			// Flow entry that is deleted
}

/**
 * The Flow Entry state as set by the controller.
 */
enum FlowEntrySwitchState {
	FE_SWITCH_UNKNOWN,		// Initialization value: state unknown
	FE_SWITCH_NOT_UPDATED,		// Switch not updated with this entry
	FE_SWITCH_UPDATE_IN_PROGRESS,	// Switch update in progress
	FE_SWITCH_UPDATED,		// Switch updated with this entry
	FE_SWITCH_UPDATE_FAILED	// Error updating the switch with this entry
}


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
    public FlowEntryId flowEntryId() { return flowEntryId; }

    /**
     * Set the Flow Entry ID.
     *
     * @param flowEntryId the Flow Entry ID to set.
     */
    public void setFlowEntryId(FlowEntryId flowEntryId) {
	this.flowEntryId = flowEntryId;
    }

    /**
     * Get the Flow Entry Match.
     *
     * @return the Flow Entry Match.
     */
    public FlowEntryMatch flowEntryMatch() { return flowEntryMatch; }

    /**
     * Set the Flow Entry Match.
     *
     * @param flowEntryMatch the Flow Entry Match to set.
     */
    public void setFlowEntryMatch(FlowEntryMatch flowEntryMatch) {
	this.flowEntryMatch = flowEntryMatch;
    }

    /**
     * Get the Flow Entry Actions.
     *
     * @return the Flow Entry Actions.
     */
    public FlowEntryActions flowEntryActions() { return flowEntryActions; }

    /**
     * Set the Flow Entry Actions.
     *
     * @param flowEntryActions the Flow Entry Actions to set.
     */
    public void setFlowEntryActions(FlowEntryActions flowEntryActions) {
	this.flowEntryActions = flowEntryActions;
    }

    /**
     * Get the Switch DPID.
     *
     * @return the Switch DPID.
     */
    public Dpid dpid() { return dpid; }

    /**
     * Set the Switch DPID.
     *
     * @param dpid the Switch DPID to set.
     */
    public void setDpid(Dpid dpid) {
	this.dpid = dpid;
    }

    /**
     * Get the Switch incoming port.
     *
     * @return the Switch incoming port.
     */
    public Port inPort() { return inPort; }

    /**
     * Set the Switch incoming port.
     *
     * @param inPort the Switch incoming port to set.
     */
    public void setInPort(Port inPort) {
	this.inPort = inPort;
    }

    /**
     * Get the Switch outgoing port.
     *
     * @return the Switch outgoing port.
     */
    public Port outPort() { return outPort; }

    /**
     * Set the Switch outgoing port.
     *
     * @param outPort the Switch outgoing port to set.
     */
    public void setOutPort(Port outPort) {
	this.outPort = outPort;
    }

    /**
     * Get the Flow Entry User state.
     *
     * @return the Flow Entry User state.
     */
    public FlowEntryUserState flowEntryUserState() {
	return flowEntryUserState;
    }

    /**
     * Set the Flow Entry User state.
     *
     * @param flowEntryUserState the Flow Entry User state to set.
     */
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
    public void setFlowEntrySwitchState(FlowEntrySwitchState flowEntrySwitchState) {
	this.flowEntrySwitchState = flowEntrySwitchState;
    }

    /**
     * Get the Flow Entry Error state.
     *
     * @return the Flow Entry Error state.
     */
    public FlowEntryErrorState flowEntryErrorState() {
	return flowEntryErrorState;
    }

    /**
     * Set the Flow Entry Error state.
     *
     * @param flowEntryErrorState the Flow Entry Error state to set.
     */
    public void setFlowEntryErrorState(FlowEntryErrorState flowEntryErrorState) {
	this.flowEntryErrorState = flowEntryErrorState;
    }

    /**
     * Convert the flow entry to a string.
     *
     * @return the flow entry as a string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
