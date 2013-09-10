package de.intevation.lada.data.importer;

import java.util.List;
import java.util.Map;

import de.intevation.lada.auth.AuthenticationResponse;

/**
 * Defines the interface for data importer using authentication information.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Importer
{
    public boolean importData(String content, AuthenticationResponse auth);
    public Map<String, List<ReportData>> getErrors();
    public Map<String, List<ReportData>> getWarnings();
    public void reset();
}
