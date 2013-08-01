#!/bin/bash

# Set paths
ONOS_HOME=`dirname $0`
CASSANDRA_DIR=${HOME}/apache-cassandra-1.2.4
LOGDIR=${ONOS_HOME}/ONOS/onos-logs
CASSANDRA_LOG=${LOGDIR}/cassandara.`hostname`.log

function lotate {
    logfile=$1
    nr_max=${2:-10}
    if [ -f $logfile ]; then
	for i in `seq $(expr $nr_max - 1) -1 1`; do
	    if [ -f ${logfile}.${i} ]; then
		mv -f ${logfile}.${i} ${logfile}.`expr $i + 1`
	    fi
	done
	mv $logfile $logfile.1
    fi
}

function start {
  if [ ! -d ${LOGDIR} ]; then
    mkdir -p ${LOGDIR}
  fi
  echo "rotate log: $log"
  if [ -f $CASSANDRA_LOG ]; then
    lotate $CASSANDRA_LOG
  fi

  # Run cassandra 
  echo "Starting cassandra"
#  echo "[31;48m[WARNING] This script copies conf/cassandra.yaml to $CASSANDRA_DIR/conf/cassandra.yaml (overwrites)[0m"
#  echo "[31;48moriginal cassandra.yaml was backed up as cassandra.yaml.backup[0m"
#  id=`hostid`
#  cp ${CASSANDRA_DIR}/conf/cassandra.yaml $CASSANDRA_DIR/conf/cassandra.yaml.backup
#  cp ${ONOS_HOME}/conf/cassandra.yaml.${id} $CASSANDRA_DIR/conf
  $CASSANDRA_DIR/bin/cassandra > $CASSANDRA_LOG 2>&1 
}

function stop {
  # Kill the existing processes
  capid=`ps -edalf |grep java |grep apache-cassandra | awk '{print $4}'`
  pids="$capid"
  for p in ${pids}; do
    if [ x$p != "x" ]; then
      kill -KILL $p
      echo "Killed existing prosess (pid: $p)"
    fi
  done
}

function deldb {
#   # Delete the berkeley db database
   if [ -d "/tmp/cassandra.titan" ]; then
      echo "deleting berkeley db dir"
      sudo rm -rf /tmp/cassandra.titan
   fi
}

case "$1" in
  start)
    deldb
    cp $ONOS_HOME/conf/cassandra.titan /tmp
    stop
    start 
    ;;
  stop)
    stop
    ;;
#  deldb)
#    deldb
#    ;;
  status)
    n=`ps -edalf |grep java |grep apache-cassandra | wc -l`
    echo "$n instance of cassandra running"
    $CASSANDRA_DIR/bin/nodetool ring 
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
esac
