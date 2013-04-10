package net.onrc.onos.util;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

public class GraphDBConnection {
	public enum Transaction {
		COMMIT,
		ROLLBACK
	}
	public enum GenerateEvent {
		TRUE,
		FALSE
	}
	class TransactionHandle {
		protected TransactionalGraph tr;
		public void create() {
			tr = graph.startTransaction();			
		}
	}
	protected static Logger log = LoggerFactory.getLogger(GraphDBConnection.class);
	private static GraphDBConnection singleton = new GraphDBConnection( );
	private static TitanGraph graph;
	private static EventTransactionalGraph<TitanGraph> eg;
	private static GraphDBUtils utils;
	private static String configFile;

	   
	   /* A private Constructor prevents any other 
	    * class from instantiating.
	    */
	   private GraphDBConnection(){ }
	   
	   /* Static 'instance' method */
	   public static GraphDBConnection getInstance(final String conf) {
		   if (GraphDBConnection.configFile == null || GraphDBConnection.configFile.isEmpty()) {
			   GraphDBConnection.configFile = conf;
			   log.debug("GraphDBConnection::Setting Config File {}", GraphDBConnection.configFile);
		   }
		   if (!GraphDBConnection.configFile.isEmpty() && 
				   (graph == null||graph.isOpen() == Boolean.FALSE)) {
		        graph = TitanFactory.open(GraphDBConnection.configFile);		        
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
		        graph.stopTransaction(Conclusion.SUCCESS);
		        eg = new EventTransactionalGraph<TitanGraph>(graph);
		   }		   
		   if (utils == null) {
			   utils = new GraphDBUtils();
		   }
	      return singleton;
	   }
	   
	   public IDBUtils utils() {
		   return utils;
	   }
	   
	   public FramedGraph<TitanGraph> getFramedGraph() {
	   
		   	if (isValid()) {
		   		FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
		   		return fg;
		   	} else {
		   		log.error("new FramedGraph failed");
		   		return null;
		   	}
	   }
	   
	   protected EventTransactionalGraph<TitanGraph> getEventGraph() {
		   
		   	if (isValid()) {		   		
		   		return eg;
		   	} else {
		   		return null;
		   	}
	   }
	   
	   public void addEventListener(final LocalGraphChangedListener listener) {		   
		   EventTransactionalGraph<TitanGraph> eg = this.getEventGraph();
		   eg.addListener(listener);
		   log.debug("Registered listener {}",listener.getClass());
	   }
	   
	   public Boolean isValid() {
		   
		   return (graph != null||graph.isOpen());
	   }
	   
	   public void startTx() {
		   
		   
	   }
	   
	   public void endTx(Transaction tx) {
		   try {
			   switch (tx) {
			   case COMMIT:
				   graph.stopTransaction(Conclusion.SUCCESS);
			   case ROLLBACK:
				   graph.stopTransaction(Conclusion.FAILURE);
			   }
		   } catch (Exception e) {
			   // TODO Auto-generated catch block
			   log.error("{}",e.toString());
		   }
	   }
	   
	   public void endTx(TransactionHandle tr, Transaction tx) {
		   switch (tx) {
		   case COMMIT:
			   if (tr != null && tr.tr != null) {
				   tr.tr.stopTransaction(Conclusion.SUCCESS);
			   } else {
				   graph.stopTransaction(Conclusion.SUCCESS);
			   }
		   case ROLLBACK:
			   if (tr != null && tr.tr != null) {
				   tr.tr.stopTransaction(Conclusion.FAILURE);
			   } else {
				   graph.stopTransaction(Conclusion.FAILURE);
			   }
		   }
	   }   
	   
	   public void endTx(Transaction tx, GenerateEvent fire) {

		   try {
			if (fire.equals(GenerateEvent.TRUE)) {
				   switch (tx) {
				   case COMMIT:
					   eg.stopTransaction(Conclusion.SUCCESS);
				   case ROLLBACK:
					   eg.stopTransaction(Conclusion.FAILURE);
				   }
			   } else {
					endTx(tx);   			   
			   }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   
	   public void close() {
		   endTx(Transaction.COMMIT);
//		   graph.shutdown();
	   }
	   
}
