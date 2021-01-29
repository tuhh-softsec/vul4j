/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for KommentarM objects.
 * <p>
 * The services produce data in the application/json media type.
 * All HTTP methods use the authorization module to determine if the user is
 * allowed to perform the requested action.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "messungsId": [number],
 *      "datum": [timestamp],
 *      "erzeuger": [string],
 *      "id": [number],
 *      "text": [string],
 *      "owner": [boolean],
 *      "readonly": [boolean]
 *  }],
 *  "errors": [object],
 *  "warnings": [object],
 *  "readonly": [boolean],
 *  "totalCount": [number]
 * }
 * </code>
 * </pre>
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("rest/mkommentar")
@RequestScoped
public class KommentarMService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RW)
    private Repository defaultRepo;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type = AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all KommentarM objects.
     * <p>
     * The requested objects have to be filtered using an URL parameter named
     * messungsId.
     * <p>
     * Example: http://example.com/mkommentar?messungsId=[ID]
     *
     * @return Response object containing filtered KommentarM objects.
     * Status-Code 699 if parameter is missing or requested objects are
     * not authorized.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("messungsId")) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        String messungId = params.getFirst("messungsId");
        int id;
        try {
            id = Integer.valueOf(messungId);
        } catch (NumberFormatException nfe) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        Messung messung = defaultRepo.getByIdPlain(
            Messung.class,
            id,
            Strings.LAND);
        if (!authorization.isAuthorized(
                request, messung, RequestMethod.GET, Messung.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }

        QueryBuilder<KommentarM> builder =
            new QueryBuilder<KommentarM>(
                defaultRepo.entityManager(Strings.LAND),
                KommentarM.class);
        builder.and("messungsId", messungId);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), Strings.LAND),
            KommentarM.class);
    }

    /**
     * Get a single KommentarM object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/mkommentar/{id}
     *
     * @return Response object containing a single KommentarM.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        Response response =
            defaultRepo.getById(
                KommentarM.class, Integer.valueOf(id), Strings.LAND);
        KommentarM kommentar = (KommentarM) response.getData();
        Messung messung = defaultRepo.getByIdPlain(
            Messung.class,
            kommentar.getMessungsId(),
            Strings.LAND);
        if (!authorization.isAuthorized(
                request, messung, RequestMethod.GET, Messung.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }

        return authorization.filter(
            request,
            response,
            KommentarM.class);
    }

    /**
     * Create a KommentarM object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  messungsId: [number],
     *  erzeuger: [string],
     *  text: [string],
     *  datum: [date]
     *  owner: [boolean],
     * }
     * </code>
     * </pre>
     * @return A response object containing the created KommentarM.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        KommentarM kommentar
    ) {
        if (!authorization.isAuthorized(
                request,
                kommentar,
                RequestMethod.POST,
                KommentarM.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        /* Persist the new object*/
        return authorization.filter(
            request,
            defaultRepo.create(kommentar, Strings.LAND),
            KommentarM.class);
    }

    /**
     * Update an existing KommentarM object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "owner": [boolean],
     *  "messungsId": [number],
     *  "erzeuger": [string],
     *  "text": [string],
     *  "datum": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated KommentarM object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id,
        KommentarM kommentar
    ) {
        if (!authorization.isAuthorized(
                request,
                kommentar,
                RequestMethod.PUT,
                KommentarM.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        return authorization.filter(
            request,
            defaultRepo.update(kommentar, Strings.LAND),
            KommentarM.class);
    }

    /**
     * Delete an existing KommentarM object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/mkommentar/{id}
     *
     * @return Response object.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        /* Get the object by id*/
        Response kommentar =
            defaultRepo.getById(
                KommentarM.class, Integer.valueOf(id), Strings.LAND);
        KommentarM kommentarObj = (KommentarM) kommentar.getData();
        if (!authorization.isAuthorized(
                request,
                kommentarObj,
                RequestMethod.DELETE,
                KommentarM.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        return defaultRepo.delete(kommentarObj, Strings.LAND);
    }
}
