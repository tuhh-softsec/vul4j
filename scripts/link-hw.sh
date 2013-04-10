#! /bin/bash

#controller=`hostname`
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

if [  "x00:00:00:00:ba:5e:ba:11" == "x$1" ]; then
        if [ x$cmd == "xup" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.61 $src_port 1  
        elif [ x$cmd == "xdown" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.61 $src_port 0 
        else
		echo "no cmd"
        fi
elif [  "x00:00:00:00:00:00:ba:12" == "x$1" ]; then
        if [ x$cmd == "xup" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.62 $src_port 1  
        elif [ x$cmd == "xdown" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.62 $src_port 0 
        else
		echo "no cmd"
        fi
elif [  "x00:00:00:00:ba:5e:ba:13" == "x$1" ]; then
        if [ x$cmd == "xup" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.63 $src_port 1 
        elif [ x$cmd == "xdown" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.63 $src_port 0 
        else
		echo "no cmd"
        fi
elif [  "x00:00:20:4e:7f:51:8a:35" == "x$1" ]; then
        if [ x$cmd == "xup" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.50 $src_port 1 
        elif [ x$cmd == "xdown" ]; then
                ~/ONOS/scripts/prontolink.exp 10.128.0.50 $src_port 0 
        else
		echo "no cmd"
        fi

elif [  "x00:01:00:16:97:08:9a:46" == "x$1" ]; then
        if [ x$cmd == "xup" ]; then
                ~/ONOS/scripts/neclink.exp $src_port no
        elif [ x$cmd == "xdown" ]; then
                ~/ONOS/scripts/neclink.exp $src_port 
        else
		echo "no cmd"
        fi


fi



#for s in $switches; do
#    dpid=`sudo ovs-ofctl  show  $s |grep dpid | awk '{print $4}'`
#    if [  "x$dpid" == "x$src_dpid" ]; then
#
##       intf=`sudo ovs-ofctl show $s |grep addr | awk -v p=$src_port 'BEGIN {pat="^ "p"\("}
##	$0 ~ pat {w=match ($0, /\(.*\)/); if (w) print substr($0, RSTART+1, RLENGTH-2)}'`
#
#        sudo ovs-ofctl show $s |grep addr | sed 's/[\(\)]/,/g'>/tmp/baz.out
#	intf=`cat /tmp/baz.out | awk -v p=$src_port 'BEGIN {pat="^ "p","}
#	$0 ~ pat {w=match($0, /,.*,/); if (w) print substr($0, RSTART+1, RLENGTH-2)}'`
#
#	if [ x$intf != "x" ]; then
#	        if [ x$cmd == "xup" ]; then
#		    echo "sudo ifconfig ${intf}  up"
#		    sudo ifconfig ${intf}  up
#       		elif [ x$cmd == "xdown" ]; then
#		    echo "sudo ifconfig ${intf}  down"
#		    sudo ifconfig ${intf}  down
#	        else
#		    echo "sudo ifconfig ${intf}"
#		    sudo ifconfig ${intf} 
#		fi
#		break
#        fi
#    fi
#done
