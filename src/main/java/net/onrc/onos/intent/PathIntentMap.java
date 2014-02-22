package net.onrc.onos.intent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntentMap extends IntentMap {
	private HashMap<Long, HashMap<Long, HashSet<PathIntent>>> intents;

	public PathIntentMap() {
		intents = new HashMap<>();
	}

	private HashSet<PathIntent> get(SwitchPort swPort) {
		Long dpid = swPort.getDpid();
		Long port = swPort.getNumber();
		HashMap<Long, HashSet<PathIntent>> portToIntents = intents.get(dpid);
		if (portToIntents == null) {
			portToIntents = new HashMap<>();
			intents.put(dpid, portToIntents);
		}
		HashSet<PathIntent> targetIntents = portToIntents.get(port);
		if (targetIntents == null) {
			targetIntents = new HashSet<>();
			portToIntents.put(port, targetIntents);
		}
		return targetIntents;
	}

	private void put(SwitchPort swPort, PathIntent intent) {
		get(swPort).add(intent);
	}

	@Override
	protected void putIntent(Intent intent) {
		if (!(intent instanceof PathIntent)) return; // TODO throw exception
		super.putIntent(intent);

		PathIntent pathIntent = (PathIntent) intent;
		for (LinkEvent linkEvent: pathIntent.getPath()) {
			put(linkEvent.getSrc(), (PathIntent) intent);
			put(linkEvent.getDst(), (PathIntent) intent);
		}
	}

	@Override
	protected void removeIntent(String intentId) {
		PathIntent intent = (PathIntent) getIntent(intentId);
		for (LinkEvent linkEvent: intent.getPath()) {
			get(linkEvent.getSrc()).remove(intent);
			get(linkEvent.getDst()).remove(intent);
		}
		super.removeIntent(intentId);
	}

	public Collection<PathIntent> getIntentsByLink(LinkEvent linkEvent) {
		return getIntentsByPort(
				linkEvent.getSrc().getDpid(),
				linkEvent.getSrc().getNumber());
	}

	public Collection<PathIntent> getIntentsByPort(Long dpid, Long port) {
		HashMap<Long, HashSet<PathIntent>> portToIntents = intents.get(dpid);
		if (portToIntents != null) {
			HashSet<PathIntent> targetIntents = portToIntents.get(port);
			if (targetIntents != null) {
				return Collections.unmodifiableCollection(targetIntents);
			}
		}
		return new HashSet<>();
	}

	public Collection<PathIntent> getIntentsByDpid(Long dpid) {
		HashSet<PathIntent> result = new HashSet<>();
		HashMap<Long, HashSet<PathIntent>> portToIntents = intents.get(dpid);
		if (portToIntents != null) {
			for (HashSet<PathIntent> targetIntents: portToIntents.values()) {
				result.addAll(targetIntents);
			}
		}
		return result;
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
		if (!bandwidth.isInfinite()) {
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
