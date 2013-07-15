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
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LMesswertId;

/**
 * This class produces a RESTful service to read, write and update
 * the contents of LMesswert objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/messwert")
@RequestScoped
public class LMesswertService
{
    /**
     * The Repository for LMesswert.
     */
    @Inject
    @Named("lmesswertrepository")
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

    /**
     * Request LMessert via a filter.
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info      The URL query parameters.
     * @param headers   The HTTP header containing authorization information.
     * @return JSON     Object via Rest service.
     */
    @GET
    @Produces("text/json")
    public Response filter(
        @Context UriInfo info,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, new ArrayList<LMesswert>());
            }
            MultivaluedMap<String, String> params = info.getQueryParameters();
            if (params.isEmpty() ||
                !params.containsKey("probeId") ||
                !params.containsKey("messungsId")) {
                return new Response(false, 609, new ArrayList<LMesswert>());
            }
            String probeId = params.getFirst("probeId");
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LMesswert> builder =
                    new QueryBuilder<LMesswert>(
                        repository.getEntityManager(), LMesswert.class);
                builder.and("probeId", probeId)
                    .and("messungsId", params.getFirst("messungsId"));
                return repository.filter(builder.getQuery());
            }
            return new Response(false, 698, new ArrayList<LMesswert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMesswert>());
        }
    }

    /**
     * Update a LMesswert object.
     * 
     * @param messwert  The LMesswert object to update.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Path("/{messgroesseId}/{messungsId}/{probeId}")
    @Produces("text/json")
    @Consumes("application/json")
    public Response update(
        LMesswert messwert,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = messwert.getProbeId();
            Integer messungsId = messwert.getMessungsId();
            if (authentication.hasAccess(headers, probeId) &&
                !isReadOnly(probeId, messungsId)) {
                return repository.update(messwert);
            }
            return new Response(false, 698, new ArrayList<LMesswert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMesswert>());
        }
    }

    /**
     * Create a new LMesswert object.
     * 
     * @param messwert  The new LMesswert object.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(
        LMesswert messwert,
        @Context HttpHeaders headers
    ) {
        try {
            String probeId = messwert.getProbeId();
            Integer messungsId = messwert.getMessungsId();
            if (authentication.hasAccess(headers, probeId) &&
                !isReadOnly(probeId, messungsId)) {
                LMesswertId id = new LMesswertId();
                id.setMessgroesseId(messwert.getMessgroesseId());
                id.setMessungsId(messungsId);
                id.setProbeId(probeId);
                messwert.setId(id);
                return repository.create(messwert);
            }
            return new Response(false, 698, new ArrayList<LMesswert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMesswert>());
        }
    }

    /**
     * Delete a LMesswert object.
     *
     * @param statusId     The object id.
     * @param messungsId   The LProbe id.
     * @param statusId     The LStatus id
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @DELETE
    @Path("/{messgroesseId}/{messungsId}/{probeId}")
    public Response delete(
        @PathParam("messgroesseId") String messgroesseId,
        @PathParam("messungsId") String messungsId,
        @PathParam("probeId") String probeId,
        @Context HttpHeaders headers
    ) {
        try {
            if (authentication.hasAccess(headers, probeId)) {
                QueryBuilder<LMesswert> builder =
                    new QueryBuilder<LMesswert>(
                        repository.getEntityManager(),
                        LMesswert.class);
                builder.and("messgroesseId", messgroesseId)
                    .and("messungsId", messungsId)
                    .and("probeId", probeId);
                Response response = repository.filter(builder.getQuery());
                List<LMesswert> list = (List<LMesswert>)response.getData();
                if (!list.isEmpty()) {
                    repository.delete(list.get(0));
                    return new Response(true, 200, null);
                }
                return new Response(false, 600, null);
            }
            return new Response(false, 698, new ArrayList<LMesswert>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LMesswert>());
        }
    }

    private boolean isReadOnly(String probeId, Integer messungsId) {
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                messungRepository.getEntityManager(),
                LMessung.class);
        builder.and("probeId", probeId)
            .and("messungsId", String.valueOf(messungsId));
        Response response = messungRepository.filter(builder.getQuery());
        List<LMessung> messungen = (List<LMessung>) response.getData();
        if (messungen.isEmpty()) {
            return true;
        }
        if (messungen.size() > 1) {
            return true;
        }
        return messungen.get(0).isFertig();
    }
}
