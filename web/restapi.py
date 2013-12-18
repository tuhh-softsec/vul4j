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
DBName="titan-netmap"

app = Flask(__name__)

## Define static link and switch data for now (will be replaced by the one dynamically quering Network DB ## 
#links_=[{"src-switch":"00:00:00:00:00:00:00:06","src-port":3,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:09","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:08","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:06","dst-port":2,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:07","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:06","dst-port":1,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:06","src-port":1,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:07","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:03","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:02","dst-port":1,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0b","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0a","dst-port":1,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:01","src-port":1,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:02","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:01","src-port":2,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:06","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0d","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0a","dst-port":3,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:06","src-port":2,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:08","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:02","src-port":2,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:04","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:09","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:06","dst-port":3,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:04","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:02","dst-port":2,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0c","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0a","dst-port":2,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:06","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:01","dst-port":2,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:01","src-port":3,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0a","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0a","src-port":1,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0b","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:05","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:02","dst-port":3,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:02","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:01","dst-port":1,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0a","src-port":4,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:01","dst-port":3,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:02","src-port":3,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:05","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0a","src-port":2,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0c","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:02","src-port":1,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:03","dst-port":4,"dst-port-state":0,"type":"internal"},{"src-switch":"00:00:00:00:00:00:00:0a","src-port":3,"src-port-state":0,"dst-switch":"00:00:00:00:00:00:00:0d","dst-port":4,"dst-port-state":0,"type":"internal"}]

#switches_= [{"dpid":"00:00:00:00:00:00:00:0c"},{"dpid":"00:00:00:00:00:00:00:06"},{"dpid":"00:00:00:00:00:00:00:0d"},{"dpid":"00:00:00:00:00:00:00:05"},{"dpid":"00:00:00:00:00:00:00:07"},{"dpid":"00:00:00:00:00:00:00:08"},{"dpid":"00:00:00:00:00:00:00:09"},{"dpid":"00:00:00:00:00:00:00:02"},{"dpid":"00:00:00:00:00:00:00:0a"},{"dpid":"00:00:00:00:00:00:00:01"},{"dpid":"00:00:00:00:00:00:00:03"},{"dpid":"00:00:00:00:00:00:00:0b"},{"dpid":"00:00:00:00:00:00:00:04"}]


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

## REST API ##
#@app.route("/wm/onos/linkdiscovery/links/json")
#def links():
#    global links_
#    js = json.dumps(links_)
#    resp = Response(js, status=200, mimetype='application/json')
#    return resp

#@app.route("/wm/floodlight/core/controller/switches/json")
#def switches():
#    global switches_
#    js = json.dumps(switches_)
#    resp = Response(js, status=200, mimetype='application/json')
#    return resp

@app.route("/wm/floodlight/device/")
def devices():
  ret = []
  js = json.dumps(ret)
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

@app.route("/wm/floodlight/core/controller/switches/json")
def query_switch():
  try:
    command = "curl -s http://%s:%s/graphs/%s/vertices" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  switches_ = []
  for v in parsedResult:
    if v['type'] == "switch":
      dpid = str(v['dpid'])[1:-1] ;# removing quotation
      sw = {}
      sw['dpid']=dpid
      switches_.append(sw)

  print switches_
  js = json.dumps(switches_)
  resp = Response(js, status=200, mimetype='application/json')
  return resp

@app.route("/wm/onos/linkdiscovery/links/json")
def query_links():
  try:
    command = "curl -s http://%s:%s/graphs/%s/edges" % (RestIP, RestPort, DBName)
    result = os.popen(command).read()
    parsedResult = json.loads(result)['results']
  except:
    log_error("REST IF has issue")
    exit

  switches_ = []
  for v in parsedResult:
    if v['_label'] == "link":
      srcport = v['_inV']
      dstport = v['_outV']
      (sport, sdpid) = get_port_switch(srcport)
      (dport, ddpid) = get_port_switch(dstport)
      link = {}
      link["src-switch"]=sdpid[1:-1]
      link["src-port"]=sport
      link["src-port-state"]=0
      link["dst-switch"]=ddpid[1:-1]
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
