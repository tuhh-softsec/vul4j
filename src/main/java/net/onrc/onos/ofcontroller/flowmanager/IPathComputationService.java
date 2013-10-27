package net.onrc.onos.ofcontroller.flowmanager;

import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.FlowPath;

/**
 * Interface for providing Path Computation Service to other modules.
 */
public interface IPathComputationService {
    /**
     * Receive a notification that a Flow is added.
     *
     * @param flowPath the flow that is added.
     */
    void notificationRecvFlowAdded(FlowPath flowPath);

    /**
     * Receive a notification that a Flow is removed.
     *
     * @param flowPath the flow that is removed.
     */
    void notificationRecvFlowRemoved(FlowPath flowPath);

    /**
     * Receive a notification that a Flow is updated.
     *
     * @param flowPath the flow that is updated.
     */
    void notificationRecvFlowUpdated(FlowPath flowPath);

    /**
     * Receive a notification that a Topology Element is added.
     *
     * @param topologyElement the Topology Element that is added.
     */
    void notificationRecvTopologyElementAdded(TopologyElement topologyElement);

    /**
     * Receive a notification that a Topology Element is removed.
     *
     * @param topologyElement the Topology Element that is removed.
     */
    void notificationRecvTopologyElementRemoved(TopologyElement topologyElement);

    /**
     * Receive a notification that a Topology Element is updated.
     *
     * @param topologyElement the Topology Element that is updated.
     */
    void notificationRecvTopologyElementUpdated(TopologyElement topologyElement);
}
