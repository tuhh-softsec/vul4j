package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
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
import de.intevation.lada.model.SMessStelle;

/**
 * This class produces a RESTful service to read SMessstelle objects
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/mst")
@RequestScoped
public class SMessstelleService
{
    /**
     * The Repository for SMessStelle.
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
     * Request all SMessstelle objects.
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
            QueryBuilder<SMessStelle> builder =
                new QueryBuilder<SMessStelle>(
                    repository.getEntityManager(), SMessStelle.class);
            builder.or("mstId", auth.getMst());
            return repository.filter(builder.getQuery());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessStelle>());
        }
    }

    /**
     * Request a single SMessstelle via its id.
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
            if (auth.getMst().contains(id)) {
                return repository.findById(SMessStelle.class, id);
            }
            return new Response(false, 698, new ArrayList<SMessStelle>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessStelle>());
        }
    }
}
