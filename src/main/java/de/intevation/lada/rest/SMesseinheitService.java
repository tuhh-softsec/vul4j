package de.intevation.lada.rest;

import java.util.logging.Logger;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SMessEinheit;

/**
 * This class produces a RESTful service to read the contents of SDatenbasis table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/datenbasis")
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
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request all SDatenbasis objects.
     *
     * @return JSON Object via Rest service
     */
    @GET
    @Produces("text/json")
    public Response findAll() {
        return repository.findAll(SMessEinheit.class);
    }
}
