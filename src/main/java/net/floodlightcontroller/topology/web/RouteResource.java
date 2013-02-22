package net.floodlightcontroller.topology.web;

import java.util.List;

import net.floodlightcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

import org.openflow.util.HexString;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(RouteResource.class);

    @Get("json")
    public List<NodePortTuple> retrieve() {
        ITopoRouteService topoRouteService = 
                (ITopoRouteService)getContext().getAttributes().
                    get(ITopoRouteService.class.getCanonicalName());
	if (topoRouteService == null) {
	    log.debug("Topology Route Service not found");
	    return null;
	}
        
        String srcDpid = (String) getRequestAttributes().get("src-dpid");
        String srcPort = (String) getRequestAttributes().get("src-port");
        String dstDpid = (String) getRequestAttributes().get("dst-dpid");
        String dstPort = (String) getRequestAttributes().get("dst-port");

        log.debug( srcDpid + "--" + srcPort + "--" + dstDpid + "--" + dstPort);

        long longSrcDpid = HexString.toLong(srcDpid);
        short shortSrcPort = Short.parseShort(srcPort);
        long longDstDpid = HexString.toLong(dstDpid);
        short shortDstPort = Short.parseShort(dstPort);
        
	List<NodePortTuple> result =
	    topoRouteService.getShortestPath(new NodePortTuple(longSrcDpid, shortSrcPort),
					     new NodePortTuple(longDstDpid, shortDstPort));
	if ((result != null) && (result.size() > 0)) {
	    return result;
	} else {
            log.debug("ERROR! no route found");
            return null;
        }
    }
}
