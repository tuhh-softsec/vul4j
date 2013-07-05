package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.auth.Authorization;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;

/**
* This class produces a RESTful service to read, write and update
* LProbe objects.
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

    @Inject
    @Named("lmessungrepository")
    private Repository messungRepository;

    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    @Inject
    @Named("dataauthorization")
    private Authorization authorization;

    /**
     * The logger for this class.
     */
    @Inject
    private Logger log;

    /**
     * Request a LProbe via its id.
     *
     * @param id        The LProbe id
     * @param header    THe HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(
        @PathParam("id") String id,
        @Context HttpHeaders header
    ) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(header);
            Response response =
                repository.findById(LProbeInfo.class, id);
            @SuppressWarnings("unchecked")
            List<LProbeInfo> probe = (List<LProbeInfo>)response.getData();
            if (probe.isEmpty()) {
                return new Response(false, 601, new ArrayList<LProbeInfo>());
            }
            String nbId = probe.get(0).getNetzbetreiberId();
            String mstId = probe.get(0).getMstId();
            if (auth.getNetzbetreiber().contains(nbId)) {
                if (auth.getMst().contains(mstId)) {
                    return response;
                }
                response.setReadonly(true);
                return response;
            }
            return new Response(false, 698, new ArrayList<LProbe>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LProbe>());
        }
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
     * @param info      The URL query parameters.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response filter(
        @Context UriInfo info,
        @Context HttpHeaders header
    ) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(header);
            QueryBuilder<LProbeInfo> builder =
                new QueryBuilder<LProbeInfo>(
                    repository.getEntityManager(),
                    LProbeInfo.class);
            builder.or("netzbetreiberId", auth.getNetzbetreiber());
            builder.or("fertig", Boolean.TRUE);
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty()) {
                return repository.filter(builder.getQuery());
            }
            QueryBuilder<LProbeInfo> mstBuilder = builder.getEmptyBuilder();
            if (params.keySet().contains("mstId")) {
                String[] paramValues = params.getFirst("mstId").split(",");
                for (String pv: paramValues) {
                    mstBuilder.or("mstId", pv);
                }
                builder.and(mstBuilder);
            }
            QueryBuilder<LProbeInfo> umwBuilder = builder.getEmptyBuilder();
            if (params.keySet().contains("umwId")) {
                String[] paramValues = params.getFirst("umwId").split(",");
                for (String pv: paramValues) {
                    umwBuilder.or("umwId", pv);
                }
                builder.and(umwBuilder);
            }
            QueryBuilder<LProbeInfo> beginBuilder = builder.getEmptyBuilder();
            if (params.keySet().contains("bedin")) {
                String[] paramValues = params.getFirst("begin").split(",");
                for (String pv: paramValues) {
                    beginBuilder.or("probeentnahmeBegin", pv);
                }
                builder.and(beginBuilder);
            }
            builder.distinct();
            return repository.filter(builder.getQuery());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LProbe>());
        }
    }

    /**
     * Update a LProbe object.
     *
     * @param probe     A LProbeInfo object wrapping the LProbe object.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Path("/{id}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(LProbeInfo probe, @Context HttpHeaders header) {
        try {
            if(authentication.hasAccess(header, probe.getProbeId())) {
                LProbe p = probe.toLProbe();
                p.setProbeId(probe.getProbeId());
                return repository.update(p);
            }
            return new Response(false, 698, new ArrayList<LProbeInfo>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LProbeInfo>());
        }
    }

    /**
     * Create a new LProbe object.
     * @param probe     A LProbeInfo object wrapping the LProbe object.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(LProbeInfo probe, @Context HttpHeaders header) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(header);
            if (auth.getNetzbetreiber().contains(probe.getNetzbetreiberId()) &&
                auth.getMst().contains(probe.getMstId())) {
                LProbe p = probe.toLProbe();
                return repository.create(p);
            }
            return new Response(false, 698, new ArrayList<LProbeInfo>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LProbeInfo>());
        }
    }
}
