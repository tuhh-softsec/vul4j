package net.onrc.onos.ofcontroller.core.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class OnosInternalWebRoutable implements RestletRoutable {
    @Override
    public String basePath() {
        return "/wm/onos/internal";
    }

    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        // Following added by ONOS
        router.attach("/clearflowtable/json", ClearFlowTableResource.class);
        return router;
    }
}
