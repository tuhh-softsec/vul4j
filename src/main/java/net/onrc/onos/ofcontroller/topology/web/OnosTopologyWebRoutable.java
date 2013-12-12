package net.onrc.onos.ofcontroller.topology.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;
import net.onrc.onos.ofcontroller.linkdiscovery.web.LinksResource;

public class OnosTopologyWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/links/json", LinksResource.class);
        router.attach("/route/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json", RouteResource.class);
		return router;
	}

	@Override
	public String basePath() {
        return "/wm/topology";
	}

}
