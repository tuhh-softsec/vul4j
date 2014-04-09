package net.onrc.onos.core.util;


import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing the Data Path Endpoints.
 * This class is immutable.
 */
public final class DataPathEndpoints {
    private final SwitchPort srcPort;        // The source port
    private final SwitchPort dstPort;        // The destination port

    /**
     * Default constructor.
     */
    public DataPathEndpoints() {
        srcPort = null;
        dstPort = null;
    }

    /**
     * Constructor for given source and destination ports.
     *
     * @param srcPort the source port to use.
     * @param dstPort the destination port to use.
     */
    public DataPathEndpoints(SwitchPort srcPort, SwitchPort dstPort) {
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }

    /**
     * Get the data path source port.
     *
     * @return the data path source port.
     */
    @JsonProperty("srcPort")
    public SwitchPort srcPort() {
        return srcPort;
    }

    /**
     * Get the data path destination port.
     *
     * @return the data path destination port.
     */
    @JsonProperty("dstPort")
    public SwitchPort dstPort() {
        return dstPort;
    }

    /**
     * Convert the data path endpoints to a string.
     * <p/>
     * The string has the following form:
     * [src=01:01:01:01:01:01:01:01/1111 dst=02:02:02:02:02:02:02:02/2222]
     *
     * @return the data path endpoints as a string.
     */
    @Override
    public String toString() {
        String ret = "[src=" + this.srcPort.toString() +
                " dst=" + this.dstPort.toString() + "]";
        return ret;
    }
}
