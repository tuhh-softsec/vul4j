/* Copyright (c) 2013 Stanford University
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR(S) DISCLAIM ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL AUTHORS BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.tinkerpop.blueprints.impls.ramcloud;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraphProtos.EdgeListProtoBuf;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraphProtos.EdgeProtoBuf;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.impls.ramcloud.PerfMon;

import edu.stanford.ramcloud.JRamCloud;
import edu.stanford.ramcloud.JRamCloud.MultiWriteObject;
import edu.stanford.ramcloud.JRamCloud.RejectRules;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

public class RamCloudVertex extends RamCloudElement implements Vertex, Serializable {

	private final static Logger log = LoggerFactory.getLogger(RamCloudGraph.class);
	private static final long serialVersionUID = 7526472295622776147L;
	protected long id;
	protected byte[] rcKey;
	private RamCloudGraph graph;

	private Versioned<EdgeListProtoBuf> cachedAdjEdgeList;

	public RamCloudVertex(long id, RamCloudGraph graph) {
		super(idToRcKey(id), graph.vertPropTableId, graph);

		this.id = id;
		this.rcKey = idToRcKey(id);
		this.graph = graph;
		this.cachedAdjEdgeList = null;
	}

	public RamCloudVertex(byte[] rcKey, RamCloudGraph graph) {
		super(rcKey, graph.vertPropTableId, graph);

		this.id = rcKeyToId(rcKey);
		this.rcKey = rcKey;
		this.graph = graph;
		this.cachedAdjEdgeList = null;
	}


	/*
	 * Vertex interface implementation
	 */
	@Override
	public Edge addEdge(String label, Vertex inVertex) {
		return graph.addEdge(null, this, inVertex, label);
	}

	@Override
	public Iterable<Edge> getEdges(Direction direction, String... labels) {
		return new ArrayList<Edge>(getEdgeList(direction, labels));
	}

	@Override
	public Iterable<Vertex> getVertices(Direction direction, String... labels) {
		List<RamCloudEdge> edges = getEdgeList(direction, labels);
		List<Vertex> neighbors = new LinkedList<Vertex>();
		for (RamCloudEdge edge : edges) {
			neighbors.add(edge.getNeighbor(this));
		}
		return neighbors;
	}

	@Override
	public VertexQuery query() {
		return new DefaultVertexQuery(this);
	}

	/*
	 * RamCloudElement overridden methods
	 */
	@Override
	public Object getId() {
		return id;
	}

	@Override
	public void remove() {
		Set<RamCloudEdge> edges = getEdgeSet();

		// neighbor vertex -> List of Edges to remove
		Map<RamCloudVertex, List<RamCloudEdge>> vertexToEdgesMap = new HashMap<RamCloudVertex, List<RamCloudEdge>>( edges.size() );

		// Batch edges together by neighbor vertex
		for (RamCloudEdge edge : edges) {
			RamCloudVertex neighbor = (RamCloudVertex) edge.getNeighbor(this);
			List<RamCloudEdge> edgeList = vertexToEdgesMap.get(neighbor);

			if (edgeList == null) {
				edgeList = new LinkedList<RamCloudEdge>();
			}

			edgeList.add(edge);
			vertexToEdgesMap.put(neighbor, edgeList);
		}

		// Remove batches of edges at a time by neighbor vertex
		for (Entry<RamCloudVertex, List<RamCloudEdge>> entry : vertexToEdgesMap.entrySet()) {
			// Skip over loopback edges to ourself
			if (!entry.getKey().equals(this)) {
				entry.getKey().removeEdgesFromAdjList(entry.getValue());
			}

			// Remove this batch of edges from the edge property table
			for (RamCloudEdge edge : entry.getValue()) {
				edge.removeProperties();
			}
		}

		Map<String,Object> props = this.getPropertyMap();
		for( Map.Entry<String,Object> entry : props.entrySet() ) {
			if ( !graph.indexedKeys.contains(entry.getKey() ) ) continue;
			RamCloudKeyIndex keyIndex = new RamCloudKeyIndex(graph.kidxVertTableId, entry.getKey(), entry.getValue(), graph, Vertex.class);
			keyIndex.remove(entry.getKey(), entry.getValue(), this);
		}

		// Remove ourselves entirely from the vertex table
		graph.getRcClient().remove(graph.vertTableId, rcKey);

		super.remove();
	}

	/*
	 * Object overridden methods
	 */
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
		RamCloudVertex other = (RamCloudVertex) obj;
		return (id == other.id);
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	@Override
	public String toString() {
		return "RamCloudVertex [id=" + id + "]";
	}

	/*
	 * RamCloudVertex specific methods
	 */
	private static byte[] idToRcKey(long id) {
		return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(id).array();
	}

	private static long rcKeyToId(byte[] rcKey) {
		return ByteBuffer.wrap(rcKey).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}

	boolean addEdgeToAdjList(RamCloudEdge edge) {
		List<RamCloudEdge> edgesToAdd = new ArrayList<RamCloudEdge>(1);
		edgesToAdd.add(edge);
		return addEdgesToAdjList(edgesToAdd);
	}
	boolean removeEdgeFromAdjList(RamCloudEdge edge) {
		List<RamCloudEdge> edgesToRemove = new ArrayList<RamCloudEdge>(1);
		edgesToRemove.add(edge);
		return removeEdgesFromAdjList(edgesToRemove);
	}

	private boolean addEdgesToAdjList(List<RamCloudEdge> edgesToAdd) {
		return updateEdgeAdjList(edgesToAdd, true);
	}
	private boolean removeEdgesFromAdjList(List<RamCloudEdge> edgesToAdd) {
		return updateEdgeAdjList(edgesToAdd, false);
	}

	/** Conditionally update Adj. Edge List
	 * @return true if EdgeAdjList was logically modified.(Cache update does not imply true return)
	 */
	private boolean updateEdgeAdjList(List<RamCloudEdge> edgesToModify, boolean add) {
		PerfMon pm = PerfMon.getInstance();
		JRamCloud rcClient = graph.getRcClient();
		final int MAX_RETRIES = 100;
		for (int retry = 1 ; retry <= MAX_RETRIES ; ++retry ) {
			Set<RamCloudEdge> edges;
			long expected_version = 0L;
			if ( this.cachedAdjEdgeList == null ) {
				edges = new HashSet<RamCloudEdge>();
			} else {
				expected_version = this.cachedAdjEdgeList.getVersion();
				if ( expected_version == 0L && add == false ) {
					updateCachedAdjEdgeList();
					expected_version = this.cachedAdjEdgeList.getVersion();
				}
				edges = buildEdgeSetFromProtobuf(this.cachedAdjEdgeList.getValue(), Direction.BOTH);
			}
			if ( expected_version == 0L && add == false ) {
				updateCachedAdjEdgeList();
				expected_version = this.cachedAdjEdgeList.getVersion();
				edges = buildEdgeSetFromProtobuf(this.cachedAdjEdgeList.getValue(), Direction.BOTH);
			}
			//log.debug( (add?"Adding":"Removing") + " edges to: {"+ edges+ "}");

			try {
				if ( add ) {
					if (edges.addAll(edgesToModify) == false) {
						log.warn("{}: There aren't any changes to edges ({})", this, edgesToModify);
						return false;
					}
				} else {
					if (edges.removeAll(edgesToModify) == false) {
						log.warn("{}: There aren't any changes to edges ({})", this, edgesToModify);
						return false;
					}
				}

				EdgeListProtoBuf edgeList = buildProtoBufFromEdgeSet(edges);
				JRamCloud.RejectRules rules = rcClient.new RejectRules();
				if ( expected_version == 0L ) {
					rules.setExists();
				} else {
					rules.setNeVersion(expected_version);
				}
				pm.write_start("RAMCloudVertex updateEdgeAdjList()");
				long updated_version = rcClient.writeRule(graph.vertTableId, rcKey, edgeList.toByteArray(), rules);
				pm.write_end("RAMCloudVertex updateEdgeAdjList()");
				this.cachedAdjEdgeList.setValue(edgeList, updated_version);
				return true;
			} catch (UnsupportedOperationException e) {
				pm.write_end("RAMCloudVertex updateEdgeAdjList()");
				pm.write_condfail("RAMCloudVertex updateEdgeAdjList()");
				log.error("{" + toString() + "}: Failed to modify a set of edges ({" + edgesToModify + "}): ", e);
				return false;
			} catch (ClassCastException e) {
				pm.write_end("RAMCloudVertex updateEdgeAdjList()");
				pm.write_condfail("RAMCloudVertex updateEdgeAdjList()");
				log.error("{" + toString() + "}: Failed to modify a set of edges ({" + edgesToModify + "}): ", e);
				return false;
			} catch (NullPointerException e) {
				pm.write_end("RAMCloudVertex updateEdgeAdjList()");
				pm.write_condfail("RAMCloudVertex updateEdgeAdjList()");
				log.error("{" + toString() + "}: Failed to modify a set of edges ({" + edgesToModify + "}): ", e);
				return false;
			} catch (Exception e) {
				pm.write_end("RAMCloudVertex updateEdgeAdjList()");
				pm.write_condfail("RAMCloudVertex updateEdgeAdjList()");
				// FIXME Workaround for native method exception declaration bug
				if ( e instanceof WrongVersionException ) {
					log.debug("Conditional Updating EdgeList failed for {} RETRYING {}", this, retry);
					//log.debug("Conditional Updating EdgeList failed for {} modifing {} RETRYING [{}]", this, edgesToModify, retry);
					updateCachedAdjEdgeList();
				} else {
					log.debug("Cond. Write to modify adj edge list failed, exception thrown", e);
					updateCachedAdjEdgeList();
				}
			}
		}
		log.error("Conditional Updating EdgeList failed for {} gave up RETRYING", this);
		return false;
	}

	/** Get all adj.edge list
	 * Method is exposed to package namespace to do Vertex removal efficiently;
	 */
	Set<RamCloudEdge> getEdgeSet() {
		return getVersionedEdgeSet(Direction.BOTH).getValue();
	}

	private Versioned<EdgeListProtoBuf> updateCachedAdjEdgeList() {
		JRamCloud.Object vertTableEntry;
		EdgeListProtoBuf edgeList;

		PerfMon pm = PerfMon.getInstance();
		try {
			JRamCloud vertTable = graph.getRcClient();
			pm.read_start("RamCloudVertex updateCachedAdjEdgeList()");
			vertTableEntry = vertTable.read(graph.vertTableId, rcKey);
			pm.read_end("RamCloudVertex updateCachedAdjEdgeList()");
		} catch (Exception e) {
			pm.read_end("RamCloudVertex updateCachedAdjEdgeList()");
			log.error("{" + toString() + "}: Error reading vertex table entry: ", e);
			return null;
		}

		try {
			pm.protodeser_start("RamCloudVertex updateCachedAdjEdgeList()");
			edgeList = EdgeListProtoBuf.parseFrom(vertTableEntry.value);
			Versioned<EdgeListProtoBuf> updatedEdgeList = new Versioned<EdgeListProtoBuf>(edgeList, vertTableEntry.version);
			this.cachedAdjEdgeList = updatedEdgeList;
			pm.protodeser_end("RamCloudVertex updateCachedAdjEdgeList()");
			return updatedEdgeList;
		} catch (InvalidProtocolBufferException e) {
			pm.protodeser_end("RamCloudVertex updateCachedAdjEdgeList()");
			log.error("{" + toString() + "}: Read malformed edge list: ", e);
			return null;
		}
	}

	private Versioned<Set<RamCloudEdge>> getVersionedEdgeSet(Direction direction, String... labels) {
		Versioned<EdgeListProtoBuf> cachedEdgeList = updateCachedAdjEdgeList();
		return new Versioned<Set<RamCloudEdge>>(buildEdgeSetFromProtobuf(cachedEdgeList.getValue(), direction, labels), cachedEdgeList.getVersion() );
	}

	private Set<RamCloudEdge> buildEdgeSetFromProtobuf(EdgeListProtoBuf edgeList,
			Direction direction, String... labels) {
		PerfMon pm = PerfMon.getInstance();
		long startTime = 0;
		if(RamCloudGraph.measureSerializeTimeProp == 1) {
		    startTime = System.nanoTime();
		}
		pm.protodeser_start("RamCloudVertex buildEdgeSetFromProtobuf()");
		Set<RamCloudEdge> edgeSet = new HashSet<RamCloudEdge>( edgeList.getEdgeCount() );
		for (EdgeProtoBuf edge : edgeList.getEdgeList()) {
			if ((direction.equals(Direction.BOTH) || (edge.getOutgoing() ^ direction.equals(Direction.IN)))
					&& (labels.length == 0 || Arrays.asList(labels).contains(edge.getLabel()))) {
				RamCloudVertex  neighbor = new RamCloudVertex(edge.getNeighborId(), graph);
				if (edge.getOutgoing()) {
					edgeSet.add(new RamCloudEdge(this, neighbor, edge.getLabel(), graph));
				} else {
					edgeSet.add(new RamCloudEdge(neighbor, this, edge.getLabel(), graph));
				}
			}
		}
		pm.protodeser_end("RamCloudVertex buildEdgeSetFromProtobuf()");
		if(RamCloudGraph.measureSerializeTimeProp == 1) {
                 	long endTime = System.nanoTime();
                	log.error("Performance buildEdgeSetFromProtobuf key {}, {}, size={}", this, endTime - startTime, edgeList.getSerializedSize());
		}
		return edgeSet;
	}



	private EdgeListProtoBuf buildProtoBufFromEdgeSet(Set<RamCloudEdge> edgeSet) {
		PerfMon pm = PerfMon.getInstance();
		long startTime = 0;
		if(RamCloudGraph.measureSerializeTimeProp == 1) {
		    startTime = System.nanoTime();
		}

		pm.protoser_start("RamCloudVertex buildProtoBufFromEdgeSet()");

		EdgeListProtoBuf.Builder edgeListBuilder = EdgeListProtoBuf.newBuilder();
		EdgeProtoBuf.Builder edgeBuilder = EdgeProtoBuf.newBuilder();

		for (Edge edge : edgeSet) {
			if (edge.getVertex(Direction.OUT).equals(this) || edge.getVertex(Direction.IN).equals(this)) {
				if (edge.getVertex(Direction.OUT).equals(edge.getVertex(Direction.IN))) {
					edgeBuilder.setNeighborId(id);
					edgeBuilder.setOutgoing(true);
					edgeBuilder.setLabel(edge.getLabel());
					edgeListBuilder.addEdge(edgeBuilder.build());

					edgeBuilder.setOutgoing(false);
					edgeListBuilder.addEdge(edgeBuilder.build());
				} else {
					if (edge.getVertex(Direction.OUT).equals(this)) {
						edgeBuilder.setNeighborId((Long) edge.getVertex(Direction.IN).getId());
						edgeBuilder.setOutgoing(true);
						edgeBuilder.setLabel(edge.getLabel());
						edgeListBuilder.addEdge(edgeBuilder.build());
					} else {
						edgeBuilder.setNeighborId((Long) edge.getVertex(Direction.OUT).getId());
						edgeBuilder.setOutgoing(false);
						edgeBuilder.setLabel(edge.getLabel());
						edgeListBuilder.addEdge(edgeBuilder.build());
					}
				}
			} else {
				log.warn("{}: Tried to add an edge unowned by this vertex ({})", this, edge);
			}
		}

		EdgeListProtoBuf buf = edgeListBuilder.build();
		pm.protoser_end("RamCloudVertex buildProtoBufFromEdgeSet");
		if(RamCloudGraph.measureSerializeTimeProp == 1) {
                	long endTime = System.nanoTime();
                	log.error("Performance buildProtoBufFromEdgeSet key {}, {}, size={}", this, endTime - startTime, buf.getSerializedSize());
		}
		return buf;
	}

	@Deprecated
	private List<RamCloudEdge> getEdgeList() {
		return getEdgeList(Direction.BOTH);
	}

	private List<RamCloudEdge> getEdgeList(Direction direction, String... labels) {

		Versioned<EdgeListProtoBuf> cachedEdgeList = updateCachedAdjEdgeList();
		PerfMon pm = PerfMon.getInstance();
		pm.protodeser_start("RamCloudVertex getEdgeList()");

		List<RamCloudEdge> edgeList = new ArrayList<RamCloudEdge>(cachedEdgeList.getValue().getEdgeCount());

		for (EdgeProtoBuf edge : cachedEdgeList.getValue().getEdgeList()) {
			if ((direction.equals(Direction.BOTH) || (edge.getOutgoing() ^ direction.equals(Direction.IN)))
					&& (labels.length == 0 || Arrays.asList(labels).contains(edge.getLabel()))) {
				RamCloudVertex neighbor = new RamCloudVertex(edge.getNeighborId(), graph);
				if (edge.getOutgoing()) {
					edgeList.add(new RamCloudEdge(this, neighbor, edge.getLabel(), graph));
				} else {
					edgeList.add(new RamCloudEdge(neighbor, this, edge.getLabel(), graph));
				}
			}
		}
		pm.protodeser_end("RamCloudVertex getEdgeList()");

		return edgeList;
	}

	protected boolean exists() {
		boolean vertTableEntryExists = false;
		boolean vertPropTableEntryExists = false;

		PerfMon pm = PerfMon.getInstance();
	        JRamCloud vertTable = graph.getRcClient();
		try {
		        pm.read_start("RamCloudVertex exists()");
			vertTable.read(graph.vertTableId, rcKey);
			pm.read_end("RamCloudVertex exists()");
			vertTableEntryExists = true;
		} catch (Exception e) {
			// Vertex table entry does not exist
		        pm.read_end("RamCloudVertex exists()");
		}

		try {
			pm.read_start("RamCloudVertex exists()");
			vertTable.read(graph.vertPropTableId, rcKey);
		        pm.read_end("RamCloudVertex exists()");
			vertPropTableEntryExists = true;
		} catch (Exception e) {
			// Vertex property table entry does not exist
		        pm.read_end("RamCloudVertex exists()");
		}

		if (vertTableEntryExists && vertPropTableEntryExists) {
			return true;
		} else if (!vertTableEntryExists && !vertPropTableEntryExists) {
			return false;
		} else {
			log.warn("{}: Detected RamCloudGraph inconsistency: vertTableEntryExists={}, vertPropTableEntryExists={}.", this, vertTableEntryExists, vertPropTableEntryExists);
			return true;
		}
	}

	protected void create() throws IllegalArgumentException {
		// TODO: Existence check costs extra (presently 2 reads), could use option to turn on/off
		if (!exists()) {
			PerfMon pm = PerfMon.getInstance();
			JRamCloud vertTable = graph.getRcClient();
			MultiWriteObject[] mwo = new MultiWriteObject[2];
			mwo[0] = new MultiWriteObject(graph.vertTableId, rcKey, ByteBuffer.allocate(0).array(), null);
			mwo[1] = new MultiWriteObject(graph.vertPropTableId, rcKey, ByteBuffer.allocate(0).array(), null);
			pm.multiwrite_start("RamCloudVertex create()");
			vertTable.multiWrite(mwo);
			pm.multiwrite_end("RamCloudVertex create()");
		} else {
			throw ExceptionFactory.vertexWithIdAlreadyExists(id);
		}
	}

	public void debugPrintEdgeList() {
		List<RamCloudEdge> edgeList = getEdgeList();

		log.debug("{}: Debug Printing Edge List...", this);
		for (RamCloudEdge edge : edgeList) {
			System.out.println(edge.toString());
		}
	}
}
