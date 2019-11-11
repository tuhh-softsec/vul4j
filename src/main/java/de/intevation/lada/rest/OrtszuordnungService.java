/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

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

import org.apache.log4j.Logger;

import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
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
 * REST service for Ort objects.
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
 *      "letzteAenderung": [timestamp],
 *      "ortsTyp": [string],
 *      "ortszusatztext": [string],
 *      "probeId": [number],
 *      "ort": [number],
 *      "owner": [boolean],
 *      "readonly": [boolean],
 *      "treeModified": [timestamp],
 *      "parentModified": [timestamp]
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
@Path("rest/ortszuordnung")
@RequestScoped
public class OrtszuordnungService {

    /**
     * The logger used in this class.
     */
    @Inject
    private Logger logger;

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

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
    @ValidationConfig(type="Ortszuordnung")
    private Validator validator;

    /**
     * Get all Ort objects.
     * <p>
     * The requested objects can be filtered using a URL parameter named
     * probeId.
     * <p>
     * Example: http://example.com/ort?probeId=[ID]
     *
     *
     * @return Response object containing all Ort objects.
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
        if (params.isEmpty() || !params.containsKey("probeId")) {
            logger.debug("get all");
            return defaultRepo.getAll(Ortszuordnung.class, Strings.LAND);
        }
        String probeId = params.getFirst("probeId");
        QueryBuilder<Ortszuordnung> builder =
            new QueryBuilder<Ortszuordnung>(
                defaultRepo.entityManager(Strings.LAND),
                Ortszuordnung.class);
        builder.and("probeId", probeId);
        Response r = authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), Strings.LAND),
            Ortszuordnung.class);
        if (r.getSuccess() == true) {
            @SuppressWarnings("unchecked")
            List<Ortszuordnung> ortszuordnungs = (List<Ortszuordnung>) r.getData();
            for (Ortszuordnung otz: ortszuordnungs) {
                Violation violation = validator.validate(otz);
                if (violation.hasErrors() || violation.hasWarnings()) {
                    otz.setErrors(violation.getErrors());
                    otz.setWarnings(violation.getWarnings());
                }
            }
            return new Response(true, 200, ortszuordnungs);
        } else {
            return r;
        }
    }

    /**
     * Get a Ort object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/ort/{id}
     *
     * @return Response object containing a single Ort.
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
            defaultRepo.getById(Ortszuordnung.class, Integer.valueOf(id), Strings.LAND);
        Ortszuordnung ort = (Ortszuordnung)response.getData();
        Violation violation = validator.validate(ort);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            Ortszuordnung.class);
    }

    /**
     * Create a new Ort object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  "owner": [boolean],
     *  "ort": [number],
     *  "probeId": [number],
     *  "ortsTyp": [string],
     *  "ortszusatztext": [string],
     *  "treeModified": null,
     *  "parentModified": null,
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return A response object containing the created Ort.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        Ortszuordnung ort
    ) {
        if (!authorization.isAuthorized(
                request,
                ort,
                RequestMethod.POST,
                Ortszuordnung.class)) {
            return new Response(false, 699, null);
        }
        Violation violation = validator.validate(ort);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, ort);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }

        /* Persist the new object*/
        Response response = defaultRepo.create(ort, Strings.LAND);
        if(violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            response,
            Ortszuordnung.class);
    }

    /**
     * Update an existing Ort object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "owner": [boolean],
     *  "ort": [number],
     *  "probeId": [number],
     *  "ortsTyp": [string],
     *  "ortszusatztext": [string],
     *  "treeModified": [timestamp],
     *  "parentModified": [timestamp],
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated Ort object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id,
        Ortszuordnung ort
    ) {
        if (!authorization.isAuthorized(
                request,
                ort,
                RequestMethod.PUT,
                Ortszuordnung.class)) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(ort)) {
            return new Response(false, 697, null);
        }
        Violation violation = validator.validate(ort);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, ort);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }

        Response response = defaultRepo.update(ort, Strings.LAND);
        if (!response.getSuccess()) {
            return response;
        }
        Response updated = defaultRepo.getById(
            Ortszuordnung.class,
            ((Ortszuordnung)response.getData()).getId(), Strings.LAND);
        if(violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            updated,
            Ortszuordnung.class);
    }

    /**
     * Delete an existing Ort object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/orortt/{id}
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
        Response object =
            defaultRepo.getById(Ortszuordnung.class, Integer.valueOf(id), Strings.LAND);
        Ortszuordnung ortObj = (Ortszuordnung)object.getData();
        if (!authorization.isAuthorized(
                request,
                ortObj,
                RequestMethod.PUT,
                Ortszuordnung.class)) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(ortObj)) {
            return new Response(false, 697, null);
        }

        return defaultRepo.delete(ortObj, Strings.LAND);
    }
}
