#! /bin/bash
controller=""
#switches=`ifconfig -a | grep sw |grep -v eth | awk '{print $1}'`
switches=`sudo ovs-vsctl list-br`
for s in $switches; do
    echo -n "$s : "
    sudo ovs-ofctl  show  $s |grep dpid
done
