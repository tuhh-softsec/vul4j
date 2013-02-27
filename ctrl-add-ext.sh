#! /bin/bash
controller="localhost onos9vpc onos10vpc onos11vpc"
me=`hostname`
controller=`echo $controller | sed "s/$me//g"`
switches=`ifconfig -a | grep sw |grep -v eth | awk '{print $1}'`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

url=""
for c in $controller; do
  url="$url tcp:`host2ip $c`:6633"
done
echo $url
for s in $switches; do
    echo "set switch $s controller $url"
    sudo ovs-vsctl set-controller $s $url
done
