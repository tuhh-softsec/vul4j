package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddFlowResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(AddFlowResource.class);

    @Get("json")
    public FlowId retrieve() {
	FlowId result = new FlowId();

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
            return result;
	}

	// Extract the arguments
	String flowPathStr = (String) getRequestAttributes().get("flow");
	FlowPath flowPath = new FlowPath(flowPathStr);

	log.debug("Add Flow Path: " + flowPathStr);

	if (flowService.addFlow(flowPath, result) != true) {
	    result = new FlowId();	// Error: Empty Flow Id
	}

        return result;
    }
}
