flowIdpackage net.floodlightcontroller.flowcache.web;

import java.io.IOException;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddFlowResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(AddFlowResource.class);

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
	log.debug("Add Flow Path: " + flowPathStr);
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
	    if (flowService.addFlow(flowPath, result) != true) {
		result = new FlowId();		// Error: Return empty Flow Id
	    }
	}

        return result;
    }
}
