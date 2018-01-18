/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TransactionRequiredException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stammdaten.Favorite;
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
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("rest/favorite")
@RequestScoped
public class FavoriteService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        Favorite favorite
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        favorite.setUserId(userInfo.getUserId());
        QueryBuilder<Favorite> builder = new QueryBuilder<Favorite>(
            repository.entityManager(Strings.STAMM),
            Favorite.class
        );
        builder.and("userId", userInfo.getUserId());
        builder.and("queryId", favorite.getQueryId());
        List<Favorite> favorites = repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (favorites.isEmpty()) {
            return repository.create(favorite, Strings.STAMM);
        }
        return new Response(false, 617, "exists");
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);

        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("queryId")) {
            return new Response(false, 618, "missing queryId parameter");
        }
        QueryBuilder<Favorite> builder = new QueryBuilder<Favorite>(
            repository.entityManager(Strings.STAMM),
            Favorite.class
        );
        builder.and("userId", userInfo.getUserId());
        builder.and("queryId", params.getFirst("queryId"));

        List<Favorite> fs = repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (fs == null || fs.isEmpty()) {
            return new Response(false, 600, "not existing");
        }
        /* Delete the object*/
        try {
            Response response = repository.delete(fs.get(0), Strings.STAMM);
            return response;
        }
        catch(IllegalArgumentException | EJBTransactionRolledbackException |
            TransactionRequiredException e) {
            return new Response(false, 600, "");
        }
    }

}
