#! /usr/bin/env python
# -*- Mode: python; py-indent-offset: 4; tab-width: 8; indent-tabs-mode: t; -*-

import pprint
import os
import sys
import subprocess
import json
import argparse
import io
import time

from flask import Flask, json, Response, render_template, make_response, request

## Global Var ##
ControllerIP="127.0.0.1"
ControllerPort=8080

DEBUG=0
pp = pprint.PrettyPrinter(indent=4)

app = Flask(__name__)

## Worker Functions ##
def log_error(txt):
  print '%s' % (txt)

def debug(txt):
  if DEBUG:
    print '%s' % (txt)

# @app.route("/wm/flow/get/<flow-id>/json")
# Sample output:
# {"flowId":{"value":"0x5"},"installerId":{"value":"FOOBAR"},"dataPath":{"srcPort":{"dpid":{"value":"00:00:00:00:00:00:00:01"},"port":{"value":0}},"dstPort":{"dpid":{"value":"00:00:00:00:00:00:00:02"},"port":{"value":0}},"flowEntries":[{"flowEntryId":"0x1389","flowEntryMatch":null,"flowEntryActions":null,"dpid":{"value":"00:00:00:00:00:00:00:01"},"inPort":{"value":0},"outPort":{"value":1},"flowEntryUserState":"FE_USER_DELETE","flowEntrySwitchState":"FE_SWITCH_NOT_UPDATED","flowEntryErrorState":null},{"flowEntryId":"0x138a","flowEntryMatch":null,"flowEntryActions":null,"dpid":{"value":"00:00:00:00:00:00:00:02"},"inPort":{"value":9},"outPort":{"value":0},"flowEntryUserState":"FE_USER_DELETE","flowEntrySwitchState":"FE_SWITCH_NOT_UPDATED","flowEntryErrorState":null}]}}
def get_flow_path(flow_id):
  try:
    command = "curl -s \"http://%s:%s/wm/flow/get/%s/json\"" % (ControllerIP, ControllerPort, flow_id)
    debug("get_flow_path %s" % command)

    result = os.popen(command).read()
    debug("result %s" % result)
    if len(result) == 0:
	print "No Flow found"
	return;

    parsedResult = json.loads(result)
    debug("parsed %s" % parsedResult)
  except:
    log_error("Controller IF has issue")
    exit(1)

  flowId = parsedResult['flowId']['value'];
  installerId = parsedResult['installerId']['value'];
  srcSwitch = parsedResult['dataPath']['srcPort']['dpid']['value'];
  srcPort = parsedResult['dataPath']['srcPort']['port']['value'];
  dstSwitch = parsedResult['dataPath']['dstPort']['dpid']['value'];
  dstPort = parsedResult['dataPath']['dstPort']['port']['value'];

  print "FlowPath: (flowId = %s installerId = %s src = %s/%s dst = %s/%s)" % (flowId, installerId, srcSwitch, srcPort, dstSwitch, dstPort)

  for f in parsedResult['dataPath']['flowEntries']:
    inPort = f['inPort']['value'];
    outPort = f['outPort']['value'];
    dpid = f['dpid']['value']
    userState = f['flowEntryUserState']
    switchState = f['flowEntrySwitchState']
    print "FlowEntry: (%s, %s, %s, %s, %s)" % (inPort, dpid, outPort, userState, switchState)


if __name__ == "__main__":
  usage_msg = "Usage: %s <flow_id>" % (sys.argv[0])

  # app.debug = False;

  # Usage info
  if len(sys.argv) > 1 and (sys.argv[1] == "-h" or sys.argv[1] == "--help"):
    print(usage_msg)
    exit(0)

  # Check arguments
  if len(sys.argv) < 2:
    log_error(usage_msg)
    exit(1)

  # Do the work
  get_flow_path(sys.argv[1]);
