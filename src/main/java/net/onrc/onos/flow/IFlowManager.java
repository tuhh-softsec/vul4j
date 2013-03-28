package net.onrc.onos.flow;

import java.util.Map;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.FlowPath;

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
    public Iterable<FlowPath> getFlows(IPortObject port);

    /**
     * Reconcile all flows on inactive port (src port of link which might be
     * broken).
     *
     * TODO: We need it now: Pavlin
     *
     * @param src_port the port that has become inactive.
     */
    public void reconcileFlows(IPortObject src_port);

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
     * TODO: We need it now: Pavlin
     *
     * @param src_port the source port.
     * @param dest_port the destination port.
     * @return the computed shortest path between the source and the
     * destination ports.
     */
    public FlowPath computeFlowPath(IPortObject src_port,
				    IPortObject dest_port);

    /**
     * Get all Flow Entries of a Flow.
     *
     * TODO: We need it now: Pavlin
     *
     * @param flow the flow whose flow entries should be returned.
     * @return the flow entries of the flow.
     */
    public Iterable<FlowEntry> getFlowEntries(FlowPath flow);

    /**
     * Install a Flow Entry on a switch.
     *
     * TODO: We need it now: Pavlin
     * - Install only for local switches
     * - It will call the installRemoteFlowEntry() for remote switches.
     * - To be called by reconcileFlow()
     *
     * @param mySwitches the DPID-to-Switch mapping for the switches
     * controlled by this controller.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    public boolean installFlowEntry(Map<Long, IOFSwitch> mySwitches,
				    FlowEntry flowEntry);

    /**
     * Remove a Flow Entry from a switch.
     *
     * TODO: We need it now: Pavlin
     * - Remove only for local switches
     * - It will call the removeRemoteFlowEntry() for remote switches.
     * - To be called by reconcileFlow()
     *
     * @param entry the flow entry to remove.
     */
    public void removeFlowEntry(FlowEntry entry);

    /**
     * Install a Flow Entry on a remote controller.
     *
     * TODO: We need it now: Jono
     * - For now it will make a REST call to the remote controller.
     * - Internally, it needs to know the name of the remote controller.
     *
     * @param entry the flow entry to install.
     * @return true on success, otherwise false.
     */
    public boolean installRemoteFlowEntry(FlowEntry entry);

    /**
     * Remove a flow entry on a remote controller.
     *
     * TODO: We need it now: Jono
     * - For now it will make a REST call to the remote controller.
     * - Internally, it needs to know the name of the remote controller.
     *
     * @param entry the flow entry to remove.
     */
    public void removeRemoteFlowEntry(FlowEntry entry);        
}
