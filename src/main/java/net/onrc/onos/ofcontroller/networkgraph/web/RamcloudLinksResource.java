package net.onrc.onos.ofcontroller.networkgraph.web;

import net.onrc.onos.datastore.topology.KVLink;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RamcloudLinksResource extends ServerResource {

	@Get("json")
	public Iterable<KVLink> retrieve() {
		return KVLink.getAllLinks();
	}
}
