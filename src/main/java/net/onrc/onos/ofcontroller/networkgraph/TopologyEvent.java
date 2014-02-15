package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Self-contained Topology event Object
 *
 * TODO: For now the topology event contains one of the following events:
 * Switch, Port, Link, Device. In the future it will contain multiple events
 * in a single transaction.
 */
public class TopologyEvent {
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
    TopologyEvent(SwitchEvent switchEvent) {
	this.switchEvent = switchEvent;
    }

    /**
     * Constructor for given Port event.
     *
     * @param portEvent the Port event to use.
     */
    TopologyEvent(PortEvent portEvent) {
	this.portEvent = portEvent;
    }

    /**
     * Constructor for given Link event.
     *
     * @param linkEvent the Link event to use.
     */
    TopologyEvent(LinkEvent linkEvent) {
	this.linkEvent = linkEvent;
    }

    /**
     * Constructor for given Device event.
     *
     * @param deviceEvent the Device event to use.
     */
    TopologyEvent(DeviceEvent deviceEvent) {
	this.deviceEvent = deviceEvent;
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
}
