#! /usr/bin/env python
import sys
import os

# Usage: flowid src_dpid dst_dpid params
def usage():
  print "%s flowid src_dpid dst_dpid svr|client <proto>/<duration>/<interval>/<samples>" % sys.argv[0]
  sys.exit()

def main():
  flowid = sys.argv[1]
  src_dpid = sys.argv[2]
  dst_dpid = sys.argv[3]
  server = sys.argv[4].upper()[0]
  params = sys.argv[5].split('/')
  proto = params[0]
  duration = params[1]
  interval = params[2]
  samples = params[3]

  src_nwid=int(src_dpid.split(':')[-2], 16)
  dst_nwid=int(dst_dpid.split(':')[-2], 16)
  src_hostid=int(src_dpid.split(':')[-1], 16)
  dst_hostid=int(dst_dpid.split(':')[-1], 16)

  if (proto == "tcp"):
    cmd="ssh -o StrictHostKeyChecking=no 1.1.%d.1 '/home/ubuntu/ONOS/scripts/iperf -t%s -i%s -k%s -yJ -o /home/ubuntu/ONOS/web/log/iperf_%s.out -c 192.168.%d.%d 2>&1 &' &" % (src_hostid, duration, interval, samples, flowid, dst_nwid, dst_hostid)
    killcmd='pkill -KILL -f \"iperf .* -o .*/iperf_%s.out\"' % (flowid)
    print killcmd
    print cmd
    os.popen(killcmd)
    os.popen(cmd)
  else:
    if (server == 'S'):
      cmd="ssh -o StrictHostKeyChecking=no 1.1.%d.1 '/home/ubuntu/ONOS/scripts/iperf -us -i%s -k%s -yJ -o /home/ubuntu/ONOS/web/log/iperfsvr_%s.out 2>&1 &' &" % (dst_hostid, interval, samples, flowid)
      killcmd='pkill -KILL -f \"iperf .* -o .*/iperfsvr_%s.out\"' % (flowid)
      print killcmd
      print cmd
    else:
      cmd="ssh -o StrictHostKeyChecking=no 1.1.%d.1 '/home/ubuntu/ONOS/scripts/iperf -u -t%s -i%s -k%s -yJ -o /home/ubuntu/ONOS/web/log/iperfclient_%s.out -c 192.168.%d.%d 2>&1 &' &" % (src_hostid, duration, interval, samples, flowid, dst_nwid, dst_hostid)
      killcmd='pkill -KILL -f \"iperf .* -o .*/iperfclient_%s.out\"' % (flowid)
      print killcmd
      print cmd
    os.popen(killcmd)
    os.popen(cmd)

if __name__ == "__main__":
  if len(sys.argv) != 6:
    print len(sys.argv)
    usage()

  main()
