#! /bin/bash
onos start 5
onos start 7
switch local
sleep 4 
cd ~/ONOS/web; ./add_flow.py -m onos -f flowdef_demo_add.txt &
