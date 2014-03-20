#!/bin/bash

echo
echo "========================================================================="
echo "[WARNING] This script is deprecated. Use \"\$ ./onos.sh rc-server\" instead."
echo "========================================================================="
echo

ulimit -c unlimited

# Set paths
ONOS_HOME=`dirname $0`
RAMCLOUD_DIR=${HOME}/ramcloud
LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}
RAMCLOUD_LOG=${LOGDIR}/ramcloud.server.`hostname`.log
RAMCLOUD_CONF=${RAMCLOUD_CONF:-${ONOS_HOME}/conf/ramcloud.conf}

#coordinatorip=`grep coordinatorIp ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
#coordinatorport=`grep coordinatorPort ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
#RAMCLOUD_COORDINATOR=`echo $coordinatorip","$coordinatorport`
COORDINATOR_IP=`grep coordinatorIp ${RAMCLOUD_CONF} | cut -d "=" -f 2,3`
COORDINATOR_PORT=`grep coordinatorPort ${RAMCLOUD_CONF} | cut -d "=" -f 2,3`
RAMCLOUD_COORDINATOR=`echo $COORDINATOR_IP","$COORDINATOR_PORT`

#serverip=`grep serverIp ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
#serverport=`grep serverPort ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
#RAMCLOUD_SERVER=`echo $serverip","$serverport`

SERVER_IP=`grep serverIp ${RAMCLOUD_CONF} | cut -d "=" -f 2,3`
SERVER_PORT=`grep serverPort ${RAMCLOUD_CONF} | cut -d "=" -f 2,3`
RAMCLOUD_SERVER=`echo $SERVER_IP","$SERVER_PORT`

RAMCLOUD_BRANCH=${RAMCLOUD_BRANCH:-master}

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
  if [ -f $RAMCLOUD_LOG ]; then
    lotate $RAMCLOUD_LOG
  fi

  # Run ramcloud
  echo "Starting ramcloud"
  $RAMCLOUD_DIR/obj.${RAMCLOUD_BRANCH}/server -M -L $RAMCLOUD_SERVER -C $RAMCLOUD_COORDINATOR --masterServiceThreads 1 --logCleanerThreads 1 --detectFailures 0 > $RAMCLOUD_LOG 2>&1 &
}

function stop {
  # Kill the existing processes
  capid=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | awk '{print $1}'`
  pids="$capid"
  for p in ${pids}; do
    if [ x$p != "x" ]; then
      kill -KILL $p
      echo "Killed existing process (pid: $p)"
    fi
  done
}

case "$1" in
  start)
    #cp $ONOS_HOME/conf/ramcloud.conf /tmp
    stop
    start 
    ;;
  stop)
    stop
    ;;
  status)
    n=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | wc -l`
    echo "$n ramcloud server running"
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
esac
