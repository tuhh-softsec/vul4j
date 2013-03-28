#! /bin/bash

controller=`hostname`
switches=`ifconfig -a | grep sw |grep -v eth | awk '{print $1}'`

function host2ip (){
   ip=`grep $1 /etc/hosts |grep -v "ip6"|  awk '{print $1}'`
   echo $ip
}

# link.sh 00:00:00:00:ba:5e:ba:11 1 up

if [ $# != 3 ];then
 echo "usage: $0 <dpid> <port> <up|down>"
fi

src_dpid="dpid:"`echo $1 | sed s'/://g'`
src_port=$2
cmd=$3

#sudo ovs-ofctl show sw02.01 | grep addr > link.in
#cat link.in | awk -v p=$src_port 'BEGIN {pat="^ "p"\("}
#$0 ~ pat {w=match ($0, /\(.*\)/); if (w) print substr($0, RSTART+1, RLENGTH-2)}'
#exit

for s in $switches; do
    dpid=`sudo ovs-ofctl  show  $s |grep dpid | awk '{print $4}'`
    if [  "x$dpid" == "x$src_dpid" ]; then
        intf=`sudo ovs-ofctl show $s |grep addr | awk -v p=$src_port 'BEGIN {pat="^ "p"\("}
	$0 ~ pat {w=match ($0, /\(.*\)/); if (w) print substr($0, RSTART+1, RLENGTH-2)}'`
	if [ x$intf != "x" ]; then
	        if [ x$cmd == "xup" ]; then
		    echo "sudo ifconfig ${intf}  up"
		    sudo ifconfig ${intf}  up
       		elif [ x$cmd == "xdown" ]; then
		    echo "sudo ifconfig ${intf}  down"
		    sudo ifconfig ${intf}  down
	        else
		    echo "sudo ifconfig ${intf}"
		    sudo ifconfig ${intf} 
		fi
		break
        fi
    fi
done
