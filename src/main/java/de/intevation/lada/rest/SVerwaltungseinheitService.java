package de.intevation.lada.rest;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.authentication.Authentication;
import de.intevation.lada.authentication.AuthenticationException;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SVerwaltungseinheit;

/**
 * This class produces a RESTful service to read SVerwaltungseinheit objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/verwaltungseinheit")
@RequestScoped
public class SVerwaltungseinheitService
{
    /**
     * The Repository for SUmwelt.
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
     * Request all SUmwelt objects.
     *
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findAll(SVerwaltungseinheit.class);
            }
            return new Response(false, 699, new ArrayList<SVerwaltungseinheit>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SVerwaltungseinheit>());
        }
    }

    /**
     * Request a SUmwelt object via its id.
     *
     * @param id        The object id.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("text/json")
    public Response findById(
        @PathParam("id") String id,
        @Context HttpHeaders headers
    ) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findById(SVerwaltungseinheit.class, id);
            }
            return new Response(false, 699, new ArrayList<SVerwaltungseinheit>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SVerwaltungseinheit>());
        }
    }
}
