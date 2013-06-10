package net.floodlightcontroller.bgproute;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IBgpRouteService extends IFloodlightService {

	public Rib lookupRib(byte[] dest);

	public Ptree getPtree();

	public String getBGPdRestIp();

	public String getRouterId();

	public void clearPtree();
	
	//TODO This functionality should be provided by some sort of Ptree listener framework
	public void prefixAdded(PtreeNode node);
	public void prefixDeleted(PtreeNode node);
}
