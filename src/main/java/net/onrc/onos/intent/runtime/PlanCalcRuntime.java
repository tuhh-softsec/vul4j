package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.intent.FlowEntry;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntents;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class PlanCalcRuntime {
	NetworkGraph graph;
	protected PathIntents intents;
	protected Set<Collection<FlowEntry>> flowEntries;
	protected List<Set<FlowEntry>> plan;
	
	public PlanCalcRuntime(NetworkGraph graph) {
		this.graph = graph;
		this.flowEntries = new HashSet<>();
		this.plan = new ArrayList<>();
	}
	
	public void addIntents(PathIntents intents) {
		this.intents = intents;
		computeFlowEntries();
		constructPlan();
	}
	
	public List<Set<FlowEntry>> getPlan() {
		return plan;
	}

	public void computeFlowEntries() {
		for(PathIntent intent : intents.getIntents()) {
			Intent parent = intent.getParentIntent();
			Port srcPort, dstPort, lastDstPort = null;
			MACAddress srcMac, dstMac;
			if(parent instanceof ShortestPathIntent) {
				ShortestPathIntent pathIntent = (ShortestPathIntent) parent;
				Switch srcSwitch = graph.getSwitch(pathIntent.getSrcSwitchDpid());
				srcPort = srcSwitch.getPort(pathIntent.getSrcPortNumber());
				srcMac = MACAddress.valueOf(pathIntent.getSrcMac());
				dstMac = MACAddress.valueOf(pathIntent.getDstMac());
				Switch dstSwitch = graph.getSwitch(pathIntent.getDstSwitchDpid());
				lastDstPort = dstSwitch.getPort(pathIntent.getDstPortNumber());
			}
			else {
				// TODO: log this error
				continue;
			}
			List<FlowEntry> entries = new ArrayList<>();
			for(Link link : intent.getPath(graph)) {
				Switch sw = link.getSourceSwitch();
				dstPort = link.getSourcePort();
				FlowEntry fe = new FlowEntry(sw, srcPort, dstPort, srcMac, dstMac);
				entries.add(fe);
				srcPort = link.getDestinationPort();
			}
			if(lastDstPort != null) {
				Switch sw = lastDstPort.getSwitch();
				dstPort = lastDstPort;
				FlowEntry fe = new FlowEntry(sw, srcPort, dstPort, srcMac, dstMac);
				entries.add(fe);
			}
			// install flow entries in reverse order
			Collections.reverse(entries);
			flowEntries.add(entries);
		}
	}
	
	public void constructPlan() {
		Map<FlowEntry, Integer> map = new HashMap<>();
		for(Collection<FlowEntry> c : flowEntries) {
			for(FlowEntry e: c) {
				Integer i = map.get(e);
				if(i == null) {
					map.put(e, 1);
				}
				else {
					i += 1;
				}
				
			}
		}
		
		// really simple first iteration of plan
		//TODO: optimize the map in phases
		plan.add(map.keySet());
	}
}
