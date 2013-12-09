package net.onrc.onos.ofcontroller.flowprogrammer.web;

import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.util.HexString;
import org.restlet.resource.Get;

/**
 * FlowProgrammer REST API implementation: Set sending rate to the switch.
 *
 *   GET /wm/fprog/pusher/setrate/{dpid}/{rate}/json"
 */
public class SetPushRateResource extends PusherResource {

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
    	long rate;
    	
    	try {
    		dpid = HexString.toLong((String)getRequestAttributes().get("dpid"));
        	rate = Long.valueOf((String)getRequestAttributes().get("rate"));
    	} catch (NumberFormatException e) {
    		log.error("Invalid number format");
    		return false;
    	}

    	IOFSwitch sw = provider.getSwitches().get(dpid);
    	if (sw == null) {
    		log.error("Invalid dpid");
    		return false;
    	}
    	
    	pusher.setRate(sw, rate);
    	
    	return true;
    }
}
