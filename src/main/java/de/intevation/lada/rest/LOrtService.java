package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.data.LOrtRepository;
import de.intevation.lada.model.LOrt;

@Path("ort")
@RequestScoped
public class LOrtService
{

    /**
     * The repository for LOrt.
     */
    @Inject
    private LOrtRepository repository;

    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(LOrt.class, id);
    }

    @GET
    @Produces("text/json")
    public Response filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty()) {
            return repository.findAll(LOrt.class);
        }
        String probeId = "";
        if (params.containsKey("probeid")) {
            probeId = params.getFirst("probeid");
        }
        return repository.filter(probeId);
    }

    @PUT
    @Path("/{id}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(LOrt probe) {
        return repository.update(probe);
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(LOrt probe) {
        return repository.create(probe);
    }
}
