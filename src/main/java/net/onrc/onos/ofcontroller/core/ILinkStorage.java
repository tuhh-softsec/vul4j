package net.onrc.onos.ofcontroller.core;

import java.util.List;

import net.floodlightcontroller.routing.Link;
import net.onrc.onos.ofcontroller.linkdiscovery.LinkInfo;

public interface ILinkStorage extends INetMapStorage {
	
    /*
	 * Init with Storage conf
	 */
	public void init(String conf);
	
	/*
	 * Generic operation method
	 */
	public boolean update(Link link, LinkInfo linkinfo, DM_OPERATION op);
	
	/*
     * Link creation
     */
	public boolean addLink(Link link);
	public boolean addLink(Link link, LinkInfo linfo);
	public boolean addLinks(List<Link> links);
	
	/*
	 * Link deletion
	 */
	public boolean deleteLink(Link link);
	public boolean deleteLinks(List<Link> links);

	/*
	 * Utility method to delete links associated with dpid and port 
	 * If only dpid is used, All links associated for switch are removed
	 * Useful for port up/down and also switch join/remove events
	 */ 
	public boolean deleteLinksOnPort(Long dpid, short port);

	/*
	 * Get Links from Storage
	 *  If dpid and port both are specified specific link is retrieved
	 *  If only dpid is set all links associated with Switch are retrieved
	 */
	public List<Link> getLinks(Long dpid, short port);

	public List<Link> getLinks(String dpid);

	/**
	 * Get list of all reverse links connected to the switch specified by
	 * given DPID.
	 * @param dpid DPID of desired switch.
	 * @return List of reverse links. Empty list if no port was found.
	 */
	public List<Link> getReverseLinks(String dpid);

	public List<Link> getActiveLinks();

	public LinkInfo getLinkInfo(Link link);
}
