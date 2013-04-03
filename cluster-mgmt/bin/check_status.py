#! /usr/bin/env python
import json
import os

urls="http://localhost:8080/wm/core/topology/switches/all/json http://localhost:8080/wm/core/topology/links/json http://localhost:8080/wm/registry/controllers/json http://localhost:8080/wm/registry/switches/json"
RestIP="onosdevz1"
RestPort="8080"

core_switches=["00:00:00:00:ba:5e:ba:11", "00:00:00:00:00:00:ba:12", "00:00:20:4e:7f:51:8a:35", "00:00:00:00:ba:5e:ba:13", "00:00:00:08:a2:08:f9:01", "00:00:00:16:97:08:9a:46"]
correct_nr_switch=[6,50,25,25,25,25,25,25]
correct_intra_link=[16, 98, 48, 48, 48, 48, 48, 48]

#nr_links=(switch[1]+switch[2]+switch[3]+switch[4]+switch[5]+switch[6]+switch[7]+len(switch)-1+8)*2
nr_links= (49 + 24 * 6 + 7 + 8) * 2

def check_switch():
  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/switches/all/json\'" % (RestIP, RestPort)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  parsedResult = json.loads(result)
  print "switch: total %d switches" % len(parsedResult)
  cnt = []
  for r in range(8):
    cnt.append(0)
  for s in parsedResult:
    nw =int(s['dpid'].split(':')[-2], 16)
    if nw >= 2 and nw <=8:
      cnt[nw-1] = cnt[nw-1] + 1
    else:
      cnt[0] = cnt[0] + 1
  for r in range(8):
    print "switch: network %d %d switches" % (r+1, cnt[r])
    if correct_nr_switch[r] != cnt[r]:
      print "switch fail: network %d should have %d switches but has %d" % (r+1, correct_nr_switch[r], cnt[r])
      break

def check_link():
  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/links/json\'" % (RestIP, RestPort)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit
  parsedResult = json.loads(result)
  print "link: total %d links (correct : %d)" % (len(parsedResult), nr_links)
  intra = []
  for r in range(8):
    intra.append(0)

  for s in parsedResult:
    if s['src-switch'] in core_switches:
      src_nw = 1
    else:
      src_nw =int(s['src-switch'].split(':')[-2], 16)
    
    if s['dst-switch'] in core_switches:
      dst_nw = 1
    else:
      dst_nw =int(s['dst-switch'].split(':')[-2], 16)

    src_swid =int(s['src-switch'].split(':')[-1], 16)
    dst_swid =int(s['dst-switch'].split(':')[-1], 16)
    if src_nw == dst_nw:
      intra[src_nw - 1] = intra[src_nw - 1] + 1 

  for r in range(8):
    if intra[r] != correct_intra_link[r]:
      print "link fail: network %d should have %d intra links but has %d" % (r+1, correct_intra_link[r], intra[r])

def check_mastership():
  try:
    command = "curl -s \'http://%s:%s/wm/registry/switches/json\'" % (RestIP, RestPort)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit
  parsedResult = json.loads(result)
  for s in parsedResult:
    #print s,len(s),s[0]['controllerId']
    ctrl=parsedResult[s][0]['controllerId']
    if s in core_switches:
      nw = 1
    else:
      nw =int(s.split(':')[-2], 16)

    if len(parsedResult[s]) > 1:
      print "ownership fail: switch %s has more than 1 ownership" % (s)
    elif int(ctrl[-1]) != nw:
      print "ownership fail: switch %s is owened by %s" % (s, ctrl)

def check_controllers():
  try:
    command = "curl -s \'http://%s:%s/wm/registry/controllers/json\'" % (RestIP, RestPort)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  parsedResult = json.loads(result)
  unique=list(set(parsedResult))
  if len(unique) != 8:
    print "controller fail: there are %d controllers" % (len(parsedResult))

if __name__ == "__main__":
  check_switch()
  check_link()
  check_mastership()
  check_controllers()
