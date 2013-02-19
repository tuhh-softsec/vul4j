package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.flowcache.IFlowService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddFlowResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(AddFlowResource.class);

    @Get("json")
    public Boolean retrieve() {
	Boolean result = false;

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
            return result;
	}

	// Extract the arguments
	String flowPathStr = (String) getRequestAttributes().get("flow");
	log.debug("Add Flow Path: " + flowPathStr);

	// TODO: Implement it.
	result = true;

        return result;
    }
}
