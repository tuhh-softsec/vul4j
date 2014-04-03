package net.onrc.onos.core.registry.web;

import java.util.ArrayList;
import java.util.Collection;

import net.onrc.onos.core.registry.IControllerRegistryService;
import net.onrc.onos.core.registry.RegistryException;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerRegistryResource extends ServerResource {

	protected final static Logger log = LoggerFactory.getLogger(ControllerRegistryResource.class);

	@Get("json")
	public Collection<String> getControllers() {
		IControllerRegistryService registry = 
				(IControllerRegistryService) getContext().getAttributes().
				get(IControllerRegistryService.class.getCanonicalName());
		
		Collection<String> controllers = null;
		try {
			controllers = registry.getAllControllers();
		} catch (RegistryException e) {
			log.warn("Error retrieving controller list: {}", e.getMessage());
		}
		
		if (controllers == null){
			controllers = new ArrayList<String>();
		}
		
		return controllers;
	}
	
}
