package com.tinkerpop.blueprints.impls.ramcloud;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.util.Base64;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.impls.ramcloud.PerfMon;
import edu.stanford.ramcloud.JRamCloud;

public class RamCloudEdge extends RamCloudElement implements Edge {

    private final static Logger log = LoggerFactory.getLogger(RamCloudGraph.class);
    private RamCloudVertex outVertex;
    private RamCloudVertex inVertex;
    private String label;
    private byte[] rcKey;
    private RamCloudGraph graph;

    public RamCloudEdge(RamCloudVertex outVertex, RamCloudVertex inVertex, String label, RamCloudGraph graph) {
	super(edgeToRcKey(outVertex, inVertex, label), graph.edgePropTableId, graph);

	this.outVertex = outVertex;
	this.inVertex = inVertex;
	this.label = label;
	this.rcKey = edgeToRcKey(outVertex, inVertex, label);
	this.graph = graph;
    }

    public RamCloudEdge(byte[] rcKey, RamCloudGraph graph) {
	super(rcKey, graph.edgePropTableId, graph);

	ByteBuffer edgeId = ByteBuffer.wrap(rcKey).order(ByteOrder.LITTLE_ENDIAN);
	outVertex = new RamCloudVertex(edgeId.getLong(), graph);
	inVertex = new RamCloudVertex(edgeId.getLong(), graph);
	label = new String(rcKey, 16, rcKey.length - 16);

	this.rcKey = rcKey;
	this.graph = graph;
    }

    private static byte[] edgeToRcKey(RamCloudVertex outVertex, RamCloudVertex inVertex, String label) {
	return ByteBuffer.allocate(16 + label.length()).order(ByteOrder.LITTLE_ENDIAN).putLong((Long) outVertex.getId()).putLong((Long) inVertex.getId()).put(label.getBytes()).array();
    }

    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
	if (direction.equals(Direction.OUT)) {
	    return outVertex;
	} else if (direction.equals(Direction.IN)) {
	    return inVertex;
	} else {
	    throw ExceptionFactory.bothIsNotSupported();
	}
    }

    @Override
    public String getLabel() {
	return label;
    }

    public boolean isLoop() {
	return outVertex.equals(inVertex);
    }

    public Vertex getNeighbor(Vertex vertex) {
	if (outVertex.equals(vertex)) {
	    return inVertex;
	} else if (inVertex.equals(vertex)) {
	    return outVertex;
	} else {
	    return null;
	}
    }

    @Override
    public void remove() {
	if (isLoop()) {
	    outVertex.removeEdgeFromAdjList(this);
	} else {
	    outVertex.removeEdgeFromAdjList(this);
	    inVertex.removeEdgeFromAdjList(this);
	}

	super.remove();
    }

    void removeProperties() {
	super.remove();
    }

    @Override
    public Object getId() {
	return new String(Base64.encode(rcKey));
    }

    public boolean exists() {
	boolean edgePropTableEntryExists;
	boolean outVertexEntryExists;
	boolean inVertexEntryExists;

	PerfMon pm = PerfMon.getInstance();
	try {
	    JRamCloud edgeTable = graph.getRcClient();
	    pm.read_start("RamCloudEdge exists()");
	    edgeTable.read(graph.edgePropTableId, rcKey);
	    pm.read_end("RamCloudEdge exists()");
	    edgePropTableEntryExists = true;
	} catch (Exception e) {
	    pm.read_end("RamCloudEdge exists()");
	    // Edge property table entry does not exist
	    edgePropTableEntryExists = false;
	}

	outVertexEntryExists = outVertex.getEdgeSet().contains(this);

	if (!outVertex.equals(inVertex)) {
	    inVertexEntryExists = inVertex.getEdgeSet().contains(this);
	} else {
	    inVertexEntryExists = outVertexEntryExists;
	}

	if (edgePropTableEntryExists && outVertexEntryExists && inVertexEntryExists) {
	    return true;
	} else if (!edgePropTableEntryExists && !outVertexEntryExists && !inVertexEntryExists) {
	    return false;
	} else {
	    log.warn("{}: Detected RamCloudGraph inconsistency: edgePropTableEntryExists={}, outVertexEntryExists={}, inVertexEntryExists={}.", this, edgePropTableEntryExists, outVertexEntryExists, inVertexEntryExists);
	    return true;
	}
    }

    public void create() throws Exception {
	// TODO: Existence check costs extra (presently 3 reads), could use option to turn on/off
	if (!exists()) {
		PerfMon pm = PerfMon.getInstance();
		// create edge property table
		JRamCloud edgeTable = graph.getRcClient();
	        pm.write_start("RamCloudEdge create()");
		edgeTable.write(graph.edgePropTableId, rcKey, ByteBuffer.allocate(0).array());
	        pm.write_end("RamCloudEdge create()");

		boolean addSucc = outVertex.addEdgeToAdjList(this);
		if ( !addSucc ) {
		    edgeTable.remove(graph.edgePropTableId, rcKey);
		    throw ExceptionFactory.edgeWithIdAlreadyExist(rcKey);
		}
		if (!isLoop()) {
		    addSucc = inVertex.addEdgeToAdjList(this);
		    if ( !addSucc ) {
			edgeTable.remove(graph.edgePropTableId, rcKey);
			outVertex.removeEdgeFromAdjList(this);
			throw ExceptionFactory.edgeWithIdAlreadyExist(rcKey);
		    }
		}
	} else {
	    throw ExceptionFactory.edgeWithIdAlreadyExist(rcKey);
	}
    }

    public static boolean isValidEdgeId(byte[] id) {
	if (id == null) {
	    return false;
	}
	if (id.length == 0) {
	    return false;
	}

	ByteBuffer edgeId = ByteBuffer.wrap(id);
	try {
	    edgeId.getLong();
	} catch (BufferUnderflowException e) {
	    return false;
	}

	try {
	    edgeId.getLong();
	} catch (BufferUnderflowException e) {
	    return false;
	}

	if (edgeId.remaining() == 0) {
	    return false;
	}

	return true;
    }

    @Override
    public int hashCode() {
	return Arrays.hashCode(rcKey);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	RamCloudEdge other = (RamCloudEdge) obj;
	return Arrays.equals(rcKey, other.rcKey);
    }

    @Override
    public String toString() {
	return "RamCloudEdge [outVertex=" + outVertex + ", inVertex=" + inVertex
		+ ", label=" + label + "]";
    }
}
