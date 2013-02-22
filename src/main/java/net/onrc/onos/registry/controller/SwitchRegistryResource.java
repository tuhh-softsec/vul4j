package net.onrc.onos.registry.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchRegistryResource extends ServerResource {

	protected static Logger log = LoggerFactory.getLogger(SwitchRegistryResource.class);
	
	@Get("json")
	public Map<String, List<ControllerRegistryEntry>> getAllControllers(){
		IControllerRegistryService registry = 
				(IControllerRegistryService) getContext().getAttributes().
				get(IControllerRegistryService.class.getCanonicalName());
		
		Map<String, List<ControllerRegistryEntry>> switches = null;
		switches = registry.getAllSwitches();
		
		if (switches == null){
			switches = new HashMap<String, List<ControllerRegistryEntry>>();
		}
		
		for (List<ControllerRegistryEntry> list: switches.values()){
			for (ControllerRegistryEntry en : list) {
				log.debug("Controller id {}", en.getControllerId());
			}
		}
		
		return switches;
	}
}
