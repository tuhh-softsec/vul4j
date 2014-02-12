package net.onrc.onos.intent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.onrc.onos.ofcontroller.networkgraph.Link;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntents {
	protected LinkedList<PathIntent> intents = new LinkedList<PathIntent>();
	protected HashMap<Link, HashSet<PathIntent>> linkToIntents = new HashMap<Link, HashSet<PathIntent>>();

	public void addIntent(PathIntent intent) {
		intents.add(intent);
		for (Link link: intent.getPath()) {
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
		for (Link link: intent.getPath()) {
			HashSet<PathIntent> value = linkToIntents.get(link);
			value.remove(intent);
		}
	}

	public void addIntents(PathIntents intents) {
		for(PathIntent intent: intents.getIntents()) {
			addIntent(intent);
		}
	}

	public void removeIntents(PathIntents intents) {
		for(PathIntent intent: intents.getIntents()) {
			removeIntent(intent);
		}
	}

	public Collection<PathIntent> getIntentByLink(Link link) {
		return Collections.unmodifiableCollection(linkToIntents.get(link));
	}

	public Collection<PathIntent> getIntents() {
		return Collections.unmodifiableCollection(intents);
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
