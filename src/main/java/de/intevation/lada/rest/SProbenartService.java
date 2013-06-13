package de.intevation.lada.rest;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SProbenart;

/**
 * This class produces a RESTful service to read the contents of SProbenart table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/probenart")
@RequestScoped
public class SProbenartService
{
    /**
     * The Repository for SProbenart.
     */
    @Inject
    @Named("readolyrepository")
    private Repository repository;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request all SProbenart objects.
     *
     * @return JSON Object via Rest service
     */
    @GET
    @Produces("text/json")
    public Response findAll() {
        return repository.findAll(SProbenart.class);
    }

    /**
     * Request a single SProbenart via its id.
     *
     * @param id The mst_id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(SProbenart.class, id);
    }
}
