package net.onrc.onos.util;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowId;

public interface IDBUtils {	
	public ISwitchObject searchSwitch(GraphDBConnection conn, String dpid);
	public IDeviceObject searchDevice(GraphDBConnection conn, String macAddr);
	public IDeviceObject newDevice(GraphDBConnection conn);
	public void removeDevice(GraphDBConnection conn, IDeviceObject dev);
	public IPortObject searchPort(GraphDBConnection conn, String dpid, short number);
	public Iterable<IDeviceObject> getDevices(GraphDBConnection conn);
	public IFlowPath searchFlowPath(GraphDBConnection conn, FlowId flowId);
	public IFlowPath newFlowPath(GraphDBConnection conn);
	public void removeFlowPath(GraphDBConnection conn, IFlowPath flowPath);
        public IFlowPath getFlowPathByFlowEntry(GraphDBConnection conn,
						IFlowEntry flowEntry);
	public Iterable<IFlowPath> getAllFlowPaths(GraphDBConnection conn);
	public IFlowEntry searchFlowEntry(GraphDBConnection conn,
					  FlowEntryId flowEntryId);
	public IFlowEntry newFlowEntry(GraphDBConnection conn);
	public void removeFlowEntry(GraphDBConnection conn,
				    IFlowEntry flowEntry);
	public Iterable<IFlowEntry> getAllFlowEntries(GraphDBConnection conn);
}
