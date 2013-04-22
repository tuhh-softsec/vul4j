package net.floodlightcontroller.bgproute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.IFloodlightProviderService;

import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.restclient.RestClient;

import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BgpRoute implements IFloodlightModule, IBgpRouteService, ITopologyListener {
	
	protected static Logger log = LoggerFactory.getLogger(BgpRoute.class);

	protected IFloodlightProviderService floodlightProvider;
	protected ITopologyService topology;
	
	protected static Ptree ptree;
	protected static String BGPdRestIp;
	protected static String RouterId;
	
	
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IBgpRouteService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IBgpRouteService.class, this);
		return m;
	}

	protected IRestApiService restApi;
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(ITopologyService.class);
		l.add(IBgpRouteService.class);
		return l;
	}
	
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
	    
	    ptree = new Ptree(32);
		
		// Register floodlight provider and REST handler.
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		topology = context.getServiceImpl(ITopologyService.class);
		
		// Test.
		//test();
		  
	}

	public Ptree getPtree() {
		return ptree;
	}
	public void  clearPtree() {
		ptree = null;
		ptree = new Ptree(32);
		
	}
	public String getBGPdRestIp() {
		return BGPdRestIp;
	}
	public String getRouterId() {
		return RouterId;
	}
	
	// Return nexthop address as byte array.
	public Rib lookupRib(byte[] dest) {
		if (ptree == null) {
		    log.debug("lookupRib: ptree null");
		    return null;
		}
		
		PtreeNode node = ptree.match(dest, 32);
		if (node == null) {
            log.debug("lookupRib: ptree node null");
			return null;
		}
		if (node.rib == null) {
            log.debug("lookupRib: ptree rib null");
			return null;
		}
		ptree.delReference(node);
		
		return node.rib;
	}
	
	@SuppressWarnings("unused")
    private void test() {
		System.out.println("Here it is");
		Prefix p = new Prefix("128.0.0.0", 8);
		Prefix q = new Prefix("8.0.0.0", 8);
		Prefix r = new Prefix("10.0.0.0", 24);
		Prefix a = new Prefix("10.0.0.1", 32);
	
		ptree.acquire(p.getAddress(), p.masklen);
		ptree.acquire(q.getAddress(), q.masklen);
		ptree.acquire(r.getAddress(), r.masklen);
	
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
	
		PtreeNode n = ptree.match(a.getAddress(), a.masklen);
		if (n != null) {
			System.out.println("Matched prefix for 10.0.0.1:");
			Prefix x = new Prefix(n.key, n.keyBits);
			ptree.delReference(n);
		}
		
		n = ptree.lookup(p.getAddress(), p.masklen);
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
		
		n = ptree.lookup(q.getAddress(), q.masklen);
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}
		
		n = ptree.lookup(r.getAddress(), r.masklen);
		if (n != null) {
			ptree.delReference(n);
			ptree.delReference(n);
		}
		System.out.println("Traverse start");
		for (PtreeNode node = ptree.begin(); node != null; node = ptree.next(node)) {
			Prefix p_result = new Prefix(node.key, node.keyBits);
		}

	}
	
	@Override
	public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new BgpRouteWebRoutable());
		topology.addListener((ITopologyListener) this);
		
		 // get the BGPdRestIp and RouterId from transit-route-pusher.py
  		File file = new File("/home/ubuntu/sdn/transit-route-pusher.py");  
       
        
    try{  
        BufferedReader input = new BufferedReader (new FileReader(file));  
        String text; 
        int is_BGPdRestIp=0;
        int is_RouterId=0;
        								                         
        while((text = input.readLine()) != null && (is_BGPdRestIp == 0) || (is_RouterId == 0) ){  
        					  
        		if(is_BGPdRestIp == 1 && is_RouterId ==1)
        		{break;}
        		
        				if(is_BGPdRestIp == 0 && text.contains("BGPdRestIp") ){
		        				String[] temp =	text.split("\"");
		        				BGPdRestIp = temp[1];
		        				is_BGPdRestIp = 1;
		        				
        				
            	}else if (is_RouterId == 0 && text.contains("RouterId") ){
												
		            	String[] temp =	text.split("\"");
		    							RouterId = temp[1];
		    							is_RouterId = 1;
		    							
    							
									}
        						
        					}
    
                   
    }  catch(Exception e){  
       e.printStackTrace();
           }  
      
		        
			// automatically get the rib from bgpd at the ONOS initiation process.
    String dest=RouterId;
    String str="http://"+BGPdRestIp+"/wm/bgp/"+dest;
	 		
	          
	try {
	       	 
				URL url = new URL(str);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
		 
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
	
			 BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()))); 
			 StringBuffer res = new StringBuffer();
			 String line;
			 while ((line = br.readLine()) != null) {
				 	res.append(line);
			 	}	   
			 
			 String res2=res.toString().replaceAll("\"", "'");
			 JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(res2);  
			 JSONArray rib_json_array = jsonObj.getJSONArray("rib");
			 String router_id = jsonObj.getString("router-id");
			       
			 int size = rib_json_array.size();
			 System.out.print("size:"+size+"\n");
			 for (int j = 0; j < size; j++) {
	        JSONObject second_json_object = rib_json_array.getJSONObject(j);
	        String prefix = second_json_object.getString("prefix");
	        String nexthop = second_json_object.getString("nexthop");
	        
	        //insert each rib entry into the local rib;
	        String[] substring= prefix.split("/");
	        String prefix1=substring[0];
	        String mask1=substring[1];
	        			
						Prefix p = new Prefix(prefix1, Integer.valueOf(mask1));
						PtreeNode node = ptree.acquire(p.getAddress(), p.masklen);
						Rib rib = new Rib(router_id, nexthop, p.masklen);
			
						if (node.rib != null) {
							node.rib = null;
							ptree.delReference(node);
						}
						node.rib = rib;
      
			 }  
			 br.close();
			 conn.disconnect();

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
	

	}

	@Override
	public void topologyChanged() {
		boolean change = false;
		String changelog = "";
		
		for (LDUpdate ldu : topology.getLastLinkUpdates()) {
			if (ldu.getOperation().equals(ILinkDiscovery.UpdateOperation.PORT_DOWN)) {
				change = true;
				changelog = changelog + " down ";
			} else if (ldu.getOperation().equals(ILinkDiscovery.UpdateOperation.PORT_UP)) {
				change = true;
				changelog = changelog + " up ";
			}
		}
		log.info ("received topo change" + changelog);

		if (change) {
			RestClient.get ("http://localhost:5000/topo_change");
		}
	}
}
