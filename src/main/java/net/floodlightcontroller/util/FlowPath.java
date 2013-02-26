package net.floodlightcontroller.util;

import net.floodlightcontroller.util.CallerId;
import net.floodlightcontroller.util.DataPath;
import net.floodlightcontroller.util.FlowId;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing the Flow Path.
 */
public class FlowPath {
    private FlowId flowId;		// The Flow ID
    private CallerId installerId;	// The Caller ID of the path installer
    private DataPath dataPath;		// The data path

    /**
     * Default constructor.
     */
    public FlowPath() {
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
     * Convert the flow path to a string.
     *
     * The string has the following form:
     *  [flowId=XXX installerId=XXX dataPath=XXX]
     *
     * @return the flow path as a string.
     */
    @Override
    public String toString() {
	String ret = "[flowId=" + this.flowId.toString();
	ret += " installerId=" + this.installerId.toString();
	ret += " dataPath=" + this.dataPath.toString();
	ret += "]";
	return ret;
    }
}
