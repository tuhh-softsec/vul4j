package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
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

import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LOrt;

@Path("ort")
@RequestScoped
public class LOrtService
{

    /**
     * The repository for LOrt.
     */
    @Inject
    @Named("lortrepository")
    private Repository repository;

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
            repository.findAll(LOrt.class);
        }
        QueryBuilder<LOrt> builder =
            new QueryBuilder<LOrt>(
                repository.getEntityManager(), LOrt.class);
        if (params.containsKey("probeId")) {
            builder.and("probeId", params.getFirst("probeId"));
        }
        return repository.filter(builder.getQuery());
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
