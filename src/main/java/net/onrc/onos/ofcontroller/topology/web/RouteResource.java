package net.onrc.onos.ofcontroller.topology.web;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.topology.ITopologyNetService;
import net.onrc.onos.ofcontroller.topology.TopologyManager;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteResource extends ServerResource {

    protected final static Logger log = LoggerFactory.getLogger(RouteResource.class);

    @Get("json")
    public DataPath retrieve() {
	// Get the services that are needed for the computation
	ITopologyNetService topologyNetService =
	    (ITopologyNetService)getContext().getAttributes().
	    get(ITopologyNetService.class.getCanonicalName());
	IFlowService flowService =
	    (IFlowService)getContext().getAttributes().
	    get(IFlowService.class.getCanonicalName());

	if (topologyNetService == null) {
	    log.debug("Topology Net Service not found");
	    return null;
	}
	if (flowService == null) {
	    log.debug("Flow Service not found");
	    return null;
	}
        
        String srcDpidStr = (String) getRequestAttributes().get("src-dpid");
        String srcPortStr = (String) getRequestAttributes().get("src-port");
        String dstDpidStr = (String) getRequestAttributes().get("dst-dpid");
        String dstPortStr = (String) getRequestAttributes().get("dst-port");

        log.debug( srcDpidStr + "--" + srcPortStr + "--" + dstDpidStr + "--" + dstPortStr);

	Dpid srcDpid = new Dpid(srcDpidStr);
	Port srcPort = new Port(Short.parseShort(srcPortStr));
	Dpid dstDpid = new Dpid(dstDpidStr);
	Port dstPort = new Port(Short.parseShort(dstPortStr));
        
	DataPath result =
	    topologyNetService.getTopologyShortestPath(
		flowService.getTopology(),
		new SwitchPort(srcDpid, srcPort),
		new SwitchPort(dstDpid, dstPort));
	if (result != null) {
	    return result;
	} else {
            log.debug("ERROR! no route found");
            return null;
        }
    }
}
