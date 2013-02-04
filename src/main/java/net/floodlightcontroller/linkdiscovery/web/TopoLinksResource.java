package net.floodlightcontroller.linkdiscovery.web;

import java.util.List;
import net.floodlightcontroller.linkdiscovery.internal.TopoLinkServiceImpl;
import net.floodlightcontroller.routing.Link;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopoLinksResource extends ServerResource {
	
	@Get("json")
    public List<Link> retrieve() {
		TopoLinkServiceImpl impl = new TopoLinkServiceImpl();
		
		List<Link> retval = impl.GetActiveLinks();
		return retval;
	}

}
