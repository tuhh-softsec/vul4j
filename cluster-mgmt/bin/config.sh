#! /bin/sh

. `dirname $0`/func.sh

basename=$ONOS_CLUSTER_BASENAME
nr_nodes=$ONOS_CLUSTER_NR_NODES

for n in `seq 1 $nr_nodes`; do
  echo "Host node$n"
  echo "User ubuntu"
  echo "HostName ${basename}${n}"
done > ~/.ssh/config

cd ${HOME}/bin
for n in `seq 1 $nr_nodes`; do
  ln -s ssh_exec node${n}
done
