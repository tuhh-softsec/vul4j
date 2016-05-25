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
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.model.land.MessungTranslation;
import de.intevation.lada.query.QueryTools;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
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
 *      "messungsIdAlt": [number]
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
     * The requested objects can be filtered using a URL parameter named
     * probeId.
     * <p>
     * Example: http://example.com/messung?probeId=[ID]
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
        if (params.isEmpty() ||
            (!params.containsKey("probeId") && !params.containsKey("qid"))) {
            return repository.getAll(LMessung.class, "land");
        }
        if (params.containsKey("probeId")) {
            String probeId = params.getFirst("probeId");
            QueryBuilder<LMessung> builder =
                new QueryBuilder<LMessung>(
                    repository.entityManager("land"),
                    LMessung.class);
            builder.and("probeId", probeId);
            return authorization.filter(
                request,
                repository.filter(builder.getQuery(), "land"),
                LMessung.class);
        }
        else if (params.containsKey("qid")) {
            Integer id = null;
            try {
                id = Integer.valueOf(params.getFirst("qid"));
            }
            catch (NumberFormatException e) {
                return new Response(false, 603, "Not a valid filter id");
            }
            List<Map<String, Object>> result =
                queryTools.getResultForQuery(params, id, "messung");

            int size = result.size();
            if (params.containsKey("start") && params.containsKey("limit")) {
                int start = Integer.valueOf(params.getFirst("start"));
                int limit = Integer.valueOf(params.getFirst("limit"));
                int end = limit + start;
                if (start + limit > result.size()) {
                    end = result.size();
                }
                result = result.subList(start, end);
            }

            QueryBuilder<LMessung> pBuilder = new QueryBuilder<LMessung>(
                repository.entityManager("land"), LMessung.class);
            for (Map<String, Object> entry: result) {
                pBuilder.or("id", (Integer)entry.get("id"));
            }
            Response r = repository.filter(pBuilder.getQuery(), "land");
            r = authorization.filter(request, r, LMessung.class);
            List<LMessung> messungen= (List<LMessung>)r.getData();
            for (Map<String, Object> entry: result) {
                Integer pId = Integer.valueOf(entry.get("id").toString());
                setAuthData(messungen, entry, pId);
            }
            return new Response(true, 200, result, size);
        }
        return new Response(false, 603, "No valid paramter given.");
    }

    private void setAuthData(
        List<LMessung> messungen,
        Map<String, Object> entry,
        Integer id
    ) {
        for (int i = 0; i < messungen.size(); i++) {
            if (id.equals(messungen.get(i).getId())) {
                entry.put("readonly", messungen.get(i).isReadonly());
                entry.put("owner", messungen.get(i).isOwner());
                entry.put("statusEdit", messungen.get(i).getStatusEdit());
                return;
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
            repository.getById(LMessung.class, Integer.valueOf(id), "land");
        LMessung messung = (LMessung)response.getData();
        Violation violation = validator.validate(messung);
        if (violation.hasErrors() || violation.hasWarnings()) {
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            LMessung.class);
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
     *  "messungsIdAlt": [string],
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
        LMessung messung
    ) {
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.POST,
                LMessung.class)
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
        Response response = repository.create(messung, "land");
        LMessung ret = (LMessung)response.getData();
        /* Create and persist a new probe translation object*/
        MessungTranslation trans = new MessungTranslation();
        trans.setMessungsId(ret);
        repository.create(trans, "land");
        /* Get and return the new probe object*/
        Response created =
            repository.getById(LMessung.class, ret.getId(), "land");
        if(violation.hasWarnings()) {
            created.setWarnings(violation.getWarnings());
        }

        LStatusProtokoll status = new LStatusProtokoll();
        status.setDatum(new Timestamp(new Date().getTime()));
        status.setMessungsId(((LMessung)created.getData()).getId());
        LProbe probe =
            repository.getByIdPlain(LProbe.class, ret.getProbeId(), "land");
        status.setErzeuger(probe.getMstId());
        status.setStatusStufe(1);
        status.setStatusWert(0);
        repository.create(status, "land");
        ret.setStatus(status.getId());
        repository.update(ret, "land");
        Response updated=
            repository.getById(LMessung.class, ret.getId(), "land");

        return authorization.filter(
            request,
            updated,
            LMessung.class);
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
     *  "messungsIdAlt": [number],
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
        LMessung messung
    ) {
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.PUT,
                LMessung.class)
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
        Response response = repository.update(messung, "land");
        if (!response.getSuccess()) {
            return response;
        }
        Response updated = repository.getById(
            LMessung.class,
            ((LMessung)response.getData()).getId(), "land");
        if(violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            updated,
            LMessung.class);
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
            repository.getById(LMessung.class, Integer.valueOf(id), "land");
        LMessung messungObj = (LMessung)messung.getData();
        if (!authorization.isAuthorized(
                request,
                messungObj,
                RequestMethod.DELETE,
                LMessung.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }

        /* Delete the messung object*/
        return repository.delete(messungObj, "land");
    }
}
