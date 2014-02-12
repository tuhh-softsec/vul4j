package net.onrc.onos.intent;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Port;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedShortestPathIntent extends ShortestPathIntent {
	protected Double bandwidth;

	public ConstrainedShortestPathIntent(String id,
			Port srcPort, MACAddress srcMac,
			Port dstPort, MACAddress dstMac,
			Double bandwidth) {
		super(id, srcPort, srcMac, dstPort, dstMac);
		this.bandwidth = bandwidth;
	}

	public ConstrainedShortestPathIntent(NetworkGraph graph, String id,
			long srcSwitch, long srcPort, long srcMac,
			long dstSwitch, long dstPort, long dstMac,
			Double bandwidth) {
		super(graph, id, srcSwitch, srcPort, srcMac, dstSwitch, dstPort, dstMac);
		this.bandwidth = bandwidth;
	}

	public Double getBandwidth() {
		return bandwidth;
	}
}
