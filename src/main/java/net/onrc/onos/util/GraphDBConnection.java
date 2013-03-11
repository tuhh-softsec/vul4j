package net.onrc.onos.util;

import java.util.Set;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.frames.FramedGraph;

public class GraphDBConnection {
	public enum Transaction {
		COMMIT,
		ROLLBACK
	}
	private static GraphDBConnection singleton = new GraphDBConnection( );
	private static TitanGraph graph;
	private static GraphDBUtils utils;
	   
	   /* A private Constructor prevents any other 
	    * class from instantiating.
	    */
	   private GraphDBConnection(){ }
	   
	   /* Static 'instance' method */
	   public static GraphDBConnection getInstance(String conf) {
		   if (graph == null||graph.isOpen() == Boolean.FALSE) {
		        graph = TitanFactory.open(conf);		        
		        // FIXME: Creation on Indexes should be done only once
		        Set<String> s = graph.getIndexedKeys(Vertex.class);
		        if (!s.contains("dpid")) {
		           graph.createKeyIndex("dpid", Vertex.class);
		        }
		        if (!s.contains("type")) {
		        	graph.createKeyIndex("type", Vertex.class);
		        }
		        if (!s.contains("dl_address")) {
		        	graph.createKeyIndex("dl_address", Vertex.class);
		        }
		        if (!s.contains("flow_id")) {
		        	graph.createKeyIndex("flow_id", Vertex.class);
		        }
		        if (!s.contains("flow_entry_id")) {
		        	graph.createKeyIndex("flow_entry_id",
						     Vertex.class);
		        }
		   }
		   graph.stopTransaction(Conclusion.SUCCESS);
		   if (utils == null) {
			   utils = new GraphDBUtils();
		   }
	      return singleton;
	   }
	   
	   public IDBUtils utils() {
		   return utils;
	   }
	   
	   protected FramedGraph<TitanGraph> getFramedGraph() {
	   
		   	if (isValid()) {
		   		FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
		   		return fg;
		   	} else {
		   		return null;
		   	}
	   }
	   
	   public Boolean isValid() {
		   
		   return (graph != null||graph.isOpen());
	   }
	   
	   public void startTx() {
		   
	   }
	   
	   public void endTx(Transaction tx) {
		   switch (tx) {
		   case COMMIT:
			   graph.stopTransaction(Conclusion.SUCCESS);
		   case ROLLBACK:
			   graph.stopTransaction(Conclusion.FAILURE);
		   }
	   }
	   
	   public void close() {
		   
	   }
	   
}
