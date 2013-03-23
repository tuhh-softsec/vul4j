#! /bin/bash
USERNAME=ubuntu
CASSANDRA_DIR='/home/ubuntu/apache-cassandra-1.1.4'
ZK_DIR='/home/ubuntu/zookeeper-3.4.5'
ZK_LIB='/var/lib/zookeeper'
CASSANDRA_LIB='/var/lib/cassandra'

if [ $# == 2 ]; then
  NR_NODES=$1
  basename=$2
else
  echo "$0 nr_nodes basename"
  exit
fi

if [ ! -f ./cluster.txt ]; then
  echo "Cannot find cluster.txt"
  exit
fi

export CLUSTER="./cluster.txt"
dsh -g $basename 'uname -a'

for n in `seq 1 $NR_NODES`; do
  pcp -w ${basename}${n} ${basename}${n}/onsdemo.py 'ONOS/test-network/mininet'
  pcp -w ${basename}${n} ${basename}${n}/tunnel_onsdemo.sh 'ONOS/test-network/mininet'
done
dsh -g $basename 'chmod 755 ONOS/test-network/mininet/tunnel_onsdemo.sh'
dsh -g $basename 'chmod 755 ONOS/test-network/mininet/onsdemo.py'
