#!/bin/sh
DIR=`dirname $0`

ROLE_NAME=${1:-lada}
ROLE_PW=${2:-$ROLE_NAME}
DB_NAME=${3:-$ROLE_NAME}

psql --command "CREATE USER $ROLE_NAME PASSWORD '$ROLE_PW';"
createdb -E UTF-8 $DB_NAME

psql -d $DB_NAME  --command \
     "CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public"

psql -d $DB_NAME -f $DIR/stammdaten_schema.sql
psql -d $DB_NAME -f $DIR/lada_schema.sql
psql -d $DB_NAME --command \
     "GRANT USAGE ON SCHEMA stammdaten, bund, land TO $ROLE_NAME;
      GRANT USAGE
            ON ALL SEQUENCES IN SCHEMA stammdaten, bund, land TO $ROLE_NAME;
      GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES
            ON ALL TABLES IN SCHEMA stammdaten, bund, land TO $ROLE_NAME;"
