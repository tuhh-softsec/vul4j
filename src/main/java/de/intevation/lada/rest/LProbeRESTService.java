package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.data.LProbeRepository;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.service.LProbeService;

/**
 * JAX-RS Example
 *
 * This class produces a RESTful service to read the contents of the members table.
*/

@Path("/proben")
@RequestScoped
public class LProbeRESTService {

    @Inject
    private LProbeRepository repository;

    @Inject
    private Logger log;

    @GET
    @Path("/{id}")
    @Produces("text/json")
    public LProbe loadById(@PathParam("id") String id) {
       return repository.findById(id);
    }

    @GET
    @Produces("text/json")
    public List<LProbe> filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty()) {
            return repository.findAll();
        }
        String mstId = "";
        String uwbId = "";
        Long begin = null;
        if (params.containsKey("mst")) {
            mstId = params.getFirst("mst");
        }
        if (params.containsKey("uwb")) {
            uwbId = params.getFirst("uwb");
        }
        if (params.containsKey("begin")) {
            String tmp = params.getFirst("begin");
            try {
                begin = Long.valueOf(tmp);
            }
            catch (NumberFormatException nfe) {
                begin = null;
            }
        }
        return repository.filter(mstId, uwbId, begin);
    }
}
