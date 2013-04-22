#! /bin/bash
USERNAME=ubuntu
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

for n in `seq 1 $NR_NODES`; do
  rm -rf ${basename}${n}
  mkdir ${basename}${n}
  echo "${basename}${n}" > ${basename}${n}/hostname
  echo $n > ${basename}${n}/myid
done

## ZK config ##
cp template/zoo.cfg common/
for n in `seq 1 $NR_NODES`; do
 echo "server.${n}=${basename}${n}:2888:3888"
done >> common/zoo.cfg

## Cassandra config ##
cat template/cassandra.yaml |\
  sed "s/__SEED__/${basename}1/g" > common/cassandra.yaml

## /etc/hosts ##
cat template/hosts $hosts_file >  common/hosts


## .ssh/known_hosts ##
ssh-keyscan -H -t rsa github.com > common/known_hosts
ssh-keyscan -H -t rsa onosnat >> common/known_hosts
for n in `seq 1 $NR_NODES`; do
  ssh-keyscan -H -t rsa ${basename}${n}
done >> common/known_hosts

echo "GROUP: $basename" > bin/cluster.txt
cat $hosts_file | awk '{print $2}' >> bin/cluster.txt


## Creating shell script to login each node ##
for n in `seq 1 $NR_NODES`; do
  cat << EOF > bin/${basename}${n}
#!/bin/sh
ssh $USERNAME@${basename}${n}
EOF
  chmod 755 bin/${basename}${n}
done

echo "======================================"
echo "Do not forget to do the following"
echo "paste $hosts_file to /etc/hosts"
echo "paste cluster.txt to your CLUSTER file"
echo "======================================"
