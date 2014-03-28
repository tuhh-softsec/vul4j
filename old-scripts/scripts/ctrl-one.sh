#! /bin/bash

if [ "x$1" == "x" ];
then
    echo "No controller specified"
    exit 1
fi

#controller=`hostname`
controller=$1
switches=`sudo ovs-vsctl list-br`
function host2ip (){
   ip=`getent hosts $1 |  awk '{print $1}' | tail -n 1`
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
