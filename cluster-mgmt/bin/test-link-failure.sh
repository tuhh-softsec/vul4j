#! /bin/sh
basename=$ONOS_CLUSTER_BASENAME

fdef="flowdef_8node_42.txt"
echo "all links up"
dsh -w ${basename}1 "cd ONOS/scripts; ./all-linkup.sh"
echo "clean up flow"
dsh -w ${basename}1 "cd ONOS/web; ./delete_flow.py 1 100"
dsh -w ${basename}1 "cd ONOS/web; ./clear_flow.py 1 100"
sleep 1
dsh -w ${basename}1 "cd ONOS/web; ./get_flow.py all"
dsh "cd ONOS/scripts; ./delflow.sh"
echo "checkup status"
./check_status.py
read -p "hit anykey> "

echo "install pre-set flows"
dsh -w ${basename}1 "cd ONOS/web; ./add_flow.py -m onos -f $fdef"
sleep 3
echo "check"
dsh -w ${basename}1 "cd ONOS/web; ./pingall.py $fdef"

ports=`dsh -w ${basename}1 "cd ONOS/scripts; ./listports.sh" | awk '{print $2}' |grep -v tap`

for p in $ports; do
  echo "port $p down"
  read -p "hit anykey> "
  dsh -w ${basename}1 "sudo ifconfig $p down"
  echo "wait 3 sec"
  sleep 3
  dsh -w ${basename}1 "cd ONOS/web; ./pingall.py $fdef"
  echo "port $p up"
  dsh -w ${basename}1 "sudo ifconfig $p up"
  echo "wait 3 sec"
  sleep 3
  dsh -w ${basename}1 "cd ONOS/web; ./pingall.py $fdef"
done
