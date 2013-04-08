#! /usr/bin/env python
import json
import sys
import os
import re
from check_status import *
import time

basename=os.getenv("ONOS_CLUSTER_BASENAME")
operation=["sw3-eth4 down","sw4-eth4 down","sw4-eth3 down","sw3-eth4 up","sw1-eth2 down","sw4-eth4 up","sw4-eth3 up","sw1-eth2 up"]
wait1=30
wait2=60

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
  global cur_nr_controllers
  buf = ""
  buf += "check by pingall\n"
  (code, result) = check_by_pingall()
  if code == 0:
    buf += "ping success %s\n" % (result)
  else:
    buf += "pingall failed\n"
    buf += "%s\n" % (result)
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
    ferror.write(check_controllers(cur_nr_controllers)[1])
    ferror.close()

  return (code, buf)

def plog(string):
  global logf
  print string
  logf.write(string+"\n")

if __name__ == "__main__":
  global logf, cur_nr_controllers

  cur_nr_controllers = 8

  argvs = sys.argv 
  if len(argvs) == 5:
    log_filename = sys.argv[1]
    flowdef = sys.argv[2]
    wait1 = int(sys.argv[3])
    wait2 = int(sys.argv[4])
  else:
    print "usage: %s log_filename flowdef_filename wait1 wait2" % sys.argv[0]
    print "  wait1: wait time (sec) to check ping after change"
    print "  wait2: additional wait time (sec) if the first check failed"
    sys.exit(1)

  logf = open(log_filename, 'w', 0)    

  plog("flow def: %s" % flowdef)
  plog("wait1 : %d" % wait1)
  plog("wait2 : %d" % wait2)

  plog(check_switch()[1])
  plog(check_link()[1])
  plog(check_controllers(cur_nr_controllers)[1])

  (code, result) = check_by_pingall()

  plog(result)

  print result
  k = raw_input('hit any key>')

  for cycle in range(1000):
    for n, op in enumerate(operation):
      plog("==== Cycle %d operation %d ====: %s" % (cycle, n, os.popen('date').read()))
      link_change_core(op)
      plog(op)

      plog("wait %d sec" % wait1)
      time.sleep(wait1)
      plog("check and log: %s" % os.popen('date').read())

      tstart=int(time.time())
      (code, result) = check_and_log("%d.%d.1" % (cycle,n))
      plog(result)
      plog("done: %s" % os.popen('date').read())
      tend=int(time.time())

      tdelta=tend-tstart

      if not code == 0:
        wait = max(0, wait2 - tdelta)
        plog("took %d sec for check and log. wait another %d sec" % (tdelta, wait))
        time.sleep(wait)
        plog("check and log: %s" % os.popen('date').read())
        (code, result) = check_and_log("%d.%d.2" % (cycle,n))
        plog(result)
        plog("done: %s" % os.popen('date').read())
        if code == 0:
          tag = "%d.%d.2" % (cycle,n)
          dump_flowgetall(tag)
          rawflow  = "raw-flow-log.%s.log" % tag
          fraw = open(rawflow,'w')
          fraw.write(check_flow_raw()[1])
          fraw.close()
  logf.close()
