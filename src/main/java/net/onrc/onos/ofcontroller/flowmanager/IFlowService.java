package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.topology.Topology;
import net.onrc.onos.ofcontroller.util.CallerId;
import net.onrc.onos.ofcontroller.util.DataPathEndpoints;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.Pair;

/**
 * Interface for providing Flow Service to other modules.
 */
public interface IFlowService extends IFloodlightService {
    /**
     * Add a flow.
     *
     * @param flowPath the Flow Path to install.
     * @return the Flow ID on success, otherwise null.
     */
    FlowId addFlow(FlowPath flowPath);

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
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    FlowPath getFlow(FlowId flowId);

    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlows();

    /**
     * Get summary of all installed flows by all installers.
     *
     * @param flowId starting flow Id of the range
     * @param maxFlows number of flows to return
     * @return the Flow Paths if found, otherwise null.
     */
    ArrayList<FlowPath> getAllFlowsSummary(FlowId flowId, int maxFlows);

    /**
     * Get the network topology.
     *
     * @return the network topology.
     */
    Topology getTopology();

    /**
     * Get a globally unique flow ID from the flow service.
     * NOTE: Not currently guaranteed to be globally unique.
     * 
     * @return unique flow ID
     */
    public long getNextFlowEntryId();

    /**
     * Inform the Flow Manager that a Flow Entry on switch expired.
     *
     * @param sw the switch the Flow Entry expired on.
     * @param flowEntryId the Flow Entry ID of the expired Flow Entry.
     */
    public void flowEntryOnSwitchExpired(IOFSwitch sw, FlowEntryId flowEntryId);

    /**
     * Inform the Flow Manager that a collection of Flow Entries have been
     * pushed to a switch.
     *
     * @param entries the collection of <IOFSwitch, FlowEntry> pairs
     * that have been pushed.
     */
    public void flowEntriesPushedToSwitch(
			Collection<Pair<IOFSwitch, FlowEntry>> entries);
}
