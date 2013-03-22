#! /bin/bash
. $HOME/cluster-mgmt/func.sh

basename="onosdevb"
nr_nodes=4

onos status
cassandra status
zk status
