package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.CallerId;
import net.onrc.onos.ofcontroller.util.DataPathEndpoints;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

/**
 * @short Interface for providing Flow Service to other modules.
 */
public interface IFlowService extends IFloodlightService {
    /**
     * Add a flow.
     *
     * Internally, ONOS will automatically register the installer for
     * receiving Flow Path Notifications for that path.
     *
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @param dataPathSummaryStr the data path summary string if the added
     * flow will be maintained internally, otherwise null.
     * @return true on success, otherwise false.
     */
    boolean addFlow(FlowPath flowPath, FlowId flowId,
		    String dataPathSummaryStr);

    /**
     * Delete all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    boolean deleteAllFlows();

    /**
     * Delete a previously added flow.
     *
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    boolean deleteFlow(FlowId flowId);

    /**
     * Clear the state for all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    boolean clearAllFlows();

    /**
     * Clear the state for a previously added flow.
     *
     * @param flowId the Flow ID of the flow to clear.
     * @return true on success, otherwise false.
     */
    boolean clearFlow(FlowId flowId);

    /**
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    FlowPath getFlow(FlowId flowId);

    /**
     * Get all previously added flows by a specific installer for a given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlows(CallerId installerId,
				 DataPathEndpoints dataPathEndpoints);

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlows(DataPathEndpoints dataPathEndpoints);

    /**
     * Get summary of all installed flows by all installers.
     *
     * @param flowId: starting flow Id of the range
     * @param maxFlows: number of flows to return
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<IFlowPath> getAllFlowsSummary(FlowId flowId, int maxFlows);
    
    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlows();

    /**
     * Add and maintain a shortest-path flow.
     *
     * NOTE: The Flow Path argument does NOT contain all flow entries.
     * Instead, it contains a single dummy flow entry that is used to
     * store the matching condition(s).
     * That entry is replaced by the appropriate entries from the
     * internally performed shortest-path computation.
     *
     * @param flowPath the Flow Path with the endpoints and the match
     * conditions to install.
     * @return the added shortest-path flow on success, otherwise null.
     */
    public FlowPath addAndMaintainShortestPathFlow(FlowPath flowPath);

    /**
     * Store a path flow for measurement purpose.
     *
     * NOTE: The Flow Path argument does NOT contain flow entries.
     *
     * @param flowPath the Flow Path with the endpoints and the match
     * conditions to store.
     * @return the stored shortest-path flow on success, otherwise null.
     */
    public FlowPath measurementStorePathFlow(FlowPath flowPath);

    /**
     * Install path flows for measurement purpose.
     *
     * @param numThreads the number of threads to use to install the path
     * flows.
     * @return true on success, otherwise false.
     */
    public boolean measurementInstallPaths(Integer numThreads);

    /**
     * Get the measurement time that took to install the path flows.
     *
     * @return the measurement time (in nanoseconds) it took to install
     * the path flows.
     */
    public Long measurementGetInstallPathsTimeNsec();

    /**
     * Get the measurement install time per Flow.
     *
     * @return a multi-line string with the following format per line:
     * ThreadAndTimePerFlow <ThreadId> <TotalThreads> <Time(ns)>
     */
    public String measurementGetPerFlowInstallTime();

    /**
     * Clear the path flows stored for measurement purpose.
     *
     * @return true on success, otherwise false.
     */
    public boolean measurementClearAllPaths();
}
