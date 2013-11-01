package net.onrc.onos.ofcontroller.topology;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class for storing information about a Topology Element: Switch, Port or
 * Link.
 */
public class TopologyElement {
    /**
     * The Element Type.
     */
    public enum Type {
	ELEMENT_UNKNOWN,	// Unknown element
	ELEMENT_SWITCH,		// Network Switch
	ELEMENT_PORT,		// Switch Port
	ELEMENT_LINK		// Unidirectional Link between Switch Ports
    }

    private Type elementType;		// The element type
    private long fromSwitchDpid = 0;	// The Switch DPID
    private int fromSwitchPort = 0;	// The Switch Port
    private long toSwitchDpid = 0;	// The Neighbor Switch DPID
    private int toSwitchPort = 0;	// The Neighbor Switch Port

    // All (known) ports for a Switch
    private Map<Integer, Integer> switchPorts = new TreeMap<Integer, Integer>();

    /**
     * Default constructor.
     */
    public TopologyElement() {
	elementType = Type.ELEMENT_UNKNOWN;
    }

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
     * Get the Element type.
     *
     * @return the Element type.
     */
    public TopologyElement.Type getType() {
	return elementType;
    }

    /**
     * Get the Switch DPID.
     *
     * NOTE: Applies for Type.ELEMENT_SWITCH and Type.ELEMENT_PORT
     *
     * @return the Switch DPID.
     */
    public long getSwitch() {
	return fromSwitchDpid;
    }

    /**
     * Get the Switch Ports.
     *
     * NOTE: Applies for Type.ELEMENT_SWITCH
     *
     * @return the collection of Switch Ports.
     */
    public Map<Integer, Integer> getSwitchPorts() {
	return switchPorts;
    }

    /**
     * Add a Switch Port.
     *
     * NOTE: Applies for Type.ELEMENT_SWITCH
     *
     * @param switchPort the Switch Port to add.
     */
    public void addSwitchPort(int switchPort) {
	switchPorts.put(switchPort, switchPort);
    }

    /**
     * Get the Switch Port.
     *
     * NOTE: Applies for Type.ELEMENT_PORT
     *
     * @return the Switch Port.
     */
    public int getSwitchPort() {
	return fromSwitchPort;
    }

    /**
     * Get the Switch DPID the Link begins from.
     *
     * NOTE: Applies for Type.ELEMENT_LINK
     */
    public long getFromSwitch() {
	return fromSwitchDpid;
    }

    /**
     * Get the Switch Port the Link begins from.
     *
     * NOTE: Applies for Type.ELEMENT_LINK
     */
    public int getFromPort() {
	return fromSwitchPort;
    }

    /**
     * Get the Switch DPID the Link ends to.
     *
     * NOTE: Applies for Type.ELEMENT_LINK
     */
    public long getToSwitch() {
	return toSwitchDpid;
    }

    /**
     * Get the Switch Port the Link ends to.
     *
     * NOTE: Applies for Type.ELEMENT_LINK
     */
    public int getToPort() {
	return toSwitchPort;
    }

    /**
     * Get the Topology Element ID.
     *
     * The Topology Element ID has the following format:
     *   - Switch: "Switch=<HexLongDpid>"
     *     Example: "Switch=101"
     *   - Switch Port: "Port=<HexLongDpid>/<IntPortId>"
     *     Example: "Port=102/1"
     *   - Link: "Link=<FromHexLongDpid>/<FromIntPortId>/<ToHexLongDpid>/<ToIntPortId>"
     *     Example: "Link=101/2/103/4"
     *
     * NOTE: The Topology Element ID has no syntax meaning. It is used only to
     * uniquely identify a topology element.
     *
     * @return the Topology Element ID.
     */
    public String elementId() {
	switch (elementType) {
	case ELEMENT_SWITCH:
	    return "Switch=" + Long.toHexString(fromSwitchDpid);
	case ELEMENT_PORT:
	    return "Port=" +
		Long.toHexString(fromSwitchDpid) + "/" + fromSwitchPort;
	case ELEMENT_LINK:
	    return "Link=" +
		Long.toHexString(fromSwitchDpid) + "/" + fromSwitchPort + "/" +
		Long.toHexString(toSwitchDpid) + "/" + toSwitchPort;
	}

	assert(false);
	return null;
    }

    /**
     * Convert the Topology Element to a string.
     *
     * @return the Topology Element as a string.
     */
    @Override
    public String toString() {
	// For now, we just return the Element ID.
	return elementId();
    }
}
