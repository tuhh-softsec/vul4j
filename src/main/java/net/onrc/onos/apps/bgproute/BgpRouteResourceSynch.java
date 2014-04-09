package net.onrc.onos.apps.bgproute;

import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST handler for sending commands to SDN-IP. This interface is intended for
 * operators or developers to change the route table of BGPd. There are
 * interfaces for both sending new routes and deleting routes. This is
 * not intended to be used during general operation. It is to have a way to
 * influence BGPd's behavior for debugging.
 */
public class BgpRouteResourceSynch extends ServerResource {
    private static final Logger log = LoggerFactory.getLogger(BgpRouteResourceSynch.class);

    /**
     * Handles a REST call to SDN-IP which gives a command to send a new route
     * to BGPd.
     *
     * @return a String describing the result of the operation
     */
    @Post
    public String handlePostMethod() {

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

    /**
     * Handles a REST call to SDN-IP which gives a command to BGPd to delete a
     * route from its route table.
     *
     * @return a String description of the result of the operation
     */
    @Delete
    public String handleDeleteMethod() {
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
