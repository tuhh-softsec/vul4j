#! /usr/bin/env python

import os
import sys
import subprocess
import json
import argparse
import io
import time

from flask import Flask, json, Response, render_template, make_response, request

## Global Var ##
RestIP="127.0.0.1"
RestPort=8182
DBName="Cassandra-Netmap"

app = Flask(__name__)

## Worker Functions ##
def log_error(txt):
  print '%s' % (txt)

def portV_to_dpid(vertex):
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

def switchV_to_dpid(vertex):
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

def portV_to_port_dpid(vertex):
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices/%d" % (RestIP, RestPort, DBName, vertex)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  port_number = parsedResult['number']
  switch_dpid = portV_to_dpid(vertex)

  return (port_number, switch_dpid)

def deviceV_to_attachpoint(vertex):
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices/%d/in" % (RestIP, RestPort, DBName, vertex)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit
    
  port = parsedResult[0]['number']
  vertex = parsedResult[0]['_id']
  dpid = portV_to_dpid(vertex)
  return port, dpid

## Rest APIs ##
### File Fetch ###
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

@app.route("/wm/device/")
def devices():
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices?key=type\&value=device" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

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

  print devices
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
      switches_.append(link)

  print switches_
  js = json.dumps(switches_)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

if __name__ == "__main__":
  if len(sys.argv) > 1 and sys.argv[1] == "-d":
    print "-- query all switches --"
    query_switch()
    print "-- query all links --"
    query_links()
    print "-- query all devices --"
    devices()
  else:
    app.debug = True
    app.run(host="0.0.0.0", port=9000)
