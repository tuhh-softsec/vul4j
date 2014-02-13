package net.onrc.onos.ofcontroller.networkgraph.web;

import net.onrc.onos.datastore.topology.RCPort;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RamcloudPortsResource extends ServerResource {

	@Get("json")
	public Iterable<RCPort> retrieve() {
		return RCPort.getAllPorts();
	}
}
