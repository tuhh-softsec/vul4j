package net.onrc.onos.ofcontroller.flowmanager.web;

import java.util.ArrayList;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;

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
