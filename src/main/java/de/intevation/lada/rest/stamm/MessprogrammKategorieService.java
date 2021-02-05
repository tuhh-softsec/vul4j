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

import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
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
@Path("rest/messprogrammkategorie")
@RequestScoped
public class MessprogrammKategorieService {

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
     * Example: http://example.com/messprogrammkategorie
     *
     * @return Response object containing all objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        List<MessprogrammKategorie> kategorie =
            repository.getAllPlain(MessprogrammKategorie.class, Strings.STAMM);
        for (MessprogrammKategorie kat: kategorie) {
            // TODO Do not iterate all the objects if its not necessary
            kat.setReadonly(true);
                // !authorization.isAuthorized(
                //     request,
                //     kat,
                //     RequestMethod.POST,
                //     MessprogrammKategorie.class));
        }
        return new Response(true, StatusCodes.OK, kategorie, kategorie.size());
    }

    /**
     * Get a single object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messprogrammkategorie/{id}
     *
     * @return Response object containing a single object.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        MessprogrammKategorie mpk = repository.getByIdPlain(
            MessprogrammKategorie.class,
            Integer.valueOf(id),
            Strings.STAMM
        );
        mpk.setReadonly(
            !authorization.isAuthorized(
                request,
                mpk,
                RequestMethod.POST,
                MessprogrammKategorie.class
            )
        );
        return new Response(true, StatusCodes.OK, mpk);
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        MessprogrammKategorie kategorie
    ) {
        if (!authorization.isAuthorized(
            request,
            kategorie,
            RequestMethod.POST,
            MessprogrammKategorie.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, kategorie);
        }
        QueryBuilder<MessprogrammKategorie> builder =
            new QueryBuilder<MessprogrammKategorie>(
                repository.entityManager(Strings.STAMM),
                MessprogrammKategorie.class
            );
        builder.and("code", kategorie.getCode());
        builder.and("netzbetreiberId", kategorie.getNetzbetreiberId());

        List<MessprogrammKategorie> kategorien =
            repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (kategorien.isEmpty()
            || kategorien.get(0).getId() == kategorie.getId()
        ) {
            return repository.create(kategorie, Strings.STAMM);
        }
        return new Response(false, StatusCodes.IMP_DUPLICATE, null);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        @PathParam("id") String id,
        MessprogrammKategorie kategorie
    ) {
        if (!authorization.isAuthorized(
            request,
            kategorie,
            RequestMethod.PUT,
            MessprogrammKategorie.class)
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, kategorie);
        }
        return repository.update(kategorie, Strings.STAMM);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        MessprogrammKategorie kategorie = repository.getByIdPlain(
            MessprogrammKategorie.class, Integer.valueOf(id), Strings.STAMM);
        if (kategorie == null
            || !authorization.isAuthorized(
                request,
                kategorie,
                RequestMethod.DELETE,
                MessprogrammKategorie.class
            )
        ) {
            return new Response(false, StatusCodes.NOT_ALLOWED, null);
        }
        return repository.delete(kategorie, Strings.STAMM);
    }
}
