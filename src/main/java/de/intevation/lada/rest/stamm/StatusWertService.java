/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import de.intevation.lada.model.bund.StatusProtokoll;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.stamm.StatusErreichbar;
import de.intevation.lada.model.stamm.StatusKombi;
import de.intevation.lada.model.stamm.StatusReihenfolge;
import de.intevation.lada.model.stamm.StatusWert;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for StatusWert objects.
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
 *      "wert": [string],
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
@Path("rest/statuswert")
@RequestScoped
public class StatusWertService {

    @Inject
    private Logger logger = Logger.getLogger(StatusWertService.class);
    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository defaultRepo;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all StatusWert objects.
     * <p>
     * Example: http://example.com/statuwert
     *
     * @return Response object containing all StatusWert objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("messungsId")) {
            return defaultRepo.getAll(StatusWert.class, "stamm");
        }
        int messungsId = Integer.valueOf(params.getFirst("messungsId"));
        UserInfo user = authorization.getInfo(request);
        List<StatusWert> werte = getReachable(messungsId, user);
        Response response = new Response(true, 200, werte);
        return response;
    }

    /**
     * Get a single StatusWert object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/statuswert/{id}
     *
     * @return Response object containing a single StatusWert.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return defaultRepo.getById(
            StatusWert.class,
            Integer.valueOf(id),
            "stamm");
    }

    private List<StatusWert> getReachable(int messungsId, UserInfo user) {
        List<StatusWert> list = new ArrayList<StatusWert>();
        LMessung messung =
            defaultRepo.getByIdPlain(LMessung.class, messungsId, "land");
        if (messung.getStatus() == null) {
            return defaultRepo.getAllPlain(StatusWert.class, "stamm");
        }
        StatusProtokoll status = defaultRepo.getByIdPlain(
            StatusProtokoll.class,
            messung.getStatus(),
            "land");

        QueryBuilder<StatusErreichbar> errFilter =
            new QueryBuilder<StatusErreichbar>(
                defaultRepo.entityManager("stamm"),
                StatusErreichbar.class);
        errFilter.andIn("stufeId", user.getFunktionen());
        errFilter.and("curStufe", status.getStatusStufe());
        errFilter.and("curWert", status.getStatusWert());
        List<StatusErreichbar> erreichbare = defaultRepo.filterPlain(
            errFilter.getQuery(), "stamm");
        QueryBuilder<StatusWert> werteFilter =
            new QueryBuilder<StatusWert>(
                defaultRepo.entityManager("stamm"),
                StatusWert.class);
        for (int i = 0; i < erreichbare.size(); i++) {
            werteFilter.or("id", erreichbare.get(i).getWertId());
        }
        list = defaultRepo.filterPlain(werteFilter.getQuery(), "stamm");
        return list;
    }
}
