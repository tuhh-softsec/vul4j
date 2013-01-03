/**
 * 
 */
package net.floodlightcontroller.linkdiscovery.internal;

import java.util.List;

import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;

/**
 * @author pankaj
 *
 */
public class LinkStorageImplStubs implements ILinkStorage {

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#update(net.floodlightcontroller.routing.Link, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(Link link, DM_OPERATION op) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#update(java.util.List, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(List<Link> List, DM_OPERATION op) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#update(net.floodlightcontroller.routing.Link, net.floodlightcontroller.linkdiscovery.LinkInfo, net.floodlightcontroller.core.INetMapStorage.DM_OPERATION)
	 */
	@Override
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#getLinks(java.lang.Long, int)
	 */
	@Override
	public List<Link> getLinks(Long dpid, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#deleteLinks(java.lang.Long, int)
	 */
	@Override
	public void deleteLinks(Long dpid, int port) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.linkdiscovery.ILinkStorage#init(java.lang.String)
	 */
	@Override
	public void init(String conf) {
		// TODO Auto-generated method stub

	}

}
