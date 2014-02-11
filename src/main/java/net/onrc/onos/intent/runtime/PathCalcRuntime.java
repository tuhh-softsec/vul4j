package net.onrc.onos.intent.runtime;

import java.util.Collection;
import java.util.HashSet;

import net.onrc.onos.intent.ConstrainedBFSTree;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntents;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathCalcRuntime {
	NetworkGraph graph;
	HashSet<Intent> inputIntents = new HashSet<Intent>();
	PathIntents outputIntents = new PathIntents();

	public PathCalcRuntime(NetworkGraph g) {
		this.graph = g;
	}

	public Collection<Intent> getInputIntents() {
		return inputIntents;
	}

	public PathIntents getOutputIntents() {
		return outputIntents;
	}

	public void addInputIntents(Collection<Intent> inputIntents) {
		this.inputIntents.addAll(inputIntents);
		this.outputIntents = calcPathIntents(inputIntents);
	}

	protected PathIntents calcPathIntents(Collection<Intent> originalIntents) {
		PathIntents pathIntents = new PathIntents();

		for (Intent intent: originalIntents) {
			if (!(intent instanceof ShortestPathIntent)) {
				// unsupported intent type.
				// TODO should push back the intent to caller
				continue;
			}

			ShortestPathIntent spIntent = (ShortestPathIntent) intent;
			Switch srcSwitch = spIntent.getSourcePort().getSwitch();
			Switch dstSwitch = spIntent.getDestinationPort().getSwitch();
			if (srcSwitch == null || dstSwitch == null) {
				// incomplete intent.
				// TODO should push back the intent to caller
				continue;
			}

			Double bandwidth = null;
			ConstrainedBFSTree tree = null;
			if (intent instanceof ConstrainedShortestPathIntent) {
				bandwidth = ((ConstrainedShortestPathIntent) intent).getBandwidth();
				tree = new ConstrainedBFSTree(srcSwitch, pathIntents, bandwidth);
			}
			else {
				tree = new ConstrainedBFSTree(srcSwitch);
			}
			Path path = tree.getPath(dstSwitch);
			if (path == null) {
				// path not found.
				// TODO should push back the intent to caller
				continue;
			}

			pathIntents.addIntent(new PathIntent(path, bandwidth, intent));
		}
		return pathIntents;
	}
}