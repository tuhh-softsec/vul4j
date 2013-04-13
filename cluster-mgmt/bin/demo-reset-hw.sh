#! /bin/bash
DIR=${HOME}/ONOS
echo "==== Reset Demo to the initial State ==="
date
start=`date +"%s"`
echo "all link up.."
$DIR/scripts/all-linkup-hw.sh
echo "link up done"

echo "cleanup excess flows"
$DIR/web/delete_flow.py 201 300
$DIR/web/clear_flow.py 201 300
echo "cleanup excess flows done"
echo "Adding 200 flows"
$DIR/web/add_flow.py -m onos -f $DIR/web/flowdef_demo_start.txt
echo "done"
echo "killing iperf"
dsh -g onos 'sudo pkill -KILL iperf'
echo "done"
echo "kill onos at 5 and 7"
onos stop 5
onos stop 7
echo "done"
echo "bringup 1 2 3 4 6 8 if dead"
for i in 1 2 3 4 6 8; do
  status=`onos status $i | grep instance | awk '{print $2}'`
  echo "onos $i status $status"
  if [ x$status == "x0" ]; then
    onos start $i
  fi
done
echo "done"

sleep 2
switch local
endt=`date +"%s"`
(( delta = endt -start ))
echo "finish: took $delta sec"
