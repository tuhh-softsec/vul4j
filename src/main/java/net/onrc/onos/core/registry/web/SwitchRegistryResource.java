package net.onrc.onos.core.registry.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.onrc.onos.core.registry.ControllerRegistryEntry;
import net.onrc.onos.core.registry.IControllerRegistryService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SwitchRegistryResource extends ServerResource {
	
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
		
		return switches;
	}
}
