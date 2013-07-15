package net.onrc.onos.ofcontroller.util;

import java.util.ArrayList;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.FlowPathFlags;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing the Flow Path.
 */
public class FlowPath implements Comparable<FlowPath> {
    private FlowId flowId;		// The Flow ID
    private CallerId installerId;	// The Caller ID of the path installer
    private FlowPathFlags flowPathFlags; // The Flow Path flags
    private DataPath dataPath;		// The data path
    private FlowEntryMatch flowEntryMatch; // Common Flow Entry Match for all
					// Flow Entries

    /**
     * Default constructor.
     */
    public FlowPath() {
	flowPathFlags = new FlowPathFlags();
	dataPath = new DataPath();
    }

    /**
     * Constructor to instantiate from object in Network Map
     */
    public FlowPath(IFlowPath flowObj) {
    	dataPath = new DataPath();
    	this.setFlowId(new FlowId(flowObj.getFlowId()));
    	this.setInstallerId(new CallerId(flowObj.getInstallerId()));
	this.setFlowPathFlags(new FlowPathFlags(flowObj.getFlowPathFlags()));
    	this.dataPath().srcPort().setDpid(new Dpid(flowObj.getSrcSwitch()));
    	this.dataPath().srcPort().setPort(new Port(flowObj.getSrcPort()));
    	this.dataPath().dstPort().setDpid(new Dpid(flowObj.getDstSwitch()));
    	this.dataPath().dstPort().setPort(new Port(flowObj.getDstPort()));
	//
	// Extract the match conditions that are common for all Flow Entries
	//
	{
    	    FlowEntryMatch match = new FlowEntryMatch();
    	    String matchSrcMac = flowObj.getMatchSrcMac();
    	    if (matchSrcMac != null)
    		match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
    	    String matchDstMac = flowObj.getMatchDstMac();
    	    if (matchDstMac != null)
    		match.enableDstMac(MACAddress.valueOf(matchDstMac));
    	    Short matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
    	    if (matchEthernetFrameType != null)
    		match.enableEthernetFrameType(matchEthernetFrameType);
    	    Short matchVlanId = flowObj.getMatchVlanId();
    	    if (matchVlanId != null)
    		match.enableVlanId(matchVlanId);
    	    Byte matchVlanPriority = flowObj.getMatchVlanPriority();
    	    if (matchVlanPriority != null)
    		match.enableVlanPriority(matchVlanPriority);
    	    String matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
    	    if (matchSrcIPv4Net != null)
    		match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
    	    String matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
    	    if (matchDstIPv4Net != null)
    		match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
    	    Byte matchIpProto = flowObj.getMatchIpProto();
    	    if (matchIpProto != null)
    		match.enableIpProto(matchIpProto);
    	    Byte matchIpToS = flowObj.getMatchIpToS();
    	    if (matchIpToS != null)
    		match.enableIpToS(matchIpToS);
    	    Short matchSrcTcpUdpPort = flowObj.getMatchSrcTcpUdpPort();
    	    if (matchSrcTcpUdpPort != null)
    		match.enableSrcTcpUdpPort(matchSrcTcpUdpPort);
    	    Short matchDstTcpUdpPort = flowObj.getMatchDstTcpUdpPort();
    	    if (matchDstTcpUdpPort != null)
    		match.enableDstTcpUdpPort(matchDstTcpUdpPort);

    	    this.setFlowEntryMatch(match);
	}

    	//
    	// Extract all Flow Entries
    	//
    	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
    	for (IFlowEntry flowEntryObj : flowEntries) {
    	    FlowEntry flowEntry = new FlowEntry();
    	    flowEntry.setFlowEntryId(new FlowEntryId(flowEntryObj.getFlowEntryId()));
    	    flowEntry.setDpid(new Dpid(flowEntryObj.getSwitchDpid()));

    	    //
    	    // Extract the match conditions
    	    //
    	    FlowEntryMatch match = new FlowEntryMatch();
    	    Short matchInPort = flowEntryObj.getMatchInPort();
    	    if (matchInPort != null)
    		match.enableInPort(new Port(matchInPort));
    	    String matchSrcMac = flowEntryObj.getMatchSrcMac();
    	    if (matchSrcMac != null)
    		match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
    	    String matchDstMac = flowEntryObj.getMatchDstMac();
    	    if (matchDstMac != null)
    		match.enableDstMac(MACAddress.valueOf(matchDstMac));
    	    Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
    	    if (matchEthernetFrameType != null)
    		match.enableEthernetFrameType(matchEthernetFrameType);
    	    Short matchVlanId = flowEntryObj.getMatchVlanId();
    	    if (matchVlanId != null)
    		match.enableVlanId(matchVlanId);
    	    Byte matchVlanPriority = flowEntryObj.getMatchVlanPriority();
    	    if (matchVlanPriority != null)
    		match.enableVlanPriority(matchVlanPriority);
    	    String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
    	    if (matchSrcIPv4Net != null)
    		match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
    	    String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
    	    if (matchDstIPv4Net != null)
    		match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
    	    Byte matchIpProto = flowEntryObj.getMatchIpProto();
    	    if (matchIpProto != null)
    		match.enableIpProto(matchIpProto);
    	    Byte matchIpToS = flowEntryObj.getMatchIpToS();
    	    if (matchIpToS != null)
    		match.enableIpToS(matchIpToS);
    	    Short matchSrcTcpUdpPort = flowEntryObj.getMatchSrcTcpUdpPort();
    	    if (matchSrcTcpUdpPort != null)
    		match.enableSrcTcpUdpPort(matchSrcTcpUdpPort);
    	    Short matchDstTcpUdpPort = flowEntryObj.getMatchDstTcpUdpPort();
    	    if (matchDstTcpUdpPort != null)
    		match.enableDstTcpUdpPort(matchDstTcpUdpPort);
    	    flowEntry.setFlowEntryMatch(match);

    	    //
    	    // Extract the actions
    	    //
    	    ArrayList<FlowEntryAction> actions = new ArrayList<FlowEntryAction>();
    	    Short actionOutputPort = flowEntryObj.getActionOutputPort();
    	    if (actionOutputPort != null) {
    		FlowEntryAction action = new FlowEntryAction();
    		action.setActionOutput(new Port(actionOutputPort));
    		actions.add(action);
    	    }
    	    flowEntry.setFlowEntryActions(actions);

    	    String userState = flowEntryObj.getUserState();
    	    flowEntry.setFlowEntryUserState(FlowEntryUserState.valueOf(userState));
    	    String switchState = flowEntryObj.getSwitchState();
    	    flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.valueOf(switchState));
    	    //
    	    // TODO: Take care of the FlowEntryMatch, FlowEntryAction set,
    	    // and FlowEntryErrorState.
    	    //
    	    this.dataPath().flowEntries().add(flowEntry);
    	}
    }

    /**
     * Get the flow path Flow ID.
     *
     * @return the flow path Flow ID.
     */
    @JsonProperty("flowId")
    public FlowId flowId() { return flowId; }

    /**
     * Set the flow path Flow ID.
     *
     * @param flowId the flow path Flow ID to set.
     */
    @JsonProperty("flowId")
    public void setFlowId(FlowId flowId) {
	this.flowId = flowId;
    }

    /**
     * Get the Caller ID of the flow path installer.
     *
     * @return the Caller ID of the flow path installer.
     */
    @JsonProperty("installerId")
    public CallerId installerId() { return installerId; }

    /**
     * Set the Caller ID of the flow path installer.
     *
     * @param installerId the Caller ID of the flow path installer.
     */
    @JsonProperty("installerId")
    public void setInstallerId(CallerId installerId) {
	this.installerId = installerId;
    }

    /**
     * Get the flow path flags.
     *
     * @return the flow path flags.
     */
    @JsonProperty("flowPathFlags")
    public FlowPathFlags flowPathFlags() { return flowPathFlags; }

    /**
     * Set the flow path flags.
     *
     * @param flowPathFlags the flow path flags to set.
     */
    @JsonProperty("flowPathFlags")
    public void setFlowPathFlags(FlowPathFlags flowPathFlags) {
	this.flowPathFlags = flowPathFlags;
    }

    /**
     * Get the flow path's data path.
     *
     * @return the flow path's data path.
     */
    @JsonProperty("dataPath")
    public DataPath dataPath() { return dataPath; }

    /**
     * Set the flow path's data path.
     *
     * @param dataPath the flow path's data path to set.
     */
    @JsonProperty("dataPath")
    public void setDataPath(DataPath dataPath) {
	this.dataPath = dataPath;
    }

    /**
     * Get the flow path's match conditions common for all Flow Entries.
     *
     * @return the flow path's match conditions common for all Flow Entries.
     */
    @JsonProperty("flowEntryMatch")
    public FlowEntryMatch flowEntryMatch() { return flowEntryMatch; }

    /**
     * Set the flow path's match conditions common for all Flow Entries.
     *
     * @param flowEntryMatch the flow path's match conditions common for all
     * Flow Entries.
     */
    @JsonProperty("flowEntryMatch")
    public void setFlowEntryMatch(FlowEntryMatch flowEntryMatch) {
	this.flowEntryMatch = flowEntryMatch;
    }

    /**
     * Convert the flow path to a string.
     *
     * The string has the following form:
     *  [flowId=XXX installerId=XXX flowPathFlags=XXX dataPath=XXX]
     *
     * @return the flow path as a string.
     */
    @Override
    public String toString() {
	String ret = "[flowId=" + this.flowId.toString();
	ret += " installerId=" + this.installerId.toString();
	ret += " flowPathFlags=" + this.flowPathFlags.toString();
	if (dataPath != null)
	    ret += " dataPath=" + this.dataPath.toString();
	if (flowEntryMatch != null)
	    ret += " flowEntryMatch=" + this.flowEntryMatch.toString();
	ret += "]";
	return ret;
    }
    
    /**
     * CompareTo method to order flowPath by Id
     */
    @Override
    public int compareTo(FlowPath f) {
    	return (int) (this.flowId.value() - f.flowId.value());
    }

}
