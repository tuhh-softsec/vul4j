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

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LStatus;

/**
 * This class produces a RESTful service to read, write and update
 * LStatus objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/status")
@RequestScoped
public class LStatusService
{
    /**
     * The Repository for SUmwelt.
     */
    @Inject
    @Named("lstatusrepository")
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * Request LStatus via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info      The URL query parameters.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response filter(
        @Context UriInfo info,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, new ArrayList<LStatus>());
            }
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty() ||
                !params.containsKey("probeId") ||
                !params.containsKey("messungsId")) {
                return new Response(false, 609, new ArrayList<LStatus>());
            }
            String probeId = params.getFirst("probeId");
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LStatus> builder =
                    new QueryBuilder<LStatus>(
                        repository.getEntityManager(), LStatus.class);
                builder.and("probeId", probeId)
                    .and("messungsId", params.getFirst("messungsId"));
                return repository.filter(builder.getQuery());
            }
            return new Response(false, 698, new ArrayList<LStatus>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LStatus>());
        }
    }

    /**
     * Update a LStatus object.
     *
     * @param status    The LStatus object to update.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(
        LStatus status,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = status.getProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.update(status);
            }
            return new Response(false, 698, new ArrayList<LStatus>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LStatus>());
        }
    }

    /**
     * Create a new LStatus object.
     *
     * @param status    The new LStatus object.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(
        LStatus status,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = status.getProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.create(status);
            }
            return new Response(false, 698, new ArrayList<LStatus>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LStatus>());
        }
    }
}
