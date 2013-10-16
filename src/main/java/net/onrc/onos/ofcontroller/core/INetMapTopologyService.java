package net.onrc.onos.ofcontroller.core;

import java.util.List;

import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.NodePortTuple;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;

public interface INetMapTopologyService extends INetMapService {

	public interface ITopoSwitchService {
		Iterable<ISwitchObject> getActiveSwitches();
		Iterable<ISwitchObject> getAllSwitches();
		Iterable<ISwitchObject> getInactiveSwitches();
		Iterable<IPortObject> getPortsOnSwitch(String dpid);
		IPortObject getPortOnSwitch(String dpid, short port_num);
		void close();

	}
	
	public interface ITopoLinkService {
		List<Link> getActiveLinks();
		List<Link> getLinksOnSwitch(String dpid);
		void close();
	}
	public interface ITopoDeviceService {
		Iterable<IDeviceObject> getActiveDevices();
		Iterable<IDeviceObject> getDevicesOnSwitch(String dpid);
		Iterable<IDeviceObject> getDevicesOnSwitch(String dpid, short port_num);
	}

	public interface ITopoFlowService {
		Boolean flowExists(NodePortTuple src, NodePortTuple dest);
		List<NodePortTuple> getShortestFlowPath(NodePortTuple src, NodePortTuple dest);
		
	}
}
