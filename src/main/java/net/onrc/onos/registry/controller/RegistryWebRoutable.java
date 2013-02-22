package net.onrc.onos.registry.controller;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class RegistryWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/json", RegistryRouteResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/registry";
	}

}
