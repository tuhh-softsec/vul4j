package net.onrc.onos.ofcontroller.bgproute;


import org.restlet.resource.Post;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BgpRouteResourceSynch extends ServerResource {
    
	protected static Logger log = LoggerFactory
            .getLogger(BgpRouteResource.class);
	
	@Post
	public String store(String fmJson) {
		
		IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
                get(IBgpRouteService.class.getCanonicalName());
	  
		String router_id = (String) getRequestAttributes().get("routerid");
		String prefix = (String) getRequestAttributes().get("prefix");
		String mask = (String) getRequestAttributes().get("mask");
		String nexthop = (String) getRequestAttributes().get("nexthop");
				
			try{		
				
			String BGPdRestIp = bgpRoute.getBGPdRestIp();	
				
			//bgpdRestIp includes port number, such as 1.1.1.1:8080
			RestClient.post("http://"+BGPdRestIp+"/wm/bgp/"+router_id+"/"+prefix+"/"+mask+"/"+nexthop);
			}catch(Exception e)
			{e.printStackTrace();}
			
			String reply = "";
			reply = "[POST: " + prefix + "/" + mask + ":" + nexthop + "/synch]";
			log.info(reply);
			
    return reply + "\n";
		
	
	}
	
	@Delete
	public String delete(String fmJson) {
		IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
                get(IBgpRouteService.class.getCanonicalName());
        
		String routerId = (String) getRequestAttributes().get("routerid");
		String prefix = (String) getRequestAttributes().get("prefix");
		String mask = (String) getRequestAttributes().get("mask");
		String nextHop = (String) getRequestAttributes().get("nexthop");
		
		String reply = "";
		try{
					String BGPdRestIp = bgpRoute.getBGPdRestIp();	
						
					RestClient.delete("http://"+BGPdRestIp+"/wm/bgp/"+routerId+"/"+prefix+"/"+mask+"/"+nextHop);	
														
		}catch(Exception e)
		{e.printStackTrace();}
		
		reply =reply + "[DELE: " + prefix + "/" + mask + ":" + nextHop + "/synch]";
					
		log.info(reply);		


		return reply + "\n";
	}
}
