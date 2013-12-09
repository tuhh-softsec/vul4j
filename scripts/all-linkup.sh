#! /bin/bash

controller=`hostname`
switches=`sudo ovs-vsctl list-br`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

for s in $switches; do
  ports=`sudo ovs-vsctl --pretty list-ports $s`
  for p in $ports; do
    sudo ifconfig $p up 
  done
done
