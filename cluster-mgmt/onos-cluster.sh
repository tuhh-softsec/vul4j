#! /bin/bash

set -e

### Env vars used by this script. (default value) ###
# $ONOS_HOME         : path of root directory of ONOS repository (~/ONOS)
# $ONOS_CLUSTER_HOME : path of ONOS cluster tools directory (this script's dir)
# $REMOTE_ONOS_HOME  : path of root directory of ONOS repository in remote hosts (ONOS)
# $SSH               : command name to access host
# $PSSH              : command name to access hosts in parallel
# $SCP               : command name to copy config file to each host
#####################################################


### Variables read from ONOS config file ###
ONOS_HOME=${ONOS_HOME:-${HOME}/ONOS}

source ${ONOS_HOME}/scripts/common/utils.sh

CLUSTER_HOME=${ONOS_CLUSTER_HOME:-$(cd `dirname $0`; pwd)}
CLUSTER_CONF_DIR=${CLUSTER_HOME}/conf
CLUSTER_CONF=${ONOS_CLUSTER_CONF:-${CLUSTER_CONF_DIR}/onos-cluster.conf}
CLUSTER_TEMPLATE_DIR=${CLUSTER_CONF_DIR}/template

REMOTE_ONOS_HOME=${REMOTE_ONOS_HOME:-ONOS}
REMOTE_ONOS_CONF_DIR=${REMOTE_ONOS_HOME}/conf

if [ ! -f ${CLUSTER_CONF} ]; then
  echo "${CLUSTER_CONF} not found."
  exit 1
fi
CLUSTER_HOSTS=$(read-conf ${CLUSTER_CONF}       cluster.hosts.names             `hostname` | tr ',' ' ')
CLUSTER_BACKEND=$(read-conf ${CLUSTER_CONF}     cluster.hosts.backend)
CLUSTER_RC_PROTOCOL=$(read-conf ${CLUSTER_CONF} cluster.hosts.ramcloud.protocol "fast+udp")
CLUSTER_HC_NETWORK=$(read-conf ${CLUSTER_CONF}  cluster.hosts.hazelcast.network)
CLUSTER_HC_ADDR=$(read-conf ${CLUSTER_CONF}     cluster.hosts.hazelcast.multicast.address "224.2.2.3")
CLUSTER_HC_PORT=$(read-conf ${CLUSTER_CONF}     cluster.hosts.hazelcast.multicast.port    "54327")
############################################


ONOS_CONF_TEMPLATE=${CLUSTER_TEMPLATE_DIR}/onos_node.conf.template


### Parallel SSH settings ###
SSH=${SSH:-ssh}
PSSH=${PSSH:-parallel-ssh}
PSSH_CONF=${CLUSTER_CONF_DIR}/pssh.hosts
SCP=${SCP:-scp}
#############################


############# Common functions #############
function print_usage {
  local filename=`basename ${ONOS_CLUSTER_CONF}`
  local usage="Usage: setup/deploy/start/stop/status ONOS cluster.
 \$ $0 setup [-f]
    Set up ONOS cluster using ${filename}.
    If -f option is used, all existing files will be overwritten without confirmation.
 \$ $0 deploy [-f]
    Deliver node config files to cluster nodes.
    If -f option is used, all existing files will be overwritten without confirmation.
 \$ $0 start
    Start ONOS cluster
 \$ $0 stop
    Stop ONOS cluster
 \$ $0 status
    Show status of ONOS-cluster"
  
  echo "${usage}"	
}

############################################


############# Setup functions ##############

function list-zk-hosts {
  local list=()
  for host in ${CLUSTER_HOSTS}; do 
    local zk_host_string=$(read-conf ${CLUSTER_CONF} "cluster.${host}.zk.host")
    
    if [ -z ${zk_host_string} ]; then
      # falling back to ip
      zk_host_string=$(read-conf ${CLUSTER_CONF} "cluster.${host}.ip")
    fi
    if [ -z ${zk_host_string} ]; then
      # falling back to hostname
      zk_host_string=${host}
    fi
    
    list=("${list[@]}" ${zk_host_string})
  done
  
  # join with comma
  local IFS=,
  echo "${list[*]}"
}

function list-hc-hosts {
  local list=()
  for host in ${CLUSTER_HOSTS}; do 
    local hc_host_string=$(read-conf ${CLUSTER_CONF} "cluster.${host}.hazelcast.ip")
    
    if [ -z ${hc_host_string} ]; then
      # falling back to ip
      hc_host_string=$(read-conf ${CLUSTER_CONF} "cluster.${host}.ip")
    fi
    
    if [ -z ${hc_host_string} ]; then
      # falling back to hostname
      hc_host_string=${host}
    fi
    
    list=("${list[@]}" ${hc_host_string})
  done
  
  local IFS=,
  echo "${list[*]}"
}

function create-pssh-conf {
  local tempfile=`begin-conf-creation ${PSSH_CONF}`
  
  # creation of pssh config file
  for host in ${CLUSTER_HOSTS}; do
    local user=$(read-conf ${CLUSTER_CONF} remote.${host}.ssh.user)
    if [ -z ${user} ]; then
      # falling back to common setting
      user=$(read-conf ${CLUSTER_CONF} remote.common.ssh.user)
    fi
    
    if [ -z ${user} ]; then
      echo ${host} >> ${tempfile}
    else
      echo ${user}@${host} >> ${tempfile}
    fi
  done
  
  end-conf-creation ${PSSH_CONF}
}

# create-onos-conf {hostname}
function create-onos-conf {
  local host_name=${1}
  
  if [ -z ${host_name} ]; then
    echo "FAILED"
    echo "[ERROR] invalid hostname ${host_name}"
    exit 1
  fi
  
  local onos_conf="${CLUSTER_CONF_DIR}/onos_node.${host_name}.conf"
  local tempfile=`begin-conf-creation ${onos_conf}`

  cp ${ONOS_CONF_TEMPLATE} ${tempfile}
  
  local prefix="cluster.${host}"
  
  local host_ip=$(read-conf ${CLUSTER_CONF} "${prefix}.ip")
  local host_string=${host_ip}
  if [ -z "${host_string}" ]; then
    host_string=${host_name}
  fi
  local host_role=$(read-conf ${CLUSTER_CONF} "${prefix}.role")
  local zk_hosts=`list-zk-hosts`
  local rc_ip=$(read-conf ${CLUSTER_CONF} "${prefix}.ramcloud.ip" ${host_string})
  local rc_coord_port=$(read-conf ${CLUSTER_CONF} "${prefix}.ramcloud.coordinator.port" 12246)
  local rc_server_port=$(read-conf ${CLUSTER_CONF} "${prefix}.ramcloud.server.port" 12242)
  local hc_hosts=`list-hc-hosts`
  
  # creation of ONOS node config file
  sed -i -e "s|__HOST_NAME__|${host_name}|" ${tempfile}
  if [ -z "${host_ip}" ]; then
    # comment out
    sed -i -e "s|^\(.*__HOST_IP__.*\)$|#\1|" ${tempfile}
  else
    sed -i -e "s|__HOST_IP__|${host_ip}|" ${tempfile}
  fi
  sed -i -e "s|__ONOS_ROLE__|${host_role}|" ${tempfile}
  sed -i -e "s|__BACKEND__|${CLUSTER_BACKEND}|" ${tempfile}
  sed -i -e "s|__ZK_HOSTS__|${zk_hosts}|" ${tempfile}
  sed -i -e "s|__RAMCLOUD_PROTOCOL__|${CLUSTER_RC_PROTOCOL}|" ${tempfile}
  sed -i -e "s|__RAMCLOUD_IP__|${rc_ip}|" ${tempfile}
  sed -i -e "s|__RAMCLOUD_COORD_PORT__|${rc_coord_port}|" ${tempfile}
  sed -i -e "s|__RAMCLOUD_SERVER_PORT__|${rc_server_port}|" ${tempfile}
  
  if [ ${CLUSTER_HC_NETWORK} = "tcp-ip" ]; then
    sed -i -e "s|__HAZELCAST_MEMBERS__|${hc_hosts}|" ${tempfile}
    
    # Comment out unused parameters
    sed -i -e "s|^\(.*__HAZELCAST_MULTICAST_GROUP__.*\)$|#\1|" ${tempfile}
    sed -i -e "s|^\(.*__HAZELCAST_MULTICAST_PORT__.*\)$|#\1|" ${tempfile}
  elif [ ${CLUSTER_HC_NETWORK} = "multicast" ]; then
    sed -i -e "s|__HAZELCAST_MULTICAST_GROUP__|${CLUSTER_HC_ADDR}|" ${tempfile}
    sed -i -e "s|__HAZELCAST_MULTICAST_PORT__|${CLUSTER_HC_PORT}|" ${tempfile}
    
    sed -i -e "s|^\(.*__HAZELCAST_MEMBERS__.*\)$|#\1|" ${tempfile}
  fi
 
  end-conf-creation ${onos_conf}
}

# setup -f : force overwrite existing files
function setup {
  if [ "${1}" = "-f" ]; then
    create-pssh-conf
    
    for host in ${CLUSTER_HOSTS}; do 
      create-onos-conf ${host}
    done
  else
    create-conf-interactive ${PSSH_CONF} create-pssh-conf
    
    for host in ${CLUSTER_HOSTS}; do 
      local filename="${CLUSTER_CONF_DIR}/onos_node.${host}.conf"
      create-conf-interactive ${filename} create-onos-conf ${host}
    done
  fi
}

############################################


############ Deploy functions ##############

function deploy {
  if [ ! -f ${PSSH_CONF} ]; then
    echo "[ERROR] ${PSSH_CONF} not found"
    local command=`basename ${0}`
    echo "[ERROR] Try \"${command} setup\" to create files."
    exit 1
  fi

  for host in ${CLUSTER_HOSTS}; do
    local conf=${CLUSTER_CONF_DIR}/onos_node.${host}.conf
    if [ ! -f ${conf} ]; then
      echo "[ERROR] ${conf} not found"
      local command=`basename ${0}`
      echo "[ERROR] Try \"${command} setup\" to create files."
      exit 1
    fi
      
    local user=$(read-conf ${CLUSTER_CONF} "remote.${host}.ssh.user")
    if [ -z ${user} ]; then
      # falling back to common setting
      user=$(read-conf ${CLUSTER_CONF} "remote.common.ssh.user")
    fi
      
    ${SCP} ${conf} ${user}@${host}:${REMOTE_ONOS_CONF_DIR}
    ${SSH} ${user}@${host} "cd ${REMOTE_ONOS_HOME}; ./onos.sh setup -f"
  done
 
# TODO: Replacing per-host ssh command with pssh command below.
#       Need to solve concurrency problem when ONOS directory is shared among hosts.
#  ${PSSH} -i -h ${PSSH_CONF} "cd ${REMOTE_ONOS_HOME}; ./onos.sh setup -f"
}
############################################


############# Start functions ##############

function start {
  if [ ! -f ${PSSH_CONF} ]; then
    echo "[ERROR] ${PSSH_CONF} not found"
    local command=`basename ${0}`
    echo "[ERROR] Try \"${command} setup\" to create files."
    exit 1
  fi
  
  ${PSSH} -i -h ${PSSH_CONF} "cd ${REMOTE_ONOS_HOME}; ./onos.sh start"
}

############################################


############# Stop functions $##############

function stop {
  if [ ! -f ${PSSH_CONF} ]; then
    echo "[ERROR] ${PSSH_CONF} not found"
    local command=`basename ${0}`
    echo "[ERROR] Try \"${command} setup\" to create files."
    exit 1
  fi
  
  ${PSSH} -i -h ${PSSH_CONF} "cd ${REMOTE_ONOS_HOME}; ./onos.sh stop"
}

############################################


############ Status functions ##############

function status {
  if [ ! -f ${PSSH_CONF} ]; then
    echo "[ERROR] ${PSSH_CONF} not found"
    local command=`basename ${0}`
    echo "[ERROR] Try \"${command} setup\" to create files."
    exit 1
  fi
  
  ${PSSH} -i -h ${PSSH_CONF} "cd ${REMOTE_ONOS_HOME}; ./onos.sh status"
}

############################################


################## Main ####################
case "$1" in
  setup)
    setup $2
    ;;
  deploy)
    deploy
    ;;
  start)
    start
    ;;
  stop)
    stop
    ;;
  stat*) # <- status
    status
    ;;
  *)
    print_usage
    exit 1
esac
