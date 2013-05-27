package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.data.LKommentarPRepository;
import de.intevation.lada.model.LKommentarP;

/**
 * This class produces a RESTful service to read the contents of LKommentarP table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/kommentar")
@RequestScoped
public class LKommentarService
{
    /**
     * The Repository.
     */
    @Inject @Named("lkommentarRepository")
    private LKommentarPRepository repository;

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
    public LKommentarP findById(@PathParam("id") String id) {
        return repository.findById(LKommentarP.class, id);
    }

    /**
     * Request a list of LKommentarP objects filtered by LProbe id.
     *
     * @param info The query parameters
     * @return JSON object via REST service.
     */
    @GET
    @Produces("text/json")
    public List<LKommentarP> filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty()) {
            return new ArrayList<LKommentarP>(0);
        }
        if (params.containsKey("probe")) {
            String probe = params.getFirst("probe");
            return repository.filter(probe);
        }
        else {
            return new ArrayList<LKommentarP>(0);
        }
    }

    @POST
    @Path("/create")
    @Consumes("application/json")
    public String create(LKommentarP kommentar) {
        String response = repository.create(kommentar);
        if (response.isEmpty()) {
            return "[{success: true}]";
        }
        else {
            return "[{success: false," +
                " error: " + response + "}]";
        }
    }
}
