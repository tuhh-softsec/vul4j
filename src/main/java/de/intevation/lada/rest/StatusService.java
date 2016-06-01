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
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.model.stamm.MessStelle;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationConfig;

/**
 * REST service for Status objects.
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
 *      "erzeuger": [string],
 *      "messungsId": [number],
 *      "status": [number],
 *      "owner": [boolean],
 *      "readonly": [boolean],
 *      "treeModified": [timestamp],
 *      "parentModified": [timestamp],
 *      "sdatum": [timestamp],
 *      "skommentar": [string]
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
@Path("rest/status")
@RequestScoped
public class StatusService {

    @Inject
    private Logger logger = Logger.getLogger(StatusService.class);

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
    @ValidationConfig(type="Status")
    private Validator validator;

    /**
     * Get all Status objects.
     * <p>
     * The requested objects have to be filtered using an URL parameter named
     * messungsId.
     * <p>
     * Example: http://example.com/status?messungsId=[ID]
     *
     * @return Response object containing filtered Status objects.
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

        QueryBuilder<LStatusProtokoll> builder =
            new QueryBuilder<LStatusProtokoll>(
                defaultRepo.entityManager("land"),
                LStatusProtokoll.class);
        builder.and("messungsId", id);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), "land"),
            LStatusProtokoll.class);
    }

    /**
     * Get a single Status object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/status/{id}
     *
     * @return Response object containing a single Status.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        Response response = defaultRepo.getById(
            LStatusProtokoll.class,
            Integer.valueOf(id),
            "land");
        LStatusProtokoll status = (LStatusProtokoll)response.getData();
        Violation violation = validator.validate(status);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            response,
            LStatusProtokoll.class);
    }

    /**
     * Create a Status object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  "owner": [boolean],
     *  "messungsId": [number],
     *  "erzeuger": [string],
     *  "status": [number],
     *  "skommentar": [string],
     *  "treeModified":null,
     *  "parentModified":null,
     *  "sdatum": [date]
     * }
     * </code>
     * </pre>
     *
     * @return A response object containing the created Status.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LStatusProtokoll status
    ) {
        if (status.getMessungsId() == null
            || status.getErzeuger() == null
            || status.getStatusWert() == null
        ) {
            return new Response(false, 631, null);
        }

        UserInfo userInfo = authorization.getInfo(request);
        LMessung messung = defaultRepo.getByIdPlain(
            LMessung.class, status.getMessungsId(), "land");
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }

        // Is user authorized to edit status at all?
        Response r = authorization.filter(
            request,
            new Response(true, 200, messung),
            LMessung.class);
        LMessung filteredMessung = (LMessung)r.getData();
        if (filteredMessung.getStatusEdit() == false) {
            return new Response(false, 699, null);
        }

        if (messung.getStatus() == null) {
            status.setStatusStufe(1);
        }
        else {
            LStatusProtokoll currentStatus = defaultRepo.getByIdPlain(
                LStatusProtokoll.class, messung.getStatus(), "land");

            String probeMstId = defaultRepo.getByIdPlain(
                LProbe.class,
                messung.getProbeId(),
                "land").getMstId();

            if (currentStatus.getStatusWert() == 4) {
                if (status.getStatusWert() == 4
                    && userInfo.getMessstellen().contains(
                        currentStatus.getErzeuger())
                    && status.getErzeuger().equals(
                        currentStatus.getErzeuger())
                ) {
                    // 'edit' currentStatus
                    status.setStatusStufe(currentStatus.getStatusStufe());
                }
                else if (
                    userInfo.getFunktionenForMst(probeMstId)
                        .contains(1)
                    && probeMstId.equals(status.getErzeuger())
                ) {
                    status.setStatusStufe(1);
                }
                else {
                    return new Response(false, 699, null);
                }
            }
            else {
                boolean next = false; // Do we advance to next 'stufe'?
                boolean change = false; // Do we change status on same 'stufe'?

                // XXX: It's assumed here, that MessStelle:function is a
                // 1:1-relationship, which is not enforced by the model
                // (there is no such constraint in stammdaten.auth).
                // Thus, next and change will be set based
                // on whichever function is the first match, which is
                // not necessary the users intention, if he has more than
                // one function for the matching Messstelle.

                // XXX: It's assumed here, that an 'Erzeuger' is an instance
                // of 'Messstelle', but the model does not enforce it!
                for (Integer function :
                         userInfo.getFunktionenForMst(status.getErzeuger())
                ) {
                    if (function.equals(currentStatus.getStatusStufe() + 1)
                        && currentStatus.getStatusWert() != 0) {
                        next = true;
                    }
                    else if (function == currentStatus.getStatusStufe()) {
                        if (currentStatus.getStatusStufe() == 1
                            && !status.getErzeuger().equals(probeMstId)) {
                            logger.debug(
                                "Messstelle does not match for change");
                            return new Response(false, 699, null);
                        }

                        String pNetzbetreiber = defaultRepo.getByIdPlain(
                            LProbe.class,
                            messung.getProbeId(),
                            "land").getNetzbetreiberId();
                        String sNetzbetreiber = defaultRepo.getByIdPlain(
                            MessStelle.class,
                            status.getErzeuger(),
                            "stamm").getNetzbetreiberId();
                        if (currentStatus.getStatusStufe() == 2
                            && !pNetzbetreiber.equals(sNetzbetreiber)){
                            logger.debug(
                                "Netzbetreiber does not match for change");
                            return new Response(false, 699, null);
                        }
                        change = true;
                    }
                }

                if (change &&
                    status.getStatusWert() == 4 &&
                    status.getStatusStufe() > 1
                ) {
                    status.setStatusStufe(currentStatus.getStatusStufe());
                }
                else if (change && status.getStatusWert() == 8) {
                    return authorization.filter(
                        request,
                        resetStatus(status, currentStatus, messung),
                        LStatusProtokoll.class);
                }
                else if (change && status.getStatusWert() != 0) {
                    status.setStatusStufe(currentStatus.getStatusStufe());
                }
                else if (next &&
                    (status.getStatusWert() > 0 &&
                     status.getStatusWert() <= 4 ||
                     status.getStatusWert() == 7)) {
                    status.setStatusStufe(currentStatus.getStatusStufe() + 1);
                }
                else {
                    return new Response(false, 699, null);
                }
            }

            // auto-set 'fertig'-flag
            if (status.getStatusStufe() == 1) {
                messung.setFertig(true);
            }
            else if (status.getStatusWert() == 4) {
                messung.setFertig(false);
            }
        }
        Violation violation = validator.validate(status);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, status);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }
        Response response = defaultRepo.create(status, "land");
        LStatusProtokoll created = (LStatusProtokoll)response.getData();
        messung.setStatus(created.getId());
        defaultRepo.update(messung, "land");
        /* Persist the new object*/
        return authorization.filter(
            request,
            response,
            LStatusProtokoll.class);
    }

    /**
     * Update an existing Status object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "owner": [boolean],
     *  "messungsId": [number],
     *  "erzeuger": [string],
     *  "status": [number],
     *  "skommentar": [string],
     *  "treeModified": [timestamp],
     *  "parentModified": [timestamp],
     *  "sdatum": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated Status object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LStatusProtokoll status
    ) {
        return new Response(false, 699, null);
    }

    /**
     * Delete an existing Status object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/status/{id}
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
        Response object =
            defaultRepo.getById(LStatusProtokoll.class, Integer.valueOf(id), "land");
        LStatusProtokoll obj = (LStatusProtokoll)object.getData();
        if (!authorization.isAuthorized(
                request,
                obj,
                RequestMethod.DELETE,
                LStatusProtokoll.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(obj)) {
            return new Response(false, 697, null);
        }
        /* Delete the object*/
        return defaultRepo.delete(obj, "land");
    }

    private Response resetStatus(
        LStatusProtokoll status,
        LStatusProtokoll currentStatus,
        LMessung messung
    ) {
        // Create a new Status with value = 8.
        LStatusProtokoll statusNew = new LStatusProtokoll();
        statusNew.setDatum(new Timestamp(new Date().getTime()));
        statusNew.setErzeuger(status.getErzeuger());
        statusNew.setMessungsId(status.getMessungsId());
        statusNew.setStatusStufe(currentStatus.getStatusStufe());
        statusNew.setStatusWert(8);
        statusNew.setText("Reset");

        defaultRepo.create(statusNew, "land");

        Response retValue;
        if (currentStatus.getStatusStufe() == 1) {
            LStatusProtokoll nV = new LStatusProtokoll();
            nV.setDatum(new Timestamp(new Date().getTime()));
            nV.setErzeuger(status.getErzeuger());
            nV.setMessungsId(status.getMessungsId());
            nV.setStatusStufe(1);
            nV.setStatusWert(0);
            nV.setText("");
            retValue = defaultRepo.create(nV, "land");
            messung.setStatus(((LStatusProtokoll)retValue.getData()).getId());
        }
        else {
            QueryBuilder<LStatusProtokoll> lastFilter =
                new QueryBuilder<LStatusProtokoll>(
                        defaultRepo.entityManager("land"),
                        LStatusProtokoll.class);
            lastFilter.and("messungsId", status.getMessungsId());
            lastFilter.and("statusStufe", currentStatus.getStatusStufe() - 1);
            lastFilter.orderBy("datum", true);
            List<LStatusProtokoll> proto =
                defaultRepo.filterPlain(lastFilter.getQuery(), "land");
            LStatusProtokoll copy = new LStatusProtokoll();
            LStatusProtokoll orig = proto.get(proto.size() - 1);
            copy.setDatum(new Timestamp(new Date().getTime()));
            copy.setErzeuger(orig.getErzeuger());
            copy.setMessungsId(orig.getMessungsId());
            copy.setStatusStufe(orig.getStatusStufe());
            copy.setStatusWert(orig.getStatusWert());
            copy.setText("");
            retValue = defaultRepo.create(copy, "land");
            LStatusProtokoll createdCopy = (LStatusProtokoll)retValue.getData();
            messung.setStatus(createdCopy.getId());
        }
        defaultRepo.update(messung, "land");
        return retValue;
    }
}
