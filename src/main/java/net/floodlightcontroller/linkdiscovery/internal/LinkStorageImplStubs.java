/**
 * 
 */
package net.floodlightcontroller.linkdiscovery.internal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;

/**
 * @author pankaj
 *
 */
public class LinkStorageImplStubs implements ILinkStorage {

	protected static Logger log = LoggerFactory.getLogger(LinkStorageImplStubs.class);

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#update(net.floodlightcontroller.routing.Link, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(Link link, DM_OPERATION op) {
		// TODO Auto-generated method stub
		log.trace("LinkStorage:update(): op {} link {}", op, link);
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#update(java.util.List, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(List<Link> list, DM_OPERATION op) {
		// TODO Auto-generated method stub
		log.trace("LinkStorage:update(): op {} link {}", op, list);
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#update(net.floodlightcontroller.routing.Link, net.floodlightcontroller.linkdiscovery.LinkInfo, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op) {
		// TODO Auto-generated method stub
		log.trace("LinkStorage:update(): op {} link {}", op, link);
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#getLinks(java.lang.Long, int)
	 */
	@Override
	public List<Link> getLinks(Long dpid, int port) {
		// TODO Auto-generated method stub
		log.trace("LinkStorage:getLinks(): dpid {} port {}", dpid, port);

		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#deleteLinks(java.lang.Long, int)
	 */
	@Override
	public void deleteLinks(Long dpid, int port) {
		// TODO Auto-generated method stub
		log.trace("LinkStorage:deleteLinks(): dpid {} port {}", dpid, port);
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#init(java.lang.String)
	 */
	@Override
	public void init(String conf) {
		// TODO Auto-generated method stub

	}

}
