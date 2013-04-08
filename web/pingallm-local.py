#! /usr/bin/env python
import sys
import time
import os
import re
import json

ping_cnt=3
wait1=ping_cnt
wait2=10

CONFIG_FILE=os.getenv("HOME") + "/ONOS/web/config.json"

def read_config():
  global controllers, cluster_basename
  f = open(CONFIG_FILE)
  conf = json.load(f)
  controllers = conf['controllers']
  cluster_basename = conf['cluster_basename']
  f.close()

def do_pingall(nwid):
  pid=os.getpid()
  os.popen("rm -f /tmp/ping.*")

  filename = sys.argv[1]
  f = open(filename, 'r')
  nr_ping = 0
  fids=[]
  for line in f:
    if line[0] != "#":
      fid=int(line.strip().split()[0])
      logfile="/tmp/ping.%d" % (fid)
      src_dpid=line.strip().split()[2]
      dst_dpid=line.strip().split()[4]
      src_nwid=int(src_dpid.split(':')[-2], 16)
      dst_nwid=int(dst_dpid.split(':')[-2], 16)
      src_hostid=int(src_dpid.split(':')[-1], 16)
      dst_hostid=int(dst_dpid.split(':')[-1], 16)

      if src_nwid == nwid:
        cmd="echo \"Pingall flow %d : 192.168.%d.%d -> 192.168.%d.%d\" > %s" % (fid, src_nwid, src_hostid, dst_nwid, dst_hostid,logfile)
        os.popen(cmd)
#        cmd="ssh %s \'${HOME}/ONOS/test-network/mininet/mrun host%d \'ping -c %d -W 1 192.168.%d.%d\'\' >> %s 2>&1 &" % (controllers[src_nwid-1], src_hostid, ping_cnt, dst_nwid, dst_hostid,logfile)
        cmd="${HOME}/ONOS/test-network/mininet/mrun host%d \'ping -c %d -W 1 192.168.%d.%d\' >> %s 2>&1 &" % (src_hostid, ping_cnt, dst_nwid, dst_hostid,logfile)
        print cmd
        result = os.popen(cmd).read()
#        time.sleep(0.2)
        fids.append(fid)
        nr_ping = nr_ping + 1

  f.close()
  return fids

def wait_ping_finish(nr_ping):
  print "all pings started.. waiting for completion (%d sec)" % (wait1)
  time.sleep(wait1)
  cmd="cat /tmp/ping.* | grep \"packet loss\" |wc -l"
  for i in range(wait2):
    nr_done = int(os.popen(cmd).read())
    if nr_done == nr_ping:
      break
    print "%d ping finished" % nr_done
    time.sleep(1)
  
  return nr_done

def report(fids, nr_done):
  cmd='cat /tmp/ping.* | grep " 0% packet loss" |wc -l'
  nr_success = int(os.popen('cat /tmp/ping.* | grep " 0% packet loss" |wc -l').read())
  nr_incomplete = len(fids) - nr_done
  nr_fail = nr_done - nr_success

  print "Pingall Result: success %d fail %d incomplete %d" % (nr_success, nr_fail, nr_incomplete)
  if nr_fail > 0 or nr_incomplete > 0:
    for i in fids:
      cmd="cat /tmp/ping.%d | head -n 1" % (i)
      flow_desc = os.popen(cmd).read().strip()

      cmd="cat /tmp/ping.%d | grep \"packet loss\"" % (i)
      result = os.popen(cmd).read().strip()

      if not re.search(" 0% packet loss", result):
        print "flow # %d %s : %s" % (i, flow_desc, result)

if __name__ == "__main__":
  read_config()
  hostname=os.popen('hostname').read().strip()
  nwid=int(hostname.replace("%s" % cluster_basename, ""))
  fids=do_pingall(nwid)
  nr_done=wait_ping_finish(len(fids))
  report(fids, nr_done)
