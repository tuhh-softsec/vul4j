package net.onrc.onos.intent.runtime;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class IntentWebRoutable implements RestletRoutable {

    @Override
    public Restlet getRestlet(Context context) {
	Router router = new Router(context);
	// TODO: add routes
	return router;
    }

    @Override
    public String basePath() {
	return "/wm/onos/intent";
    }

}
