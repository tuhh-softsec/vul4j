package de.intevation.lada.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

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
     * Export LProbe objects.
     *
     * The service takes form url encoded POST data containing probe ids and
     * exports the LProbe objects filtered by these ids.
     *
     * @param proben    Form data (url encoded) string with probe ids.
     * @param header    The HTTP header containing authorization information.
     * @return The LAF file to export.
     */
    @POST
    @Path("/laf")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response download(
        String proben,
        @Context HttpHeaders header
    ) {
        try {
            String[] raw = proben.split("&");
            List<String> probeIds = new ArrayList<String>();
            for (int i = 0; i < raw.length; i++) {
                String[] probe = raw[i].split("=");
                probeIds.add(probe[1]);
            }
            String fileName = "export.laf";
            AuthenticationResponse auth = authentication.authorizedGroups(header);
            if (!authentication.isAuthorizedUser(header)) {
                ResponseBuilder response = Response.status(Status.FORBIDDEN);
                return response.build();
            }
            for (int i = 0; i < probeIds.size(); i++) {
                String probeId = probeIds.get(i);
                if (!authentication.hasAccess(header, probeId) &&
                    !authorization.isReadOnly(probeId)) {
                    probeIds.remove(probeId);
                }
            }
            InputStream exported = exporter.export(probeIds, auth);
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
