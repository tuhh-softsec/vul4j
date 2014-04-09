package net.onrc.onos.core.util;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing the Flow Path.
 */
public class FlowPath implements Comparable<FlowPath> {
    public static final int PRIORITY_DEFAULT = 32768;    // Default Flow Priority

    private FlowId flowId;        // The Flow ID
    private CallerId installerId;    // The Caller ID of the path installer
    private FlowPathType flowPathType;    // The Flow Path type
    private FlowPathUserState flowPathUserState; // The Flow Path User state
    private FlowPathFlags flowPathFlags; // The Flow Path flags
    private int idleTimeout;    // The Flow idle timeout
    private int hardTimeout;    // The Flow hard timeout
    private int priority;    // The Flow priority
    private DataPath dataPath;    // The data path
    private FlowEntryMatch flowEntryMatch; // Common Flow Entry Match for all
    // Flow Entries
    private FlowEntryActions flowEntryActions; // The Flow Entry Actions for
    // the first Flow Entry

    /**
     * Default constructor.
     */
    public FlowPath() {
        flowPathType = FlowPathType.FP_TYPE_UNKNOWN;
        flowPathUserState = FlowPathUserState.FP_USER_UNKNOWN;
        flowPathFlags = new FlowPathFlags();
        priority = FlowPath.PRIORITY_DEFAULT;
        dataPath = new DataPath();
        flowEntryActions = new FlowEntryActions();
    }

    /**
     * Get the flow path Flow ID.
     *
     * @return the flow path Flow ID.
     */
    @JsonProperty("flowId")
    public FlowId flowId() {
        return flowId;
    }

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
     * Test whether the Flow ID is valid.
     *
     * @return true if the Flow ID is valid, otherwise false.
     */
    @JsonIgnore
    public boolean isValidFlowId() {
        if (this.flowId == null)
            return false;
        return (this.flowId.isValid());
    }

    /**
     * Get the Caller ID of the flow path installer.
     *
     * @return the Caller ID of the flow path installer.
     */
    @JsonProperty("installerId")
    public CallerId installerId() {
        return installerId;
    }

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
     * Get the flow path type.
     *
     * @return the flow path type.
     */
    @JsonProperty("flowPathType")
    public FlowPathType flowPathType() {
        return flowPathType;
    }

    /**
     * Set the flow path type.
     *
     * @param flowPathType the flow path type to set.
     */
    @JsonProperty("flowPathType")
    public void setFlowPathType(FlowPathType flowPathType) {
        this.flowPathType = flowPathType;
    }

    /**
     * Get the flow path user state.
     *
     * @return the flow path user state.
     */
    @JsonProperty("flowPathUserState")
    public FlowPathUserState flowPathUserState() {
        return flowPathUserState;
    }

    /**
     * Set the flow path user state.
     *
     * @param flowPathUserState the flow path user state to set.
     */
    @JsonProperty("flowPathUserState")
    public void setFlowPathUserState(FlowPathUserState flowPathUserState) {
        this.flowPathUserState = flowPathUserState;
    }

    /**
     * Get the flow path flags.
     *
     * @return the flow path flags.
     */
    @JsonProperty("flowPathFlags")
    public FlowPathFlags flowPathFlags() {
        return flowPathFlags;
    }

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
     * Get the flow idle timeout in seconds.
     * <p/>
     * It should be an unsigned integer in the interval [0, 65535].
     * If zero, the timeout is not set.
     *
     * @return the flow idle timeout.
     */
    @JsonProperty("idleTimeout")
    public int idleTimeout() {
        return idleTimeout;
    }

    /**
     * Set the flow idle timeout in seconds.
     * <p/>
     * It should be an unsigned integer in the interval [0, 65535].
     * If zero, the timeout is not set.
     *
     * @param idleTimeout the flow idle timeout to set.
     */
    @JsonProperty("idleTimeout")
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = 0xffff & idleTimeout;
    }

    /**
     * Get the flow hard timeout in seconds.
     * <p/>
     * It should be an unsigned integer in the interval [0, 65535].
     * If zero, the timeout is not set.
     *
     * @return the flow hard timeout.
     */
    @JsonProperty("hardTimeout")
    public int hardTimeout() {
        return hardTimeout;
    }

    /**
     * Set the flow hard timeout.
     * <p/>
     * It should be an unsigned integer in the interval [0, 65535].
     * If zero, the timeout is not set.
     *
     * @param hardTimeout the flow hard timeout to set.
     */
    @JsonProperty("hardTimeout")
    public void setHardTimeout(int hardTimeout) {
        this.hardTimeout = 0xffff & hardTimeout;
    }

    /**
     * Get the flow priority.
     * <p/>
     * It should be an unsigned integer in the interval [0, 65535].
     *
     * @return the flow priority.
     */
    @JsonProperty("priority")
    public int priority() {
        return priority;
    }

    /**
     * Set the flow priority.
     * <p/>
     * It should be an unsigned integer in the interval [0, 65535].
     *
     * @param priority the flow priority to set.
     */
    @JsonProperty("priority")
    public void setPriority(int priority) {
        this.priority = 0xffff & priority;
    }

    /**
     * Get the flow path's data path.
     *
     * @return the flow path's data path.
     */
    @JsonProperty("dataPath")
    public DataPath dataPath() {
        return dataPath;
    }

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
     * Get the data path flow entries.
     *
     * @return the data path flow entries.
     */
    public ArrayList<FlowEntry> flowEntries() {
        return this.dataPath.flowEntries();
    }

    /**
     * Get the flow path's match conditions common for all Flow Entries.
     *
     * @return the flow path's match conditions common for all Flow Entries.
     */
    @JsonProperty("flowEntryMatch")
    public FlowEntryMatch flowEntryMatch() {
        return flowEntryMatch;
    }

    /**
     * Set the flow path's match conditions common for all Flow Entries.
     *
     * @param flowEntryMatch the flow path's match conditions common for all
     *                       Flow Entries.
     */
    @JsonProperty("flowEntryMatch")
    public void setFlowEntryMatch(FlowEntryMatch flowEntryMatch) {
        this.flowEntryMatch = flowEntryMatch;
    }

    /**
     * Get the flow path's flow entry actions for the first Flow Entry.
     *
     * @return the flow path's flow entry actions for the first Flow Entry.
     */
    @JsonProperty("flowEntryActions")
    public FlowEntryActions flowEntryActions() {
        return flowEntryActions;
    }

    /**
     * Set the flow path's flow entry actions for the first Flow Entry.
     *
     * @param flowEntryActions the flow path's flow entry actions for the first
     *                         Flow Entry.
     */
    @JsonProperty("flowEntryActions")
    public void setFlowEntryActions(FlowEntryActions flowEntryActions) {
        this.flowEntryActions = flowEntryActions;
    }

    /**
     * Convert the flow path to a string.
     * <p/>
     * The string has the following form:
     * [flowId=XXX installerId=XXX flowPathType = XXX flowPathUserState = XXX
     * flowPathFlags=XXX idleTimeout=XXX hardTimeout=XXX priority=XXX
     * dataPath=XXX flowEntryMatch=XXX flowEntryActions=XXX]
     *
     * @return the flow path as a string.
     */
    @Override
    public String toString() {
        String ret = "[flowId=" + this.flowId.toString();
        ret += " installerId=" + this.installerId.toString();
        ret += " flowPathType=" + this.flowPathType;
        ret += " flowPathUserState=" + this.flowPathUserState;
        ret += " flowPathFlags=" + this.flowPathFlags.toString();
        ret += " idleTimeout=" + this.idleTimeout;
        ret += " hardTimeout=" + this.hardTimeout;
        ret += " priority=" + this.priority;
        if (dataPath != null)
            ret += " dataPath=" + this.dataPath.toString();
        if (flowEntryMatch != null)
            ret += " flowEntryMatch=" + this.flowEntryMatch.toString();
        if (flowEntryActions != null)
            ret += " flowEntryActions=" + this.flowEntryActions.toString();
        ret += "]";
        return ret;
    }

    /**
     * Compares this object with the specified object for order.
     * NOTE: The test is based on the Flow ID.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(FlowPath f) {
        return (this.flowId.compareTo(f.flowId()));
    }

    /**
     * Test whether some other object is "equal to" this one.
     * NOTE: The test is based on the Flow ID.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false
     * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlowPath) {
            FlowPath other = (FlowPath) obj;
            return (this.flowId.equals(other.flowId()));
        }
        return false;
    }

    /**
     * Get the hash code for the object.
     * NOTE: The computation is based on the Flow ID.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return this.flowId.hashCode();
    }
}
