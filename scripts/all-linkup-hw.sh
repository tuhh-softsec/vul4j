#!/bin/bash
#   BRINGS BACK UP ALL THE LINKS FOR THE HARDWARE DEMO AS SHOWN IN ONS 2013
#   link-hw.sh %s %s %s  % (src_dpid, port1, cmd)

./link-hw.sh 00:00:00:00:ba:5e:ba:11 24 up 
./link-hw.sh 00:00:00:00:ba:5e:ba:11 23 up 

./link-hw.sh 00:00:00:00:ba:5e:ba:13 22 up
./link-hw.sh 00:00:00:00:ba:5e:ba:13 23 up

./link-hw.sh 00:00:00:00:00:00:ba:12 23 up
./link-hw.sh 00:00:00:00:00:00:ba:12 22 up
./link-hw.sh 00:00:00:00:00:00:ba:12 24 up

./link-hw.sh 00:01:00:16:97:08:9a:46 23 up
./link-hw.sh 00:01:00:16:97:08:9a:46 24 up

./link-hw.sh 00:00:20:4e:7f:51:8a:35 21 up 
./link-hw.sh 00:00:20:4e:7f:51:8a:35 22 up 
./link-hw.sh 00:00:20:4e:7f:51:8a:35 24 up 
./link-hw.sh 00:00:20:4e:7f:51:8a:35 23 up 
