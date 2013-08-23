package de.intevation.lada.data.importer;

import java.util.Map;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.rest.Response;


public interface Importer
{
    public boolean importData(String content, AuthenticationResponse auth);
    public Map<String, Map<String, Integer>> getErrors();
    public Map<String, Map<String, Integer>> getWarnings();
}
