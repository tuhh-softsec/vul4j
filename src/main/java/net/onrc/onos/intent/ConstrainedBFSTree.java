package net.onrc.onos.intent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

/**
 * This class creates bandwidth constrained breadth first tree
 * and returns paths from root switch to leaf switches
 * which satisfies the bandwidth condition.
 * If bandwidth parameter is not specified, the normal breadth first tree will be calculated.
 * The paths are snapshot paths at the point of the class instantiation.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedBFSTree {
	LinkedList<Switch> switchQueue = new LinkedList<Switch>();
	HashSet<Switch> switchSearched = new HashSet<Switch>();
	HashMap<Switch, Link> upstreamLinks = new HashMap<Switch, Link>();
	HashMap<Switch, Path> paths = new HashMap<Switch, Path>();
	Switch rootSwitch;
	PathIntents intents = null;
	double bandwidth = 0.0; // 0.0 means no limit for bandwidth (normal BFS tree)

	public ConstrainedBFSTree(Switch rootSwitch) {
		this.rootSwitch = rootSwitch;
		calcTree();
	}

	public ConstrainedBFSTree(Switch rootSwitch, PathIntents intents, double bandwidth) {
		this.rootSwitch = rootSwitch;
		this.intents = intents;
		this.bandwidth = bandwidth;
		calcTree();
	}

	protected void calcTree() {
		switchQueue.add(rootSwitch);
		switchSearched.add(rootSwitch);
		while (!switchQueue.isEmpty()) {
			Switch sw = switchQueue.poll();
			for (Link link: sw.getOutgoingLinks()) {
				Switch reachedSwitch = link.getDestinationPort().getSwitch();
				if (switchSearched.contains(reachedSwitch)) continue;
				if (intents != null && intents.getAvailableBandwidth(link) < bandwidth) continue;
				switchQueue.add(reachedSwitch);
				switchSearched.add(reachedSwitch);
				upstreamLinks.put(reachedSwitch, link);
			}
		}
	}

	public Path getPath(Switch leafSwitch) {
		Path path = paths.get(leafSwitch);
		if (path == null && switchSearched.contains(leafSwitch)) {
			path = new Path();
			Switch sw = leafSwitch;
			while (sw != rootSwitch) {
				Link upstreamLink = upstreamLinks.get(sw);
				path.add(0, upstreamLink);
				sw = upstreamLink.getSourcePort().getSwitch();
			}
			paths.put(leafSwitch, path);
		}
		return path;
	}
}
