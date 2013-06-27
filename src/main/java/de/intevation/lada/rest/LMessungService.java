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

import de.intevation.lada.data.LMessungRepository;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.model.LMessung;

/**
* This class produces a RESTful service to read the contents of LProbe table.
* 
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@Path("/messung")
@RequestScoped
public class LMessungService
{
    /**
     * The Repository for LMessung.
     */
    @Inject
    private LMessungRepository repository;

    /**
     * Request a LMessung via its id.
     *
     * @param id The LMessung id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(LMessung.class, id);
    }

    /**
     * Request LMessung via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     * This filter can take the three parameters
     *   probe=$PROBEID (String)
     *
     * @param info The URL query parameters.
     * @return JSON Object via Rest service.
     */
    @GET
    @Produces("text/json")
    public Response filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty()) {
            repository.findAll(LMessung.class);
        }
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                repository.getEntityManager(), LMessung.class);
        if (params.containsKey("probeId")) {
            builder.and("probeId", params.getFirst("probeId"));
        }
        return repository.filter(builder.getQuery());
    }

    @PUT
    @Path("/{id}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(LMessung messung) {
        return repository.update(messung);
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(LMessung messung) {
        return repository.create(messung);
    }
}
