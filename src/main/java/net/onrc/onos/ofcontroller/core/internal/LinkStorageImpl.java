package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.routing.Link;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.ILinkStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.linkdiscovery.LinkInfo;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.transform.PathPipe;

/**
 * This is the class for storing the information of links into CassandraDB
 */
public class LinkStorageImpl implements ILinkStorage {
	
	protected static Logger log = LoggerFactory.getLogger(LinkStorageImpl.class);
	protected GraphDBOperation dbop;

	/**
	 * Update a record in the LinkStorage in a way provided by op.
	 * @param link Record of a link to be updated.
	 * @param op Operation to be done.
	 */
	@Override
	public void update(Link link, DM_OPERATION op) {
		update(link, (LinkInfo)null, op);
	}

	/**
	 * Update multiple records in the LinkStorage in a way provided by op.
	 * @param links List of records to be updated.
	 * @param op Operation to be done.
	 */
	@Override
	public void update(List<Link> links, DM_OPERATION op) {
		for (Link lt: links) {
			update(lt, (LinkInfo)null, op);
		}
	}

	/**
	 * Update a record of link with meta-information in the LinkStorage in a way provided by op.
	 * @param link Record of a link to update.
	 * @param linkinfo Meta-information of a link to be updated.
	 * @param op Operation to be done.
	 */
	@Override
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op) {
		switch (op) {
		case UPDATE:
		case CREATE:
		case INSERT:
			updateLink(link, linkinfo, op);
			break;
		case DELETE:
			deleteLink(link);
			break;
		}
	}
	
	/**
	 * Perform INSERT/CREATE/UPDATE operation to update the LinkStorage.
	 * @param lt Record of a link to be updated.
	 * @param linkinfo Meta-information of a link to be updated.
	 * @param op Operation to be done. (only INSERT/CREATE/UPDATE is acceptable)
	 */
	public void updateLink(Link lt, LinkInfo linkinfo, DM_OPERATION op) {
		IPortObject vportSrc = null, vportDst = null;
	
		log.trace("updateLink(): op {} {} {}", new Object[]{op, lt, linkinfo});
		
        try {
            // get source port vertex
        	String dpid = HexString.toHexString(lt.getSrc());
        	short port = lt.getSrcPort();
        	vportSrc = dbop.searchPort(dpid, port);
            
            // get dest port vertex
            dpid = HexString.toHexString(lt.getDst());
            port = lt.getDstPort();
            vportDst = dbop.searchPort(dpid, port);
                        
            if (vportSrc != null && vportDst != null) {
            	// check if the link exists
            	
            	Iterable<IPortObject> currPorts = vportSrc.getLinkedPorts();
            	List<IPortObject> currLinks = new ArrayList<IPortObject>();
            	for (IPortObject V : currPorts) {
            		currLinks.add(V);
            	}

            	if (currLinks.contains(vportDst)) {
            		// TODO: update linkinfo
            		if (op.equals(DM_OPERATION.INSERT) || op.equals(DM_OPERATION.CREATE)) {
            			log.debug("addOrUpdateLink(): failed link exists {} {} src {} dst {}", 
            					new Object[]{op, lt, vportSrc, vportDst});
            		}
            	} else {
            		vportSrc.setLinkPort(vportDst);

            		dbop.commit();
            		log.debug("updateLink(): link added {} {} src {} dst {}", new Object[]{op, lt, vportSrc, vportDst});
            	}
            } else {
            	log.error("updateLink(): failed invalid vertices {} {} src {} dst {}", new Object[]{op, lt, vportSrc, vportDst});
            	dbop.rollback();
            }
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	e.printStackTrace();
        	log.error("updateLink(): titan exception {} {} {}", new Object[]{op, lt, e.toString()});
        }
	}
	
	/**
	 * Delete multiple records in LinkStorage.
	 * @param links List of records to be deleted.
	 */
	@Override
	public void deleteLinks(List<Link> links) {

		for (Link lt : links) {
			deleteLink(lt);
		}
	}
	
	/**
	 * Delete a record in the LinkStorage.
	 * @param link Record to be deleted.
	 */
	@Override
	public void deleteLink(Link lt) {
		IPortObject vportSrc = null, vportDst = null;
		int count = 0;
		
		log.debug("deleteLink(): {}", lt);
		
        try {
            // get source port vertex
         	String dpid = HexString.toHexString(lt.getSrc());
         	short port = lt.getSrcPort();
         	vportSrc = dbop.searchPort(dpid, port);
            
            // get dst port vertex
         	dpid = HexString.toHexString(lt.getDst());
         	port = lt.getDstPort();
         	vportDst = dbop.searchPort(dpid, port);
     		// FIXME: This needs to remove all edges
         	
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
        		dbop.commit();
            	log.debug("deleteLink(): deleted edges src {} dst {}", new Object[]{
            			lt, vportSrc, vportDst});
            	
            } else {
            	log.error("deleteLink(): failed invalid vertices {} src {} dst {}", new Object[]{lt, vportSrc, vportDst});
            	dbop.rollback();
            }
         	
        } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
        	log.error("deleteLink(): titan exception {} {}", new Object[]{lt, e.toString()});
        	dbop.rollback();
        	e.printStackTrace();
        }
	}

	/**
	 * Get list of all links connected to the port specified by given DPID and port number.
	 * @param dpid DPID of desired port.
	 * @param port Port number of desired port.
	 * @return List of links. Empty list if no port was found.
	 */
	// TODO: Fix me
	@Override
	public List<Link> getLinks(Long dpid, short port) {
    	List<Link> links = new ArrayList<Link>();
    	
    	IPortObject srcPort = dbop.searchPort(HexString.toHexString(dpid), port);
    	ISwitchObject srcSw = srcPort.getSwitch();
    	
    	if(srcSw != null) {
        	for(IPortObject dstPort : srcPort.getLinkedPorts()) {
        		ISwitchObject dstSw = dstPort.getSwitch();
        		Link link = new Link(HexString.toLong(srcSw.getDPID()),
        				srcPort.getNumber(),
        				HexString.toLong(dstSw.getDPID()),
        				dstPort.getNumber());
    		
        		links.add(link);
        	}
    	}
    	
     	return links;
	}
	
	/**
	 * Initialize the object. Open LinkStorage using given configuration file.
	 * @param conf Path (absolute path for now) to configuration file.
	 */
	@Override
	public void init(String conf) {
		//TODO extract the DB location from properties
		this.dbop = new GraphDBOperation(conf);
	}

	/**
	 * Delete records of the links connected to the port specified by given DPID and port number.
	 * @param dpid DPID of desired port.
	 * @param port Port number of desired port.
	 */
	// TODO: Fix me
	@Override
	public void deleteLinksOnPort(Long dpid, short port) {
		List<Link> linksToDelete = getLinks(dpid,port);
		
		for(Link l : linksToDelete) {
			deleteLink(l);
		}
	}

	/**
	 * Get list of all links connected to the switch specified by given DPID.
	 * @param dpid DPID of desired switch.
	 * @return List of links. Empty list if no port was found.
	 */
	// TODO: Fix me
	@Override
	public List<Link> getLinks(String dpid) {
		List<Link> links = new ArrayList<Link>();

		ISwitchObject srcSw = dbop.searchSwitch(dpid);
		
		if(srcSw != null) {
			for(IPortObject srcPort : srcSw.getPorts()) {
				for(IPortObject dstPort : srcPort.getLinkedPorts()) {
					ISwitchObject dstSw = dstPort.getSwitch();
					if(dstSw != null) {
		        		Link link = new Link(HexString.toLong(srcSw.getDPID()),
		        				srcPort.getNumber(),
		        				HexString.toLong(dstSw.getDPID()),
		        				dstPort.getNumber());
		        		links.add(link);
					}
				}
			}
		}
		
		return links;
	}

	/**
	 * Get list of all links whose state is ACTIVE.
	 * @return List of active links. Empty list if no port was found.
	 */
	public List<Link> getActiveLinks() {
		Iterable<ISwitchObject> switches = dbop.getActiveSwitches();

		List<Link> links = new ArrayList<Link>(); 
		
		for (ISwitchObject srcSw : switches) {
			for(IPortObject srcPort : srcSw.getPorts()) {
				for(IPortObject dstPort : srcPort.getLinkedPorts()) {
					ISwitchObject dstSw = dstPort.getSwitch();
					
					if(dstSw != null && dstSw.getState().equals("ACTIVE")) {
						links.add(new Link(HexString.toLong(srcSw.getDPID()),
								srcPort.getNumber(),
								HexString.toLong(dstSw.getDPID()),
								dstPort.getNumber()));
					}
				}
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
	
	/**
	 * Finalize the object.
	 */
	public void finalize() {
		close();
	}

	/**
	 * Close LinkStorage.
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
//		graph.shutdown();		
	}


}
