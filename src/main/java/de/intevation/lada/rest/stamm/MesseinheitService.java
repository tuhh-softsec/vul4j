/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stammdaten.MassEinheitUmrechnung;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for MessEinheit objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [number],
 *      "beschreibung": [string],
 *      "einheit": [string],
 *      "eudfMesseinheitId": [string],
 *      "umrechnungsFaktorEudf": [number]
 *  }],
 *  "errors": [object],
 *  "warnings": [object],
 *  "readonly": [boolean],
 *  "totalCount": [number]
 * }
 * </code>
 * </pre>
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("rest/messeinheit")
@RequestScoped
public class MesseinheitService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository defaultRepo;

    /**
     * Get all MessEinheit objects.
     * <p>
     * The requested Objects can be filtered using an two URL parameters named
     * mehId and secMehId.
     * If these parameters are used, the filter only returns records that are
     * convertable into one of these units.
     * Records, convertable into the primary messeinheit (mehId) will have the
     * attribute 'primary' set to true.
     * Records convertable into the secondary messeinheit (secMehId) will have the
     * attribute 'primary' set to false.
     * Example: http://example.com/messeinheit
     *
     * @return Response object containing all MessEinheit objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("mehId") || params.getFirst("mehId").equals("")) {
            return defaultRepo.getAll(MessEinheit.class, Strings.STAMM);
        }
        String mehId = params.getFirst("mehId");


        MessEinheit meh = defaultRepo.getByIdPlain(MessEinheit.class, Integer.parseInt(mehId), Strings.STAMM);
        MessEinheit secMeh = null;
        if (params.containsKey("secMehId")) {
            String secMehId = params.getFirst("secMehId");
            secMeh = defaultRepo.getByIdPlain(MessEinheit.class, Integer.parseInt(secMehId), Strings.STAMM);
        }
        List<MessEinheit> einheits = new ArrayList<MessEinheit>(meh.getMassEinheitUmrechnungZus().size());
        meh.setPrimary(true);
        einheits.add(meh);
        if (secMeh != null) {
            secMeh.setPrimary(false);
            einheits.add(secMeh);
        }
        for (MassEinheitUmrechnung umrechnung : meh.getMassEinheitUmrechnungZus()) {
            MessEinheit einheit = umrechnung.getMehVon();
            einheit.setPrimary(true);
            einheits.add(einheit);
        }
        if (secMeh != null) {
            secMeh.getMassEinheitUmrechnungZus().forEach(umrechnung -> {
                MessEinheit einheit = umrechnung.getMehVon();
                //If unit was not already added
                if (!einheits.contains(einheit)) {
                    //Add as secondary unit
                    einheit.setPrimary(false);
                    einheits.add(einheit);
                }
            });
        }
        return new Response(true, 200, einheits);
    }

    /**
     * Get a single MessEinheit object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/messeinheit/{id}
     *
     * @return Response object containing a single MessEinheit.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return defaultRepo.getById(
            MessEinheit.class,
            Integer.valueOf(id),
            Strings.STAMM);
    }
}
