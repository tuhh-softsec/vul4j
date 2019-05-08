/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.sql.Types;

import org.hibernate.spatial.dialect.postgis.PostgisPG82Dialect;

/**
 * This dialect extends the default postgis dialect and registers a new data
 * type for integer arrays.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LadaPostgisDialect extends PostgisPG82Dialect {
    private static final long serialVersionUID = 1L;

    public LadaPostgisDialect() {
        super();
        this.registerColumnType(Types.ARRAY, "integer[]");
        this.registerColumnType(Types.JAVA_OBJECT, "json");
    }
}
