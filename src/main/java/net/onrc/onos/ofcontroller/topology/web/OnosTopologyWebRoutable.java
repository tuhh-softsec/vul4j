package net.onrc.onos.ofcontroller.topology.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;
import net.onrc.onos.graph.web.TopoDevicesResource;
import net.onrc.onos.ofcontroller.core.web.TopoLinksResource;
import net.onrc.onos.ofcontroller.core.web.TopoSwitchesResource;

public class OnosTopologyWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/route/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json", RouteResource.class);
        router.attach("/switches/{filter}/json", TopoSwitchesResource.class);
        router.attach("/links/json", TopoLinksResource.class);
        router.attach("/devices/json", TopoDevicesResource.class);
		return router;
	}

	@Override
	public String basePath() {
        return "/wm/onos/topology";
	}

}
