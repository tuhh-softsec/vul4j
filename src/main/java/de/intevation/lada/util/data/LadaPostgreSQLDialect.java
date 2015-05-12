package de.intevation.lada.util.data;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;

public class LadaPostgreSQLDialect extends PostgreSQL9Dialect {
    public LadaPostgreSQLDialect() {
        super();
        this.registerColumnType(Types.ARRAY, "integer[]");
    }

}
