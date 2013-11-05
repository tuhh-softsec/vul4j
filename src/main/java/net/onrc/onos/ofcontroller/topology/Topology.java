package net.onrc.onos.ofcontroller.topology;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

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
	public int myPort;		// Local port ID for the link
	public int neighborPort;	// Neighbor port ID for the link

	/**
	 * Link constructor.
	 *
	 * @param me the node this link originates from.
	 * @param the neighbor node on the other side of the link.
	 * @param myPort local port ID for the link.
	 * @param neighborPort neighbor port ID for the link.
	 */
	public Link(Node me, Node neighbor, int myPort, int neighborPort) {
	    this.me = me;
	    this.neighbor = neighbor;
	    this.myPort = myPort;
	    this.neighborPort = neighborPort;
	}
    };

    public long nodeId;				// The node ID
    public TreeMap<Integer, Link> links;	// The links from this node:
						//     (src PortID -> Link)
    private TreeMap<Integer, Link> reverseLinksMap; // The links to this node:
						//     (dst PortID -> Link)
    private TreeMap<Integer, Integer> portsMap;	// The ports on this node:
						//     (PortID -> PortID)
						// TODO: In the future will be:
						//     (PortID -> Port)

    /**
     * Node constructor.
     *
     * @param nodeId the node ID.
     */
    public Node(long nodeId) {
	this.nodeId = nodeId;
	links = new TreeMap<Integer, Link>();
	reverseLinksMap = new TreeMap<Integer, Link>();
	portsMap = new TreeMap<Integer, Integer>();
    }

    /**
     * Get all ports.
     *
     * @return all ports.
     */
    public Map<Integer, Integer> ports() {
	return portsMap;
    }

    /**
     * Get the port for a given Port ID.
     *
     * Note: For now the port itself is just the Port ID. In the future
     * it might contain more information.
     *
     * @return the port if found, otherwise null.
     */
    public Integer getPort(int portId) {
	return portsMap.get(portId);
    }

    /**
     * Add a port for a given Port ID.
     *
     * Note: For now the port itself is just the Port ID. In the future
     * it might contain more information.
     *
     * @param portId the Port ID of the port to add.
     * @return the added Port.
     */
    Integer addPort(int portId) {
	Integer port = new Integer(portId);
	portsMap.put(portId, port);
	return port;
    }

    /**
     * Remove a port for a given Port ID.
     *
     * NOTE: The outgoing and incoming links using this port are removed as
     * well.
     */
    void removePort(int portId) {
	// Remove the outgoing link
	Link link = getLink(portId);
	if (link != null) {
	    link.neighbor.removeReverseLink(link);
	    removeLink(portId);
	}

	// Remove the incoming link
	Link reverseLink = reverseLinksMap.get(portId);
	if (reverseLink != null) {
	    // NOTE: reverseLink.myPort is the neighbor's outgoing port
	    reverseLink.neighbor.removeLink(reverseLink.myPort);
	    removeReverseLink(reverseLink);
	}

	portsMap.remove(portId);
    }

    /**
     * Get a link on a port to a neighbor.
     *
     * @param myPortId the local port ID for the link to the neighbor.
     * @return the link if found, otherwise null.
     */
    public Link getLink(int myPortId) {
	return links.get(myPortId);
    }

    /**
     * Add a link to a neighbor.
     *
     * @param myPortId the local port ID for the link to the neighbor.
     * @param neighbor the neighbor for the link.
     * @param neighborPortId the neighbor port ID for the link.
     * @return the added Link.
     */
    public Link addLink(int myPortId, Node neighbor, int neighborPortId) {
	Link link = new Link(this, neighbor, myPortId, neighborPortId);
	links.put(myPortId, link);
	neighbor.addReverseLink(link);
	return link;
    }

    /**
     * Add a reverse link from a neighbor.
     *
     * @param link the reverse link from a neighbor to add.
     */
    private void addReverseLink(Link link) {
	// NOTE: link.neghborPort is my port
	reverseLinksMap.put(link.neighborPort, link);
    }

    /**
     * Remove a link to a neighbor.
     *
     * @param myPortId the local port ID for the link to the neighbor.
     */
    public void removeLink(int myPortId) {
	links.remove(myPortId);
    }

    /**
     * Remove a reverse link from a neighbor.
     *
     * @param link the reverse link from a neighbor to remove.
     */
    private void removeReverseLink(Link link) {
	// NOTE: link.neghborPort is my port
	reverseLinksMap.remove(link.neighborPort);
    }
};

/**
 * A class for storing topology information.
 */
public class Topology {
    private Map<Long, Node> nodesMap;	// The dpid->Node mapping

    /**
     * Default constructor.
     */
    public Topology() {
	nodesMap = new TreeMap<Long, Node>();
    }

    /**
     * Add a topology element to the topology.
     *
     * @param topologyElement the topology element to add.
     * @return true if the topology was modified, otherwise false.
     */
    public boolean addTopologyElement(TopologyElement topologyElement) {
	boolean isModified = false;

	switch (topologyElement.getType()) {
	case ELEMENT_SWITCH: {
	    // Add the switch
	    Node node = getNode(topologyElement.getSwitch());
	    if (node == null) {
		node = addNode(topologyElement.getSwitch());
		isModified = true;
	    }
	    break;
	}
	case ELEMENT_PORT: {
	    // Add the switch
	    Node node = getNode(topologyElement.getSwitch());
	    if (node == null) {
		node = addNode(topologyElement.getSwitch());
		isModified = true;
	    }
	    // Add the port for the switch
	    Integer port = node.getPort(topologyElement.getSwitchPort());
	    if (port == null) {
		node.addPort(topologyElement.getSwitchPort());
		isModified = true;
	    }
	    break;
	}
	case ELEMENT_LINK: {
	    // Add the "from" switch
	    Node fromNode = getNode(topologyElement.getFromSwitch());
	    if (fromNode == null) {
		fromNode = addNode(topologyElement.getFromSwitch());
		isModified = true;
	    }
	    // Add the "to" switch
	    Node toNode = getNode(topologyElement.getToSwitch());
	    if (toNode == null) {
		toNode = addNode(topologyElement.getToSwitch());
		isModified = true;
	    }
	    // Add the "from" port
	    Integer fromPort = fromNode.getPort(topologyElement.getFromPort());
	    if (fromPort == null) {
		fromNode.addPort(topologyElement.getFromPort());
		isModified = true;
	    }
	    // Add the "to" port
	    Integer toPort = fromNode.getPort(topologyElement.getToPort());
	    if (toPort == null) {
		toNode.addPort(topologyElement.getToPort());
		isModified = true;
	    }
	    Node.Link link = fromNode.getLink(topologyElement.getFromPort());
	    if (link == null) {
		fromNode.addLink(topologyElement.getFromPort(),
				 toNode,
				 topologyElement.getToPort());
		isModified = true;
	    }

	    break;
	}
	}

	return isModified;
    }

    /**
     * Remove a topology element from the topology.
     *
     * @param topologyElement the topology element to remove.
     * @return true if the topology was modified, otherwise false.
     */
    public boolean removeTopologyElement(TopologyElement topologyElement) {
	boolean isModified = false;

	switch (topologyElement.getType()) {
	case ELEMENT_SWITCH: {
	    // Remove the switch
	    Node node = getNode(topologyElement.getSwitch());
	    if (node != null) {
		removeNode(node);
		isModified = true;
	    }
	    break;
	}
	case ELEMENT_PORT: {
	    // Find the switch
	    Node node = getNode(topologyElement.getSwitch());
	    if (node == null)
		break;
	    // Remove the port for the switch
	    Integer port = node.getPort(topologyElement.getSwitchPort());
	    if (port != null) {
		node.removePort(topologyElement.getSwitchPort());
		isModified = true;
	    }
	    break;
	}
	case ELEMENT_LINK: {
	    // Find the "from" switch
	    Node fromNode = getNode(topologyElement.getFromSwitch());
	    if (fromNode == null)
		break;
	    // Remove the link originating from the "from" port
	    Node.Link link = fromNode.getLink(topologyElement.getFromPort());
	    if (link != null) {
		fromNode.removeLink(topologyElement.getFromPort());
		isModified = true;
	    }
	    break;
	}
	}

	return isModified;
    }

    /**
     * Get a node for a given Node ID.
     *
     * @param nodeId the Node ID to use.
     * @return the corresponding Node if found, otherwise null.
     */
    Node getNode(long nodeId) {
	return nodesMap.get(nodeId);
    }

    /**
     * Add a node for a given Node ID.
     *
     * @param nodeId the Node ID to use.
     * @return the added Node.
     */
    Node addNode(long nodeId) {
	Node node = new Node(nodeId);
	nodesMap.put(nodeId, node);
	return node;
    }

    /**
     * Remove an existing node.
     *
     * @param node the Node to remove.
     */
    void removeNode(Node node) {
	//
	// Remove all ports one-by-one. This operation will also remove the
	// incoming links originating from the neighbors.
	//
	// NOTE: We have to extract all Port IDs in advance, otherwise we
	// cannot loop over the Ports collection and remove entries at the
	// same time.
	// TODO: If there is a large number of ports, the implementation
	// below can be sub-optimal. It should be refactored as follows:
	//   1. Modify removePort() to perform all the cleanup, except
	//     removing the Port entry from the portsMap
	//   2. Call portsMap.clear() at the end of this method
	//   3. In all other methods: if removePort() is called somewhere else,
	//      add an explicit removal of the Port entry from the portsMap.
	//
	List<Integer> allPortIdKeys = new LinkedList<Integer>();
	allPortIdKeys.addAll(node.ports().keySet());
	for (Integer portId : allPortIdKeys)
	    node.removePort(portId);

	nodesMap.remove(node.nodeId);
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
	    if (me == null)
		me = addNode(nodeId);

	    //
	    // The local Port info
	    //
	    for (Vertex myPortVertex : nodeVertex.getVertices(Direction.OUT, "on")) {
		// Ignore inactive ports
		if (! myPortVertex.getProperty("state").toString().equals("ACTIVE"))
		    continue;

		int myPort = 0;
		Object obj = myPortVertex.getProperty("number");
		if (obj instanceof Short) {
		    myPort = (Short)obj;
		} else if (obj instanceof Integer) {
		    myPort = (Integer)obj;
		}

		//
		// The neighbor Port info
		//
		for (Vertex neighborPortVertex : myPortVertex.getVertices(Direction.OUT, "link")) {
		    // Ignore inactive ports
		    if (! neighborPortVertex.getProperty("state").toString().equals("ACTIVE"))
			continue;

		    int neighborPort = 0;
		    obj = neighborPortVertex.getProperty("number");
		    if (obj instanceof Short) {
			neighborPort = (Short)obj;
		    } else if (obj instanceof Integer) {
			neighborPort = (Integer)obj;
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
			if (neighbor == null)
			    neighbor = addNode(neighborId);
			me.addLink(myPort, neighbor, neighborPort);
		    }
		}
	    }
	}
	dbHandler.commit();
    }
}
