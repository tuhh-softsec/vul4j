#!/bin/sh
DIR=`dirname $0`

ROLE_NAME=lada
ROLE_PW=lada
DB_NAME=lada

psql --command "CREATE USER $ROLE_NAME PASSWORD '$ROLE_PW';"
createdb -E UTF-8 $DB_NAME
psql -d $DB_NAME -f $DIR/lada_schema.sql
psql -d $DB_NAME --command \
     "GRANT USAGE ON SCHEMA stammdaten, bund, land TO $ROLE_NAME;
      GRANT SELECT
            ON ALL SEQUENCES IN SCHEMA stammdaten, bund, land TO $ROLE_NAME;
      GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES
            ON ALL TABLES IN SCHEMA stammdaten, bund, land TO $ROLE_NAME;"
