package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Interface used by the Topology Discovery module to write topology-related
 * events.
 */
public interface NetworkGraphDiscoveryInterface {
    /**
     * Switch discovered event.
     *
     * @param switchEvent the switch event.
     */
    public void putSwitchDiscoveryEvent(SwitchEvent switchEvent);

    /**
     * Switch removed event.
     *
     * @param switchEvent the switch event.
     */
    public void removeSwitchDiscoveryEvent(SwitchEvent switchEvent);

    /**
     * Port discovered event.
     *
     * @param portEvent the port event.
     */
    public void putPortDiscoveryEvent(PortEvent portEvent);

    /**
     * Port removed event.
     *
     * @param portEvent the port event.
     */
    public void removePortDiscoveryEvent(PortEvent portEvent);

    /**
     * Link discovered event.
     *
     * @param linkEvent the link event.
     */
    public void putLinkDiscoveryEvent(LinkEvent linkEvent);

    /**
     * Link removed event.
     *
     * @param linkEvent the link event.
     */
    public void removeLinkDiscoveryEvent(LinkEvent linkEvent);

    /**
     * Device discovered event.
     *
     * @param deviceEvent the device event.
     */
    public void putDeviceDiscoveryEvent(DeviceEvent deviceEvent);

    /**
     * Device removed event.
     *
     * @param deviceEvent the device event.
     */
    public void removeDeviceDiscoveryEvent(DeviceEvent deviceEvent);
}
