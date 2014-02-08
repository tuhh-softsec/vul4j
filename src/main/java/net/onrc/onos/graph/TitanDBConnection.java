package net.onrc.onos.graph;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.frames.FramedGraph;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitanDBConnection extends DBConnection {

    private TitanGraph graph;
    private static Logger log = LoggerFactory.getLogger(TitanDBConnection.class);
    private EventTransactionalGraph<TitanGraph> eg;

    public TitanDBConnection(final String dbConfigFile) {
        graph = TitanFactory.open(dbConfigFile);
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

    class TransactionHandle {

        protected TransactionalGraph tr;

        public void create() {
            tr = graph.newTransaction();
        }
    }

    @Override
    public FramedGraph getFramedGraph() {
        if (isValid()) {
            FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
            return fg;
        } else {
            log.error("new FramedGraph failed");
            return null;
        }
    }

    @Override
    public void addEventListener(LocalGraphChangedListener listener) {
        EventTransactionalGraph<TitanGraph> eg = this.getEventGraph();
        eg.addListener(listener);
        log.debug("Registered listener {}", listener.getClass());
    }

    @Override
    public Boolean isValid() {
        return (graph != null || graph.isOpen());
    }

    @Override
    public void commit() {
        try {
            graph.commit();
        } catch (Exception e) {
            log.error("{}", e.toString());
        }
    }

    @Override
    public void rollback() {
        try {
            graph.rollback();
        } catch (Exception e) {
            log.error("{}", e.toString());
        }
    }

    @Override
    public void close() {
        commit();
    }

    /**
     * Get EventTransactionalGraph of the titan graph.
     *
     * @return EventTransactionalGraph of the titan graph
     */
    private EventTransactionalGraph<TitanGraph> getEventGraph() {
        if (isValid()) {
            return eg;
        } else {
            return null;
        }
    }
}
