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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.QueryColumns;
import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.stammdaten.DatensatzErzeuger;
import de.intevation.lada.model.stammdaten.GridColumn;
import de.intevation.lada.model.stammdaten.GridColumnValue;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.model.stammdaten.ResultType;
import de.intevation.lada.query.QueryTools;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for universal objects.
 * <p>
 * The services produce data in the application/json media type.
 * All HTTP methods use the authorization module to determine if the user is
 * allowed to perform the requested action.
 * </p>
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("rest/universal")
@RequestScoped
public class UniversalService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    /**
     * The header authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    @Inject
    private QueryTools queryTools;


    /**
     * Execute query, using the given result columns.
     * The query can contain the following post data:
     * <pre>
     * <code>
     * {
     *   columns[{
     *     gridColumnId: [number],
     *     sort: [string],
     *     sortIndex: [number],
     *     filterValue: [string],
     *     filterActive: [boolean],
     *   }]
     * }
     * </code>
     * </pre>
     * @return JSON encoded query results
     */
    @POST
    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public Response execute(
        @Context HttpServletRequest request,
        @Context UriInfo info,
        QueryColumns columns
    ) {
        Integer qid;
        MultivaluedMap<String, String> params = info.getQueryParameters();
        List<GridColumnValue> gridColumnValues= columns.getColumns();

        String authorizationColumnIndex = null;
        Class<?> authorizationColumnType = null;
        if (gridColumnValues == null ||
                gridColumnValues.isEmpty()) {
            //TODO: Error code if no columns are given
            return new Response(false, 999, null);
        }
        ArrayList<String> hierarchy = new ArrayList<String>();
        hierarchy.add("messungId");
        hierarchy.add("probeId");
        hierarchy.add("mpId");
        hierarchy.add("ortId");
        hierarchy.add("probenehmer");
        hierarchy.add("dsatzerz");
        hierarchy.add("mprkat");
        int resultNdx = hierarchy.size();
        String authType = "";
        for (GridColumnValue columnValue : gridColumnValues) {
            GridColumn gridColumn= repository.getByIdPlain(
                GridColumn.class,
                Integer.valueOf(columnValue.getGridColumnId()),
                Strings.STAMM);
            //Check if column can be used for authorization
            ResultType resultType = repository.getByIdPlain(ResultType.class, gridColumn.getDataType().getId(), Strings.STAMM);
            if (resultType != null) {
                int ndx = hierarchy.indexOf(resultType.getName());
                if (ndx > -1 && ndx < resultNdx) {
                    resultNdx = ndx;
                    authorizationColumnIndex = gridColumn.getDataIndex();
                    authType = resultType.getName();
                }
            }
            columnValue.setGridColumn(gridColumn);
        }

        switch(authType) {
            case "probeId":
                authorizationColumnType =  de.intevation.lada.model.land.Probe.class;
                break;
            case "messungId":
                authorizationColumnType =  de.intevation.lada.model.land.Messung.class;
                break;
            case "mpId":
                authorizationColumnType = Messprogramm.class;
                break;
            case "ortId":
                authorizationColumnType = Ort.class;
                break;
            case "probenehmer":
                authorizationColumnType = Probenehmer.class;
                break;
            case "dsatzerz":
                authorizationColumnType = DatensatzErzeuger.class;
                break;
            case "mprkat":
                authorizationColumnType = MessprogrammKategorie.class;
                break;
        }

        GridColumn gridColumn = repository.getByIdPlain(
            GridColumn.class,
            Integer.valueOf(gridColumnValues.get(0).getGridColumnId()),
        Strings.STAMM);

        qid = gridColumn.getBaseQuery();
        List<Map<String, Object>> result =
            queryTools.getResultForQuery(columns.getColumns(), qid);
        if (result == null) {
            return new Response(true, 200, null);
        }
        for (Map<String, Object> row: result) {
            Object idToAuthorize = row.get(authorizationColumnIndex);
            boolean readonly;

            if (idToAuthorize != null){
                //If column is an ort, get Netzbetreiberid
                if (authorizationColumnType == Ort.class) {
                    Ort ort = (Ort) repository.getByIdPlain(
                        Ort.class,
                        idToAuthorize,
                        Strings.STAMM);
                    idToAuthorize = ort.getNetzbetreiberId();
                }
                if (authorizationColumnType == DatensatzErzeuger.class) {
                    DatensatzErzeuger de = (DatensatzErzeuger) repository.getByIdPlain(
                        DatensatzErzeuger.class,
                        idToAuthorize,
                        Strings.STAMM);
                    idToAuthorize = de.getNetzbetreiberId();
                }
                if (authorizationColumnType == Probenehmer.class) {
                    Probenehmer pn = (Probenehmer) repository.getByIdPlain(
                        Probenehmer.class,
                        idToAuthorize,
                        Strings.STAMM);
                    idToAuthorize = pn.getNetzbetreiberId();
                }
                if (authorizationColumnType == MessprogrammKategorie.class) {
                    MessprogrammKategorie mk = (MessprogrammKategorie) repository.getByIdPlain(
                        MessprogrammKategorie.class,
                        idToAuthorize,
                        Strings.STAMM);
                    idToAuthorize = mk.getNetzbetreiberId();
                }

                readonly = !authorization.isAuthorizedById(
                    request,
                    idToAuthorize,
                    RequestMethod.POST,
                    authorizationColumnType);
            } else {
                readonly = true;
            }
            row.put("readonly", readonly);

        }
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

        return new Response(true, 200, result, size);
    }

    /**
     * Get all objects.
     * <p>
     * The requested objects can be filtered using the following URL
     * parameters:<br>
     *  * qid: The id of the query.<br>
     *  * page: The page to display in a paginated result grid.<br>
     *  * start: The first Probe item.<br>
     *  * limit: The count of Probe items.<br>
     *  * sort: Sort the result ascending(ASC) or descenting (DESC).<br>
     *  <br>
     *  The response data contains a set of objects. The returned fields
     *  are defined in the query used in the request.
     * <p>
     * Example:
     *
     * @return Response object containing all Probe objects.
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        /*

        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("qid")) {
            return new Response(false, 603, "Not a valid filter id");
        }
        
        Integer id = null;
        try {
            id = Integer.valueOf(params.getFirst("qid"));
        }
        catch (NumberFormatException e) {
            return new Response(false, 603, "Not a valid filter id");
        }
        List<Map<String, Object>> result =
            queryTools.getResultForQuery(params, id);

        List<Map<String, Object>> filtered;
        if (params.containsKey("filter")) {
            filtered = queryTools.filterResult(params.getFirst("filter"), result);
        }
        else {
            filtered = result;
        }

        if (filtered.isEmpty()) {
            return new Response(true, 200, filtered, 0);
        }

        int size = filtered.size();
        if (params.containsKey("start") && params.containsKey("limit")) {
            int start = Integer.valueOf(params.getFirst("start"));
            int limit = Integer.valueOf(params.getFirst("limit"));
            int end = limit + start;
            if (start + limit > filtered.size()) {
                end = filtered.size();
            }
            filtered = filtered.subList(start, end);
        }

        return new Response(true, 200, filtered, size);
        */
        return new Response(true, 200, null);
    }
}
