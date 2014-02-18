package net.onrc.onos.intent.runtime;

import java.util.Collection;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.intent.ConstrainedBFSTree;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntentMap;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathCalcRuntime implements IFloodlightService {
	private NetworkGraph graph;
	public PathCalcRuntime(NetworkGraph g) {
		this.graph = g;
	}

	public PathIntentMap calcPathIntents(Collection<Intent> highLevelIntents, PathIntentMap pathIntents) {
		IntentOperationList intentOpList = new IntentOperationList();

		for (Intent intent: highLevelIntents) {
			if (!(intent instanceof ShortestPathIntent)) {
				// unsupported intent type.
				// TODO should push back the intent to caller
				continue;
			}

			ShortestPathIntent spIntent = (ShortestPathIntent) intent;
			Switch srcSwitch = graph.getSwitch(spIntent.getSrcSwitchDpid());
			Switch dstSwitch = graph.getSwitch(spIntent.getDstSwitchDpid());
			if (srcSwitch == null || dstSwitch == null) {
				// incomplete intent.
				// TODO should push back the intent to caller
				continue;
			}

			double bandwidth = 0.0;
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

			PathIntent pathIntent = new PathIntent("pi" + intent.getId(), path, bandwidth, intent);
			pathIntents.addIntent(pathIntent);
			intentOpList.add(new IntentOperation(IntentOperation.Operator.ADD, pathIntent));
		}
		return pathIntents;
	}
}