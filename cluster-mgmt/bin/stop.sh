#! /bin/bash
. $HOME/bin/func.sh

onos stop
cassandra cleandb
cassandra stop
zk stop
