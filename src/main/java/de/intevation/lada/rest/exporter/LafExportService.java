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
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.util.annotation.AuthorizationConfig;
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
@Path("export")
@RequestScoped
public class LafExportService {

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
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
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
    @Produces("text/plain")
    public Response download(
        JsonObject proben,
        @Context HttpServletRequest request
    ) {
        JsonArray array = proben.getJsonArray("proben");
        List<Integer> probeIds = new ArrayList<Integer>();
        String fileName = "export.laf";
        UserInfo userInfo = authorization.getInfo(request);
        for (int i = 0; i < array.size(); i++) {
            Integer probeId = array.getInt(i);
            //if (authorization.isAuthorized(userInfo, probeId)) {
                probeIds.add(probeId);
            //}
        }
        InputStream exported = exporter.export(probeIds, userInfo);
        ResponseBuilder response = Response.ok((Object)exported);
        response.header(
            "Content-Disposition",
            "attachment; filename=\"" + fileName + "\"");
        return response.build();
    }
}
