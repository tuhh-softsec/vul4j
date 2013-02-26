package net.floodlightcontroller.core;

import java.util.List;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.NodePortTuple;
import net.floodlightcontroller.util.DataPath;
import net.floodlightcontroller.util.SwitchPort;

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
	    DataPath getShortestPath(SwitchPort src, SwitchPort dest);
	    Boolean routeExists(SwitchPort src, SwitchPort dest);
	}
	
	public interface ITopoFlowService {
		Boolean flowExists(NodePortTuple src, NodePortTuple dest);
		List<NodePortTuple> getShortestFlowPath(NodePortTuple src, NodePortTuple dest);
		
	}
}
