#! /bin/bash
USERNAME=ubuntu
CASSANDRA_DIR='/home/ubuntu/apache-cassandra-1.1.4'
ZK_DIR='/home/ubuntu/zookeeper-3.4.5'
ZK_LIB='/var/lib/zookeeper'
CASSANDRA_LIB='/var/lib/cassandra'

if [ x$ONOS_CLUSTER_BASENAME == "x" -o x$ONOS_CLUSTER_NR_NODES == "x" ]; then
  echo "set environment variable ONOS_CLUSTER_BASENAME and ONOS_CLUSTER_NR_NODES"
  exit
fi

basename=$ONOS_CLUSTER_BASENAME
NR_NODES=$ONOS_CLUSTER_NR_NODES

dsh -g $basename 'uname -a'

dsh -g ${basename} 'cd ONOS/test-network/mininet; ./tunnel_onsdemo.sh start'
dsh -g ${basename} 'cd ONOS/test-network/mininet; ./tunnel_onsdemo.sh start'
dsh -g ${basename} 'cd ONOS/test-network/mininet; sudo mn -c'
dsh -g ${basename} 'cd ONOS/test-network/mininet; sudo ./onsdemo.py -n'
