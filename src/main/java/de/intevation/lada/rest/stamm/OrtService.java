/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.List;

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

import de.intevation.lada.model.stamm.Ort;
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
 * REST service for SOrt objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [number],
 *      "beschreibung": [string],
 *      "bezeichnung": [string],
 *      "hoeheLand": [number],
 *      "koordXExtern": [string],
 *      "koordYExtern": [string],
 *      "latitude": [number],
 *      "letzteAenderung": [timestamp],
 *      "longitude": [number],
 *      "nutsCode": [string],
 *      "unscharf": [string],
 *      "koordinatenartId": [number],
 *      "netzbetreiberId": [number],
 *      "staatId": [number],
 *      "verwaltungseinheitId": [string],
 *      "otyp": [string]
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
@Path("rest/ort")
@RequestScoped
public class OrtService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all SOrt objects.
     * <p>
     * The requested objects can be filtered using a URL parameter named
     * ortId.
     * <p>
     * Example: http://example.com/location?ortId=[ID]
     *
     * @return Response object containing all (filtered) SOrt objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("ortId")) {
            List<Ort> orte = defaultRepo.getAllPlain(Ort.class, "stamm");
            int size = orte.size();
            if (params.containsKey("start") && params.containsKey("limit")) {
                int start = Integer.valueOf(params.getFirst("start"));
                int limit = Integer.valueOf(params.getFirst("limit"));
                int end = limit + start;
                if (limit == 0) {
                    end = orte.size();
                }
                else if (start + limit > orte.size()) {
                    end = orte.size();
                }
                orte = orte.subList(start, end);
            }
            for (Ort o : orte) {
                o.setReadonly(
                    !authorization.isAuthorized(
                        request,
                        o,
                        RequestMethod.POST,
                        Ort.class));
            }
            return new Response(true, 200, orte, size);
        }
        String ortId = params.getFirst("ortId");
        QueryBuilder<Ort> builder =
            new QueryBuilder<Ort>(
                defaultRepo.entityManager("stamm"),
                Ort.class);
        builder.and("id", ortId);
        return defaultRepo.filter(builder.getQuery(), "stamm");
    }

    /**
     * Get a single SOrt object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/location/{id}
     *
     * @return Response object containing a single SOrt.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return defaultRepo.getById(
            Ort.class,
            Integer.valueOf(id),
            "stamm");
    }

    /**
     * Create a SOrt object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  "bezeichnung": [string],
     *  "beschreibung": [string],
     *  "unscharf": [string],
     *  "nutsCode": [string],
     *  "koordXExtern": [string],
     *  "koordYExtern": [string],
     *  "hoeheLand": [string],
     *  "longitude": [number],
     *  "latitude": [number],
     *  "staatId": [number],
     *  "verwaltungseinheitId": [string],
     *  "otyp": [string],
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     * @return A response object containing the created SOrt.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        Ort ort
    ) {
        if (!authorization.isAuthorized(
            request,
            ort,
            RequestMethod.POST,
            Ort.class)
        ) {
            return new Response(false, 699, ort);
        }
        /* Persist the new object*/
        return defaultRepo.create(ort, "stamm");
    }

    /**
     * Update an existing SOrt object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "bezeichnung": [string],
     *  "beschreibung": [string],
     *  "unscharf": [string],
     *  "nutsCode": [string],
     *  "koordXExtern": [string],
     *  "koordYExtern": [string],
     *  "hoeheLand": [number],
     *  "longitude": [number],
     *  "latitude": [number],
     *  "staatId": [number],
     *  "verwaltungseinheitId": [string],
     *  "otyp": [string],
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated SOrt object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        Ort ort
    ) {
        if (!authorization.isAuthorized(
            request,
            ort,
            RequestMethod.PUT,
            Ort.class)
        ) {
            return new Response(false, 699, ort);
        }
        Response response = defaultRepo.update(ort, "stamm");
        Response updated = defaultRepo.getById(
            Ort.class,
            ((Ort)response.getData()).getId(), "stamm");
        return updated;
    }

    /**
     * Delete an existing SOrt object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/location/{id}
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
        /* Get the object by id*/
        Ort ort =
            defaultRepo.getByIdPlain(Ort.class, Integer.valueOf(id), "stamm");
        if (!authorization.isAuthorized(
            request,
            ort,
            RequestMethod.DELETE,
            Ort.class)
        ) {
            return new Response(false, 699, ort);
        }
        /* Delete the object*/
        return defaultRepo.delete(ort, "stamm");
    }
}
