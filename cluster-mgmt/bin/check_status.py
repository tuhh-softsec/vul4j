#! /usr/bin/env python
import json
import os

urls="http://localhost:8080/wm/core/topology/switches/all/json http://localhost:8080/wm/core/topology/links/json http://localhost:8080/wm/registry/controllers/json http://localhost:8080/wm/registry/switches/json"
RestIP=os.environ.get("ONOS_CLUSTER_BASENAME")+"1"
RestPort="8080"

core_switches=["00:00:00:00:ba:5e:ba:11", "00:00:00:00:00:00:ba:12", "00:00:20:4e:7f:51:8a:35", "00:00:00:00:ba:5e:ba:13", "00:00:00:08:a2:08:f9:01", "00:00:00:16:97:08:9a:46"]
correct_nr_switch=[6,50,25,25,25,25,25,25]
correct_intra_link=[16, 98, 48, 48, 48, 48, 48, 48]

#nr_links=(switch[1]+switch[2]+switch[3]+switch[4]+switch[5]+switch[6]+switch[7]+len(switch)-1+8)*2
nr_links= (49 + 24 * 6 + 7 + 8) * 2

cluster_basename=os.environ.get("ONOS_CLUSTER_BASENAME")
nr_nodes=os.environ.get("ONOS_CLUSTER_NR_NODES")

def get_json(url):
  print url
  try:
    command = "curl -s %s" % (url)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    print "REST IF %s has issue" % command
    parsedResult = ""

  if type(parsedResult) == 'dict' and parsedResult.has_key('code'):
    print "REST %s returned code %s" % (command, parsedResult['code'])
    parsedResult = ""

  return parsedResult 

def check_switch():
  buf = ""
  retcode = 0

  url="http://%s:%s/wm/core/topology/switches/all/json" % (RestIP, RestPort)
  parsedResult = get_json(url)

  if parsedResult == "":
    retcode = 1
    return (retcode, "Rest API has an issue")

  url = "http://%s:%s/wm/registry/switches/json" % (RestIP, RestPort)
  registry = get_json(url)

  if registry == "":
    retcode = 1
    return (retcode, "Rest API has an issue")


  buf += "switch: total %d switches\n" % len(parsedResult)
  cnt = []
  active = []
  for r in range(8):
    cnt.append(0)
    active.append(0)

  for s in parsedResult:
    if s['dpid'] in core_switches:
      nw_index = 0
    else:
      nw_index =int(s['dpid'].split(':')[-2], 16) - 1
    cnt[nw_index] += 1

    if s['state']  == "ACTIVE":
      active[nw_index] += 1

    if not s['dpid'] in registry:
      buf += "switch:  dpid %s lost controller\n" % (s['dpid'])

  for r in range(8):
    buf += "switch: network %d : %d switches %d active\n" % (r+1, cnt[r], active[r])
    if correct_nr_switch[r] != cnt[r]:
      buf += "switch fail: network %d should have %d switches but has %d\n" % (r+1, correct_nr_switch[r], cnt[r])
      retcode = 1

    if correct_nr_switch[r] != active[r]:
      buf += "switch fail: network %d should have %d active switches but has %d\n" % (r+1, correct_nr_switch[r], active[r])
      retcode = 1

  return (retcode, buf)

def check_link():
  buf = ""
  retcode = 0

  url = "http://%s:%s/wm/core/topology/links/json" % (RestIP, RestPort)
  parsedResult = get_json(url)

  if parsedResult == "":
    retcode = 1
    return (retcode, "Rest API has an issue")

  buf += "link: total %d links (correct : %d)\n" % (len(parsedResult), nr_links)
  intra = []
  interlink=0
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
    else:
      interlink += 1

  for r in range(8):
    if intra[r] != correct_intra_link[r]:
      buf += "link fail: network %d should have %d intra links but has %d\n" % (r+1, correct_intra_link[r], intra[r])
      retcode = 1

  if interlink != 14:
      buf += "link fail: There should be %d intra links (uni-directional) but %d\n" % (14, interlink)
      retcode = 1

  return (retcode, buf)

def check_switch_local():
  buf = "check_switch_local\n"
  retcode = 0

  url = "http://%s:%s/wm/registry/switches/json" % (RestIP, RestPort)
  parsedResult = get_json(url)

  if parsedResult == "":
    retcode = 1
    return (retcode, "Rest API has an issue")

  for s in parsedResult:
    #print s,len(s),s[0]['controllerId']
    ctrl=parsedResult[s][0]['controllerId']
    if s in core_switches:
      nw = 1
    else:
      nw =int(s.split(':')[-2], 16)

    if len(parsedResult[s]) > 1:
      buf += "switch_local warn: switch %s has more than 1 controller: " % (s)
      for i in parsedResult[s]:
        buf += "%s " % (i['controllerId'])
      buf += "\n"
      retcode = 1

    if int(ctrl[-1]) != nw:
      buf += "switch_local fail: switch %s is wrongly controlled by %s\n" % (s, ctrl)
      retcode = 1
      
  return (retcode, buf)

def check_switch_all(nr_ctrl):
  buf = "check_switch_all\n"
  retcode = 0

  url = "http://%s:%s/wm/registry/controllers/json" % (RestIP, RestPort)
  parsedResult = get_json(url)

  if parsedResult == "":
    retcode = 1
    return (retcode, "Rest API has an issue")

  ## Check Dup Controller ##
  controllers=list(set(parsedResult))
  if len (controllers) != len(parsedResult):
    buf += "Duplicated Controller in registory: " + str(parsedResult) + "\n"
    retcode = 1

  ## Check Missing Controller ##
  if len (controllers) != nr_ctrl:
    buf += "Missiing Controller in registory: " + str(parsedResult) + "\n"
    retcode = 1

  ## Check Core Controller Exist ##
  core_ctrl="%s1" % (cluster_basename)
  if not core_ctrl in controllers:
    buf += "Core controller missing in registory: " + str(parsedResult) + "\n"
    retcode = 1

  controllers.remove(core_ctrl)

  url = "http://%s:%s/wm/registry/switches/json" % (RestIP, RestPort)
  parsedResult = get_json(url)

  if parsedResult == "":
    retcode = 1
    return (retcode, "Rest API has an issue")

  for s in parsedResult:
    ctrl_set = []
    for c in parsedResult[s]:
      ctrl_set.append(c['controllerId'])

    if s in core_switches:
      nw = 1
    else:
      nw =int(s.split(':')[-2], 16)

    if nw == 1 and len(ctrl_set) != 1:
      buf += "Core switch %s has more than 1 controller: %s\n" % (s, ctrl_set)
    elif nw != 1:
      if len(list(set(ctrl_set))) != len(ctrl_set):
        buf += "Edge switch %s has dup controller: %s\n" % (s, ctrl_set)
      elif len(list(set(ctrl_set))) != len(controllers):
        buf += "Edge switch %s has missing controller: %s\n" % (s, ctrl_set)

  return (retcode, buf)

def check_controllers(n):
  retcode = 0
  buf = ""
  url = "http://%s:%s/wm/registry/controllers/json" % (RestIP, RestPort)
  parsedResult = get_json(url)

  if parsedResult == "":
    retcode = 1

    return (retcode, "Rest API has an issue")

  for i,c in enumerate(parsedResult):
    buf += "%d : %s\n" % (i,c)

  if len(parsedResult) != n:
    buf += "controller fail: there are %d controllers (should be %d)\n" % (len(parsedResult), n)
    retcode = 1

  return (retcode, buf)

if __name__ == "__main__":
  print "%s" % check_switch()[1]
  print "%s" % check_link()[1]
  print "%s" % check_switch_local()[1]
  print "%s" % check_controllers(8)[1]
