/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.QueryMessstelle;
import de.intevation.lada.model.stammdaten.QueryUser;
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
    @RepositoryConfig(type = RepositoryType.RW)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type = AuthorizationType.HEADER)
    private Authorization authorization;

    @Inject
    Logger logger;

    static final Integer DEFAULT_USER_ID = 0;

    /**
     * Request all queries (query_user table).
     * @return All queries owned by the user, connected to the user's
     *         messstelle or owned by the default user.
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
        CriteriaQuery<QueryUser> criteriaQuery =
            builder.createQuery(QueryUser.class);
        Root<QueryUser> root = criteriaQuery.from(QueryUser.class);
        Join<MessStelle, QueryUser> mess =
            root.join("messStelles", javax.persistence.criteria.JoinType.LEFT);
        Predicate filter =
            builder.equal(root.get("userId"), userInfo.getUserId());
        filter = builder.or(filter, root.get("userId").in(DEFAULT_USER_ID));
        if (userInfo.getMessstellen() != null
            && !userInfo.getMessstellen().isEmpty()
        ) {
            filter = builder.or(
                filter, mess.get("messStelle").in(userInfo.getMessstellen()));
        }
        criteriaQuery.where(filter);

        List<QueryUser> queries =
            repository.filterPlain(criteriaQuery, Strings.STAMM);
        for (QueryUser query: queries) {
            if (query.getMessStelles() != null
                && query.getMessStelles().size() > 0
            ) {
                List<String> mstIds = new ArrayList<String>();
                for (QueryMessstelle mst: query.getMessStelles()) {
                    mstIds.add(mst.getMessStelle());
                }
                query.setMessStellesIds(
                    mstIds.toArray(new String[mstIds.size()]));
            }
        }
        return new Response(true, 200, queries);
    }

    /**
     * Create a new query_user object in the database.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        QueryUser query
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        if (query.getUserId() != null
            && !query.getUserId().equals(userInfo.getUserId())
        ) {
            return new Response(false, 699, null);
        } else {
            query.setUserId(userInfo.getUserId());
            for (String m : query.getMessStellesIds()) {
                QueryMessstelle qms = new QueryMessstelle();
                qms.setMessStelle(m);
                qms.setQueryUser(query);
                query.addMessStelle(qms);
            }
            return repository.create(query, Strings.STAMM);
        }
    }

    /**
     * Update an existing query_user object in the database
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        QueryUser query
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        if (query.getUserId() != null
            && !query.getUserId().equals(userInfo.getUserId())
        ) {
            return new Response(false, 699, null);
        }

        query.setUserId(userInfo.getUserId());
        QueryBuilder<QueryMessstelle> builder = new QueryBuilder<>(
                repository.entityManager(Strings.STAMM),
                QueryMessstelle.class);
        builder.and("queryUser", query.getId());
        List<QueryMessstelle> qms =
            repository.filterPlain(builder.getQuery(), Strings.STAMM);
        List<QueryMessstelle> delete = new ArrayList<>();
        List<String> create = new ArrayList<>();
        for (String mst : query.getMessStellesIds()) {
            boolean hit = false;
            for (QueryMessstelle qm : qms) {
                if (mst.equals(qm.getMessStelle())) {
                    hit = true;
                }
            }
            if (!hit) {
                create.add(mst);
            }
        }
        for (QueryMessstelle qm : qms) {
            boolean hit = false;
            for (String mst : query.getMessStellesIds()) {
                if (mst.equals(qm.getMessStelle())) {
                    hit = true;
                }
            }
            if (!hit) {
                delete.add(qm);
            }
        }
        List<QueryMessstelle> dbMesstelles =
            repository.getByIdPlain(
                QueryUser.class, query.getId(), Strings.STAMM).getMessStelles();
        query.setMessStelles(dbMesstelles);

        for (QueryMessstelle qm : delete) {
            Iterator<QueryMessstelle> qmIter =
                query.getMessStelles().iterator();
            while (qmIter.hasNext()) {
                QueryMessstelle qmi = qmIter.next();
                if (qmi.getId().equals(qm.getId())) {
                    repository.delete(qmi, Strings.STAMM);
                    qmIter.remove();
                    break;
                }
            }
        }
        for (String mst : create) {
            QueryMessstelle qm = new QueryMessstelle();
            qm.setMessStelle(mst);
            qm.setQueryUser(query);
            query.addMessStelle(qm);
        }

        return repository.update(query, Strings.STAMM);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        QueryUser query = repository.getByIdPlain(
            QueryUser.class,
            Integer.valueOf(id),
            Strings.STAMM);
        if (query.getUserId().equals(userInfo.getUserId())) {
            return repository.delete(query, Strings.STAMM);
        }
        return new Response(false, 699, null);
    }
}
