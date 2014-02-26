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

# @app.route("/wm/onos/datagrid/get/map/<map-name>/json ")
# Sample output:

def print_datagrid_map(parsedResult):
  print '%s' % (parsedResult),

def get_datagrid_map():
  try:
    command = "curl -s \"http://%s:%s/wm/onos/datagrid/get/ng-events/json\"" % (ControllerIP, ControllerPort)
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
  
  # Do the work
  get_datagrid_map()
