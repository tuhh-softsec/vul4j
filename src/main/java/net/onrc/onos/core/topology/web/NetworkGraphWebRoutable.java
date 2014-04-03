package net.onrc.onos.core.topology.web;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class NetworkGraphWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		// leaving old path there for compatibility
		router.attach("/rc/switches/json", DatastoreSwitchesResource.class);
		router.attach("/rc/links/json", DatastoreLinksResource.class);
		router.attach("/rc/ports/json", DatastorePortsResource.class);

                // debug API to dump datastore content
                router.attach("/ds/switches/json", DatastoreSwitchesResource.class);
                router.attach("/ds/links/json", DatastoreLinksResource.class);
                router.attach("/ds/ports/json", DatastorePortsResource.class);

		router.attach("/ng/switches/json", NetworkGraphSwitchesResource.class);
		router.attach("/ng/links/json", NetworkGraphLinksResource.class);
		router.attach("/ng/shortest-path/{src-dpid}/{dst-dpid}/json", NetworkGraphShortestPathResource.class);
		
		// Old URLs for compatibility
		router.attach("/topology/switches/json", NetworkGraphSwitchesResource.class);
		router.attach("/topology/links/json", NetworkGraphLinksResource.class);
		
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/onos";
	}

}
