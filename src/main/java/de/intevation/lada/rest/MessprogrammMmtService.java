/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import java.sql.Timestamp;
import java.util.Date;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for MessprogrammMmt objects.
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
@Path("messprogrammmmt")
@RequestScoped
public class MessprogrammMmtService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
    private Authorization authorization;

    /**
     * Get all MessprogrammMmt objects.
     * <p>
     * The requested objects can be filtered using a URL parameter named
     * messprogrammId.
     * <p>
     * Example: http://example.com/messprogrammmmt?messprogrammId=[ID]
     *
     * @return Response object containing all MessprogrammMmt objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("messprogrammId")) {
            return defaultRepo.getAll(MessprogrammMmt.class, "land");
        }
        String messprogrammId = params.getFirst("messprogrammId");
        QueryBuilder<MessprogrammMmt> builder =
            new QueryBuilder<MessprogrammMmt>(
                defaultRepo.entityManager("land"),
                MessprogrammMmt.class);
        builder.and("messprogrammId", messprogrammId);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), "land"),
            MessprogrammMmt.class);
    }

    /**
     * Get a MessprogrammMmt object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messprogrammmmt/{id}
     *
     * @return Response object containing a single MessprogrammMmt.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        return authorization.filter(
            request,
            defaultRepo.getById(MessprogrammMmt.class, Integer.valueOf(id), "land"),
            MessprogrammMmt.class);
    }

    /**
     * Create a MessprogrammMmt object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     * }
     * </code>
     * </pre>
     *
     * @return A response object containing the created MessprogrammMmt.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        MessprogrammMmt messprogrammmmt
    ) {
        if (!authorization.isAuthorized(
                request,
                messprogrammmmt,
                RequestMethod.POST,
                MessprogrammMmt.class)
        ) {
            return new Response(false, 699, null);
        }

        /* Persist the new messprogrammmmt object*/
        Response response = defaultRepo.create(messprogrammmmt, "land");
        MessprogrammMmt ret = (MessprogrammMmt)response.getData();
        Response created =
            defaultRepo.getById(MessprogrammMmt.class, ret.getId(), "land");
        return authorization.filter(
            request,
            new Response(true, 200, created.getData()),
            MessprogrammMmt.class);
    }

    /**
     * Update an existing MessprogrammMmt object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated MessprogrammMmt object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        MessprogrammMmt messprogrammmmt
    ) {
        if (!authorization.isAuthorized(
                request,
                messprogrammmmt,
                RequestMethod.PUT,
                MessprogrammMmt.class)
        ) {
            return new Response(false, 699, null);
        }
        messprogrammmmt.setLetzteAenderung(new Timestamp(new Date().getTime()));
        Response response = defaultRepo.update(messprogrammmmt, "land");
        Response updated = defaultRepo.getById(
            MessprogrammMmt.class,
            ((MessprogrammMmt)response.getData()).getId(), "land");
        return authorization.filter(
            request,
            updated,
            MessprogrammMmt.class);
    }

    /**
     * Delete an existing MessprogrammMmt object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messprogammmmt/{id}
     *
     * @return Response object.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        /* Get the messprogrammmmt object by id*/
        Response messprogrammmmt =
            defaultRepo.getById(MessprogrammMmt.class, Integer.valueOf(id), "land");
        MessprogrammMmt messprogrammmmtObj = (MessprogrammMmt)messprogrammmmt.getData();
        if (!authorization.isAuthorized(
                request,
                messprogrammmmtObj,
                RequestMethod.DELETE,
                Messprogramm.class)
        ) {
            return new Response(false, 699, null);
        }
        /* Delete the messprogrammmmt object*/
        Response response = defaultRepo.delete(messprogrammmmtObj, "land");
        return response;
    }
}
