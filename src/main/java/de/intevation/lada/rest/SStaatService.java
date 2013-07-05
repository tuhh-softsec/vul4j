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

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SStaat;

/**
 * This class produces a RESTful service to read SStaat objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/staat")
@RequestScoped
public class SStaatService
{
    /**
     * The Repository for SStaat.
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
     * Request all SStaat objects.
     *
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findAll(SStaat.class);
            }
            return new Response(false, 699, new ArrayList<SStaat>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SStaat>());
        }
    }

    /**
     * Request a SStaat object via its id.
     *
     * @param id The object id.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("text/json")
    public Response findById(
        @PathParam("id") String id,
        @Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findById(SStaat.class, id);
            }
            return new Response(false, 699, new ArrayList<SStaat>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SStaat>());
        }
    }
}
