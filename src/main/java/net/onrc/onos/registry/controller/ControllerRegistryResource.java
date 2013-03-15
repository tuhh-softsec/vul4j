package net.onrc.onos.registry.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerRegistryResource extends ServerResource {

	protected static Logger log = LoggerFactory.getLogger(ControllerRegistryResource.class);

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
			Response response = getResponse();
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			Representation error = new StringRepresentation("Null data returned. Zookeeper connection may be down");
			response.setEntity(error);
			return null;
		}

		return controllers;
	}
	
}
