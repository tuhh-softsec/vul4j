package net.floodlightcontroller.linkdiscovery.internal;

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
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class LinkStorageImpl implements ILinkStorage {
	public TitanGraph graph;
	protected static Logger log = LoggerFactory.getLogger(LinkStorageImpl.class);
	
	@Override
	public void update(Link link, DM_OPERATION op) {
		update (link, (LinkInfo)null, op);
	}

	@Override
	public void update(List<Link> links, DM_OPERATION op) {
		log.debug("LinkStorage:update(): {} {}", op, links);

		for (Link lt: links) {
			update(lt, op);
		}
	}

	@Override
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op) {
		log.debug("LinkStorage:update(): {} {}", op, link);
		
		switch (op) {
		case UPDATE:
		case CREATE:
		case INSERT:
			addLink(link, linkinfo);
			break;
		case DELETE:
			break;
		}
	}

	protected void addLink(Link lt, LinkInfo linkinfo) {
		Vertex vswSrc, vswDst;
		Vertex vportSrc = null, vportDst = null;
	
		log.info("addLink(): {} {} getSrc {}", new Object[]{lt, linkinfo, lt.getSrc()});
		
        try {
            // get source port vertex
        	String dpid = HexString.toHexString(lt.getSrc());
        	short port = lt.getSrcPort();
            if ((vswSrc = graph.getVertices("dpid", dpid).iterator().next()) != null) {
            	log.debug("addLink(): sw exists {} {}", dpid, vswSrc);
            	GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
            	
            	//if (vswSrc.query().direction(Direction.OUT).labels("on").has("number",port).vertices().iterator().hasNext()) {
            	pipe.start(vswSrc).out("on").has("number", (int)port);
            	//pipe.start(vswSrc).out("on");
            	//log.debug("pipe count {}", pipe.count());
            	if (pipe.hasNext()) {
            		//vportSrc = vswSrc.query().direction(Direction.OUT).labels("on").has("number",port).vertices().iterator().next();
            		vportSrc = pipe.next();
            		log.debug("addLink(): port found {} {}", port, vportSrc);
            	} else {
            		log.error("addLink(): sw {} port {} not found", dpid, port);
            	}
            }
            
            // get dest port vertex
            dpid = HexString.toHexString(lt.getDst());
            port = lt.getDstPort();
            if ((vswDst = graph.getVertices("dpid",dpid).iterator().next()) != null) {
            	log.debug("addLink(): sw exists {} {}", dpid, vswDst);
            	GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
            	pipe.start(vswDst).out("on").has("number", (int)port);
            	//if (vswDst.query().direction(Direction.OUT).labels("on").has("number",port).vertices().iterator().hasNext()) {
            	if (pipe.hasNext()){
            		//vportDst = vswDst.query().direction(Direction.OUT).labels("on").has("number",port).vertices().iterator().next();
            		vportDst = pipe.next();
            		log.debug("addLink(): port found {} {}", port, vportDst);
            	} else {
            		log.error ("addLink(): sw {} port {} not found", dpid, port);
            	}
            }
            
            if (vportSrc != null && vportDst != null) {
            	//TODO: If Edge already exists should we remove and add again?
            	if (vportSrc.query().direction(Direction.OUT).labels("link").vertices().iterator().hasNext() &&
            		vportSrc.query().direction(Direction.OUT).labels("link").vertices().iterator().next().equals(vportDst)) {
            		//FIXME: Succeed silently for now
            	} else {
            		graph.addEdge(null, vportSrc, vportDst, "link");
            		graph.stopTransaction(Conclusion.SUCCESS);
            	}
        		log.debug("addLink(): link added {} src {} dst {}", new Object[]{lt, vportSrc, vportDst});
            } else {
            	log.error("addLink(): failed {} src {} dst {}", new Object[]{lt, vportSrc, vportDst});
            	graph.stopTransaction(Conclusion.FAILURE);
            }
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	log.error("addLink(): {} failed", lt);
        }
	}
	
	@Override
	public List<Link> getLinks(Long dpid, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLinks(Long dpid, int port) {
		// TODO Auto-generated method stub

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
}
