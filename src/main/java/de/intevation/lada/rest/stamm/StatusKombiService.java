/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.stammdaten.StatusErreichbar;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for StatusKombi objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [number],
 *      "stufeId": [number],
 *      "wertId": [number]
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
@Path("rest/statuskombi")
@RequestScoped
public class StatusKombiService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type = AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all StatusKombi objects.
     * <p>
     * Example: http://example.com/statuskombi
     *
     * @return Response object containing all StatusStufe objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        return repository.getAll(StatusKombi.class, Strings.STAMM);
    }

    /**
     * Get a single StatusStufe object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/statusstufe/{id}
     *
     * @return Response object containing a single StatusStufe.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return repository.getById(
            StatusKombi.class,
            Integer.valueOf(id),
            Strings.STAMM);
    }

    @POST
    @Path("/getbyids")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpServletRequest request,
        JsonArray ids
    ) {
        UserInfo user = authorization.getInfo(request);
        List<JsonNumber> idList = ids.getValuesAs(JsonNumber.class);
        List<Integer> intList = new ArrayList<>();
        for (JsonNumber id : idList) {
            intList.add(id.intValue());
        }
        return new Response(true, StatusCodes.OK, getReachable(intList, user));
    }

    /**
     * Get the list of possible status values following the actual status
     * values of the Messungen represented by the given IDs.
     *
     * @return Disjunction of possible status values for all Messungen
     */
    private List<StatusKombi> getReachable(
        List<Integer> messIds,
        UserInfo user
    ) {
        List<StatusKombi> list = new ArrayList<StatusKombi>();

        QueryBuilder<Messung> messungQuery = new QueryBuilder<Messung>(
            repository.entityManager(Strings.LAND),
            Messung.class);
        messungQuery.orIn("id", messIds);
        List<Messung> messungen = repository.filterPlain(
            messungQuery.getQuery(), Strings.LAND);

        Map<Integer, StatusErreichbar> erreichbare =
            new HashMap<Integer, StatusErreichbar>();
        for (Messung messung : messungen) {
            StatusProtokoll status = repository.getByIdPlain(
                StatusProtokoll.class, messung.getStatus(), Strings.LAND);
            StatusKombi kombi = repository.getByIdPlain(
                StatusKombi.class, status.getStatusKombi(), Strings.STAMM);

            QueryBuilder<StatusErreichbar> errFilter =
                new QueryBuilder<StatusErreichbar>(
                    repository.entityManager(Strings.STAMM),
                    StatusErreichbar.class);
            errFilter.andIn("stufeId", user.getFunktionen());
            errFilter.and("curStufe", kombi.getStatusStufe().getId());
            errFilter.and("curWert", kombi.getStatusWert().getId());
            List<StatusErreichbar> err = repository.filterPlain(
                    errFilter.getQuery(), Strings.STAMM);
            for (StatusErreichbar e : err) {
                erreichbare.put(e.getId(), e);
            }
        }

        if (erreichbare.size() == 0) {
            return new ArrayList<StatusKombi>();
        }

        QueryBuilder<StatusKombi> kombiFilter =
            new QueryBuilder<StatusKombi>(
                repository.entityManager(Strings.STAMM),
                StatusKombi.class);
        for (Entry<Integer, StatusErreichbar> erreichbar
            : erreichbare.entrySet()
        ) {
                QueryBuilder<StatusKombi> tmp = kombiFilter.getEmptyBuilder();
                tmp.and("statusWert", erreichbar.getValue().getWertId())
                    .and("statusStufe", erreichbar.getValue().getStufeId());
                kombiFilter.or(tmp);
        }

        list = repository.filterPlain(kombiFilter.getQuery(), Strings.STAMM);
        return list;
    }
}
