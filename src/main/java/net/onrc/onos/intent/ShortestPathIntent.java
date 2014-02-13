package net.onrc.onos.intent;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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

	public ShortestPathIntent(String id,
			Port srcPort, MACAddress srcMac,
			Port dstPort, MACAddress dstMac) {
		super(id);
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcMac = srcMac;
		this.dstMac = dstMac;
	}

	public ShortestPathIntent(NetworkGraph graph, String id,
			long srcSwitch, long srcPort, long srcMac,
			long dstSwitch, long dstPort, long dstMac) {
		super(id);
		this.srcPort = graph.getSwitch(srcSwitch).getPort(srcPort);
		this.dstPort = graph.getSwitch(dstSwitch).getPort(srcPort);
		this.srcMac = MACAddress.valueOf(srcMac);
		this.dstMac = MACAddress.valueOf(dstMac);
	}

	public static ShortestPathIntent fromBytes(NetworkGraph graph, byte[] bytes) {
		Input input = new Input(bytes);
		ShortestPathIntent intent = new ShortestPathIntent(graph,
				input.readString(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readLong(),
				input.readLong());
		input.close();
		return intent;
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
		output.close();
		return output.toBytes();
	}
}
