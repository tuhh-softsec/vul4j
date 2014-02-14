package net.onrc.onos.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedShortestPathIntent extends ShortestPathIntent {
	protected double bandwidth;

	/**
	 * Default constructor for Kryo deserialization
	 */
	@Deprecated
	public ConstrainedShortestPathIntent() {
	}
	
	public ConstrainedShortestPathIntent(String id,
			long srcSwitch, long srcPort, long srcMac,
			long dstSwitch, long dstPort, long dstMac,
			double bandwidth) {
		super(id, srcSwitch, srcPort, srcMac, dstSwitch, dstPort, dstMac);
		this.bandwidth = bandwidth;
	}

	public double getBandwidth() {
		return bandwidth;
	}
}
