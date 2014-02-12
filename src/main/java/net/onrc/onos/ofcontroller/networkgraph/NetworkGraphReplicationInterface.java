package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Interface to use to update Network Graph triggered by notification.
 *
 * Requested change by these methods will not be propagated down to DataStore
 */
public interface NetworkGraphReplicationInterface {
	public void putSwitchReplicationEvent(SwitchEvent switchEvent);
	public void removeSwitchReplicationEvent(SwitchEvent switchEvent);
	public void putPortReplicationEvent(PortEvent portEvent);
	public void removePortReplicationEvent(PortEvent portEvent);
	public void putLinkReplicationEvent(LinkEvent linkEvent);
	public void removeLinkReplicationEvent(LinkEvent linkEvent);
	public void putDeviceReplicationEvent(DeviceEvent deviceEvent);
	public void removeDeviceReplicationEvent(DeviceEvent deviceEvent);
}
