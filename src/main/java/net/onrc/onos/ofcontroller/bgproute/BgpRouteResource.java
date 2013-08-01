package net.onrc.onos.ofcontroller.bgproute;

import java.net.UnknownHostException;

import net.onrc.onos.ofcontroller.bgproute.RibUpdate.Operation;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BgpRouteResource extends ServerResource {

	protected static Logger log = LoggerFactory.getLogger(BgpRouteResource.class);

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

	//@SuppressWarnings("unused")
	@Get
	public String get(String fmJson) {
		String dest = (String) getRequestAttributes().get("dest");
		String output = "";
		IBgpRouteService bgpRoute = (IBgpRouteService)getContext().getAttributes().
				get(IBgpRouteService.class.getCanonicalName());

		if (dest != null) {
			//TODO Needs to be changed to use the new RestClient.get().
			
			
			//Prefix p;
			//try {
			//	p = new Prefix(dest, 32);
			//} catch (UnknownHostException e) {
			//if (p == null) {
			//	return "[GET]: dest address format is wrong";
			//}

			// the dest here refers to router-id
			//bgpdRestIp includes port number, such as 1.1.1.1:8080
			String BGPdRestIp = bgpRoute.getBGPdRestIp();
			String url="http://"+BGPdRestIp+"/wm/bgp/"+dest;

			RestClient.get(url);
			
			output="Get rib from bgpd finished!\n";
			return output;
		} 
		else {
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

	//unused?
	/*
	public static ByteBuffer toByteBuffer(String value) throws UnsupportedEncodingException {
		return ByteBuffer.wrap(value.getBytes("UTF-8"));
	}
	*/

	//unused?
	/*
	public static String toString(ByteBuffer buffer) throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes, "UTF-8");
	}
	*/

	@Post
	public String store(String fmJson) {
		IBgpRouteService bgpRoute = (IBgpRouteService) getContext().getAttributes().
				get(IBgpRouteService.class.getCanonicalName());

		//Ptree ptree = bgpRoute.getPtree();

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
			} catch (UnknownHostException e1) {
				reply = "[POST: prefix format is wrong]";
				log.info(reply);
				return reply + "\n";
			}
			
			Rib rib = new Rib(routerId, nexthop, p.getPrefixLength());

			bgpRoute.newRibUpdate(new RibUpdate(Operation.UPDATE, p, rib));
			
			/*
			PtreeNode node = ptree.acquire(p.getAddress(), p.getPrefixLength());
			
			if (node.rib != null) {
				node.rib = null;
				ptree.delReference(node);
			}
			node.rib = rib;

			bgpRoute.prefixAdded(node);
			*/
			
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

		//Ptree ptree = bgpRoute.getPtree();

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
			} catch (UnknownHostException e1) {
				reply = "[DELE: prefix format is wrong]";
				log.info(reply);
				return reply + "\n";
			}
			
			Rib r = new Rib(routerId, nextHop, p.getPrefixLength());
			
			bgpRoute.newRibUpdate(new RibUpdate(Operation.DELETE, p, r));
			
			/*
			PtreeNode node = ptree.lookup(p.getAddress(), p.getPrefixLength());
			
			//Remove the flows from the switches before the rib is lost
			//Theory: we could get a delete for a prefix not in the Ptree.
			//This would result in a null node being returned. We could get a delete for
			//a node that's not actually there, but is a aggregate node. This would result
			//in a non-null node with a null rib. Only a non-null node with a non-null
			//rib is an actual prefix in the Ptree.
			if (node != null && node.rib != null){
				bgpRoute.prefixDeleted(node);
			}

			

			if (node != null && node.rib != null) {
				if (r.equals(node.rib)) {
					node.rib = null;
					ptree.delReference(node);					
				}
			}
			*/
			
			reply =reply + "[DELE: " + prefix + "/" + mask + ":" + nextHop + "]";
		}
		else {
			// clear the local rib: Ptree			
			bgpRoute.clearPtree();
			reply = "[DELE-capability: " + capability + "; The local Rib is cleared!]\n";

			// to store the number in the top node of the Ptree	
		}
		
		log.info(reply);
		return reply + "\n";
	}
}
