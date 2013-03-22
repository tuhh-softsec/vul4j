#! /bin/bash
USERNAME=ubuntu
CASSANDRA_DIR='/home/ubuntu/apache-cassandra-1.1.4'
ZK_DIR='/home/ubuntu/zookeeper-3.4.5'
ZK_LIB='/var/lib/zookeeper'
CASSANDRA_LIB='/var/lib/cassandra'

if [ $# == 1 ]; then
  basename=$1
else
  echo "$0 basename"
  exit
fi

export CLUSTER="./cluster.txt"
dsh -g $basename 'uname -a'

#dsh -g ${basename} 'cd ONOS/test-network/mininet; ./tunnel_onsdemo.sh start'
#dsh -g ${basename} 'cd ONOS/test-network/mininet; ./tunnel_onsdemo.sh start'
dsh -g ${basename} 'cd ONOS/test-network/mininet; sudo mn -c'
dsh -g ${basename} 'cd ONOS/test-network/mininet; sudo ./onsdemo.py -n'
