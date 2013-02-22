package net.floodlightcontroller.core;

import java.util.List;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.NodePortTuple;

public interface INetMapTopologyService extends INetMapService {

	public interface ITopoSwitchService {
		Iterable<ISwitchObject> getActiveSwitches();
		Iterable<ISwitchObject> getAllSwitches();
		Iterable<ISwitchObject> getInactiveSwitches();
		Iterable<IPortObject> getPortsOnSwitch(String dpid);
		IPortObject getPortOnSwitch(String dpid, short port_num);

	}
	
	public interface ITopoLinkService {
		List<Link> getActiveLinks();
		List<Link> getLinksOnSwitch(String dpid);
	}
	public interface ITopoDeviceService {
		Iterable<IDeviceObject> getActiveDevices();
		Iterable<IDeviceObject> getDevicesOnSwitch(String dpid);
		Iterable<IDeviceObject> getDevicesOnSwitch(String dpid, short port_num);
	}
	
	public interface ITopoRouteService extends IFloodlightService {
		List<NodePortTuple> getShortestPath(NodePortTuple src, NodePortTuple dest);
		Boolean routeExists(NodePortTuple src, NodePortTuple dest);
	}
	
	public interface ITopoFlowService {
		Boolean flowExists(NodePortTuple src, NodePortTuple dest);
		List<NodePortTuple> getShortestFlowPath(NodePortTuple src, NodePortTuple dest);
		
	}
}
