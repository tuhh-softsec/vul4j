package net.onrc.onos.intent.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.intent.FlowEntry;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class PlanInstallRuntime {
    NetworkGraph graph;
    IFlowPusherService pusher;
    IFloodlightProviderService provider;
    private final static Logger log = LoggerFactory.getLogger(PlanInstallRuntime.class);

    public PlanInstallRuntime(NetworkGraph graph, 
	    		      IFloodlightProviderService provider,
	                      IFlowPusherService pusher) {
	this.graph = graph;
	this.provider = provider;
	this.pusher = pusher;
    }

    public void installPlan(List<Set<FlowEntry>> plan) {
	Map<Long,IOFSwitch> switches = provider.getSwitches();
	log.debug("IOFSwitches: {}", switches);
	for(Set<FlowEntry> phase : plan) {
	    Set<Pair<IOFSwitch, net.onrc.onos.ofcontroller.util.FlowEntry>> entries = new HashSet<>();
	    // convert flow entries and create pairs
	    for(FlowEntry entry : phase) {
		entries.add(new Pair<>(switches.get(entry.getSwitch().getDpid()), 
			entry.getFlowEntry()));
	    }
	    log.debug("Pushing flow entries: {}", entries);
	    // push flow entries to switches
	    pusher.pushFlowEntries(entries);
	    // TODO: wait for confirmation messages before proceeding
	}
    }

}
