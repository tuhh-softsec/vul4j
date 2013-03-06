package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.CallerId;
import net.floodlightcontroller.util.DataPathEndpoints;
import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.FlowPath;
import net.floodlightcontroller.util.Port;
import net.floodlightcontroller.util.SwitchPort;

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
        String srcDpidStr = (String) getRequestAttributes().get("src-dpid");
        String srcPortStr = (String) getRequestAttributes().get("src-port");
        String dstDpidStr = (String) getRequestAttributes().get("dst-dpid");
        String dstPortStr = (String) getRequestAttributes().get("dst-port");

	log.debug("Get Flow By Installer: " + installerIdStr + " Endpoints: " +
		  srcDpidStr + "--" + srcPortStr + "--" +
		  dstDpidStr + "--" + dstPortStr);

	CallerId installerId = new CallerId(installerIdStr);
	Dpid srcDpid = new Dpid(srcDpidStr);
	Port srcPort = new Port(Short.parseShort(srcPortStr));
	Dpid dstDpid = new Dpid(dstDpidStr);
	Port dstPort = new Port(Short.parseShort(dstPortStr));
	SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
	SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);
	DataPathEndpoints dataPathEndpoints =
	    new DataPathEndpoints(srcSwitchPort, dstSwitchPort);

	result = flowService.getFlow(installerId, dataPathEndpoints);

        return result;
    }
}
