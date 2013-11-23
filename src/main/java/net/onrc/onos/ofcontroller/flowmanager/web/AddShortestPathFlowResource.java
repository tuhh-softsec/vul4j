package net.onrc.onos.ofcontroller.flowmanager.web;

import java.io.IOException;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager REST API implementation: Add a Flow by delegating
 * the Shortest Path computation to ONOS:
 *
 *   POST /wm/flow/add-shortest-path/json
 */
public class AddShortestPathFlowResource extends ServerResource {

    protected final static Logger log = LoggerFactory.getLogger(AddShortestPathFlowResource.class);

    /**
     * Implement the API.
     *
     * @param flowJson a string with the JSON representation of the Flow to
     * add.
     * @return the Flow ID of the added flow.
     */
    @Post("json")
    public FlowId store(String flowJson) {
	FlowId result = new FlowId();

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
            return result;
	}

	//
	// Extract the arguments
	// NOTE: The "flow" is specified in JSON format.
	//
	ObjectMapper mapper = new ObjectMapper();
	String flowPathStr = flowJson;
	FlowPath flowPath = null;
	log.debug("Add Shortest Path Flow Path: " + flowPathStr);
	try {
	    flowPath = mapper.readValue(flowPathStr, FlowPath.class);
	} catch (JsonGenerationException e) {
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Process the request
	if (flowPath != null) {
	    FlowPath addedFlowPath =
		flowService.addAndMaintainShortestPathFlow(flowPath);
	    if (addedFlowPath == null) {
		result = new FlowId();		// Error: Return empty Flow Id
	    } else {
		result = addedFlowPath.flowId();
	    }
	}

        return result;
    }
}
