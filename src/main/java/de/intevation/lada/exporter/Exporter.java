/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter;

import java.io.InputStream;
import java.util.List;

import de.intevation.lada.util.auth.UserInfo;

/**
 * Interface for Lada data exporter.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Exporter
{
    public InputStream export(List<Integer> proben, UserInfo userInfo);
}
