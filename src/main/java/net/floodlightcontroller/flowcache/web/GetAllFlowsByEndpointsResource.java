package net.floodlightcontroller.flowcache.web;

import java.util.ArrayList;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAllFlowsByEndpointsResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(GetAllFlowsByEndpointsResource.class);

    @Get("json")
    public ArrayList<FlowPath> retrieve() {
	ArrayList<FlowPath> result = new ArrayList<FlowPath>();

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
            return result;
	}

	// Extract the arguments
	String dataPathEndpointsStr = (String) getRequestAttributes().get("data-path-endpoints");
	log.debug("Get All Flows Endpoints: " + dataPathEndpointsStr);

	// TODO: Implement it.

        return result;
    }
}
