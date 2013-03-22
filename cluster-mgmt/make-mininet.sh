#! /bin/bash
if [ $# == 3 ]; then
  NR_NODES=$1
  basename=$2
  hosts_file=$3
else
  echo "$0 nr_hodes basename hostfile"
  exit
fi

for n in `seq 2 $NR_NODES`; do
  if [ $n == 2 ]; then
    nrsw=50
  else
    nrsw=25
  fi
  cat template/onsdemo_edge_template.py | sed "s/__NWID__/$n/g" | sed "s/__NRSW__/${nrsw}/g" > ${basename}${n}/onsdemo.py
done
cp template/onsdemo_core.py ${basename}1/onsdemo.py

cat hosts  | awk '{printf("%s=%s\n",$2,$1)}' > .tmp
for n in `seq 2 $NR_NODES`; do
  cat template/tunnel_onsdemo_edge_template.sh | awk '{if(NR==2){system("cat .tmp")}else{print $0}}' |\
  sed "s/__NWID__/$n/g" |\
  sed "s/__TUNNEL__/TUNNEL\=\(\"1 $n ${basename}1\"\)/g" > ${basename}${n}/tunnel_onsdemo.sh
  chmod 755 ${basename}${n}/tunnel_onsdemo.sh
done

cat template/tunnel_onsdemo_core_template.sh | awk '{if(NR==2){system("cat .tmp")}else{print $0}}' |\
  sed "s/__basename__/$basename/g" > ${basename}1/tunnel_onsdemo.sh 
  chmod 755 ${basename}1/tunnel_onsdemo.sh
