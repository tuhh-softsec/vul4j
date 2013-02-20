package net.floodlightcontroller.devicemanager;

import net.floodlightcontroller.core.INetMapStorage;
import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;

public interface IDeviceStorage extends INetMapStorage {
	
	public IDeviceObject addDevice(IDevice device);
	public IDeviceObject updateDevice(IDevice device);
	public IDeviceObject removeDevice(IDevice device);
	public IDeviceObject getDeviceByMac(String mac);
	public IDeviceObject getDeviceByIP(String ip);
	
}
