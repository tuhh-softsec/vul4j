USERNAME=ubuntu
CASSANDRA_DIR='/home/ubuntu/apache-cassandra-1.1.4'
ZK_DIR='/home/ubuntu/zookeeper-3.4.5'
ONOS_DIR='/home/ubuntu/ONOS'
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
    stop)
      echo "Stopping Cassandra.."
      dsh -g ${basename} "cd $ONOS_DIR; ./start-cassandra.sh stop"
      ;;
    cleandb)
      echo "Removing all data in db"
      dsh -w ${basename}1 "cd $ONOS_DIR; ./scripts/cleanup-cassandra.sh"
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
      echo "Starting ONOS"
      dsh -g ${basename} "cd $ONOS_DIR; ./start-onos.sh start"
      dsh -w ${basename}1 "cd $ONOS_DIR; ./start-rest.sh start"
      ;;
    stop)
      echo "Stop ONOS"
      dsh -g ${basename} "cd $ONOS_DIR; ./start-onos.sh stop"
      ;;
    status)
      echo "Checking ONOS Status"
      dsh -g ${basename} "cd $ONOS_DIR; ./start-onos.sh status"
      ;;
  esac
}
