package net.onrc.onos.ofcontroller.networkgraph.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class NetworkGraphWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/switches/json", RamcloudSwitchesResource.class);
		router.attach("/links/json", RamcloudLinksResource.class);
		return router;
	}

	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/wm/onos/rc";
	}

}
