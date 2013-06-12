package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SNetzBetreiber;

/**
 * This class produces a RESTful service to read the contents of SNetzbetreiber table.
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
    @Inject @Named
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
        return repository.findAll(SNetzBetreiber.class);
    }

    /**
     * Request a single SDatenbasis via its id.
     *
     * @param id The mst_id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(SNetzBetreiber.class, id);
    }
}
