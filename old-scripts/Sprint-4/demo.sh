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

dsh -w onos9vpc,onos10vpc 'ONOS/ctrl-add-ext.sh'
sleep 5

echo "start ONOS on onos11vpc"
read
./start-onos.sh onos11vpc start
echo "done"

echo "kill ONOS on onos9"
read
./start-onos.sh onos9vpc stop
echo "done"

echo "kill ONOS on onos11"
read
dsh -w onos11vpc 'ONOS/ctrl-add-ext.sh'
sleep 1
./start-onos.sh onos11vpc stop
echo "done"

echo "bring back ONOS on onos9 and onos11"
read
./start-onos.sh 'onos9vpc,onos11vpc' start
echo "done"

echo "kill ONOS on onos10"
read
./start-onos.sh onos10vpc stop
echo "done"
