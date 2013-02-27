package net.onrc.onos.util;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;

public interface IDBUtils {	
	public ISwitchObject searchSwitch(GraphDBConnection conn, String dpid);
	public IDeviceObject searchDevice(GraphDBConnection conn, String macAddr);
	public IDeviceObject newDevice(GraphDBConnection conn);
	public void removeDevice(GraphDBConnection conn, IDeviceObject dev);
	public IPortObject searchPort(GraphDBConnection conn, String dpid, short number);
	public Iterable<IDeviceObject> getDevices(GraphDBConnection conn);
}
