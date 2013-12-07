package net.onrc.onos.ofcontroller.flowprogrammer.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class FlowProgrammerWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/pusher/setrate/{dpid}/{rate}/json", SetPushRateResource.class);
		router.attach("/pusher/suspend/{dpid}/json", SuspendPusherResource.class);
		router.attach("/pusher/resume/{dpid}/json", ResumePusherResource.class);
		router.attach("/pusher/barrier/{dpid}/json", SendBarrierResource.class);
		router.attach("/synchronizer/sync/{dpid}/json", DoSynchronizeResource.class);
		router.attach("/synchronizer/interrupt/{dpid}/json", DoInterruptResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/fprog";
	}

}
