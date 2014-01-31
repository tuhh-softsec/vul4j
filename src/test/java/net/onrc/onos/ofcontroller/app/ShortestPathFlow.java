package net.onrc.onos.ofcontroller.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ShortestPathFlow extends Flow {

	public ShortestPathFlow(NetworkGraph graph, String name, SwitchPort srcPort, SwitchPort dstPort) {
		super(graph, name, srcPort, dstPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean calcPath() {
		LinkedList<Switch> switchQueue = new LinkedList<Switch>();
		HashSet<Switch> switchSearched = new HashSet<Switch>();
		HashMap<Switch, Link> upstreamLinks = new HashMap<Switch, Link>();
		
		Switch srcSwitch = srcPort.getSwitch();
		Switch dstSwitch = dstPort.getSwitch();
		
		switchQueue.add(srcSwitch);
		switchSearched.add(srcSwitch);

		while (!switchQueue.isEmpty()) {
			Switch sw = switchQueue.poll();
			if (sw == dstSwitch) {
				// path has been searched.
				// store path into itself
				path.clear();
				while (sw != srcSwitch) {
					Link upstreamLink = upstreamLinks.get(sw);
					path.add(0, upstreamLink);
					sw = upstreamLink.getSrcPort().getSwitch();
				}
				state = FlowState.PathCalculated;
				return true;
			}
			for (Link link: sw.getAdjLinks()) {
				Switch reachedSwitch = link.getDstPort().getSwitch();
				if (switchSearched.contains(reachedSwitch)) continue;
				switchQueue.add(reachedSwitch);
				switchSearched.add(reachedSwitch);
				upstreamLinks.put(reachedSwitch, link);
			}
		}
		state = FlowState.PathCalculationFailed;
		return false;
	}

	@Override
	void calcFlowEntries() {
		// TODO Auto-generated method stub
		// not implemented yet
	}

}
