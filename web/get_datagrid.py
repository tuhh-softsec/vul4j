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

# @app.route("/wm/datagrid/get/map/<map-name>/json ")
# Sample output:

def print_datagrid_map(parsedResult):
  print '%s' % (parsedResult)

def get_datagrid_map(map_name):
  try:
    command = "curl -s \"http://%s:%s/wm/datagrid/get/map/%s/json\"" % (ControllerIP, ControllerPort, map_name)
    debug("get_datagrid_map %s" % command)

    result = os.popen(command).read()
    debug("result %s" % result)
    if len(result) == 0:
      print "No Map found"
      return;

    # TODO: For now, the string is not JSON-formatted
    # parsedResult = json.loads(result)
    parsedResult = result
    debug("parsed %s" % parsedResult)
  except:
    log_error("Controller IF has issue")
    exit(1)

  print_datagrid_map(parsedResult)


if __name__ == "__main__":
  usage_msg1 = "Usage:\n"
  usage_msg2 = "%s <map_name> : Print datagrid map with name of <map_name>\n" % (sys.argv[0])
  usage_msg3 = "    Valid map names:\n"
  usage_msg4 = "        all              : Print all maps\n"
  usage_msg5 = "        flow             : Print all flows\n"
  usage_msg6 = "        flow-entry       : Print all flow entries\n"
  usage_msg7 = "        topology         : Print the topology\n"
  usage_msg = usage_msg1 + usage_msg2 + usage_msg3 + usage_msg4 + usage_msg5
  usage_msg = usage_msg + usage_msg6 + usage_msg7

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
  get_datagrid_map(sys.argv[1])
