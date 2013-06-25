CASSANDRA_DIR='${HOME}/apache-cassandra-1.1.4'
ZK_DIR='${HOME}/zookeeper-3.4.5'
ONOS_DIR='${HOME}/ONOS'

ZK_LIB='/var/lib/zookeeper'
CASSANDRA_LIB='/var/lib/cassandra'

if [ x$CLUSTER == "x" ]; then
  echo "CLUSTER is not set. Exitting."
  exit
fi
if [ x$ONOS_CLUSTER_BASENAME == "x" ]; then
  echo "ONOS_CLUSTER_BASENAME is not set. Exitting"
  exit
fi
if [ x$ONOS_CLUSTER_NR_NODES == "x" ]; then
  echo "ONOS_CLUSTER_NR_NODES is not set. Exitting"
  exit
fi

export basename=$ONOS_CLUSTER_BASENAME
export nr_nodes=$ONOS_CLUSTER_NR_NODES

checkcluster () {
  dsh -g $basename 'uname -a'
}

zk () {
  case "$1" in
    start)
      echo "Starting ZK.."
      dsh -g $basename "$ZK_DIR/bin/zkServer.sh start"
      while [ 1 ]; do
        nup=`dsh -g $basename "$ZK_DIR/bin/zkServer.sh status" | grep "Mode" | egrep "leader|follower" | wc -l`
        if [ $nup == $nr_nodes ]; then
          echo "everybody's up: $nup up of of $nr_nodes"
          echo "ZK started"
          break;
        fi
        echo "waiting for everybody's up: $nup up of of $nr_nodes"
        sleep 1
      done
      ;;
    stop)
      echo "Stopping ZK.."
      dsh -g $basename "$ZK_DIR/bin/zkServer.sh stop"
      ;;
    status)
      echo "Checking ZK Status"
      dsh -g $basename "$ZK_DIR/bin/zkServer.sh status"
      ;;
  esac
}

cassandra () {
  case "$1" in
    start)
      echo "Starting Cassandra.."
      echo "  start cassandra at the seed node"
      dsh -w ${basename}1 "cd $ONOS_DIR; ./start-cassandra.sh start"
      sleep 1
      echo "  start cassandra in rest nodes"
      dsh -g ${basename} -x ${basename}1 "cd $ONOS_DIR; ./start-cassandra.sh start"
      while [ 1 ]; do
        echo $$
        dsh -w ${basename}1 "cd $ONOS_DIR; ./start-cassandra.sh status" > .cassandra_check.$$
        cat .cassandra_check.$$
        nup=`cat .cassandra_check.$$ | grep Normal |grep Up| wc -l`
        if [ $nup == $nr_nodes ]; then
          echo "everybody's up: $nup up of of $nr_nodes"
          echo "Cassandra started"
          break;
        fi
        echo "waiting for everybody's up: $nup up of of $nr_nodes"
        sleep 1
      done
      ;;
    bootup)
      echo "Removing old Cassandra data and logs"
      dsh -g ${basename} "rm -rf /var/lib/cassandra/*"
      dsh -g ${basename} "rm -rf /var/log/cassandra/*"

      echo "Starting Cassandra nodes one by one..."
      for (( c=1; c<=$nr_nodes; c++ ))
      do
	echo "Starting node ${basename}${c}"
	dsh -g ${basename} -w ${basename}${c} "cd $ONOS_DIR; ./start-cassandra.sh start"

	#Wait until it's up
	while [ 1 ]; do
            echo $$
            dsh -w ${basename}1 "cd $ONOS_DIR; ./start-cassandra.sh status" > .cassandra_check.$$
            cat .cassandra_check.$$
            nup=`cat .cassandra_check.$$ | grep Normal |grep Up| wc -l`
            if [ $nup == $c ]; then
		echo "New node up: $nup up of of $nr_nodes"
		break;
            fi
            echo "Waiting for new node to come up: $nup up of of $nr_nodes"
            sleep 5
	done
      done
      ;;
    stop)
      echo "Stopping Cassandra.."
      dsh -g ${basename} "cd $ONOS_DIR; ./start-cassandra.sh stop"
      ;;
    cleandb)
      echo "Removing all data in db"
      dsh -w ${basename}1 "cd $ONOS_DIR; ./scripts/cleanup-cassandra.sh"
      ;;
    checkdb)
      echo "Check DB Status"
      dsh -w ${basename}1 "cd $ONOS_DIR; ./scripts/check-db-status.sh"
      ;;
    status)
      echo "Checking Cassandra Status"
      dsh -w ${basename}1 "cd $ONOS_DIR; ./start-cassandra.sh status"
      ;;
  esac
}

onos () {
  case "$1" in
    start)
      if [ x$2 == "x" -o x$2 == "xall" ]; then
        echo "Starting ONOS on all nodes"
        dsh -g ${basename} "cd $ONOS_DIR; ./start-onos.sh start"
        dsh -g ${basename} "cd $ONOS_DIR; ./start-rest.sh start"
      else
        echo "Starting ONOS on ${basename}$2"
        dsh -w ${basename}$2 "cd $ONOS_DIR; ./start-onos.sh start"
      fi
      ;;
    stop)
      if [ x$2 == "x" -o x$2 == "xall" ]; then
        echo "Stop ONOS on all nodes"
        dsh -g ${basename} "cd $ONOS_DIR; ./start-onos.sh stop"
      else
        echo "Stop ONOS on ${basename}$2"
        dsh -w ${basename}$2 "cd $ONOS_DIR; ./start-onos.sh stop"
      fi
      ;;
    status)
      echo "Checking ONOS Status"
      dsh -g ${basename} "cd $ONOS_DIR; ./start-onos.sh status"
      ;;
  esac
}
switch () {
  case "$1" in
    local)
      if [ x$2 == "x" -o x$2 == "xall" ]; then
        echo "set all switches point to local controller"
        dsh -g ${basename} "$ONOS_DIR/scripts/ctrl-local.sh"
      else
        dsh -w ${basename}$2 "$ONOS_DIR/scripts/ctrl-local.sh"
      fi
      ;;
    all)
      if [ x$2 == "x" -o x$2 == "xall" ]; then
        echo "set all non-core switches point to all non-core controllers"
        dsh -g ${basename} -x ${basename}1  "$ONOS_DIR/scripts/ctrl-add-ext.sh"
      else
        dsh -w ${basename}$2 "$ONOS_DIR/scripts/ctrl-add-ext.sh"
      fi
      ;;
    none)
      if [ x$2 == "x" -o x$2 == "xall" ]; then
        echo "all non-core switches loose controller"
        dsh -g ${basename} -x ${basename}1 "$ONOS_DIR/scripts/ctrl-none.sh"
      else
        dsh -w ${basename}$2 "$ONOS_DIR/scripts/ctrl-none.sh"
      fi
      ;;
  esac
}
