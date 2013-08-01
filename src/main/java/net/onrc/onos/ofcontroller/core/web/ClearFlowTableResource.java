package net.onrc.onos.ofcontroller.core.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;

import org.codehaus.jackson.map.ObjectMapper;
import org.openflow.util.HexString;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearFlowTableResource extends ServerResource {
	static Logger log = LoggerFactory.getLogger(ClearFlowTableResource.class);

	@Post("json")
	public List<String> ClearFlowTable(String jsonData){
		IFloodlightProviderService floodlightProvider = 
				(IFloodlightProviderService) getContext().getAttributes()
				.get(IFloodlightProviderService.class.getCanonicalName());
		
		Map<Long, IOFSwitch> switches = floodlightProvider.getSwitches();
		
		List<String> response = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		String[] dpids = null;
		try {
			dpids = mapper.readValue(jsonData, String[].class);
		} catch (IOException e) {
			log.debug("Error parsing switch dpid array: {}", e.getMessage());
			response.add("Error parsing input");
			return response;
		}
		
		
		for (String dpid : dpids){
			IOFSwitch sw = switches.get(HexString.toLong(dpid));
			if (sw != null){
				sw.clearAllFlowMods();
				response.add(dpid + " cleared");
			}
			else {
				response.add(dpid + " not found");
			}
		}
		
		return response;
	}

}
