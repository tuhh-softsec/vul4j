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
import de.intevation.lada.model.SMessgroesse;

/**
 * This class produces a RESTful service to read SMessgroesse objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/messgroesse")
@RequestScoped
public class SMessgroesseService
{
    /**
     * The Repository for SMessgroesse.
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
     * Request all SMessgroesse objects.
     *
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findAll(SMessgroesse.class);
            }
            return new Response(false, 699, new ArrayList<SMessgroesse>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessgroesse>());
        }
    }

    /**
     * Request a SMessgroesse object via its id.
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
        @Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findById(SMessgroesse.class, id);
            }
            return new Response(false, 699, new ArrayList<SMessgroesse>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessgroesse>());
        }
    }
}
