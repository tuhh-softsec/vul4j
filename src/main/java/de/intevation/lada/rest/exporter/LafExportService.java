/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.exporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.log4j.Logger;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;

/**
 * REST service to export probe objects and the child objects associated with
 * the selected Probe objects.
 * <p>
 * To request objects post a JSON formatted string with an array of probe ids.
 * <pre>
 * <code>
 * {
 *  "proben": [[number], [number], ...]
 * }
 * </code>
 * </pre>
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("data/export")
@RequestScoped
public class LafExportService {

    /**
     * The data repository granting read-only access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    /**
     * The exporter.
     */
    @Inject
    @ExportConfig(format=ExportFormat.LAF)
    private Exporter exporter;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;


    /**
     * Export Probe objects.
     *
     * The service takes JSON formatted  POST data containing probe ids and
     * exports the Probe objects filtered by these ids.
     *
     * @param proben    JSON formatted string with an array of probe ids.
     * @param header    The HTTP header containing authorization information.
     * @return The LAF file to export.
     */
    @POST
    @Path("/laf")
    @Consumes("application/json")
    @Produces("application/octet-stream")
    public Response download(
        JsonObject objects,
        @Context HttpServletRequest request
    ) {
        List<Integer> probeIds = new ArrayList<Integer>();
        List<Integer> messungIds = new ArrayList<Integer>();
        if (objects.getJsonArray("proben") != null) {
            for (JsonValue id : objects.getJsonArray("proben")) {
                if (id instanceof JsonNumber) {
                    probeIds.add(((JsonNumber)id).intValue());
                }
            }
        }
        if (objects.getJsonArray("messungen") != null) {
            for (JsonValue id : objects.getJsonArray("messungen")) {
                if (id instanceof JsonNumber) {
                    messungIds.add(((JsonNumber)id).intValue());
                }
            }
        }

        List<Integer> pIds = new ArrayList<Integer>();
        if (!probeIds.isEmpty()) {
            QueryBuilder<Probe> pBuilder = new QueryBuilder<Probe>(
                repository.entityManager(Strings.LAND), Probe.class);
            pBuilder.andIn("id", probeIds);
            List<Probe> pObjects = repository.filterPlain(
                pBuilder.getQuery(), Strings.LAND);
            for (Probe p : pObjects) {
                pIds.add(p.getId());
            }
        }

        List<Integer> mIds = new ArrayList<Integer>();
        if (!messungIds.isEmpty()) {
            QueryBuilder<Messung> mBuilder = new QueryBuilder<Messung>(
                repository.entityManager(Strings.LAND), Messung.class);
            mBuilder.andIn("id", messungIds);
            List<Messung> mObjects = repository.filterPlain(
                mBuilder.getQuery(), Strings.LAND);
            for (Messung m : mObjects) {
                mIds.add(m.getId());
            }
        }

        if (pIds.isEmpty() && mIds.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String encoding = request.getHeader("X-FILE-ENCODING");
        if (encoding == null || encoding.equals("")) {
            encoding = "iso-8859-15";
        }

        UserInfo userInfo = authorization.getInfo(request);
        InputStream exported = exporter.exportProben(pIds, mIds, encoding, userInfo);

        ResponseBuilder response = Response.ok((Object)exported);
        response.header(
            "Content-Disposition",
            "attachment; filename=\"export.laf\"");
        response.encoding(encoding);
        response.header("Content-Type", "application/octet-stream; charset=" + encoding);
        return response.build();
    }
}
