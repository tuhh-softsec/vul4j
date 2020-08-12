/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.exporter;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
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
 * - Export a query result into csv files
 * - Export a query result into json files
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
     * Export data into a csv file.
     * 
     * This service takes json formatted POST data containing:
     * - Query parameters used for obtaining the data to. The "export" set whether the field should be export or not.
     *   Note: The column list must contain the record's id column even if it will not be exported.
     * - A boolean that sets if related subdata should be exported too
     * - An optional list of subdata column names. May only be set if "subData" is true
     * - The gridColumnId that contains the record id
     * - An optional id filter to limit the export data. If not set, the complete query result will be exported
     * - CSV specific options
     * - An optional filename used for download
     * <p>
     * Input format:
     * <p>
     * <
     * {
     *   "columns": [{
     *     "gridColumnId": [number],
     *     "sort": [string],
     *     "sortIndex": [number],
     *     "filterValue": [string],
     *     "filterActive": [boolean],
     *     "export": [boolean]
     *   }],
     *   "exportSubData": [boolean],
     *   "subDataColumns": [ [string] ]
     *   "idField": [string],
     *   "idType": [string],
     *   idFilter: [ [number] ],
     *   csvOptions: {
     *     decimalSeparator: "comma" | "period",
     *     fieldSeparator: "comma" | "semicolon" | "period" | "space",
     *     rowDelimiter: "windows" | "linux",
     *     quoteType: "singlequote" | "doublequote"
     *   },
     *   filename: [string]
     * }
     * <p>
     * Return format:
     * <p>
     * <pre>
     * {
     *   "refId": [String]
     * }
     * </pre>
     * @param objects JSON Object containing the export parameters
     * @param request Request object
     * @return Response containing the new export ref id
     */
    @POST
    @Path("/csv")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createCsvExportJob(
        JsonObject objects,
        @Context HttpServletRequest request
    ) {

        String encoding = request.getHeader("X-FILE-ENCODING");
        if (encoding == null || encoding.equals("")) {
            encoding = "iso-8859-15";
        }
        UserInfo userInfo = authorization.getInfo(request);
        String newJobId = exportJobManager.createExportJob("csv", encoding, objects, userInfo);
        JsonObject responseJson = Json.createObjectBuilder()
            .add("refId", newJobId)
            .build();
        return Response.ok(responseJson.toString()).build();
    }

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
     * Export data into a json file.
     * 
     * This service takes json formatted POST data containing:
     * - Query parameters used for obtaining the data to. The "export" set whether the field should be export or not.
     *   Note: The column list must contain the record's id column even if it will not be exported.
     * - A boolean that sets if related subdata should be exported too
     * - An optional list of subdata column names. May only be set if "subData" is true
     * - The gridColumnId that contains the record id
     * - An optional id filter to limit the export data. If not set, the complete query result will be exported
     * - An optional filename used for download
     * <p>
     * Input format:
     * <p>
     * <
     * {
     *   "columns": [{
     *     "gridColumnId": [number],
     *     "sort": [string],
     *     "sortIndex": [number],
     *     "filterValue": [string],
     *     "filterActive": [boolean],
     *     "export": [boolean]
     *   }],
     *   "exportSubData": [boolean],
     *   "subDataColumns": [ [string] ]
     *   "idField": [number]
     *   idFilter: [ [number] ],
     *   filename: [string]
     * }
     * <p>
     * Return format:
     * <p>
     * <pre>
     * {
     *   "refId": [String]
     * }
     * </pre>
     * @param objects JSON Object containing the export parameters
     * @param request Request object
     * @return Response containing the new export ref id
     */
    @POST
    @Path("/json")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createJsonExportJob(
        JsonObject objects,
        @Context HttpServletRequest request
    ) {

        String encoding = request.getHeader("X-FILE-ENCODING");
        if (encoding == null || encoding.equals("")) {
            encoding = "iso-8859-15";
        }
        UserInfo userInfo = authorization.getInfo(request);
        String newJobId = exportJobManager.createExportJob("json", encoding, objects, userInfo);
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
     *    message: string (optional)
     *  }
     * </pre>
     *
     * @param id Job id to check
     * @return Json object containing the status information, status 403 if the requesting user has not created the request
     *         or status 404 if job was not found
     */
    @GET
    @Path("/status/{id}")
    @Produces("application/json")
    public Response getStatus(
        @PathParam("id") String id,
        @Context HttpServletRequest request) {

        JobStatus status;
        UserInfo originalCreator;
        UserInfo requestingUser = authorization.getInfo(request);

        try {
            originalCreator = exportJobManager.getJobUserInfo(id);
            if (!originalCreator.getUserId().equals(requestingUser.getUserId())) {
                logger.warn(String.format("Rejected status request by user #%s for job %s created by user #%s",
                    requestingUser.getUserId(), id, originalCreator.getUserId()));
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            status = exportJobManager.getJobStatus(id);
        } catch (JobNotFoundException jnfe) {
            logger.info(String.format("Could not find status for job %s", id));
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        JsonObject responseJson = Json.createObjectBuilder()
            .add("status", status.getStatus())
            .add("message", status.getMessage())
            .add("done", status.isDone())
            .build();

        return Response.ok(responseJson.toString()).build();
    }

    /**
     * Download a finished export file
     * @param id Job id to download file from
     * @return Export file, status 403 if the requesting user has not created the request
     *         or status 404 if job was not found
     */
    @GET
    @Path("download/{id}")
    @Produces("application/octet-stream")
    public Response download(
        @PathParam("id") String id,
        @Context HttpServletRequest request) {

        ByteArrayInputStream resultStream;
        String encoding;
        String filename;
        UserInfo originalCreator;
        UserInfo requestingUser = authorization.getInfo(request);

        try {
            originalCreator = exportJobManager.getJobUserInfo(id);
            if (!originalCreator.getUserId().equals(requestingUser.getUserId())) {
                logger.warn(String.format("Rejected download request by user %s for job %s created by user %s",
                    requestingUser.getUserId(), id, originalCreator.getUserId()));
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            encoding = exportJobManager.getJobEncoding(id);
            filename = exportJobManager.getJobDownloadFilename(id);
            resultStream = exportJobManager.getResultFileAsStream(id);

        } catch (JobNotFoundException jfe) {
            logger.info(String.format("Returning 404 for download: Could not find job %s", id));
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (FileNotFoundException fnfe) {
            logger.error(String.format("Error on reading result file for job %s", id));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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