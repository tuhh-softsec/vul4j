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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;

import org.hibernate.sql.JoinType;

import de.intevation.lada.model.stammdaten.Favorite;
import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.FilterValue;
import de.intevation.lada.model.stammdaten.GridColumn;
import de.intevation.lada.model.stammdaten.GridColumnValue;
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
@Path("rest/column")
@RequestScoped
public class ColumnService {

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
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
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
        UserInfo userInfo = authorization.getInfo(request);
        EntityManager em = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<GridColumn> criteriaQuery = builder.createQuery(GridColumn.class);
        Root<GridColumn> root = criteriaQuery.from(GridColumn.class);
        Join<GridColumnValue, GridColumn> value = root.join("gridColumnValue", javax.persistence.criteria.JoinType.LEFT);
        Predicate filter = builder.equal(root.get("query"), id);
        Predicate uId = builder.equal(value.get("userId"), userInfo.getUserId());
        Predicate nullId = builder.isNull(value.get("userId"));
        Predicate userIdFilter = builder.or(uId, nullId);
        filter = builder.and(filter, userIdFilter);
        criteriaQuery.where(filter);
        
        List<GridColumn> queries = repository.filterPlain(criteriaQuery, Strings.STAMM);

        return new Response(true, 200, queries);
    }
}
