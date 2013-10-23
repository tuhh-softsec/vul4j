package net.onrc.onos.ofcontroller.topology;

import java.util.HashMap;
import java.util.Map;

import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;

import org.openflow.util.HexString;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

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
 * A class for storing topology information.
 */
public class Topology {
    private Map<Long, Node> nodesMap;	// The dpid->Node mapping

    public Topology() {
	nodesMap = new HashMap<Long, Node>();
    }

    /**
     * Get a node for a give Node ID.
     *
     * @param nodeId the Node ID to use.
     * @return the corresponding Node if found, otherwise null.
     */
    Node getNode(long nodeId) {
	return nodesMap.get(nodeId);
    }

    /**
     * Read topology state from the database.
     *
     * @param dbHandler the Graph Database handler to use.
     */
    public void readFromDatabase(GraphDBOperation dbHandler) {
	//
	// Fetch the relevant info from the Switch and Port vertices
	// from the Titan Graph.
	//
	Iterable<ISwitchObject> activeSwitches = dbHandler.getActiveSwitches();
	for (ISwitchObject switchObj : activeSwitches) {
	    Vertex nodeVertex = switchObj.asVertex();
	    //
	    // The Switch info
	    //
	    String nodeDpid = nodeVertex.getProperty("dpid").toString();
	    long nodeId = HexString.toLong(nodeDpid);
	    Node me = nodesMap.get(nodeId);
	    if (me == null) {
		me = new Node(nodeId);
		nodesMap.put(nodeId, me);
	    }

	    //
	    // The local Port info
	    //
	    for (Vertex myPortVertex : nodeVertex.getVertices(Direction.OUT, "on")) {
		// Ignore inactive ports
		if (! myPortVertex.getProperty("state").toString().equals("ACTIVE"))
		    continue;

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
		    // Ignore inactive ports
		    if (! neighborPortVertex.getProperty("state").toString().equals("ACTIVE"))
			continue;

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
			Node neighbor = nodesMap.get(neighborId);
			if (neighbor == null) {
			    neighbor = new Node(neighborId);
			    nodesMap.put(neighborId, neighbor);
			}
			me.addNeighbor(neighbor, myPort, neighborPort);
		    }
		}
	    }
	}
	dbHandler.commit();
    }
}
