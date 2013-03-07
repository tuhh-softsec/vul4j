package net.floodlightcontroller.bgproute;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class BgpRouteWebRoutable implements RestletRoutable {
	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/json", BgpRouteResource.class);
		router.attach("/rib/{dest}", BgpRouteResource.class);
		router.attach("/{routerid}/{prefix}/{mask}/{nexthop}", BgpRouteResource.class);
		router.attach("/{routerid}/{capability}", BgpRouteResource.class);
		return router;
	}
	
	@Override
	public String basePath() {
		return "/wm/bgp";
	}
}
