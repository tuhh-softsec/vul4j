package net.onrc.onos.ofcontroller.proxyarp;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ArpCacheResource extends ServerResource {

	@Get("json")
	public List<String> getArpCache() {
		IProxyArpService arp = (IProxyArpService) getContext().getAttributes().
				get(IProxyArpService.class.getCanonicalName());
		
		return arp.getMappings();
	}

}
