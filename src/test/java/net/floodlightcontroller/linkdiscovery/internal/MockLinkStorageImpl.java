package net.floodlightcontroller.linkdiscovery.internal;

import java.util.Set;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;

/**
 * Seam that allows me to set up a mock of LinkStorageImpl that writes to a
 * file database rather than a Cassandra cluster. Currently LinkStorageImpl
 * is hardcoded to connect to a Cassandra cluster in its init method.
 * 
 * @author jono
 *
 */

public class MockLinkStorageImpl extends LinkStorageImpl {

	public MockLinkStorageImpl(TitanGraph graph){
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
