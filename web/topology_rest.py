#! /usr/bin/env python
import pprint
import os
import sys
import subprocess
import json
import argparse
import io
import time
import random

import re

from flask import Flask, json, Response, render_template, make_response, request

CONFIG_FILE=os.getenv("HOME") + "/ONOS/web/config.json"
LINK_FILE=os.getenv("HOME") + "/ONOS/web/link.json"
ONOSDIR=os.getenv("HOME") + "/ONOS"

## Global Var for ON.Lab local REST ##
RestIP="localhost"
RestPort=8080
ONOS_DEFAULT_HOST="localhost" ;# Has to set if LB=False
DEBUG=1

pp = pprint.PrettyPrinter(indent=4)
app = Flask(__name__)

def read_config():
  global LB, TESTBED, controllers, core_switches, ONOS_GUI3_HOST, ONOS_GUI3_CONTROL_HOST
  f = open(CONFIG_FILE)
  conf = json.load(f)
  LB = conf['LB']
  TESTBED = conf['TESTBED']
  controllers = conf['controllers']
  core_switches=conf['core_switches']
  ONOS_GUI3_HOST=conf['ONOS_GUI3_HOST']
  ONOS_GUI3_CONTROL_HOST=conf['ONOS_GUI3_CONTROL_HOST']
  f.close()

def read_link_def():
  global link_def
  f=open(LINK_FILE)
  try:
    link_def=json.load(f)
    f.close()
  except:
    print "Can't read link def file (link.json)"
    sys.exit(1)

def get_link_ports(src_dpid, dst_dpid):
  ret = (-1, -1)
  for link in link_def:
    if link['src-switch'] == src_dpid and link['dst-switch'] == dst_dpid:
        ret = (link['src-port'], link['dst-port'])
        break
  return ret

## Worker Functions ##
def log_error(txt):
  print '%s' % (txt)

def debug(txt):
  if DEBUG:
    print '%s' % (txt)

### File Fetch ###
@app.route('/ui/img/<filename>', methods=['GET'])
@app.route('/img/<filename>', methods=['GET'])
@app.route('/css/<filename>', methods=['GET'])
@app.route('/js/models/<filename>', methods=['GET'])
@app.route('/js/views/<filename>', methods=['GET'])
@app.route('/js/<filename>', methods=['GET'])
@app.route('/lib/<filename>', methods=['GET'])
@app.route('/log/<filename>', methods=['GET'])
@app.route('/', methods=['GET'])
@app.route('/<filename>', methods=['GET'])
@app.route('/tpl/<filename>', methods=['GET'])
@app.route('/ons-demo/<filename>', methods=['GET'])
@app.route('/ons-demo/js/<filename>', methods=['GET'])
@app.route('/ons-demo/d3/<filename>', methods=['GET'])
@app.route('/ons-demo/css/<filename>', methods=['GET'])
@app.route('/ons-demo/assets/<filename>', methods=['GET'])
@app.route('/ons-demo/data/<filename>', methods=['GET'])
def return_file(filename="index.html"):
  if request.path == "/":
    fullpath = "./index.html"
  else:
    fullpath = str(request.path)[1:]

  try:
    open(fullpath)
  except:
    response = make_response("Cannot find a file: %s" % (fullpath), 500)
    response.headers["Content-type"] = "text/html"
    return response

  response = make_response(open(fullpath).read())
  suffix = fullpath.split(".")[-1]

  if suffix == "html" or suffix == "htm":
    response.headers["Content-type"] = "text/html"
  elif suffix == "js":
    response.headers["Content-type"] = "application/javascript"
  elif suffix == "css":
    response.headers["Content-type"] = "text/css"
  elif suffix == "png":
    response.headers["Content-type"] = "image/png"
  elif suffix == "svg":
    response.headers["Content-type"] = "image/svg+xml"

  return response

## Proxy ##
@app.route("/proxy/gui/link/<cmd>/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>")
def proxy_link_change(cmd, src_dpid, src_port, dst_dpid, dst_port):
  try:
    command = "curl -s %s/gui/link/%s/%s/%s/%s/%s" % (ONOS_GUI3_CONTROL_HOST, cmd, src_dpid, src_port, dst_dpid, dst_port)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/switchctrl/<cmd>")
def proxy_switch_controller_setting(cmd):
  try:
    command = "curl -s %s/gui/switchctrl/%s" % (ONOS_GUI3_CONTROL_HOST, cmd)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/switch/<cmd>/<dpid>")
def proxy_switch_status_change(cmd, dpid):
  try:
    command = "curl -s %s/gui/switch/%s/%s" % (ONOS_GUI3_CONTROL_HOST, cmd, dpid)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/controller/<cmd>/<controller_name>")
def proxy_controller_status_change(cmd, controller_name):
  try:
    command = "curl -s %s/gui/controller/%s/%s" % (ONOS_GUI3_CONTROL_HOST, cmd, controller_name)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/addflow/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>/<srcMAC>/<dstMAC>")
def proxy_add_flow(src_dpid, src_port, dst_dpid, dst_port, srcMAC, dstMAC):
  try:
    command = "curl -s %s/gui/addflow/%s/%s/%s/%s/%s/%s" % (ONOS_GUI3_CONTROL_HOST, src_dpid, src_port, dst_dpid, dst_port, srcMAC, dstMAC)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/delflow/<flow_id>")
def proxy_del_flow(flow_id):
  try:
    command = "curl -s %s/gui/delflow/%s" % (ONOS_GUI3_CONTROL_HOST, flow_id)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/iperf/start/<flow_id>/<duration>/<samples>")
def proxy_iperf_start(flow_id,duration,samples):
  try:
    command = "curl -m 40 -s %s/gui/iperf/start/%s/%s/%s" % (ONOS_GUI3_CONTROL_HOST, flow_id, duration, samples)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/iperf/rate/<flow_id>")
def proxy_iperf_rate(flow_id):
  try:
    command = "curl -s %s/gui/iperf/rate/%s" % (ONOS_GUI3_CONTROL_HOST, flow_id)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/switchctrl/<cmd>")
def proxy_switch_controller_setting(cmd):
  try:
    command = "curl -s %s/gui/switchctrl/%s" % (ONOS_GUI3_CONTROL_HOST, cmd)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/reset")
def proxy_gui_reset():
  result = ""
  try:
    command = "curl -m 300 -s %s/gui/reset" % (ONOS_GUI3_CONTROL_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/gui/scale")
def proxy_gui_scale():
  result = ""
  try:
    command = "curl -m 300 -s %s/gui/scale" % (ONOS_GUI3_CONTROL_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

###### ONOS REST API ##############################
## Worker Func ###
def get_json(url):
  code = 200
  try:
    command = "curl -m 60 -s %s" % (url)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
    if type(parsedResult) == 'dict' and parsedResult.has_key('code'):
      print "REST %s returned code %s" % (command, parsedResult['code'])
      code=500
  except:
    print "REST IF %s has issue" % command
    result = ""
    code = 500

  return (code, result)

def pick_host():
  if LB == True:
    nr_host=len(controllers)
    r=random.randint(0, nr_host - 1)
    host=controllers[r]
  else:
    host=ONOS_DEFAULT_HOST

  return "http://" + host + ":8080"

## Switch ##
@app.route("/wm/core/topology/switches/all/json")
def switches():
  if request.args.get('proxy') == None:
    host = pick_host()
  else:
    host = ONOS_GUI3_HOST

  url ="%s/wm/core/topology/switches/all/json" % (host)
  (code, result) = get_json(url)

  resp = Response(result, status=code, mimetype='application/json')
  return resp

## Link ##
@app.route("/wm/core/topology/links/json")
def links():
  if request.args.get('proxy') == None:
    host = pick_host()
  else:
    host = ONOS_GUI3_HOST

  url ="%s/wm/core/topology/links/json" % (host)
  (code, result) = get_json(url)

  resp = Response(result, status=code, mimetype='application/json')
  return resp

## FlowSummary ##
@app.route("/wm/flow/getsummary/<start>/<range>/json")
def flows(start, range):
  if request.args.get('proxy') == None:
    host = pick_host()
  else:
    host = ONOS_GUI3_HOST

  url ="%s/wm/flow/getsummary/%s/%s/json" % (host, start, range)
  (code, result) = get_json(url)

  resp = Response(result, status=code, mimetype='application/json')
  return resp

@app.route("/wm/registry/controllers/json")
def registry_controllers():
  if request.args.get('proxy') == None:
    host = pick_host()
  else:
    host = ONOS_GUI3_HOST

  url= "%s/wm/registry/controllers/json" % (host)
  (code, result) = get_json(url)

  resp = Response(result, status=code, mimetype='application/json')
  return resp


@app.route("/wm/registry/switches/json")
def registry_switches():
  if request.args.get('proxy') == None:
    host = pick_host()
  else:
    host = ONOS_GUI3_HOST

  url="%s/wm/registry/switches/json" % (host)
  (code, result) = get_json(url)

  resp = Response(result, status=code, mimetype='application/json')
  return resp

def node_id(switch_array, dpid):
  id = -1
  for i, val in enumerate(switch_array):
    if val['name'] == dpid:
      id = i
      break

  return id

## API for ON.Lab local GUI ##
@app.route('/topology', methods=['GET'])
def topology_for_gui():
  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/switches/all/json\'" % (RestIP, RestPort)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

  topo = {}
  switches = []
  links = []
  devices = []

  for v in parsedResult:
    if v.has_key('dpid'):
#      if v.has_key('dpid') and str(v['state']) == "ACTIVE":#;if you want only ACTIVE nodes
      dpid = str(v['dpid'])
      state = str(v['state'])
      sw = {}
      sw['name']=dpid
      sw['group']= -1

      if state == "INACTIVE":
        sw['group']=0
      switches.append(sw)

  try:
    command = "curl -s \'http://%s:%s/wm/registry/switches/json\'" % (RestIP, RestPort)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)

  for key in parsedResult:
    dpid = key
    ctrl = parsedResult[dpid][0]['controllerId']
    sw_id = node_id(switches, dpid)
    if sw_id != -1:
      if switches[sw_id]['group'] != 0:
        switches[sw_id]['group'] = controllers.index(ctrl) + 1

  try:
    v1 = "00:00:00:00:00:0a:0d:00"
#    v1 = "00:00:00:00:00:0d:00:d1"
    p1=1
    v2 = "00:00:00:00:00:0b:0d:03"
#    v2 = "00:00:00:00:00:0d:00:d3"
    p2=1
    command = "curl -s http://%s:%s/wm/topology/route/%s/%s/%s/%s/json" % (RestIP, RestPort, v1, p1, v2, p2)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("No route")
    parsedResult = {}

  path = []
  if parsedResult.has_key('flowEntries'):
    flowEntries= parsedResult['flowEntries']
    for i, v in enumerate(flowEntries):
      if i < len(flowEntries) - 1:
        sdpid= flowEntries[i]['dpid']['value']
        ddpid = flowEntries[i+1]['dpid']['value']
        path.append( (sdpid, ddpid))

  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/links/json\'" % (RestIP, RestPort)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

  for v in parsedResult:
    link = {}
    if v.has_key('dst-switch'):
      dst_dpid = str(v['dst-switch'])
      dst_id = node_id(switches, dst_dpid)
    if v.has_key('src-switch'):
      src_dpid = str(v['src-switch'])
      src_id = node_id(switches, src_dpid)
    link['source'] = src_id
    link['target'] = dst_id

    onpath = 0
    for (s,d) in path:
      if s == v['src-switch'] and d == v['dst-switch']:
        onpath = 1
        break
    link['type'] = onpath

    links.append(link)

  topo['nodes'] = switches
  topo['links'] = links

  js = json.dumps(topo)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

#@app.route("/wm/topology/toporoute/00:00:00:00:00:a1/2/00:00:00:00:00:c1/3/json")
#@app.route("/wm/topology/toporoute/<srcdpid>/<srcport>/<destdpid>/<destport>/json")
@app.route("/wm/topology/toporoute/<v1>/<p1>/<v2>/<p2>/json")
def shortest_path(v1, p1, v2, p2):
  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/switches/all/json\'" % (RestIP, RestPort)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

  topo = {}
  switches = []
  links = []

  for v in parsedResult:
    if v.has_key('dpid'):
      dpid = str(v['dpid'])
      state = str(v['state'])
      sw = {}
      sw['name']=dpid
      if str(v['state']) == "ACTIVE":
        if dpid[-2:-1] == "a":
         sw['group']=1
        if dpid[-2:-1] == "b":
         sw['group']=2
        if dpid[-2:-1] == "c":
         sw['group']=3
      if str(v['state']) == "INACTIVE":
         sw['group']=0

      switches.append(sw)

  try:
    command = "curl -s http://%s:%s/wm/topology/route/%s/%s/%s/%s/json" % (RestIP, RestPort, v1, p1, v2, p2)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("No route")
    parsedResult = []
#    exit(1)

  path = [];
  for i, v in enumerate(parsedResult):
    if i < len(parsedResult) - 1:
      sdpid= parsedResult[i]['switch']
      ddpid = parsedResult[i+1]['switch']
      path.append( (sdpid, ddpid))

  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/links/json\'" % (RestIP, RestPort)
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

  for v in parsedResult:
    link = {}
    if v.has_key('dst-switch'):
      dst_dpid = str(v['dst-switch'])
      dst_id = node_id(switches, dst_dpid)
    if v.has_key('src-switch'):
      src_dpid = str(v['src-switch'])
      src_id = node_id(switches, src_dpid)
    link['source'] = src_id
    link['target'] = dst_id
    onpath = 0
    for (s,d) in path:
      if s == v['src-switch'] and d == v['dst-switch']:
        onpath = 1
        break

    link['type'] = onpath
    links.append(link)

  topo['nodes'] = switches
  topo['links'] = links

  js = json.dumps(topo)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

@app.route("/wm/core/controller/switches/json")
def query_switch():
  try:
    command = "curl -s \'http://%s:%s/wm/core/topology/switches/all/json\'" % (RestIP, RestPort)
#    http://localhost:8080/wm/core/topology/switches/active/json
    print command
    result = os.popen(command).read()
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

#  print command
#  print result
  switches_ = []
  for v in parsedResult:
    if v.has_key('dpid'):
      if v.has_key('dpid') and str(v['state']) == "ACTIVE":#;if you want only ACTIVE nodes
        dpid = str(v['dpid'])
        state = str(v['state'])
        sw = {}
        sw['dpid']=dpid
        sw['active']=state
        switches_.append(sw)

#  pp.pprint(switches_)
  js = json.dumps(switches_)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

@app.route("/wm/device/")
def devices():
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices\?key=type\&value=device" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

  devices = []
  for v in parsedResult:
    dl_addr = v['dl_addr']
    nw_addr = v['nw_addr']
    vertex = v['_id']
    mac = []
    mac.append(dl_addr)
    ip = []
    ip.append(nw_addr)
    device = {}
    device['entryClass']="DefaultEntryClass"
    device['mac']=mac
    device['ipv4']=ip
    device['vlan']=[]
    device['lastSeen']=0
    attachpoints =[]

    port, dpid = deviceV_to_attachpoint(vertex)
    attachpoint = {}
    attachpoint['port']=port
    attachpoint['switchDPID']=dpid
    attachpoints.append(attachpoint)
    device['attachmentPoint']=attachpoints
    devices.append(device)

  js = json.dumps(devices)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

#{"entityClass":"DefaultEntityClass","mac":["7c:d1:c3:e0:8c:a3"],"ipv4":["192.168.2.102","10.1.10.35"],"vlan":[],"attachmentPoint":[{"port":13,"switchDPID":"00:01:00:12:e2:78:32:44","errorStatus":null}],"lastSeen":1357333593496}

## return fake stat for now
@app.route("/wm/core/switch/<switchId>/<statType>/json")
def switch_stat(switchId, statType):
    if statType == "desc":
        desc=[{"length":1056,"serialNumber":"None","manufacturerDescription":"Nicira Networks, Inc.","hardwareDescription":"Open vSwitch","softwareDescription":"1.4.0+build0","datapathDescription":"None"}]
        ret = {}
        ret[switchId]=desc
    elif statType == "aggregate":
        aggr = {"packetCount":0,"byteCount":0,"flowCount":0}
        ret = {}
        ret[switchId]=aggr
    else:
        ret = {}

    js = json.dumps(ret)
    resp = Response(js, status=200, mimetype='application/json')
    return resp


@app.route("/wm/topology/links/json")
def query_links():
  try:
    command = 'curl -s http://%s:%s/graphs/%s/vertices?key=type\&value=port' % (RestIP, RestPort, DBName)
    print command
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return
#    sys.exit(0)

  debug("query_links %s" % command)
#  pp.pprint(parsedResult)
  sport = []
  links = []
  for v in parsedResult:
    srcport = v['_id']
    try:
      command = "curl -s http://%s:%s/graphs/%s/vertices/%d/out?_label=link" % (RestIP, RestPort, DBName, srcport)
      print command
      result = os.popen(command).read()
      linkResults = json.loads(result)['results']
    except:
      log_error("REST IF has issue: %s" % command)
      log_error("%s" % result)
      return
#      sys.exit(0)

    for p in linkResults:
      if p.has_key('type') and p['type'] == "port":
        dstport = p['_id']
        (sport, sdpid) = portV_to_port_dpid(srcport)
        (dport, ddpid) = portV_to_port_dpid(dstport)
        link = {}
        link["src-switch"]=sdpid
        link["src-port"]=sport
        link["src-port-state"]=0
        link["dst-switch"]=ddpid
        link["dst-port"]=dport
        link["dst-port-state"]=0
        link["type"]="internal"
        links.append(link)

#  pp.pprint(links)
  js = json.dumps(links)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

@app.route("/controller_status")
def controller_status():
#  onos_check="ssh -i ~/.ssh/onlabkey.pem %s ONOS/start-onos.sh status | awk '{print $1}'"
  onos_check="cd; onos status | grep %s | awk '{print $2}'"
  #cassandra_check="ssh -i ~/.ssh/onlabkey.pem %s ONOS/start-cassandra.sh status"

  cont_status=[]
  for i in controllers:
    status={}
    onos=os.popen(onos_check % i).read()[:-1]
#    onos=os.popen(onos_check % (i, i.lower())).read()[:-1]
    status["name"]=i
    status["onos"]=onos
    status["cassandra"]=0
    cont_status.append(status)

  js = json.dumps(cont_status)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

### Command ###
@app.route("/gui/controller/<cmd>/<controller_name>")
def controller_status_change(cmd, controller_name):
  if (TESTBED == "hw"):
    start_onos="/home/admin/bin/onos start %s" % (controller_name[-1:])
#    start_onos="/home/admin/bin/onos start %s > /tmp/debug " % (controller_name[-1:])
    stop_onos="/home/admin/bin/onos stop %s" % (controller_name[-1:])
#    stop_onos="/home/admin/bin/onos stop %s > /tmp/debug " % (controller_name[-1:])
#    print "Debug: Controller command %s called %s" % (cmd, controller_name)
  else:
    # No longer use -i to specify keys (use .ssh/config to specify it)
    start_onos="ssh %s ONOS/start-onos.sh start" % (controller_name)
    stop_onos="ssh %s ONOS/start-onos.sh stop" % (controller_name)
#    start_onos="ssh -i ~/.ssh/onlabkey.pem %s ONOS/start-onos.sh start" % (controller_name)
#    stop_onos="ssh -i ~/.ssh/onlabkey.pem %s ONOS/start-onos.sh stop" % (controller_name)

  if cmd == "up":
    result=os.popen(start_onos).read()
    ret = "controller %s is up: %s" % (controller_name, result)
  elif cmd == "down":
    result=os.popen(stop_onos).read()
    ret = "controller %s is down: %s" % (controller_name, result)

  return ret

@app.route("/gui/switchctrl/<cmd>")
def switch_controller_setting(cmd):
  if cmd =="local":
    print "All aggr switches connects to local controller only"
    result=""
    if (TESTBED == "sw"):
      for i in range(1, len(controllers)):
          cmd_string="ssh %s 'cd ONOS/scripts; ./ctrl-local.sh'" % (controllers[i])
          result += os.popen(cmd_string).read()
    else:
      cmd_string="cd; switch local > /tmp/watch"
      result += os.popen(cmd_string).read()
  elif cmd =="all":
    print "All aggr switches connects to all controllers except for core controller"
    result=""
    if (TESTBED == "sw"):
      for i in range(1, len(controllers)):
        cmd_string="ssh %s 'cd ONOS/scripts; ./ctrl-add-ext.sh'" % (controllers[i])
#        cmd_string="ssh -i ~/.ssh/onlabkey.pem %s 'cd ONOS/scripts; ./ctrl-add-ext.sh'" % (controllers[i])
        print "cmd is: "+cmd_string
        result += os.popen(cmd_string).read()
    else:
      cmd_string="/home/admin/bin/switch all > /tmp/watch"
      result += os.popen(cmd_string).read()

  return result

@app.route("/gui/reset")
def reset_demo():
  if (TESTBED == "hw"):
    cmd_string="cd ~/bin; ./demo-reset-hw.sh > /tmp/watch &"
  else:
    cmd_string="cd ~/ONOS/scripts; ./demo-reset-sw.sh > /tmp/watch &"
  os.popen(cmd_string)
  return "Reset" 

@app.route("/gui/scale")
def scale_demo():
  if (TESTBED == "hw"):
    cmd_string="cd ~/bin;  ~/bin/demo-scale-out-hw.sh > /tmp/watch &"
  else:
    cmd_string="cd ~/ONOS/scripts; ./demo-scale-out-sw.sh > /tmp/watch &"
  os.popen(cmd_string)
  return "scale"

@app.route("/gui/switch/<cmd>/<dpid>")
def switch_status_change(cmd, dpid):
  result = ""
  if (TESTBED == "hw"):
    return result

  r = re.compile(':')
  dpid = re.sub(r, '', dpid)
  host=controllers[0]
  cmd_string="ssh %s 'cd ONOS/scripts; ./switch.sh %s %s'" % (host, dpid, cmd)
#  cmd_string="ssh -i ~/.ssh/onlabkey.pem %s 'cd ONOS/scripts; ./switch.sh %s %s'" % (host, dpid, cmd)
  get_status="ssh -i ~/.ssh/onlabkey.pem %s 'cd ONOS/scripts; ./switch.sh %s'" % (host, dpid)
  print "cmd_string"

  if cmd =="up" or cmd=="down":
    print "make dpid %s %s" % (dpid, cmd)
    os.popen(cmd_string)
    result=os.popen(get_status).read()

  return result

#* Link Up
#http://localhost:9000/gui/link/up/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>
@app.route("/gui/link/up/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>")
def link_up(src_dpid, src_port, dst_dpid, dst_port):
  result = ""

  if (TESTBED == "sw"):
    result = link_up_sw(src_dpid, src_port, dst_dpid, dst_port)
  else:
    result = link_up_hw(src_dpid, src_port, dst_dpid, dst_port)
  return result

# Link up on software testbed
def link_up_sw(src_dpid, src_port, dst_dpid, dst_port):

  cmd = 'up'
  result=""
  for dpid in (src_dpid, dst_dpid):
    if dpid in core_switches:
      host = controllers[0]
    else:
      hostid=int(dpid.split(':')[-2])
      host = controllers[hostid-1]

    if dpid == src_dpid:
      (port, dontcare) = get_link_ports(dpid, dst_dpid)
    else:
      (port, dontcare) = get_link_ports(dpid, src_dpid)

#    cmd_string="ssh -i ~/.ssh/onlabkey.pem %s 'cd ONOS/scripts; ./link.sh %s %s %s'" % (host, dpid, port, cmd)
    cmd_string="ssh %s 'cd ONOS/scripts; ./link.sh %s %s %s'" % (host, dpid, port, cmd)
    print cmd_string
    res=os.popen(cmd_string).read()
    result = result + ' ' + res

  return result

#      if hostid == 2 :
#        src_ports = [51]
#      else :
#        src_ports = [26]
#
#    for port in src_ports :
#      cmd_string="ssh -i ~/.ssh/onlabkey.pem %s 'cd ONOS/scripts; ./link.sh %s %s %s'" % (host, dpid, port, cmd)
#      print cmd_string
#      res=os.popen(cmd_string).read()



# Link up on hardware testbed
def link_up_hw(src_dpid, src_port, dst_dpid, dst_port):

	port1 = src_port
	port2 = dst_port
	if src_dpid == "00:00:00:00:ba:5e:ba:11":
		if dst_dpid == "00:00:00:08:a2:08:f9:01":
			port1 = 24
			port2 = 24
		elif dst_dpid == "00:01:00:16:97:08:9a:46":
			port1 = 23
			port2 = 23
	elif src_dpid == "00:00:00:00:ba:5e:ba:13":
                if dst_dpid == "00:00:20:4e:7f:51:8a:35":
			port1 = 22
			port2 = 22
                elif dst_dpid == "00:00:00:00:00:00:ba:12":
			port1 = 23
			port2 = 23
	elif src_dpid == "00:00:00:00:00:00:ba:12":
                if dst_dpid == "00:00:00:00:ba:5e:ba:13":
			port1 = 23
			port2 = 23
                elif dst_dpid == "00:00:00:08:a2:08:f9:01":
			port1 = 22
			port2 = 22
                elif dst_dpid == "00:00:20:4e:7f:51:8a:35":
			port1 = 24
			port2 = 21
	elif src_dpid == "00:01:00:16:97:08:9a:46":
                if dst_dpid == "00:00:00:00:ba:5e:ba:11":
			port1 = 23
			port2 = 23
                elif dst_dpid == "00:00:20:4e:7f:51:8a:35":
			port1 = 24
			port2 = 24
	elif src_dpid == "00:00:00:08:a2:08:f9:01":
                if dst_dpid == "00:00:00:00:ba:5e:ba:11":
			port1 = 24
			port2 = 24
                elif dst_dpid == "00:00:00:00:00:00:ba:12":
			port1 = 22
			port2 = 22
                elif dst_dpid == "00:00:20:4e:7f:51:8a:35":
			port1 = 23
			port2 = 23
	elif src_dpid == "00:00:20:4e:7f:51:8a:35":
                if dst_dpid == "00:00:00:00:00:00:ba:12":
			port1 = 21
			port2 = 24
                elif dst_dpid == "00:00:00:00:ba:5e:ba:13":
			port1 = 22
			port2 = 22
                elif dst_dpid == "00:01:00:16:97:08:9a:46":
			port1 = 24
			port2 = 24
                elif dst_dpid == "00:00:00:08:a2:08:f9:01":
			port1 = 23
			port2 = 23

	cmd = 'up'
	result=""
	host = controllers[0]
	cmd_string="~/ONOS/scripts/link-hw.sh %s %s %s " % (src_dpid, port1, cmd)
	print cmd_string
	res=os.popen(cmd_string).read()
	result = result + ' ' + res
	cmd_string="~/ONOS/scripts/link-hw.sh %s %s %s " % (dst_dpid, port2, cmd)
	print cmd_string
	res=os.popen(cmd_string).read()
	result = result + ' ' + res


	return result


#* Link Down
#http://localhost:9000/gui/link/down/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>
@app.route("/gui/link/<cmd>/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>")
def link_down(cmd, src_dpid, src_port, dst_dpid, dst_port):

  if src_dpid in core_switches:
    host = controllers[0]
  else:
    hostid=int(src_dpid.split(':')[-2])
    host = controllers[hostid-1]

  if (TESTBED == "sw"):
    cmd_string="ssh %s 'cd ONOS/scripts; ./link.sh %s %s %s'" % (host, src_dpid, src_port, cmd)
  else:
    if ( src_dpid == "00:00:00:08:a2:08:f9:01" ):
      cmd_string="~/ONOS/scripts/link-hw.sh %s %s %s " % ( dst_dpid, dst_port, cmd)
    else:
      cmd_string="~/ONOS/scripts/link-hw.sh %s %s %s " % ( src_dpid, src_port, cmd)
  print cmd_string

  result=os.popen(cmd_string).read()

  return result

#* Create Flow
#http://localhost:9000/gui/addflow/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>/<srcMAC>/<dstMAC>
#1 FOOBAR 00:00:00:00:00:00:01:01 1 00:00:00:00:00:00:01:0b 1 matchSrcMac 00:00:00:00:00:00 matchDstMac 00:01:00:00:00:00
@app.route("/gui/addflow/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>/<srcMAC>/<dstMAC>")
def add_flow(src_dpid, src_port, dst_dpid, dst_port, srcMAC, dstMAC):
  host = pick_host()
  url ="%s/wm/flow/getsummary/%s/%s/json" % (host, 0, 0)
  (code, result) = get_json(url)
  parsedResult = json.loads(result)
  if len(parsedResult) > 0:
    if parsedResult[-1].has_key('flowId'):
      flow_nr = int(parsedResult[-1]['flowId'], 16)
  else:
    flow_nr = -1  # first flow
    print "first flow"

  flow_nr += 1
  command =  "%s/web/add_flow.py -m onos %d %s %s %s %s %s matchSrcMac %s matchDstMac %s" % (ONOSDIR, flow_nr, "dummy", src_dpid, src_port, dst_dpid, dst_port, srcMAC, dstMAC)
  flow_nr += 1
  command1 = "%s/web/add_flow.py -m onos %d %s %s %s %s %s matchSrcMac %s matchDstMac %s" % (ONOSDIR, flow_nr, "dummy", dst_dpid, dst_port, src_dpid, src_port, dstMAC, srcMAC)
  print "add flow: %s, %s" % (command, command1)
  errcode = os.popen(command).read()
  errcode1 = os.popen(command1).read()
  ret=command+":"+errcode+" "+command1+":"+errcode1
  print ret 
  return ret

#* Delete Flow
#http://localhost:9000/gui/delflow/<flow_id>
@app.route("/gui/delflow/<flow_id>")
def del_flow(flow_id):
  command = "%/web/delete_flow.py %s" % (ONOSDIR, flow_id)
  print command
  errcode = os.popen(command).read()
  return errcode

#* Start Iperf Througput
#http://localhost:9000/gui/iperf/start/<flow_id>/<duration>
@app.route("/gui/iperf/start/<flow_id>/<duration>/<samples>")
def iperf_start(flow_id,duration,samples):
  try:
    command = "curl -s \'http://%s:%s/wm/flow/get/%s/json\'" % (RestIP, RestPort, flow_id)
    print command
    result = os.popen(command).read()
    if len(result) == 0:
      print "No Flow found"
      return "Flow %s not found" % (flow_id);
  except:
    print "REST IF has issue"
    return "REST IF has issue"
    exit

  parsedResult = json.loads(result)

  flowId = int(parsedResult['flowId']['value'], 16)
  src_dpid = parsedResult['dataPath']['srcPort']['dpid']['value']
  src_port = parsedResult['dataPath']['srcPort']['port']['value']
  dst_dpid = parsedResult['dataPath']['dstPort']['dpid']['value']
  dst_port = parsedResult['dataPath']['dstPort']['port']['value']
#  print "FlowPath: (flowId = %s src = %s/%s dst = %s/%s" % (flowId, src_dpid, src_port, dst_dpid, dst_port)

  if src_dpid in core_switches:
      src_host = controllers[0]
  else:
      hostid=int(src_dpid.split(':')[-2])
      if TESTBED == "hw":
        src_host = "mininet%i" % hostid
      else:
        src_host = controllers[hostid-1]

  if dst_dpid in core_switches:
      dst_host = controllers[0]
  else:
      hostid=int(dst_dpid.split(':')[-2])
      if TESTBED == "hw":
        dst_host = "mininet%i" % hostid
      else:
        dst_host = controllers[hostid-1]

# /runiperf.sh <flowid> <src_dpid> <dst_dpid> hw:svr|sw:svr|hw:client|sw:client <proto>/<duration>/<interval>/<samples>
  protocol="udp"
  interval=0.1
  if TESTBED == "hw":
    cmd_string="dsh -w %s 'cd ONOS/scripts; " % dst_host
  else:
    cmd_string="ssh %s 'cd ONOS/scripts; " % dst_host
  cmd_string += "./runiperf.sh %d %s %s %s:%s %s/%s/%s/%s'" % (flowId, src_dpid, dst_dpid, TESTBED, "svr", protocol, duration, interval, samples)
  print cmd_string
  os.popen(cmd_string)

  if TESTBED == "hw":
    cmd_string="dsh -w %s 'cd ONOS/scripts; " % src_host
  else:
    cmd_string="ssh %s 'cd ONOS/scripts;" % src_host
  cmd_string+="./runiperf.sh %d %s %s %s:%s %s/%s/%s/%s'" % (flowId, src_dpid, dst_dpid, TESTBED, "client", protocol, duration, interval, samples)
  print cmd_string
  os.popen(cmd_string)

  return cmd_string


#* Get Iperf Throughput
#http://localhost:9000/gui/iperf/rate/<flow_id>
@app.route("/gui/iperf/rate/<flow_id>")
def iperf_rate(flow_id):
  try:
    command = "curl -s \'http://%s:%s/wm/flow/get/%s/json\'" % (RestIP, RestPort, flow_id)
    print command
    result = os.popen(command).read()
    if len(result) == 0:
      resp = Response(result, status=400, mimetype='text/html')
      return "no such iperf flow (flowid %s)" % flow_id;
  except:
    print "REST IF has issue"
    exit

  parsedResult = json.loads(result)

  flowId = int(parsedResult['flowId']['value'], 16)
  src_dpid = parsedResult['dataPath']['srcPort']['dpid']['value']
  src_port = parsedResult['dataPath']['srcPort']['port']['value']
  dst_dpid = parsedResult['dataPath']['dstPort']['dpid']['value']
  dst_port = parsedResult['dataPath']['dstPort']['port']['value']

  if dst_dpid in core_switches:
    host = controllers[0]
  else:
    hostid=int(dst_dpid.split(':')[-2])
    if TESTBED == "hw":
      host = "mininet%i" % hostid
    else:
      host = controllers[hostid-1]

  try:
    command = "curl -s http://%s:%s/log/iperfsvr_%s.out" % (host, 9000, flow_id)
    print command
    result = os.popen(command).read()
  except:
    exit

  if re.match("Cannot", result):
    resp = Response(result, status=400, mimetype='text/html')
    return "no iperf file found (host %s flowid %s): %s" % (host, flow_id, result)
  else:
    resp = Response(result, status=200, mimetype='application/json')
    return resp

if __name__ == "__main__":
  random.seed()
  read_config()
  read_link_def()
  if len(sys.argv) > 1 and sys.argv[1] == "-d":
#      add_flow("00:00:00:00:00:00:02:02", 1, "00:00:00:00:00:00:03:02", 1, "00:00:00:00:02:02", "00:00:00:00:03:0c")
#     link_change("up", "00:00:00:00:ba:5e:ba:11", 1, "00:00:00:00:00:00:00:00", 1)
#     link_change("down", "00:00:20:4e:7f:51:8a:35", 1, "00:00:00:00:00:00:00:00", 1)
#     link_change("up", "00:00:00:00:00:00:02:03", 1, "00:00:00:00:00:00:00:00", 1)
#     link_change("down", "00:00:00:00:00:00:07:12", 1, "00:00:00:00:00:00:00:00", 1)
#    print "-- query all switches --"
#    query_switch()
#    print "-- query topo --"
#    topology_for_gui()
#    link_change(1,2,3,4)
    print "-- query all links --"
#    query_links()
#    print "-- query all devices --"
#    devices()
#    iperf_start(1,10,15)
#    iperf_rate(1)
#    switches()
#    add_flow(1,2,3,4,5,6)
    reset_demo()
  else:
    app.debug = True
    app.run(threaded=True, host="0.0.0.0", port=9000)
