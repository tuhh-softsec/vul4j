package net.onrc.onos.ofcontroller.flowprogrammer.web;

import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.util.HexString;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlowProgrammer REST API implementation: Suspend sending message to switch.
 *
 *   GET /wm/fprog/pusher/suspend/{dpid}/json"
 */
public class SuspendPusherResource extends PusherResource {

    protected final static Logger log = LoggerFactory.getLogger(SetPushRateResource.class);

    /**
     * Implement the API.
     *
     * @return true if succeeded, false if failed.
     */
    @Get("json")
    public boolean retrieve() {
    	if (! init()) {
    		return false;
    	}
    	
    	long dpid;
    	try {
    		dpid = HexString.toLong((String)getRequestAttributes().get("dpid"));
    	} catch (NumberFormatException e) {
    		log.error("Invalid number format");
    		return false;
    	}

    	IOFSwitch sw = provider.getSwitches().get(dpid);
    	if (sw == null) {
    		log.error("Invalid dpid");
    		return false;
    	}
    	
    	return pusher.suspend(sw);
    }
}
