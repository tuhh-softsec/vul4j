/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.ramcloud.*;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.structures.FramedVertexIterable;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IBaseObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IIpv4Address;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

public abstract class DBOperation implements IDBOperation {

	protected DBConnection conn;
	private final static Logger log = LoggerFactory.getLogger(DBOperation.class);


	/**
	 * Search and get an active switch object with DPID.
	 * @param dpid DPID of the switch
	 */
	@Override
	public ISwitchObject searchActiveSwitch(String dpid) {
	    ISwitchObject sw = searchSwitch(dpid);
	    if ((sw != null)
		    && sw.getState().equals(ISwitchStorage.SwitchState.ACTIVE.toString())) {
		return sw;
	    }
	    return null;
	}

	/**
	 * Create a new switch and return the created switch object.
	 * @param dpid DPID of the switch
	 */
	@Override
	public ISwitchObject newSwitch(final String dpid) {
	    ISwitchObject obj = (ISwitchObject) conn.getFramedGraph().addVertex(null, ISwitchObject.class);
	    if (obj != null) {
		obj.setType("switch");
		obj.setDPID(dpid);
	    }
	    return obj;
	}

	/**
	 * Get all port objects.
	 */
	@Override
	public Iterable<IPortObject> getAllPorts() {
	    Iterable<IPortObject> ports = conn.getFramedGraph().getVertices("type", "port", IPortObject.class);
	    return ports;
	}

	/**
	 * Get all switch objects.
	 */
	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
	    Iterable<ISwitchObject> switches = conn.getFramedGraph().getVertices("type", "switch", ISwitchObject.class);
	    return switches;
	}

	/**
	 * Get all inactive switch objects.
	 */
	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
	    Iterable<ISwitchObject> switches = conn.getFramedGraph().getVertices("type", "switch", ISwitchObject.class);
	    List<ISwitchObject> inactiveSwitches = new ArrayList<ISwitchObject>();

	    for (ISwitchObject sw : switches) {
		if (sw.getState().equals(ISwitchStorage.SwitchState.INACTIVE.toString())) {
		    inactiveSwitches.add(sw);
		}
	    }
	    return inactiveSwitches;
	}

	/**
	 * Get all flow entries objects where their switches are not updated.
	 */
	@Override
	public Iterable<INetMapTopologyObjects.IFlowEntry> getAllSwitchNotUpdatedFlowEntries() {
	    //TODO: Should use an enum for flow_switch_state
	    return conn.getFramedGraph().getVertices("switch_state", "FE_SWITCH_NOT_UPDATED", INetMapTopologyObjects.IFlowEntry.class);

	}

	/**
	 * Remove specified switch.
	 * @param sw switch object to remove
	 */
	@Override
	public void removeSwitch(ISwitchObject sw) {
	    conn.getFramedGraph().removeVertex(sw.asVertex());
	}

	@Override
	public IPortObject newPort(String dpid, Short portNum) {
	    IPortObject obj = (IPortObject) conn.getFramedGraph().addVertex(null, IPortObject.class);
	    if (obj != null) {
		obj.setType("port");
		String id = dpid + PORT_ID_DELIM + portNum.toString();
		obj.setPortId(id);
		obj.setNumber(portNum);
	    }
	    return obj;
	}

	/**
	* Create a port having specified port number.
	*
	* @param portNumber port number
	*/
	@Override
	@Deprecated
	public IPortObject newPort(Short portNumber) {
	    IPortObject obj = (IPortObject) conn.getFramedGraph().addVertex(null, IPortObject.class);
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
	@Override
	public IPortObject searchPort(String dpid, Short number) {
	    FramedGraph fg = conn.getFramedGraph();
	    if ( fg == null ) return null;
	    String id = dpid + PORT_ID_DELIM + number.toString();
	    Iterator<IPortObject> it = fg.getVertices("port_id", id, IPortObject.class).iterator();
	    return (it.hasNext()) ? it.next() : null;

	}

	/**
	 * Remove the specified switch port.
	 * @param port switch port object to remove
	 */
	@Override
	public void removePort(IPortObject port) {
	    if (conn.getFramedGraph() != null) {
		conn.getFramedGraph().removeVertex(port.asVertex());
	    }
	}

	/**
	 * Create and return a device object.
	 */
	@Override
	public IDeviceObject newDevice() {
	    IDeviceObject obj = (IDeviceObject) conn.getFramedGraph().addVertex(null, IDeviceObject.class);
	    if (obj != null) {
		obj.setType("device");
	    }
	    return obj;
	}

	/**
	 * Get all devices.
	 */
	@Override
	public Iterable<IDeviceObject> getDevices() {
	    return conn.getFramedGraph() != null ? conn.getFramedGraph().getVertices("type", "device", IDeviceObject.class) : null;
	}

	/**
	 * Remove the specified device.
	 * @param dev a device object to remove
	 */
	@Override
	public void removeDevice(IDeviceObject dev) {
	    if (conn.getFramedGraph() != null) {
		conn.getFramedGraph().removeVertex(dev.asVertex());
	    }
	}

	/**
	* Create and return a flow path object.
	*/
	@Override
	public IFlowPath newFlowPath() {
	    IFlowPath flowPath = (IFlowPath)conn.getFramedGraph().addVertex(null, IFlowPath.class);
	    if (flowPath != null) {
		flowPath.setType("flow");
	    }
	    return flowPath;
	}

	/**
	 * Get a flow path object with a flow entry.
	 * @param flowEntry flow entry object
	 */
	@Override
	public IFlowPath getFlowPathByFlowEntry(INetMapTopologyObjects.IFlowEntry flowEntry) {
	    GremlinPipeline<Vertex, IFlowPath> pipe = new GremlinPipeline<Vertex, IFlowPath>();
	    pipe.start(flowEntry.asVertex());
	    pipe.out("flow");
	    FramedVertexIterable<IFlowPath> r = new FramedVertexIterable(conn.getFramedGraph(), pipe, IFlowPath.class);
	    return r.iterator().hasNext() ? r.iterator().next() : null;
	}


	/**
	* Search and get a switch object with DPID.
	*
	* @param dpid DPID of the switch
	*/
	@Override
	public ISwitchObject searchSwitch(final String dpid) {
	    FramedGraph fg = conn.getFramedGraph();
	    if ( fg == null ) return null;
	    Iterator<ISwitchObject> it = fg.getVertices("dpid", dpid, ISwitchObject.class).iterator();
	    return (it.hasNext()) ? it.next() : null;
	}

	/**
	 * Get all active switch objects.
	 */
	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
	    Iterable<ISwitchObject> switches = conn.getFramedGraph().getVertices("type", "switch", ISwitchObject.class);
	    List<ISwitchObject> activeSwitches = new ArrayList<ISwitchObject>();

	    for (ISwitchObject sw : switches) {
		if (sw.getState().equals(ISwitchStorage.SwitchState.ACTIVE.toString())) {
		    activeSwitches.add(sw);
		}
	    }
	    return activeSwitches;
	}

	/**
	 * Search and get a device object having specified MAC address.
	 * @param macAddr MAC address to search and get
	 */
	@Override
	public IDeviceObject searchDevice(String macAddr) {
	    FramedGraph fg = conn.getFramedGraph();
	    if ( fg == null ) return null;
	    Iterator<IDeviceObject> it = fg.getVertices("dl_addr", macAddr, IDeviceObject.class).iterator();
	    return (it.hasNext()) ? it.next() : null;
	}

	/**
	 * Search and get a flow path object with specified flow ID.
	 * @param flowId flow ID to search
	 */
	@Override
	public IFlowPath searchFlowPath(final FlowId flowId) {
	    FramedGraph fg = conn.getFramedGraph();
	    if ( fg == null ) return null;
	    Iterator<IFlowPath> it = fg.getVertices("flow_id", flowId.toString(), IFlowPath.class).iterator();
	    return (it.hasNext()) ? it.next() : null;
	}

	/**
	 * Get all flow path objects.
	 */
	@Override
	public Iterable<IFlowPath> getAllFlowPaths() {
	    Iterable<IFlowPath> flowPaths = conn.getFramedGraph().getVertices("type", "flow", IFlowPath.class);

	    List<IFlowPath> nonNullFlows = new ArrayList<IFlowPath>();

	    for (IFlowPath fp : flowPaths) {
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
	@Override
	public void removeFlowPath(IFlowPath flowPath) {
	    conn.getFramedGraph().removeVertex(flowPath.asVertex());
	}

	/**
	 * Search and get a flow entry object with flow entry ID.
	 * @param flowEntryId flow entry ID to search
	 */
	@Override
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
	    FramedGraph fg = conn.getFramedGraph();
	    if ( fg == null ) return null;
	    Iterator<IFlowEntry> it = fg.getVertices("flow_entry_id", flowEntryId.toString(), IFlowEntry.class).iterator();
	    return (it.hasNext()) ? it.next() : null;
	}

	/**
	 * Get all flow entry objects.
	 */
	@Override
	public Iterable<IFlowEntry> getAllFlowEntries() {
	    return conn.getFramedGraph().getVertices("type", "flow_entry", IFlowEntry.class);
	}

	/**
	 * Remove the specified flow entry.
	 * @param flowEntry flow entry object to remove
	 */
	@Override
	public void removeFlowEntry(IFlowEntry flowEntry) {
	    conn.getFramedGraph().removeVertex(flowEntry.asVertex());
	}

	/**
	 * Create and return a flow entry object.
	 */
	@Override
	public IFlowEntry newFlowEntry() {
	    IFlowEntry flowEntry = (IFlowEntry) conn.getFramedGraph().addVertex(null, IFlowEntry.class);
	    if (flowEntry != null) {
		flowEntry.setType("flow_entry");
	    }
	    return flowEntry;
	}


	public IIpv4Address newIpv4Address() {
		return newVertex("ipv4Address", IIpv4Address.class);
	}

	private <T extends IBaseObject> T newVertex(String type, Class<T> vertexType) {
		T newVertex = (T) conn.getFramedGraph().addVertex(null, vertexType);
		if (newVertex != null) {
			newVertex.setType(type);
		}
		return newVertex;
	}

	public IIpv4Address searchIpv4Address(int intIpv4Address) {
		return searchForVertex("ipv4_address", intIpv4Address, IIpv4Address.class);
	}


	public IIpv4Address ensureIpv4Address(int intIpv4Address) {
		IIpv4Address ipv4Vertex = searchIpv4Address(intIpv4Address);
		if (ipv4Vertex == null) {
			ipv4Vertex = newIpv4Address();
			ipv4Vertex.setIpv4Address(intIpv4Address);
		}
		return ipv4Vertex;
	}


	private <T> T searchForVertex(String propertyName, Object propertyValue, Class<T> vertexType) {
		if (conn.getFramedGraph() != null) {
			Iterator<T> it = conn.getFramedGraph().getVertices(propertyName, propertyValue, vertexType).iterator();
			if (it.hasNext()) {
				return it.next();
			}
		}
		return null;
	}

	public void removeIpv4Address(IIpv4Address ipv4Address) {
		conn.getFramedGraph().removeVertex(ipv4Address.asVertex());
	}

	/**
	 * Get the instance of GraphDBConnection assigned to this class.
	 */
	@Override
	public IDBConnection getDBConnection() {
	    return conn;
	}

	@Override
	public void commit() {
	    conn.commit();
	}

	@Override
	public void rollback() {
	    conn.rollback();
	}

	@Override
	public void close() {
	    conn.close();
	}

	@Override
	public void setVertexProperties(Vertex vertex, Map<String, Object> map) {
		log.debug("setProperties start: size {}", map.size());
		RamCloudVertex v = (RamCloudVertex) vertex;
		v.setProperties(map);
		log.debug("setProperties end: size {}, id {}", map.size(), v.getId());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(ISwitchObject sw: getAllSwitches()) {
			sb.append("sw: " + sw.getDPID() + "\n");
			for(IPortObject port: sw.getPorts()) {
				sb.append("  port: " + port.getPortId() + "\n");
			}
		}
		return sb.toString();
	}
}
