package de.intevation.lada.data.importer;

import javax.inject.Named;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.rest.Response;

@Named("lafimporter")
public class LAFImporter
implements Importer
{
    @Override
    public Response importData(String content, AuthenticationResponse auth) {
        return new Response(true, 200, null);
    }
}
