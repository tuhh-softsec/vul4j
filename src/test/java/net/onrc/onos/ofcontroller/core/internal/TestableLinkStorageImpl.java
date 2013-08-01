package net.onrc.onos.ofcontroller.core.internal;

import java.util.Set;

import net.onrc.onos.ofcontroller.core.internal.LinkStorageImpl;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;

/**
 * Seam that allows me to set up a testable instance of LinkStorageImpl that 
 * writes to a file database rather than a Cassandra cluster. 
 * It seems the init() API on LinkStorageImpl might change so I won't rely
 * on it yet.
 * 
 * @author jono
 *
 */

public class TestableLinkStorageImpl extends LinkStorageImpl {
	protected TitanGraph graph;

	public TestableLinkStorageImpl(TitanGraph graph){
		this.graph = graph;
	}
	
	@Override
	public void init(String conf){
        Set<String> s = graph.getIndexedKeys(Vertex.class);
        if (!s.contains("dpid")) {
           graph.createKeyIndex("dpid", Vertex.class);
           graph.stopTransaction(Conclusion.SUCCESS);
        }
        if (!s.contains("type")) {
        	graph.createKeyIndex("type", Vertex.class);
        	graph.stopTransaction(Conclusion.SUCCESS);
        }
		
	}
}
