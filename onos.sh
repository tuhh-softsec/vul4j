#!/bin/bash

### Env vars used by this script. (default value) ###
# $ONOS_HOME       : path of root directory of ONOS repository (this script's dir)
# $ONOS_CONF_DIR   : path of ONOS config directory (~/ONOS/conf)
# $ONOS_CONF       : path of ONOS node config file (~/ONOS/conf/onos_node.`hostname`.conf or onos_node.conf)
# $ONOS_PROPS      : path of ONOS properties file (~/ONOS/conf/onos.properties)
# $ONOS_LOGBACK    : path of logback config file (~/ONOS/conf/logback.`hostname`.xml)
# $LOGDIR          : path of log output directory (~/ONOS/onos-logs)
# $LOGBASE         : base name of log output file (onos.`hostname`)
# $RAMCLOUD_HOME   : path of root directory of RAMCloud repository (~/ramcloud)
# $RAMCLOUD_BRANCH : branch name of RAMCloud to use (master)
# $ZK_HOME         : path of root directory of ZooKeeper (~/zookeeper-3.4.5)
# $ZK_LIB_DIR      : path of ZooKeeper library (/var/lib/zookeeper)
# $JVM_OPTS        : JVM options ONOS starts with
#####################################################


# read-conf {filename} {parameter name} [default value]
function read-conf {
  local value=`grep ^${2} ${1} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
  if [ -z "${value}" ]; then
    echo $3
  else
    echo ${value}
  fi
}

ONOS_HOME=${ONOS_HOME:-$(cd `dirname $0`; pwd)}
ONOS_CONF_DIR=${ONOS_CONF_DIR:-${ONOS_HOME}/conf}
ONOS_CONF=${ONOS_CONF:-${ONOS_CONF_DIR}/onos_node.`hostname`.conf}

if [ ! -f ${ONOS_CONF} ]; then
  # falling back to default config file
  ONOS_CONF=${ONOS_CONF_DIR}/onos_node.conf
  if [ ! -f ${ONOS_CONF} ]; then
    echo "${ONOS_CONF} not found."
    exit 1
  fi
fi


### Variables read from ONOS config file ###
ONOS_HOST_NAME=$(read-conf ${ONOS_CONF}     host.name                     `hostname`)
ONOS_HOST_IP=$(read-conf ${ONOS_CONF}       host.ip)
ONOS_HOST_ROLE=$(read-conf ${ONOS_CONF}     host.role)
ONOS_HOST_BACKEND=$(read-conf ${ONOS_CONF}  host.backend)
ZK_HOSTS=$(read-conf ${ONOS_CONF}           zookeeper.hosts               ${ONOS_HOST_NAME})
RC_COORD_PROTOCOL=$(read-conf ${ONOS_CONF}  ramcloud.coordinator.protocol "fast+udp")
RC_COORD_IP=$(read-conf ${ONOS_CONF}        ramcloud.coordinator.ip       ${ONOS_HOST_IP})
RC_COORD_PORT=$(read-conf ${ONOS_CONF}      ramcloud.coordinator.port     12246)
RC_SERVER_PROTOCOL=$(read-conf ${ONOS_CONF} ramcloud.server.protocol      "fast+udp")
RC_SERVER_IP=$(read-conf ${ONOS_CONF}       ramcloud.server.ip            ${ONOS_HOST_IP})
RC_SERVER_PORT=$(read-conf ${ONOS_CONF}     ramcloud.server.port          12242)
HC_TCPIP_MEMBERS=$(read-conf ${ONOS_CONF}   hazelcast.tcp-ip.members)
HC_MULTICAST_GROUP=$(read-conf ${ONOS_CONF} hazelcast.multicast.group     "224.2.2.3")
HC_MULTICAST_PORT=$(read-conf ${ONOS_CONF}  hazelcast.multicast.port      54327)
############################################


############## Other variables #############
ONOS_TEMPLATE_DIR=${ONOS_CONF_DIR}/template

LOGDIR=${ONOS_LOGDIR:-${ONOS_HOME}/onos-logs}

ZK_HOME=${ZK_HOME:-~/zookeeper-3.4.5}
ZK_CONF_FILE=zoo.cfg
ZK_CONF=${ONOS_CONF_DIR}/${ZK_CONF_FILE}
ZK_CONF_TEMPLATE=${ONOS_TEMPLATE_DIR}/zoo.cfg.template
ZK_LOG_DIR=${ONOS_HOME}/onos-logs
ZK_LIB_DIR=${ZK_LIB_DIR:-/var/lib/zookeeper}
ZK_MY_ID=${ZK_LIB_DIR}/myid

HC_CONF=${ONOS_CONF_DIR}/hazelcast.xml
HC_CONF_TEMPLATE=${ONOS_TEMPLATE_DIR}/hazelcast.xml.template

RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}
RAMCLOUD_COORD_LOG=${LOGDIR}/ramcloud.coordinator.${ONOS_HOST_NAME}.log
RAMCLOUD_SERVER_LOG=${LOGDIR}/ramcloud.server.${ONOS_HOST_NAME}.log
RAMCLOUD_BRANCH=${RAMCLOUD_BRANCH:-master}

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${ONOS_HOME}/lib:${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH}

## Because the script change dir to $ONOS_HOME, we can set ONOS_LOGBACK and LOGDIR relative to $ONOS_HOME
ONOS_LOGBACK=${ONOS_LOGBACK:-${ONOS_CONF_DIR}/logback.${ONOS_HOST_NAME}.xml}
ONOS_LOGBACK_BACKUP=${ONOS_LOGBACK}.bak
ONOS_LOGBACK_TEMPLATE=${ONOS_TEMPLATE_DIR}/logback.xml.template
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

function handle-error {
  set -e
  
  revert-confs
  
  set +e
  
  exit 1
}

# revert-file {filename}
# revert "filename" from "filename.bak" if "filename.tmp" exists.
function revert-file {
  local filename=$1
  local temp="${filename}.tmp"
  local backup="${filename}.bak"
  
  if [ -f "${temp}" ]; then
    echo -n "reverting ${filename} ... "
    mv ${backup} ${filename}
    rm ${temp}
    echo "DONE"
  fi
}

# revert-confs [error message]
function revert-confs {
  echo -n "ERROR occurred ... "
  
  revert-file `basename ${ZK_CONF}`
  revert-file `basename ${HC_CONF}`

  echo "EXIT"
  
  if [ ! -z "$1" ]; then
    echo $1
  fi
}

function create-zk-conf {
  echo -n "Creating ${ZK_CONF} ... "
  
  # creation of zookeeper config
  
  local temp_zk="${ZK_CONF}.tmp"
  if [ -f ${temp_zk} ]; then
    rm ${temp_zk}
  fi
  touch ${temp_zk}

  if [ -f ${ZK_CONF} ]; then
    mv ${ZK_CONF} ${ZK_CONF}.bak
    local filename=`basename ${ZK_CONF}`
    echo -n "backup old file to ${filename}.bak ... "
  fi
  
  hostarr=`echo ${ZK_HOSTS} | tr "," " "`
  
  local i=1
  local myid=
  for host in ${hostarr}; do
    if [ "${host}" = "${ONOS_HOST_NAME}" -o "${host}" = "${ONOS_HOST_IP}" ]; then
      myid=$i
      break
    fi
    i=`expr $i + 1`
  done
  
  if [ -z "${myid}" ]; then
    local filename=`basename ${ONOS_CONF}`
    revert-confs "[ERROR in ${filename}] zookeeper.hosts must have hostname \"${ONOS_HOST_NAME}\" or IP address"
  fi
  
  if [ -f "${ZK_MY_ID}" ]; then
    local SUDO=${SUDO:-}
    {
      ${SUDO} mv -f ${ZK_MY_ID} ${ZK_MY_ID}.old
    } || {
      echo "FAILED"
      echo "[ERROR] Failed to rename ${ZK_MY_ID}."
      echo "[ERROR] Please retry after setting \"export SUDO=sudo\""
      exit 1
    }
  fi
  
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

function create-hazelcast-conf {
  echo -n "Creating ${HC_CONF} ... "
  
  local temp_hc="${HC_CONF}.tmp"
  if [ -f ${temp_hc} ]; then
    rm ${temp_hc}
  fi
  touch ${temp_hc}
  
  if [ -f ${HC_CONF} ]; then
    mv ${HC_CONF} ${HC_CONF}.bak
    local filename=`basename ${HC_CONF}`
    echo -n "backup old file to ${filename}.bak ... "
  fi
  
  # To keep indent of XML file, change IFS
  local IFS=''
  while read line; do
    if [[ $line =~ __HC_NETWORK__ ]]; then
      if [ ! -z "${HC_TCPIP_MEMBERS}" ]; then
        # temporary change
        IFS=' '
        local memberarr=`echo ${HC_TCPIP_MEMBERS} | tr "," " "`
        echo '<multicast enabled="false" />'
        echo '<tcp-ip enabled="true">'
        for member in ${memberarr}; do
          echo "  <member>${member}</member>"
        done
        echo '</tcp-ip>'
        IFS=''
      else
        echo '<multicast enabled="true">'
        echo "  <multicast-group>${HC_MULTICAST_GROUP}</multicast-group>"
        echo "  <multicast-port>${HC_MULTICAST_PORT}</multicast-port>"
        echo '</multicast>'
        echo '<tcp-ip enabled="false" />'
      fi
    else
      echo "${line}"
    fi
  done < ${HC_CONF_TEMPLATE} > ${temp_hc}
  
  mv ${temp_hc} ${HC_CONF}
  
  echo "DONE"
}

function create-logback-conf {
  echo -n "Creating ${ONOS_LOGBACK} ... "
  
  # creation of logback config
  if [ -f $ONOS_LOGBACK ]; then
    local logback_file=`basename ${ONOS_LOGBACK}`
    mv ${ONOS_LOGBACK} ${ONOS_LOGBACK}.bak
    local filename=`basename ${ONOS_LOGBACK}`
    echo -n "backup old file to ${filename}.bak ... "
  fi
  sed -e "s|__FILENAME__|${ONOS_LOG}|" ${ONOS_LOGBACK_TEMPLATE} > ${ONOS_LOGBACK}
  
  echo "DONE"
}

# create-conf-interactive {filename} {function to create conf}
function create-conf-interactive {
  local filepath=$1
  local filename=`basename ${filepath}`
  local func=$2
  
  if [ -f ${filepath} ]; then
    # confirmation to overwrite existing config file
    echo -n "Overwriting ${filename} [Y/n]? "
    while [ 1 ]; do
      read key
      if [ -z "${key}" -o "${key}" == "Y" -o "${key}" == "y" ]; then
        ${func}
        break
      elif [ "${key}" == "N" -o "${key}" == "n" ]; then
        break
      fi
      echo "[Y/n]?"
    done
  else
    ${func}
  fi
}

function create-confs {
  local key
  local filename
  
  trap handle-error ERR

  if [ "$1" == "-f" ]; then
    create-zk-conf
    create-hazelcast-conf
    create-logback-conf
  else
    create-conf-interactive ${ZK_CONF} create-zk-conf
    create-conf-interactive ${HC_CONF} create-hazelcast-conf
    create-conf-interactive ${ONOS_LOGBACK} create-logback-conf
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
  
  export ZOO_LOG_DIR=${ZK_LOG_DIR}
  if [ -f "${ZK_CONF}" ]; then
    # Run Zookeeper with our configuration
    export ZOOCFG=${ZK_CONF_FILE}
    export ZOOCFGDIR=${ONOS_CONF_DIR}
  fi
  
  ${ZK_HOME}/bin/zkServer.sh start
}

function stop-zk {
  kill-processes "ZooKeeper" `jps -l | grep org.apache.zookeeper.server | awk '{print $1}'`
}

function status-zk {
  if [ -f ${ZK_CONF} ]; then
    export ZOOCFG=${ZK_CONF_FILE}
    export ZOOCFGDIR=${ONOS_CONF_DIR}
  fi
  
  ${ZK_HOME}/bin/zkServer.sh status
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
  echo "${RC_COORD_PROTOCOL}:host=${RC_COORD_IP},port=${RC_COORD_PORT}"
}

function rc-server-addr {
  echo "${RC_SERVER_PROTOCOL}:host=${RC_SERVER_IP},port=${RC_SERVER_PORT}"
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
  ${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH}/coordinator -L ${coord_addr} > $RAMCLOUD_COORD_LOG 2>&1 &
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
  ${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH}/server -M -L ${server_addr} -C ${coord_addr} --masterServiceThreads 1 --logCleanerThreads 1 --detectFailures 0 > $RAMCLOUD_SERVER_LOG 2>&1 &
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

  JVM_OPTS="${JVM_OPTS} -Dnet.onrc.onos.core.datastore.backend=${ONOS_HOST_BACKEND}"
  
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
    create-confs $2
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

