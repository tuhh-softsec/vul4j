#!/bin/bash
# Set paths

ONOS_HOME=`dirname $0`
ZK_DIR=${HOME}/zookeeper-3.4.5
ZK_CONF=${ONOS_HOME}/conf/zoo.cfg

function start {
  # Run Zookeeper with our configuration
  echo "Starting Zookeeper"
  echo "[31;48m[WARNING] This script copies conf/zoo.cfg to $ZK_DIR/conf/zoo.cfg (overwrites)[0m"
  echo "[31;48moriginal zoo.cfg was backed up as zoo.cfg.backup[0m"
  if [ $ZK_DIR/conf/zoo.cfg ]; then
    cp $ZK_DIR/conf/zoo.cfg $ZK_DIR/conf/zoo.cfg.backup
  fi
  cp $ZK_CONF $ZK_DIR/conf
  echo "cp $ZK_CONF $ZK_DIR/conf"
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
