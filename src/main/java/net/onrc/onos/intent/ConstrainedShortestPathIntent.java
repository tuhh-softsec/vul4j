package net.onrc.onos.intent;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Port;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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

	public static ConstrainedShortestPathIntent fromBytes(NetworkGraph graph, byte[] bytes) {
		Input input = new Input(bytes);
		ConstrainedShortestPathIntent intent = new ConstrainedShortestPathIntent(graph,
				input.readString(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readDouble());
		input.close();
		return intent;
	}

	public Double getBandwidth() {
		return bandwidth;
	}

	@Override
	public byte[] toBytes() {
		byte[] buffer = new byte[1024];
		Output output = new Output(buffer, -1);
		output.writeString(id);
		output.writeLong(srcPort.getSwitch().getDpid());
		output.writeLong(srcPort.getNumber());
		output.writeLong(srcMac.toLong());
		output.writeLong(dstPort.getSwitch().getDpid());
		output.writeLong(dstPort.getNumber());
		output.writeLong(dstMac.toLong());
		output.writeDouble(bandwidth);
		output.close();
		return output.toBytes();
	}
}
