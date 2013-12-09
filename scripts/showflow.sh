#! /bin/bash
controller=""
switches=`sudo ovs-vsctl list-br`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

dpids=()
for s in $switches; do
    i=`sudo ovs-ofctl  show  $s |grep dpid | awk -F ":" '{print $4}'`
    dpids+=($i)
done
((j=0))
for s in $switches; do
    sudo ovs-ofctl  dump-flows  $s |grep cookie| awk -vsw=$s -vdpid=${dpids[$j]} '{printf("%s dpid=%s %s\n",sw,dpid,$0)}'
    ((j ++ ))
done
