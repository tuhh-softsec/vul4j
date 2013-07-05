package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;

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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LZusatzWert;

/**
 * This class produces a RESTful service to read, write and update
 * LZusatzwert objects.
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
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * Request LMessert via a filter.
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
                return new Response(false, 699, new ArrayList<LZusatzWert>());
            }
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty() || !params.containsKey("probeId")) {
                return new Response(false, 609, new ArrayList<LZusatzWert>());
            }
            String probeId = params.getFirst("probeId");
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LZusatzWert> builder =
                    new QueryBuilder<LZusatzWert>(
                        repository.getEntityManager(), LZusatzWert.class);
                builder.and("probeId", probeId);
                return repository.filter(builder.getQuery());
            }
            return new Response(false, 698, new ArrayList<LZusatzWert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LZusatzWert>());
        }
    }

    /**
     * Update a LZusatzwert object.
     *
     * @param zusatzwert    The LZusatzwert object to update.
     * @param headers       The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Produces("text/json")
    @Path("/{pzsId}/{probeId}")
    @Consumes("application/json")
    public Response update(
        LZusatzWert zusatzwert,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = zusatzwert.getProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.update(zusatzwert);
            }
            return new Response(false, 698, new ArrayList<LZusatzWert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LZusatzWert>());
        }
    }

    /**
     * Create a new LZusatzwert object.
     *
     * @param zusatzwert    The new LZusatzwert object.
     * @param headers       THe HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(
        LZusatzWert zusatzwert,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = zusatzwert.getProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.create(zusatzwert);
            }
            return new Response(false, 698, new ArrayList<LZusatzWert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LZusatzWert>());
        }
    }

    /**
     * Delete a LZusatzwert object.
     *
     * @param pzsId     The object id.
     * @param probeId   The LProbe id.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @DELETE
    @Path("/{pzsId}/{probeId}")
    public Response delete(
        @PathParam("pzsId") String pzsId,
        @PathParam("probeId") String probeId,
        @Context HttpHeaders headers
    ) {
        try {
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LZusatzWert> builder =
                    new QueryBuilder<LZusatzWert>(
                        repository.getEntityManager(),
                        LZusatzWert.class);
                builder.and("pzsId", pzsId).and("probeId", probeId);
                Response response = repository.filter(builder.getQuery());
                List<LZusatzWert> list = (List<LZusatzWert>)response.getData();
                if (!list.isEmpty()) {
                    repository.delete(list.get(0));
                    return new Response(true, 200, null);
                }
                return new Response(false, 600, null);
            }
            return new Response(false, 698, new ArrayList<LZusatzWert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LZusatzWert>());
        }
    }
}
