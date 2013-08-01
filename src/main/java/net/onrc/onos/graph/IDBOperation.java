package net.onrc.onos.graph;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

public interface IDBOperation {
	public ISwitchObject newSwitch(String dpid);
	public ISwitchObject searchSwitch(String dpid);
	public ISwitchObject searchActiveSwitch(String dpid);
	public Iterable<ISwitchObject> getActiveSwitches();
	public Iterable<ISwitchObject> getAllSwitches();
	public Iterable<ISwitchObject> getInactiveSwitches();
	public Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries();
	public void removeSwitch(ISwitchObject sw);
	
	@Deprecated
	public IPortObject newPort(Short portNumber);
	public IPortObject newPort(String dpid, Short portNum);
	public IPortObject searchPort(String dpid, Short number);
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
	
	public IDBConnection getDBConnection();	
	public void commit();
	public void rollback();
	public void close();
	
}
