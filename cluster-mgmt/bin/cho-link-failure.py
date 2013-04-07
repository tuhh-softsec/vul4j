#! /usr/bin/env python
import json
import sys
import os
import re
from check_status import *
import time

flowdef="flowdef_8node_252.txt"
basename="onosdevz"
operation=["sw3-eth4 down","sw4-eth4 down","sw4-eth3 down","sw3-eth4 up","sw1-eth2 down","sw4-eth4 up","sw4-eth3 up","sw1-eth2 up"]

def check_by_pingall():
  buf = ""
  cmd = "dsh -w %s1 \"cd ONOS/web; ./pingallm.py %s\"" % (basename, flowdef)
  result = os.popen(cmd).read()
  buf += result
  
  if re.search("fail 0", result):
    return (0, buf)
  else:
    return (1, buf)

def link_change_core(op):
  cmd = "dsh -w %s1 \"sudo ifconfig %s\"" % (basename, op)
  os.popen(cmd)
  print cmd

def check_flow_nmap():
  buf = "" 
  buf += os.popen("date").read()
  print "dump all flows from network map"
  cmd  = "dsh -w %s1 \"cd ONOS/web; ./get_flow.py all\"" % cluster_basename
  buf += os.popen(cmd).read()
  return (0, buf)

def check_flow_raw():
  buf = "" 
  print "dump all flows from switches"
  cmd  = "dsh \"cd ONOS/scripts; ./showflow.sh\""
  buf += os.popen(cmd).read()
  return (0, buf)

def dump_json(url, filename):
  f = open(filename, 'w')
  buf = "" 
  command = "curl -s %s" % (url)
  result = os.popen(command).read()
  buf += json.dumps(json.loads(result), sort_keys = True, indent = 2)
  f.write(buf)
  f.close()

def check_rest(cycle, n):
  url="http://%s:%s/wm/core/topology/switches/all/json" % (RestIP, RestPort)
  filename  = "rest-sw-log.%d.%d.log" % (cycle, n)
  dump_json(url, filename)

  filename  = "rest-link-log.%d.%d.log" % (cycle, n)
  url = "http://%s:%s/wm/core/topology/links/json" % (RestIP, RestPort)
  dump_json(url, filename)

  url = "http://%s:%s/wm/registry/switches/json" % (RestIP, RestPort)
  filename  = "rest-reg-sw-log.%d.%d.log" % (cycle, n)
  dump_json(url, filename)

  url = "http://%s:%s/wm/registry/controllers/json" % (RestIP, RestPort)
  filename  = "rest-reg-ctrl-log.%d.%d.log" % (cycle, n)
  dump_json(url, filename)

  url = "http://%s:%s/wm/flow/getsummary/0/0/json" % (RestIP, RestPort)
  filename  = "rest-flow-log.%d.%d.log" % (cycle, n)
  dump_json(url, filename)

if __name__ == "__main__":
  print "%s" % check_switch()[1]
  print "%s" % check_link()[1]
  print "%s" % check_controllers(8)[1]
  (code, result) = check_by_pingall()
  print result
  k = raw_input('hit any key>')


  for cycle in range(1000):
    for n, op in enumerate(operation):
      print "==== Cycle %d operation %d ====" % (cycle, n)
      link_change_core(op)
      print "wait 30 sec"
      time.sleep(30)
      print "check by pingall"
      (code, result) = check_by_pingall()
      if code == 0:
        print "ping success %s" % (result)
      else:
        print "pingall failed (%d, %d). Collecting logs" % (cycle, n)
        error = "error-log.%d.%d.log" % (cycle, n)
        nmapflow  = "nmap-flow-log.%d.%d.log" % (cycle, n)
        rawflow  = "raw-flow-log.%d.%d.log" % (cycle, n)

        f = open(error, 'w')
        f.write(result)
        f.write(check_switch()[1])
        f.write(check_link()[1])
        f.write(check_switch_local()[1])
        f.write(check_controllers(8)[1])
        f.close()

        f = open(nmapflow,'w')
        f.write(check_flow_nmap()[1])
        f.close()

        f = open(rawflow,'w')
        f.write(check_flow_raw()[1])
        f.close()
        check_rest(cycle,n)

    
