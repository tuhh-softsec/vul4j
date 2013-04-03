package net.onrc.onos.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.FlowPath;

public class FlowManagerImpl implements IFlowManager {

	@Override
	public void createFlow(IPortObject src_port, IPortObject dest_port) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<FlowPath> getFlows(IPortObject src_port,
			IPortObject dest_port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<FlowPath> getOutFlows(IPortObject port) {
		// TODO Auto-generated method stub
		List<FlowPath> flowPaths = new ArrayList<FlowPath> ();
		Iterable<IFlowEntry> flowEntries = port.getOutFlowEntries();

		for(IFlowEntry fe: flowEntries) {
			IFlowPath flow = fe.getFlow();
			FlowPath flowPath = new FlowPath(flow);
			flowPaths.add(flowPath);
		}
		return flowPaths;
	}

	@Override
	public void reconcileFlows(IPortObject src_port) {
		// TODO Auto-generated method stub

		Iterable<IFlowEntry> flowEntries = src_port.getOutFlowEntries();

		for(IFlowEntry fe: flowEntries) {
			IFlowPath flow = fe.getFlow();
			reconcileFlow(flow);
		}
	}

	private void reconcileFlow(IFlowPath flow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconcileFlow(IPortObject src_port, IPortObject dest_port) {
		// TODO Auto-generated method stub

	}

	@Override
	public FlowPath computeFlowPath(IPortObject src_port, IPortObject dest_port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<FlowEntry> getFlowEntries(FlowPath flow) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean installRemoteFlowEntry(FlowPath flowPath,
					      FlowEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeRemoteFlowEntry(FlowPath flowPath,
					     FlowEntry entry) {
		return false;
		// TODO Auto-generated method stub

	}

	@Override
	public boolean installFlowEntry(IOFSwitch mySwitch,
					FlowPath flowPath,
					FlowEntry flowEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeFlowEntry(IOFSwitch mySwitch,
				       FlowPath flowPath,
				       FlowEntry flowEntry) {
		// TODO Auto-generated method stub
		return false;
	}

}
