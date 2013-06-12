package net.onrc.onos.util;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

public interface IDBUtils {	
	public ISwitchObject searchSwitch(GraphDBConnection conn, String dpid);
	public ISwitchObject searchActiveSwitch(GraphDBConnection conn, String dpid);
	public Iterable<ISwitchObject> getActiveSwitches(GraphDBConnection conn);
	public Iterable<ISwitchObject> getAllSwitches(GraphDBConnection conn);
	public Iterable<ISwitchObject> getInactiveSwitches(GraphDBConnection conn);
	

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
	public IPortObject newPort(GraphDBConnection conn);
	ISwitchObject newSwitch(GraphDBConnection conn);
	void removePort(GraphDBConnection conn, IPortObject port);
	void removeSwitch(GraphDBConnection conn, ISwitchObject sw);
	Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries(GraphDBConnection conn);
}
