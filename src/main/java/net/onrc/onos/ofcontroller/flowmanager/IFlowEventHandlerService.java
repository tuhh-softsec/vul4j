package net.onrc.onos.ofcontroller.flowmanager;

import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

/**
 * Interface for providing Flow Event Handler Service to other modules.
 */
public interface IFlowEventHandlerService {
    /**
     * Receive a notification that a Flow is added.
     *
     * @param flowPath the Flow that is added.
     */
    void notificationRecvFlowAdded(FlowPath flowPath);

    /**
     * Receive a notification that a Flow is removed.
     *
     * @param flowPath the Flow that is removed.
     */
    void notificationRecvFlowRemoved(FlowPath flowPath);

    /**
     * Receive a notification that a Flow is updated.
     *
     * @param flowPath the Flow that is updated.
     */
    void notificationRecvFlowUpdated(FlowPath flowPath);

    /**
     * Receive a notification that a FlowEntry is added.
     *
     * @param flowEntry the FlowEntry that is added.
     */
    void notificationRecvFlowEntryAdded(FlowEntry flowEntry);

    /**
     * Receive a notification that a FlowEntry is removed.
     *
     * @param flowEntry the FlowEntry that is removed.
     */
    void notificationRecvFlowEntryRemoved(FlowEntry flowEntry);

    /**
     * Receive a notification that a FlowEntry is updated.
     *
     * @param flowEntry the FlowEntry that is updated.
     */
    void notificationRecvFlowEntryUpdated(FlowEntry flowEntry);

    /**
     * Receive a notification that a FlowId is added.
     *
     * @param flowId the FlowId that is added.
     */
    void notificationRecvFlowIdAdded(FlowId flowId);

    /**
     * Receive a notification that a FlowId is removed.
     *
     * @param flowId the FlowId that is removed.
     */
    void notificationRecvFlowIdRemoved(FlowId flowId);

    /**
     * Receive a notification that a FlowId is updated.
     *
     * @param flowId the FlowId that is updated.
     */
    void notificationRecvFlowIdUpdated(FlowId flowId);

    /**
     * Receive a notification that a FlowEntryId is added.
     *
     * @param flowEntryId the FlowEntryId that is added.
     * @param dpid the Switch Dpid for the corresponding Flow Entry.
     */
    void notificationRecvFlowEntryIdAdded(FlowEntryId flowEntryId, Dpid dpid);

    /**
     * Receive a notification that a FlowEntryId is removed.
     *
     * @param flowEntryId the FlowEntryId that is removed.
     * @param dpid the Switch Dpid for the corresponding Flow Entry.
     */
    void notificationRecvFlowEntryIdRemoved(FlowEntryId flowEntryId,
					    Dpid dpid);

    /**
     * Receive a notification that a FlowEntryId is updated.
     *
     * @param flowEntryId the FlowEntryId that is updated.
     * @param dpid the Switch Dpid for the corresponding Flow Entry.
     */
    void notificationRecvFlowEntryIdUpdated(FlowEntryId flowEntryId,
					    Dpid dpid);

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
