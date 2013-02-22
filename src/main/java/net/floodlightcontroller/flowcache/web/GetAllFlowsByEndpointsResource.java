package net.floodlightcontroller.flowcache.web;

import java.util.ArrayList;

import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.util.DataPathEndpoints;
import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.FlowPath;
import net.floodlightcontroller.util.Port;
import net.floodlightcontroller.util.SwitchPort;

import org.openflow.util.HexString;
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
        String srcDpidStr = (String) getRequestAttributes().get("src-dpid");
        String srcPortStr = (String) getRequestAttributes().get("src-port");
        String dstDpidStr = (String) getRequestAttributes().get("dst-dpid");
        String dstPortStr = (String) getRequestAttributes().get("dst-port");

	log.debug("Get All Flows Endpoints: " + srcDpidStr + "--" +
		  srcPortStr + "--" + dstDpidStr + "--" + dstPortStr);

	Dpid srcDpid = new Dpid(HexString.toLong(srcDpidStr));
	Port srcPort = new Port(Short.parseShort(srcPortStr));
	Dpid dstDpid = new Dpid(HexString.toLong(dstDpidStr));
	Port dstPort = new Port(Short.parseShort(dstPortStr));
	SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
	SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);
	DataPathEndpoints dataPathEndpoints =
	    new DataPathEndpoints(srcSwitchPort, dstSwitchPort);

	flowService.getAllFlows(dataPathEndpoints, result);

        return result;
    }
}
