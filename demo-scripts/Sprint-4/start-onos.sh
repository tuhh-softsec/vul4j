#! /bin/sh
if [ $# != 2 ]; then
  echo "$0 server cmd"
fi
CLUSTER=/home/masayosi/bin/hosts-3x3.txt
dsh -w $1 "cd ONOS; sudo ./start-onos.sh $2" 
