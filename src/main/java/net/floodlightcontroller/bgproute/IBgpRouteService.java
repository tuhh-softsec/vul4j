package net.floodlightcontroller.bgproute;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IBgpRouteService extends IFloodlightService {

    public Rib lookupRib(byte[] dest);
    
    public Ptree getPtree();
    
    public String getBGPdRestIp();
    
    public String getRouterId();
    
	  public void  clearPtree() ;
       
    
}
