package net.floodlightcontroller.mastership;

import org.restlet.resource.ServerResource;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class MastershipRouteResource extends ServerResource {

	protected static Logger log = LoggerFactory.getLogger(MastershipRouteResource.class);

	@Get
	public String get(String fmJson) {
		// TODO
		return null;
	}
	
	@Post
	public String store (String fmJson) {
		//TODO
		return null;
	}
	
	@Delete
	public String delete (String fmJson) {
		//TODO
		return null;
	}
}
