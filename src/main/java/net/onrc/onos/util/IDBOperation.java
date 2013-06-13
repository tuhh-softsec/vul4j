package net.onrc.onos.util;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowId;

public interface IDBOperation {	
	public ISwitchObject newSwitch(String dpid);
	public ISwitchObject searchSwitch(String dpid);
	public ISwitchObject searchActiveSwitch(String dpid);
	public Iterable<ISwitchObject> getActiveSwitches();
	public Iterable<ISwitchObject> getAllSwitches();
	public Iterable<ISwitchObject> getInactiveSwitches();
	public Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries();
	public void removeSwitch(ISwitchObject sw);
	
	public IPortObject newPort(Short portNumber);
	public IPortObject searchPort(String dpid, short number);
	public void removePort(IPortObject port);
	
	public IDeviceObject newDevice();
	public IDeviceObject searchDevice(String macAddr);
	public Iterable<IDeviceObject> getDevices();
	public void removeDevice(IDeviceObject dev);

	public IFlowPath newFlowPath();
	public IFlowPath searchFlowPath(FlowId flowId);
	public IFlowPath getFlowPathByFlowEntry(IFlowEntry flowEntry);
	public Iterable<IFlowPath> getAllFlowPaths();
	public void removeFlowPath(IFlowPath flowPath);

	public IFlowEntry newFlowEntry();
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId);
	public Iterable<IFlowEntry> getAllFlowEntries();
	public void removeFlowEntry(IFlowEntry flowEntry);
}
