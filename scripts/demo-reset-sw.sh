#! /bin/bash
basename=$ONOS_CLUSTER_BASENAME
DIR=${HOME}/ONOS
endt=`date +"%s"`
echo "All Link Up"
$DIR/scripts/all-linkup.sh
echo "Delete Flows"
$DIR/web/delete_flow.py 201 300
$DIR/web/clear_flow.py 201 300
echo "Adding Flows"
$DIR/web/add_flow.py -m onos -f $DIR/web/flowdef_demo_start.txt
ssh -i ~/.ssh/onlabkey.pem  ${basename}5 'ONOS/start-onos.sh stop'
ssh -i ~/.ssh/onlabkey.pem  ${basename}7 'ONOS/start-onos.sh stop'
for i in 1 2 3 4 6 8; do
    ssh -i ~/.ssh/onlabkey.pem  ${basename}$i 'ONOS/start-onos.sh startifdown'
done
sleep 2
for i in 1 2 3 4 5 6 7 8; do
    ssh -i ~/.ssh/onlabkey.pem  ${basename}$i 'cd ONOS/scripts; ./ctrl-local.sh'
done
endt=`date +"%s"`
(( delta = endt -start ))
echo "Demo Reset Done: took $delta sec"
