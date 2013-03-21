#! /usr/bin/python

import os
import sys
import subprocess
import json
import argparse
import io
import time

from flask import Flask, json, Response, render_template, make_response, request

RestIP="127.0.0.1"
RestPort=8182
DBName="Cassandra-Netmap"

app = Flask(__name__)


## File Fetch ##
@app.route('/ui/img/<filename>', methods=['GET'])
@app.route('/img/<filename>', methods=['GET'])
@app.route('/css/<filename>', methods=['GET'])
@app.route('/js/models/<filename>', methods=['GET'])
@app.route('/js/views/<filename>', methods=['GET'])
@app.route('/js/<filename>', methods=['GET'])
@app.route('/lib/<filename>', methods=['GET'])
@app.route('/', methods=['GET'])
@app.route('/<filename>', methods=['GET'])
@app.route('/tpl/<filename>', methods=['GET'])
@app.route('/ons-demo/<filename>', methods=['GET'])
@app.route('/ons-demo/js/<filename>', methods=['GET'])
@app.route('/ons-demo/css/<filename>', methods=['GET'])
@app.route('/ons-demo/assets/<filename>', methods=['GET'])
@app.route('/ons-demo/data/<filename>', methods=['GET'])
def return_file(filename="index.html"):
  if request.path == "/":
    fullpath = "./index.html"
  else:
    fullpath = str(request.path)[1:]

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

  return response

## PROXY API (allows development where the webui is served from someplace other than the ONOS_HOST)##
ONOS_HOST="http://gui3.onlab.us:8080"

@app.route("/proxy/wm/core/topology/switches/all/json")
def switches():
  try:
    command = "curl -s %s/wm/core/topology/switches/all/json" % (ONOS_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/wm/core/topology/links/json")
def links():
  try:
    command = "curl -s %s/wm/core/topology/links/json" % (ONOS_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/wm/flow/getall/json")
def flows():
  try:
    command = "curl -s %s/wm/flow/getall/json" % (ONOS_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/wm/registry/controllers/json")
def registry_controllers():
  try:
    command = "curl -s %s/wm/registry/controllers/json" % (ONOS_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp

@app.route("/proxy/wm/registry/switches/json")
def registry_switches():
  try:
    command = "curl -s %s/wm/registry/switches/json" % (ONOS_HOST)
    print command
    result = os.popen(command).read()
  except:
    print "REST IF has issue"
    exit

  resp = Response(result, status=200, mimetype='application/json')
  return resp




## REST API ##
#@app.route("/wm/topology/links/json")
#def links():
#    global links_
#    js = json.dumps(links_)
#    resp = Response(js, status=200, mimetype='application/json')
#    return resp

#@app.route("/wm/core/controller/switches/json")
#def switches():
#    global switches_
#    js = json.dumps(switches_)
#    resp = Response(js, status=200, mimetype='application/json')
#    return resp

@app.route("/wm/device/")
def devices():
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices?key=type\&value=device" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  devices_ = []
  for v in parsedResult:
    if v['type'] == "device":
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
      try:
        command = "curl -s http://%s:%s/graphs/%s/vertices/%d/in" % (RestIP, RestPort, DBName, vertex)
        result = os.popen(command).read()
        parsedResult = json.loads(result)['results']
      except:
        log_error("REST IF has issue")
        exit

      port = parsedResult[0]['number']
      vertex = parsedResult[0]['_id']
      dpid = portid_to_switch_dpid(vertex)
      attachpoint = {}
      attachpoint['port']=port
      attachpoint['switchDPID']=dpid
      attachpoints.append(attachpoint)
      device['attachmentPoint']=attachpoints
      devices_.append(device)

  print devices_
  js = json.dumps(devices_)
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

@app.route("/wm/core/controller/switches/json")
def query_switch():
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices?key=type\&value=switch" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  switches_ = []
  for v in parsedResult:
    if v['type'] == "switch":
      dpid = str(v['dpid']) ;# removing quotation
      sw = {}
      sw['dpid']=dpid
      switches_.append(sw)

  print switches_
  js = json.dumps(switches_)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

@app.route("/wm/topology/links/json")
def query_links():
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices?key=type\&value=port" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  sport = []
  switches_ = []
  for v in parsedResult:
    srcport = v['_id']
    try:
      command = "curl -s http://%s:%s/graphs/%s/vertices/%d/out?_label=link" % (RestIP, RestPort, DBName, srcport)
      result = os.popen(command).read()
      linkResults = json.loads(result)['results']
    except:
      log_error("REST IF has issue")
      exit

    for p in linkResults:
      dstport = p['_id']
      (sport, sdpid) = get_port_switch(srcport)
      (dport, ddpid) = get_port_switch(dstport)
      link = {}
      link["src-switch"]=sdpid
      link["src-port"]=sport
      link["src-port-state"]=0
      link["dst-switch"]=ddpid
      link["dst-port"]=dport
      link["dst-port-state"]=0
      link["type"]="internal"
      switches_.append(link)

  print switches_
  js = json.dumps(switches_)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

def get_port_switch(vertex):
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices/%d" % (RestIP, RestPort, DBName, vertex)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  port_number = parsedResult['number']
  vertex_id = parsedResult['_id']
  switch_dpid = portid_to_switch_dpid(vertex_id)

  return (port_number, switch_dpid)

def portid_to_switch_dpid(vertex):
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices/%d/in" % (RestIP, RestPort, DBName, vertex)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  for v in parsedResult:
    if v['type'] == "switch":
      sw_dpid = v['dpid']
      break

  return sw_dpid

def id_to_dpid(vertex):
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices/%d" % (RestIP, RestPort, DBName, vertex)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  if parsedResult['type'] != "switch":
    print "not a switch vertex"
    exit
  else:
    sw_dpid = parsedResult['dpid']

  return sw_dpid


if __name__ == "__main__":
    app.debug = True
    app.run(host="0.0.0.0", port=9000)
#  query_switch()
#   query_links()
#  devices()
