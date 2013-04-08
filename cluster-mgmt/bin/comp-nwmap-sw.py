#! /usr/bin/env python
import os
import re
import json
import sys
import os

status=0

pid=os.getpid()
basename=os.getenv("ONOS_CLUSTER_BASENAME")
RestPort=8080

def dump_switch_table(filename):
  cmd="dsh \"cd ONOS/scripts; ./showflow.sh\""
  f=open(filename, 'w')
  result=os.popen(cmd).read()

  f.write(result)
  f.close()

def dump_network_map(filename):
  url="http://%s1:%d/wm/flow/getall/json" % (basename, RestPort)
  cmd="curl -s %s" % url
  f=open(filename, 'w')
  try:
    result=json.loads(os.popen(cmd).read())
  except:
    print "REST has issue"
    sys.exit(1)

  json.dump(result, f, indent=2, sort_keys=True)
  f.close()
    
def make_key(*kargs):
  key=""
  for k in kargs:
    key += str(k)+"_"
  return key[:-1]

def fdb_nmap(filename):
  f=open(filename, 'r')
  json_flow = json.load(f)
  nr_flow_entries = 0
  fdb_nmap={}
  ## XXX should be better way to ditect empty list ##
  if json_flow == "[]":
    print "nmap contained %d flow entries" % nr_flow_entries
    return fdb_nmap

  for flow in json_flow:
    fid = flow['flowId']['value']
    dl_src = flow['flowEntryMatch']['srcMac']['value'].lower()
    dl_dst = flow['flowEntryMatch']['dstMac']['value'].lower()
    e = {}
    for entry in flow['dataPath']['flowEntries']:
       dpid = entry['dpid']['value'].replace(":","").lower()
       cookie = entry['flowEntryId']
       in_port = entry['flowEntryMatch']['inPort']['value']

       outport = []
       for p in entry['flowEntryActions']:
         outport.append(p['actionOutput']['port']['value'])
       outport.sort()

       e['dpid']=dpid  
       e['cookie']=cookie
       e['in_port']=in_port
       e['dl_src']=dl_src
       e['dl_dst']=dl_dst
       e['actions']=outport
       e['fid']=fid
       key = make_key(dpid, in_port, dl_src, dl_dst, outport[0])

       fdb_nmap[key]=e
       nr_flow_entries += 1

  print "nmap contained %d flow entries" % nr_flow_entries
  return fdb_nmap

def fdb_raw(filename):
  f = open(filename, 'r')
  fdb_raw={}
  nr_flow_entries = 0
  for line in f:
    e = {}
    if line[0] == '#':
      continue
    dpid=re.search("dpid=([0-9]|[a-f])*", line.strip()).group().split("=")[1]
    cookie=re.search("cookie=0x([0-9]|[a-f])*", line.strip()).group().split("=")[1]
    in_port=re.search("in_port=[0-9]*", line.strip()).group().split("=")[1]
    dl_src=re.search("dl_src=([0-9]|[a-f]|:)*", line.strip()).group().split("=")[1]
    dl_dst=re.search("dl_dst=([0-9]|[a-f]|:)*", line.strip()).group().split("=")[1]
    outport_list=re.search("actions=(output:[0-9]*,*)*", line.strip()).group().split("=")[1].split(",")
    outport=[]
    for i in outport_list:
      outport.append(int(i.split(":")[1]))
    outport.sort()

    e['dpid']=dpid  
    e['cookie']=cookie
    e['in_port']=in_port
    e['dl_src']=dl_src
    e['dl_dst']=dl_dst
    e['actions']=outport
    key = make_key(dpid, in_port, dl_src, dl_dst, outport[0])
    fdb_raw[key]=e
    nr_flow_entries += 1

  print "real switches contained %d flow entries" % nr_flow_entries
  f.close()
  return fdb_raw

if __name__ == "__main__":
  argvs = sys.argv 
  if len(argvs) != 2:
    f1=".nmap.%d.txt" % pid
    f2=".rawflow.%d.txt" % pid
    dump_network_map(f1)
    dump_switch_table(f2)

  else:
    f1 = sys.argv[1]
    f2 = sys.argv[2]


  fdb_nmap = fdb_nmap(f1)
  fdb_raw = fdb_raw(f2)

  nr_not_found_in_switch = 0
  for f in fdb_nmap:
    if not fdb_raw.has_key(f):
      nr_not_found_in_switch += 1
      print "fid=%s dpid=%s cookie=%s in_port=%s dl_src=%s dl_dst=%s outport=%s not found in switch" % (fdb_nmap[f]['fid'],fdb_nmap[f]['dpid'],fdb_nmap[f]['cookie'],fdb_nmap[f]['in_port'],fdb_nmap[f]['dl_src'],fdb_nmap[f]['dl_dst'],fdb_nmap[f]['actions'])

  nr_not_found_in_nmap = 0
  for f in fdb_raw:
    if not fdb_nmap.has_key(f):
      nr_not_found_in_nmap += 1
      print "dpid=%s cookie=%s in_port=%s dl_src=%s dl_dst=%s outport=%s not found in nmap" % (fdb_raw[f]['dpid'],fdb_raw[f]['cookie'],fdb_raw[f]['in_port'],fdb_raw[f]['dl_src'],fdb_raw[f]['dl_dst'],fdb_raw[f]['actions'])
  
  print "Network Map has %d flow entries,  %d not found in switch" % (len(fdb_nmap), nr_not_found_in_switch)
  print "Switches have %d flow entries, %d not found in network map" % (len(fdb_raw), nr_not_found_in_nmap)
  print "dumpfiles: %s %s" % (f1, f2)
