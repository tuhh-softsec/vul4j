package net.onrc.onos.graph;

import java.util.ArrayList;
import java.util.List;

import org.openflow.protocol.OFPhysicalPort;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

import com.google.common.base.Stopwatch;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.structures.FramedVertexIterable;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class GraphDBOperation implements IDBOperation {
	private GraphDBConnection conn;

	/**
	 * Create a GraphDBOperation instance from specified GraphDBConnection's instance.
	 * @param dbConnection an instance of GraphDBConnection
	 */
	public GraphDBOperation(GraphDBConnection dbConnection) {
		this.conn = dbConnection;
	}

	/**
	 * Create a GraphDBOperation instance from database configuration path.
	 * @param dbConfPath a path for database configuration file.
	 */
	public GraphDBOperation(final String dbConfPath) {
		this.conn = GraphDBConnection.getInstance(dbConfPath);
	}

	/**
	 * Create a new switch and return the created switch object.
	 * @param dpid DPID of the switch
	 */
	public ISwitchObject newSwitch(String dpid) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		ISwitchObject obj = fg.addVertex(null,ISwitchObject.class);
		if (obj != null) {
			obj.setType("switch");
			obj.setDPID(dpid);
		}
		return obj;
	}

	/**
	 * Search and get a switch object with DPID.
	 * @param dpid DPID of the switch 
	 */
	public ISwitchObject searchSwitch(String dpid) {

		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return (fg != null && fg.getVertices("dpid",dpid).iterator().hasNext()) ? 
				fg.getVertices("dpid",dpid,ISwitchObject.class).iterator().next() : null;
				
	}

	/**
	 * Search and get an active switch object with DPID.
	 * @param dpid DPID of the switch 
	 */
	public ISwitchObject searchActiveSwitch(String dpid) {
	
	    ISwitchObject sw = searchSwitch(dpid);
	    if ((sw != null) &&
	        sw.getState().equals(SwitchState.ACTIVE.toString())) {
	        return sw;
	    }
	    return null;
	}

	/**
	 * Get all switch objects.
	 */
	public Iterable<ISwitchObject> getAllSwitches() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		return switches;
	}

	/**
	 * Get all active switch objects.
	 */
	public Iterable<ISwitchObject> getActiveSwitches() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		List<ISwitchObject> activeSwitches = new ArrayList<ISwitchObject>();
	
		for (ISwitchObject sw: switches) {
			if(sw.getState().equals(SwitchState.ACTIVE.toString())) {
				activeSwitches.add(sw);
			}
		}
		return activeSwitches;
	}

	/**
	 * Get all inactive switch objects.
	 */
	public Iterable<ISwitchObject> getInactiveSwitches() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		List<ISwitchObject> inactiveSwitches = new ArrayList<ISwitchObject>();
	
		for (ISwitchObject sw: switches) {
			if(sw.getState().equals(SwitchState.INACTIVE.toString())) {
				inactiveSwitches.add(sw);
			}
		}
		return inactiveSwitches;
	}

	/**
	 * Get all flow entries' objects where their switches are not updated.
	 */
	public Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		//TODO: Should use an enum for flow_switch_state
		return fg.getVertices("switch_state", "FE_SWITCH_NOT_UPDATED", IFlowEntry.class);
	}

	/**
	 * Remove specified switch.
	 * @param sw switch object to remove
	 */
	public void removeSwitch(ISwitchObject sw) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		fg.removeVertex(sw.asVertex());		
	}
	
	@Override
	public IPortObject newPort(String dpid, Short portNumber) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IPortObject obj = fg.addVertex(null,IPortObject.class);
		if (obj != null) {
			obj.setType("port");
			String id = dpid + portNumber.toString();
			obj.setPortId(id);
			obj.setNumber(portNumber);
		}
		return obj;	
		
	}

	/**
	 * Create a port having specified port number.
	 * @param portNumber port number
	 */
	@Deprecated
	public IPortObject newPort(Short portNumber) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IPortObject obj = fg.addVertex(null,IPortObject.class);
		if (obj != null) {
			obj.setType("port");
			obj.setNumber(portNumber);
		}
		return obj;
	}

	/**
	 * Search and get a port object of specified switch and port number.
	 * @param dpid DPID of a switch
	 * @param number port number of the switch's port
	 */
	public IPortObject searchPort(String dpid, Short number) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		String id = dpid + number.toString();
		return (fg != null && fg.getVertices("port_id",id).iterator().hasNext()) ? 
				fg.getVertices("port_id",id,IPortObject.class).iterator().next() : null;
	}

	/**
	 * Remove the specified switch port.
	 * @param port switch port object to remove
	 */
	public void removePort(IPortObject port) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
//		EventGraph<TitanGraph> eg = conn.getEventGraph();
		if (fg != null) fg.removeVertex(port.asVertex());		
	}

	/**
	 * Create and return a device object.
	 */
	public IDeviceObject newDevice() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IDeviceObject obj = fg.addVertex(null,IDeviceObject.class);
		if (obj != null) obj.setType("device");
		return obj;
	}

	/**
	 * Search and get a device object having specified MAC address.
	 * @param macAddr MAC address to search and get
	 */
	public IDeviceObject searchDevice(String macAddr) {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		return (fg != null && fg.getVertices("dl_addr",macAddr).iterator().hasNext()) ?
			fg.getVertices("dl_addr",macAddr, IDeviceObject.class).iterator().next() : null;
	}

	/**
	 * Get all devices.
	 */
	public Iterable<IDeviceObject> getDevices() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		return fg != null ? fg.getVertices("type","device",IDeviceObject.class) : null;
	}

	/**
	 * Remove the specified device.
	 * @param dev a device object to remove
	 */
	public void removeDevice(IDeviceObject dev) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		if (fg != null) fg.removeVertex(dev.asVertex());		
	}

	/**
	 * Create and return a flow path object.
	 */
	public IFlowPath newFlowPath() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IFlowPath flowPath = fg.addVertex(null, IFlowPath.class);
		if (flowPath != null) flowPath.setType("flow");
		return flowPath;
	}

	/**
	 * Search and get a flow path object with specified flow ID.
	 * @param flowId flow ID to search
	 */
	public IFlowPath searchFlowPath(FlowId flowId) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("flow_id", flowId.toString()).iterator().hasNext() ? 
		    fg.getVertices("flow_id", flowId.toString(),
				   IFlowPath.class).iterator().next() : null;
	}

	/**
	 * Get a flow path object with a flow entry.
	 * @param flowEntry flow entry object
	 */
	public IFlowPath getFlowPathByFlowEntry(IFlowEntry flowEntry) {
		GremlinPipeline<Vertex, IFlowPath> pipe = new GremlinPipeline<Vertex, IFlowPath>();
		pipe.start(flowEntry.asVertex());
		pipe.out("flow");
		FramedVertexIterable<IFlowPath> r = new FramedVertexIterable(conn.getFramedGraph(), (Iterable) pipe, IFlowPath.class);
		return r.iterator().hasNext() ? r.iterator().next() : null;
	}

	/**
	 * Get all flow path objects.
	 */
    public Iterable<IFlowPath> getAllFlowPaths() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<IFlowPath> flowPaths = fg.getVertices("type", "flow", IFlowPath.class);
		
		List<IFlowPath> nonNullFlows = new ArrayList<IFlowPath>();

		for (IFlowPath fp: flowPaths) {
			if (fp.getFlowId() != null) {
				nonNullFlows.add(fp);
			}
		}
		return nonNullFlows;
	}

    /**
     * Remove the specified flow path.
     * @param flowPath flow path object to remove
     */
	public void removeFlowPath(IFlowPath flowPath) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		fg.removeVertex(flowPath.asVertex());
	}

	/**
	 * Create and return a flow entry object.
	 */
	public IFlowEntry newFlowEntry() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IFlowEntry flowEntry = fg.addVertex(null, IFlowEntry.class);
		if (flowEntry != null) flowEntry.setType("flow_entry");
		return flowEntry;
	}

	/**
	 * Search and get a flow entry object with flow entry ID.
	 * @param flowEntryId flow entry ID to search
	 */
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("flow_entry_id", flowEntryId.toString()).iterator().hasNext() ? 
		    fg.getVertices("flow_entry_id", flowEntryId.toString(),
				   IFlowEntry.class).iterator().next() : null;
	}

	/**
	 * Get all flow entry objects.
	 */
	public Iterable<IFlowEntry> getAllFlowEntries() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("type", "flow_entry", IFlowEntry.class);
	}

	/**
	 * Remove the specified flow entry.
	 * @param flowEntry flow entry object to remove
	 */
	public void removeFlowEntry(IFlowEntry flowEntry) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		fg.removeVertex(flowEntry.asVertex());
	}
	
	/**
	 * Get the instance of GraphDBConnection assigned to this class.
	 */
	public IDBConnection getDBConnection() {
		return conn;
	}
	
	/**
	 * Commit changes for the graph.
	 */
	public void commit() {
		conn.commit();
	}

	/**
	 * Rollback changes for the graph.
	 */
	public void rollback() {
		conn.rollback();
	}

	/**
	 * Close the connection of the assigned GraphDBConnection.
	 */
	public void close() {
		conn.close();
	}


}
