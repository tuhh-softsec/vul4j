#! /bin/bash
if [ $# != 1 ]; then
  echo "$0 flowdef_file"
elif [ ! -f ${HOME}/ONOS/web/$1  ]; then
  echo "no such flowdef file: $1"
fi
dsh "cd ONOS/web; ./pingallm-local.py $1" > /tmp/.pingall.result.$$
cat /tmp/.pingall.result.$$ | grep "Pingall flow" | sort -n -k 4 
cat /tmp/.pingall.result.$$ | grep "Pingall Result" | awk '{s+=$5; f+=$7; i+=$9}END{printf("Pingall Result: success %d fail %d incomplete %d\n",s,f,i)}'

