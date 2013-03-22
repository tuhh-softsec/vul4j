#! /bin/bash
. $HOME/cluster-mgmt/func.sh

onos stop
cassandra cleandb
cassandra stop
zk stop

zk start
cassandra start
cassandra cleandb
onos start
dsh -g $basename 'cd ONOS; ./ctrl-local.sh'
