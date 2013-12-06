package net.onrc.onos.ofcontroller.flowprogrammer.web;

import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.util.HexString;
import org.restlet.resource.Get;

/**
 * FlowProgrammer REST API implementation: Interrupt synchronization to a switch.
 *
 *   GET /wm/fprog/synchronizer/interrupt/{dpid}/json"
 */
public class DoInterruptResource extends SynchronizerResource {

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
    	
    	synchronizer.interrupt(sw);
    	
    	return true;
    }
}
