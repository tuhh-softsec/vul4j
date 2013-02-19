package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetFlowByInstallerIdResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(GetFlowByInstallerIdResource.class);

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
	String installerIdStr = (String) getRequestAttributes().get("installer-id");
	String dataPathEndpointsStr = (String) getRequestAttributes().get("data-path-endpoints");
	log.debug("Get Flow Installer: " + installerIdStr + " Endpoints: " + dataPathEndpointsStr);

	// TODO: Implement it.

        return result;
    }
}
