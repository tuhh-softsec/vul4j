#! /bin/bash
export CLUSTER=/home/masayosi/bin/hosts-3x3.txt
function cmd {
while [ 1 ] ; do
   $@
   echo "Continue? [y/n]:"
   read -t 2 value 
   if [ x$value == "xy" ]; then 
     break;
   fi
   sleep 1
done
}

./start-onos.sh 'onos9vpc,onos10vpc,onos11vpc,onos12vpc' stop
./start-onos.sh 'onos9vpc,onos10vpc,onos11vpc,onos12vpc' status
#./start-cassandra.sh 'onos9vpc' start
#sleep 2
#./start-cassandra.sh 'onos10vpc,onos11vpc,onos12vpc' start

dsh -w onos9vpc 'cd ONOS;./clean-cassandra.sh'
cmd ./start-cassandra.sh onos9vpc status
sleep 5
echo "Set mininet local"
dsh -g onos 'ONOS/ctrl-none.sh'
echo "Start ONOS on 9, 10 and 12"
./start-onos.sh 'onos9vpc' start
sleep 1
./start-onos.sh 'onos10vpc' start
sleep 1
./start-onos.sh 'onos12vpc' start
while [ 1 ] ; do
   ./start-onos.sh 'onos9vpc,onos10vpc,onos12vpc' status
   echo "Continue? [y/s/n]:"
   read -t 2 value 
   if [ x$value == "xy" ]; then 
     break;
   elif [ x$value == "xs" ]; then 
     ./start-onos.sh 'onos9vpc,onos10vpc,onos12vpc' start
   fi
   sleep 1
done
sleep 2
dsh -g onos 'ONOS/ctrl-local.sh'
#sleep 2
#./start-onos.sh 'onos9vpc,onos10vpc,onos12vpc' start
while [ 1 ] ; do
   ./start-onos.sh 'onos9vpc,onos10vpc,onos12vpc' status
   echo "Continue? [y/s/n]:"
   read -t 2 value 
   if [ x$value == "xy" ]; then 
     break;
   elif [ x$value == "xs" ]; then 
     ./start-onos.sh 'onos9vpc,onos10vpc,onos12vpc' start
   fi
   sleep 1
done
#cmd dsh -w onos9vpc,onos10vpc 'ONOS/ctrl-add-ext.sh'
#while [ 1 ] ; do
#   ./start-onos.sh 'onos9vpc,onos10vpc,onos12vpc' status
#   echo "Continue? [y/n]:"
#   read -t 2 value 
#   if [ x$value == "xy" ]; then 
#     break;
#   fi
#   sleep 1
#done
#dsh -w onos9vpc,onos10vpc 'ONOS/ctrl-add-ext.sh'
#sleep 1
