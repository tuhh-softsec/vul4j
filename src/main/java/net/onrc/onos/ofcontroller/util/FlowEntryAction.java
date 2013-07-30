package net.onrc.onos.ofcontroller.util;

import net.floodlightcontroller.util.MACAddress;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing a single Flow Entry action.
 *
 * A Flow Entry action that needs to be applied to each packet.
 * Note that it contains only a single action. Multiple actions are
 * listed in a list inside @ref FlowEntryActions.
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionOutput(ActionOutput other) {
	    if (other.port != null)
		this.port = new Port(other.port);
	    this.maxLen = other.maxLen;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [port=XXX maxLen=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionOutput(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [port=XXX maxLen=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split(" ");
	    String decode = null;

	    // Decode the "port=XXX" part
	    if (parts.length > 0)
		decode = parts[0];
	    if (decode != null) {
		String[] tokens = decode.split("port=");
		if (tokens.length > 1 && tokens[1] != null) {
		    try {
			Short valueShort = Short.valueOf(tokens[1]);
			port = new Port(valueShort);
		    } catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid action string");
		    }
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }

	    // Decode the "maxLen=XXX" part
	    decode = null;
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		String[] tokens = decode.split("maxLen=");
		if (tokens.length > 1 && tokens[1] != null) {
		    try {
			maxLen = Short.valueOf(tokens[1]);
		    } catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid action string");
		    }
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionSetVlanId(ActionSetVlanId other) {
	    this.vlanId = other.vlanId;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [vlanId=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionSetVlanId(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [vlanId=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("vlanId=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		try {
		    vlanId = Short.valueOf(decode);
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException("Invalid action string");
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionSetVlanPriority(ActionSetVlanPriority other) {
	    this.vlanPriority = other.vlanPriority;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [vlanPriority=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionSetVlanPriority(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [vlanPriority=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("vlanPriority=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		try {
		    vlanPriority = Byte.valueOf(decode);
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException("Invalid action string");
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionStripVlan(ActionStripVlan other) {
	    this.stripVlan = other.stripVlan;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [stripVlan=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionStripVlan(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [stripVlan=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("stripVlan=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		stripVlan = Boolean.valueOf(decode);
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionSetEthernetAddr(ActionSetEthernetAddr other) {
	    if (other.addr != null)
		this.addr = MACAddress.valueOf(other.addr.toLong());
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [addr=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionSetEthernetAddr(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [addr=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("addr=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		try {
		    addr = MACAddress.valueOf(decode);
		} catch (IllegalArgumentException e) {
		    throw new IllegalArgumentException("Invalid action string");
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionSetIPv4Addr(ActionSetIPv4Addr other) {
	    if (other.addr != null)
		this.addr = new IPv4(other.addr);
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [addr=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionSetIPv4Addr(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [addr=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("addr=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		try {
		    addr = new IPv4(decode);
		} catch (IllegalArgumentException e) {
		    throw new IllegalArgumentException("Invalid action string");
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionSetIpToS(ActionSetIpToS other) {
	    this.ipToS = other.ipToS;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [ipToS=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionSetIpToS(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [ipToS=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("ipToS=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		try {
		    ipToS = Byte.valueOf(decode);
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException("Invalid action string");
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionSetTcpUdpPort(ActionSetTcpUdpPort other) {
	    this.port = other.port;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [port=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionSetTcpUdpPort(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [port=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split("port=");
	    String decode = null;

	    // Decode the value
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		try {
		    port = Short.valueOf(decode);
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException("Invalid action string");
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
	 * Copy constructor.
	 *
	 * @param other the object to copy from.
	 */
	public ActionEnqueue(ActionEnqueue other) {
	    if (other.port != null)
		this.port = new Port(other.port);
	    this.queueId = other.queueId;
	}

	/**
	 * Constructor from a string.
	 *
	 * The string has the following form:
	 *  [port=XXX queueId=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public ActionEnqueue(String actionStr) {
	    this.fromString(actionStr);
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

	/**
	 * Convert a string to an action.
	 *
	 * The string has the following form:
	 *  [port=XXX queueId=XXX]
	 *
	 * @param actionStr the action as a string.
	 */
	public void fromString(String actionStr) {
	    String[] parts = actionStr.split(" ");
	    String decode = null;

	    // Decode the "port=XXX" part
	    if (parts.length > 0)
		decode = parts[0];
	    if (decode != null) {
		String[] tokens = decode.split("port=");
		if (tokens.length > 1 && tokens[1] != null) {
		    try {
			Short valueShort = Short.valueOf(tokens[1]);
			port = new Port(valueShort);
		    } catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid action string");
		    }
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }

	    // Decode the "queueId=XXX" part
	    decode = null;
	    if (parts.length > 1)
		decode = parts[1];
	    if (decode != null) {
		decode = decode.replace("]", "");
		String[] tokens = decode.split("queueId=");
		if (tokens.length > 1 && tokens[1] != null) {
		    try {
			queueId = Short.valueOf(tokens[1]);
		    } catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid action string");
		    }
		}
	    } else {
		throw new IllegalArgumentException("Invalid action string");
	    }
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
     * Copy constructor.
     *
     * @param other the object to copy from.
     */
    public FlowEntryAction(FlowEntryAction other) {
	this.actionType = other.actionType;

	//
	if (other.actionOutput != null)
	    this.actionOutput = new ActionOutput(other.actionOutput);
	else
	    this.actionOutput = null;
	//
	if (other.actionSetVlanId != null)
	    this.actionSetVlanId = new ActionSetVlanId(other.actionSetVlanId);
	else
	    this.actionSetVlanId = null;
	//
	if (other.actionSetVlanPriority != null)
	    this.actionSetVlanPriority = new ActionSetVlanPriority(other.actionSetVlanPriority);
	else
	    this.actionSetVlanPriority = null;
	//
	if (other.actionStripVlan != null)
	    this.actionStripVlan = new ActionStripVlan(other.actionStripVlan);
	else
	    this.actionStripVlan = null;
	//
	if (other.actionSetEthernetSrcAddr != null)
	    this.actionSetEthernetSrcAddr = new ActionSetEthernetAddr(other.actionSetEthernetSrcAddr);
	else
	    this.actionSetEthernetSrcAddr = null;
	//
	if (other.actionSetEthernetDstAddr != null)
	    this.actionSetEthernetDstAddr = new ActionSetEthernetAddr(other.actionSetEthernetDstAddr);
	else
	    this.actionSetEthernetDstAddr = null;
	//
	if (other.actionSetIPv4SrcAddr != null)
	    this.actionSetIPv4SrcAddr = new ActionSetIPv4Addr(other.actionSetIPv4SrcAddr);
	else
	    this.actionSetIPv4SrcAddr = null;
	//
	if (other.actionSetIPv4DstAddr != null)
	    this.actionSetIPv4DstAddr = new ActionSetIPv4Addr(other.actionSetIPv4DstAddr);
	else
	    this.actionSetIPv4DstAddr = null;
	//
	if (other.actionSetIpToS != null)
	    this.actionSetIpToS = new ActionSetIpToS(other.actionSetIpToS);
	else
	    this.actionSetIpToS = null;
	//
	if (other.actionSetTcpUdpSrcPort != null)
	    this.actionSetTcpUdpSrcPort = new ActionSetTcpUdpPort(other.actionSetTcpUdpSrcPort);
	else
	    this.actionSetTcpUdpSrcPort = null;
	//
	if (other.actionSetTcpUdpDstPort != null)
	    this.actionSetTcpUdpDstPort = new ActionSetTcpUdpPort(other.actionSetTcpUdpDstPort);
	else
	    this.actionSetTcpUdpDstPort = null;
	//
	if (other.actionEnqueue != null)
	    this.actionEnqueue = new ActionEnqueue(other.actionEnqueue);
	else
	    this.actionEnqueue = null;
    }

    /**
     * Constructor from a string.
     *
     * The string has the following form:
     *  [type=XXX action=XXX]
     *
     * @param actionStr the action as a string.
     */
    public FlowEntryAction(String actionStr) {
	this.fromString(actionStr);
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
     * Convert the action to a string.
     *
     * The string has the following form:
     *  [type=XXX action=XXX]
     *
     * @return the action as a string.
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

    /**
     * Convert a string to an action.
     *
     * The string has the following form:
     *  [type=XXX action=XXX]
     *
     * @param actionStr the action as a string.
     */
    public void fromString(String actionStr) {
	String[] parts = actionStr.split("type=");
	String decode = null;

	// Extract the string after the "type="
	if (parts.length > 1)
	    decode = parts[1];
	if (decode == null)
	    throw new IllegalArgumentException("Invalid action string");

	// Remove the trailing ']'
	if ((decode.length() > 0) && (decode.charAt(decode.length() - 1) == ']')) {
	    decode = decode.substring(0, decode.length() - 1);
	} else {
	    throw new IllegalArgumentException("Invalid action string");
	}

	// Extract the type value and the action value
	parts = decode.split(" action=");

	// Decode the "type=XXX" payload
	if (parts.length > 0)
	    decode = parts[0];
	if (decode != null) {
	    try {
		actionType = Enum.valueOf(ActionValues.class, decode);
	    } catch (IllegalArgumentException e) {
		throw new IllegalArgumentException("Invalid action string");
	    }
	} else {
	    throw new IllegalArgumentException("Invalid action string");
	}

	// Decode the "action=XXX" payload
	decode = null;
	if (parts.length > 1)
	    decode = parts[1];
	if (decode == null)
	    throw new IllegalArgumentException("Invalid action string");
	//
	try {
	    switch (actionType) {
	    case ACTION_OUTPUT:
		actionOutput = new ActionOutput(decode);
		break;
	    case ACTION_SET_VLAN_VID:
		actionSetVlanId = new ActionSetVlanId(decode);
		break;
	    case ACTION_SET_VLAN_PCP:
		actionSetVlanPriority = new ActionSetVlanPriority(decode);
		break;
	    case ACTION_STRIP_VLAN:
		actionStripVlan = new ActionStripVlan(decode);
		break;
	    case ACTION_SET_DL_SRC:
		actionSetEthernetSrcAddr = new ActionSetEthernetAddr(decode);
		break;
	    case ACTION_SET_DL_DST:
		actionSetEthernetDstAddr = new ActionSetEthernetAddr(decode);
		break;
	    case ACTION_SET_NW_SRC:
		actionSetIPv4SrcAddr = new ActionSetIPv4Addr(decode);
		break;
	    case ACTION_SET_NW_DST:
		actionSetIPv4DstAddr = new ActionSetIPv4Addr(decode);
		break;
	    case ACTION_SET_NW_TOS:
		actionSetIpToS = new ActionSetIpToS(decode);
		break;
	    case ACTION_SET_TP_SRC:
		actionSetTcpUdpSrcPort = new ActionSetTcpUdpPort(decode);
		break;
	    case ACTION_SET_TP_DST:
		actionSetTcpUdpDstPort = new ActionSetTcpUdpPort(decode);
		break;
	    case ACTION_ENQUEUE:
		actionEnqueue = new ActionEnqueue(decode);
		break;
	    }
	} catch (IllegalArgumentException e) {
	    throw new IllegalArgumentException("Invalid action string");
	}
    }
}
