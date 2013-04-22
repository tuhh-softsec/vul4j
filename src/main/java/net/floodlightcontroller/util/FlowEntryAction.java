package net.floodlightcontroller.util;

import net.floodlightcontroller.util.IPv4;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.Port;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing a single Flow Entry action.
 *
 * A set of Flow Entry actions need to be applied to each packet.
 */
public class FlowEntryAction {
    /**
     * Special action values.
     *
     * Those values are taken as-is from the OpenFlow-v1.0.0 specification
     * (pp 21-22).
     */
    public enum ActionValues {
	ACTION_OUTPUT		((short)0x0),	// Output to switch port
	ACTION_SET_VLAN_VID	((short)0x1),	// Set the 802.1q VLAN id
	ACTION_SET_VLAN_PCP	((short)0x2),	// Set the 802.1q priority
	ACTION_STRIP_VLAN	((short)0x3),	// Strip the 802.1q header
	ACTION_SET_DL_SRC	((short)0x4),	// Ethernet source address
	ACTION_SET_DL_DST	((short)0x5),	// Ethernet destination address
	ACTION_SET_NW_SRC	((short)0x6),	// IP source address
	ACTION_SET_NW_DST	((short)0x7),	// IP destination address
	ACTION_SET_NW_TOS	((short)0x8),	// IP ToS (DSCP field, 6 bits)
	ACTION_SET_TP_SRC	((short)0x9),	// TCP/UDP source port
	ACTION_SET_TP_DST	((short)0xa),	// TCP/UDP destination port
	ACTION_ENQUEUE		((short)0xb),	// Output to queue on port
	ACTION_VENDOR		((short)0xffff); // Vendor-specific

	private final short value;	// The value

	/**
	 * Constructor for a given value.
	 *
	 * @param value the value to use for the initialization.
	 */
	private ActionValues(short value) {
	    this.value = value;
	}
    }

    /**
     * Action structure for ACTION_OUTPUT: Output to switch port.
     */
    public class ActionOutput {
	private Port port;	// Output port
	private short maxLen;	// Max. length (in bytes) to send to controller
				// if the port is set to PORT_CONTROLLER

	/**
	 * Default constructor.
	 */
	public ActionOutput() {
	    this.port = null;
	    this.maxLen = 0;
	}


	/**
	 * Constructor for a given output port and maximum length.
	 *
	 * @param port the output port to set.
	 * @param maxLen the maximum length (in bytes) to send to controller
	 * if the port is set to PORT_CONTROLLER.
	 */
	public ActionOutput(Port port, short maxLen) {
	    this.port = port;
	    this.maxLen = maxLen;
	}

	/**
	 * Constructor for a given output port.
	 *
	 * @param port the output port to set.
	 */
	public ActionOutput(Port port) {
	    this.port = port;
	    this.maxLen = 0;
	}

	/**
	 * Get the output port.
	 *
	 * @return the output port.
	 */
	@JsonProperty("port")
	public Port port() {
	    return this.port;
	}

	/**
	 * Get the maximum length (in bytes) to send to controller if the
	 * port is set to PORT_CONTROLLER.
	 *
	 * @return the maximum length (in bytes) to send to controller if the
	 * port is set to PORT_CONTROLLER.
	 */
	@JsonProperty("maxLen")
	public short maxLen() {
	    return this.maxLen;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [port=XXX maxLen=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "port=" + port.toString();
	    ret += " maxLen=" + maxLen;
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_SET_VLAN_VID: Set the 802.1q VLAN id
     */
    public class ActionSetVlanId {
	private short vlanId;		// The VLAN ID to set

	/**
	 * Default constructor.
	 */
	public ActionSetVlanId() {
	    this.vlanId = 0;
	}

	/**
	 * Constructor for a given VLAN ID.
	 *
	 * @param vlanId the VLAN ID to set.
	 */
	public ActionSetVlanId(short vlanId) {
	    this.vlanId = vlanId;
	}

	/**
	 * Get the VLAN ID.
	 *
	 * @return the VLAN ID.
	 */
	@JsonProperty("vlanId")
	public short vlanId() {
	    return this.vlanId;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [vlanId=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "vlanId=" + this.vlanId;
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_SET_VLAN_PCP: Set the 802.1q priority
     */
    public class ActionSetVlanPriority {
	private byte vlanPriority;	// The VLAN priority to set

	/**
	 * Default constructor.
	 */
	public ActionSetVlanPriority() {
	    this.vlanPriority = 0;
	}

	/**
	 * Constructor for a given VLAN priority.
	 *
	 * @param vlanPriority the VLAN priority to set.
	 */
	public ActionSetVlanPriority(byte vlanPriority) {
	    this.vlanPriority = vlanPriority;
	}

	/**
	 * Get the VLAN priority.
	 *
	 * @return the VLAN priority.
	 */
	@JsonProperty("vlanPriority")
	public byte vlanPriority() {
	    return this.vlanPriority;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [vlanPriority=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "vlanPriority=" + this.vlanPriority;
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_STRIP_VLAN: Strip the 802.1q header
     */
    public class ActionStripVlan {
	private boolean stripVlan;	// If true, strip the VLAN header

	/**
	 * Default constructor.
	 */
	public ActionStripVlan() {
	    this.stripVlan = false;
	}

	/**
	 * Constructor for a given boolean flag.
	 *
	 * @param stripVlan if true, strip the VLAN header.
	 */
	public ActionStripVlan(boolean stripVlan) {
	    this.stripVlan = stripVlan;
	}

	/**
	 * Get the boolean flag whether the VLAN header should be stripped.
	 *
	 * @return the boolean flag whether the VLAN header should be stripped.
	 */
	@JsonProperty("stripVlan")
	public boolean stripVlan() {
	    return this.stripVlan;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [stripVlan=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "stripVlan=" + this.stripVlan;
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_SET_DL_SRC and ACTION_SET_DL_DST:
     * Set the Ethernet source/destination address.
     */
    public class ActionSetEthernetAddr {
	private MACAddress addr;	// The MAC address to set

	/**
	 * Default constructor.
	 */
	public ActionSetEthernetAddr() {
	    this.addr = null;
	}

	/**
	 * Constructor for a given MAC address.
	 *
	 * @param addr the MAC address to set.
	 */
	public ActionSetEthernetAddr(MACAddress addr) {
	    this.addr = addr;
	}

	/**
	 * Get the MAC address.
	 *
	 * @return the MAC address.
	 */
	@JsonProperty("addr")
	public MACAddress addr() {
	    return this.addr;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [addr=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "addr=" + addr.toString();
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_SET_NW_SRC and ACTION_SET_NW_DST:
     * Set the IPv4 source/destination address.
     */
    public class ActionSetIPv4Addr {
	private IPv4 addr;		// The IPv4 address to set

	/**
	 * Default constructor.
	 */
	public ActionSetIPv4Addr() {
	    this.addr = null;
	}

	/**
	 * Constructor for a given IPv4 address.
	 *
	 * @param addr the IPv4 address to set.
	 */
	public ActionSetIPv4Addr(IPv4 addr) {
	    this.addr = addr;
	}

	/**
	 * Get the IPv4 address.
	 *
	 * @return the IPv4 address.
	 */
	@JsonProperty("addr")
	public IPv4 addr() {
	    return this.addr;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [addr=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "addr=" + addr.toString();
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_SET_NW_TOS:
     * Set the IP ToS (DSCP field, 6 bits).
     */
    public class ActionSetIpToS {
	private byte ipToS;	// The IP ToS to set DSCP field, 6 bits)

	/**
	 * Default constructor.
	 */
	public ActionSetIpToS() {
	    this.ipToS = 0;
	}

	/**
	 * Constructor for a given IP ToS (DSCP field, 6 bits).
	 *
	 * @param ipToS the IP ToS (DSCP field, 6 bits) to set.
	 */
	public ActionSetIpToS(byte ipToS) {
	    this.ipToS = ipToS;
	}

	/**
	 * Get the IP ToS (DSCP field, 6 bits).
	 *
	 * @return the IP ToS (DSCP field, 6 bits).
	 */
	@JsonProperty("ipToS")
	public byte ipToS() {
	    return this.ipToS;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [ipToS=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "ipToS=" + ipToS;
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_SET_TP_SRC and ACTION_SET_TP_DST:
     * Set the TCP/UDP source/destination port.
     */
    public class ActionSetTcpUdpPort {
	private short port;		// The TCP/UDP port to set

	/**
	 * Default constructor.
	 */
	public ActionSetTcpUdpPort() {
	    this.port = 0;
	}

	/**
	 * Constructor for a given TCP/UDP port.
	 *
	 * @param port the TCP/UDP port to set.
	 */
	public ActionSetTcpUdpPort(short port) {
	    this.port = port;
	}

	/**
	 * Get the TCP/UDP port.
	 *
	 * @return the TCP/UDP port.
	 */
	@JsonProperty("port")
	public short port() {
	    return this.port;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [port=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "port=" + port;
	    ret += "]";

	    return ret;
	}
    }

    /**
     * Action structure for ACTION_ENQUEUE: Output to queue on port.
     */
    public class ActionEnqueue {
	private Port port;	// Port that queue belongs. Should
				// refer to a valid physical port
				// (i.e. < PORT_MAX) or PORT_IN_PORT
	private int queueId;	// Where to enqueue the packets

	/**
	 * Default constructor.
	 */
	public ActionEnqueue() {
	    this.port = null;
	    this.queueId = 0;
	}

	/**
	 * Constructor for a given port and queue ID.
	 *
	 * @param port the port to set.
	 * @param queueId the queue ID on the port.
	 */
	public ActionEnqueue(Port port, int queueId) {
	    this.port = port;
	    this.queueId = queueId;
	}

	/**
	 * Get the port.
	 *
	 * @return the port.
	 */
	@JsonProperty("port")
	public Port port() {
	    return this.port;
	}

	/**
	 * Get the queue ID.
	 *
	 * @return the queue ID.
	 */
	@JsonProperty("queueId")
	public int queueId() {
	    return this.queueId;
	}

	/**
	 * Convert the action to a string.
	 *
	 * The string has the following form:
	 *  [port=XXX queueId=XXX]
	 *
	 * @return the action as a string.
	 */
	@Override
	public String toString() {
	    String ret = "[";
	    ret += "port=" + port.toString();
	    ret += " queueId=" + queueId;
	    ret += "]";

	    return ret;
	}
    }

    private ActionValues actionType;	// The action type

    //
    // The actions.
    // NOTE: Only one action should be set.
    //
    private ActionOutput actionOutput;
    private ActionSetVlanId actionSetVlanId;
    private ActionSetVlanPriority actionSetVlanPriority;
    private ActionStripVlan actionStripVlan;
    private ActionSetEthernetAddr actionSetEthernetSrcAddr;
    private ActionSetEthernetAddr actionSetEthernetDstAddr;
    private ActionSetIPv4Addr actionSetIPv4SrcAddr;
    private ActionSetIPv4Addr actionSetIPv4DstAddr;
    private ActionSetIpToS actionSetIpToS;
    private ActionSetTcpUdpPort actionSetTcpUdpSrcPort;
    private ActionSetTcpUdpPort actionSetTcpUdpDstPort;
    private ActionEnqueue actionEnqueue;

    /**
     * Default constructor.
     */
    public FlowEntryAction() {
	actionType = ActionValues.ACTION_VENDOR;	// XXX: Initial value
    }

    /**
     * Get the action type.
     *
     * @return the action type.
     */
    @JsonProperty("actionType")
    public ActionValues actionType() { return actionType; }

    /**
     * Get the output action.
     *
     * @return the output action.
     */
    @JsonProperty("actionOutput")
    public ActionOutput actionOutput() { return actionOutput; }

    /**
     * Set the output action on a port.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionOutput")
    public void setActionOutput(ActionOutput action) {
	actionOutput = action;
	actionType = ActionValues.ACTION_OUTPUT;
    }

    /**
     * Set the output action on a port.
     *
     * @param port the output port to set.
     */
    public void setActionOutput(Port port) {
	actionOutput = new ActionOutput(port);
	actionType = ActionValues.ACTION_OUTPUT;
    }

    /**
     * Set the output action to controller.
     *
     * @param maxLen the maximum length (in bytes) to send to controller.
     */
    public void setActionOutputToController(short maxLen) {
	Port port = new Port(Port.PortValues.PORT_CONTROLLER);
	actionOutput = new ActionOutput(port, maxLen);
	actionType = ActionValues.ACTION_OUTPUT;
    }

    /**
     * Get the action to set the VLAN ID.
     *
     * @return the action to set the VLAN ID.
     */
    @JsonProperty("actionSetVlanId")
    public ActionSetVlanId actionSetVlanId() { return actionSetVlanId; }

    /**
     * Set the action to set the VLAN ID.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetVlanId")
    public void setActionSetVlanId(ActionSetVlanId action) {
	actionSetVlanId = action;
	actionType = ActionValues.ACTION_SET_VLAN_VID;
    }

    /**
     * Set the action to set the VLAN ID.
     *
     * @param vlanId the VLAN ID to set.
     */
    public void setActionSetVlanId(short vlanId) {
	actionSetVlanId = new ActionSetVlanId(vlanId);
	actionType = ActionValues.ACTION_SET_VLAN_VID;
    }

    /**
     * Get the action to set the VLAN priority.
     *
     * @return the action to set the VLAN priority.
     */
    @JsonProperty("actionSetVlanPriority")
    public ActionSetVlanPriority actionSetVlanPriority() {
	return actionSetVlanPriority;
    }

    /**
     * Set the action to set the VLAN priority.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetVlanPriority")
    public void setActionSetVlanPriority(ActionSetVlanPriority action) {
	actionSetVlanPriority = action;
	actionType = ActionValues.ACTION_SET_VLAN_PCP;
    }

    /**
     * Set the action to set the VLAN priority.
     *
     * @param vlanPriority the VLAN priority to set.
     */
    public void setActionSetVlanPriority(byte vlanPriority) {
	actionSetVlanPriority = new ActionSetVlanPriority(vlanPriority);
	actionType = ActionValues.ACTION_SET_VLAN_PCP;
    }

    /**
     * Get the action to strip the VLAN header.
     *
     * @return the action to strip the VLAN header.
     */
    @JsonProperty("actionStripVlan")
    public ActionStripVlan actionStripVlan() {
	return actionStripVlan;
    }

    /**
     * Set the action to strip the VLAN header.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionStripVlan")
    public void setActionStripVlan(ActionStripVlan action) {
	actionStripVlan = action;
	actionType = ActionValues.ACTION_STRIP_VLAN;
    }

    /**
     * Set the action to strip the VLAN header.
     *
     * @param stripVlan if true, strip the VLAN header.
     */
    public void setActionStripVlan(boolean stripVlan) {
	actionStripVlan = new ActionStripVlan(stripVlan);
	actionType = ActionValues.ACTION_STRIP_VLAN;
    }

    /**
     * Get the action to set the Ethernet source address.
     *
     * @return the action to set the Ethernet source address.
     */
    @JsonProperty("actionSetEthernetSrcAddr")
    public ActionSetEthernetAddr actionSetEthernetSrcAddr() {
	return actionSetEthernetSrcAddr;
    }

    /**
     * Set the action to set the Ethernet source address.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetEthernetSrcAddr")
    public void setActionSetEthernetSrcAddr(ActionSetEthernetAddr action) {
	actionSetEthernetSrcAddr = action;
	actionType = ActionValues.ACTION_SET_DL_SRC;
    }

    /**
     * Set the action to set the Ethernet source address.
     *
     * @param addr the MAC address to set as the Ethernet source address.
     */
    public void setActionSetEthernetSrcAddr(MACAddress addr) {
	actionSetEthernetSrcAddr = new ActionSetEthernetAddr(addr);
	actionType = ActionValues.ACTION_SET_DL_SRC;
    }

    /**
     * Get the action to set the Ethernet destination address.
     *
     * @return the action to set the Ethernet destination address.
     */
    @JsonProperty("actionSetEthernetDstAddr")
    public ActionSetEthernetAddr actionSetEthernetDstAddr() {
	return actionSetEthernetDstAddr;
    }

    /**
     * Set the action to set the Ethernet destination address.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetEthernetDstAddr")
    public void setActionSetEthernetDstAddr(ActionSetEthernetAddr action) {
	actionSetEthernetDstAddr = action;
	actionType = ActionValues.ACTION_SET_DL_DST;
    }

    /**
     * Set the action to set the Ethernet destination address.
     *
     * @param addr the MAC address to set as the Ethernet destination address.
     */
    public void setActionSetEthernetDstAddr(MACAddress addr) {
	actionSetEthernetDstAddr = new ActionSetEthernetAddr(addr);
	actionType = ActionValues.ACTION_SET_DL_DST;
    }

    /**
     * Get the action to set the IPv4 source address.
     *
     * @return the action to set the IPv4 source address.
     */
    @JsonProperty("actionSetIPv4SrcAddr")
    public ActionSetIPv4Addr actionSetIPv4SrcAddr() {
	return actionSetIPv4SrcAddr;
    }

    /**
     * Set the action to set the IPv4 source address.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetIPv4SrcAddr")
    public void setActionSetIPv4SrcAddr(ActionSetIPv4Addr action) {
	actionSetIPv4SrcAddr = action;
	actionType = ActionValues.ACTION_SET_NW_SRC;
    }

    /**
     * Set the action to set the IPv4 source address.
     *
     * @param addr the IPv4 address to set as the IPv4 source address.
     */
    public void setActionSetIPv4SrcAddr(IPv4 addr) {
	actionSetIPv4SrcAddr = new ActionSetIPv4Addr(addr);
	actionType = ActionValues.ACTION_SET_NW_SRC;
    }

    /**
     * Get the action to set the IPv4 destination address.
     *
     * @return the action to set the IPv4 destination address.
     */
    @JsonProperty("actionSetIPv4DstAddr")
    public ActionSetIPv4Addr actionSetIPv4DstAddr() {
	return actionSetIPv4DstAddr;
    }

    /**
     * Set the action to set the IPv4 destination address.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetIPv4DstAddr")
    public void setActionSetIPv4DstAddr(ActionSetIPv4Addr action) {
	actionSetIPv4DstAddr = action;
	actionType = ActionValues.ACTION_SET_NW_DST;
    }

    /**
     * Set the action to set the IPv4 destination address.
     *
     * @param addr the IPv4 address to set as the IPv4 destination address.
     */
    public void setActionSetIPv4DstAddr(IPv4 addr) {
	actionSetIPv4DstAddr = new ActionSetIPv4Addr(addr);
	actionType = ActionValues.ACTION_SET_NW_DST;
    }

    /**
     * Get the action to set the IP ToS (DSCP field, 6 bits).
     *
     * @return the action to set the IP ToS (DSCP field, 6 bits).
     */
    @JsonProperty("actionSetIpToS")
    public ActionSetIpToS actionSetIpToS() {
	return actionSetIpToS;
    }

    /**
     * Set the action to set the IP ToS (DSCP field, 6 bits).
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetIpToS")
    public void setActionSetIpToS(ActionSetIpToS action) {
	actionSetIpToS = action;
	actionType = ActionValues.ACTION_SET_NW_TOS;
    }

    /**
     * Set the action to set the IP ToS (DSCP field, 6 bits).
     *
     * @param ipToS the IP ToS (DSCP field, 6 bits) to set.
     */
    public void setActionSetIpToS(byte ipToS) {
	actionSetIpToS = new ActionSetIpToS(ipToS);
	actionType = ActionValues.ACTION_SET_NW_TOS;
    }

    /**
     * Get the action to set the TCP/UDP source port.
     *
     * @return the action to set the TCP/UDP source port.
     */
    @JsonProperty("actionSetTcpUdpSrcPort")
    public ActionSetTcpUdpPort actionSetTcpUdpSrcPort() {
	return actionSetTcpUdpSrcPort;
    }

    /**
     * Set the action to set the TCP/UDP source port.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetTcpUdpSrcPort")
    public void setActionSetTcpUdpSrcPort(ActionSetTcpUdpPort action) {
	actionSetTcpUdpSrcPort = action;
	actionType = ActionValues.ACTION_SET_TP_SRC;
    }

    /**
     * Set the action to set the TCP/UDP source port.
     *
     * @param port the TCP/UDP port to set as the TCP/UDP source port.
     */
    public void setActionSetTcpUdpSrcPort(short port) {
	actionSetTcpUdpSrcPort = new ActionSetTcpUdpPort(port);
	actionType = ActionValues.ACTION_SET_TP_SRC;
    }

    /**
     * Get the action to set the TCP/UDP destination port.
     *
     * @return the action to set the TCP/UDP destination port.
     */
    @JsonProperty("actionSetTcpUdpDstPort")
    public ActionSetTcpUdpPort actionSetTcpUdpDstPort() {
	return actionSetTcpUdpDstPort;
    }

    /**
     * Set the action to set the TCP/UDP destination port.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionSetTcpUdpDstPort")
    public void setActionSetTcpUdpDstPort(ActionSetTcpUdpPort action) {
	actionSetTcpUdpDstPort = action;
	actionType = ActionValues.ACTION_SET_TP_DST;
    }

    /**
     * Set the action to set the TCP/UDP destination port.
     *
     * @param port the TCP/UDP port to set as the TCP/UDP destination port.
     */
    public void setActionSetTcpUdpDstPort(short port) {
	actionSetTcpUdpDstPort = new ActionSetTcpUdpPort(port);
	actionType = ActionValues.ACTION_SET_TP_DST;
    }

    /**
     * Get the action to output to queue on a port.
     *
     * @return the action to output to queue on a port.
     */
    @JsonProperty("actionEnqueue")
    public ActionEnqueue actionEnqueue() { return actionEnqueue; }

    /**
     * Set the action to output to queue on a port.
     *
     * @param action the action to set.
     */
    @JsonProperty("actionEnqueue")
    public void setActionEnqueue(ActionEnqueue action) {
	actionEnqueue = action;
	actionType = ActionValues.ACTION_ENQUEUE;
    }

    /**
     * Set the action to output to queue on a port.
     *
     * @param port the port to set.
     * @param int queueId the queue ID to set.
     */
    public void setActionEnqueue(Port port, int queueId) {
	actionEnqueue = new ActionEnqueue(port, queueId);
	actionType = ActionValues.ACTION_ENQUEUE;
    }

    /**
     * Convert the set of actions to a string.
     *
     * The string has the following form:
     *  [type=XXX action=XXX]
     *
     * @return the set of actions as a string.
     */
    @Override
    public String toString() {
	String ret = "[";
	ret += "type=" + actionType;
	switch (actionType) {
	case ACTION_OUTPUT:
	    ret += " action=" + actionOutput.toString();
	    break;
	case ACTION_SET_VLAN_VID:
	    ret += " action=" + actionSetVlanId.toString();
	    break;
	case ACTION_SET_VLAN_PCP:
	    ret += " action=" + actionSetVlanPriority.toString();
	    break;
	case ACTION_STRIP_VLAN:
	    ret += " action=" + actionStripVlan.toString();
	    break;
	case ACTION_SET_DL_SRC:
	    ret += " action=" + actionSetEthernetSrcAddr.toString();
	    break;
	case ACTION_SET_DL_DST:
	    ret += " action=" + actionSetEthernetDstAddr.toString();
	    break;
	case ACTION_SET_NW_SRC:
	    ret += " action=" + actionSetIPv4SrcAddr.toString();
	    break;
	case ACTION_SET_NW_DST:
	    ret += " action=" + actionSetIPv4DstAddr.toString();
	    break;
	case ACTION_SET_NW_TOS:
	    ret += " action=" + actionSetIpToS.toString();
	    break;
	case ACTION_SET_TP_SRC:
	    ret += " action=" + actionSetTcpUdpSrcPort.toString();
	    break;
	case ACTION_SET_TP_DST:
	    ret += " action=" + actionSetTcpUdpDstPort.toString();
	    break;
	case ACTION_ENQUEUE:
	    ret += " action=" + actionEnqueue.toString();
	    break;
	}
	ret += "]";

	return ret;
    }
}
