package net.onrc.onos.ofcontroller.topology.web;

import net.floodlightcontroller.restserver.RestletRoutable;
import net.onrc.onos.graph.web.TopoDevicesResource;
import net.onrc.onos.ofcontroller.networkgraph.web.NetworkGraphLinksResource;
import net.onrc.onos.ofcontroller.networkgraph.web.NetworkGraphSwitchesResource;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class OnosTopologyWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/route/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json", RouteResource.class);
        router.attach("/switches/json", NetworkGraphSwitchesResource.class);
        router.attach("/links/json", NetworkGraphLinksResource.class);
        router.attach("/devices/json", TopoDevicesResource.class);
		return router;
	}

	@Override
	public String basePath() {
        return "/wm/onos/topology";
	}

}
