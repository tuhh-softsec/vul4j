package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import de.intevation.lada.model.LKommentarP;

/**
 * This class produces a RESTful service to read the contents of LKommentarP table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/kommentare")
@RequestScoped
public class LKommentarService
{
    /**
     * The Repository.
     */
    @Inject
    @Named("lkommentarRepository")
    private Repository repository;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request a single SKommentarP via its id.
     *
     * @param id The mst_id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(LKommentarP.class, id);
    }

    @DELETE
    @Path("/{kId}/{probeId}")
    @Produces("text/json")
    public Response delete(
        @PathParam("kId") String kId,
        @PathParam("probeId") String probeId) {
        QueryBuilder<LKommentarP> builder =
            new QueryBuilder<LKommentarP>(
                repository.getEntityManager(),
                LKommentarP.class);
        builder.and("probeId", probeId).and("kId", kId);
        Response response = repository.filter(builder.getQuery());
        List<LKommentarP> list = (List<LKommentarP>)response.getData();
        if (!list.isEmpty()) {
            repository.delete(list.get(0));
            return new Response(true, 200, null);
        }
        return new Response(false, 600, null);
    }

    @PUT
    @Path("/{kId}/{probeId}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(LKommentarP kommentar) {
        return repository.update(kommentar);
    }

    /**
     * Request a list of LKommentarP objects filtered by LProbe id.
     *
     * @param info The query parameters
     * @return JSON object via REST service.
     */
    @GET
    @Produces("text/json")
    public Response filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() ||
            !params.containsKey("probeId")
        ) {
            return new Response(false, 609, new ArrayList<LKommentarP>());
        }
        QueryBuilder<LKommentarP> builder =
            new QueryBuilder<LKommentarP>(
                repository.getEntityManager(), LKommentarP.class);
        builder.and("probeId", params.getFirst("probeId"));
        return repository.filter(builder.getQuery());
    }

    @POST
    @Consumes("application/json")
    @Produces("text/json")
    public Response create(LKommentarP kommentar) {
        return repository.create(kommentar);
    }
}
