package net.onrc.onos.apps.bgproute;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * REST URL router for SDN-IP REST calls.
 */
public class BgpRouteWebRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/json", BgpRouteResource.class);
        router.attach("/rib/{dest}", BgpRouteResource.class);
        router.attach("/{sysuptime}/{sequence}/{routerid}/{prefix}/{mask}/{nexthop}", BgpRouteResource.class);
        router.attach("/{routerid}/{prefix}/{mask}/{nexthop}/synch", BgpRouteResourceSynch.class);
        router.attach("/{routerid}/{capability}", BgpRouteResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/wm/bgp";
    }
}
