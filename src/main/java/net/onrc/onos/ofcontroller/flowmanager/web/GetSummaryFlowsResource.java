package net.onrc.onos.ofcontroller.flowmanager.web;

import java.util.ArrayList;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @short Flow Manager REST API implementation: Get summary of all installed
 * flows by all installers in a given range.
 *
 * The "{flow-id}" request attribute value is the Flow ID of the flow in the
 * flow range to get.
 * The "{max-flows}" request attribute value is the maximum number of flows
 * to be returned.
 *
 *   GET /wm/flow/getsummary/{flow-id}/{max-flows}/json"
 */
public class GetSummaryFlowsResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(GetSummaryFlowsResource.class);

    /**
     * Implement the API.
     *
     * @return the collection of Flow states if any found, otherwise null.
     */
    @Get("json")
    public ArrayList<IFlowPath> retrieve() {
    	ArrayList<IFlowPath> result = null;
    	
    	FlowId flowId;
    	int maxFlows = 0;
    	
    	IFlowService flowService = (IFlowService)getContext().getAttributes().get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
        	log.debug("ONOS Flow Service not found");
        	return result;
        }

        // Extract the arguments
    	String flowIdStr = (String) getRequestAttributes().get("flow-id");
    	String maxFlowStr = (String) getRequestAttributes().get("max-flows");
    	log.debug("Get Summary Flows starting flow-id: " + flowIdStr + " max-flows: " + maxFlowStr);
    	
    	flowId = new FlowId(flowIdStr);
    	maxFlows = Integer.parseInt(maxFlowStr);
    	if (maxFlows < 0) maxFlows = 0;

        result = flowService.getAllFlowsSummary(flowId, maxFlows);

        return result;
    }
}
