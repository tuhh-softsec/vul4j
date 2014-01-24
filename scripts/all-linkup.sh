#! /bin/bash

controller=`hostname`
switches=`sudo ovs-vsctl list-br`

for s in $switches; do
  ports=`sudo ovs-vsctl --pretty list-ports $s`
  for p in $ports; do
    sudo ifconfig $p up 
  done
done
