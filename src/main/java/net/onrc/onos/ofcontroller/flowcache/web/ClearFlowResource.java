package net.onrc.onos.ofcontroller.flowcache.web;

import net.floodlightcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.flowcache.IFlowService;

import org.openflow.util.HexString;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearFlowResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(ClearFlowResource.class);

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

	// Process the request
	if (flowIdStr.equals("all")) {
	    log.debug("Clear All Flows");
	    result = flowService.clearAllFlows();
	} else {
	    FlowId flowId = new FlowId(flowIdStr);
	    log.debug("Clear Flow Id: " + flowIdStr);
	    result = flowService.clearFlow(flowId);
	}
	return result;
    }
}
