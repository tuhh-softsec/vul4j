package net.onrc.onos.ofcontroller.app;

import java.util.Map;

/**
 * Base class for Flow representation
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public abstract class Flow extends NetworkGraphEntity {
	public enum FlowState {
		Created,
		Configuring,
		Configured,
		PathCalculating,
		PathCalculated,
		PathCalculationFailed,
		PathInstalled,
		PathInstallationFailed,
		FlowEntriesCalculating,
		FlowEntriesCalculated,
		FlowEntriesCalculationFailed,
		FlowEntriesInstalling,
		FlowEntriesInstalled,
		FlowEntriesInstallationFailed,
		FlowEntriesRemoving,
		FlowEntriesRemoved,
		FlowEntriesRemovalFailed,
		PathRemoved,
		PathRemovalFailed,
	}

	protected FlowState state = FlowState.Created;
	
	// configurations
	protected SwitchPort srcPort = null;
	protected SwitchPort dstPort = null;
	
	// path
	protected Path path = new Path();
	
	// flow entries
	protected Map<SwitchPort, FlowEntry> flowEntries = null;

	// abstract methods
	abstract boolean calcPath();
	abstract void calcFlowEntries();
	

	public Flow(NetworkGraph graph, String name, SwitchPort srcPort, SwitchPort dstPort) {
		super(graph);
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		state = FlowState.Created;
	}
	
	FlowState getState() {
		return state;
	}
	
	boolean isState(FlowState state) {
		return this.state == state;
	}
		
	public Path getPath() {
		return path;
	}
	
	public boolean installPath() {
		for (Link link: path) {
			link.addFlow(this);
		}
		state = FlowState.PathInstalled;
		return true;
	}
	
	public boolean uninstallPath() {
		for (Link link: path) {
			link.removeFlow(this);
		}
		state = FlowState.PathRemoved;
		return true;
	}

	/**
	 * not implemented yet
	 */
	public void installFlowEntries() {
		state = FlowState.FlowEntriesInstalled;
	}
	
	/**
	 * not implemented yet
	 */
	public void uninstallFlowEntries() {
		state = FlowState.FlowEntriesRemoved;
	}
	
	@Override
	public String toString() {
		return String.format("srcPort:%s, dstPort:%s, Path: %s",
				srcPort.toString(),
				dstPort.toString(),
				path.toString());
	}
}
