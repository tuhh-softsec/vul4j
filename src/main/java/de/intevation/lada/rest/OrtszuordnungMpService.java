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

import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.OrtszuordnungMp;
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
 *      "messprogrammId": [number],
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
@Path("rest/ortszuordnungmp")
@RequestScoped
public class OrtszuordnungMpService {

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
     * messprogrammId.
     * <p>
     * Example: http://example.com/ort?messprogrammId=[ID]
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
        if (params.isEmpty() || !params.containsKey("messprogrammId")) {
            return defaultRepo.getAll(OrtszuordnungMp.class, Strings.LAND);
        }
        String messprogrammId = params.getFirst("messprogrammId");
        QueryBuilder<OrtszuordnungMp> builder =
            new QueryBuilder<OrtszuordnungMp>(
                defaultRepo.entityManager(Strings.LAND),
                OrtszuordnungMp.class);
        builder.and("messprogrammId", messprogrammId);
        Response r =  authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), Strings.LAND),
            OrtszuordnungMp.class);
            if (r.getSuccess() == true) {
                @SuppressWarnings("unchecked")
                List<OrtszuordnungMp> ortszuordnungs = (List<OrtszuordnungMp>) r.getData();
                for (OrtszuordnungMp otz: ortszuordnungs) {
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
            defaultRepo.getById(OrtszuordnungMp.class, Integer.valueOf(id), Strings.LAND);
        OrtszuordnungMp ort = (OrtszuordnungMp)response.getData();
        Violation violation = validator.validate(ort);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            OrtszuordnungMp.class);
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
     *  "messprogrammId": [number],
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
        OrtszuordnungMp ort
    ) {
        if (!authorization.isAuthorized(
                request,
                ort,
                RequestMethod.POST,
                OrtszuordnungMp.class)) {
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
            OrtszuordnungMp.class);
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
     *  "messprogrammId": [number],
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
        OrtszuordnungMp ort
    ) {
        if (!authorization.isAuthorized(
                request,
                ort,
                RequestMethod.PUT,
                OrtszuordnungMp.class)) {
            return new Response(false, 699, null);
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
            OrtszuordnungMp.class,
            ((OrtszuordnungMp)response.getData()).getId(), Strings.LAND);
        if(violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            updated,
            OrtszuordnungMp.class);
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
            defaultRepo.getById(OrtszuordnungMp.class, Integer.valueOf(id), Strings.LAND);
        OrtszuordnungMp ortObj = (OrtszuordnungMp)object.getData();
        if (!authorization.isAuthorized(
                request,
                ortObj,
                RequestMethod.PUT,
                OrtszuordnungMp.class)) {
            return new Response(false, 699, null);
        }

        return defaultRepo.delete(ortObj, Strings.LAND);
    }
}
