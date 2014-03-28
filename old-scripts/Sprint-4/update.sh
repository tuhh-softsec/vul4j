#! /bin/sh
CLUSTER=/home/masayosi/bin/hosts-3x3.txt

dsh -g onos 'cd ONOS; git pull; ant'
