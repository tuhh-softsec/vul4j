package net.floodlightcontroller.devicemanager;

import net.floodlightcontroller.core.INetMapStorage;
import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;

public interface IDeviceStorage extends INetMapStorage {
	
	public IDeviceObject addDevice(IDevice device);
	public IDeviceObject updateDevice(IDevice device);
	public void removeDevice(IDevice device);
	public IDeviceObject getDeviceByMac(String mac);
	public IDeviceObject getDeviceByIP(String ip);
	public void changeDeviceAttachments(IDevice device);
	public void changeDeviceIPv4Address(IDevice device);	
}
