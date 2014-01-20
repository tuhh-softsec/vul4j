/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
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
