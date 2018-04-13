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
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.FilterValue;
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

@Path("rest/filter")
@RequestScoped
public class FilterService {

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        Filter filter
    ) {
        /*
        UserInfo userInfo = authorization.getInfo(request);
        String value = filter.getValue();
        Filter f = repository.getByIdPlain(Filter.class, filter.getId(), Strings.STAMM);
        QueryBuilder<FilterValue> builder = new QueryBuilder<FilterValue>(
            repository.entityManager(Strings.STAMM),
            FilterValue.class
        );
        builder.and("userId", userInfo.getUserId());
        builder.and("filterId", f.getId());
        List<FilterValue> values = repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (values == null || values.isEmpty()) {
            FilterValue newValue = new FilterValue();
            newValue.setFilterId(f.getId());
            newValue.setUserId(userInfo.getUserId());
            newValue.setValue(value);
            repository.create(newValue, Strings.STAMM);
            f.setValue(value);
            return new Response(true, 200, f);
        }
        else {
            FilterValue fv = values.get(0);
            fv.setValue(value);
            repository.update(fv, Strings.STAMM);
            f.setValue(value);
            return new Response(true, 200, f);
        }
        */
        return new Response(true, 200, null);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        /*
        UserInfo userInfo = authorization.getInfo(request);
        Integer fId = Integer.valueOf(id);
        Filter f = repository.getByIdPlain(Filter.class, fId, Strings.STAMM);
        QueryBuilder<FilterValue> builder = new QueryBuilder<FilterValue>(
            repository.entityManager(Strings.STAMM),
            FilterValue.class
        );
        builder.and("userId", userInfo.getUserId());
        builder.and("filterId", f.getId());
        List<FilterValue> values = repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (values == null || values.isEmpty()) {
            return new Response(false, 618, "not existing");
        }
        repository.delete(values.get(0), Strings.STAMM);
        QueryBuilder<FilterValue> fvBuilder = builder.getEmptyBuilder();
        fvBuilder.and("userId", 0);
        fvBuilder.and("filterId", f.getId());
        List<FilterValue> basicValues = repository.filterPlain(fvBuilder.getQuery(), Strings.STAMM);
        if (basicValues == null || basicValues.isEmpty()) {
            f.setValue(null);
            return new Response(true, 200, f);
        }
        f.setValue(basicValues.get(0).getValue());
        return new Response(true, 200, f);
        */
        return new Response(true, 200, null);
    }
}
