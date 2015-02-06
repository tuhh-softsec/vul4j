/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LProbe;
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

@Path("probe")
@RequestScoped
public class ProbeService {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

    @Inject
    @AuthenticationConfig(type=AuthenticationType.NONE)
    private Authentication authentication;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.NONE)
    private Authorization authorization;

    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(@Context HttpHeaders headers) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        defaultRepo.setDataSource("land");
        return defaultRepo.getAll(LProbe.class);
    }

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
        defaultRepo.setDataSource("land");
        return defaultRepo.getById(LProbe.class, Integer.valueOf(id));
    }

    @GET
    @Produces("application/json")
    @Path("/land")
    public Response getAllLand() {
        //List<LProbe> res = dao.getAllLand();
        //return new Response(true, 200, res);
        return null;
    }
}
