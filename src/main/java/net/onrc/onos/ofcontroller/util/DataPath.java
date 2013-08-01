package net.onrc.onos.ofcontroller.util;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The data forwarding path state from a source to a destination.
 */
public class DataPath {
    private SwitchPort srcPort;		// The source port
    private SwitchPort dstPort;		// The destination port
    private ArrayList<FlowEntry> flowEntries;	// The Flow Entries

    /**
     * Default constructor.
     */
    public DataPath() {
	srcPort = new SwitchPort();
	dstPort = new SwitchPort();
	flowEntries = new ArrayList<FlowEntry>();
    }

    /**
     * Get the data path source port.
     *
     * @return the data path source port.
     */
    @JsonProperty("srcPort")
    public SwitchPort srcPort() { return srcPort; }

    /**
     * Set the data path source port.
     *
     * @param srcPort the data path source port to set.
     */
    @JsonProperty("srcPort")
    public void setSrcPort(SwitchPort srcPort) {
	this.srcPort = srcPort;
    }

    /**
     * Get the data path destination port.
     *
     * @return the data path destination port.
     */
    @JsonProperty("dstPort")
    public SwitchPort dstPort() { return dstPort; }

    /**
     * Set the data path destination port.
     *
     * @param dstPort the data path destination port to set.
     */
    @JsonProperty("dstPort")
    public void setDstPort(SwitchPort dstPort) {
	this.dstPort = dstPort;
    }

    /**
     * Get the data path flow entries.
     *
     * @return the data path flow entries.
     */
    @JsonProperty("flowEntries")
    public ArrayList<FlowEntry> flowEntries() { return flowEntries; }

    /**
     * Set the data path flow entries.
     *
     * @param flowEntries the data path flow entries to set.
     */
    @JsonProperty("flowEntries")
    public void setFlowEntries(ArrayList<FlowEntry> flowEntries) {
	this.flowEntries = flowEntries;
    }

    /**
     * Apply Flow Path Flags to the pre-computed Data Path.
     *
     * @param flowPathFlags the Flow Path Flags to apply.
     */
    public void applyFlowPathFlags(FlowPathFlags flowPathFlags) {
	if (flowPathFlags == null)
	    return;		// Nothing to do

	// Discard the first Flow Entry
	if (flowPathFlags.isDiscardFirstHopEntry()) {
	    if (flowEntries.size() > 0)
		flowEntries.remove(0);
	}

	// Keep only the first Flow Entry
	if (flowPathFlags.isKeepOnlyFirstHopEntry()) {
	    if (flowEntries.size() > 1) {
		FlowEntry flowEntry = flowEntries.get(0);
		flowEntries.clear();
		flowEntries.add(flowEntry);
	    }
	}
    }

    /**
     * Get a string with the summary of the shortest-path data path
     * computation.
     *
     * NOTE: This method assumes the DataPath was created by
     * using FlowManager::getShortestPath() so the inPort and outPort
     * of the Flow Entries are set.
     * NOTE: This method is a temporary solution and will be removed
     * in the future.
     *
     * @return a string with the summary of the shortest-path
     * data path computation if valid, otherwise the string "X".
     * If the shortest-path was valid, The string has the following form:
     * inPort/dpid/outPort;inPort/dpid/outPort;...
     */
    public String dataPathSummary() {
	String resultStr = new String();
	if (this.flowEntries != null) {
	    for (FlowEntry flowEntry : this.flowEntries) {
		// The data path summary string
		resultStr = resultStr +
		    flowEntry.inPort().toString() + "/"
		    + flowEntry.dpid().toString() + "/" +
		    flowEntry.outPort().toString() + ";";
	    }
	}
	if (resultStr.isEmpty())
	    resultStr = "X";		// Invalid shortest-path
	return resultStr;
    }

    /**
     * Convert the data path to a string.
     *
     * The string has the following form:
     * [src=01:01:01:01:01:01:01:01/1111 flowEntry=<entry1> flowEntry=<entry2> flowEntry=<entry3> dst=02:02:02:02:02:02:02:02/2222]
     *
     * @return the data path as a string.
     */
    @Override
    public String toString() {
	String ret = "[src=" + this.srcPort.toString();

	for (FlowEntry fe : flowEntries) {
	    ret += " flowEntry=" + fe.toString();
	}
	ret += " dst=" + this.dstPort.toString() + "]";

	return ret;
    }
}
