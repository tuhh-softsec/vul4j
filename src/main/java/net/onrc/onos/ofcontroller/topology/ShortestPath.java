package net.onrc.onos.ofcontroller.topology;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.openflow.util.HexString;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

/**
 * Class to calculate a shortest DataPath between 2 SwitchPorts
 * based on hops in Network Topology.
 */
public class ShortestPath {
    /**
     * Get the shortest path from a source to a destination by
     * using the pre-populated local topology state prepared
     * by method @ref TopologyManager.newDatabaseTopology().
     *
     * For additional documentation and usage, see method
     * @ref TopologyManager.newDatabaseTopology()
     *
     * @param topology the topology handler to use.
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    public static DataPath getTopologyShortestPath(
		Topology topology,
		SwitchPort src, SwitchPort dest) {
	DataPath result_data_path = new DataPath();

	// Initialize the source and destination in the data path to return
	result_data_path.setSrcPort(src);
	result_data_path.setDstPort(dest);

	String dpid_src = src.dpid().toString();
	String dpid_dest = dest.dpid().toString();

	// Get the source vertex
	Node v_src = topology.getNode(src.dpid().value());
	if (v_src == null) {
	    return null;		// Source vertex not found
	}

	// Get the destination vertex
	Node v_dest = topology.getNode(dest.dpid().value());
	if (v_dest == null) {
	    return null;		// Destination vertex not found
	}

	//
	// Test whether we are computing a path from/to the same DPID.
	// If "yes", then just add a single flow entry in the return result.
	//
	if (dpid_src.equals(dpid_dest)) {
	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(src.dpid());
	    flowEntry.setInPort(src.port());
	    flowEntry.setOutPort(dest.port());
	    result_data_path.flowEntries().add(flowEntry);
	    return result_data_path;
	}

	//
	// Implement the Shortest Path computation by using Breath First Search
	//
	Set<Node> visitedSet = new HashSet<Node>();
	Queue<Node> processingList = new LinkedList<Node>();
	Map<Node, Node.Link> previousVertexMap = new HashMap<Node, Node.Link>();
	processingList.add(v_src);
	visitedSet.add(v_src);
	Boolean path_found = false;
	while (! processingList.isEmpty()) {
	    Node nextVertex = processingList.poll();
	    if (v_dest == nextVertex) {
		path_found = true;
		break;
	    }
	    for (Node.Link link : nextVertex.links.values()) {
		Node child = link.neighbor;
		if (! visitedSet.contains(child)) {
		    previousVertexMap.put(child, link);
		    visitedSet.add(child);
		    processingList.add(child);
		}
	    }
	}
	if (! path_found)
	    return null;		// No path found

	// Collect the path as a list of links
	List<Node.Link> resultPath = new LinkedList<Node.Link>();
	Node previousVertex = v_dest;
	while (! v_src.equals(previousVertex)) {
	    Node.Link currentLink = previousVertexMap.get(previousVertex);
	    resultPath.add(currentLink);
	    previousVertex = currentLink.me;
	}
	Collections.reverse(resultPath);

	//
	// Loop through the result and prepare the return result
	// as a list of Flow Entries.
	//
	Port inPort = new Port(src.port().value());
	Port outPort;
	for (Node.Link link: resultPath) {
	    // Setup the outgoing port, and add the Flow Entry
	    outPort = new Port((short)link.myPort);

	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(new Dpid(link.me.nodeId));
	    flowEntry.setInPort(inPort);
	    flowEntry.setOutPort(outPort);
	    result_data_path.flowEntries().add(flowEntry);

	    // Setup the next incoming port
	    inPort = new Port((short)link.neighborPort);
	}
	if (resultPath.size() > 0) {
	    // Add the last Flow Entry
	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(new Dpid(dest.dpid().value()));
	    flowEntry.setInPort(inPort);
	    flowEntry.setOutPort(dest.port());
	    result_data_path.flowEntries().add(flowEntry);
	}

	if (result_data_path.flowEntries().size() > 0)
	    return result_data_path;

	return null;
    }

    /**
     * Get the shortest path from a source to a destination by using
     * the underlying Graph Database.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    public static DataPath getDatabaseShortestPath(GraphDBOperation dbHandler,
					     SwitchPort src, SwitchPort dest) {
	DataPath result_data_path = new DataPath();

	// Initialize the source and destination in the data path to return
	result_data_path.setSrcPort(src);
	result_data_path.setDstPort(dest);

	String dpid_src = src.dpid().toString();
	String dpid_dest = dest.dpid().toString();

	// Get the source and destination switches
	ISwitchObject srcSwitch =
	    dbHandler.searchActiveSwitch(dpid_src);
	ISwitchObject destSwitch =
	    dbHandler.searchActiveSwitch(dpid_dest);
	if (srcSwitch == null || destSwitch == null) {
	    return null;
	}

	//
	// Test whether we are computing a path from/to the same DPID.
	// If "yes", then just add a single flow entry in the return result.
	//
	if (dpid_src.equals(dpid_dest)) {
	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(src.dpid());
	    flowEntry.setInPort(src.port());
	    flowEntry.setOutPort(dest.port());
	    result_data_path.flowEntries().add(flowEntry);
	    dbHandler.commit();
	    return result_data_path;
	}

	Vertex v_src = srcSwitch.asVertex();	
	Vertex v_dest = destSwitch.asVertex();

	//
	// Implement the Shortest Path computation by using Breath First Search
	//
	Set<Vertex> visitedSet = new HashSet<Vertex>();
	Queue<Vertex> processingList = new LinkedList<Vertex>();
	Map<Vertex, Vertex> previousVertexMap = new HashMap<Vertex, Vertex>();

	processingList.add(v_src);
	visitedSet.add(v_src);
	Boolean path_found = false;
	while (! processingList.isEmpty()) {
	    Vertex nextVertex = processingList.poll();
	    if (v_dest.equals(nextVertex)) {
		path_found = true;
		break;
	    }
	    for (Vertex parentPort : nextVertex.getVertices(Direction.OUT, "on")) {
		// Ignore inactive ports
		if (! parentPort.getProperty("state").toString().equals("ACTIVE"))
			continue;

		for (Vertex childPort : parentPort.getVertices(Direction.OUT, "link")) {
		    // Ignore inactive ports
		    if (! childPort.getProperty("state").toString().equals("ACTIVE"))
			continue;

		    for (Vertex child : childPort.getVertices(Direction.IN, "on")) {
			// Ignore inactive switches
			String state = child.getProperty("state").toString();
			if (! state.equals(SwitchState.ACTIVE.toString()))
			    continue;

			if (! visitedSet.contains(child)) {
			    previousVertexMap.put(parentPort, nextVertex);
			    previousVertexMap.put(childPort, parentPort);
			    previousVertexMap.put(child, childPort);
			    visitedSet.add(child);
			    processingList.add(child);
			}
		    }
		}
	    }
	}
	if (! path_found)
	    return null;		// No path found

	List<Vertex> resultPath = new LinkedList<Vertex>();
	Vertex previousVertex = v_dest;
	resultPath.add(v_dest);
	while (! v_src.equals(previousVertex)) {
	    Vertex currentVertex = previousVertexMap.get(previousVertex);
	    resultPath.add(currentVertex);
	    previousVertex = currentVertex;
	}
	Collections.reverse(resultPath);


	//
	// Loop through the result and prepare the return result
	// as a list of Flow Entries.
	//
	long nodeId = 0;
	short portId = 0;
	Port inPort = new Port(src.port().value());
	Port outPort = new Port();
	int idx = 0;
	for (Vertex v: resultPath) {
	    String type = v.getProperty("type").toString();
	    // System.out.println("type: " + type);
	    if (type.equals("port")) {
		//String number = v.getProperty("number").toString();
		// System.out.println("number: " + number);

		Object obj = v.getProperty("number");
		// String class_str = obj.getClass().toString();
		if (obj instanceof Short) {
		    portId = (Short)obj;
		} else if (obj instanceof Integer) {
		    Integer int_nodeId = (Integer)obj;
		    portId = int_nodeId.shortValue();
		    // int int_nodeId = (Integer)obj;
		    // portId = (short)int_nodeId.;
		}
	    } else if (type.equals("switch")) {
		String dpid = v.getProperty("dpid").toString();
		nodeId = HexString.toLong(dpid);

		// System.out.println("dpid: " + dpid);
	    }
	    idx++;
	    if (idx == 1) {
		continue;
	    }
	    int mod = idx % 3;
	    if (mod == 0) {
		// Setup the incoming port
		inPort = new Port(portId);
		continue;
	    }
	    if (mod == 2) {
		// Setup the outgoing port, and add the Flow Entry
		outPort = new Port(portId);

		FlowEntry flowEntry = new FlowEntry();
		flowEntry.setDpid(new Dpid(nodeId));
		flowEntry.setInPort(inPort);
		flowEntry.setOutPort(outPort);
		result_data_path.flowEntries().add(flowEntry);
		continue;
	    }
	}
	if (idx > 0) {
	    // Add the last Flow Entry
	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(new Dpid(nodeId));
	    flowEntry.setInPort(inPort);
	    flowEntry.setOutPort(dest.port());
	    result_data_path.flowEntries().add(flowEntry);
	}

	dbHandler.commit();
	if (result_data_path.flowEntries().size() > 0)
	    return result_data_path;

	return null;
    }
}