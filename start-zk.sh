#!/bin/bash
# Set paths

ONOS_HOME=`dirname $0`
ZK_DIR=${HOME}/zookeeper-3.4.5
ZK_CONF=${ONOS_HOME}/conf/zoo.cfg

function start {
  # Run Zookeeper with our configuration
  echo "Starting Zookeeper"
  cp $ZK_CONF $ZKDIR/conf
  $ZK_DIR/bin/zkServer.sh start
}

function stop {
  # Kill the existing processes
  pids=`jps -l | grep org.apache.zookeeper.server | awk '{print $1}'`
  for p in ${pids}; do
    if [ x$p != "x" ]; then
      kill -KILL $p
      echo "Killed existing prosess (pid: $p)"
    fi
  done
}
function status {
  $ZK_DIR/bin/zkServer.sh status $ZK_CONF
}

case "$1" in
  start)
    start 
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart)
    stop
    start
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
esac
