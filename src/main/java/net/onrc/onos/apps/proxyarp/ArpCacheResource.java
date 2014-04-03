package net.onrc.onos.apps.proxyarp;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * REST resource to view the IP to MAC mappings in the ARP cache.
 *
 */
public class ArpCacheResource extends ServerResource {

    /**
     * Handler for a REST call to retrieve the ARP cache.
     * @return list of mappings formatted as a human-readable string.
     */
    @Get("json")
    public List<String> getArpCache() {
        IProxyArpService arp = (IProxyArpService) getContext().getAttributes()
                .get(IProxyArpService.class.getCanonicalName());

        return arp.getMappings();
    }

}
