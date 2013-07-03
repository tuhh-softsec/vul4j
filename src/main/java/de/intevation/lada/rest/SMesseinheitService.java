package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.authentication.Authentication;
import de.intevation.lada.authentication.AuthenticationException;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SMessEinheit;

/**
 * This class produces a RESTful service to read SMesseinheit objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/messeinheit")
@RequestScoped
public class SMesseinheitService
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
     * Request all SMesseinheit objects.
     *
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findAll(SMessEinheit.class);
            }
            return new Response(false, 699, new ArrayList<SMessEinheit>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessEinheit>());
        }
    }
}
