package net.floodlightcontroller.flowcache;

import java.util.ArrayList;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.CallerId;
import net.floodlightcontroller.util.DataPathEndpoints;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;

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
     * @return true on success, otherwise false.
     */
    boolean addFlow(FlowPath flowPath, FlowId flowId);

    /**
     * Delete a previously added flow.
     *
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    boolean deleteFlow(FlowId flowId);

    /**
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    FlowPath getFlow(FlowId flowId);

    /**
     * Get a previously added flow by a specific installer for given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    FlowPath getFlow(CallerId installerId,
		     DataPathEndpoints dataPathEndpoints);

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlows(DataPathEndpoints dataPathEndpoints);

    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlows();
}
