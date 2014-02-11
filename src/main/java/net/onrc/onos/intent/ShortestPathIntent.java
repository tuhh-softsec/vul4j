package net.onrc.onos.intent;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Port;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ShortestPathIntent extends Intent {
	protected Port srcPort = null;
	protected Port dstPort = null;
	protected MACAddress srcMac = null;
	protected MACAddress dstMac = null;

	public ShortestPathIntent(
			Port srcPort, MACAddress srcMac,
			Port dstPort, MACAddress dstMac) {
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcMac = srcMac;
		this.dstMac = dstMac;
	}

	public ShortestPathIntent(NetworkGraph graph,
			Long srcSwitch, Long srcPort, long srcMac,
			Long dstSwitch, Long dstPort, long dstMac) {
		this.srcPort = graph.getSwitch(srcSwitch).getPort(srcPort);
		this.dstPort = graph.getSwitch(dstSwitch).getPort(srcPort);
		this.srcMac = MACAddress.valueOf(srcMac);
		this.dstMac = MACAddress.valueOf(dstMac);
	}

	public Port getSourcePort() {
		return srcPort;
	}

	public MACAddress getSourceMac() {
		return srcMac;
	}

	public Port getDestinationPort() {
		return dstPort;
	}

	public MACAddress getDestinationMac() {
		return dstMac;
	}

	@Override
	public String toString() {
		return String.format("srcPort:%s, srcMac:%s, dstPort:%s, dstMac:%s",
				srcPort.toString(), srcMac.toString(),
				dstPort.toString(), dstMac.toString());
	}
}
