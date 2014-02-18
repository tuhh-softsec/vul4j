package net.onrc.onos.intent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntentMap extends IntentMap {
	protected HashMap<Link, HashSet<PathIntent>> linkToIntents = new HashMap<Link, HashSet<PathIntent>>();
	protected NetworkGraph graph;

	public PathIntentMap(NetworkGraph graph) {
		this.graph = graph;
	}

	public void addIntent(PathIntent intent) {
		if (intents.containsKey(intent.getId()))
			removeIntent((PathIntent)intents.get(intent.getId()));
		intents.put(intent.getId(), intent);
		for (Link link: intent.getPath(graph)) {
			HashSet<PathIntent> value = linkToIntents.get(link);
			if (value == null) {
				value = new HashSet<PathIntent>();
				linkToIntents.put(link, value);
			}
			value.add(intent);
		}
	}

	public void removeIntent(PathIntent intent) {
		intents.remove(intent);
		for (Link link: intent.getPath(graph)) {
			HashSet<PathIntent> value = linkToIntents.get(link);
			value.remove(intent);
		}
	}

	public Collection<PathIntent> getIntentByLink(Link link) {
		return Collections.unmodifiableCollection(linkToIntents.get(link));
	}

	/**
	 * calculate available bandwidth of specified link
	 * @param link
	 * @return
	 */
	public Double getAvailableBandwidth(Link link) {
		Double bandwidth = link.getCapacity();
		if (!bandwidth.isInfinite() && linkToIntents.containsKey(link)) {
			for (PathIntent intent: getIntentByLink(link)) {
				Double intentBandwidth = intent.getBandwidth();
				if (intentBandwidth == null || intentBandwidth.isInfinite() || intentBandwidth.isNaN())
					continue;
				bandwidth -= intentBandwidth;
			}
		}
		return bandwidth;
	}
}
