package net.onrc.onos.ofcontroller.flowmanager.web;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasurementInstallPathsFlowResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(MeasurementInstallPathsFlowResource.class);

    @Get("json")
    public Boolean retrieve() {
	Boolean result = false;

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
	    return result;
	}

	// Extract the arguments
	String numThreadsStr = (String) getRequestAttributes().get("num-threads");
	Integer numThreads = new Integer(numThreadsStr);
	log.debug("Measurement Install Paths Number of Threads " + numThreadsStr);

	// Process the request
	result = flowService.measurementInstallPaths(numThreads);
	return result;
    }
}
