package de.intevation.lada.data.importer;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.rest.Response;


public interface Importer
{
    public Response importData(String content, AuthenticationResponse auth);
}
