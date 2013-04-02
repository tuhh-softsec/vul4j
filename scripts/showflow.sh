#! /bin/bash
controller=""
switches=`ifconfig -a | grep sw |grep -v eth | awk '{print $1}'`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

function cdpid (){
#    dpid=echo $1 | awk '{printf("%s%s:%s%s:%s%s:%s%s:%s%s:%s%s:%s%s:%s%s",$1[0],$1[1],$1[2],$1[3],$1[4],$1[5],$1[6],$1[7],$1[8],$1[9],$1[10],$1[11],$1[12],$1[13],$1[14],$1[15])}'
    dpid=`echo $1 | awk '{printf("%s\n",$0[0])}'`
}

dpids=()
for s in $switches; do
#    echo -n "$s : "
    i=`sudo ovs-ofctl  show  $s |grep dpid | awk -F ":" '{print $4}'`
    dpids+=($i)
done
((j=0))
for s in $switches; do
    id=`cdpid ${dpids[$j]}`
    echo id
#    sudo ovs-ofctl  dump-flows  $s |grep cookie| awk -vsw=$s -vdpid=${dpids[$j]} '{printf("%s %s %s\n",sw,dpid,$0)}'
    ((j ++ ))
done
