package net.onrc.onos.ofcontroller.flowmanager.web;

import java.util.ArrayList;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.DataPathEndpoints;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow Manager REST API implementation: Get all Flow state for given
 * source and destination switches and ports.
 *
 * The "{src-dpid}" request attribute value is the source DPID of the flows to
 * get.
 * The "{src-port}" request attribute value is the source port of the flows to
 * get.
 * The "{dst-dpid}" request attribute value is the destination DPID of the
 * flows to get.
 * The "{dst-port}" request attribute value is the destination port of the
 * flows to get.
 *
 *   GET /wm/flow/getall-by-endpoints/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json"
 */
public class GetAllFlowsByEndpointsResource extends ServerResource {
    protected final static Logger log = LoggerFactory.getLogger(GetAllFlowsByEndpointsResource.class);

    /**
     * Implement the API.
     *
     * @return the collection of Flow states if any found, otherwise null.
     */
    @Get("json")
    public ArrayList<FlowPath> retrieve() {
	ArrayList<FlowPath> result = null;

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

	Dpid srcDpid = new Dpid(srcDpidStr);
	Port srcPort = new Port(Short.parseShort(srcPortStr));
	Dpid dstDpid = new Dpid(dstDpidStr);
	Port dstPort = new Port(Short.parseShort(dstPortStr));
	SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
	SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);
	DataPathEndpoints dataPathEndpoints =
	    new DataPathEndpoints(srcSwitchPort, dstSwitchPort);

	result = flowService.getAllFlows(dataPathEndpoints);

        return result;
    }
}
