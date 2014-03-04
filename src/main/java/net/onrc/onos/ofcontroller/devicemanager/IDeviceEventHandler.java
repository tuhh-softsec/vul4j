package net.onrc.onos.ofcontroller.devicemanager;

public interface IDeviceEventHandler {
	public void addDeviceEvent(Long key, OnosDevice value);
	public void deleteDeviceEvent(Long key, OnosDevice value);
	public void updateDeviceEvent(Long key, OnosDevice value);
}
