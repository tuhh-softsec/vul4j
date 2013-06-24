package de.intevation.lada.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;

/**
* This class produces a RESTful service to read the contents of LProbe table.
* 
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@Path("/proben")
@RequestScoped
public class LProbeService {

    /**
     * The Repository for LProbe.
     */
    @Inject
    @Named("lproberepository")
    private Repository repository;

    /**
     * The logger for this class.
     */
    @Inject
    private Logger log;

    /**
     * Request a LProbe via its id.
     *
     * @param id The LProbe id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(LProbeInfo.class, id);
    }

    /**
     * Request LProbe via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     * This filter can take the three parameters
     *   mst=$MSTID (String)
     *   uwb=$UWBID (String)
     *   begin=$PROBEENTNAHMEBEGIN (Timestamp)
     *
     * @param info The URL query parameters.
     * @return JSON Object via Rest service.
     */
    @GET
    @Produces("text/json")
    public Response filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty()) {
            return repository.findAll(LProbeInfo.class);
        }
        Map<String, String> filter = new HashMap<String, String>();
        for (String key: params.keySet()) {
            filter.put(key, params.getFirst(key));
        }

        return repository.filter(filter);
    }

    @PUT
    @Path("/{id}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(LProbeInfo probe) {
        return repository.update(probe);
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(LProbeInfo probe) {
        LProbe p = probe.toLProbe();
        return repository.create(p);
    }
}
