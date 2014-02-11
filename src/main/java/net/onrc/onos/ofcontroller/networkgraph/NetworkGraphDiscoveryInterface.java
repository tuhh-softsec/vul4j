package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Set;

public interface NetworkGraphDiscoveryInterface {
	public void putSwitchEvent(SwitchEvent switchEvent);
	public void removeSwitchEvent(SwitchEvent switchEvent);
	public void putPortEvent(PortEvent portEvent);
	public void removePortEvent(PortEvent portEvent);
	public void putLinkEvent(LinkEvent linkEvent);
	public void removeLinkEvent(LinkEvent linkEvent);
	public void updateDeviceEvent(DeviceEvent deviceToUpdate, 
			Set<InetAddress> updatedIpAddrs, Set<Port> updatedAttachmentPoints);
	public void removeDeviceEvent(DeviceEvent deviceEvent);
}
