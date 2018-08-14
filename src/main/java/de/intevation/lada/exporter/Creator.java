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
public interface Creator
{
    public void setUserInfo(UserInfo userInfo);
    public String createProbe(String probeId);
    public String createMessung(String probeId, List<Integer> messungen);
}
