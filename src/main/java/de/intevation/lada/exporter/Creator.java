/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter;

import de.intevation.lada.util.auth.UserInfo;
import java.util.List;

/**
 * Interface for export content creator.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Creator {
    /**
     * Setter for the current user info.
     * @param userInfo The current userinfo
     */
    void setUserInfo(UserInfo userInfo);
    /**
     * Create a string representation of a probe object.
     * @param probeId the id of the requested probe object.
     * @return the string representation
     */
    String createProbe(String probeId);

    /**
     * Create a string representation of a messung objects.
     * @param probeId The id of the probe object.
     * @param messungen the list of messung ids
     * @return String representation of the messug objects
     */
    String createMessung(String probeId, List<Integer> messungen);
}
