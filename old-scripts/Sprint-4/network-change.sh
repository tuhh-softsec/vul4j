#! /bin/sh

function Wait {
  echo "press ret> "
  read
}

function port_down {
  echo "Taking down $2 port $3"
  Wait
  dsh -w $1 "sudo ifconfig $2-$3 down"
  echo "done"
}
function port_up {
  echo "bring up $2 port $3"
  Wait
  dsh -w $1 "sudo ifconfig $2-$3 up"
  echo "done"
}

function port_change {
  port_down $1 $2 $3
  port_up $1 $2 $3
}

function switch_remove {
  echo "taking switch $2 out"
  Wait
  dsh -w $1 "sudo ovs-vsctl set-controller $2 tcp:127.0.0.1:6639"
  echo "done"
}

function switch_back {
  echo "taking switch $2 back"
  Wait
  ctrl="tcp:127.0.0.1:6633"
  dsh -w $1 "sudo ovs-vsctl set-controller $2 $ctrl"
  echo "done"
}
function switch_go_back {
  switch_remove $1 $2
  switch_back $1 $2
}

function link_down {
  echo "remove link from sw $2 port $3"
  n=`dsh -w $1 "sudo tc qdisc show dev $2-$3" | grep netem | wc -l`
  if [ $n -eq 1  ]; then
    echo "dsh -w $1 sudo tc qdisc change dev $2-$3 root netem loss 100%"
    dsh -w $1 "sudo tc qdisc change dev $2-$3 root netem loss 100%"
  else 
    echo "dsh -w $1 sudo tc qdisc add dev $2-$3 root netem 100%"
    dsh -w $1 "sudo tc qdisc add dev $2-$3 root netem loss 100%"
  fi
  echo "done"
}
function link_up {
  echo "add link from sw $2 port $3"
  n=`dsh -w $1 "sudo tc qdisc show dev $2-$3" | grep netem | wc -l`
  if [ $n -eq 1  ]; then
    echo "dsh -w $1 sudo tc qdisc change dev $2-$3 root netem loss 0%"
    dsh -w $1 "sudo tc qdisc change dev $2-$3 root netem loss 0%"
  else 
    echo "dsh -w $1 sudo tc qdisc add dev $2-$3 root netem loss 0%"
    dsh -w $1 "sudo tc qdisc add dev $2-$3 root netem loss 0%"
  fi
  echo "done"
}
function link_change {
  link_down $1 $2 $3
  link_up $1 $2 $3
}

function ilink_down {
  echo "remove link from $2"
  n=`dsh -w $1 "sudo tc qdisc show dev $2" | grep netem | wc -l`
  if [ $n -eq 1  ]; then
    echo "dsh -w $1 sudo tc qdisc change dev $2 root netem loss 100%"
    dsh -w $1 "sudo tc qdisc change dev $2 root netem loss 100%"
  else 
    echo "dsh -w $1 sudo tc qdisc add dev $2 root netem 100%"
    dsh -w $1 "sudo tc qdisc add dev $2 root netem loss 100%"
  fi
  echo "done"
}
function ilink_up {
  echo "add link at $2"
  n=`dsh -w $1 "sudo tc qdisc show dev $2" | grep netem | wc -l`
  if [ $n -eq 1  ]; then
    echo "dsh -w $1 sudo tc qdisc change dev $2 root netem loss 0%"
    dsh -w $1 "sudo tc qdisc change dev $2 root netem loss 0%"
  else 
    echo "dsh -w $1 sudo tc qdisc add dev $2 root netem loss 0%"
    dsh -w $1 "sudo tc qdisc add dev $2 root netem loss 0%"
  fi
  echo "done"
}
function ilink_change {
  ilink_down $1 $2
  ilink_up $1 $2
}


#port_change onos9vpc swa1 eth2
#port_change onos10vpc swb3 eth4
#port_change onos11vpc swc5 eth2

#switch_go_back onos9vpc swa1
#switch_go_back onos10vpc swb3
#switch_go_back onos11vpc swc3

#echo "link down between swa4 and swa3"
#Wait
#link_down onos9vpc swa4 eth2 
#link_down onos9vpc swa3 eth4 
#echo "link up between swa4 and swa3"
Wait
link_up onos9vpc swa3 eth4 
link_up onos9vpc swa4 eth2 

echo "link down between swb4 and swb3"
Wait
link_down onos10vpc swb4 eth2 
link_down onos10vpc swb3 eth4 
echo "link down between swb4 and swb3"
Wait
link_up onos10vpc swb3 eth4 
link_up onos10vpc swb4 eth2 

echo "link down between network 1 and network2"
Wait
ilink_down onos9vpc tapa0 
ilink_down onos10vpc tapb0 

echo "link up between network 1 and network2"
Wait
ilink_up onos10vpc tapb0 
ilink_up onos9vpc tapa0 
