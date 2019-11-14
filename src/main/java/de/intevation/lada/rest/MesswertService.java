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
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.MesswertNormalizer;
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
 * REST service for Messwert objects.
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
 *      "grenzwertueberschreitung": [boolean],
 *      "letzteAenderung": [timestamp],
 *      "mehId": [number],
 *      "messfehler": [number],
 *      "messgroesseId": [number],
 *      "messungsId": [number],
 *      "messwert": [number],
 *      "messwertNwg": [string],
 *      "nwgZuMesswert": [number],
 *      "owner": [boolean],
 *      "readonly":[boolean],
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
@Path("rest/messwert")
@RequestScoped
public class MesswertService {

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
    @ValidationConfig(type="Messwert")
    private Validator validator;

    /**
     * Get all Messwert objects.
     * <p>
     * The requested objects have to be filtered using an URL parameter named
     * messungsId.
     * <p>
     * Example: http://example.com/messwert?messungsId=[ID]
     *
     * @return Response object containing filtered Messwert objects.
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
            return new Response(false, 699, null);
        }
        String messungId = params.getFirst("messungsId");
        int id;
        try {
            id = Integer.valueOf(messungId);
        }
        catch(NumberFormatException nfe) {
            return new Response(false, 698, null);
        }
        Messung messung = defaultRepo.getByIdPlain(
            Messung.class,
            id,
            Strings.LAND);
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.GET,
                Messung.class)
        ) {
            return new Response(false, 699, null);
        }
        QueryBuilder<Messwert> builder =
            new QueryBuilder<Messwert>(
                defaultRepo.entityManager(Strings.LAND),
                Messwert.class);
        builder.and("messungsId", messungId);

        Response r = authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), Strings.LAND),
            Messwert.class);
        if (r.getSuccess() == true) {
            @SuppressWarnings("unchecked")
            List<Messwert> messwerts = (List<Messwert>) r.getData();
            for (Messwert messwert: messwerts) {
                Violation violation = validator.validate(messwert);
                if (violation.hasErrors() || violation.hasWarnings()) {
                    messwert.setErrors(violation.getErrors());
                    messwert.setWarnings(violation.getWarnings());
                }
            }
            return new Response(true, 200, messwerts);
        } else {
            return r;
        }
    }

    /**
     * Get a Messwert object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messwert/{id}
     *
     * @return Response object containing a single Messwert.
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
            defaultRepo.getById(Messwert.class, Integer.valueOf(id), Strings.LAND);
        Messwert messwert = (Messwert)response.getData();
        Messung messung = defaultRepo.getByIdPlain(
            Messung.class,
            messwert.getMessungsId(),
            Strings.LAND);
        if (!authorization.isAuthorized(
            request,
            messung,
            RequestMethod.GET,
            Messung.class)
        ) {
            return new Response(false, 699, null);
        }
        Violation violation = validator.validate(messwert);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            Messwert.class);
    }

    /**
     * Create a Messwert object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  "owner": [boolean],
     *  "messungsId": [number],
     *  "messgroesseId": [number],
     *  "messwert": [number],
     *  "messwertNwg": [string],
     *  "messfehler": [number],
     *  "nwgZuMesswert": [number],
     *  "mehId": [number],
     *  "grenzwertueberschreitung": [boolean],
     *  "treeModified": null,
     *  "parentModified": null,
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return A response object containing the created Messwert.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        Messwert messwert
    ) {
        if (!authorization.isAuthorized(
                request,
                messwert,
                RequestMethod.POST,
                Messwert.class)
        ) {
            return new Response(false, 699, null);
        }
        Violation violation = validator.validate(messwert);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, messwert);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }

        /* Persist the new messung object*/
        Response response = defaultRepo.create(messwert, Strings.LAND);
        if(violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            Messwert.class);
    }

    /**
     * Update an existing Messwert object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "owner": [boolean],
     *  "messungsId": [number],
     *  "messgroesseId": [number],
     *  "messwert": [number],
     *  "messwertNwg": [string],
     *  "messfehler": [number],
     *  "nwgZuMesswert": [number],
     *  "mehId": [number],
     *  "grenzwertueberschreitung": [boolean],
     *  "treeModified": [timestamp],
     *  "parentModified": [timestamp],
     *  "letzteAenderung": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated Messwert object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id,
        Messwert messwert
    ) {
        if (!authorization.isAuthorized(
                request,
                messwert,
                RequestMethod.PUT,
                Messwert.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messwert)) {
            return new Response(false, 697, null);
        }
        Violation violation = validator.validate(messwert);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, messwert);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }

        Response response = defaultRepo.update(messwert, Strings.LAND);
        if (!response.getSuccess()) {
            return response;
        }
        Response updated = defaultRepo.getById(
            Messwert.class,
            ((Messwert)response.getData()).getId(), Strings.LAND);
        if(violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            updated,
            Messwert.class);
    }

    /**
     * Normalise all Messwert objects connected to the given Messung.
     * The messung id needs to be given as url parameter 'messungsId'.
     * @return Response object containing the updated Messwert objects.
     */
    @PUT
    @Path("/normalize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response normalize(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("messungsId")) {
            return new Response(false, 699, null);
        }
        String messungId = params.getFirst("messungsId");
        int messungIdInt;
        try {
            messungIdInt = Integer.valueOf(messungId);
        }
        catch(NumberFormatException nfe) {
            return new Response(false, 698, null);
        }
        //Load messung, probe and umwelt to get MessEinheit to convert to
        Messung messung = defaultRepo.getByIdPlain(
            Messung.class,
            messungIdInt,
            Strings.LAND);
        if (!authorization.isAuthorized(
            request,
            messung,
            RequestMethod.PUT,
            Messung.class)
        ) {
            return new Response(false, 699, null);
        }

        Probe probe = defaultRepo.getByIdPlain(Probe.class, messung.getProbeId(), Strings.LAND);
        if (probe.getUmwId() == null || probe.getUmwId().equals("")) {
            return new Response(true, 696, null);
        }
        Umwelt umwelt = defaultRepo.getByIdPlain(Umwelt.class, probe.getUmwId(), Strings.STAMM);
        //Get all Messwert objects to convert
        QueryBuilder<Messwert> messwertBuilder =
                new QueryBuilder<Messwert>(
                    defaultRepo.entityManager(Strings.LAND),
                    Messwert.class);
        messwertBuilder.and("messungsId", messungIdInt);
        List<Messwert> messwerte = MesswertNormalizer.normalizeMesswerte(
                defaultRepo.filterPlain(messwertBuilder.getQuery(), Strings.LAND),
                umwelt.getId(), defaultRepo);

        for (Messwert messwert: messwerte) {
            if (!authorization.isAuthorized(
                request,
                messwert,
                RequestMethod.PUT,
                Messwert.class)
            ) {
                return new Response(false, 699, null);
            }
            if (lock.isLocked(messwert)) {
                return new Response(false, 697, null);
            }
            Violation violation = validator.validate(messwert);
            if (violation.hasErrors()) {
                Response response = new Response(false, 604, messwert);
                response.setErrors(violation.getErrors());
                response.setWarnings(violation.getWarnings());
                return response;
            }
            Response response = defaultRepo.update(messwert, Strings.LAND);
            if (!response.getSuccess()) {
                return response;
            }
            Response updated = defaultRepo.getById(
                Messwert.class,
                ((Messwert)response.getData()).getId(), Strings.LAND);
            if(violation.hasWarnings()) {
                updated.setWarnings(violation.getWarnings());
            }
            authorization.filter(
                    request,
                    updated,
                    Messwert.class);
        }
        return new Response(true, 200, messwerte);
    }

    /**
     * Delete an existing Messwert object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messwert/{id}
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
        /* Get the messwert object by id*/
        Response messwert =
            defaultRepo.getById(Messwert.class, Integer.valueOf(id), Strings.LAND);
        Messwert messwertObj = (Messwert)messwert.getData();
        if (!authorization.isAuthorized(
                request,
                messwertObj,
                RequestMethod.DELETE,
                Messwert.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messwert)) {
            return new Response(false, 697, null);
        }
        /* Delete the messwert object*/
        return defaultRepo.delete(messwertObj, Strings.LAND);
    }
}
