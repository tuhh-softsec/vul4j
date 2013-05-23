package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.SMessstelleRepository;
import de.intevation.lada.model.SMessStelle;

@Path("/mst")
@RequestScoped
public class SMessstelleRESTService
{
    @Inject
    private SMessstelleRepository repository;

    @Inject
    private Logger logger;

    @GET
    @Produces("text/json")
    public List<SMessStelle> findAll() {
        final List<SMessStelle> result = repository.findAll();
        return result;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("text/json")
    public SMessStelle findById(@PathParam("id") String id) {
        return repository.findById(id);
    }
}
