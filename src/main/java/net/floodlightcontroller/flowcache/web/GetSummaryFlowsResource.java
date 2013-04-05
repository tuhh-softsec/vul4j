package net.floodlightcontroller.flowcache.web;

import java.util.ArrayList;

import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSummaryFlowsResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(GetSummaryFlowsResource.class);

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
