package net.floodlightcontroller.flowcache.web;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class FlowWebRoutable implements RestletRoutable {
    /**
     * Create the Restlet router and bind to the proper resources.
     */
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/add/json", AddFlowResource.class);
        router.attach("/delete/{flow-id}/json", DeleteFlowResource.class);
        router.attach("/get/{flow-id}/json", GetFlowByIdResource.class);
        router.attach("/get/{installer-id}/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json", GetFlowByInstallerIdResource.class);
        router.attach("/getall/{src-dpid}/{src-port}/{dst-dpid}/{dst-port}/json", GetAllFlowsByEndpointsResource.class);
        router.attach("/getall/json", GetAllFlowsResource.class);
        return router;
    }

    /**
     * Set the base path for the Topology
     */
    @Override
    public String basePath() {
        return "/wm/flow";
    }
}
