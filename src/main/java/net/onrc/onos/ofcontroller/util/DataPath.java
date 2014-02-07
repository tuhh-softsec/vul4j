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
     * Remove Flow Entries that were deleted.
     */
    public void removeDeletedFlowEntries() {
	//
	// NOTE: We create a new ArrayList, and add only the Flow Entries
	// that are NOT FE_USER_DELETE.
	// This is sub-optimal: if it adds notable processing cost,
	// the Flow Entries container should be changed to LinkedList
	// or some other container that has O(1) cost of removing an entry.
	//

	// Test first whether any Flow Entry was deleted
	boolean foundDeletedFlowEntry = false;
	for (FlowEntry flowEntry : this.flowEntries) {
	    if (flowEntry.flowEntryUserState() ==
		FlowEntryUserState.FE_USER_DELETE) {
		foundDeletedFlowEntry = true;
		break;
	    }
	}
	if (! foundDeletedFlowEntry)
	    return;			// Nothing to do

	// Create a new collection and exclude the deleted flow entries
	ArrayList<FlowEntry> newFlowEntries = new ArrayList<FlowEntry>();
	for (FlowEntry flowEntry : this.flowEntries()) {
	    if (flowEntry.flowEntryUserState() !=
		FlowEntryUserState.FE_USER_DELETE) {
		newFlowEntries.add(flowEntry);
	    }
	}
	setFlowEntries(newFlowEntries);
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
