package de.intevation.lada.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.data.importer.Importer;

/**
* This class produces a RESTful service to import a LAF file.
*
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@Path("/import")
@RequestScoped
public class LAFImportService
{
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    @Inject
    @Named("lafimporter")
    private Importer importer;

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
    @POST
    @Path("/laf")
    @Produces("application/json")
    @Consumes("multipart/form-data")
    public Response upload(MultipartFormDataInput input, @Context HttpHeaders header) {
        try {
            AuthenticationResponse auth = authentication.authorizedGroups(header);
            if (!authentication.isAuthorizedUser(header)) {
                return new Response(false, 698, null);
            }

            String name = "";
            String content = "";
            Map<String, List<InputPart>> data = input.getFormDataMap();
            try {
                List<InputPart> parts = input.getParts();
                for (InputPart part: parts) {
                    InputStream inStream = part.getBody(InputStream.class, null);
                    MultivaluedMap<String, String> headers = part.getHeaders();
                    String[] cDisp = headers.getFirst("content-disposition").split(";");
                    for (String fName : cDisp) {
                        if (fName.trim().startsWith("filename")) {
                            String[] fileName = fName.split("=");
                            name = fileName[1].trim().replace("\"", "");
                        }
                    }
                    content = IOUtils.toString(inStream);
                }
            }
            catch (IOException e) {
                importer.reset();
                return new Response(false, 603, null);
            }

            boolean success = importer.importData(content, auth);
            Map<String, Object> respData = new HashMap<String,Object>();
            respData.put("errors", importer.getErrors());
            respData.put("warnings", importer.getWarnings());
            respData.put("filename", name);
            int code = 200;
            if (!success) {
                code = 670;
            }
            Response response = new Response(success, code, respData);
            importer.reset();
            return response;
        }
        catch(AuthenticationException ae) {
            importer.reset();
            return new Response(false, 699, null);
        }
    }
}
