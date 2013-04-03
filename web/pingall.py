#! /usr/bin/env python
import sys
import os

hosts=['onosgui1', 'onosgui2', 'onosgui3', 'onosgui4', 'onosgui5', 'onosgui6', 'onosgui7', 'onosgui8']
filename = sys.argv[1]

f = open(filename, 'r')
for line in f:
  if line[0] != "#":
    fid=int(line.strip().split()[0])
    src_dpid=line.strip().split()[2]
    dst_dpid=line.strip().split()[4]
    src_nwid=int(src_dpid.split(':')[-2], 16)
    dst_nwid=int(dst_dpid.split(':')[-2], 16)
    src_hostid=int(src_dpid.split(':')[-1], 16)
    dst_hostid=int(dst_dpid.split(':')[-1], 16)
#    cmd="ssh %s \'ssh -o StrictHostKeyChecking=no 1.1.%d.1 ping -c 10 -W 1 192.168.%d.%d\' > /tmp/ping.%d 2>&1 &" % (hosts[src_nwid-1], src_hostid, dst_nwid, dst_hostid,fid)
#    cmd="ssh %s \'ssh -o StrictHostKeyChecking=no 1.1.%d.1 arp 193.168.%d.%d; ping -c 10 -W 1 192.168.%d.%d\' > /tmp/ping.%d 2>&1 &" % (hosts[src_nwid-1], src_hostid, dst_nwid, dst_hostid, dst_nwid, dst_hostid,fid)
    cmd="ssh %s \'ssh -o StrictHostKeyChecking=no 1.1.%d.1 ping -c 10 -W 1 192.168.%d.%d\' > /tmp/ping.%d 2>&1 &" % (hosts[src_nwid-1], src_hostid, dst_nwid, dst_hostid,fid)
    print cmd
    result = os.popen(cmd).read()

f.close()
