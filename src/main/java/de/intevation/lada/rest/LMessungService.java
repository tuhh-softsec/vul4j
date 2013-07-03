package de.intevation.lada.rest;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.authentication.Authentication;
import de.intevation.lada.authentication.AuthenticationException;
import de.intevation.lada.authentication.AuthenticationResponse;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LMessung;

/**
* This class produces a RESTful service to read, write and update
* LMessung objects.
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
    @Named("lmessungrepository")
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

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
    public Response filter(
        @Context UriInfo info,
        @Context HttpHeaders header
    ) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(header);
            QueryBuilder<LMessung> builder =
                new QueryBuilder<LMessung>(
                    repository.getEntityManager(),
                    LMessung.class);
            builder.or("netzbetreiberId", auth.getNetzbetreiber());
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty()) {
                repository.filter(builder.getQuery());
            }
            QueryBuilder<LMessung> pBuilder = builder.getEmptyBuilder();
            if (params.containsKey("probeId")) {
                pBuilder.and("probeId", params.getFirst("probeId"));
                builder.and(pBuilder);
            }
            return repository.filter(builder.getQuery());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMessung>());
        }
    }

    /**
     * Update a LMessung object.
     *
     * @param messung   The LMessung object to update.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Path("/{id}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(
        LMessung messung,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = messung.getLProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.update(messung);
            }
            return new Response(false, 698, new ArrayList<LMessung>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMessung>());
        }
    }

    /**
     * Create a new LMessung object.
     *
     * @param messung   The new LMessung object.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(
        LMessung messung,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = messung.getLProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.create(messung);
            }
            return new Response(false, 698, new ArrayList<LMessung>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMessung>());
        }
    }
}
