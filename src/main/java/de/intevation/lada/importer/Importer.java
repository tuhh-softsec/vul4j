/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer;

import java.util.List;
import java.util.Map;

import de.intevation.lada.util.auth.UserInfo;

/**
 * Interface for Lada importer.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Importer {
    void reset();
    Map<String, List<ReportItem>> getWarnings();
    Map<String, List<ReportItem>> getErrors();
    void doImport(String content, UserInfo userInfo);
}
