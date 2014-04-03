package net.floodlightcontroller.core.web;

import java.util.Map;

import net.floodlightcontroller.core.module.ModuleLoaderResource;

import org.restlet.resource.Get;

public class LoadedModuleLoaderResource extends ModuleLoaderResource {
	/**
	 * Retrieves information about all modules available
	 * to Floodlight.
	 * @return Information about all modules available.
	 */
    @Get("json")
    public Map<String, Object> retrieve() {
    	return retrieveInternal(true);
    }
}
