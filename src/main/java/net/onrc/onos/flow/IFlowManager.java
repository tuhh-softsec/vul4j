package net.onrc.onos.flow;

import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowPath;

public interface IFlowManager {
    /**
     * Create a Flow from port to port.
     *
     * TODO: We don't need it for now.
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     */
    public void createFlow(IPortObject src_port, IPortObject dest_port);

    /**
     * Get all Flows matching a source and a destination port.
     *
     * TODO: Pankaj might be implementing it later.
     *
     * @param src_port the source port to match.
     * @param dest_port the destination port to match.
     * @return all flows matching the source and the destination port.
     */
    public Iterable<FlowPath> getFlows(IPortObject src_port,
				       IPortObject dest_port);

    /**
     * Get all Flows going out from a port.
     *
     * TODO: We need it now: Pankaj
     *
     * @param port the port to match.
     * @return the list of flows that are going out from the port.
     */
    public Iterable<FlowPath> getOutFlows(IPortObject port);

    /**
     * Reconcile all flows on inactive switch port.
     *
     * @param portObject the port that has become inactive.
     */
    public void reconcileFlows(IPortObject portObject);

    /**
     * Reconcile all flows between a source and a destination port.
     *
     * TODO: We don't need it for now.
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     */
    public void reconcileFlow(IPortObject src_port, IPortObject dest_port);

    /**
     * Compute the shortest path between a source and a destination ports.
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     * @return the computed shortest path between the source and the
     * destination ports. The flow entries in the path itself would
     * contain the incoming port matching and the outgoing port output
     * actions set. However, the path itself will NOT have the Flow ID,
     * Installer ID, and any additional matching conditions for the
     * flow entries (e.g., source or destination MAC address, etc).
     */
    public FlowPath computeFlowPath(IPortObject src_port,
				    IPortObject dest_port);

    /**
     * Get all Flow Entries of a Flow.
     *
     * @param flow the flow whose flow entries should be returned.
     * @return the flow entries of the flow.
     */
    public Iterable<FlowEntry> getFlowEntries(FlowPath flow);

    /**
     * Install a Flow Entry on a switch.
     *
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    public boolean installFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				    FlowEntry flowEntry);

    /**
     * Remove a Flow Entry from a switch.
     *
     * @param mySwitch the switch to remove the Flow Entry from.
     * @param flowPath the flow path for the flow entry to remove.
     * @param flowEntry the flow entry to remove.
     * @return true on success, otherwise false.
     */
    public boolean removeFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				   FlowEntry flowEntry);

    /**
     * Install a Flow Entry on a remote controller.
     *
     * TODO: We need it now: Jono
     * - For now it will make a REST call to the remote controller.
     * - Internally, it needs to know the name of the remote controller.
     *
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    public boolean installRemoteFlowEntry(FlowPath flowPath,
					  FlowEntry flowEntry);

    /**
     * Remove a flow entry on a remote controller.
     *
     * @param flowPath the flow path for the flow entry to remove.
     * @param flowEntry the flow entry to remove.
     * @return true on success, otherwise false.
     */
    public boolean removeRemoteFlowEntry(FlowPath flowPath,
					 FlowEntry flowEntry);
}
