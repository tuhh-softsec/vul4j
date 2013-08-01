package net.onrc.onos.graph;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

public class GraphDBConnection implements IDBConnection {
	public enum Transaction {
		COMMIT, ROLLBACK
	}

	public enum GenerateEvent {
		TRUE, FALSE
	}

	class TransactionHandle {
		protected TransactionalGraph tr;

		public void create() {
			tr = graph.newTransaction();
		}
	}

	protected static Logger log = LoggerFactory
			.getLogger(GraphDBConnection.class);
	private static GraphDBConnection singleton = new GraphDBConnection();
	private static TitanGraph graph;
	private static EventTransactionalGraph<TitanGraph> eg;
	private static String configFile;

	/*
	 * A private Constructor prevents any other class from instantiating.
	 */
	private GraphDBConnection() {
	}

	/* Static 'instance' method */
	/**
	 * Get the instance of GraphDBConnection class.
	 * @param conf the path to the database configuration file.
	 * @return GraphDBConnection instance.
	 */
	public static synchronized GraphDBConnection getInstance(final String conf) {
		if (GraphDBConnection.configFile == null
				|| GraphDBConnection.configFile.isEmpty()) {
			GraphDBConnection.configFile = conf;
			log.debug("GraphDBConnection::Setting Config File {}",
					GraphDBConnection.configFile);
		}
		if (!GraphDBConnection.configFile.isEmpty()
				&& (graph == null || graph.isOpen() == Boolean.FALSE)) {
			graph = TitanFactory.open(GraphDBConnection.configFile);
			// FIXME: Creation on Indexes should be done only once
			Set<String> s = graph.getIndexedKeys(Vertex.class);
			if (!s.contains("dpid")) {
				graph.createKeyIndex("dpid", Vertex.class);
			}
			if (!s.contains("port_id")) {
				graph.createKeyIndex("port_id", Vertex.class);
			}
			if (!s.contains("type")) {
				graph.createKeyIndex("type", Vertex.class);
			}
			if (!s.contains("dl_addr")) {
				graph.createKeyIndex("dl_addr", Vertex.class);
			}
			if (!s.contains("flow_id")) {
				graph.createKeyIndex("flow_id", Vertex.class);
			}
			if (!s.contains("flow_entry_id")) {
				graph.createKeyIndex("flow_entry_id", Vertex.class);
			}
			if (!s.contains("switch_state")) {
				graph.createKeyIndex("switch_state", Vertex.class);
			}
			graph.commit();
			eg = new EventTransactionalGraph<TitanGraph>(graph);
		}
		return singleton;
	}

	/** 
	 * Get a FramedGraph instance of the graph.
	 */
	public FramedGraph<TitanGraph> getFramedGraph() {
		if (isValid()) {
			FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
			return fg;
		} else {
			log.error("new FramedGraph failed");
			return null;
		}
	}

	/**
	 * Get EventTransactionalGraph of the titan graph.
	 * @return EventTransactionalGraph of the titan graph
	 */
	protected EventTransactionalGraph<TitanGraph> getEventGraph() {
		if (isValid()) {
			return eg;
		} else {
			return null;
		}
	}

	/**
	 * Add LocalGraphChangedLister for the graph.
	 */
	public void addEventListener(final LocalGraphChangedListener listener) {
		EventTransactionalGraph<TitanGraph> eg = this.getEventGraph();
		eg.addListener(listener);
		log.debug("Registered listener {}", listener.getClass());
	}

	/**
	 * Return whether this connection is valid.
	 */
	public Boolean isValid() {
		return (graph != null || graph.isOpen());
	}

	/**
	 * Commit changes for the graph operations.
	 */
	public void commit() {
		try {
			graph.commit();
		}
		catch (Exception e) {
			log.error("{}", e.toString());
		}
	}

	/**
	 * Rollback changes for the graph operations.
	 */
	public void rollback() {
		try {
			graph.rollback();
		}
		catch (Exception e) {
			log.error("{}", e.toString());
		}
	}

	/**
	 * Close this database connection.
	 */
	public void close() {
		commit();
	}
}
