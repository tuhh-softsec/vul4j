package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.HashMap;
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
        if (params.isEmpty()) {
            return new Response(false, 609, new ArrayList<LZusatzWert>());
        }
        Map<String, String> filter = new HashMap<String, String>();
        if (!params.containsKey("probe")) {
            return new Response(false, 609, new ArrayList<LZusatzWert>());
        }
        for (String key: params.keySet()) {
            filter.put(key, params.getFirst(key));
        }

        return repository.filter(filter);
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
}
