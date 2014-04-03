package net.onrc.onos.core.topology.web;

import net.onrc.onos.core.datastore.topology.KVPort;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DatastorePortsResource extends ServerResource {

	@Get("json")
	public Iterable<KVPort> retrieve() {
		return KVPort.getAllPorts();
	}
}
