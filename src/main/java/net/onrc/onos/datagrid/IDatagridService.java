package net.onrc.onos.datagrid;

import java.util.Collection;

import net.floodlightcontroller.core.module.IFloodlightService;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

/**
 * Interface for providing Datagrid Service to other modules.
 */
public interface IDatagridService extends IFloodlightService {
    /**
     * Register Flow Service for receiving Flow-related notifications.
     *
     * NOTE: Only a single Flow Service can be registered.
     *
     * @param flowService the Flow Service to register.
     */
    void registerFlowService(IFlowService flowService);

    /**
     * De-register Flow Service for receiving Flow-related notifications.
     *
     * NOTE: Only a single Flow Service can be registered.
     *
     * @param flowService the Flow Service to de-register.
     */
    void deregisterFlowService(IFlowService flowService);

    /**
     * Get all Flows that are currently in the datagrid.
     *
     * @return all Flows that are currently in the datagrid.
     */
    Collection<FlowPath> getAllFlows();

    /**
     * Send a notification that a Flow is added.
     *
     * @param flowPath the flow that is added.
     */
    void notificationSendFlowAdded(FlowPath flowPath);

    /**
     * Send a notification that a Flow is removed.
     *
     * @param flowId the Flow ID of the flow that is removed.
     */
    void notificationSendFlowRemoved(FlowId flowId);

    /**
     * Send a notification that a Flow is updated.
     *
     * @param flowPath the flow that is updated.
     */
    void notificationSendFlowUpdated(FlowPath flowPath);

    /**
     * Send a notification that all Flows are removed.
     */
    void notificationSendAllFlowsRemoved();

    /**
     * Get all Topology Elements that are currently in the datagrid.
     *
     * @return all Topology Elements that are currently in the datagrid.
     */
    Collection<TopologyElement> getAllTopologyElements();

    /**
     * Send a notification that a Topology Element is added.
     *
     * @param topologyElement the Topology Element that is added.
     */
    void notificationSendTopologyElementAdded(TopologyElement topologyElement);

    /**
     * Send a notification that a Topology Element is removed.
     *
     * @param topologyElement the Topology Element that is removed.
     */
    void notificationSendTopologyElementRemoved(TopologyElement topologyElement);

    /**
     * Send a notification that a Topology Element is updated.
     *
     * @param topologyElement the Topology Element that is updated.
     */
    void notificationSendTopologyElementUpdated(TopologyElement topologyElement);

    /**
     * Send a notification that all Topology Elements are removed.
     */
    void notificationSendAllTopologyElementsRemoved();
}
