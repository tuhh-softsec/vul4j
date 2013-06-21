#! /usr/bin/env python
import sys
import os

ONOSDIR=os.getenv("HOME") + "/ONOS"
IPERF=ONOSDIR + "/scripts/iperf"
IPERFLOGDIR=ONOSDIR + "/web/log"

# Usage: flowid src_dpid dst_dpid params
def usage():
  print "%s flowid src_dpid dst_dpid hw:svr|sw:svr|hw:client|sw:client <proto>/<duration>/<interval>/<samples>" % sys.argv[0]
  sys.exit()



def main():
  flowid = sys.argv[1]
  src_dpid = sys.argv[2]
  dst_dpid = sys.argv[3]
  (testbed,server) = sys.argv[4].upper().split(':')
  server = server[0]
  params = sys.argv[5].split('/')
  proto = params[0]
  duration = params[1]
  interval = params[2]
  samples = params[3]

  src_nwid=int(src_dpid.split(':')[-2], 16)
  dst_nwid=int(dst_dpid.split(':')[-2], 16)
  src_hostid=int(src_dpid.split(':')[-1], 16)
  dst_hostid=int(dst_dpid.split(':')[-1], 16)

  if (testbed == "SW"):
    MRUN=ONOSDIR + "/test-network/mininet/mrun"
    HOST_FMT="host%d.%d"
  else:
    MRUN=ONOSDIR + "$HOME/mininet/util/m"
    HOST_FMT="g%sh%02d"

  if (proto == "tcp"):
      mininet_host = HOST_FMT % (dst_nwid, dst_hostid)
      cmd="%s %s \'%s -t%s -i%s -k%s -yJ -o %s/iperf_%s.out -c 192.168.%d.%d 2>&1 &\' &" % (MRUN, mininet_host, IPERF, src_hostid, duration, interval, samples, IPERFLOGDIR, flowid, dst_nwid, dst_hostid)
      killcmd='sudo pkill -KILL -f \"iperf .* -o .*/iperf_%s.out\"' % (flowid)
      print killcmd
      print cmd
      os.popen(killcmd)
      os.popen(cmd)
  else:
    if (server == 'S'):
      mininet_host = HOST_FMT % (dst_nwid, dst_hostid)
      cmd="%s %s \'%s -us -i%s -k%s -yJ -o %s/iperfsvr_%s.out 2>&1 &\' &" % (MRUN, mininet_host, IPERF, interval, samples, IPERFLOGDIR, flowid)
      killcmd='sudo pkill -KILL -f \"iperf .* -o .*/iperfsvr_%s.out\"' % (flowid)
      print killcmd
      print cmd
    else:
      mininet_host = HOST_FMT % (src_nwid, src_hostid)
      cmd="%s %s \'%s -u  -t%s -i%s -k%s -yJ -o %s/iperfclient_%s.out -c 192.168.%d.%d 2>&1 &\' &" % (MRUN, mininet_host, IPERF, duration, interval, samples, IPERFLOGDIR, flowid, dst_nwid, dst_hostid)
      killcmd='sudo pkill -KILL -f \"iperf .* -o .*/iperfclient_%s.out\"' % (flowid)
      print killcmd
      print cmd

    os.popen(killcmd)
    os.popen(cmd)

if __name__ == "__main__":
  if len(sys.argv) != 6:
    print len(sys.argv)
    usage()

  main()
