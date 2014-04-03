package net.onrc.onos.core.topology.web;

import net.onrc.onos.core.datastore.topology.KVLink;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DatastoreLinksResource extends ServerResource {

    @Get("json")
    public Iterable<KVLink> retrieve() {
        return KVLink.getAllLinks();
    }
}
