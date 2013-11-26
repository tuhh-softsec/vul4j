#! /bin/bash
USERNAME=ubuntu
CASSANDRA_DIR='/home/ubuntu/apache-cassandra-1.1.4'
ZK_DIR='/home/ubuntu/zookeeper-3.4.5'
ZK_LIB='/var/lib/zookeeper'
CASSANDRA_LIB='/var/lib/cassandra'

SSH_COPY="authorized_keys  id_rsa  id_rsa.pub  known_hosts  onlab-gui.pem  onlabkey.pem"

if [ x$ONOS_CLUSTER_BASENAME == "x" -o x$ONOS_CLUSTER_NR_NODES == "x" ]; then
  echo "set environment variable ONOS_CLUSTER_BASENAME and ONOS_CLUSTER_NR_NODES"
  exit
fi

basename=$ONOS_CLUSTER_BASENAME
NR_NODES=$ONOS_CLUSTER_NR_NODES

dsh -g $basename 'uname -a'

echo "Stopping Services"
#dsh -g $basename 'cd ONOS; ./start-onos.sh stop'
#dsh -g $basename 'cd ONOS; ./stop-cassandra stop'
#dsh -g $basename '$ZK_DIR/bin/zkServer.sh stop'

# authorized_keys  cassandra.yaml  hosts  id_rsa  id_rsa.pub  known_hosts  onlab-gui.pem  onlabkey.pem  zoo.cfg
## SSH Setting
dsh -g $basename 'mkdir -m 700 .ssh' 
for n in $SSH_COPY; do
 pcp -g $basename  common/$n '.ssh'
 if [ $n != "id_rsa.pub" ] ; then
   dsh -g $basename "chmod 600 .ssh/$n"
 fi
done

dsh -g $basename "sudo rm -rf $CASSANDRA_LIB/commitlog/*"
dsh -g $basename "sudo rm -rf $CASSANDRA_LIB/saved_caches/*"
dsh -g $basename "sudo rm -rf $CASSANDRA_LIB/data/*"
dsh -g $basename "sudo chown -R $username:$username $CASSANDRA_LIB"

dsh -g $basename "sudo rm -rf $ZK_LIB/version-2*"
dsh -g $basename "sudo rm -rf $ZK_LIB/myid"

pcp -g $basename common/cassandra.yaml $CASSANDRA_DIR/conf
pcp -g $basename common/zoo.cfg        $ZK_DIR/conf
pcp -g $basename common/hosts          '~'

for n in `seq 1 $NR_NODES`; do
  pcp -w ${basename}${n} ${basename}${n}/hostname '~'
  pcp -w ${basename}${n} ${basename}${n}/myid $ZK_DIR/conf
done

dsh -g $basename 'sudo cp ~/hostname /etc' 
dsh -g $basename 'sudo cp ~/hosts /etc' 
dsh -g $basename "cd $ZK_LIB; sudo ln -s $ZK_DIR/conf/myid"

dsh -g $basename 'sudo hostname `cat /etc/hostname`'

#for n in `seq 2 $NR_NODES`; do
#  pcp -w ${basename}${n} ${basename}${n}/onsdemo_edge.py 'ONOS/test-network/mininet'
#  pcp -w ${basename}${n} ${basename}${n}/tunnel_onos_edge.sh 'ONOS/test-network/mininet'
#done
#pcp -w ${basename}1 ${basename}1/tunnel_onos_core.sh 'ONOS/test-network/mininet'
#pcp -w ${basename}1 ${basename}1/onsdemo_core.py 'ONOS/test-network/mininet'
