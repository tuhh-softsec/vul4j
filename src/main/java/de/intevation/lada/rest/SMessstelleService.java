package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SMessStelle;
import javax.inject.Named;

/**
 * This class produces a RESTful service to read the contents of s_messstelle
 * table.
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
    @Inject @Named
    private Repository repository;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request all SMessStelle objects.
     *
     * @return JSON Object via Rest service
     */
    @GET
    @Produces("text/json")
    public List<SMessStelle> findAll() {
        List<SMessStelle> result = repository.findAll(SMessStelle.class);
        return result;
    }

    /**
     * Request a single SMessStelle via its id.
     *
     * @param id The mst_id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public SMessStelle findById(@PathParam("id") String id) {
        return repository.findById(SMessStelle.class, id);
    }
}
