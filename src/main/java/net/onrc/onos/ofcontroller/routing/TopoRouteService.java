package net.onrc.onos.ofcontroller.routing;

import java.util.ArrayList;
import java.util.Collection;
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
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;


/**
 * A class for storing Node and Link information for fast computation
 * of shortest paths.
 */
class Node {
    /**
     * A class for storing Link information for fast computation of shortest
     * paths.
     */
    class Link {
	public Node me;			// The node this link originates from
	public Node neighbor;		// The neighbor node on the other side
	public short myPort;		// Local port number for the link
	public short neighborPort;	// Neighbor port number for the link

	/**
	 * Link constructor.
	 *
	 * @param me the node this link originates from.
	 * @param the neighbor node on the other side of the link.
	 * @param myPort local port number for the link.
	 * @param neighborPort neighrobr port number for the link.
	 */
	public Link(Node me, Node neighbor, short myPort, short neighborPort) {
	    this.me = me;
	    this.neighbor = neighbor;
	    this.myPort = myPort;
	    this.neighborPort = neighborPort;
	}
    };

    public long nodeId;			// The node ID
    public HashMap<Short, Link> links;	// The links originating from this node

    /**
     * Node constructor.
     *
     * @param nodeId the node ID.
     */
    public Node(long nodeId) {
	this.nodeId = nodeId;
	links = new HashMap<Short, Link>();
    }

    /**
     * Add a neighbor.
     *
     * A new link to the neighbor will be created. 
     *
     * @param neighbor the neighbor to add.
     * @param myPort the local port number for the link to the neighbor.
     * @param neighborPort the neighbor port number for the link.
     */
    public void addNeighbor(Node neighbor, short myPort, short neighborPort) {
	Link link = new Link(this, neighbor, myPort, neighborPort);
	links.put(myPort, link);
    }
};

/**
 * A class for implementing Topology Route Service.
 */
public class TopoRouteService implements ITopoRouteService {

    /** The logger. */
    private static Logger log =
	LoggerFactory.getLogger(TopoRouteService.class);
    
    protected GraphDBOperation op;


    /**
     * Default constructor.
     */
    public TopoRouteService() {
    }

    /**
     * Constructor for given database configuration file.
     *
     * @param config the database configuration file to use for
     * the initialization.
     */
    public TopoRouteService(String config) {
	this.init(config);
    }

    /**
     * Init the module.
     *
     * @param config the database configuration file to use for
     * the initialization.
     */
    public void init(String config) {
	try {
	    op = new GraphDBOperation(config);
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
    }

    /**
     * Close the service. It will close the corresponding database connection.
     */
    public void close() {
	op.close();
    }

    /**
     * Set the database operation handler.
     *
     * @param init_op the database operation handler to use for the
     * initialization.
     */
    public void setDbOperationHandler(GraphDBOperation init_op) {
    	op = init_op;
    }

    /**
     * Fetch the Switch and Ports info from the Titan Graph
     * and return it for fast access during the shortest path
     * computation.
     *
     * After fetching the state, method @ref getTopoShortestPath()
     * can be used for fast shortest path computation.
     *
     * Note: There is certain cost to fetch the state, hence it should
     * be used only when there is a large number of shortest path
     * computations that need to be done on the same topology.
     * Typically, a single call to @ref prepareShortestPathTopo()
     * should be followed by a large number of calls to
     * method @ref getTopoShortestPath().
     * After the last @ref getTopoShortestPath() call,
     * method @ref dropShortestPathTopo() should be used to release
     * the internal state that is not needed anymore:
     *
     *       Map<Long, ?> shortestPathTopo;
     *       shortestPathTopo = prepareShortestPathTopo();
     *       for (int i = 0; i < 10000; i++) {
     *           dataPath = getTopoShortestPath(shortestPathTopo, ...);
     *           ...
     *        }
     *        dropShortestPathTopo(shortestPathTopo);
     *
     * @return the Shortest Path info handler stored in a map.
     */
    public Map<Long, ?> prepareShortestPathTopo() {
	Map<Long, Node> shortestPathTopo = new HashMap<Long, Node>();

	//
	// Fetch the relevant info from the Switch and Port vertices
	// from the Titan Graph.
	//
	Iterable<ISwitchObject> nodes = op.getActiveSwitches();
	for (ISwitchObject switchObj : nodes) {
	    Vertex nodeVertex = switchObj.asVertex();
	    //
	    // The Switch info
	    //
	    String nodeDpid = nodeVertex.getProperty("dpid").toString();
	    long nodeId = HexString.toLong(nodeDpid);
	    Node me = shortestPathTopo.get(nodeId);
	    if (me == null) {
		me = new Node(nodeId);
		shortestPathTopo.put(nodeId, me);
	    }

	    //
	    // The local Port info
	    //
	    for (Vertex myPortVertex : nodeVertex.getVertices(Direction.OUT, "on")) {
		short myPort = 0;
		Object obj = myPortVertex.getProperty("number");
		if (obj instanceof Short) {
		    myPort = (Short)obj;
		} else if (obj instanceof Integer) {
		    Integer int_nodeId = (Integer)obj;
		    myPort = int_nodeId.shortValue();
		}

		//
		// The neighbor Port info
		//
		for (Vertex neighborPortVertex : myPortVertex.getVertices(Direction.OUT, "link")) {
		    short neighborPort = 0;
		    obj = neighborPortVertex.getProperty("number");
		    if (obj instanceof Short) {
			neighborPort = (Short)obj;
		    } else if (obj instanceof Integer) {
			Integer int_nodeId = (Integer)obj;
			neighborPort = int_nodeId.shortValue();
		    }
		    //
		    // The neighbor Switch info
		    //
		    for (Vertex neighborVertex : neighborPortVertex.getVertices(Direction.IN, "on")) {
			// Ignore inactive switches
			String state = neighborVertex.getProperty("state").toString();
			if (! state.equals(SwitchState.ACTIVE.toString()))
			    continue;

			String neighborDpid = neighborVertex.getProperty("dpid").toString();
			long neighborId = HexString.toLong(neighborDpid);
			Node neighbor = shortestPathTopo.get(neighborId);
			if (neighbor == null) {
			    neighbor = new Node(neighborId);
			    shortestPathTopo.put(neighborId, neighbor);
			}
			me.addNeighbor(neighbor, myPort, neighborPort);
		    }
		}
	    }
	}
	op.commit();

	return shortestPathTopo;
    }

    /**
     * Release the state that was populated by
     * method @ref prepareShortestPathTopo().
     *
     * See the documentation for method @ref prepareShortestPathTopo()
     * for additional information and usage.
     *
     * @param shortestPathTopo the Shortest Path info handler to release.
     */
    public void dropShortestPathTopo(Map<Long, ?> shortestPathTopo) {
	shortestPathTopo = null;
    }

    /**
     * Get the shortest path from a source to a destination by
     * using the pre-populated local topology state prepared
     * by method @ref prepareShortestPathTopo().
     *
     * See the documentation for method @ref prepareShortestPathTopo()
     * for additional information and usage.
     *
     * @param shortestPathTopoHandler the Shortest Path info handler
     * to use.
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    public DataPath getTopoShortestPath(Map<Long, ?> shortestPathTopoHandler,
					SwitchPort src, SwitchPort dest) {
	@SuppressWarnings("unchecked")
	Map<Long, Node> shortestPathTopo = (Map)shortestPathTopoHandler;
	DataPath result_data_path = new DataPath();

	// Initialize the source and destination in the data path to return
	result_data_path.setSrcPort(src);
	result_data_path.setDstPort(dest);

	String dpid_src = src.dpid().toString();
	String dpid_dest = dest.dpid().toString();

	// Get the source vertex
	Node v_src = shortestPathTopo.get(src.dpid().value());
	if (v_src == null) {
	    return null;		// Source vertex not found
	}

	// Get the destination vertex
	Node v_dest = shortestPathTopo.get(dest.dpid().value());
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
	    outPort = new Port(link.myPort);

	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(new Dpid(link.me.nodeId));
	    flowEntry.setInPort(inPort);
	    flowEntry.setOutPort(outPort);
	    result_data_path.flowEntries().add(flowEntry);

	    // Setup the next incoming port
	    inPort = new Port(link.neighborPort);
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
     * Get the shortest path from a source to a destination.
     *
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    @Override
    public DataPath getShortestPath(SwitchPort src, SwitchPort dest) {
	DataPath result_data_path = new DataPath();

	// Initialize the source and destination in the data path to return
	result_data_path.setSrcPort(src);
	result_data_path.setDstPort(dest);

	String dpid_src = src.dpid().toString();
	String dpid_dest = dest.dpid().toString();

	// Get the source and destination switches
	ISwitchObject srcSwitch =
	    op.searchActiveSwitch(dpid_src);
	ISwitchObject destSwitch =
	    op.searchActiveSwitch(dpid_dest);
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
	    op.commit();
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
		for (Vertex childPort : parentPort.getVertices(Direction.OUT, "link")) {
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
		String number = v.getProperty("number").toString();
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

	op.commit();
	if (result_data_path.flowEntries().size() > 0)
	    return result_data_path;

	return null;
    }

    /**
     * Test whether a route exists from a source to a destination.
     *
     * @param src the source node for the test.
     * @param dest the destination node for the test.
     * @return true if a route exists, otherwise false.
     */
    @Override
    public Boolean routeExists(SwitchPort src, SwitchPort dest) {
	DataPath dataPath = getShortestPath(src, dest);
	return (dataPath != null);
    }
}
