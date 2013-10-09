package net.onrc.onos.ofcontroller.flowmanager.web;

import java.util.ArrayList;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager REST API implementation: Get all Flow state.
 *
 *   GET /wm/flow/getall/json"
 */
public class GetAllFlowsResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(GetAllFlowsResource.class);

    /**
     * Implement the API.
     *
     * @return the collection of Flow states if any found, otherwise null.
     */
    @Get("json")
    public ArrayList<FlowPath> retrieve() {
	ArrayList<FlowPath> result = null;

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
            return result;
	}

	// Extract the arguments
	log.debug("Get All Flows Endpoints");

	result = flowService.getAllFlows();

        return result;
    }
}
