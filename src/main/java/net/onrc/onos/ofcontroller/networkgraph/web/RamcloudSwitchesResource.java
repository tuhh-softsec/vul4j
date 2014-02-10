package net.onrc.onos.ofcontroller.networkgraph.web;

import net.onrc.onos.datastore.topology.RCSwitch;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RamcloudSwitchesResource extends ServerResource {
	
	@Get("json")
	public Iterable<RCSwitch> retrieve() {
		return RCSwitch.getAllSwitches();
	}

}
