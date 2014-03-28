#! /bin/bash

# Change this accordingly
ONOS_HOME=${ONOS_HOME:-${HOME}/ONOS}
SCRIPT_NAME="topology_rest.py"

#######################
SCRIPT_DIR=${ONOS_HOME}/sample/ONS2014demo/scripts
REST_SCRIPT=${SCRIPT_DIR}/${SCRIPT_NAME}
LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}
REST_LOG="${LOGDIR}/ons2014demo_rest.`hostname`.log"
#######################

dokill() {
    for cpid in $(ps -o pid= --ppid $1)
    do 
        dokill $cpid
    done
    echo "killing: $(ps -p $1 -o cmd=)"
    kill -9 $1 > /dev/null 2>&1
}


function rotate {
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
    pids=`ps -edalf | grep ${SCRIPT_NAME} | grep python | grep -v grep | awk '{print $4}'`
    for p in ${pids}; do
	if [ x$p != "x" ]; then
            dokill $p
#	    sudo kill -KILL $p
#	    echo "Killed existing prosess (pid: $p)"
	fi
    done
}

function status {
    nr_process=`ps -edalf | grep ${SCRIPT_NAME} | grep python | grep -v grep | wc -l` 
    if [ ${nr_process} != 0 ] ; then
      echo "rest server is running"
    else
      echo "rest server is not running"
    fi
}

function start {
    rotate $REST_LOG 10 
    cd ${SCRIPT_DIR}
    # Make log dir for iperf log files
    if [ ! -d  log ]; then
      mkdir log
    fi
    $REST_SCRIPT > $REST_LOG 2>&1 &
}

case "$1" in
  start)
    stop
    sleep 2
    start 
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  *)
    echo "Usage: $0 {start|stop|status}"
    exit 1
esac
