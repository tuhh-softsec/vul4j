package de.intevation.lada.rest;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import de.intevation.lada.model.Ort;

@Path("/ortinfo")
@RequestScoped
public class OrtService
{
    /**
     * The Repository for SUmwelt.
     */
    @Inject
    @Named("ortrepository")
    private Repository repository;

    /**
     * Request a LZusatzWert via its id.
     *
     * @param id The LProbe id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(Ort.class, id);
    }

    /**
     * Request LMessert via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info The URL query parameters.
     * @return JSON Object via Rest service.
     */
    @GET
    @Produces("text/json")
    public Response filter() {
        return repository.findAll(Ort.class);
    }

    @PUT
    @Produces("text/json")
    @Path("/{ortId}")
    @Consumes("application/json")
    public Response update(Ort ort) {
        return repository.update(ort);
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(Ort ort) {
        return repository.create(ort);
    }

    @DELETE
    @Path("/{ortId}")
    public Response delete(@PathParam("ortId") String ortId ) {
        Response response = repository.findById(Ort.class, ortId);
        Ort ort = (Ort)response.getData();
        if (ort != null) {
            repository.delete(ort);
            return new Response(true, 200, null);
        }
        return new Response(false, 600, null);
    }
}
