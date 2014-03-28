#! /bin/bash
controller="10.128.4.12 10.128.4.13 10.128.4.14 10.128.4.15 10.128.4.16"
switches=`ifconfig -a | grep "^s" |grep -v eth | awk '{print $1}'`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

url=""
for c in $controller; do
  url="$url tcp:$c:6633"
done
echo $url
for s in $switches; do
    echo "set switch $s controller $url"
    sudo ovs-vsctl set-controller $s $url
done
