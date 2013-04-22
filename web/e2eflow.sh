#! /bin/sh
./add_flow.py -m 1 host0101-host0301 00:00:00:00:00:00:01:01 1 00:00:00:00:00:00:03:01 1 matchSrcMac 00:00:00:00:01:01 matchDstMac 00:00:00:00:03:01 2>&1 > /dev/null &
./add_flow.py -m 2 host0301-host0101 00:00:00:00:00:00:03:01 1 00:00:00:00:00:00:01:01 1 matchSrcMac 00:00:00:00:03:01 matchDstMac 00:00:00:00:01:01 2>&1 > /dev/null &
#./get_flow.py all
