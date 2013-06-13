package net.onrc.onos.util;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowId;

public interface IDBOperation {	
	public ISwitchObject searchSwitch(String dpid);
	public ISwitchObject searchActiveSwitch(String dpid);
	public Iterable<ISwitchObject> getActiveSwitches();
	public Iterable<ISwitchObject> getAllSwitches();
	public Iterable<ISwitchObject> getInactiveSwitches();
	public IDeviceObject searchDevice(String macAddr);
	public IDeviceObject newDevice();
	public void removeDevice(IDeviceObject dev);
	public IPortObject searchPort(String dpid, short number);
	public Iterable<IDeviceObject> getDevices();
	public IFlowPath searchFlowPath(FlowId flowId);
	public IFlowPath newFlowPath();
	public void removeFlowPath(IFlowPath flowPath);
	public IFlowPath getFlowPathByFlowEntry(IFlowEntry flowEntry);
	public Iterable<IFlowPath> getAllFlowPaths();
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId);
	public IFlowEntry newFlowEntry();
	public void removeFlowEntry(IFlowEntry flowEntry);
	public Iterable<IFlowEntry> getAllFlowEntries();
	public IPortObject newPort();
	public ISwitchObject newSwitch();
	public void removePort(IPortObject port);
	public void removeSwitch(ISwitchObject sw);
	public Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries();
}
