#!/bin/bash -e
# SYNOPSIS
# ./update-db.sh [DB_NAME]
#   DB_NAME    name of the databaes (default = lada)
#
# There will be used a remote database server if there exists the
# enviroment variable DB_SRV and optional DB_PORT

DIR=$(readlink -f $(dirname $0))

DB_NAME=${1:-lada}
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

get_version()
{
  local ver
  if ver=$(psql -qtA $DB_CONNECT_STRING -d $DB_NAME \
                -c 'SELECT max(version) FROM lada_schema_version'
           2>/dev/null)
  then
    echo ${ver:--1}
  else
    echo '-1'
  fi
}

current_ver=$( get_version )
for d in "$DIR"/updates/* ; do
  new_ver=$( basename $d )
  if [ -d "$d" ] && [ "$new_ver" -gt $current_ver ] ; then
    echo "Running updates for $new_ver ..."

    file_args=""
    for f in "$d"/*.sql ; do
      file_args+=" -f $f"
    done

    psql -1q $DB_CONNECT_STRING -d $DB_NAME $file_args \
      -c "INSERT INTO lada_schema_version(version) VALUES ($new_ver)"

  fi
done
