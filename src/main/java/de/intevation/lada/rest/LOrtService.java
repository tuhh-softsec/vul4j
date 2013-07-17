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
import de.intevation.lada.auth.Authorization;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LOrt;

/**
 * This class produces a RESTful service to read, write and update
 * LOrt objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/ort")
@RequestScoped
public class LOrtService
{

    /**
     * The repository for LOrt.
     */
    @Inject
    @Named("lortrepository")
    private Repository repository;

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
     * Request LOrt via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info      The URL query parameters.
     * @param headers   The HTTP header containing authorization information.
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
                return new Response(false, 699, new ArrayList<LOrt>());
            }
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty() ||
                !params.containsKey("probeId")) {
                return new Response(false, 609, new ArrayList<LOrt>());
            }
            String probeId = params.getFirst("probeId");
            QueryBuilder<LOrt> builder =
                new QueryBuilder<LOrt>(
                    repository.getEntityManager(), LOrt.class);
            builder.and("probeId", probeId);
            Response response = repository.filter(builder.getQuery());
            List<LOrt> list = (List<LOrt>) response.getData();
            boolean readonly = authorization.isReadOnly(probeId);
            for (LOrt ort: list) {
                ort.setReadonly(readonly);
            }
            response.setData(list);
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }

    /**
     * Update LOrt objects.
     *
     * @param ort       The LOrt object to update.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Path("/{portId}/{probeId}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(
        LOrt ort,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = ort.getProbeId();
            if (authentication.hasAccess(headers, probeId) &&
                !authorization.isReadOnly(probeId)) {
                return repository.update(ort);
            }
            return new Response(false, 698, new ArrayList<LOrt>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }

    /**
     * Create a new LOrt object.
     *
     * @param ort       The new LOrt object.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(
        LOrt ort,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = ort.getProbeId();
            if (authentication.hasAccess(headers, probeId) &&
                !authorization.isReadOnly(probeId)) {
                return repository.create(ort);
            }
            return new Response(false, 698, new ArrayList<LOrt>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }

    /**
     * Delete a LOrt object.
     *
     * @param pzsId     The object id.
     * @param probeId   The LProbe id.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @DELETE
    @Produces("text/json")
    @Path("/{ortId}/{probeId}")
    public Response delete(
        @PathParam("ortId") String ortId,
        @PathParam("probeId") String probeId,
        @Context HttpHeaders headers
    ) {
        try {
            QueryBuilder<LOrt> builder =
                new QueryBuilder<LOrt>(
                    repository.getEntityManager(),
                    LOrt.class);
            builder.and("POrtId", ortId).and("probeId", probeId);
            Response response = repository.filter(builder.getQuery());
            List<LOrt> list = (List<LOrt>)response.getData();
            if (!list.isEmpty()) {
                LOrt ort = list.get(0);
                if (authentication.hasAccess(headers, ort.getProbeId())) {
                    repository.delete(list.get(0));
                    return new Response(true, 200, null);
                }
                return new Response(false, 698, null);
            }
            return new Response(false, 600, null);
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }
}
