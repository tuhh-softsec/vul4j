package de.intevation.lada.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.auth.Authorization;
import de.intevation.lada.data.exporter.Exporter;

/**
* This class produces a RESTful service to export a LAF file.
*
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@Path("/export")
@RequestScoped
public class LAFExportService
{
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    @Inject
    @Named("dataauthorization")
    private Authorization authorization;

    @Inject
    @Named("lafexporter")
    private Exporter exporter;
    /**
     * Import LProbe object.
     * See
     * http://howtodoinjava.com/2013/05/21/jax-rs-resteasy-file-upload-httpclient-example/
     * for more details on the implementation.
     *
     * @param input MulitpartFormDataInput containing the file to upload.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/laf/{id}")
    @Produces("text/plain")
    public Response download(
        @PathParam("id") String probeId,
        @Context HttpHeaders header
    ) {
        try {
            String fileName = "export.laf";
            AuthenticationResponse auth = authentication.authorizedGroups(header);
            if (!authentication.isAuthorizedUser(header)) {
                ResponseBuilder response = Response.status(Status.FORBIDDEN);
                return response.build();
            }
            if (!authentication.hasAccess(header, probeId) ||
                !authorization.isReadOnly(probeId)) {
                ResponseBuilder response = Response.status(Status.FORBIDDEN);
                return response.build();
            }
            InputStream exported = exporter.export(probeId, auth);
            ResponseBuilder response = Response.ok((Object)exported);
            response.header(
                "Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");
            return response.build();

        }
        catch(AuthenticationException ae) {
            ResponseBuilder response = Response.status(Status.FORBIDDEN);
            return response.build();
        }
    }
}
