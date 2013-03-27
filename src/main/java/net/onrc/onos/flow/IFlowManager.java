package net.onrc.onos.flow;

import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.FlowPath;

public interface IFlowManager {
	
	/*
	 * Generic create Flow from port to port
	 */
	public void createFlow(IPortObject src_port, IPortObject dest_port);
	/*
	 * get Flows matching a src_port & dest_port
	 */
	public Iterable<FlowPath> getFlows(IPortObject src_port, IPortObject dest_port);
	/*
	 * get all Flows going out from port
	 */
	public Iterable<FlowPath> getFlows(IPortObject port);
	/*
	 * Reconcile all flows on inactive port (src port of link which might be broken)
	 */
	public void reconcileFlows(IPortObject src_port);
	/*
	 * Reconcile flow based on flow
	 */
	public void reconcileFlow(IPortObject src_port, IPortObject dest_port);
	/*
	 * compute a flow path using src/dest port
	 */
	public FlowPath computeFlowPath(IPortObject src_port, IPortObject dest_port);
	/*
	 * Get all FlowEntries of a Flow
	 */
    public Iterable<FlowEntry> getFlowEntries(FlowPath flow);
    /*
     * install a flow entry on switch
     */
    public void installFlowEntry(FlowEntry entry);
    /*
     * remove a flowEntry from switch
     */
    public void removeFlowEntry(FlowEntry entry);
    /*
     * install flow entry on remote controller
     */
    public void installFlowEntry(String ctrlId, FlowEntry entry);
    /*
     * remove flow entry on remote controller
     */
    public void removeFlowEntry(String ctrlId, FlowEntry entry);        
}
