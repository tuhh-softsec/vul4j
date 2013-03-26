#! /bin/bash
controller=`hostname`
switches=`ifconfig -a | grep sw |grep -v eth | awk '{print $1}'`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

url=""
for c in $controller; do
  url="$url tcp:`host2ip $c`:6633"
done

for s in $switches; do
    dpid=`sudo ovs-ofctl  show  $s |grep dpid | awk '{split($4,x,":"); print x[2]}'`
    if [  "x$dpid" == "x$1" ]; then
        if [ x$2 == "xup" ]; then
           sudo ovs-vsctl set-controller $s $url 
           echo "$s up"
        elif [ x$2 == "xdown" ]; then
           sudo ovs-vsctl set-controller $s
           echo "$s down"
        else
           echo -n "$s controller: "
           sudo ovs-vsctl get-controller $s
        fi
    fi
done
