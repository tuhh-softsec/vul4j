package net.onrc.onos.ofcontroller.networkgraph.web;

import net.onrc.onos.datastore.topology.RCLink;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RamcloudLinksResource extends ServerResource {

	@Get("json")
	public Iterable<RCLink> retrieve() {
		return RCLink.getAllLinks();
	}
}
