#!/bin/bash

### Env vars used by this script. (default value) ###
# $ONOS_HOME       : path of root directory of ONOS repository (this script's dir)
# $ONOS_CONF_DIR   : path of ONOS config directory (~/ONOS/conf)
# $ONOS_CONF       : path of ONOS node config file (~/ONOS/conf/onos_node.conf)
# $ONOS_PROPS      : path of ONOS properties file (~/ONOS/conf/onos.properties)
# $ONOS_LOGBACK    : path of logback config file (~/ONOS/conf/logback.`hostname`.xml)
# $LOGDIR          : path of log output directory (~/ONOS/onos-logs)
# $LOGBASE         : base name of log output file (onos.`hostname`)
# $RAMCLOUD_HOME   : path of root directory of RAMCloud repository (~/ramcloud)
# $RAMCLOUD_BRANCH : branch name of RAMCloud to use (master)
# $JVM_OPTS        : JVM options ONOS starts with
#####################################################


### Variables read from ONOS config file ###
ONOS_HOME=${ONOS_HOME:-$(cd `dirname $0`; pwd)}
ONOS_CONF_DIR=${ONOS_CONF_DIR:-${ONOS_HOME}/conf}
ONOS_CONF=${ONOS_CONF:-${ONOS_CONF_DIR}/onos_node.conf}

if [ ! -f ${ONOS_CONF} ]; then
  echo "${ONOS_CONF} not found."
  exit 1
fi
ONOS_HOST_NAME=`grep ^host.name ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
if [ -z "${ONOS_HOST_NAME}" ]; then
  ONOS_HOST_NAME=`hostname`
fi
ONOS_HOST_IP=`grep ^host.ip ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
ONOS_HOST_ROLE=`grep ^host.role ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
ONOS_HOST_BACKEND=`grep ^host.backend ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
ZK_HOSTS=`grep ^zookeeper.hosts ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
RC_COORD_PROTOCOL=`grep ^ramcloud.coordinator.protocol ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
RC_COORD_IP=`grep ^ramcloud.coordinator.ip ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
RC_COORD_PORT=`grep ^ramcloud.coordinator.port ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
RC_SERVER_PROTOCOL=`grep ^ramcloud.server.protocol ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
RC_SERVER_IP=`grep ^ramcloud.server.ip ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
RC_SERVER_PORT=`grep ^ramcloud.server.port ${ONOS_CONF} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
############################################


############## Other variables #############
LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}

ZK_DIR=${HOME}/zookeeper-3.4.5
ZK_CONF_FILE=zoo.cfg
ZK_CONF=${ONOS_CONF_DIR}/${ZK_CONF_FILE}
ZK_CONF_BACKUP=${ZK_CONF}.bak
ZK_CONF_TEMPLATE=${ONOS_CONF_DIR}/zoo.cfg.template
ZOO_LOG_DIR=${ONOS_HOME}/onos-logs
ZK_LIB_DIR=/var/lib/zookeeper
ZK_MY_ID=${ZK_LIB_DIR}/myid

RAMCLOUD_DIR=${HOME}/ramcloud
RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}
RAMCLOUD_COORD_LOG=${LOGDIR}/ramcloud.coordinator.${ONOS_HOST_NAME}.log
RAMCLOUD_SERVER_LOG=${LOGDIR}/ramcloud.server.${ONOS_HOST_NAME}.log
RAMCLOUD_BRANCH=${RAMCLOUD_BRANCH:-master}
RAMCLOUD_COORD_PORT=12246
RAMCLOUD_SERVER_PORT=12242

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${ONOS_HOME}/lib:${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH}

## Because the script change dir to $ONOS_HOME, we can set ONOS_LOGBACK and LOGDIR relative to $ONOS_HOME
ONOS_LOGBACK=${ONOS_LOGBACK:-${ONOS_CONF_DIR}/logback.${ONOS_HOST_NAME}.xml}
ONOS_LOGBACK_BACKUP=${ONOS_LOGBACK}.bak
ONOS_LOGBACK_TEMPLATE=${ONOS_CONF_DIR}/logback.xml.template
LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}
LOGBASE=${ONOS_LOGBASE:-onos.${ONOS_HOST_NAME}}
ONOS_LOG="${LOGDIR}/${LOGBASE}.log"
PCAP_LOG="${LOGDIR}/${LOGBASE}.pcap"
LOGS="$ONOS_LOG $PCAP_LOG"

ONOS_PROPS=${ONOS_PROPS:-${ONOS_CONF_DIR}/onos.properties}
JMX_PORT=${JMX_PORT:-7189}

# Set JVM options
JVM_OPTS="${JVM_OPTS:-}"
JVM_OPTS="$JVM_OPTS -server -d64"
#JVM_OPTS="$JVM_OPTS -XX:+TieredCompilation -XX:InitialCodeCacheSize=512m -XX:ReservedCodeCacheSize=512m"

# Uncomment or specify appropriate value as JVM_OPTS environment variables.
#JVM_OPTS="$JVM_OPTS -Xmx4g -Xms4g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -Xmx2g -Xms2g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -Xmx1g -Xms1g -Xmn800m"

#JVM_OPTS="$JVM_OPTS -XX:+UseParallelGC"
JVM_OPTS="$JVM_OPTS -XX:+UseConcMarkSweepGC"
JVM_OPTS="$JVM_OPTS -XX:+AggressiveOpts"

# We may want to remove UseFastAccessorMethods option: http://bugs.java.com/view_bug.do?bug_id=6385687
JVM_OPTS="$JVM_OPTS -XX:+UseFastAccessorMethods"

JVM_OPTS="$JVM_OPTS -XX:MaxInlineSize=8192"
JVM_OPTS="$JVM_OPTS -XX:FreqInlineSize=8192"
JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500"

JVM_OPTS="$JVM_OPTS -XX:OnError=crash-logger" ;# For dumping core

# Workaround for Thread Priority http://tech.stolsvik.com/2010/01/linux-java-thread-priorities-workaround.html
JVM_OPTS="$JVM_OPTS -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42"

JVM_OPTS="$JVM_OPTS -XX:+UseCompressedOops"

JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT"
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.ssl=false"
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.authenticate=false"

JVM_OPTS="$JVM_OPTS -Dhazelcast.logging.type=slf4j"

# Uncomment to dump final JVM flags to stdout
#JVM_OPTS="$JVM_OPTS -XX:+PrintFlagsFinal"

# Set ONOS core main class
MAIN_CLASS="net.onrc.onos.core.main.Main"

MVN=${MVN:-mvn -o}
############################################


############# Common functions #############
function print_usage {
  local filename=`basename ${ONOS_CONF}`
  local usage="Usage: setup/start/stop ONOS on this server.
 \$ $0 setup [-f]
    Set up ONOS node using ${filename}.
      - generate and replace config file of ZooKeeper.
      - create myid in ZooKeeper datadir.
      - generate and replace logback.${ONOS_HOST_NAME}.xml
    If -f option is used, all existing files will be overwritten without confirmation.
 \$ $0 start [single-node|coord-node|server-node|coord-and-server-node]
    Start ONOS node with specific RAMCloud entities
      - single-node: start ONOS with stand-alone RAMCloud
      - coord-node : start ONOS with RAMCloud coordinator
      - server-node: start ONOS with RAMCloud server
      - coord-and-server-node: start ONOS with RAMCloud coordinator and server
      * Default behavior can be defined by ${filename}
 \$ $0 stop
    Stop all ONOS-related processes
 \$ $0 restart
    Stop and start currently running ONOS-related processes
 \$$0 status
    Show status of ONOS-related processes
 \$ $0 {zk|rc-coord|rc-server|core} {start|stop|restart|status}
    Control specific ONOS-related process"
  
  echo "${usage}"	
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
  if [ ! -z "$pids" ]; then
    echo -n "Stopping $1 ... "
  fi
  for p in ${pids}; do
    if [ x$p != "x" ]; then
      kill -KILL $p
      echo "Killed existing process (pid: $p)"
    fi
  done
}

function revert-conf {
  set -e
  
  echo -n "ERROR occurred ... "
  local temp_zk="${ZK_CONF}.tmp"
  
  if [ -f "${temp_zk}" ]; then
    local zk_file=`basename ${ZK_CONF}`
    echo -n "reverting ${zk_file} ... "
    rm ${temp_zk}
    mv ${ZK_CONF_BACKUP} ${ZK_CONF}
  fi

  echo "EXIT"
  
  if [ ! -z "$1" ]; then
    echo $1
  fi
  
  set +e
  
  exit 1
}

function create-zk-conf {
  echo -n "Creating ${ZK_CONF} ... "
  
  # creation of zookeeper config
  
  local temp_zk="${ZK_CONF}.tmp"
  touch ${temp_zk}
  
  if [ -f ${ZK_CONF} ]; then
    mv ${ZK_CONF} ${ZK_CONF_BACKUP}
    local backup_file=`basename ${ZK_CONF_BACKUP}`
    echo -n "backup old file to ${backup_file} ... "
  fi
  
  local hosts=${ZK_HOSTS}
  if [ -z "${hosts}" ]; then
    # assume single-node mode
    hosts=${ONOS_HOST_NAME}
  fi
  
  hostarr=`echo ${hosts} | tr "," " "`
  
  local i=1
  local myid=
  for host in ${hostarr}; do
    if [ ${host} = ${ONOS_HOST_NAME} ]; then
      myid=$i
      break
    fi
    i=`expr $i + 1`
  done
  
  if [ -z "${myid}" ]; then
    local filename=`basename ${ONOS_CONF}`
    revert-conf "[ERROR in ${filename}] zookeeper.hosts must have hostname \"${ONOS_HOST_NAME}\""
  fi
  
  # TODO: Remove sudo.
  # This is temporary code for the sake of compatibility with old code (which creates myid with root user).
  sudo mv ${ZK_MY_ID} ${ZK_MY_ID}.old
  echo ${myid} > ${ZK_MY_ID}
  
  echo -n "myid is assigned to ${myid} ... "
  
  while read line; do
    if [[ $line =~ ^__HOSTS__$ ]]; then
      i=1
      for host in ${hostarr}; do
        # TODO: ports might be configurable
        local hostline="server.${i}=${host}:2888:3888"
        echo $hostline >> "${temp_zk}"
        i=`expr $i + 1`
      done
    elif [[ $line =~ __DATADIR__ ]]; then
      echo $line | sed -e "s|__DATADIR__|${ZK_LIB_DIR}|" >> ${temp_zk}
    else
      echo $line >> ${temp_zk}
    fi
  done < ${ZK_CONF_TEMPLATE}
  mv ${temp_zk} ${ZK_CONF}
  
  echo "DONE"
}

function create-logback-conf {
  echo -n "Creating ${ONOS_LOGBACK} ... "
  
  # creation of logback config
  if [ -f $ONOS_LOGBACK ]; then
    local logback_file=`basename ${ONOS_LOGBACK}`
    mv ${ONOS_LOGBACK} ${ONOS_LOGBACK_BACKUP}
    local logback_back_file=`basename ${ONOS_LOGBACK_BACKUP}`
    echo -n "backup old file to ${logback_back_file} ... "
  fi
  sed -e "s|__FILENAME__|${ONOS_LOG}|" ${ONOS_LOGBACK_TEMPLATE} > ${ONOS_LOGBACK}
  
  echo "DONE"
}

function create-conf {
  local key
  local filename
  
  trap revert-conf ERR
  
  if [ -f ${ZK_CONF} -a "$1" != "-f" ]; then
    # confirmation to overwrite existing config file
    echo -n "Overwriting ${ZK_CONF_FILE} [Y/n]? "
    while [ 1 ]; do
      read key
      if [ -z "${key}" -o "${key}" == "Y" ]; then
        create-zk-conf
        break
      elif [ "${key}" == "n" ]; then
        break
      fi
      echo "[Y/n]?"
    done
  else
    create-zk-conf
  fi
  
  if [ -f ${ONOS_LOGBACK} -a "$1" != "-f" ]; then
    filename=`basename ${ONOS_LOGBACK}`
    echo -n "Overwriting ${filename} [Y/n]? "
    while [ 1 ]; do
      read key
      if [ -z "${key}" -o "${key}" == "Y" ]; then
        create-logback-conf
        break
      elif [ "${key}" == "n" ]; then
        break
      fi
      echo "[Y/n]?"
    done
  else
    create-logback-conf
  fi
  
  trap - ERR
}
############################################


###### Functions related to ZooKeeper ######
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
      print_usage
      exit 1
  esac
}

function start-zk {
  echo -n "Starting Zookeeper ... "
  
  if [ -f "${ZK_CONF}" ]; then
    # Run Zookeeper with our configuration
    export ZOOCFG=${ZK_CONF_FILE}
    export ZOOCFGDIR=${ONOS_CONF_DIR}
  fi
  
  $ZK_DIR/bin/zkServer.sh start
}

function stop-zk {
  kill-processes "ZooKeeper" `jps -l | grep org.apache.zookeeper.server | awk '{print $1}'`
}

function status-zk {
  if [ -f ${ZK_CONF} ]; then
    export ZOOCFG=${ZK_CONF_FILE}
    export ZOOCFGDIR=${ONOS_CONF_DIR}
  fi
  
  $ZK_DIR/bin/zkServer.sh status
}
############################################


####### Functions related to RAMCloud ######
function start-backend {
  if [ "${ONOS_HOST_BACKEND}" = "ramcloud" ]; then
    if [ $1 == "coord" ]; then
      rc-coord startifdown
    elif [ $1 == "server" ]; then
      rc-server startifdown
    fi
  fi
}

function stop-backend {
  rcsn=`pgrep -f obj.${RAMCLOUD_BRANCH}/server | wc -l`
  if [ $rcsn != 0 ]; then
    rc-server stop
  fi
  
  rccn=`pgrep -f obj.${RAMCLOUD_BRANCH}/coordinator | wc -l`
  if [ $rccn != 0 ]; then
    rc-coord stop
  fi
}

function deldb {
# TODO: implement
  return
}


### Functions related to RAMCloud coordinator
function rc-coord-addr {
  local coordproto=${RC_COORD_PROTOCOL}
  local coordip=${RC_COORD_IP}
  local coordport=${RC_COORD_PORT}

  if [ -z "${coordproto}" ]; then
    coordproto='fast+udp'
  fi

  if [ -z "${coordip}" ]; then
    # assume single-node mode
    coordip=${ONOS_HOST_IP}
  fi
  
  if [ -z "${coordport}" ]; then
    # assume default port
    coordport=${RAMCLOUD_COORD_PORT}
  fi
  
  echo "${coordproto}:host=${coordip},port=${coordport}"
}

function rc-server-addr {
  local serverproto=${RC_SERVER_PROTOCOL}
  local serverip=${RC_SERVER_IP}
  local serverport=${RC_SERVER_PORT}

  if [ -z "${serverproto}" ]; then
    serverproto='fast+udp'
  fi

  # Normally this parameter should be null
  if [ -z "${serverip}" ]; then
    serverip=${ONOS_HOST_IP}
  fi
  
  if [ -z "${serverport}" ]; then
    # assume default port
    serverport=${RAMCLOUD_SERVER_PORT}
  fi
  
  echo "${serverproto}:host=${serverip},port=${serverport}"
}

function rc-coord {
  case "$1" in
    start)
      deldb
      stop-coord
      start-coord
      ;;
    startifdown)
      local n=`pgrep -f obj.${RAMCLOUD_BRANCH}/coordinator | wc -l`
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
      print_usage
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
  
  local coord_addr=`rc-coord-addr`

  # Run ramcloud 
  echo -n "Starting RAMCloud coordinator ... "
  $RAMCLOUD_DIR/obj.${RAMCLOUD_BRANCH}/coordinator -L ${coord_addr} > $RAMCLOUD_COORD_LOG 2>&1 &
  echo "STARTED"
}


function stop-coord {
  kill-processes "RAMCloud coordinator" `pgrep -f obj.${RAMCLOUD_BRANCH}/coordinator`
}

### Functions related to RAMCloud server
function rc-server {
  case "$1" in
    start)
      deldb
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
      print_usage
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
  
  local coord_addr=`rc-coord-addr`
  local server_addr=`rc-server-addr`

  # Run ramcloud
  echo -n "Starting RAMCloud server ... "
  ${RAMCLOUD_DIR}/obj.${RAMCLOUD_BRANCH}/server -M -L ${server_addr} -C ${coord_addr} --masterServiceThreads 1 --logCleanerThreads 1 --detectFailures 0 > $RAMCLOUD_SERVER_LOG 2>&1 &
  echo "STARTED"
}

function stop-server {
  kill-processes "RAMCloud server" `pgrep -f obj.${RAMCLOUD_BRANCH}/server`
}
############################################


## Functions related to ONOS core process ##
function onos {
  CPFILE=${ONOS_HOME}/.javacp.${ONOS_HOST_NAME}
  if [ ! -f ${CPFILE} ]; then
    echo "ONOS core needs to be built"
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
      print_usage
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
  
  if [ ! -f ${ONOS_LOGBACK} ]; then
    echo "[WARNING] ${ONOS_LOGBACK} not found."
    echo "          Run \"\$ $0 setup\" to create."
    exit 1
  fi

  JVM_OPTS="${JVM_OPTS} -Dnet.onrc.onos.datastore.backend=${ONOS_HOST_BACKEND}"
  
  # Run ONOS
  
  echo -n "Starting ONOS controller ..."
  java ${JVM_OPTS} -Dlogback.configurationFile=${ONOS_LOGBACK} -cp ${JAVA_CP} ${MAIN_CLASS} -cf ${ONOS_PROPS} > ${LOGDIR}/${LOGBASE}.stdout 2>${LOGDIR}/${LOGBASE}.stderr &
  
  # We need to wait a bit to find out whether starting the ONOS process succeeded
  sleep 1
  
  n=`jps -l |grep "${MAIN_CLASS}" | wc -l`
  if [ $n -ge 1 ]; then
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
############################################


################## Main ####################
case "$1" in
  setup)
    create-conf $2
    ;;
  start)
    mode_parameter=${ONOS_HOST_ROLE}
    if [ ! -z "$2" ]; then
      mode_parameter=$2
    fi
    
    case "${mode_parameter}" in
      single-node)
        zk start
        start-backend coord
        start-backend server
        onos startifdown
        ;;
      coord-node)
        zk start
        start-backend coord
        onos startifdown
        ;;
      server-node)
        zk start
        start-backend server
        onos startifdown
        ;;
      coord-and-server-node)
        zk start
        start-backend coord
        start-backend server
        onos startifdown
        ;;
      *)
        print_usage
        ;;
      esac
    echo
    ;;
  stop)
    on=`jps -l | grep "${MAIN_CLASS}" | wc -l`
    if [ $on != 0 ]; then
      onos stop
    fi
    
    stop-backend
    
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
    
    rccn=`pgrep -f obj.${RAMCLOUD_BRANCH}/coordinator | wc -l`
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
    print_usage
    exit 1
esac

