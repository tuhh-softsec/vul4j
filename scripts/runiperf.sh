#! /usr/bin/env python
import sys
import os

def usage():
  print "%s flowid src_dpid src_port dst_dpid dst_port duration samples" % sys.argv[0]
  sys.exit()

def main():
  flowid = sys.argv[1]
  src_dpid = sys.argv[2]
  dst_dpid = sys.argv[4]
  duration=int(sys.argv[6])
  samples=int(sys.argv[7])
  src_nwid=int(src_dpid.split(':')[-2], 16)
  dst_nwid=int(dst_dpid.split(':')[-2], 16)
  src_hostid=int(src_dpid.split(':')[-1], 16)
  dst_hostid=int(dst_dpid.split(':')[-1], 16)
  # /home/ubuntu/ONOS/web/scripts/iperf -t%s -i0.1 -yJ -o /tmp/iperf_%s.out -c 127.0.0.1 &
  cmd="ssh -o StrictHostKeyChecking=no 1.1.%d.1 '/home/ubuntu/ONOS/scripts/iperf -t %s -i0.1 -k %d -yJ -o /home/ubuntu/ONOS/web/log/iperf_%s.out -c 192.168.%d.%d 2>&1 &' &" % (src_hostid, duration, samples, flowid, dst_nwid, dst_hostid)
  killcmd='pkill -KILL -f \"iperf .* -o .*/iperf_%s.out\"' % (flowid)
  print killcmd
  print cmd
  os.popen(killcmd)
  os.popen(cmd)

if __name__ == "__main__":
  if len(sys.argv) != 8:
    print len(sys.argv)
    usage()

  main()
