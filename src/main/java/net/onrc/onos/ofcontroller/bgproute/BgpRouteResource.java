package net.onrc.onos.ofcontroller.bgproute;

import java.util.Iterator;

import net.onrc.onos.ofcontroller.bgproute.RibUpdate.Operation;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BgpRouteResource extends ServerResource {

	protected static Logger log = LoggerFactory.getLogger(BgpRouteResource.class);

	@Get
	public String get(String fmJson) {
		String dest = (String) getRequestAttributes().get("dest");
		String output = "";
		IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
				get(IBgpRouteService.class.getCanonicalName());

		if (dest != null) {
			//TODO Needs to be changed to use the new RestClient.get().

			// the dest here refers to router-id
			//bgpdRestIp includes port number, such as 1.1.1.1:8080
			String BGPdRestIp = bgpRoute.getBGPdRestIp();
			String url="http://"+BGPdRestIp+"/wm/bgp/"+dest;

			//Doesn't actually do anything with the response
			RestClient.get(url); 
			
			output="Get rib from bgpd finished!\n";
			return output;
		} 
		else {
			IPatriciaTrie<RibEntry> ptree = bgpRoute.getPtree();
			output += "{\n  \"rib\": [\n";
			boolean printed = false;
			
			synchronized(ptree) {
				Iterator<IPatriciaTrie.Entry<RibEntry>> it = ptree.iterator();
				while (it.hasNext()) {
					IPatriciaTrie.Entry<RibEntry> entry = it.next();
					
					if (printed == true) {
						output += ",\n";
					}
					
					output += "    {\"prefix\": \"" + entry.getPrefix() +"\", ";
					output += "\"nexthop\": \"" + entry.getValue().getNextHop().getHostAddress() +"\"}";
					
					printed = true;
				}
			}
			
			output += "\n  ]\n}\n";
		}
		
		return output;
	}

	@Post
	public String store(String fmJson) {
		IBgpRouteService bgpRoute = (IBgpRouteService) getContext().getAttributes().
				get(IBgpRouteService.class.getCanonicalName());

		String routerId = (String) getRequestAttributes().get("routerid");
		String prefix = (String) getRequestAttributes().get("prefix");
		String mask = (String) getRequestAttributes().get("mask");
		String nexthop = (String) getRequestAttributes().get("nexthop");
		String capability = (String) getRequestAttributes().get("capability");

		String reply = "";

		if (capability == null) {
			// this is a prefix add
			Prefix p;
			try {
				p = new Prefix(prefix, Integer.valueOf(mask));
			} catch (NumberFormatException e) {
				reply = "[POST: mask format is wrong]";
				log.info(reply);
				return reply + "\n";				
			} catch (IllegalArgumentException e1) {
				reply = "[POST: prefix format is wrong]";
				log.info(reply);
				return reply + "\n";
			}
			
			RibEntry rib = new RibEntry(routerId, nexthop);

			bgpRoute.newRibUpdate(new RibUpdate(Operation.UPDATE, p, rib));
			
			reply = "[POST: " + prefix + "/" + mask + ":" + nexthop + "]";
			log.info(reply);
		}
		else if(capability.equals("1")) {
			reply = "[POST-capability: " + capability + "]\n";
			log.info(reply);
			// to store the number in the top node of the Ptree	
		}
		else {			
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

		String routerId = (String) getRequestAttributes().get("routerid");
		String prefix = (String) getRequestAttributes().get("prefix");
		String mask = (String) getRequestAttributes().get("mask");
		String nextHop = (String) getRequestAttributes().get("nexthop");
		String capability = (String) getRequestAttributes().get("capability");

		String reply = "";

		if (capability == null) {
			// this is a prefix delete
			Prefix p;
			try {
				p = new Prefix(prefix, Integer.valueOf(mask));
			} catch (NumberFormatException e) {
				reply = "[DELE: mask format is wrong]";
				log.info(reply);
				return reply + "\n";
			} catch (IllegalArgumentException e1) {
				reply = "[DELE: prefix format is wrong]";
				log.info(reply);
				return reply + "\n";
			}
			
			RibEntry r = new RibEntry(routerId, nextHop);
			
			bgpRoute.newRibUpdate(new RibUpdate(Operation.DELETE, p, r));
			
			reply =reply + "[DELE: " + prefix + "/" + mask + ":" + nextHop + "]";
		}
		else {
			// clear the local rib: Ptree			
			bgpRoute.clearPtree();
			reply = "[DELE-capability: " + capability + "; The local RibEntry is cleared!]\n";

			// to store the number in the top node of the Ptree	
		}
		
		log.info(reply);
		return reply + "\n";
	}
}
