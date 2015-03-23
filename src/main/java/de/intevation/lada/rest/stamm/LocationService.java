/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest.stamm;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stamm.SOrt;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

@Path("location")
@RequestScoped
public class LocationService {

    /* The data repository granting read/write access.*/
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

    /**
     * Get all objects.
     *
     * @return Response object containing all objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("ortId")) {
            return defaultRepo.getAll(SOrt.class, "stamm");
        }
        String probeId = params.getFirst("ortId");
        QueryBuilder<SOrt> builder =
            new QueryBuilder<SOrt>(
                defaultRepo.entityManager("land"),
                SOrt.class);
        builder.and("probeId", probeId);
        return defaultRepo.filter(builder.getQuery(), "stamm");
    }

    /**
     * Get an object by id.
     *
     * @return Response object containing a single object.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return defaultRepo.getById(
            SOrt.class,
            Integer.valueOf(id),
            "stamm");
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        SOrt ort
    ) {
        /* Persist the new object*/
        return defaultRepo.create(ort, "stamm");
    }

    /**
     * Update an existing object.
     *
     * @return Response object containing the updated object.
     */
    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers, SOrt ort) {
        Response response = defaultRepo.update(ort, "stamm");
        Response updated = defaultRepo.getById(
            SOrt.class,
            ((SOrt)response.getData()).getId(), "stamm");
        return updated;
    }

    /**
     * Delete an existing object by id.
     *
     * @return Response object.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        /* Get the object by id*/
        Response object =
            defaultRepo.getById(SOrt.class, Integer.valueOf(id), "stamm");
        SOrt ortObj = (SOrt)object.getData();
        /* Delete the object*/
        return defaultRepo.delete(ortObj, "stamm");
    }
}
