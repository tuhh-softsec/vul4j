#!/usr/bin/env python

import json
from urllib2 import Request, urlopen, URLError, HTTPError
from flask import Flask, json, Response, render_template, make_response, request

## Global Var for ON.Lab local REST ##
RestIP="localhost"
RestPort=8080
ONOS_DEFAULT_HOST="localhost" ;# Has to set if LB=False
DEBUG=1
controllers=["ubuntu1","ubuntu2","ubuntu3","ubuntu4"]

app = Flask(__name__)

## Worker Functions ##
def log_error(txt):
  print '%s' % (txt)

def debug(txt):
  if DEBUG:
    print '%s' % (txt)

def node_id(switch_array, dpid):
  id = -1
  for i, val in enumerate(switch_array):
    if val['name'] == dpid:
      id = i
      break

  return id

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

  result = response.read()
#  parsedResult = json.loads(result)
  return (code, result)

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

## API for ON.Lab local GUI ##
@app.route('/topology', methods=['GET'])
def topology_for_gui():
  try:
    #url="http://%s:%s/wm/onos/topology/switches/all/json" % (RestIP, RestPort)
    url="http://%s:%s/wm/onos/ng/switches/json" % (RestIP, RestPort)
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
    #url = "http://%s:%s/wm/onos/topology/links/json" % (RestIP, RestPort)
    url = "http://%s:%s/wm/onos/ng/links/json" % (RestIP, RestPort)
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
  app.debug = True
  app.run(threaded=True, host="0.0.0.0", port=9000)
