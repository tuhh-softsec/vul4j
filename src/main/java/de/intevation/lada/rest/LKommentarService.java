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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.authentication.Authentication;
import de.intevation.lada.authentication.AuthenticationException;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LKommentarP;

/**
 * This class produces a RESTful service to read, write and update
 * LKommentarP objects.
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
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Delete a LKommentarP object identified by 'probeId' and 'kId'.
     *
     * @param kId       The object id.
     * @param probeId   The LProbe object id.
     * @param headers   The HTTP headers containing authorization information.
     * @return Response object.
     */
    @DELETE
    @Path("/{kId}/{probeId}")
    @Produces("text/json")
    public Response delete(
        @PathParam("kId") String kId,
        @PathParam("probeId") String probeId,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, new ArrayList<LKommentarP>());
            }
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LKommentarP> builder =
                    new QueryBuilder<LKommentarP>(
                        repository.getEntityManager(), LKommentarP.class);
                builder.and("probeId", probeId).and("kId", kId);
                Response response = repository.filter(builder.getQuery());
                List<LKommentarP> list = (List<LKommentarP>)response.getData();
                if (!list.isEmpty()) {
                    repository.delete(list.get(0));
                    return new Response(true, 200, null);
                }
                return new Response(false, 600, null);
            }
            return new Response(false, 698, new ArrayList<LKommentarP>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LKommentarP>());
        }
    }

    /**
     * Request a list of LKommentarP objects filtered by LProbe id.
     *
     * @param info      The URL parameters
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
                return new Response(false, 699, new ArrayList<LKommentarP>());
            }
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty() || !params.containsKey("probeId")) {
                return new Response(false, 609, new ArrayList<LKommentarP>());
            }
            String probeId = params.getFirst("probeId");
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LKommentarP> builder =
                    new QueryBuilder<LKommentarP>(
                        repository.getEntityManager(), LKommentarP.class);
                builder.and("probeId", probeId);
                return repository.filter(builder.getQuery());
            }
            return new Response(false, 698, new ArrayList<LKommentarP>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LKommentarP>());
        }
    }

    /**
     * Update a LKommentarP object.
     *
     * @param kommentar The LKommentarP object to update.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Path("/{kId}/{probeId}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(
        LKommentarP kommentar,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = kommentar.getProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.update(kommentar);
            }
            return new Response(false, 698, new ArrayList<LKommentarP>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LKommentarP>());
        }
    }

    /**
     * Create a new LKommentarP object.
     *
     * @param kommentar The new LKommentarP object.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Consumes("application/json")
    @Produces("text/json")
    public Response create(
        LKommentarP kommentar,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = kommentar.getProbeId();
            if (authentication.hasAccess(headers, probeId)) {
                return repository.create(kommentar);
            }
            return new Response(false, 698, new ArrayList<LKommentarP>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LKommentarP>());
        }
    }
}