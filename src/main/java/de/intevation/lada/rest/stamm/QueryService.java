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
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;

import org.hibernate.sql.JoinType;

import de.intevation.lada.model.stammdaten.Favorite;
import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.FilterValue;
import de.intevation.lada.model.stammdaten.NetzBetreiber;
import de.intevation.lada.model.stammdaten.Query;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
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
    @Path("/")
    @Produces("application/json")
    public Response getQueries(
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        EntityManager em = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Query> criteriaQuery = builder.createQuery(Query.class);
        Root<Query> root = criteriaQuery.from(Query.class);
        Join<NetzBetreiber, Query> netz = root.join("netzBetreibers", javax.persistence.criteria.JoinType.LEFT);
        Predicate filter = builder.equal(root.get("owner"), userInfo.getUserId());
        filter = builder.or(filter, netz.get("id").in(userInfo.getNetzbetreiber()));
        criteriaQuery.where(filter);
        
        List<Query> queries = repository.filterPlain(criteriaQuery, Strings.STAMM);

        return new Response(true, 200, queries);
    }

    private void setFilterValues(List<Query> queries, Integer userId) {
        /*
        QueryBuilder<FilterValue> builder = new QueryBuilder<FilterValue>(
            repository.entityManager(Strings.STAMM),
            FilterValue.class
        );
        builder.and("userId", userId);
        for (Query q : queries) {
            List<FilterValue> values = repository.filterPlain(builder.getQuery(), Strings.STAMM);
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
        */
    }
}
