package net.onrc.onos.intent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntentMap extends IntentMap {
	protected HashMap<LinkEvent, HashSet<PathIntent>> linkToIntents = new HashMap<>();

	@Override
	protected void putIntent(Intent intent) {
		super.putIntent(intent);
		for (LinkEvent linkEvent: ((PathIntent) intent).getPathByLinkEvent()) {
			HashSet<PathIntent> value = linkToIntents.get(linkEvent);
			if (value == null)
				value = new HashSet<PathIntent>();
			value.add((PathIntent) intent);
			linkToIntents.put(linkEvent, value);
		}
	}

	@Override
	protected void removeIntent(String intentId) {
		PathIntent intent = (PathIntent) getIntent(intentId);
		for (LinkEvent linkEvent: intent.getPathByLinkEvent()) {
			HashSet<PathIntent> value = linkToIntents.get(linkEvent);
			value.remove(intent);
		}
		super.removeIntent(intentId);
	}

	public Collection<PathIntent> getIntentsByLink(LinkEvent linkEvent) {
		Collection<PathIntent> intents = linkToIntents.get(linkEvent);
		if (intents == null) {
			return null;
		}
		else {
			return Collections.unmodifiableCollection(intents);
		}
	}

	/**
	 * calculate available bandwidth of specified link
	 * @param link
	 * @return
	 */
	public Double getAvailableBandwidth(Link link) {
		if (link == null) return null;
		Double bandwidth = link.getCapacity();
		LinkEvent linkEvent = new LinkEvent(link);
		if (!bandwidth.isInfinite() && linkToIntents.containsKey(linkEvent)) {
			for (PathIntent intent: getIntentsByLink(linkEvent)) {
				Double intentBandwidth = intent.getBandwidth();
				if (intentBandwidth == null || intentBandwidth.isInfinite() || intentBandwidth.isNaN())
					continue;
				bandwidth -= intentBandwidth;
			}
		}
		return bandwidth;
	}
}
