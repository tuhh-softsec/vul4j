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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.query.QueryTools;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for Messprogramm objects.
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
@Path("messprogramm")
@RequestScoped
public class MessprogrammService {

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
     * Get all Messprogramm objects.
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
     * @return Response object containing all Messprogramm objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("qid")) {
            return defaultRepo.getAll(Messprogramm.class, "land");
        }
        String qid = params.getFirst("qid");
        JsonObject jsonQuery = QueryTools.getMpQueryById(qid);
        String sql = "";
        List<String> filters = new ArrayList<String>();
        List<String> results = new ArrayList<String>();
        try {
            sql = jsonQuery.getString("sql");
            if (params.containsKey("sort")) {
                String sort = params.getFirst("sort");
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
        @SuppressWarnings("unchecked")
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
            return new Response(true, 200, subList, result.size());
        }
        return new Response(true, 200, result, result.size());
    }

    /**
     * Get a Messprogramm object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messprogramm/{id}
     *
     * @return Response object containing a single Messprogramm.
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
            defaultRepo.getById(Messprogramm.class, Integer.valueOf(id), "land"),
            Messprogramm.class);
    }

    /**
     * Create a Messprogramm object.
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
     * @return A response object containing the created Messprogramm.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        Messprogramm messprogramm
    ) {
        if (!authorization.isAuthorized(
                request,
                messprogramm,
                RequestMethod.POST,
                Messprogramm.class)
        ) {
            return new Response(false, 699, null);
        }

        /* Persist the new messprogramm object*/
        Response response = defaultRepo.create(messprogramm, "land");
        Messprogramm ret = (Messprogramm)response.getData();
        Response created =
            defaultRepo.getById(Messprogramm.class, ret.getId(), "land");
        return authorization.filter(
            request,
            new Response(true, 200, created.getData()),
            Messprogramm.class);
    }

    /**
     * Update an existing Messprogramm object.
     * <p>
     * The object to update should come as JSON formatted string.
     * <pre>
     * <code>
     * {
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing the updated Messprogramm object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        Messprogramm messprogramm
    ) {
        if (!authorization.isAuthorized(
                request,
                messprogramm,
                RequestMethod.PUT,
                Messprogramm.class)
        ) {
            return new Response(false, 699, null);
        }
        messprogramm.setLetzteAenderung(new Timestamp(new Date().getTime()));
        Response response = defaultRepo.update(messprogramm, "land");
        Response updated = defaultRepo.getById(
            Messprogramm.class,
            ((Messprogramm)response.getData()).getId(), "land");
        return authorization.filter(
            request,
            updated,
            Messprogramm.class);
    }

    /**
     * Delete an existing Messprogramm object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messprogamm/{id}
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
        /* Get the messung object by id*/
        Response messprogramm =
            defaultRepo.getById(Messprogramm.class, Integer.valueOf(id), "land");
        Messprogramm messprogrammObj = (Messprogramm)messprogramm.getData();
        if (!authorization.isAuthorized(
                request,
                messprogrammObj,
                RequestMethod.DELETE,
                Messprogramm.class)
        ) {
            return new Response(false, 699, null);
        }
        /* Delete the messprogramm object*/
        Response response = defaultRepo.delete(messprogrammObj, "land");
        return response;
    }
}
