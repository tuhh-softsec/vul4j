#! /bin/bash
basename=$ONOS_CLUSTER_BASENAME
DIR=${HOME}/ONOS
start=`date +"%s"`
echo "bring up two nodes"
ssh -i ~/.ssh/onlabkey.pem  ${basename}5 'ONOS/start-onos.sh start'
ssh -i ~/.ssh/onlabkey.pem  ${basename}7 'ONOS/start-onos.sh start'
sleep 2
echo "Adding more flows"
$DIR/web/add_flow.py -m onos -f $DIR/web/flowdef_demo_add.txt
endt=`date +"%s"`
(( delta = endt -start ))
echo "Scale Up Done: took $delta sec"

