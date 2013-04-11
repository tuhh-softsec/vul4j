#! /bin/bash
DIR=${HOME}/ONOS
$DIR/scripts/all-linkup-hw.sh
$DIR/web/delete_flow.py 201 300
$DIR/web/clean_flow.py 201 300
$DIR/web/add_flow.py -m onos -f $DIR/web/flowdef_demo_start.txt
onos stop 5
onos stop 7
for i in 1 2 3 4 6 8; do
  status=`onos status $i | grep instance | awk '{print $2}'`
  echo "onos $i status $status"
  if [ x$status == "x0" ]; then
    onos start $i
  fi
done
sleep 2
switch local
