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

import de.intevation.lada.model.stammdaten.ImporterConfig;
import de.intevation.lada.util.auth.UserInfo;

/**
 * Interface for Lada importer.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Importer {
    /**
     * Reset the importer values and status.
     */
    void reset();

    /**
     * Get the warnings occured during import.
     * @return list of warnings
     */
    Map<String, List<ReportItem>> getWarnings();

    /**
     * Get the errors occured during import.
     * @return list of errors
     */
    Map<String, List<ReportItem>> getErrors();

    /**
     * Get the notifications occured during import.
     * @return list of notifications
     */
    Map<String, List<ReportItem>> getNotifications();

    /**
     * Start the import.
     * @param content the data to be imported
     * @param userInfo the current user info
     * @param config the import configuration
     */
    void doImport(
        String content,
        UserInfo userInfo,
        List<ImporterConfig> config);
}
