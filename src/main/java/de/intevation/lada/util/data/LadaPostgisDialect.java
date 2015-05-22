package de.intevation.lada.util.data;

import java.sql.Types;

import org.hibernate.spatial.dialect.postgis.PostgisDialect;

public class LadaPostgisDialect extends PostgisDialect {
    public LadaPostgisDialect() {
        super();
        this.registerColumnType(Types.ARRAY, "integer[]");
    }
}
