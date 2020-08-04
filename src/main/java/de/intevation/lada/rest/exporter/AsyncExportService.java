/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportJobManager;
import de.intevation.lada.exporter.ExportJobManager.JobNotFoundException;
import de.intevation.lada.exporter.ExportJobManager.JobStatus;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;

/**
 * REST service to export data into files using a polling mechanism.
 *
 * Available actions are
 *
 * - Export probe objects with their child objects into .laf files.
 *
 * @author <a href="mailto:awoestmann@intevation.de">Alexander Woestmann</a>
 */
@Path("data/asyncexport")
@RequestScoped
public class AsyncExportService {

    @Inject
    private Logger logger;

    @Inject
    private ExportJobManager exportJobManager;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Export Probe objects into laf files.
     *
     * The service takes JSON formatted  POST data containing probe ids and
     * creates a asynchronous export job for the Probe objects filtered by these ids.
     * <p>
     * To request the export post a JSON formatted string with an array of probe ids and an optional filename
     * <pre>
     * <code>
     * {
     *  "proben": [[number], [number], ...]
     *  "filename": [string]
     * }
     * </code>
     * </pre>
     *
     * The services returns a JSON object containing the id of the newly created export job,
     * which can be used to get the job status or download the file:
     * <p>
     * <pre>
     * {
     *   "refId": [String]
     * }
     * </pre>
     *
     * @param objects    JSON object with an array of probe or messung ids.
     * @param request    The HTTP header containing authorization information.
     * @return The job identifier.
     */
    @POST
    @Path("/laf")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createLafExportJob(
        JsonObject objects,
        @Context HttpServletRequest request
    ) {

        //Check if requests contains either messung or probe ids
        if (objects.getJsonArray("proben") == null
            && objects.getJsonArray("messungen") == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String encoding = request.getHeader("X-FILE-ENCODING");
        if (encoding == null || encoding.equals("")) {
            encoding = "iso-8859-15";
        }
        UserInfo userInfo = authorization.getInfo(request);
        String newJobId = exportJobManager.createExportJob("laf", encoding, objects, userInfo);
        JsonObject responseJson = Json.createObjectBuilder()
            .add("refId", newJobId)
            .build();
        return Response.ok(responseJson.toString()).build();
    }

    /**
     * Get the status of an export job.
     *
     * Output format:
     *
     * <pre>
     * {
     *    done: boolean
     *    status: 'waiting' | 'running' | 'finished' | 'error'
     *    error: string (optional)
     *  }
     * </pre>
     *
     * @param id Job id to check
     * @return Json object containing the status information or status 404 if job was not found
     */
    @GET
    @Path("/status/{id}")
    @Produces("application/json")
    public Response getStatus(@PathParam("id") String id) {
        JobStatus status;
        try {
            status = exportJobManager.getJobStatus(id);
        } catch (JobNotFoundException jnfe) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        JsonObject responseJson = Json.createObjectBuilder()
            .add("status", status.getStatus())
            .add("message", status.getMessage())
            .build();

        return Response.ok(responseJson.toString()).build();
    }

    /**
     * Download a finished export file
     * @param id Job id to download file from
     * @return Export file or status 404 if file was not found
     */
    @GET
    @Path("download/{id}")
    @Produces("application/octet-stream")
    public Response download(@PathParam("id") String id) {
        ByteArrayInputStream resultStream;
        String encoding;
        String filename;
        try {
            resultStream = exportJobManager.getResultFileAsStream(id);
            encoding = exportJobManager.getJobEncoding(id);
            filename = exportJobManager.getJobDownloadFilename(id);
        } catch (JobNotFoundException jfe) {
            logger.info(String.format("Could not find export file for job %s", id));
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ResponseBuilder response = Response.ok(resultStream);
            response.header(
                "Content-Disposition",
                "attachment; filename=\"" + filename + "\"");
            response.encoding(encoding);
            response.header("Content-Type", "application/octet-stream; charset=" + encoding);

        return response.build();
    }
}