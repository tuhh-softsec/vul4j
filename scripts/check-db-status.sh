#! /bin/bash
DIR=~/ONOS
status=`~/titan-0.2.0/bin/gremlin.sh -e $DIR/scripts/check-db-clean | grep null | wc -l`
if [ $status == 0 ]; then
  echo "OK"
else
  echo "BAD"
fi
