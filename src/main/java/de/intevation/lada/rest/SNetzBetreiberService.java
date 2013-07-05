package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SNetzBetreiber;

/**
 * This class produces a RESTful service to read SNetzbetreiber objects.
 * 
 * @author <a href="mailto:torsten.irlaender@intevation.de">Torsten Irländer</a>
 */
@Path("/netzbetreiber")
@RequestScoped
public class SNetzBetreiberService
{
    /**
     * The Repository for SDatenbasis.
     */
    @Inject
    @Named("readonlyrepository")
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request all SNetzbetreiber objects.
     *
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(headers);
            QueryBuilder<SNetzBetreiber> builder =
                new QueryBuilder<SNetzBetreiber>(
                    repository.getEntityManager(), SNetzBetreiber.class);
            builder.or("netzbetreiberId", auth.getNetzbetreiber());
            return repository.filter(builder.getQuery());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SNetzBetreiber>());
        }
    }

    /**
     * Request a single SNetzbetreiber via its id.
     *
     * @param id        The object id.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(
        @PathParam("id") String id,
        @Context HttpHeaders headers) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(headers);
            if (auth.getNetzbetreiber().contains(id)) {
                return repository.findById(SNetzBetreiber.class, id);
            }
            return new Response(false, 698, new ArrayList<SNetzBetreiber>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SNetzBetreiber>());
        }
    }
}
