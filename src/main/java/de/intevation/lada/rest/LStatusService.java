package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.intevation.lada.model.LStatus;

/**
 * This class produces a RESTful service to read the contents of
 * l_status table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/status")
@RequestScoped
public class LStatusService
{
    /**
     * The Repository for SUmwelt.
     */
    @Inject
    @Named("lstatusrepository")
    private Repository repository;

    /**
     * Request a LStatus via its id.
     *
     * @param id The LStatus id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(LStatus.class, id);
    }

    /**
     * Request LStatus via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info The URL query parameters.
     * @return JSON Object via Rest service.
     */
    @GET
    @Produces("text/json")
    public Response filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() ||
            !params.containsKey("probeId") ||
            !params.containsKey("messungId")) {
            return new Response(false, 609, new ArrayList<LStatus>());
        }
        QueryBuilder<LStatus> builder =
            new QueryBuilder<LStatus>(
                repository.getEntityManager(), LStatus.class);
        builder.and("probeId", params.getFirst("probeId"))
            .and("messungsId", params.getFirst("messungsId"));
        return repository.filter(builder.getQuery());
    }

    @PUT
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(LStatus status) {
        return repository.update(status);
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(LStatus status) {
        return repository.create(status);
    }
}
