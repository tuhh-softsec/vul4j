package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Self-contained Topology event Object
 *
 * TODO: For now the topology event contains one of the following events:
 * Switch, Port, Link, Device. In the future it will contain multiple events
 * in a single transaction.
 */
public class TopologyEvent {
    public static final String NOBODY = "";
    String originID = NOBODY;
    SwitchEvent switchEvent = null;		// Set for Switch event
    PortEvent portEvent = null;			// Set for Port event
    LinkEvent linkEvent = null;			// Set for Link event
    DeviceEvent deviceEvent = null;		// Set for Device event

    /**
     * Default constructor.
     */
    public TopologyEvent() {
    }

    /**
     * Constructor for given Switch event.
     *
     * @param switchEvent the Switch event to use.
     */
    TopologyEvent(SwitchEvent switchEvent, String originID) {
	this.switchEvent = switchEvent;
	setOriginID(originID);
    }

    /**
     * Constructor for given Port event.
     *
     * @param portEvent the Port event to use.
     */
    TopologyEvent(PortEvent portEvent, String originID) {
	this.portEvent = portEvent;
	setOriginID(originID);
    }

    /**
     * Constructor for given Link event.
     *
     * @param linkEvent the Link event to use.
     */
    TopologyEvent(LinkEvent linkEvent, String originID) {
	this.linkEvent = linkEvent;
	setOriginID(originID);
    }

    /**
     * Constructor for given Device event.
     *
     * @param deviceEvent the Device event to use.
     */
    TopologyEvent(DeviceEvent deviceEvent, String originID) {
	this.deviceEvent = deviceEvent;
	setOriginID(originID);
    }

    /**
     * Get the string representation of the event.
     *
     * @return the string representation of the event.
     */
    @Override
    public String toString() {
	if (switchEvent != null)
	    return switchEvent.toString();
	if (portEvent != null)
	    return portEvent.toString();
	if (linkEvent != null)
	    return linkEvent.toString();
	if (deviceEvent != null)
	    return deviceEvent.toString();
	return null;
    }

    /**
     * Get the Topology event ID.
     *
     * @return the Topology event ID.
     */
    public byte[] getID() {
	if (switchEvent != null)
	    return switchEvent.getID();
	if (portEvent != null)
	    return portEvent.getID();
	if (linkEvent != null)
	    return linkEvent.getID();
	if (deviceEvent != null)
	    return deviceEvent.getID();
	return null;
    }

    public String getOriginID() {
	return originID;
    }

    void setOriginID(String originID) {
	if (originID != null) {
	    this.originID = originID;
	} else {
	    this.originID = NOBODY;
	}
    }
}
