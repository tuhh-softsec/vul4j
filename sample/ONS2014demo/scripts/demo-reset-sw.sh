#! /bin/bash
source ~/env_cluster
basename=$ONOS_CLUSTER_BASENAME
DIR=${HOME}/ONOS
tstart=`date +"%s"`
echo "All Link Up"
$DIR/scripts/all-linkup.sh
echo "Delete Flows"
$DIR/web/delete_flow.py 1 300
$DIR/web/clear_flow.py 1 300
echo "Adding Flows"
$DIR/web/add_flow.py -m onos -f $DIR/web/flowdef_demo_start.txt
ssh -i ~/.ssh/onlabkey.pem  ${basename}5 'cd ONOS;./start-onos.sh stop'
ssh -i ~/.ssh/onlabkey.pem  ${basename}6 'cd ONOS;./start-onos.sh stop'
ssh -i ~/.ssh/onlabkey.pem  ${basename}7 'cd ONOS;./start-onos.sh stop'
ssh -i ~/.ssh/onlabkey.pem  ${basename}8 'cd ONOS;./start-onos.sh stop'
for i in 1 2 3 4 ; do
    ssh -i ~/.ssh/onlabkey.pem  ${basename}$i 'cd ONOS;./start-onos.sh startifdown'
done
sleep 2
for i in 1 2 3 4 5 6 7 8; do
    ssh -i ~/.ssh/onlabkey.pem  ${basename}$i 'cd ONOS/scripts; ./ctrl-local.sh'
done
tend=`date +"%s"`
(( delta = tend - tstart ))
echo "Demo Reset Done: took $delta sec"
