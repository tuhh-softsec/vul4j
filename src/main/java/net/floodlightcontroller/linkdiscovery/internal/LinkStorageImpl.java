package net.floodlightcontroller.linkdiscovery.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;

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
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class LinkStorageImpl implements ILinkStorage {
	public TitanGraph graph;
	protected static Logger log = LoggerFactory.getLogger(LinkStorageImpl.class);

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
		Vertex vportSrc = null, vportDst = null;
	
		log.debug("addOrUpdateLink(): op {} {} {}", new Object[]{op, lt, linkinfo});
		
        try {
            // get source port vertex
        	String dpid = HexString.toHexString(lt.getSrc());
        	short port = lt.getSrcPort();
        	vportSrc = getPortVertex(dpid, port);
            
            // get dest port vertex
            dpid = HexString.toHexString(lt.getDst());
            port = lt.getDstPort();
            vportDst = getPortVertex(dpid, port);
                        
            if (vportSrc != null && vportDst != null) {
            	
            	// check if the link exists
            	List<Vertex> currLinks = new ArrayList<Vertex>();
            	for (Vertex V : vportSrc.query().direction(Direction.OUT).labels("link").vertices()) {
            		currLinks.add(V);
            	}
            	
            	if (currLinks.contains(vportDst)) {
            		// TODO: update linkinfo
            		if (op.equals(DM_OPERATION.INSERT) || op.equals(DM_OPERATION.CREATE)) {
            			log.debug("addOrUpdateLink(): Failure: link exists {} {} src {} dst {}", 
            					new Object[]{op, lt, vportSrc, vportDst});
            		}
            	} else {
            		graph.addEdge(null, vportSrc, vportDst, "link");
            		graph.stopTransaction(Conclusion.SUCCESS);
            		log.debug("addOrUpdateLink(): link added {} {} src {} dst {}", new Object[]{op, lt, vportSrc, vportDst});
            	}
            } else {
            	log.error("addOrUpdateLink(): failed {} {} src {} dst {}", new Object[]{op, lt, vportSrc, vportDst});
            	graph.stopTransaction(Conclusion.FAILURE);
            }
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	log.error("addOrUpdateLink(): failed {} {}", new Object[]{op, lt});
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
		Vertex vportSrc = null, vportDst = null;
		int count = 0;
		
		log.debug("deleteLink(): {}", lt);
		
        try {
            // get source port vertex
         	String dpid = HexString.toHexString(lt.getSrc());
         	short port = lt.getSrcPort();
         	vportSrc = getPortVertex(dpid, port);
            
            // get dst port vertex
         	dpid = HexString.toHexString(lt.getDst());
         	port = lt.getDstPort();
         	vportDst = getPortVertex(dpid, port);
         	
         	if (vportSrc != null && vportDst != null) {
         		for (Edge e : vportSrc.getEdges(Direction.OUT)) {
         			log.debug("deleteLink(): {} in {} out {}", 
         					new Object[]{e.getLabel(), e.getVertex(Direction.IN), e.getVertex(Direction.OUT)});
         			if (e.getLabel().equals("link") && e.getVertex(Direction.IN).equals(vportDst)) {
         				graph.removeEdge(e);
         				count++;
         			}
         		}
        		graph.stopTransaction(Conclusion.SUCCESS);
            	log.debug("deleteLink(): deleted {} edges {} src {} dst {}", new Object[]{
            			count, lt, vportSrc, vportDst});
            	
            } else {
            	log.error("deleteLink(): failed src port vertex not found {} src {} dst {}", new Object[]{lt, vportSrc, vportDst});
            	graph.stopTransaction(Conclusion.FAILURE);
            }
         	
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	log.error("deleteLink(): {} failed", lt);
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
		//TODO extract the DB location from conf
	
        graph = TitanFactory.open(conf);
        
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

}
