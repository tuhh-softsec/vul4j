#! /bin/bash
source ~/env_cluster
basename=$ONOS_CLUSTER_BASENAME
DIR=${HOME}/ONOS

function scale_onos(){
  for i in 5 6 7 8 ; do
    ssh -i ~/.ssh/onlabkey.pem  ${basename}${i} 'cd ONOS;./start-onos.sh start' &
  done

  while [ 1 ]; do
    up=`for i in 5 6 7 8 ; do
      ssh -i ~/.ssh/onlabkey.pem  ${basename}${i} 'cd ONOS;./start-onos.sh status' &
    done | grep "instance" | awk '{s+=$1}END{print s}'`

    if [ x$up == 4 ]; then
      break;
    fi 
    sleep 1
  done
}

start=`date +"%s"`
echo "bring up four nodes"
scale_onos
sleep 2
echo "Adding more flows"
$DIR/web/add_flow.py -m onos -f $DIR/web/flowdef_demo_add.txt
endt=`date +"%s"`
(( delta = endt -start ))
echo "Scale Up Done: took $delta sec"
