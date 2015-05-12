/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.Query;
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

import de.intevation.lada.factory.ProbeFactory;
import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.ProbeTranslation;
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
 * REST service for Probe objects.
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
 *      "id":[number],
 *      "baId": [string],
 *      "datenbasisId": [number],
 *      "letzteAenderung": [timestamp],
 *      "media": [string],
 *      "mediaDesk": [string],
 *      "mittelungsdauer": [number],
 *      "mstId": [string],
 *      "netzbetreiberId":[string],
 *      "probeentnahmeBeginn": [timestamp],
 *      "probeentnahmeEnde": [timestamp],
 *      "probenartId": [number],
 *      "test": [boolean],
 *      "umwId": [string],
 *      "hauptprobenNr": [string],
 *      "erzeugerId": [string],
 *      "mpKat": [string],
 *      "mplId": [number],
 *      "mprId": [number],
 *      "probeNehmerId": [number],
 *      "solldatumBeginn": [timestamp],
 *      "solldatumEnde": [timestamp],
 *      "treeModified": [timestamp],
 *      "readonly": [boolean],
 *      "owner": [boolean],
 *      "probeIdAlt": [string]
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
@Path("probe")
@RequestScoped
public class ProbeService {

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
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
    private Authorization authorization;

    /**
     * The object lock mechanism.
     */
    @Inject
    @LockConfig(type=LockType.TIMESTAMP)
    private ObjectLocker lock;

    /**
     * The validator used for Probe objects.
     */
    @Inject
    @ValidationConfig(type="Probe")
    private Validator validator;

    /**
     * The factory to create Probe objects.
     * Used for messprogramm.
     */
    @Inject
    private ProbeFactory factory;

    /**
     * Get all Probe objects.
     * <p>
     * The requested objects can be filtered using the following URL
     * parameters:<br>
     *  * qid: The id of the query.<br>
     *  * page: The page to display in a paginated result grid.<br>
     *  * start: The first Probe item.<br>
     *  * limit: The count of Probe items.<br>
     *  * sort: Sort the result ascending(ASC) or descenting (DESC).<br>
     *  <br>
     *  The response data contains a stripped set of Probe objects. The returned fields
     *  are defined in the query used in the request.
     * <p>
     * Example:
     * http://example.com/probe?qid=[ID]&page=[PAGE]&start=[START]&limit=[LIMIT]&sort=[{"property":"probeId","direction":"ASC"}]
     *
     * @return Response object containing all Probe objects.
     */
    @SuppressWarnings("unchecked")
    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("qid")) {
            return defaultRepo.getAll(LProbe.class, "land");
        }
        String qid = params.getFirst("qid");
        JsonObject jsonQuery = QueryTools.getQueryById(qid);
        String sql = "";
        List<String> filters = new ArrayList<String>();
        List<String> results = new ArrayList<String>();
        try {
            sql = jsonQuery.getString("sql");
            if (params.containsKey("sort")) {
                String sort = params.getFirst("sort");
                logger.debug("Sort parameter: " + sort);
                JsonReader reader = Json.createReader(new StringReader(sort));
                JsonObject sortProperties = reader.readArray().getJsonObject(0);
                sql += " ORDER BY ";
                sql += sortProperties.getJsonString("property").getString() + " ";
                sql += sortProperties.getJsonString("direction").getString();
            }
            JsonArray jsonFilters = jsonQuery.getJsonArray("filters");
            JsonArray jsonResults = jsonQuery.getJsonArray("result");
            for (int i = 0; i < jsonFilters.size(); i++) {
                filters.add(
                    jsonFilters.getJsonObject(i).getString("dataIndex"));
            }
            results.add("id");
            for (int i = 0; i < jsonResults.size(); i++) {
                results.add(
                    jsonResults.getJsonObject(i).getString("dataIndex"));
            }
        }
        catch (JsonException je) {
            return new Response(false, 603, new ArrayList<Object>());
        }
        Query query = QueryTools.prepareQuery(
            sql,
            filters,
            params,
            defaultRepo.entityManager("land"));
        List<Map<String, Object>> result =
            QueryTools.prepareResult(query.getResultList(), results);
        if (params.containsKey("start") && params.containsKey("limit")) {
            int start = Integer.valueOf(params.getFirst("start"));
            int limit = Integer.valueOf(params.getFirst("limit"));
            int end = limit + start;
            if (start + limit > result.size()) {
                end = result.size();
            }
            List<Map<String, Object>> subList = result.subList(start, end);
            for (Map<String, Object> entry: subList) {
                boolean readOnly =
                    authorization.isReadOnly((Integer)entry.get("id"));
                entry.put("readonly", readOnly);
            }
            return new Response(true, 200, subList, result.size());
        }
        return new Response(true, 200, result, result.size());
    }

    /**
     * Get a single Probe object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/probe/{id}
     *
     * @return Response object containing a single Probe.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id,
        @Context HttpServletRequest request
    ) {
        Response response =
            defaultRepo.getById(LProbe.class, Integer.valueOf(id), "land");
        Violation violation = validator.validate(response.getData());
        if (violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }
        return this.authorization.filter(request, response, LProbe.class);
    }

    /**
     * Create a new Probe object.
     * <p>
     * The new object is embedded in the post data as JSON formatted string.
     * <p>
     * <pre>
     * <code>
     * {
     *  "probeIdAlt": [string],
     *  "hauptprobenNr": [string],
     *  "test": [boolean],
     *  "netzbetreiberId": [string],
     *  "mstId": [string],
     *  "datenbasisId": [number],
     *  "baId": [string],
     *  "probenartId": [number],
     *  "mediaDesk": [string],
     *  "media": [string],
     *  "umwId": [string],
     *  "mittelungsdauer": [number],
     *  "erzeugerId":[string],
     *  "probeNehmerId": [number],
     *  "mpKat": [string],
     *  "mplId": [number],
     *  "mprId": [number],
     *  "treeModified":null,
     *  "probeentnahmeBeginn": [date],
     *  "probeentnahmeEnde": [date],
     *  "letzteAenderung": [date],
     *  "solldatumBeginn": [date],
     *  "solldatumEnde": [date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the new probe object.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LProbe probe
    ) {
        if (!authorization.isAuthorized(
                request,
                probe,
                RequestMethod.POST,
                LProbe.class)
        ) {
            return new Response(false, 699, null);
        }
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, probe);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }
        /* Persist the new probe object*/
        Response newProbe = defaultRepo.create(probe, "land");
        LProbe ret = (LProbe)newProbe.getData();
        /* Create and persist a new probe translation object*/
        ProbeTranslation trans = new ProbeTranslation();
        trans.setProbeId(ret);
        defaultRepo.create(trans, "land");
        /* Get and return the new probe object*/
        Response response =
            defaultRepo.getById(LProbe.class, ret.getId(), "land");
        if(violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            response,
            LProbe.class);
    }

    /**
     * Create new Probe objects from a messprogramm.
     * <p>
     * <p>
     * <pre>
     * <code>
     * {
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the new probe object.
     */
    @POST
    @Path("/messprogramm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFromMessprogramm(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        JsonObject object
    ) {
        String id = object.get("id").toString();
        long start = object.getJsonNumber("start").longValue();
        long end = object.getJsonNumber("end").longValue();
        List<LProbe> proben = factory.create(
            id,
            start,
            end);
        return new Response(true, 200, proben);
    }

    /**
     * Update an existing Probe object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     *  "id": [number],
     *  "probeIdAlt": [string],
     *  "hauptprobenNr": [string],
     *  "test": [boolean],
     *  "netzbetreiberId": [string],
     *  "mstId": [string],
     *  "datenbasisId": [number],
     *  "baId": [string],
     *  "probenartId": [number],
     *  "mediaDesk": [string],
     *  "media": [string],
     *  "umwId": [string],
     *  "mittelungsdauer": [number],
     *  "erzeugerId": [number],
     *  "probeNehmerId": [number],
     *  "mpKat": [string],
     *  "mplId": [number],
     *  "mprId": [number],
     *  "treeModified": [timestamp],
     *  "probeentnahmeBeginn": [date],
     *  "probeentnahmeEnde": [date],
     *  "letzteAenderung": [date],
     *  "solldatumBeginn": [date],
     *  "solldatumEnde":[date]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated Probe object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LProbe probe
    ) {
        if (!authorization.isAuthorized(
                request,
                probe,
                RequestMethod.PUT,
                LProbe.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(probe)) {
            return new Response(false, 697, null);
        }
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, null);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }
        probe.setLetzteAenderung(new Timestamp(new Date().getTime()));
        Response response = defaultRepo.update(probe, "land");
        Response updated = defaultRepo.getById(
            LProbe.class,
            ((LProbe)response.getData()).getId(), "land");
        if (violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }
        return authorization.filter(
            request,
            updated,
            LProbe.class);
    }

    /**
     * Delete an existing Probe object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/probe/{id}
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
        /* Get the probe object by id*/
        Response probe =
            defaultRepo.getById(LProbe.class, Integer.valueOf(id), "land");
        LProbe probeObj = (LProbe)probe.getData();
        if (!authorization.isAuthorized(
                request,
                probeObj,
                RequestMethod.DELETE,
                LProbe.class)
        ) {
            return new Response(false, 699, null);
        }
        /* Create a query and request the probetranslation object for the
         * probe*/
        QueryBuilder<ProbeTranslation> builder =
            new QueryBuilder<ProbeTranslation>(
                defaultRepo.entityManager("land"), ProbeTranslation.class);
        builder.and("probe", probeObj.getId());
        Response probeTrans = defaultRepo.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        ProbeTranslation probeTransObj = ((List<ProbeTranslation>)probeTrans.getData()).get(0);
        /* Delete the probe translation object*/
        defaultRepo.delete(probeTransObj, "land");
        /* Delete the probe object*/
        Response response = defaultRepo.delete(probeObj, "land");
        return response;
    }
}
