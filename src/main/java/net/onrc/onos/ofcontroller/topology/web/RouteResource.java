package net.onrc.onos.ofcontroller.topology.web;

import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.onrc.onos.ofcontroller.routing.TopoRouteService;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(RouteResource.class);

    @Get("json")
    public DataPath retrieve() {
        ITopoRouteService topoRouteService = new TopoRouteService("");
	if (topoRouteService == null) {
	    log.debug("Topology Route Service not found");
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
	    topoRouteService.getShortestPath(new SwitchPort(srcDpid, srcPort),
					     new SwitchPort(dstDpid, dstPort));
	if (result != null) {
	    return result;
	} else {
            log.debug("ERROR! no route found");
            return null;
        }
    }
}
