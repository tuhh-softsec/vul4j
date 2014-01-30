package net.onrc.onos.ofcontroller.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedFlow extends Flow {
	public double bandwidth;
	
	/**
	 * 
	 * @param srcPort
	 * @param dstPort
	 * @param bandwidth
	 */
	public ConstrainedFlow(NetworkGraph graph, String name, SwitchPort srcPort, SwitchPort dstPort, double bandwidth) {
		super(graph, name, srcPort, dstPort);
		this.bandwidth = bandwidth;
	}
	
	/**
	 * calculate available bandwidth of specified link	
	 * @param link
	 * @return
	 */
	protected Double getAvailableBandwidth(Link link) {
		Double capacity = link.getCapacity();
		if (capacity.isInfinite()) {
			return capacity;
		}
		Double bandwidth = capacity;
		for (Flow flow: link.getFlows()) {
			if (flow instanceof ConstrainedFlow) {
				bandwidth -= ((ConstrainedFlow)flow).getBandwidth();
			}
		}
		return bandwidth;
	}

	public Double getBandwidth() {
		return bandwidth;
	}

	/**
	 * calculate path by creating BFS tree satisfying the bandwidth condition
	 */
	@Override
	public boolean calcPath() {
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
				return super.calcPath();
			}
			for (Link link: sw.getAdjLinks()) {
				Switch reachedSwitch = link.getDstPort().getSwitch();
				Double availableBandwidth = getAvailableBandwidth(link);
				if (availableBandwidth < bandwidth || switchSearched.contains(reachedSwitch)) continue;
				switchQueue.add(reachedSwitch);
				switchSearched.add(reachedSwitch);
				upstreamLinks.put(reachedSwitch, link);
			}
		}
		state = FlowState.PathCalcurationFailed;
		return false;
	}
}
