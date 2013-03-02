package net.floodlightcontroller.flowcache.web;

import java.io.IOException;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddFlowResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(AddFlowResource.class);

    @Get("json")
    public FlowId retrieve() {
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
	String flowPathStr = (String) getRequestAttributes().get("flow");
	// TODO: Remove it later
	// String flowPathStr = "{\"flowId\":{\"value\":\"5\"},\"installerId\":{\"value\":\"FOOBAR\"},\"dataPath\":{\"srcPort\":{\"dpid\":{\"value\":\"00:00:00:00:00:00:00:01\"},\"port\":{\"value\":0}},\"dstPort\":{\"dpid\":{\"value\":\"00:00:00:00:00:00:00:02\"},\"port\":{\"value\":0}},\"flowEntries\":[{\"flowEntryId\":null,\"flowEntryMatch\":null,\"flowEntryActions\":null,\"dpid\":{\"value\":\"00:00:00:00:00:00:00:01\"},\"inPort\":{\"value\":0},\"outPort\":{\"value\":1},\"flowEntryUserState\":\"FE_USER_UNKNOWN\",\"flowEntrySwitchState\":\"FE_SWITCH_UNKNOWN\",\"flowEntryErrorState\":null},{\"flowEntryId\":null,\"flowEntryMatch\":null,\"flowEntryActions\":null,\"dpid\":{\"value\":\"00:00:00:00:00:00:00:02\"},\"inPort\":{\"value\":9},\"outPort\":{\"value\":0},\"flowEntryUserState\":\"FE_USER_UNKNOWN\",\"flowEntrySwitchState\":\"FE_SWITCH_UNKNOWN\",\"flowEntryErrorState\":null}]}}";

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
