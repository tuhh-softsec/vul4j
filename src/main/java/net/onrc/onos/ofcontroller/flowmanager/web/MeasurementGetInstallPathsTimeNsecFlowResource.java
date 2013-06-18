package net.onrc.onos.ofcontroller.flowmanager.web;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasurementGetInstallPathsTimeNsecFlowResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(MeasurementGetInstallPathsTimeNsecFlowResource.class);

    @Get("json")
    public Long retrieve() {
	Long result = null;

        IFlowService flowService =
                (IFlowService)getContext().getAttributes().
                get(IFlowService.class.getCanonicalName());

        if (flowService == null) {
	    log.debug("ONOS Flow Service not found");
	    return result;
	}

	// Extract the arguments

	// Process the request
	result = flowService.measurementGetInstallPathsTimeNsec();

	log.debug("Measurement Get Install Paths Time (nsec): " + result);

	return result;
    }
}
