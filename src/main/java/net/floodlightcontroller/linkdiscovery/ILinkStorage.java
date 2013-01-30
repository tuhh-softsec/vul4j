package net.floodlightcontroller.linkdiscovery;

import java.util.List;

import net.floodlightcontroller.core.INetMapStorage;
import net.floodlightcontroller.routing.Link;

public interface ILinkStorage extends INetMapStorage {
	
    /*
     * Link creation
     */
	public void update(Link link, DM_OPERATION op);
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op);
	public void update(List<Link> List, DM_OPERATION op);

	/*
	 *  Add Linkinfo
	 */
	public void addOrUpdateLink (Link link, LinkInfo linkinfo, DM_OPERATION op);
	
	/*
	 * Delete a single link
	 */
	public void deleteLink(Link link);

	/*
	 * Delete links associated with dpid and port 
	 * If only dpid is used, All links associated for switch are removed
	 * Useful for port up/down and also switch join/remove events
	 */ 
	public void deleteLinksOnPort(Long dpid, short port);
	
	/*
	 * Delete a list of links
	 */
	public void deleteLinks(List<Link> links);

	/*
	 * Get Links from Storage
	 *  If dpid and port both are specified specific link is retrieved
	 *  If only dpid is set all links associated with Switch are retrieved
	 */
	public List<Link> getLinks(Long dpid, short port);
	public List<Link> getLinks(String dpid);
	public List<Link> getActiveLinks();
	
	/*
	 * Init with Storage conf
	 */
	public void init(String conf);

}
