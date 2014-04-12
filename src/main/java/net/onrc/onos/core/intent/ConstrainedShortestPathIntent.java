package net.onrc.onos.core.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedShortestPathIntent extends ShortestPathIntent {
    protected double bandwidth;

    /**
     * Default constructor for Kryo deserialization.
     */
    protected ConstrainedShortestPathIntent() {
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

    @Override
    public int hashCode() {
        // TODO: Is this the intended behavior?
        return (super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: Is this the intended behavior?
        return (super.equals(obj));
    }
}
