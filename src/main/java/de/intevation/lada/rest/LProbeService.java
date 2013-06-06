package de.intevation.lada.rest;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

import de.intevation.lada.data.LProbeRepository;
import de.intevation.lada.model.LProbe;

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
    private LProbeRepository repository;

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
    LProbe item = repository.findById(LProbe.class, id);
    return new Response(true, "200", item);
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
            List<LProbe> items = repository.findAll(LProbe.class);
            return new Response(true, "200", items);
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
        List<LProbe> items = repository.filter(mstId, uwbId, begin);
        return new Response(true, "200", items);
    }

    @PUT
    @Path("/{id}")
    @Produces("text/json")
    @Consumes("application/json")
    public String update(LProbe probe) {
        return "{success: false, errors: {probeId: \"Client not found\"}}";
    }

    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public String create(LProbe probe) {
        boolean success = repository.create(probe);
        int generalError = repository.getGeneralError();
        if(success) {
            return "{" +
                   "success: true, " +
                   "message: \"" + generalError + "\", " +
                   "errors: {" + createErrorFields() + "}, " +
                   "warnings: {" + createWarningFields() + "}" +
                   "}";
        }
        else {
            return "{" +
                   "success: false, " +
                   "message: \"" + generalError + "\", " +
                   "errors: {" + createErrorFields() + "}, " +
                   "warnings: {" + createWarningFields() + "}" +
                   "}";
        }
    }

    private String createWarningFields() {
        Map<String, Integer> warnings = repository.getWarnings();
        String response = "";
        if (warnings == null || warnings.isEmpty()) {
            return response;
        }
        boolean first = true;
        for (Map.Entry<String, Integer> entry: warnings.entrySet()) {
            if (!first) {
                response +=",";
            }
            response += entry.getKey() + ":" + "\"" + entry.getValue() + "\"";
            first = false;
        }
        return response;
    }

    private String createErrorFields() {
        Map<String, Integer> errors = repository.getErrors();
        String response = "";
        if (errors.isEmpty()) {
            return response;
        }
        boolean first = true;
        for (Map.Entry<String, Integer> entry: errors.entrySet()) {
            if (!first) {
                response +=",";
            }
            response += entry.getKey() + ":" + "\"" + entry.getValue() + "\"";
            first = false;
        }
        return response;
    }
}
