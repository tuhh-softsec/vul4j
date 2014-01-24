package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.routing.Link;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.ofcontroller.core.ILinkStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.linkdiscovery.LinkInfo;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.impls.ramcloud.PerfMon;
import net.onrc.onos.graph.GraphDBManager;

/**
 * This is the class for storing the information of links into GraphDB
 */
public class LinkStorageImpl implements ILinkStorage {

	protected final static Logger log = LoggerFactory.getLogger(LinkStorageImpl.class);
	protected DBOperation dbop;
	private static PerfMon pm = PerfMon.getInstance();

	/**
	 * Initialize the object. Open LinkStorage using given configuration file.
	 * @param conf Path (absolute path for now) to configuration file.
	 */
	@Override
	public void init(final String dbStore, final String conf) {
		this.dbop = GraphDBManager.getDBOperation("ramcloud", "/tmp/ramcloud.conf");

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
						dbop.commit();
						success = true;
					}
				} catch (Exception e) {
					dbop.rollback();
					e.printStackTrace();
		        	log.error("LinkStorageImpl:update {} link:{} failed", dmop, link);
				}
			}
			break;
		case UPDATE:
			if (link != null && linkinfo != null) {
				try {
					if (setLinkInfoImpl(link, linkinfo)) {
						dbop.commit();
						success = true;
					}
				} catch (Exception e) {
					dbop.rollback();
					e.printStackTrace();
		        	log.error("LinkStorageImpl:update {} link:{} failed", dmop, link);
				}
			}
			break;
		case DELETE:
			if (link != null) {
				try {
					if (deleteLinkImpl(link)) {
						dbop.commit();
						success = true;
		            	log.debug("LinkStorageImpl:update {} link:{} succeeded", dmop, link);
		            } else {
						dbop.rollback();
		            	log.debug("LinkStorageImpl:update {} link:{} failed", dmop, link);
					}
				} catch (Exception e) {
					dbop.rollback();
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

	private void deleteDeviceOnPort(Long dpid, Short number)
	{
		IPortObject srcPortObject = dbop.searchPort(HexString.toHexString(dpid), number);
		if (srcPortObject == null)
		    return;
		Iterable<IDeviceObject> devices = srcPortObject.getDevices();
		if (devices == null)
		    return;
		if (devices.iterator().hasNext()) {
			for (IDeviceObject deviceObject: srcPortObject.getDevices()) {
				srcPortObject.removeDevice(deviceObject);
				log.debug("delete Device "+ deviceObject.getMACAddress() +
						" from sw: {} port: {} due to a new link added",
						dpid, number);
			}
		}
	}

	@Override
	public boolean addLink(Link link, LinkInfo linfo) {
		boolean success = false;

		try {
			//delete the Device attachment points for the related switch and port
			deleteDeviceOnPort(link.getSrc(),link.getSrcPort());
			deleteDeviceOnPort(link.getDst(),link.getDstPort());

			pm.addlink_start();
			if (addLinkImpl(link)) {
				// Set LinkInfo only if linfo is non-null.
				if (linfo != null && (! setLinkInfoImpl(link, linfo))) {
					log.debug("Adding linkinfo failed: {}", link);
					dbop.rollback();
				}
				dbop.commit();
				pm.addlink_end();
				success = true;
			} else {
				pm.addlink_end();
				// If we fail here that's because the ports aren't added
				// before we try to add the link
				log.debug("Adding link failed: {}", link);
				dbop.rollback();
			}
		} catch (Exception e) {
			dbop.rollback();
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
			dbop.commit();
			success = true;
		} catch (Exception e) {
			dbop.rollback();
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
        		dbop.commit();
        		success = true;
            	log.debug("LinkStorageImpl:deleteLink(): deleted edges {}", lt);
            } else {
            	dbop.rollback();
            	log.error("LinkStorageImpl:deleteLink(): failed invalid vertices {}", lt);
            }
        } catch (Exception e) {
        	dbop.rollback();
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
					dbop.rollback();
					return false;
				}
			}
			dbop.commit();
			success = true;
		} catch (Exception e) {
        	dbop.rollback();
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
	    IPortObject srcPort = dbop.searchPort(HexString.toHexString(dpid), port);
	    if (srcPort == null)
		return links;
	    ISwitchObject srcSw = srcPort.getSwitch();
	    if (srcSw == null)
		return links;

	    for(IPortObject dstPort : srcPort.getLinkedPorts()) {
		ISwitchObject dstSw = dstPort.getSwitch();
		if (dstSw != null) {
		    Link link = new Link(dpid, port,
					 HexString.toLong(dstSw.getDPID()),
					 dstPort.getNumber());
		    links.add(link);
		}
	    }
	    return links;
	}

	/**
	 * Get list of all reverse links connected to the port specified by given DPID and port number.
	 * @param dpid DPID of desired port.
	 * @param port Port number of desired port.
	 * @return List of reverse links. Empty list if no port was found.
	 */
	@Override
	public List<Link> getReverseLinks(Long dpid, short port) {
	    List<Link> links = new ArrayList<Link>();

	    IPortObject srcPort = dbop.searchPort(HexString.toHexString(dpid), port);
	    if (srcPort == null)
		return links;
	    ISwitchObject srcSw = srcPort.getSwitch();
	    if (srcSw == null)
		return links;

	    for(IPortObject dstPort : srcPort.getReverseLinkedPorts()) {
		ISwitchObject dstSw = dstPort.getSwitch();
		if (dstSw != null) {
		    Link link = new Link(HexString.toLong(dstSw.getDPID()),
					 dstPort.getNumber(),
					 dpid, port);
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
					dbop.rollback();
					log.error("LinkStorageImpl:deleteLinksOnPort dpid:{} port:{} failed", dpid, port);
					return false;
				}
			}
			dbop.commit();
			success = true;
		} catch (Exception e) {
        	dbop.rollback();
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
		ISwitchObject srcSw = dbop.searchSwitch(dpid);

		if(srcSw != null) {
			for(IPortObject srcPort : srcSw.getPorts()) {
				for(IPortObject dstPort : srcPort.getLinkedPorts()) {
					ISwitchObject dstSw = dstPort.getSwitch();
					if(dstSw != null) {
					    Link link = new Link(HexString.toLong(dpid),
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
	 * Get list of all reverse links connected to the switch specified by
	 * given DPID.
	 * @param dpid DPID of desired switch.
	 * @return List of reverse links. Empty list if no port was found.
	 */
	@Override
	public List<Link> getReverseLinks(String dpid) {
		List<Link> links = new ArrayList<Link>();

		ISwitchObject srcSw = dbop.searchSwitch(dpid);

		if(srcSw != null) {
			for(IPortObject srcPort : srcSw.getPorts()) {
				for(IPortObject dstPort : srcPort.getReverseLinkedPorts()) {
					ISwitchObject dstSw = dstPort.getSwitch();
					if(dstSw != null) {
		        		Link link = new Link(
							HexString.toLong(dstSw.getDPID()),
							dstPort.getNumber(),

							HexString.toLong(dpid),
							srcPort.getNumber());
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
	@Override
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

	@Override
	public LinkInfo getLinkInfo(Link link) {
		// TODO implement this
		return null;
	}

	/**
	 * Finalize the object.
	 */
	@Override
	protected void finalize() {
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
		log.debug("addLinkImpl Src dpid : {} port : {}", dpid, port);
		vportSrc = dbop.searchPort(dpid, port);

		// get dest port vertex
		dpid = HexString.toHexString(lt.getDst());
		port = lt.getDstPort();
		log.debug("addLinkImpl Dst dpid : {} port : {}", dpid, port);
		vportDst = dbop.searchPort(dpid, port);

		log.debug("addLinkImpl vportSrc : {} vportDst : {}", vportSrc, vportDst);

		if (vportSrc != null && vportDst != null) {
			IPortObject portExist = null;
			// check if the link exists
			for (IPortObject V : vportSrc.getLinkedPorts()) {
			        log.debug("vportSrc.getLinkedPorts() :{}", V);
				if (V.equals(vportDst)) {
					portExist = V;
					break;
				}
			}

			if (portExist == null) {
				vportSrc.setLinkPort(vportDst);
				success = true;
			} else {
				log.error("LinkStorageImpl:addLinkImpl failed link exists {} {} src {} dst {}",
						new Object[]{dbop, lt, vportSrc, vportDst});
			}
		} else {
			log.error("Ports not found : {}", lt);
		}

		return success;
	}

	private boolean deleteLinkImpl(Link lt) {
		boolean success = false;
		IPortObject vportSrc = null, vportDst = null;

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
			vportSrc.removeLink(vportDst);
			log.debug("deleteLinkImpl(): deleted edge {} src {} dst {}", new Object[]{
				lt, vportSrc, vportDst});
			success = true;
		}

		return success;
	}
}
