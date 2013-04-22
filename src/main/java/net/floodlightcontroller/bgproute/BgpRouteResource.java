package net.floodlightcontroller.bgproute;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.floodlightcontroller.restclient.RestClient;
import java.io.UnsupportedEncodingException;  
import java.nio.ByteBuffer;  

public class BgpRouteResource extends ServerResource {
    
	protected static Logger log = LoggerFactory
            .getLogger(BgpRouteResource.class);
	
	private String addrToString(byte [] addr) {
	    String str = "";
		
		for (int i = 0; i < 4; i++) {
			int val = (addr[i] & 0xff);
			str += val;
			if (i != 3)
				str += ".";
		}
		
		return str;
	}
	
	@SuppressWarnings("unused")
	@Get
		public String get(String fmJson) {		
		String linpp=fmJson;
			String dest = (String) getRequestAttributes().get("dest");
			String output = "";
			IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
	                get(IBgpRouteService.class.getCanonicalName());
			
			if (dest != null) {
				Prefix p = new Prefix(dest, 32);
				if (p == null) {
					return "[GET]: dest address format is wrong";
				}
							
				// the dest here refers to router-id
				//BGPdRestIp includes port number, such as 1.1.1.1:8080
				String BGPdRestIp = bgpRoute.getBGPdRestIp();
				String url="http://"+BGPdRestIp+"/wm/bgp/"+dest;
				
							
				
				RestClient.get(url);
				output="Get rib from bgpd finished!\n";
				return output;
			
			} else {
				Ptree ptree = bgpRoute.getPtree();
				output += "{\n  \"rib\": [\n";
				boolean printed = false;
				for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
					if (node.rib == null) {
						continue;
					}
					if (printed == true) {
						output += ",\n";
					}
					output += "    {\"prefix\": \"" + addrToString(node.key) + "/" + node.keyBits +"\", ";
					output += "\"nexthop\": \"" + addrToString(node.rib.nextHop.getAddress()) +"\"}";
					printed = true;
				}
				//output += "{\"router_id\": \"" + addrToString(node.rib.routerId.getAddress()) +"\"}\n";
				output += "\n  ]\n}\n";
	   
			}
			return output;
		}

	public static ByteBuffer toByteBuffer(String value) throws UnsupportedEncodingException
	  {
	  return ByteBuffer.wrap(value.getBytes("UTF-8"));
	  }

public static String toString(ByteBuffer buffer) throws UnsupportedEncodingException
	 {
	    byte[] bytes = new byte[buffer.remaining()];
	    buffer.get(bytes);
	    return new String(bytes, "UTF-8");
	    
	   }

	
	@Post
	public String store(String fmJson) {
        IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
                get(IBgpRouteService.class.getCanonicalName());
 	
	  Ptree ptree = bgpRoute.getPtree();
	
		String router_id = (String) getRequestAttributes().get("routerid");
		String prefix = (String) getRequestAttributes().get("prefix");
		String mask = (String) getRequestAttributes().get("mask");
		String nexthop = (String) getRequestAttributes().get("nexthop");
		String capability = (String) getRequestAttributes().get("capability");
		
	
		String reply = "";
		
		if (capability == null) {
		
			// this is a prefix add
			Prefix p = new Prefix(prefix, Integer.valueOf(mask));
			PtreeNode node = ptree.acquire(p.getAddress(), p.masklen);
			Rib rib = new Rib(router_id, nexthop, p.masklen);

			if (node.rib != null) {
				node.rib = null;
				ptree.delReference(node);
			}
			node.rib = rib;
			
			reply = "[POST: " + prefix + "/" + mask + ":" + nexthop + "]";
			log.info(reply);
	
			
		}else if(capability.equals("1")){
			reply = "[POST-capability: " + capability + "]\n";
			log.info(reply);
			// to store the number in the top node of the Ptree	
			
		}else{			
			reply = "[POST-capability: " + capability + "]\n";
			log.info(reply);
			// to store the number in the top node of the Ptree	
	
		}
		
		
		return reply + "\n";
		
	
	}
	
	@Delete
	public String delete(String fmJson) {
		IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
                get(IBgpRouteService.class.getCanonicalName());

   Ptree ptree = bgpRoute.getPtree();
      	
		String routerId = (String) getRequestAttributes().get("routerid");
		String prefix = (String) getRequestAttributes().get("prefix");
		String mask = (String) getRequestAttributes().get("mask");
		String nextHop = (String) getRequestAttributes().get("nexthop");
		String capability = (String) getRequestAttributes().get("capability");
		
		String reply = "";
		
		if (capability == null) {
					// this is a prefix delete
					Prefix p = new Prefix(prefix, Integer.valueOf(mask));
										
					PtreeNode node = ptree.lookup(p.getAddress(), p.masklen);
									
					Rib r = new Rib(routerId, nextHop, p.masklen);
					
					if (node != null && node.rib != null) {
						
						if (r.equals(node.rib)) {
							
							node.rib = null;
							ptree.delReference(node);					
						}
					}
					
							
					reply =reply + "[DELE: " + prefix + "/" + mask + ":" + nextHop + "]";
					
		}else {
			
			// clear the local rib: Ptree			
			bgpRoute.clearPtree();
			reply = "[DELE-capability: " + capability + "; The local Rib is cleared!]\n";
			
			
			// to store the number in the top node of the Ptree	
				
		}	
		log.info(reply);
	
		return reply + "\n";
	}
}
