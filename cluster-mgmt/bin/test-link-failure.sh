#! /bin/sh
basename=$ONOS_CLUSTER_BASENAME
wait=10

fdef="flowdef_8node_42.txt"

function log()
{
    date > error.$1.$2.log
    check_status.py >> error.$1.$2.log
    dsh -w ${basename}1 "cd ONOS/web; ./get_flow.py all" >> error.$1.$2.log
    dsh "cd ONOS/scripts; ./showflow.sh"             >> error.$1.$2.log
}

echo "all links up"
dsh -w ${basename}1 "cd ONOS/scripts; ./all-linkup.sh"
echo "clean up flow"
dsh -w ${basename}1 "cd ONOS/web; ./delete_flow.py 1 100"
dsh -w ${basename}1 "cd ONOS/web; ./clear_flow.py 1 100"
sleep 1
dsh -w ${basename}1 "cd ONOS/web; ./get_flow.py all"
dsh "cd ONOS/scripts; ./delflow.sh"
echo "checkup status"
check_status.py
read -p "hit anykey> "

echo "install pre-set flows"
dsh -w ${basename}1 "cd ONOS/web; ./add_flow.py -m onos -f $fdef"
sleep 6
echo "check"
dsh -w ${basename}1 "cd ONOS/web; ./pingall.py $fdef"

#ports=`dsh -w ${basename}1 "cd ONOS/scripts; ./listports.sh" | awk '{print $2}' |grep -v tap`
operation=("sw3-eth3 down" "sw4-eth4 down" "sw4-eth3 down" "sw3-eth3 up" "sw1-eth2 down" "sw4-eth4 up" "sw4-eth3 up" "sw1-eth2 up")

((n=0))
while [ 1 ] ; do
  for (( i = 0; i< ${#operation[@]}; i ++)); do
    echo "Test $n-$i"
    p=`echo ${operation[$i]}`
    echo "operation: $p"
#  read -p "hit anykey> "
    dsh -w ${basename}1 "sudo ifconfig $p"
    echo "wait $wait sec"
    sleep $wait 
    result=`dsh -w ${basename}1 "cd ONOS/web; ./pingall.py $fdef"`
    echo $result
    nr_fail=`echo $result |grep fail | wc -l`
    if [ $nr_fail -gt 0 ]; then
      log $n $i
    fi
  done
  ((n++))
done

