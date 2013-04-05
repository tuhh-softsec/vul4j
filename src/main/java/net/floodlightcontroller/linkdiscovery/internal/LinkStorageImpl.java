package net.floodlightcontroller.linkdiscovery.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoSwitchService;
import net.floodlightcontroller.core.internal.TopoSwitchServiceImpl;
import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBConnection.GenerateEvent;
import net.onrc.onos.util.GraphDBConnection.Transaction;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.transform.PathPipe;

public class LinkStorageImpl implements ILinkStorage {
	public TitanGraph graph;
	protected static Logger log = LoggerFactory.getLogger(LinkStorageImpl.class);
	protected String conf;

	@Override
	public void update(Link link, DM_OPERATION op) {
		update(link, (LinkInfo)null, op);
	}

	@Override
	public void update(List<Link> links, DM_OPERATION op) {
		for (Link lt: links) {
			update(lt, (LinkInfo)null, op);
		}
	}

	@Override
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op) {
		switch (op) {
		case UPDATE:
		case CREATE:
		case INSERT:
			addOrUpdateLink(link, linkinfo, op);
			break;
		case DELETE:
			deleteLink(link);
			break;
		}
	}

	private Vertex getPortVertex(String dpid, short port) {
		Vertex vsw, vport = null;
        if ((vsw = graph.getVertices("dpid", dpid).iterator().next()) != null) {
        	GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();        	
        	pipe.start(vsw).out("on").has("number", port);
        	if (pipe.hasNext()) {
        		vport = pipe.next();
        	}
        }
        return vport;
	}
	
	public void addOrUpdateLink(Link lt, LinkInfo linkinfo, DM_OPERATION op) {
		GraphDBConnection conn = GraphDBConnection.getInstance(this.conf);
		IPortObject vportSrc = null, vportDst = null;
	
		log.trace("addOrUpdateLink(): op {} {} {}", new Object[]{op, lt, linkinfo});
		
        try {
            // get source port vertex
        	String dpid = HexString.toHexString(lt.getSrc());
        	short port = lt.getSrcPort();
        	vportSrc = conn.utils().searchPort(conn, dpid, port);
            
            // get dest port vertex
            dpid = HexString.toHexString(lt.getDst());
            port = lt.getDstPort();
            vportDst = conn.utils().searchPort(conn, dpid, port);
                        
            if (vportSrc != null && vportDst != null) {
         	
            	
            	// check if the link exists
            	List<Vertex> currLinks = new ArrayList<Vertex>();
            	for (Vertex V : vportSrc.asVertex().query().direction(Direction.OUT).labels("link").vertices()) {
            		currLinks.add(V);
            	}
            	
            	if (currLinks.contains(vportDst)) {
            		// TODO: update linkinfo
            		if (op.equals(DM_OPERATION.INSERT) || op.equals(DM_OPERATION.CREATE)) {
            			log.debug("addOrUpdateLink(): failed link exists {} {} src {} dst {}", 
            					new Object[]{op, lt, vportSrc, vportDst});
            		}
            	} else {
            		graph.addEdge(null, vportSrc.asVertex(), vportDst.asVertex(), "link");
            		graph.stopTransaction(Conclusion.SUCCESS);
            		log.debug("addOrUpdateLink(): link added {} {} src {} dst {}", new Object[]{op, lt, vportSrc, vportDst});
            	}
            } else {
            	log.error("addOrUpdateLink(): failed invalid vertices {} {} src {} dst {}", new Object[]{op, lt, vportSrc, vportDst});
            	graph.stopTransaction(Conclusion.FAILURE);
            }
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	log.error("addOrUpdateLink(): titan exception {} {} {}", new Object[]{op, lt, e.toString()});
        	e.printStackTrace();
        }
	}
	
	@Override
	public void deleteLinks(List<Link> links) {

		for (Link lt : links) {
			deleteLink(lt);
		}
	}
	

	@Override
	public void deleteLink(Link lt) {
		GraphDBConnection conn = GraphDBConnection.getInstance(this.conf);
		IPortObject vportSrc = null, vportDst = null;
		int count = 0;
		
		log.debug("deleteLink(): {}", lt);
		
        try {
            // get source port vertex
         	String dpid = HexString.toHexString(lt.getSrc());
         	short port = lt.getSrcPort();
         	vportSrc = conn.utils().searchPort(conn, dpid, port);
            
            // get dst port vertex
         	dpid = HexString.toHexString(lt.getDst());
         	port = lt.getDstPort();
         	vportDst = conn.utils().searchPort(conn, dpid, port);
     		// FIXME: This needs to remove all edges
         	// FIXME: Events will only be generated on singleton graph object (GraphDBConnection)
         	
         	if (vportSrc != null && vportDst != null) {

   /*      		for (Edge e : vportSrc.asVertex().getEdges(Direction.OUT)) {
         			log.debug("deleteLink(): {} in {} out {}", 
         					new Object[]{e.getLabel(), e.getVertex(Direction.IN), e.getVertex(Direction.OUT)});
         			if (e.getLabel().equals("link") && e.getVertex(Direction.IN).equals(vportDst)) {
         				graph.removeEdge(e);
         				count++;
         			}
         		}*/
         		vportSrc.removeLink(vportDst);
        		conn.endTx(Transaction.COMMIT);
            	log.debug("deleteLink(): deleted edges src {} dst {}", new Object[]{
            			lt, vportSrc, vportDst});
            	
            } else {
            	log.error("deleteLink(): failed invalid vertices {} src {} dst {}", new Object[]{lt, vportSrc, vportDst});
            	conn.endTx(Transaction.ROLLBACK);
            }
         	
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	log.error("deleteLink(): titan exception {} {}", new Object[]{lt, e.toString()});
        	e.printStackTrace();
        }
	}

	// TODO: Fix me
	@Override
	public List<Link> getLinks(Long dpid, short port) {
		Vertex vportSrc, vportDst;
    	List<Link> links = null;
    	Link lt;
    	
		vportSrc = getPortVertex(HexString.toHexString(dpid), port);
		if (vportSrc != null) {
     		for (Edge e : vportSrc.getEdges(Direction.OUT)) {
     			if (e.getLabel().equals("link")) {
     				break;
     			}
     		}
		}
     	return null;
	}
	
	@Override
	public void init(String conf) {
		//TODO extract the DB location from properties
	
		this.conf = conf;
        graph = TitanFactory.open(this.conf);
        
        // FIXME: These keys are not needed for Links but we better create it before using it as per titan
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

	@Override
	public void deleteLinksOnPort(Long dpid, short port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Link> getLinks(String dpid) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Link> getActiveLinks() {

		ITopoSwitchService swService = new TopoSwitchServiceImpl();
		
		Iterable<ISwitchObject> switches = swService.getActiveSwitches();

		List<Link> links = new ArrayList<Link>(); 
		for (ISwitchObject sw : switches) {
			GremlinPipeline<Vertex, Link> pipe = new GremlinPipeline<Vertex, Link>();
			ExtractLink extractor = new ExtractLink();

			pipe.start(sw.asVertex());
			pipe.enablePath(true);
			pipe.out("on").out("link").in("on").path().step(extractor);
					
			while (pipe.hasNext() ) {
				Link l = pipe.next();
				links.add(l);
			}
						
		}
		return links;
	}
	
	static class ExtractLink implements PipeFunction<PathPipe<Vertex>, Link> {
	
		@Override
		public Link compute(PathPipe<Vertex> pipe ) {
			// TODO Auto-generated method stub
			long s_dpid = 0;
			long d_dpid = 0;
			short s_port = 0;
			short d_port = 0;
			List<Vertex> V = new ArrayList<Vertex>();
			V = pipe.next();
			Vertex src_sw = V.get(0);
			Vertex dest_sw = V.get(3);
			Vertex src_port = V.get(1);
			Vertex dest_port = V.get(2);
			s_dpid = HexString.toLong((String) src_sw.getProperty("dpid"));
			d_dpid = HexString.toLong((String) dest_sw.getProperty("dpid"));
			s_port = (Short) src_port.getProperty("number");
			d_port = (Short) dest_port.getProperty("number");
			
			Link l = new Link(s_dpid,s_port,d_dpid,d_port);
			
			return l;
		}
	}
	
	public void finalize() {
		close();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		graph.shutdown();		
	}


}
