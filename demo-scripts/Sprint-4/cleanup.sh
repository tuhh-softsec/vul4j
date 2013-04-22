#! /bin/sh
CLUSTER=/home/masayosi/bin/hosts-3x3.txt
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

ilink_up onos9vpc tapa0
ilink_up onos10vpc tapb0
ilink_up onos10vpc tapb1
ilink_up onos11vpc tapc0

#echo "stopping mininet"
#dsh -g onos  'sudo mn -c'
echo "stopping ONOS"
dsh -g onos 'cd ONOS; ./start-onos.sh stop'
echo "stopping Cassandra"
dsh -g onos 'cd ONOS; ./start-cassandra.sh stop'
echo "Removing Cassandra DB"
dsh -g onos 'sudo rm -rf /var/lib/cassandra/*'
