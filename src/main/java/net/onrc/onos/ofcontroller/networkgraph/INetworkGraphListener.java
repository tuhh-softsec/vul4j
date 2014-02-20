package net.onrc.onos.ofcontroller.networkgraph;

import java.util.Collection;

/**
 * Interface which needs to be implemented to receive Topology events from
 * the NetworkGraph.
 */
public interface INetworkGraphListener {
    /**
     * Network Graph events.
     *
     * @param addedSwitchEvents the Added Switch Events.
     * @param removedSwitchEvents the Removed Switch Events.
     * @param addedPortEvents the Added Port Events.
     * @param removedPortEvents the Removed Port Events.
     * @param addedLinkEvents the Added Link Events.
     * @param removedLinkEvents the Removed Link Events.
     * @param addedDeviceEvents the Added Device Events.
     * @param removedDeviceEvents the Removed Device Events.
     */
    public void networkGraphEvents(Collection<SwitchEvent> addedSwitchEvents,
				   Collection<SwitchEvent> removedSwitchEvents,
				   Collection<PortEvent> addedPortEvents,
				   Collection<PortEvent> removedPortEvents,
				   Collection<LinkEvent> addedLinkEvents,
				   Collection<LinkEvent> removedLinkEvents,
				   Collection<DeviceEvent> addedDeviceEvents,
				   Collection<DeviceEvent> removedDeviceEvents);
}
