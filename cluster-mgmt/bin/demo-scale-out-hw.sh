#! /bin/bash
onos start 5
onos start 7
sleep 2
cd ONOS/web; ./add_flow.py -m onos -f flowdef_demo_add.txt
sleep 3
switch all
