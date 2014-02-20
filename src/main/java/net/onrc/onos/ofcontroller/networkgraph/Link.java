package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Interface of Link Object exposed to the "NB" read-only Topology.
 *
 * Everything returned by these interfaces must be either Unmodifiable view,
 * immutable object, or a copy of the original "SB" In-memory Topology.
 *
 */
public interface Link {
    /**
     * Get the source switch for the link.
     *
     * @return the source switch for the link.
     */
    public Switch getSrcSwitch();

    /**
     * Get the source port for the link.
     *
     * @return the source port for the link.
     */
    public Port getSrcPort();

    /**
     * Get the destination switch for the link.
     *
     * @return the destination switch for the link.
     */
    public Switch getDstSwitch();

    /**
     * Get the destination port for the link.
     *
     * @return the destination port for the link.
     */
    public Port getDstPort();

    /**
     * Get the last seen time for the link.
     *
     * TODO: Not implemented yet.
     * TODO: what is the time definition?
     *
     * @return the last seen time for the link.
     */
    public long getLastSeenTime();

    /**
     * Get the link cost.
     *
     * TODO: What is the unit?
     *
     * @param return the link cost.
     */
    public int getCost();

    /**
     * Get the link capacity.
     *
     * TODO: What is the unit?
     *
     * @return the link capacity.
     */
    public Double getCapacity();
}
