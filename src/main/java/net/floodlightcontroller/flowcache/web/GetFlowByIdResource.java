package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowPath;

import org.openflow.util.HexString;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetFlowByIdResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(GetFlowByIdResource.class);

    @Get("json")
    public FlowPath retrieve() {
	FlowPath result = null;

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
            return result;
	}

	// Extract the arguments
	String flowIdStr = (String) getRequestAttributes().get("flow-id");
	long flowId = HexString.toLong(flowIdStr);
	log.debug("Get Flow Id: " + flowIdStr);

	// TODO: Implement it.

        return result;
    }
}
