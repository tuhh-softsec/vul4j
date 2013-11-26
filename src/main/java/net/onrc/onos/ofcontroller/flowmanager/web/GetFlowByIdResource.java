package net.onrc.onos.ofcontroller.flowmanager.web;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager REST API implementation: Get a single Flow state.
 *
 * The "{flow-id}" request attribute value is the Flow ID of the flow to get:
 *
 *   GET /wm/flow/get/{flow-id}/json
 */
public class GetFlowByIdResource extends ServerResource {
    protected final static Logger log = LoggerFactory.getLogger(GetFlowByIdResource.class);

    /**
     * Implement the API.
     *
     * @return the Flow state if the flow is found, otherwise null.
     */
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
	FlowId flowId = new FlowId(flowIdStr);

	log.debug("Get Flow Id: " + flowIdStr);

	result = flowService.getFlow(flowId);

        return result;
    }
}
