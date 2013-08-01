package net.onrc.onos.ofcontroller.core.web;

import java.util.List;
import net.floodlightcontroller.routing.Link;
import net.onrc.onos.ofcontroller.core.internal.TopoLinkServiceImpl;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopoLinksResource extends ServerResource {
	
	@Get("json")
    public List<Link> retrieve() {
		TopoLinkServiceImpl impl = new TopoLinkServiceImpl();
		
		List<Link> retval = impl.getActiveLinks();
		return retval;
	}

}
