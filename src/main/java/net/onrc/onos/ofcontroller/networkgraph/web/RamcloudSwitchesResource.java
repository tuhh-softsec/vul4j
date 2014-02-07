package net.onrc.onos.ofcontroller.networkgraph.web;

import net.onrc.onos.datastore.topology.RCSwitch;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RamcloudSwitchesResource extends ServerResource {
	
	@Get("json")
	public Iterable<RCSwitch> retrieve() {
		INetworkGraphService networkGraphService = 
				(INetworkGraphService) getContext().getAttributes().
				get(INetworkGraphService.class.getCanonicalName());
		
		return RCSwitch.getAllSwitches();
	}

}
