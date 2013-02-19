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
     * @param flowPath the return-by-reference flow path.
     * @return true on success, otherwise false.
     */
    boolean getFlow(FlowId flowId, FlowPath flowPath);

    /**
     * Get a previously added flow by a specific installer for given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @param flowPath the return-by-reference flow path.
     * @return true on success, otherwise false.
     */
    boolean getFlow(CallerId installerId,
		    DataPathEndpoints dataPathEndpoints,
		    FlowPath flowPath);

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @param flowPaths the return-by-reference list of flows.
     */
    void getAllFlows(DataPathEndpoints dataPathEndpoints,
		     ArrayList<FlowPath> flowPaths);

    /**
     * Get all installed flows by all installers.
     *
     * @param flowPaths the return-by-reference list of flows.
     */
    void getAllFlows(ArrayList<FlowPath> flowPaths);
}
