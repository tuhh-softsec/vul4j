#! /usr/bin/env python
import json
import sys
import os
import re
from check_status import *
import time

flowdef="flowdef_8node_252.txt"
basename="onosdevt"
operation=["sw3-eth4 down","sw4-eth4 down","sw4-eth3 down","sw3-eth4 up","sw1-eth2 down","sw4-eth4 up","sw4-eth3 up","sw1-eth2 up"]

def check_by_pingall():
  buf = ""
  cmd = "pingall-speedup.sh %s" % (flowdef)
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

def dump_flowgetall(tag):
  url="http://%s:%s/wm/flow/getall/json" % (RestIP, RestPort)
  filename  = "rest-flow-getall-log.%s.log" % tag
  dump_json(url, filename)

def check_rest(tag):
  url="http://%s:%s/wm/flow/getall/json" % (RestIP, RestPort)
  filename  = "rest-flow-getall-log.%s.log" % tag
  dump_json(url, filename)

  url="http://%s:%s/wm/core/topology/switches/all/json" % (RestIP, RestPort)
  filename  = "rest-sw-log.%s.log" % tag
  dump_json(url, filename)

  url = "http://%s:%s/wm/core/topology/links/json" % (RestIP, RestPort)
  filename  = "rest-link-log.%s.log" % tag
  dump_json(url, filename)

  url = "http://%s:%s/wm/registry/switches/json" % (RestIP, RestPort)
  filename  = "rest-reg-sw-log.%s.log" % tag
  dump_json(url, filename)

  url = "http://%s:%s/wm/registry/controllers/json" % (RestIP, RestPort)
  filename  = "rest-reg-ctrl-log.%s.log" % tag
  dump_json(url, filename)

  url = "http://%s:%s/wm/flow/getsummary/0/0/json" % (RestIP, RestPort)
  filename  = "rest-flow-getsummary-log.%s.log" % tag
  dump_json(url, filename)


def check_and_log(tag):
  print "check by pingall"
  (code, result) = check_by_pingall()
  if code == 0:
    print "ping success %s" % (result)
  else:
    print "pingall failed"
    print "%s" % (result)
    error = "error-log.%s.log" % tag
    rawflow  = "raw-flow-log.%s.log" % tag
    
    ferror = open(error, 'w')
    ferror.write(result)

    fraw = open(rawflow,'w')
    fraw.write(check_flow_raw()[1])
    fraw.close()

    check_rest(tag)

    ferror.write(check_switch()[1])
    ferror.write(check_link()[1])
    ferror.write(check_switch_local()[1])
    ferror.write(check_controllers(8)[1])
    ferror.close()

  return code


if __name__ == "__main__":
  print "%s" % check_switch()[1]
  print "%s" % check_link()[1]
  print "%s" % check_controllers(8)[1]
  (code, result) = check_by_pingall()
  print result
  k = raw_input('hit any key>')

  for cycle in range(1000):
    for n, op in enumerate(operation):
      print "==== Cycle %d operation %d ====: %s" % (cycle, n, os.popen('date').read())
      link_change_core(op)
      print "wait 30 sec"
      time.sleep(30)
      print "check and log: %s" % os.popen('date').read()
      code = check_and_log("%d.%d.1" % (cycle,n))
      print "done: %s" % os.popen('date').read()
      if not code == 0:
        print "wait another 60 sec"
        time.sleep(60)
        print "check and log: %s" % os.popen('date').read()
        code = check_and_log("%d.%d.2" % (cycle,n))
        print "done: %s" % os.popen('date').read()
        if code == 0:
          tag = "%d.%d.2" % (cycle,n)
          dump_flowgetall(tag)
          rawflow  = "raw-flow-log.%s.log" % tag
          fraw = open(rawflow,'w')
          fraw.write(check_flow_raw()[1])
          fraw.close()
