#! /bin/bash

# This checks the overall status of Cassandra, onos, zookeeper, and the web server 
# Checks status on all 8 onos nodes.
# Built to run only on the hw testbed from ONOS1.
# 

echo 	""
echo    "****************************"
echo 	"***** CASSANDRA STATUS *****"
echo    "****************************"
dsh -w onos1 'cd ONOS; ./start-cassandra.sh status'

echo 	""
echo    "***********************"
echo 	"***** ONOS STATUS *****"
echo    "***********************"
dsh -g onos 'cd ONOS; ./start-onos.sh status; echo "Open ports on 9160: "; netstat -nat | grep 9160 | wc -l'

echo 	""
echo    "****************************"
echo 	"***** ZOOKEEPER STATUS *****"
echo    "****************************"
dsh -g onos '~/zookeeper-3.4.5/bin/zkServer.sh status'

echo 	""
echo    "**************************"
echo 	"***** WEB GUI STATUS *****"
echo    "**************************"
~/ONOS/start-rest.sh status
