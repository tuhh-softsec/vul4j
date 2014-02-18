package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Interface which needs to be implemented to receive Topology events from
 * NetworkGraph
 *
 * TODO Should these interface hand over Event object or Object in NetworkGraph.
 */
public interface INetworkGraphListener {
    public void putSwitchEvent(SwitchEvent switchEvent);
    public void removeSwitchEvent(SwitchEvent switchEvent);

    public void putPortEvent(PortEvent portEvent);
    public void removePortEvent(PortEvent portEvent);

    public void putLinkEvent(LinkEvent linkEvent);
    public void removeLinkEvent(LinkEvent linkEvent);

    public void putDeviceEvent(DeviceEvent deviceEvent);
    public void removeDeviceEvent(DeviceEvent deviceEvent);

}
