package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;

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
import de.intevation.lada.model.LZusatzWert;

/**
 * This class produces a RESTful service to read the contents of
 * l_zusatz_wert table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/zusatzwert")
@RequestScoped
public class LZusatzwertService
{
    /**
     * The Repository for SUmwelt.
     */
    @Inject
    @Named("lzusatzwertrepository")
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
        return repository.findById(LZusatzWert.class, id);
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
    public Response filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("probeId")) {
            return new Response(false, 609, new ArrayList<LZusatzWert>());
        }
        String paramValue = params.getFirst("probeId");
        QueryBuilder<LZusatzWert> builder =
            new QueryBuilder<LZusatzWert>(
                repository.getEntityManager(), LZusatzWert.class);
        builder.and("probeId", paramValue);
        return repository.filter(builder.getQuery());
    }

    @PUT
    @Produces("text/json")
    @Path("/{pzsId}/{probeId}")
    @Consumes("application/json")
    public Response update(LZusatzWert zusatzwert) {
        return repository.update(zusatzwert);
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(LZusatzWert zusatzwert) {
        return repository.create(zusatzwert);
    }

    @DELETE
    @Path("/{pzsId}/{probeId}")
    public Response delete(
        @PathParam("pzsId") String pzsId,
        @PathParam("probeId") String probeId ) {
        QueryBuilder<LZusatzWert> builder =
            new QueryBuilder<LZusatzWert>(
                repository.getEntityManager(),
                LZusatzWert.class);
        builder.and("pzsId", pzsId).and("probeId", probeId);
        Response response = repository.filter(builder.getQuery());
        List<LZusatzWert> list = (List<LZusatzWert>)response.getData();
        if (!list.isEmpty()) {
            repository.delete(list.get(0));
            return new Response(true, 200, null);
        }
        return new Response(false, 600, null);
    }
}
