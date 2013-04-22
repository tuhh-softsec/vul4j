#! /bin/bash
if [ $# != 1 ]; then
  echo "$0 flowdef_file"
elif [ ! -f ${HOME}/ONOS/web/$1  ]; then
  echo "no such flowdef file: $1"
fi
logfile="/tmp/.$USER.pingall.result.$$"
echo "Raw data at $logfile"
dsh "cd ONOS/web; ./pingallm-local.py $1" > $logfile 
cat $logfile | grep "Pingall flow" | sort -n -k 4 
cat $logfile | grep "Pingall Result" | awk '{s+=$5; f+=$7; i+=$9}END{printf("Pingall Result: success %d fail %d incomplete %d\n",s,f,i)}'

