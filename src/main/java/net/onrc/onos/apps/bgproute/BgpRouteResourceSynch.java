package net.onrc.onos.apps.bgproute;

import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BgpRouteResourceSynch extends ServerResource {
    private final static Logger log = LoggerFactory.getLogger(BgpRouteResourceSynch.class);

    @Post
    public String store(String fmJson) {

        IBgpRouteService bgpRoute = (IBgpRouteService) getContext().getAttributes().
                get(IBgpRouteService.class.getCanonicalName());

        String routerId = (String) getRequestAttributes().get("routerid");
        String prefix = (String) getRequestAttributes().get("prefix");
        String mask = (String) getRequestAttributes().get("mask");
        String nexthop = (String) getRequestAttributes().get("nexthop");

        String bgpdRestIp = bgpRoute.getBGPdRestIp();

        // bgpdRestIp includes port number, e.g. 1.1.1.1:8080
        RestClient.post("http://" + bgpdRestIp + "/wm/bgp/" + routerId + "/" + prefix + "/"
                + mask + "/" + nexthop);

        String reply = "";
        reply = "[POST: " + prefix + "/" + mask + ":" + nexthop + "/synch]";
        log.info(reply);

        return reply + "\n";

    }

    @Delete
    public String delete(String fmJson) {
        IBgpRouteService bgpRoute = (IBgpRouteService) getContext().getAttributes().
                get(IBgpRouteService.class.getCanonicalName());

        String routerId = (String) getRequestAttributes().get("routerid");
        String prefix = (String) getRequestAttributes().get("prefix");
        String mask = (String) getRequestAttributes().get("mask");
        String nextHop = (String) getRequestAttributes().get("nexthop");

        StringBuilder reply = new StringBuilder();

        String bgpdRestIp = bgpRoute.getBGPdRestIp();

        RestClient.delete("http://" + bgpdRestIp + "/wm/bgp/" + routerId + "/" + prefix + "/"
                + mask + "/" + nextHop);

        reply.append("[DELE: ")
             .append(prefix)
             .append('/')
             .append(mask)
             .append(':')
             .append(nextHop)
             .append("/synch]");

        log.info(reply.toString());

        return reply.append("\n").toString();
    }
}
