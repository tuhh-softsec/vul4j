package net.onrc.onos.ofcontroller.core.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;
import net.onrc.onos.ofcontroller.devicemanager.web.TopoDevicesResource;

public class OnosCoreWebRoutable implements RestletRoutable {
    @Override
    public String basePath() {
        return "/wm/core";
    }

    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        // Following added by ONOS
        router.attach("/topology/switches/{filter}/json", TopoSwitchesResource.class);
        router.attach("/topology/links/json", TopoLinksResource.class);
        router.attach("/topology/devices/json", TopoDevicesResource.class);
        router.attach("/clearflowtable/json", ClearFlowTableResource.class);
        return router;
    }
}
