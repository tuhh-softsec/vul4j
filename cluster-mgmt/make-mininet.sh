#! /bin/bash
if [ x$ONOS_CLUSTER_BASENAME == "x" -o x$ONOS_CLUSTER_NR_NODES == "x" ]; then 
  echo "set environment variable ONOS_CLUSTER_BASENAME and ONOS_CLUSTER_NR_NODES"
  exit
elif [ $# != 1 ]; then
  echo "$0 hostfile"
  exit
fi

basename=$ONOS_CLUSTER_BASENAME
NR_NODES=$ONOS_CLUSTER_NR_NODES
hosts_file=$1

for n in `seq 2 $NR_NODES`; do
  if [ $n == 2 ]; then
    nrsw=50
  else
    nrsw=25
  fi
  cat template/onsdemo_edge_template.py | sed "s/__NWID__/$n/g" | sed "s/__NRSW__/${nrsw}/g" > ${basename}${n}/onsdemo.py
done
cp template/onsdemo_core.py ${basename}1/onsdemo.py

cat $hosts_file  | awk '{printf("%s=%s\n",$2,$1)}' > .tmp
for n in `seq 2 $NR_NODES`; do
  cat template/tunnel_onsdemo_edge_template.sh | awk '{if(NR==2){system("cat .tmp")}else{print $0}}' |\
  sed "s/__NWID__/$n/g" |\
  sed "s/__TUNNEL__/TUNNEL\=\(\"1 $n ${basename}1\"\)/g" > ${basename}${n}/tunnel_onsdemo.sh
  chmod 755 ${basename}${n}/tunnel_onsdemo.sh
done

cat template/tunnel_onsdemo_core_template.sh | awk '{if(NR==2){system("cat .tmp")}else{print $0}}' |\
  sed "s/__basename__/$basename/g" > ${basename}1/tunnel_onsdemo.sh 
  chmod 755 ${basename}1/tunnel_onsdemo.sh
