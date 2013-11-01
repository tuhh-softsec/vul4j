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

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.transform.PathPipe;

/**
 * This is the class for storing the information of links into GraphDB
 */
public class LinkStorageImpl implements ILinkStorage {
	
	protected final static Logger log = LoggerFactory.getLogger(LinkStorageImpl.class);
	protected GraphDBOperation op;

	
	/**
	 * Initialize the object. Open LinkStorage using given configuration file.
	 * @param conf Path (absolute path for now) to configuration file.
	 */
	@Override
	public void init(String conf) {
		this.op = new GraphDBOperation(conf);
	}

	// Method designing policy:
	//  op.commit() and op.rollback() MUST called in public (first-class) methods.
	//  A first-class method MUST NOT call other first-class method.
	//  Routine process should be implemented in private method.
	//  A private method MUST NOT call commit or rollback.

	
	/**
	 * Update a record in the LinkStorage in a way provided by dmop.
	 * @param link Record of a link to be updated.
	 * @param linkinfo Meta-information of a link to be updated.
	 * @param dmop Operation to be done.
	 */
	@Override
	public boolean update(Link link, LinkInfo linkinfo, DM_OPERATION dmop) {
		boolean success = false;
		
		switch (dmop) {
		case CREATE:
		case INSERT:
			if (link != null) {
				try {
					if (addLinkImpl(link)) {
						op.commit();
						success = true;
					}
				} catch (Exception e) {
					op.rollback();
					e.printStackTrace();
		        	log.error("LinkStorageImpl:update {} link:{} failed", dmop, link);
				}
			}
			break;
		case UPDATE:
			if (link != null && linkinfo != null) {
				try {
					if (setLinkInfoImpl(link, linkinfo)) {
						op.commit();
						success = true;
					}
				} catch (Exception e) {
					op.rollback();
					e.printStackTrace();
		        	log.error("LinkStorageImpl:update {} link:{} failed", dmop, link);
				}
			}
			break;
		case DELETE:
			if (link != null) {
				try {
					if (deleteLinkImpl(link)) {
						op.commit();
						success = true;
		            	log.debug("LinkStorageImpl:update {} link:{} succeeded", dmop, link);
		            } else {
						op.rollback();
		            	log.debug("LinkStorageImpl:update {} link:{} failed", dmop, link);
					}
				} catch (Exception e) {
					op.rollback();
					e.printStackTrace();
					log.error("LinkStorageImpl:update {} link:{} failed", dmop, link);
				}
			}
			break;
		}
		
		return success;
	}

	@Override
	public boolean addLink(Link link) {
		return addLink(link, null);
	}

	@Override
	public boolean addLink(Link link, LinkInfo linfo) {
		boolean success = false;
		
		try {
			if (addLinkImpl(link)) {
				// Set LinkInfo only if linfo is non-null.
				if (linfo != null && (! setLinkInfoImpl(link, linfo))) {
					log.debug("Adding linkinfo failed: {}", link);
					op.rollback();
				}
				op.commit();
				success = true;
			} else {
				// If we fail here that's because the ports aren't added
				// before we try to add the link
				log.debug("Adding link failed: {}", link);
				op.rollback();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("LinkStorageImpl:addLink link:{} linfo:{} failed", link, linfo);
		}
		
		return success;
	}
	
	/**
	 * Update multiple records in the LinkStorage in a way provided by op.
	 * @param links List of records to be updated.
	 * @param op Operation to be done.
	 */
	@Override
	public boolean addLinks(List<Link> links) {
		boolean success = false;
		
		for (Link lt: links) {
			if (! addLinkImpl(lt)) {
				return false;
			}
		}
		
		try {
			op.commit();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("LinkStorageImpl:addLinks link:s{} failed", links);
		}
		
		return success;
	}

	/**
	 * Delete a record in the LinkStorage.
	 * @param lt Record to be deleted.
	 */
	@Override
	public boolean deleteLink(Link lt) {
		boolean success = false;
		
		log.debug("LinkStorageImpl:deleteLink(): {}", lt);
		
        try {
         	if (deleteLinkImpl(lt)) {
        		op.commit();
        		success = true;
            	log.debug("LinkStorageImpl:deleteLink(): deleted edges {}", lt);
            } else {
            	op.rollback();
            	log.error("LinkStorageImpl:deleteLink(): failed invalid vertices {}", lt);
            }
        } catch (Exception e) {
        	op.rollback();
        	log.error("LinkStorageImpl:deleteLink(): failed {} {}",
        			new Object[]{lt, e.toString()});
        	e.printStackTrace();
        }
        
        return success;
	}

	/**
	 * Delete multiple records in LinkStorage.
	 * @param links List of records to be deleted.
	 */
	@Override
	public boolean deleteLinks(List<Link> links) {
		boolean success = false;
		
		try {
			for (Link lt : links) {
				if (! deleteLinkImpl(lt)) {
					op.rollback();
					return false;
				}
			}
			op.commit();
			success = true;
		} catch (Exception e) {
        	op.rollback();
			e.printStackTrace();
        	log.error("LinkStorageImpl:deleteLinks failed invalid vertices {}", links);
		}
		
		return success;
	}

	/**
	 * Get list of all links connected to the port specified by given DPID and port number.
	 * @param dpid DPID of desired port.
	 * @param port Port number of desired port.
	 * @return List of links. Empty list if no port was found.
	 */
	@Override
	public List<Link> getLinks(Long dpid, short port) {
    	List<Link> links = new ArrayList<Link>();
    	
    	IPortObject srcPort = op.searchPort(HexString.toHexString(dpid), port);
    	ISwitchObject srcSw = srcPort.getSwitch();
    	
    	if(srcSw != null && srcPort != null) {
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
	 * Delete records of the links connected to the port specified by given DPID and port number.
	 * @param dpid DPID of desired port.
	 * @param port Port number of desired port.
	 */
	@Override
	public boolean deleteLinksOnPort(Long dpid, short port) {
		boolean success = false;
		
		List<Link> linksToDelete = getLinks(dpid, port);
		try {
			for(Link l : linksToDelete) {
				if (! deleteLinkImpl(l)) {
					op.rollback();
					log.error("LinkStorageImpl:deleteLinksOnPort dpid:{} port:{} failed", dpid, port);
					return false;
				}
			}
			op.commit();
			success = true;
		} catch (Exception e) {
        	op.rollback();
			e.printStackTrace();
        	log.error("LinkStorageImpl:deleteLinksOnPort dpid:{} port:{} failed", dpid, port);
		}
		
		return success;
	}

	/**
	 * Get list of all links connected to the switch specified by given DPID.
	 * @param dpid DPID of desired switch.
	 * @return List of links. Empty list if no port was found.
	 */
	@Override
	public List<Link> getLinks(String dpid) {
		List<Link> links = new ArrayList<Link>();

		ISwitchObject srcSw = op.searchSwitch(dpid);
		
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
		Iterable<ISwitchObject> switches = op.getActiveSwitches();

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
	
	@Override
	public LinkInfo getLinkInfo(Link link) {
		// TODO implement this
		return null;
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

	/**
	 * Update a record of link with meta-information in the LinkStorage.
	 * @param link Record of a link to update.
	 * @param linkinfo Meta-information of a link to be updated.
	 */
	private boolean setLinkInfoImpl(Link link, LinkInfo linkinfo) {
		// TODO implement this
		
		return false;
	}

	private boolean addLinkImpl(Link lt) {
		boolean success = false;
		
		IPortObject vportSrc = null, vportDst = null;
		
		// get source port vertex
		String dpid = HexString.toHexString(lt.getSrc());
		short port = lt.getSrcPort();
		vportSrc = op.searchPort(dpid, port);
		
		// get dest port vertex
		dpid = HexString.toHexString(lt.getDst());
		port = lt.getDstPort();
		vportDst = op.searchPort(dpid, port);
		            
		if (vportSrc != null && vportDst != null) {
			IPortObject portExist = null;
			// check if the link exists
			for (IPortObject V : vportSrc.getLinkedPorts()) {
				if (V.equals(vportDst)) {
					portExist = V;
					break;
				}
			}
		
			if (portExist == null) {
				vportSrc.setLinkPort(vportDst);
				success = true;
			} else {
				log.debug("LinkStorageImpl:addLinkImpl failed link exists {} {} src {} dst {}", 
						new Object[]{op, lt, vportSrc, vportDst});
			}
		}
		
		return success;
	}

	private boolean deleteLinkImpl(Link lt) {
		boolean success = false;
		IPortObject vportSrc = null, vportDst = null;
	
	    // get source port vertex
	 	String dpid = HexString.toHexString(lt.getSrc());
	 	short port = lt.getSrcPort();
	 	vportSrc = op.searchPort(dpid, port);
	    
	    // get dst port vertex
	 	dpid = HexString.toHexString(lt.getDst());
	 	port = lt.getDstPort();
	 	vportDst = op.searchPort(dpid, port);
	 	
		// FIXME: This needs to remove all edges
	 	if (vportSrc != null && vportDst != null) {
	 		vportSrc.removeLink(vportDst);
	    	log.debug("deleteLinkImpl(): deleted edges src {} dst {}", new Object[]{
	    			lt, vportSrc, vportDst});
	    	success = true;
	    }
	    
	 	return success;
	}

	// TODO should be moved to TopoLinkServiceImpl (never used in this class)
	static class ExtractLink implements PipeFunction<PathPipe<Vertex>, Link> {
	
		@SuppressWarnings("unchecked")
		@Override
		public Link compute(PathPipe<Vertex> pipe ) {
			long s_dpid = 0;
			long d_dpid = 0;
			short s_port = 0;
			short d_port = 0;
			List<Vertex> V = new ArrayList<Vertex>();
			V = (List<Vertex>)pipe.next();
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


}
