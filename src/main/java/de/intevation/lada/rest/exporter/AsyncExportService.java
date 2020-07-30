/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.exporter;

import javax.enterprise.context.RequestScoped;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import de.intevation.lada.util.rest.Response;

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

    /**
     * Export Probe objects into laf files.
     *
     * The service takes JSON formatted  POST data containing probe ids and
     * creates a asynchronous export job for the Probe objects filtered by these ids.
     * <p>
     * To request the export post a JSON formatted string with an array of probe ids.
     * <pre>
     * <code>
     * {
     *  "proben": [[number], [number], ...]
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
     * @param proben    JSON object with an array of probe ids.
     * @param request    The HTTP header containing authorization information.
     * @return The job identifier.
     */
    @POST
    @Path("/laf")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createLafExportJob(
        JsonObject proben,
        @Context HttpServletRequest request
    ) {
        return null;
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
     * @return Json object containing the status information
     */
    @GET
    @Path("/status/{id}")
    @Produces("application/json")
    public Response getStatus(@PathParam("id") String id) {
        return null;
    }

    /**
     * Download a finished export file
     * @param id Job id to download file from
     * @return Export file
     */
    @GET
    @Path("download/{id}")
    @Produces("application/octet-stream")
    public Response download(@PathParam("id") String id) {
        return null;
    }
}