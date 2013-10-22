package net.onrc.onos.ofcontroller.flowmanager.web;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager REST API implementation: Clear internal Flow state.
 *
 * The "{flow-id}" request attribute value can be either a specific Flow ID,
 * or the keyword "all" to clear all Flows:
 *
 *   GET /wm/flow/clear/{flow-id}/json
 */
public class ClearFlowResource extends ServerResource {
    protected final static Logger log = LoggerFactory.getLogger(ClearFlowResource.class);

    /**
     * Implement the API.
     *
     * @return true on success, otehrwise false.
     */
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
