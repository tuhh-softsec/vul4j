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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stammdaten.DatensatzErzeuger;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for DatensatzErzeuger objects.
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
 *      "bezeichnung": [string],
 *      "daErzeugerId": [string],
 *      "letzteAenderung": [timestamp],
 *      "mstId": [string],
 *      "netzbetreiberId": [string]
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
@Path("rest/datensatzerzeuger")
@RequestScoped
public class DatensatzErzeugerService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RW)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type = AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all Datenbasis objects.
     * <p>
     * Example: http://example.com/datenbasis
     *
     * @return Response object containing all Datenbasis objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        List<DatensatzErzeuger> erzeuger =
            repository.getAllPlain(DatensatzErzeuger.class, Strings.STAMM);

        for (DatensatzErzeuger erz : erzeuger) {
            erz.setReadonly(
                !authorization.isAuthorized(
                    request,
                    erz,
                    RequestMethod.POST,
                    DatensatzErzeuger.class));
        }
        return new Response(true, StatusCodes.OK, erzeuger, erzeuger.size());
    }

    /**
     * Get a single Datenbasis object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/datenbasis/{id}
     *
     * @return Response object containing a single Datenabasis.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        DatensatzErzeuger erzeuger = repository.getByIdPlain(
            DatensatzErzeuger.class,
            Integer.valueOf(id),
            Strings.STAMM
        );
        erzeuger.setReadonly(
            !authorization.isAuthorized(
                request,
                erzeuger,
                RequestMethod.POST,
                DatensatzErzeuger.class
            )
        );
        return new Response(true, StatusCodes.OK, erzeuger);
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        DatensatzErzeuger datensatzerzeuger
    ) {
        if (!authorization.isAuthorized(
            request,
            datensatzerzeuger,
            RequestMethod.POST,
            DatensatzErzeuger.class)
        ) {
            return new Response(
                false, StatusCodes.NOT_ALLOWED, datensatzerzeuger);
        }
        QueryBuilder<DatensatzErzeuger> builder =
            new QueryBuilder<DatensatzErzeuger>(
                repository.entityManager(Strings.STAMM),
                DatensatzErzeuger.class
            );
        builder.and(
            "datensatzErzeugerId", datensatzerzeuger.getDatensatzErzeugerId());
        builder.and("netzbetreiberId", datensatzerzeuger.getNetzbetreiberId());

        List<DatensatzErzeuger> erzeuger =
            repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (erzeuger.isEmpty()) {
            return repository.create(datensatzerzeuger, Strings.STAMM);
        }
        return new Response(false, StatusCodes.IMP_DUPLICATE, null);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        @PathParam("id") String id,
        DatensatzErzeuger datensatzerzeuger
    ) {
        if (!authorization.isAuthorized(
            request,
            datensatzerzeuger,
            RequestMethod.PUT,
            DatensatzErzeuger.class)
        ) {
            return new Response(
                false, StatusCodes.NOT_ALLOWED, datensatzerzeuger);
        }
        return repository.update(datensatzerzeuger, Strings.STAMM);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        DatensatzErzeuger datensatzerzeuger = repository.getByIdPlain(
            DatensatzErzeuger.class, Integer.valueOf(id), Strings.STAMM);
        if (datensatzerzeuger == null
            || !authorization.isAuthorized(
                request,
                datensatzerzeuger,
                RequestMethod.DELETE,
                DatensatzErzeuger.class
            )
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        return repository.delete(datensatzerzeuger, Strings.STAMM);
    }
}
