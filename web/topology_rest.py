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
from urllib2 import Request, urlopen, URLError, HTTPError

from flask import Flask, json, Response, render_template, make_response, request

CONFIG_FILE=os.getenv("HOME") + "/ONOS/web/config.json"
LINK_FILE=os.getenv("HOME") + "/ONOS/web/link.json"
ONOSDIR=os.getenv("HOME") + "/ONOS"

## Global Var for this proxy script setting.
# "0.0.0.0" means any interface
ProxyIP="0.0.0.0"
ProxyPort=9000

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

###### ONOS REST API ##############################
## Worker Func ###
def get_json(url):
  code = 200;
  try:
    response = urlopen(url)
  except URLError, e:
    print "get_json: REST IF %s has issue. Reason: %s" % (url, e.reason)
    result = ""
    return (500, result)
  except HTTPError, e:
    print "get_json: REST IF %s has issue. Code %s" % (url, e.code)
    result = ""
    return (e.code, result)

  print response
  result = response.read()
#  parsedResult = json.loads(result)
  return (code, result)


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
    url="http://%s:%s/wm/onos/topology/switches/json" % (RestIP, RestPort)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % url)
    log_error("%s" % result)
    return

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
    url="http://%s:%s/wm/onos/registry/switches/json" % (RestIP, RestPort)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % url)
    log_error("%s" % result)

  for key in parsedResult:
    dpid = key
    ctrl = parsedResult[dpid][0]['controllerId']
    sw_id = node_id(switches, dpid)
    if sw_id != -1:
      if switches[sw_id]['group'] != 0:
        switches[sw_id]['group'] = controllers.index(ctrl) + 1

  try:
    url = "http://%s:%s/wm/onos/topology/links/json" % (RestIP, RestPort)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % url)
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

    #onpath = 0
    #for (s,d) in path:
    #  if s == v['src-switch'] and d == v['dst-switch']:
    #    onpath = 1
    #    break
    #link['type'] = onpath

    links.append(link)

  topo['nodes'] = switches
  topo['links'] = links

  js = json.dumps(topo)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

@app.route("/wm/floodlight/topology/toporoute/<v1>/<p1>/<v2>/<p2>/json")
def shortest_path(v1, p1, v2, p2):
  try:
    url = "http://%s:%s/wm/onos/topology/switches/json" % (RestIP, RestPort)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return

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
    url = "http://%s:%s/wm/onos/topology/route/%s/%s/%s/%s/json" % (RestIP, RestPort, v1, p1, v2, p2)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("No route")
    parsedResult = []

  path = [];
  for i, v in enumerate(parsedResult):
    if i < len(parsedResult) - 1:
      sdpid= parsedResult['flowEntries'][i]['dpid']['value']
      ddpid= parsedResult['flowEntries'][i+1]['dpid']['value']
      path.append( (sdpid, ddpid))

  try:
    url = "http://%s:%s/wm/onos/topology/links/json" % (RestIP, RestPort)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % command)
    log_error("%s" % result)
    return

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

@app.route("/wm/floodlight/core/controller/switches/json")
def query_switch():
  try:
    url = "http://%s:%s/wm/onos/topology/switches/json" % (RestIP, RestPort)
    (code, result) = get_json(url)
    parsedResult = json.loads(result)
  except:
    log_error("REST IF has issue: %s" % url)
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

## return fake stat for now
@app.route("/wm/floodlight/core/switch/<switchId>/<statType>/json")
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

@app.route("/controller_status")
def controller_status():
  url= "http://%s:%d/wm/onos/registry/controllers/json" % (RestIP, RestPort)
  (code, result) = get_json(url)
  parsedResult = json.loads(result)

  cont_status=[]
  for i in controllers:
    status={}
    if i in parsedResult:
      onos=1
    else:
      onos=0
    status["name"]=i
    status["onos"]=onos
    status["cassandra"]=0
    cont_status.append(status)

  js = json.dumps(cont_status)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

if __name__ == "__main__":
  random.seed()
  read_config()
  if len(sys.argv) > 1 and sys.argv[1] == "-d":
    # for debugging
    #add_flow("00:00:00:00:00:00:02:02", 1, "00:00:00:00:00:00:03:02", 1, "00:00:00:00:02:02", "00:00:00:00:03:0c")
    #proxy_link_change("up", "00:00:00:00:ba:5e:ba:11", 1, "00:00:00:00:00:00:00:00", 1)
    #proxy_link_change("down", "00:00:20:4e:7f:51:8a:35", 1, "00:00:00:00:00:00:00:00", 1)
    #proxy_link_change("up", "00:00:00:00:00:00:02:03", 1, "00:00:00:00:00:00:00:00", 1)
    #proxy_link_change("down", "00:00:00:00:00:00:07:12", 1, "00:00:00:00:00:00:00:00", 1)
    #print "-- query all switches --"
    #query_switch()
    #print "-- query topo --"
    #topology_for_gui()
    ##print "-- query all links --"
    ##query_links()
    #print "-- query all devices --"
    #devices()
    #links()
    #switches()
    #reset_demo()
    pass
  else:
    app.debug = True
    app.run(threaded=True, host=ProxyIP, port=ProxyPort)