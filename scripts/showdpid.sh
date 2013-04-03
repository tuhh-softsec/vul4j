#! /bin/bash
controller=""
#switches=`ifconfig -a | grep sw |grep -v eth | awk '{print $1}'`
switches=`sudo ovs-vsctl list-br`

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
    echo -n "$s : "
    sudo ovs-ofctl  show  $s |grep dpid
done
