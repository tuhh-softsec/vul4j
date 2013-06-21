#! /bin/bash
. `dirname $0`/func.sh

onos stop
cassandra cleandb
cassandra stop
zk stop

zk start
cassandra start
cassandra cleandb
db_status=`cassandra checkdb |grep OK | wc -l`
if [ $db_status != 1 ];then
  echo $db_status
  echo "Cassandra DB was screwed up. Need DB key drop"
  exit
fi
onos start
switch local
#dsh -g $basename 'cd ONOS; ./ctrl-local.sh'
