#!/bin/sh -e
# SYNOPSIS
# ./setup-db.sh [-cn] [ROLE_NAME] [ROLE_PW] [DB_NAME]
#   -c         clean - drop an existing database
#   -n         no data - do not import example data
#   ROLE_NAME  name of db user (default = lada)
#   ROLE_PW    login password  (default = ROLE_NAME)
#   DB_NAME    name of the databaes (default = ROLE_NAME)
#
# There will be used a remote database server if there exists the
# enviroment variable DB_SRV and optional DB_PORT

DIR=`dirname $0`

while getopts "cn" opt; do
    case "$opt" in
        c)
            DROP_DB="true"
            ;;
        n)
            NO_DATA="true"
            ;;
    esac
done

shift $((OPTIND-1))

ROLE_NAME=${1:-lada}
echo "ROLE_NAME = $ROLE_NAME"
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

if [ "$DROP_DB" = "true" ] && psql $DB_CONNECT_STRING -l | grep -q "^ $DB_NAME " ; then
  echo drop db $DB_NAME
  psql $DB_CONNECT_STRING --command "DROP DATABASE $DB_NAME"
fi

echo create db $DB_NAME
psql $DB_CONNECT_STRING --command \
     "CREATE DATABASE $DB_NAME WITH OWNER = $ROLE_NAME ENCODING = 'UTF8'"

echo create postgis extension
psql $DB_CONNECT_STRING -d $DB_NAME  --command  \
     "CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public"

echo create stammdaten schema
psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_schema.sql

echo create lada schema
psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/lada_schema.sql
echo set grants
psql $DB_CONNECT_STRING -d $DB_NAME --command \
     "GRANT USAGE ON SCHEMA stammdaten, land TO $ROLE_NAME;
      GRANT USAGE
            ON ALL SEQUENCES IN SCHEMA stammdaten, land TO $ROLE_NAME;
      GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES
            ON ALL TABLES IN SCHEMA stammdaten, land TO $ROLE_NAME;"

if [ "$NO_DATA" != "true" ]; then
    echo import stammdaten.verwaltungseinheit
    psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_data_verwaltungseinheit.sql

    echo import stammdaten
    psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_data.sql

    echo import lada test data
    psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/lada_data.sql

    echo create user $ROLE_NAME
    psql $DB_CONNECT_STRING -d $DB_NAME --command "CREATE SCHEMA geo AUTHORIZATION $ROLE_NAME"

    echo downlaod and import german administrative borders
    TS="0101"
    cd /tmp
    if [ ! -f vg250_${TS}.utm32s.shape.ebenen.zip ]; then
        curl -O \
            http://sg.geodatenzentrum.de/web_download/vg/vg250_${TS}/utm32s/shape/vg250_${TS}.utm32s.shape.ebenen.zip
    fi
    unzip -u vg250_${TS}.utm32s.shape.ebenen.zip "*VG250_GEM*"
#    cd vg250_${TS}.utm32s.shape.ebenen/vg250_ebenen/
    shp2pgsql -s 25832:4326 vg250_${TS}.utm32s.shape.ebenen/vg250_ebenen/VG250_GEM geo.gem_utm | psql -q $DB_CONNECT_STRING -d $DB_NAME
#   rm -rf vg250_${TS}.utm32s.shape.ebenen

    echo fille stammdaten.verwaltungsgrenze
    psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_fill_verwaltungsgrenze.sql

    if [ -f $DIR/lada_auth.sql ]; then
        echo load private auth configuration
        psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/lada_auth.sql
    fi
fi

exec /opt/lada_sql/alter_owner.sh
