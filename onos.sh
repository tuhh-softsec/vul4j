#!/bin/bash
# Set paths

ONOS_HOME=`dirname $0`
LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}

ZK_DIR=${HOME}/zookeeper-3.4.5
#ZK_CONF=${ONOS_HOME}/conf/zoo.cfg
ZOO_LOG_DIR=${ONOS_HOME}/onos-logs

RAMCLOUD_DIR=${HOME}/ramcloud
RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}
RAMCLOUD_COORD_LOG=${LOGDIR}/ramcloud.coordinator.`hostname`.log
RAMCLOUD_SERVER_LOG=${LOGDIR}/ramcloud.server.`hostname`.log
coordinatorip=`grep coordinatorIp ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
coordinatorport=`grep coordinatorPort ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
serverip=`grep serverIp ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
serverport=`grep serverPort ${ONOS_HOME}/conf/ramcloud.conf | cut -d "=" -f 2,3`
RAMCLOUD_COORDINATOR=`echo $coordinatorip","$coordinatorport`
RAMCLOUD_SERVER=`echo $serverip","$serverport`
RAMCLOUD_BRANCH=${RAMCLOUD_BRANCH:-master}

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${ONOS_HOME}/lib:${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH}

## Because the script change dir to $ONOS_HOME, we can set ONOS_LOGBACK and LOGDIR relative to $ONOS_HOME
ONOS_LOGBACK=${ONOS_LOGBACK:-${ONOS_HOME}/conf/logback.`hostname`.xml}
ONOS_LOGBACK_TEMPLATE=${ONOS_HOME}/conf/logback.xml.template
LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}
LOGBASE=${ONOS_LOGBASE:-onos.`hostname`}
ONOS_LOG="${LOGDIR}/${LOGBASE}.log"
PCAP_LOG="${LOGDIR}/${LOGBASE}.pcap"
LOGS="$ONOS_LOG $PCAP_LOG"

ONOS_PROPS=${ONOS_PROPS:-${ONOS_HOME}/conf/onos.properties}
JMX_PORT=${JMX_PORT:-7189}

# Set JVM options
JVM_OPTS="${JVM_OPTS:-}"
## If you want JaCoCo Code Coverage reports... uncomment line below
#JVM_OPTS="$JVM_OPTS -javaagent:${ONOS_HOME}/lib/jacocoagent.jar=dumponexit=true,output=file,destfile=${LOGDIR}/jacoco.exec"
JVM_OPTS="$JVM_OPTS -server -d64"
#JVM_OPTS="$JVM_OPTS -XX:+TieredCompilation -XX:InitialCodeCacheSize=512m -XX:ReservedCodeCacheSize=512m"
JVM_OPTS="$JVM_OPTS -Xmx4g -Xms4g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -Xmx2g -Xms2g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -Xmx1g -Xms1g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
JVM_OPTS="$JVM_OPTS -XX:+UseConcMarkSweepGC -XX:+UseAdaptiveSizePolicy -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
JVM_OPTS="$JVM_OPTS -XX:MaxInlineSize=8192 -XX:FreqInlineSize=8192"
JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500 -XX:PreBlockSpin=8"
JVM_OPTS="$JVM_OPTS -XX:OnError=crash-logger" ;# For dumping core
#JVM_OPTS="$JVM_OPTS -Dpython.security.respectJavaAccessibility=false"
JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500 -XX:PreBlockSpin=8 \
		-XX:+UseThreadPriorities \
		-XX:ThreadPriorityPolicy=42 \
		-XX:+UseCompressedOops \
		-Dcom.sun.management.jmxremote.port=$JMX_PORT \
		-Dcom.sun.management.jmxremote.ssl=false \
                -Dbenchmark.measureBP=0 \
                -Dbenchmark.measureRc=0 \
                -Dbenchmark.measureONOS=0 \
		-Dcom.sun.management.jmxremote.authenticate=false"
JVM_OPTS="$JVM_OPTS -Dhazelcast.logging.type=slf4j"

# Set ONOS core main class
MAIN_CLASS="net.onrc.onos.ofcontroller.core.Main"

MVN=${MVN:-mvn -o}

function usage {
  echo "Usage"
  echo "  $0 start {single-node|coord-node|server-node}"
  echo "            single-node: start ONOS with RAMCloud coordinator/server"
  echo "            coord-node : start ONOS with RAMCloud coordinator"
  echo "            server-node: start ONOS with RAMCloud server"
  echo "  $0 stop"
  echo "  $0 restart"
  echo "  $0 status"
  echo "  $0 {zk|rc-coord|rc-server|core} {start|stop|restart|status}"
}

function rotate-log {
    local logfile=$1
    local nr_max=${2:-10}
    if [ -f $logfile ]; then
	for i in `seq $(expr $nr_max - 1) -1 1`; do
	    if [ -f ${logfile}.${i} ]; then
		mv -f ${logfile}.${i} ${logfile}.`expr $i + 1`
	    fi
	done
	mv $logfile $logfile.1
    fi
}

# kill-processes {module-name} {array of pids}
function kill-processes {
  # Kill the existing processes
  local pids=$2
  local n=${#pids[*]}
  if [ $n != 0 ]; then
    echo -n "Stopping $1 ... "
  fi
  for p in ${pids}; do
    if [ x$p != "x" ]; then
      kill -KILL $p
      echo "Killed existing process (pid: $p)"
    fi
  done
}


### Functions related to ZooKeeper
function zk {
  case "$1" in
    start)
      start-zk
      ;;
    stop)
      stop-zk
      ;;
    stat*) # <- status
      status-zk
      ;;
    re*)   # <- restart
      stop-zk
      start-zk
      ;;
    *)
      usage
      exit 1
  esac
}

function start-zk {
  # Run Zookeeper with our configuration
  echo -n "Starting Zookeeper ... "
  $ZK_DIR/bin/zkServer.sh start
}

function stop-zk {
  kill-processes "ZooKeeper" `jps -l | grep org.apache.zookeeper.server | awk '{print $1}'`
}

function status-zk {
  $ZK_DIR/bin/zkServer.sh status
}


### Functions related to RAMCloud coordinator
function rc-coord {
  case "$1" in
    start)
      deldb
      cp $ONOS_HOME/conf/ramcloud.conf /tmp
      stop-coord
      start-coord
      ;;
    startifdown)
      local n=`pgrep coordinator | wc -l`
      if [ $n == 0 ]; then
        start-coord
      else
        echo "$n instance of RAMCloud coordinator running"
      fi
      ;;
    stop)
      stop-coord
      ;;
    stat*) # <- status
      local n=`pgrep -f obj.${RAMCLOUD_BRANCH}/coordinator | wc -l`
      echo "$n RAMCloud coordinator running"
      ;;
    *)
      usage
      exit 1
  esac
}

function start-coord {
  if [ ! -d ${LOGDIR} ]; then
    mkdir -p ${LOGDIR}
  fi
  if [ -f $RAMCLOUD_COORD_LOG ]; then
    rotate-log $RAMCLOUD_COORD_LOG
  fi

  # Run ramcloud 
  echo -n "Starting RAMCloud coordinator ... "
  $RAMCLOUD_DIR/obj.${RAMCLOUD_BRANCH}/coordinator -L $RAMCLOUD_COORDINATOR > $RAMCLOUD_COORD_LOG 2>&1 &
  echo "STARTED"
}


function stop-coord {
  kill-processes "RAMCloud coordinator" `pgrep coordinator | awk '{print $1}'`
}

function deldb {
# TODO: implement
  return
}

### Functions related to RAMCloud server
function rc-server {
  case "$1" in
    start)
      deldb
      cp $ONOS_HOME/conf/ramcloud.conf /tmp
      stop-server
      start-server
      ;;
    startifdown)
      local n=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | wc -l`
      if [ $n == 0 ]; then
        start-server
      else
        echo "$n instance of RAMCloud server running"
      fi
      ;;
    stop)
      stop-server
      ;;
#    deldb)
#      deldb
#      ;;
    stat*) # <- status
      n=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | wc -l`
      echo "$n RAMCloud server running"
      ;;
    *)
      usage
      exit 1
  esac
}

function start-server {
  if [ ! -d ${LOGDIR} ]; then
    mkdir -p ${LOGDIR}
  fi
  if [ -f $RAMCLOUD_SERVER_LOG ]; then
    rotate-log $RAMCLOUD_SERVER_LOG
  fi

  # Run ramcloud
  echo -n "Starting RAMCloud server ... "
  $RAMCLOUD_DIR/obj.${RAMCLOUD_BRANCH}/server -M -L $RAMCLOUD_SERVER -C $RAMCLOUD_COORDINATOR --masterServiceThreads 1 --logCleanerThreads 1 --detectFailures 0 > $RAMCLOUD_SERVER_LOG 2>&1 &
  echo "STARTED"
}

function stop-server {
  kill-processes "RAMCloud server" `pgrep -f obj.${RAMCLOUD_BRANCH}/server | awk '{print $1}'`
}


### Functions related to ONOS core process
function onos {
  CPFILE=${ONOS_HOME}/.javacp.`hostname`
  if [ ! -f ${CPFILE} ]; then
    ${MVN} -f ${ONOS_HOME}/pom.xml compile
  fi
  JAVA_CP=`cat ${CPFILE}`
  JAVA_CP="${JAVA_CP}:${ONOS_HOME}/target/classes"

  case "$1" in
    start)
      stop-onos
      start-onos
      ;;
    startnokill)
      start-onos
      ;;
    startifdown)
      n=`jps -l | grep "${MAIN_CLASS}" | wc -l`
      if [ $n == 0 ]; then
        start-onos
      else
        echo "$n instance of onos running"
      fi
      ;;
    stop)
      stop-onos
      ;;
    stat*) # <- status
      n=`jps -l | grep "${MAIN_CLASS}" | wc -l`
      echo "$n instance of onos running"
      ;;
    *)
      usage
      exit 1
  esac
}

function start-onos {
  if [ ! -d ${LOGDIR} ]; then
    mkdir -p ${LOGDIR}
  fi
  # Backup log files
  for log in ${LOGS}; do
    if [ -f ${log} ]; then
      rotate-log ${log}
    fi
  done

  # Run ONOS
  echo -n "Starting ONOS controller ..."
# Create default logback config file using template
  if [ ! -f ${ONOS_LOGBACK} ]; then
    sed -e "s|__FILENAME__|${ONOS_LOG}|" ${ONOS_LOGBACK_TEMPLATE} > ${ONOS_LOGBACK}
  fi
  java ${JVM_OPTS} -Dlogback.configurationFile=${ONOS_LOGBACK} -cp ${JAVA_CP} ${MAIN_CLASS} -cf ${ONOS_PROPS} > ${LOGDIR}/${LOGBASE}.stdout 2>${LOGDIR}/${LOGBASE}.stderr &
  
  # We need to wait a bit to find out whether starting the ONOS process succeeded
  sleep 1
  
  n=`jps -l |grep "${MAIN_CLASS}" | wc -l`
  if [ "$n" -ge "1" ]; then
    echo " STARTED"
  else
    echo " FAILED"
  fi

#  echo "java ${JVM_OPTS} -Dlogback.configurationFile=${ONOS_LOGBACK} -jar ${ONOS_JAR} -cf ./onos.properties > /dev/null 2>&1 &"
#  sudo -b /usr/sbin/tcpdump -n -i eth0 -s0 -w ${PCAP_LOG} 'tcp port 6633' > /dev/null  2>&1
}

function stop-onos {
  kill-processes "ONOS controller" `jps -l | grep ${MAIN_CLASS} | awk '{print $1}'`
#  kill-processes "tcpdump" `ps -edalf |grep tcpdump |grep ${PCAP_LOG} | awk '{print $4}'`
}


case "$1" in
  start)
    case $2 in
      single-node)
        zk start
        rc-coord startifdown
        rc-server startifdown
        onos startifdown
        ;;
      coord-node)
        zk start
        rc-coord startifdown
        onos startifdown
        ;;
      server-node)
        zk start
        rc-server startifdown
        onos startifdown
        ;;
      *)
        usage
        ;;
      esac
    echo
    ;;
  stop)
    on=`jps -l | grep "${MAIN_CLASS}" | wc -l`
    if [ $on != 0 ]; then
      onos stop
    fi
    
    rcsn=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | wc -l`
    if [ $rcsn != 0 ]; then
      rc-server stop
    fi
    
    rccn=`pgrep coordinator | wc -l`
    if [ $rccn != 0 ]; then
      rc-coord stop
    fi
    
    zkn=`jps -l | grep org.apache.zookeeper.server | wc -l`
    if [ $zkn != 0 ]; then
      zk stop
    fi
    echo
    ;;
  restart)
    on=`jps -l | grep "${MAIN_CLASS}" | wc -l`
    if [ $on != 0 ]; then
      onos stop
    fi
    
    rcsn=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | wc -l`
    if [ $rcsn != 0 ]; then
      rc-server stop
    fi
    
    rccn=`pgrep coordinator | wc -l`
    if [ $rccn != 0 ]; then
      rc-coord stop
    fi
    
    zkn=`jps -l | grep org.apache.zookeeper.server | wc -l`
    if [ $zkn != 0 ]; then
      zk restart
    fi
    
    if [ $rccn != 0 ]; then
      rc-coord startifdown
    fi
    
    if [ $rcsn != 0 ]; then
      rc-server startifdown
    fi
    
    if [ $on != 0 ]; then
      onos startifdown
    fi
    echo
    ;;
  stat*) # <- status
    echo '[ZooKeeper]'
    zk status
    echo
    echo '[RAMCloud coordinator]'
    rc-coord status
    echo
    echo '[RAMCloud server]'
    rc-server status
    echo
    echo '[ONOS core]'
    onos status
    echo
    ;;
  zk)
    zk $2
    ;;
  rc-c*) # <- rc-coordinator
    rc-coord $2
    ;;
  rc-s*) # <- rc-server
    rc-server $2
    ;;
  core)
    onos $2
    ;;
  *)
    usage
    exit 1
esac
