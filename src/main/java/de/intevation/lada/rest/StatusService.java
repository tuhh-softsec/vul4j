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

import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.model.stammdaten.StatusReihenfolge;
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

//import org.apache.log4j.Logger;

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

//    @Inject
//    private Logger logger;

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

        QueryBuilder<StatusProtokoll> builder =
            new QueryBuilder<StatusProtokoll>(
                defaultRepo.entityManager(Strings.LAND),
                StatusProtokoll.class);
        builder.and("messungsId", id);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), Strings.LAND),
            StatusProtokoll.class);
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
            StatusProtokoll.class,
            Integer.valueOf(id),
            Strings.LAND);
        StatusProtokoll status = (StatusProtokoll)response.getData();
        Violation violation = validator.validate(status);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }

        return authorization.filter(
            request,
            response,
            StatusProtokoll.class);
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
        StatusProtokoll status
    ) {
        if (status.getMessungsId() == null
            || status.getMstId() == null
        ) {
            return new Response(false, 631, null);
        }

        UserInfo userInfo = authorization.getInfo(request);
        Messung messung = defaultRepo.getByIdPlain(
            Messung.class, status.getMessungsId(), Strings.LAND);
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }

        // Is user authorized to edit status at all?
        Response r = authorization.filter(
            request,
            new Response(true, 200, messung),
            Messung.class);
        Messung filteredMessung = (Messung)r.getData();
        if (filteredMessung.getStatusEdit() == false) {
            return new Response(false, 699, null);
        }

        if (messung.getStatus() == null) {
            // set the first status as default
            status.setStatusKombi(1);
        }
        else {
            StatusProtokoll oldStatus = defaultRepo.getByIdPlain(
                StatusProtokoll.class, messung.getStatus(), Strings.LAND);
            StatusKombi oldKombi = defaultRepo.getByIdPlain(StatusKombi.class, oldStatus.getStatusKombi(), Strings.STAMM);
            StatusKombi newKombi = defaultRepo.getByIdPlain(StatusKombi.class, status.getStatusKombi(), Strings.STAMM);

            // Check if the user is allowed to change to the requested
            // status_kombi
            // 1. The old 'status_wert' is 'rückfrage'
            //    User has 'funktion' 1 for the given mstId
            if (oldKombi.getStatusWert().getId() == 4) {
                if (userInfo.getFunktionenForMst(status.getMstId()).contains(1)) {
                    // Set the new status.
                    return setNewStatus(status, newKombi, messung, request);
                }
                else {
                    // Not allowed.
                    return new Response(false, 699, null);
                }
            }
            // 2. user wants to edit the status (stufe stays the same.)
            //    Users mstId equals the mstId of the old status.
            else if (oldKombi.getStatusStufe().getStufe().equals(
                        newKombi.getStatusStufe().getStufe()) &&
                     userInfo.getFunktionenForMst(status.getMstId()).contains(
                        newKombi.getStatusStufe().getId())
            ) {
                // a) user wants to reset the current status
                //    'status wert' == 8
                if (newKombi.getStatusWert().getId() == 8) {
                    return authorization.filter(
                        request,
                        resetStatus(status, oldStatus, messung),
                        StatusProtokoll.class);
                }
                // b) update the status by the setting the new one.
                return setNewStatus(status, newKombi, messung, request);
            }
            // 3. user wants to advance to the next 'status_stufe'
            //    Users 'funktion' equals old 'stufe' + 1
            else if (userInfo.getFunktionenForMst(status.getMstId()).contains(
                oldKombi.getStatusStufe().getId() + 1) &&
                newKombi.getStatusStufe().getId() == oldKombi.getStatusStufe().getId() + 1) {
                // Set the next status
                return setNewStatus(status, newKombi, messung, request);
            }
        }
        return new Response(false, 699, null);
    }

    private Response setNewStatus(
        StatusProtokoll status,
        StatusKombi newKombi,
        Messung messung,
        HttpServletRequest request
    ) {
        if (newKombi.getStatusWert().getId() == 1 ||
            newKombi.getStatusWert().getId() == 2 ) {
            Violation violation = validator.validate(status);
            if (violation.hasErrors()) {
                Response response = new Response(false, 604, status);
                response.setErrors(violation.getErrors());
                response.setWarnings(violation.getWarnings());
                return response;
            }
        }
        if (newKombi.getStatusStufe().getId() == 1) {
            messung.setFertig(true);
        }
        else if (newKombi.getStatusWert().getId() == 4) {
            messung.setFertig(false);
        }
        Response response = defaultRepo.create(status, Strings.LAND);
        StatusProtokoll created = (StatusProtokoll)response.getData();
        messung.setStatus(created.getId());
        defaultRepo.update(messung, Strings.LAND);
        return authorization.filter(
            request,
            response,
            StatusProtokoll.class);
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
        @PathParam("id") String id,
        StatusProtokoll status
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
            defaultRepo.getById(StatusProtokoll.class, Integer.valueOf(id), Strings.LAND);
        StatusProtokoll obj = (StatusProtokoll)object.getData();
        if (!authorization.isAuthorized(
                request,
                obj,
                RequestMethod.DELETE,
                StatusProtokoll.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(obj)) {
            return new Response(false, 697, null);
        }
        /* Delete the object*/
        return defaultRepo.delete(obj, Strings.LAND);
    }

    private Response resetStatus(
        StatusProtokoll newStatus,
        StatusProtokoll oldStatus,
        Messung messung
    ) {
        // Create a new Status with value = 8.
        QueryBuilder<StatusKombi> kombiFilter =
            new QueryBuilder<StatusKombi>(
                    defaultRepo.entityManager(Strings.STAMM),
                    StatusKombi.class);
        StatusKombi oldKombi = defaultRepo.getByIdPlain(StatusKombi.class, oldStatus.getStatusKombi(), Strings.STAMM);

        kombiFilter.and("statusStufe", oldKombi.getStatusStufe().getId());
        kombiFilter.and("statusWert", 8);
        List<StatusKombi> newKombi = defaultRepo.filterPlain(kombiFilter.getQuery(), Strings.STAMM);
        StatusProtokoll statusNew = new StatusProtokoll();
        statusNew.setDatum(new Timestamp(new Date().getTime()));
        statusNew.setMstId(newStatus.getMstId());
        statusNew.setMessungsId(newStatus.getMessungsId());
        statusNew.setStatusKombi(newKombi.get(0).getId());
        statusNew.setText(newStatus.getText());

        defaultRepo.create(statusNew, Strings.LAND);

        Response retValue;
        StatusKombi kombi = defaultRepo.getByIdPlain(
            StatusKombi.class,
            oldStatus.getStatusKombi(),
            Strings.STAMM);
        if (kombi.getStatusStufe().getId() == 1) {
            StatusProtokoll nV = new StatusProtokoll();
            nV.setDatum(new Timestamp(new Date().getTime()));
            nV.setMstId(newStatus.getMstId());
            nV.setMessungsId(newStatus.getMessungsId());
            nV.setStatusKombi(1);
            nV.setText("");
            retValue = defaultRepo.create(nV, Strings.LAND);
            messung.setStatus(((StatusProtokoll)retValue.getData()).getId());
            messung.setFertig(false);
        }
        else {
            QueryBuilder<StatusProtokoll> lastFilter =
                new QueryBuilder<StatusProtokoll>(
                        defaultRepo.entityManager(Strings.LAND),
                        StatusProtokoll.class);
            lastFilter.and("messungsId", newStatus.getMessungsId());
            lastFilter.orderBy("datum", true);
            List<StatusProtokoll> proto =
                defaultRepo.filterPlain(lastFilter.getQuery(), Strings.LAND);
            // Find a status that has "status_stufe" = "old status_stufe - 1"
            int ndx = -1;
            for (int i = proto.size() - 1; i >= 0; i--) {
                int curKom = proto.get(i).getStatusKombi();
                StatusKombi sk =
                    defaultRepo.getByIdPlain(StatusKombi.class, curKom, Strings.STAMM);
                if (sk.getStatusStufe().getId() ==
                        kombi.getStatusStufe().getId() -1
                ) {
                    ndx = i;
                    break;
                }
            }
            StatusProtokoll copy = new StatusProtokoll();
            StatusProtokoll orig = proto.get(ndx);
            copy.setDatum(new Timestamp(new Date().getTime()));
            copy.setMstId(orig.getMstId());
            copy.setMessungsId(orig.getMessungsId());
            copy.setStatusKombi(orig.getStatusKombi());
            copy.setText("");
            retValue = defaultRepo.create(copy, Strings.LAND);
            StatusProtokoll createdCopy = (StatusProtokoll)retValue.getData();
            messung.setStatus(createdCopy.getId());
        }
        defaultRepo.update(messung, Strings.LAND);
        return retValue;
    }
}
