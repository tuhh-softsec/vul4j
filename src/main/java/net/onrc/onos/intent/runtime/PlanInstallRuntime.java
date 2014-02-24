package net.onrc.onos.intent.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.intent.FlowEntry;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
//import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class PlanInstallRuntime {
//    NetworkGraph graph;
    IFlowPusherService pusher;
    IFloodlightProviderService provider;
    private final static Logger log = LoggerFactory.getLogger(PlanInstallRuntime.class);

    public PlanInstallRuntime(//NetworkGraph graph, 
	    		      IFloodlightProviderService provider,
	                      IFlowPusherService pusher) {
//	this.graph = graph;
	this.provider = provider;
	this.pusher = pusher;
    }

    public boolean installPlan(List<Set<FlowEntry>> plan) {
	long start = System.nanoTime();
	Map<Long,IOFSwitch> switches = provider.getSwitches();
	log.debug("IOFSwitches: {}", switches);
	for(Set<FlowEntry> phase : plan) {
	    Set<Pair<IOFSwitch, net.onrc.onos.ofcontroller.util.FlowEntry>> entries = new HashSet<>();
	    Set<IOFSwitch> modifiedSwitches = new HashSet<>();

	    // convert flow entries and create pairs
	    for(FlowEntry entry : phase) {
		IOFSwitch sw = switches.get(entry.getSwitch());
		if(sw == null) {
		    // no active switch, skip this flow entry
		    log.debug("Skipping flow entry: {}", entry);
		    continue;
		}
		entries.add(new Pair<>(sw, entry.getFlowEntry()));
		modifiedSwitches.add(sw);
	    }
	    
	    // push flow entries to switches
	    log.debug("Pushing flow entries: {}", entries);
	    pusher.pushFlowEntries(entries);
	    
	    // TODO: insert a barrier after each phase on each modifiedSwitch
	    // TODO: wait for confirmation messages before proceeding
	}
	long end = System.nanoTime();
	log.error("MEASUREMENT: Install plan: {} ns", (end-start));
	// TODO: we assume that the plan installation succeeds for now
	return true;
    }

}
