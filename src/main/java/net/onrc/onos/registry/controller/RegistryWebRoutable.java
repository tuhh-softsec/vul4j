package net.onrc.onos.registry.controller;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RegistryWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/controllers/json", ControllerRegistryResource.class);
		router.attach("/switches/json", SwitchRegistryResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/registry";
	}

}
