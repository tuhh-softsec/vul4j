/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.query.QueryTools;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationConfig;

/**
 * REST service for Messung objects.
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
 *      "id": [number],
 *      "fertig": [boolean],
 *      "letzteAenderung": [timestamp],
 *      "messdauer": [number],
 *      "messzeitpunkt": [timestamp],
 *      "mmtId": [string],
 *      "probeId": [number],
 *      "owner": [boolean],
 *      "readonly": [boolean],
 *      "nebenprobenNr": [string],
 *      "geplant": [boolean],
 *      "treeModified": [timestamp],
 *      "parentModified": [timestamp],
 *      "externeMessunsgsId": [number]
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
@Path("rest/messung")
@RequestScoped
public class MessungService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    /**
     * The object lock mechanism.
     */
    @Inject
    @LockConfig(type=LockType.TIMESTAMP)
    private ObjectLocker lock;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    @Inject
    @ValidationConfig(type="Messung")
    private Validator validator;

    @Inject
    private QueryTools queryTools;

    /**
     * Get all Messung objects.
     * <p>
     * The requested objects can be filtered using the following URL parameters:
     * parameters:<br>
     * probeId: probeId to use as filter
     * page: The page to display in a paginated result grid.<br>
     * start: The first Probe item.<br>
     * limit: The count of Probe items.<br>
     * <p>
     * Example: http://example.com/messung?probeId=[ID]&page=[PAGE]&start=[START]&limit=[LIMIT]
     *
     * @return Response object containing all Messung objects.
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
        UserInfo userInfo = authorization.getInfo(request);
        //If no params are given: return all messung records
        if (params.isEmpty() ||
            (!params.containsKey("probeId"))) {
            List<Messung> messungs = repository.getAllPlain(Messung.class, Strings.LAND);
            int size = messungs.size();
            if (params.containsKey("start") && params.containsKey("limit")) {
                int start = Integer.valueOf(params.getFirst("start"));
                int limit = Integer.valueOf(params.getFirst("limit"));
                int end = limit + start;
                if (start + limit > size) {
                    end = size;
                }
                messungs = messungs.subList(start, end);
            }
            for (Messung m: messungs) {
                m.setReadonly(authorization.isMessungReadOnly(m.getId()));
                Violation violation = validator.validate(m);
                if (violation.hasErrors() || violation.hasWarnings()) {
                    m.setErrors(violation.getErrors());
                    m.setWarnings(violation.getWarnings());
                }
            }
            return new Response(true, 200, messungs);
        } else {
            //Filter by probeId
            String probeId = params.getFirst("probeId");
            QueryBuilder<Messung> builder =
                new QueryBuilder<Messung>(
                    repository.entityManager(Strings.LAND),
                    Messung.class);
            builder.and("probeId", probeId);
            Response r = authorization.filter(
                request,
                repository.filter(builder.getQuery(), Strings.LAND),
                Messung.class);
            if (r.getSuccess() == true) {
                @SuppressWarnings("unchecked")
                List<Messung> messungs = (List<Messung>) r.getData();
                int size = messungs.size();
                if (params.containsKey("start") && params.containsKey("limit")) {
                    int start = Integer.valueOf(params.getFirst("start"));
                    int limit = Integer.valueOf(params.getFirst("limit"));
                    int end = limit + start;
                    if (start + limit > size) {
                        end = size;
                    }
                    messungs = messungs.subList(start, end);
                }
                if (messungs.size() > 0) {
                    for (Messung messung: messungs) {
                        messung.setReadonly(
                            !authorization.isAuthorized(request, messung, RequestMethod.PUT, Messung.class));
                        Violation violation = validator.validate(messung);
                        if (violation.hasErrors() || violation.hasWarnings()) {
                            messung.setErrors(violation.getErrors());
                            messung.setWarnings(violation.getWarnings());
                        }
                    }
                }
                return new Response(true, 200, messungs);
                //return authorization.filter(request, new Response(true, 200, messungs), Messung.class);
            } else {
                return r;
            }
        }
    }

    /**
     * Get a Messung object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messung/{id}
     *
     * @return Response object containing a single Messung.
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
            repository.getById(Messung.class, Integer.valueOf(id), Strings.LAND);
        Messung messung = (Messung)response.getData();
        Violation violation = validator.validate(messung);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            Messung.class);
    }

    /**
     * Create a Messung object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  "owner": [boolean],
     *  "probeId": [number],
     *  "mmtId": [string],
     *  "nebenprobenNr": [string],
     *  "messdauer": [number],
     *  "fertig": [boolean],
     *  "geplant": [boolean],
     *  "externeMessungsId": [string],
     *  "treeModified": null,
     *  "parentModified": null,
     *  "messzeitpunkt": [date],
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return A response object containing the created Messung.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        Messung messung
    ) {
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.POST,
                Messung.class)
        ) {
            return new Response(false, 699, null);
        }

        Violation violation = validator.validate(messung);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, messung);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }

        /* Persist the new messung object*/
        Response response = repository.create(messung, Strings.LAND);
        if(violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            response,
            Messung.class);
    }

    /**
     * Update an existing Messung object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "owner": [boolean],
     *  "probeId": [number],
     *  "mmtId": [string],
     *  "nebenprobenNr": [string],
     *  "messdauer": [number],
     *  "fertig": [boolean],
     *  "geplant": [boolean],
     *  "externeMessungsId": [number],
     *  "treeModified": [timestamp],
     *  "parentModified": [timestamp],
     *  "messzeitpunkt": [date],
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated Messung object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id,
        Messung messung
    ) {
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.PUT,
                Messung.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }
        Violation violation = validator.validate(messung);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, messung);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }
        Response response = repository.update(messung, Strings.LAND);
        if (!response.getSuccess()) {
            return response;
        }
        Response updated = repository.getById(
            Messung.class,
            ((Messung)response.getData()).getId(), Strings.LAND);
        if(violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            updated,
            Messung.class);
    }

    /**
     * Delete an existing Messung object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messung/{id}
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
        /* Get the messung object by id*/
        Response messung =
            repository.getById(Messung.class, Integer.valueOf(id), Strings.LAND);
        Messung messungObj = (Messung)messung.getData();
        if (!authorization.isAuthorized(
                request,
                messungObj,
                RequestMethod.DELETE,
                Messung.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }

        /* Delete the messung object*/
        return repository.delete(messungObj, Strings.LAND);
    }
}
