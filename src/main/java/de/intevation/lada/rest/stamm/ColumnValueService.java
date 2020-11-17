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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;


import de.intevation.lada.model.stammdaten.GridColumn;
import de.intevation.lada.model.stammdaten.GridColumnValue;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.QueryUser;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;


/**
 * REST-Service for user defined columns.
 * <p>
 * The services produce data in the application/json media type.
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("rest/columnvalue")
@RequestScoped
public class ColumnValueService {

    @Inject
    @RepositoryConfig(type = RepositoryType.RW)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type = AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Request all user defined grid_column_value objects
     * @return All GridColumnValue objects referencing the given query.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
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
        } catch (NumberFormatException e) {
            return new Response(false, 603, "Not a valid filter id");
        }
        UserInfo userInfo = authorization.getInfo(request);
        EntityManager em = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<GridColumnValue> criteriaQuery =
            builder.createQuery(GridColumnValue.class);
        Root<GridColumnValue> root = criteriaQuery.from(GridColumnValue.class);
        Join<GridColumnValue, QueryUser> value =
            root.join("queryUser", javax.persistence.criteria.JoinType.LEFT);
        Join<MessStelle, QueryUser> mess =
            value.join("messStelles", javax.persistence.criteria.JoinType.LEFT);
        Predicate filter = builder.equal(root.get("queryUser"), id);
        Predicate uId = builder.equal(root.get("userId"), userInfo.getUserId());
        Predicate zeroIdFilter = builder.equal(root.get("userId"), "0");
        Predicate userFilter = builder.or(uId, zeroIdFilter);
        if (userInfo.getMessstellen() != null
            && !userInfo.getMessstellen().isEmpty()
        ) {
            userFilter = builder.or(
                userFilter,
                mess.get("messStelle").in(userInfo.getMessstellen()));
        }
        if (userInfo.getLaborMessstellen() != null
            && !userInfo.getLaborMessstellen().isEmpty()
        ) {
            userFilter = builder.or(
                userFilter,
                mess.get("messStelle").in(userInfo.getLaborMessstellen()));
        }
        filter = builder.and(filter, userFilter);
        criteriaQuery.where(filter).distinct(true);
        List<GridColumnValue> queries =
            repository.filterPlain(criteriaQuery, Strings.STAMM);

        for (GridColumnValue gcv : queries) {
            gcv.setgridColumnId(gcv.getGridColumn().getId());
            gcv.setQueryUserId(gcv.getQueryUser().getId());
        }

        return new Response(true, 200, queries);
    }

    /**
     * Creates a new grid_column_value in the database
     * @return Response containing the created record.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        GridColumnValue gridColumnValue
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        if (gridColumnValue.getUserId() != null
            && !gridColumnValue.getUserId().equals(userInfo.getUserId())
        ) {
                return new Response(false, 699, null);
        } else {
            gridColumnValue.setUserId(userInfo.getUserId());
            GridColumn gridColumn = new GridColumn();
            gridColumn.setId(gridColumnValue.getGridColumnId());
            gridColumnValue.setGridColumn(gridColumn);


            QueryUser queryUser = repository.getByIdPlain(
                QueryUser.class,
                gridColumnValue.getQueryUserId(),
                Strings.STAMM);
            gridColumnValue.setQueryUser(queryUser);

            return repository.create(gridColumnValue, Strings.STAMM);
        }

    }

    /**
     * Update an existing grid_column_value in the database
     * @return Response containing the updated record.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        GridColumnValue gridColumnValue
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        if (gridColumnValue.getUserId() != null
            && !gridColumnValue.getUserId().equals(userInfo.getUserId())
        ) {
                return new Response(false, 699, null);
        } else {
            gridColumnValue.setUserId(userInfo.getUserId());

            GridColumn gridColumn = repository.getByIdPlain(
                GridColumn.class,
                 gridColumnValue.getGridColumnId(),
                 Strings.STAMM);
            gridColumnValue.setGridColumn(gridColumn);

            QueryUser queryUser = repository.getByIdPlain(
                QueryUser.class,
                gridColumnValue.getQueryUserId(),
                Strings.STAMM);

            gridColumnValue.setQueryUser(queryUser);

            return repository.update(gridColumnValue, Strings.STAMM);
        }
    }

    /**
     * Delete the given column.
     * @return Response containing the deleted record.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        GridColumnValue gridColumnValue = repository.getByIdPlain(
            GridColumnValue.class,
            Integer.valueOf(id),
            Strings.STAMM);
        if (gridColumnValue.getUserId().equals(userInfo.getUserId())) {
            return repository.delete(gridColumnValue, Strings.STAMM);
        }
        return new Response(false, 699, null);
    }
}
