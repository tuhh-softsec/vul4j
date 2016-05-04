#!/bin/sh
DIR=`dirname $0`

ROLE_NAME=${1:-lada}
echo "DROLE_NAME = $ROLE_NAME"
ROLE_PW=${2:-$ROLE_NAME}
echo "ROLE_PW = $ROLE_PW"
DB_NAME=${3:-$ROLE_NAME}
echo "DB_NAME = $DB_NAME"

# if variable DB_SRV and otional DB_PORT is set a remote database connection will be used
if [ -n "$DB_SRV" ] ; then DB_CONNECT_STRING="-h $DB_SRV" ; fi
if [ -n "$DB_SRV" -a -n "$DB_PORT"  ] ; then 
  DB_CONNECT_STRING="$DB_CONNECT_STRING -p $DB_PORT"
fi
DB_CONNECT_STRING="$DB_CONNECT_STRING -U postgres"
echo "DB_CONNECT_STRING = $DB_CONNECT_STRING"

if [ `psql $DB_CONNECT_STRING -t --command "SELECT count(*) FROM pg_catalog.pg_user WHERE usename = '$ROLE_NAME'"` -eq 0 ] ; then
  echo create user $ROLE_NAME
  psql $DB_CONNECT_STRING --command "CREATE USER $ROLE_NAME PASSWORD '$ROLE_PW';"
fi

if psql -h test-pgsql1-fr.lab.bfs.de -U postgres -l | grep -q "^ $DB_NAME " ; then
  echo drop db $DB_NAME 
  psql $DB_CONNECT_STRING --command "DROP DATABASE $DB_NAME"
fi

echo create db $DB_NAME
psql $DB_CONNECT_STRING --command \
     "CREATE DATABASE $DB_NAME WITH OWNER = $ROLE_NAME ENCODING = 'UTF8'"

echo create postgis extention
psql $DB_CONNECT_STRING -d $DB_NAME  --command  \
     "CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public"

echo create stammdaten schema
psql $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_schema.sql

echo create lada schema
psql $DB_CONNECT_STRING -d $DB_NAME -f $DIR/lada_schema.sql
echo set grants
psql $DB_CONNECT_STRING -d $DB_NAME --command \
     "GRANT USAGE ON SCHEMA stammdaten, bund, land TO $ROLE_NAME;
      GRANT USAGE
            ON ALL SEQUENCES IN SCHEMA stammdaten, bund, land TO $ROLE_NAME;
      GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES
            ON ALL TABLES IN SCHEMA stammdaten, bund, land TO $ROLE_NAME;"

echo import stammdaten
psql $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_data.sql

echo import lada test data
psql $DB_CONNECT_STRING -d $DB_NAME -f $DIR/lada_data.sql
