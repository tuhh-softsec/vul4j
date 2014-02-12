package net.onrc.onos.ofcontroller.networkgraph;

public interface NetworkGraphDiscoveryInterface {
	public void putSwitchEvent(SwitchEvent switchEvent);
	public void removeSwitchEvent(SwitchEvent switchEvent);
	public void putPortEvent(PortEvent portEvent);
	public void removePortEvent(PortEvent portEvent);
	public void putLinkEvent(LinkEvent linkEvent);
	public void removeLinkEvent(LinkEvent linkEvent);
	public void putDeviceEvent(DeviceEvent deviceEvent);
	public void removeDeviceEvent(DeviceEvent deviceEvent);
}
