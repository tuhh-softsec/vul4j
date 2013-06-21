package net.onrc.onos.ofcontroller.flowmanager.web;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;

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

	// Process the request
	if (flowIdStr.equals("all")) {
	    log.debug("Delete All Flows");
	    result = flowService.deleteAllFlows();
	} else {
	    FlowId flowId = new FlowId(flowIdStr);
	    log.debug("Delete Flow Id: " + flowIdStr);
	    result = flowService.deleteFlow(flowId);
	}
	return result;
    }
}
