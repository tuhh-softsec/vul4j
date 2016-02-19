/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import de.intevation.lada.model.stamm.Favorite;
import de.intevation.lada.model.stamm.Filter;
import de.intevation.lada.model.stamm.FilterValue;
import de.intevation.lada.model.stamm.Query;
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
 * REST-Service for preconfigured queries.
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
 *      "id": [string],
 *      "name": [string],
 *      "description": [string],
 *      "sql": [string],
 *      "filters": [array],
 *      "results": [array]
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
@Path("rest/query")
@RequestScoped
public class QueryService {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Request all configured probe queries.
     */
    @GET
    @Path("/probe")
    @Produces("application/json")
    public Response getProbe(
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.and("type", "probe");
        List<Query> queries = repository.filterPlain(builder.getQuery(), "stamm");

        markFavorites(queries, userInfo);

        setFilterValues(queries, 0);
        setFilterValues(queries, userInfo.getUserId());

        return new Response(true, 200, queries);
    }

    /**
     * Request all configured messprogramm queries.
     */
    @GET
    @Path("/messprogramm")
    @Produces("application/json")
    public Response getMessprogramm(
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.and("type", "messprogramm");
        List<Query> queries = repository.filterPlain(builder.getQuery(), "stamm");

        markFavorites(queries, userInfo);

        return new Response(true, 200, queries);
    }

    /**
     * Request all configured stammdaten queries.
     */
    @GET
    @Path("/stammdaten")
    @Produces("application/json")
    public Response getStammdaten(
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.or("type", "ort");
        builder.or("type", "probenehmer");
        builder.or("type", "datensatzerzeuger");
        builder.or("type", "messprogrammkategorie");
        List<Query> queries = repository.filterPlain(builder.getQuery(), "stamm");

        markFavorites(queries, userInfo);

        return new Response(true, 200, queries);
    }

    private void markFavorites(List<Query> queries, UserInfo userInfo) {
        QueryBuilder<Favorite> fBuilder = new QueryBuilder<Favorite>(
            repository.entityManager("stamm"),
            Favorite.class
        );
        fBuilder.and("userId", userInfo.getUserId());
        List<Favorite> favorites = repository.filterPlain(fBuilder.getQuery(), "stamm");
        for (Favorite f : favorites) {
            for (Query q : queries) {
                if (q.getId().equals(f.getQueryId())) {
                    q.setFavorite(true);
                }
            }
        }
    }

    private void setFilterValues(List<Query> queries, Integer userId) {
        QueryBuilder<FilterValue> builder = new QueryBuilder<FilterValue>(
            repository.entityManager("stamm"),
            FilterValue.class
        );
        builder.and("userId", userId);
        for (Query q : queries) {
            List<FilterValue> values = repository.filterPlain(builder.getQuery(), "stamm");
            for (Filter f : q.getFilters()) {
                for (FilterValue fv : values) {
                    if (fv.getFilterId().equals(f.getId())) {
                        f.setValue(fv.getValue());
                    }
                }
            }
            builder = builder.getEmptyBuilder();
            builder.and("userId", userId);
        }
    }
}
