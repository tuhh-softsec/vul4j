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

DIR=$(readlink -f $(dirname $0))

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

# Stop on error any execution of SQL via psql
DB_CONNECT_STRING="-v ON_ERROR_STOP=on "

# if variable DB_SRV and otional DB_PORT is set a remote database connection will be used
if [ -n "$DB_SRV" ] ; then DB_CONNECT_STRING="-h $DB_SRV" ; fi
if [ -n "$DB_SRV" -a -n "$DB_PORT"  ] ; then
  DB_CONNECT_STRING="$DB_CONNECT_STRING -p $DB_PORT"
fi
DB_CONNECT_STRING="$DB_CONNECT_STRING -U postgres"
echo "DB_CONNECT_STRING = $DB_CONNECT_STRING"

if [ `psql $DB_CONNECT_STRING -t --quiet --command "SELECT count(*) FROM pg_catalog.pg_user WHERE usename = '$ROLE_NAME'"` -eq 0 ] ; then
  echo create user $ROLE_NAME
  psql $DB_CONNECT_STRING --command "CREATE USER $ROLE_NAME PASSWORD '$ROLE_PW';"
fi

if [ "$DROP_DB" = "true" ] && psql $DB_CONNECT_STRING -l | grep -q "^ $DB_NAME " ; then
  echo drop db $DB_NAME
  psql $DB_CONNECT_STRING --command "DROP DATABASE $DB_NAME"
fi

echo create db $DB_NAME
psql $DB_CONNECT_STRING --command \
     "CREATE DATABASE $DB_NAME ENCODING = 'UTF8'"

echo create postgis extension
psql $DB_CONNECT_STRING -d $DB_NAME  --command  \
     "CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public"

echo create tablefunc extension
psql $DB_CONNECT_STRING -d $DB_NAME  --command  \
     "CREATE EXTENSION IF NOT EXISTS tablefunc WITH SCHEMA public"

echo create version table
psql $DB_CONNECT_STRING -d $DB_NAME \
     -f $DIR/updates/0000/01.add_schema_version.sql
for d in "$DIR"/updates/* ; do
  new_ver=$( basename $d )
done
psql $DB_CONNECT_STRING -d $DB_NAME --command \
     "INSERT INTO lada_schema_version(version) VALUES ($new_ver)"

echo create stammdaten schema
psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_schema.sql

echo create lada schema
psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/lada_schema.sql

echo create audit-trail table/trigger/views
psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/audit_stamm.sql
psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/audit_land.sql

echo set grants
psql $DB_CONNECT_STRING -d $DB_NAME --command \
     "GRANT USAGE ON SCHEMA stamm, land TO $ROLE_NAME;
      GRANT USAGE
            ON ALL SEQUENCES IN SCHEMA stamm, land TO $ROLE_NAME;
      GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES
            ON ALL TABLES IN SCHEMA stamm, land TO $ROLE_NAME;"

if [ "$NO_DATA" != "true" ]; then
    echo "load data:"
    for file in \
        stammdaten_data_status_reihenfolge.sql \
        stammdaten_data_verwaltungseinheit.sql \
        stammdaten_data_netzbetreiber.sql \
        stammdaten_data_mess_stelle.sql \
        stammdaten_data_auth.sql \
        stammdaten_data_betriebsart.sql \
        stammdaten_data_mess_einheit.sql \
        stammdaten_data_mass_einheit_umrechnung.sql \
        stammdaten_data_umwelt.sql \
        stammdaten_data_auth_lst_umw.sql \
        stammdaten_data_datenbasis.sql \
        stammdaten_data_datensatz_erzeuger.sql \
        stammdaten_data_deskriptor_umwelt.sql \
        stammdaten_data_deskriptoren.sql \
        stammdaten_data_koordinaten_art.sql \
        stammdaten_data_messmethode.sql \
        stammdaten_data_messgroesse.sql \
        stammdaten_data_messgroessen_gruppe.sql \
        stammdaten_data_ort_typ.sql \
        stammdaten_data_staat.sql \
        stammdaten_data_kta.sql \
        stammdaten_data_ortszuordnung_typ.sql \
        stammdaten_data_pflicht_messgroesse.sql \
        stammdaten_data_proben_zusatz.sql \
        stammdaten_data_probenart.sql \
        stammdaten_data_messprogramm_transfer.sql \
        stammdaten_data_ortszusatz.sql \
        stammdaten_data_messprogramm_kategorie.sql \
        stammdaten_data_gemeindeuntergliederung.sql \
        stammdaten_data_rei.sql \
        stammdaten_data_ort.sql \
        stammdaten_data_probenehmer.sql \
        stammdaten_data_zeitbasis.sql \
        stammdaten_data_query.sql \
        stammdaten_data_user_context.sql \
        stammdaten_data_importer_config.sql \
        lada_data.sql \
        lada_messprogramm.sql \
        lada_views.sql
    do
        [ -f private_${file} ] && file=private_${file}
        echo "  ${file%.sql}"
        psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/$file
    done

    echo init sequences
    psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_init_sequences.sql

    echo create schema geo
    psql $DB_CONNECT_STRING -d $DB_NAME --command "CREATE SCHEMA geo AUTHORIZATION $ROLE_NAME"

    echo download and import german administrative borders
    TS="0101"
    cd /tmp
    if [ ! -f vg250_${TS}.utm32s.shape.ebenen.zip ]; then
        curl -fO \
            http://sg.geodatenzentrum.de/web_download/vg/vg250_${TS}/utm32s/shape/vg250_${TS}.utm32s.shape.ebenen.zip
    fi
    unzip -u vg250_${TS}.utm32s.shape.ebenen.zip "*VG250_*"

    shp2pgsql -s 25832:4326 vg250_${TS}.utm32s.shape.ebenen/vg250_ebenen/VG250_GEM geo.vg250_gem | psql -q $DB_CONNECT_STRING -d $DB_NAME
    shp2pgsql -s 25832:4326 vg250_${TS}.utm32s.shape.ebenen/vg250_ebenen/VG250_KRS geo.vg250_kr | psql -q $DB_CONNECT_STRING -d $DB_NAME
    shp2pgsql -s 25832:4326 vg250_${TS}.utm32s.shape.ebenen/vg250_ebenen/VG250_RBZ geo.vg250_rb | psql -q $DB_CONNECT_STRING -d $DB_NAME
    shp2pgsql -s 25832:4326 vg250_${TS}.utm32s.shape.ebenen/vg250_ebenen/VG250_LAN geo.vg250_bl | psql -q $DB_CONNECT_STRING -d $DB_NAME

    echo create verwaltungsgrenze view
    psql -q $DB_CONNECT_STRING -d $DB_NAME -f $DIR/stammdaten_verwaltungsgrenze_view.sql
fi
