package net.floodlightcontroller.util;

import java.util.ArrayList;

import net.floodlightcontroller.util.SwitchPort;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.serializers.DataPathSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing the Data Path.
 */
@JsonSerialize(using=DataPathSerializer.class)
public class DataPath {
    private SwitchPort srcPort;		// The source port
    private SwitchPort dstPort;		// The destination port
    private ArrayList<FlowEntry> flowEntries;	// The Flow Entries

    /**
     * Default constructor.
     */
    public DataPath() {
    }

    /**
     * Get the data path source port.
     *
     * @return the data path source port.
     */
    public SwitchPort srcPort() { return srcPort; }

    /**
     * Set the data path source port.
     *
     * @param srcPort the data path source port to set.
     */
    public void setSrcPort(SwitchPort srcPort) {
	this.srcPort = srcPort;
    }

    /**
     * Get the data path destination port.
     *
     * @return the data path destination port.
     */
    public SwitchPort dstPort() { return dstPort; }

    /**
     * Set the data path destination port.
     *
     * @param dstPort the data path destination port to set.
     */
    public void setDstPort(SwitchPort dstPort) {
	this.dstPort = dstPort;
    }

    /**
     * Get the data path flow entries.
     *
     * @return the data path flow entries.
     */
    public ArrayList<FlowEntry> flowEntries() { return flowEntries; }

    /**
     * Set the data path flow entries.
     *
     * @param flowEntries the data path flow entries to set.
     */
    public void setFlowEntries(ArrayList<FlowEntry> flowEntries) {
	this.flowEntries = flowEntries;
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
