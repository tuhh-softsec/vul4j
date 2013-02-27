#! /bin/bash

# Change this accordingly
ONOSDIR=${HOME}/ONOS
script_name="topology_rest.py"

#######################
WEBDIR=${ONOSDIR}/web
restscript=${WEBDIR}/$script_name
LOGDIR=${ONOSDIR}/onos-logs
REST_LOG="${LOGDIR}/rest.`hostname`.log"
#######################

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

function stop {
    pids=`ps -edalf |grep ${script_name} | grep python | grep -v grep | awk '{print $4}'`
    for p in ${pids}; do
	if [ x$p != "x" ]; then
	    sudo kill -KILL $p
	    echo "Killed existing prosess (pid: $p)"
	fi
    done
}

function status {
    nr_process=`ps -edalf |grep ${script_name} | grep python | grep -v grep | wc -l` 
    if [ x${nr_process} != "x" ] ; then
      echo "rest server is running"
    else
      echo "rest server is not running"
    fi
}

function start {
    lotate $REST_LOG 10 
    cd $WEBDIR
    $restscript > $REST_LOG 2>&1 &
}

case "$1" in
  start)
    stop
    start 
    ;;
  stop)
    stop
    ;;
  deldb)
    deldb
    ;;
  status)
    status
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
esac
