package net.onrc.onos.ofcontroller.proxyarp;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * Routing class for ARP module REST URLs.
 */
public class ArpWebRoutable implements RestletRoutable {

    /**
     * Get a router configured with ARP module REST URLs.
     *
     * @param context the restlet context to build a router with
     * @return the router
     */
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/cache/json", ArpCacheResource.class);
        return router;
    }

    /**
     * Get the base path of the ARP module URLs.
     *
     * @return the string base path
     */
    @Override
    public String basePath() {
        return "/wm/arp";
    }
}
