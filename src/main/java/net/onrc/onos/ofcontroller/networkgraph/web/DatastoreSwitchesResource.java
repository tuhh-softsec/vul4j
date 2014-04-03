package net.onrc.onos.ofcontroller.networkgraph.web;

import net.onrc.onos.core.datastore.topology.KVSwitch;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DatastoreSwitchesResource extends ServerResource {
	
	@Get("json")
	public Iterable<KVSwitch> retrieve() {
		return KVSwitch.getAllSwitches();
	}

}
