package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowId;

import org.openflow.util.HexString;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteFlowResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(DeleteFlowResource.class);

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
	String flowIdStr = (String) getRequestAttributes().get("flow-id");
	FlowId flowId = new FlowId(flowIdStr);
	log.debug("Delete Flow Id: " + flowIdStr);

	// Process the request
	result = flowService.deleteFlow(flowId);
	return result;
    }
}
