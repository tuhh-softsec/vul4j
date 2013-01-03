package net.floodlightcontroller.linkdiscovery;

import java.util.List;

import net.floodlightcontroller.core.INetMapStorage;
import net.floodlightcontroller.routing.Link;

public interface ILinkStorage extends INetMapStorage {
	
    /*
     * Link creation
     */
	public void update(Link link, DM_OPERATION op);
	public void update(List<Link> List, DM_OPERATION op);
	
	/*
	 *  Add Linkinfo
	 */
	public void update(Link link, LinkInfo linkinfo, DM_OPERATION op);
	
	/*
	 * Get Links from Storage
	 *  If dpid and port both are specified specific link is retrieved
	 *  If only dpid is set all links associated with Switch are retrieved
	 */
	public List<Link> getLinks(Long dpid, int port);

	/*
	 * Delete links associated with dpid and port 
	 * If only dpid is used, All links associated for switch are removed
	 * Useful for port up/down and also switch join/remove events
	 */ 
	public void deleteLinks(Long dpid, int port);
	
	/*
	 * Init with Storage conf
	 */
	public void init(String conf);
}
