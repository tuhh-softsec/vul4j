package net.onrc.onos.ofcontroller.bgproute;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IBgpRouteService extends IFloodlightService {

	//public Rib lookupRib(byte[] dest);

	//public Ptree getPtree();
	public IPatriciaTrie getPtree();

	public String getBGPdRestIp();

	public String getRouterId();

	public void clearPtree();
	
	/**
	 * Pass a RIB update to the {@link IBgpRouteService}
	 * @param update
	 */
	public void newRibUpdate(RibUpdate update);
	
	//TODO This functionality should be provided by some sort of Ptree listener framework
	//public void prefixAdded(PtreeNode node);
	//public void prefixDeleted(PtreeNode node);
}
