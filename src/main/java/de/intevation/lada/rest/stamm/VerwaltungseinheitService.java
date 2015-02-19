/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest.stamm;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stamm.Verwaltungseinheit;
import de.intevation.lada.util.annotation.AuthenticationConfig;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authentication;
import de.intevation.lada.util.auth.AuthenticationType;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

@Path("verwaltungseinheit")
@RequestScoped
public class VerwaltungseinheitService {

    /* The logger used in this class.*/
    @Inject
    private Logger logger;

    /* The data repository granting read/write access.*/
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository defaultRepo;

    /* The authentication module.*/
    @Inject
    @AuthenticationConfig(type=AuthenticationType.NONE)
    private Authentication authentication;

    /* The authorization module.*/
    @Inject
    @AuthorizationConfig(type=AuthorizationType.NONE)
    private Authorization authorization;

    /**
     * Get all objects.
     *
     * @return Response object containing all objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        return defaultRepo.getAll(Verwaltungseinheit.class, "stamm");
    }

    /**
     * Get an object by id.
     *
     * @return Response object containing a single object.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        return defaultRepo.getById(
            Verwaltungseinheit.class,
            id,
            "stamm");
    }
}
