package net.onrc.onos.ofcontroller.topology;

/**
 * Class for storing information about a Topology Element: Switch, Port or
 * Link.
 */
public class TopologyElement {
    /**
     * The Element Type.
     */
    enum Type {
	ELEMENT_SWITCH,		// Network Switch
	ELEMENT_PORT,		// Switch Port
	ELEMENT_LINK		// Unidirectional Link between Switch Ports
    }

    private Type elementType;		// The element type
    private long fromSwitchDpid = 0;	// The Switch DPID
    private int fromSwitchPort = 0;	// The Switch Port
    private long toSwitchDpid = 0;	// The Neighbor Switch DPID
    private int toSwitchPort = 0;	// The Neighbor Switch Port

    /**
     * Constructor to create a Topology Element for a Switch.
     *
     * @param switchDpid the Switch DPID.
     */
    public TopologyElement(long switchDpid) {
	this.elementType = Type.ELEMENT_SWITCH;
	this.fromSwitchDpid = switchDpid;
    }

    /**
     * Constructor to create a Topology Element for a Switch Port.
     *
     * @param switchDpid the Switch DPID.
     * @param switchPort the Switch Port.
     */
    public TopologyElement(long switchDpid, int switchPort) {
	this.elementType = Type.ELEMENT_PORT;
	this.fromSwitchDpid = switchDpid;
	this.fromSwitchPort = switchPort;
    }

    /**
     * Constructor to create a Topology Element for an unidirectional Link
     * between Switch Ports.
     *
     * @param fromSwitchDpid the Switch DPID the Link begins from.
     * @param fromSwitchPort the Switch Port the Link begins from.
     * @param toSwitchDpid the Switch DPID the Link ends to.
     * @param toSwitchPort the Switch Port the Link ends to.
     */
    public TopologyElement(long fromSwitchDpid, int fromSwitchPort,
			   long toSwitchDpid, int toSwitchPort) {
	this.elementType = Type.ELEMENT_LINK;
	this.fromSwitchDpid = fromSwitchDpid;
	this.fromSwitchPort = fromSwitchPort;
	this.toSwitchDpid = toSwitchDpid;
	this.toSwitchPort = toSwitchPort;
    }

    /**
     * Get the Topology Element ID.
     *
     * The Topology Element ID has the following format:
     *   - Switch: "Switch=<Dpid>"
     *     Example: "Switch=00:00:00:00:00:00:00:01"
     *   - Switch Port: "Port=<Dpid>/<PortId>"
     *     Example: "Port=00:00:00:00:00:00:00:01/1"
     *   - Link: "Link=<FromDpid>/<FromPortId>/<ToDpid>/<ToPortId>"
     *     Example: "Link=00:00:00:00:00:00:00:01/1/00:00:00:00:00:00:00:02/1"
     *
     * @return the Topology Element ID.
     */
    public String elementId() {
	switch (elementType) {
	case ELEMENT_SWITCH:
	    return "Switch=" + fromSwitchDpid;
	case ELEMENT_PORT:
	    return "Port=" + fromSwitchDpid + "/" + fromSwitchPort;
	case ELEMENT_LINK:
	    return "Link=" + fromSwitchDpid + "/" + fromSwitchPort +
		toSwitchDpid + "/" + toSwitchPort;
	}

	assert(false);
	return null;
    }
}
