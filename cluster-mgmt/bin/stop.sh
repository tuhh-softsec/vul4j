#! /bin/bash
. $HOME/cluster-mgmt/func.sh

basename="onosdevc"
nr_nodes=4

onos stop
cassandra cleandb
cassandra stop
zk stop
