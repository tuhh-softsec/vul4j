package net.floodlightcontroller.util;

import java.util.ArrayList;

import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.FlowEntryAction;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowEntryMatch;
import net.floodlightcontroller.util.FlowEntrySwitchState;
import net.floodlightcontroller.util.FlowEntryUserState;
import net.floodlightcontroller.util.Port;

import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.IPv4;

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
    private ArrayList<FlowEntryAction> flowEntryActions; // The Flow Entry Actions
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
	// TODO: Test code
	/*
	MACAddress mac = MACAddress.valueOf("01:02:03:04:05:06");
	IPv4 ipv4 = new IPv4("1.2.3.4");
	IPv4Net ipv4net = new IPv4Net("5.6.7.0/24");

	flowEntryMatch = new FlowEntryMatch();
	flowEntryMatch.enableInPort(new Port((short)10));
	flowEntryMatch.enableSrcMac(mac);
	flowEntryMatch.enableDstMac(mac);
	flowEntryMatch.enableVlanId((short)20);
	flowEntryMatch.enableVlanPriority((byte)30);
	flowEntryMatch.enableEthernetFrameType((short)40);
	flowEntryMatch.enableIpToS((byte)50);
	flowEntryMatch.enableIpProto((byte)60);
	flowEntryMatch.enableSrcIPv4Net(ipv4net);
	flowEntryMatch.enableDstIPv4Net(ipv4net);
	flowEntryMatch.enableSrcTcpUdpPort((short)70);
	flowEntryMatch.enableDstTcpUdpPort((short)80);

	FlowEntryAction action = null;
	ArrayList<FlowEntryAction> actions = new ArrayList<FlowEntryAction>();

	action = new FlowEntryAction();
	action.setActionOutput(new Port((short)12));
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionOutputToController((short)13);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetVlanId((short)14);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetVlanPriority((byte)15);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionStripVlan(true);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetEthernetSrcAddr(mac);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetEthernetDstAddr(mac);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetIPv4SrcAddr(ipv4);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetIPv4DstAddr(ipv4);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetIpToS((byte)16);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetTcpUdpSrcPort((short)17);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionSetTcpUdpDstPort((short)18);
	actions.add(action);

	action = new FlowEntryAction();
	action.setActionEnqueue(new Port((short)19), 20);
	actions.add(action);

	setFlowEntryActions(actions);
	*/


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
    public ArrayList<FlowEntryAction> flowEntryActions() {
	return flowEntryActions;
    }

    /**
     * Set the Flow Entry Actions.
     *
     * @param flowEntryActions the Flow Entry Actions to set.
     */
    @JsonProperty("flowEntryActions")
    public void setFlowEntryActions(ArrayList<FlowEntryAction> flowEntryActions) {
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
     *  [flowEntryId=XXX flowEntryMatch=XXX flowEntryAction=XXX
     *   flowEntryAction=XXX flowEntryAction=XXX dpid=XXX
     *   inPort=XXX outPort=XXX flowEntryUserState=XXX flowEntrySwitchState=XXX
     *   flowEntryErrorState=XXX]
     * @return the flow entry as a string.
     */
    @Override
    public String toString() {
	String ret = "[flowEntryId=" + this.flowEntryId.toString();
	ret += " flowEntryMatch=" + this.flowEntryMatch.toString();
	for (FlowEntryAction fa : flowEntryActions) {
	    ret += " flowEntryAction=" + fa.toString();
	}
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
