package net.onrc.onos.ofcontroller.proxyarp;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ArpWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/cache/json", ArpCacheResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/arp";
	}
}
