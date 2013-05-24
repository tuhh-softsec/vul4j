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

#
# TODO: remove this! We don't use JSON argument here!
# curl http://127.0.0.1:8080/wm/flow/delete/{"value":"0xf"}/json'
#

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

# @app.route("/wm/flow/measurement-get-per-flow-install-time/json")
def measurement_get_per_flow_install_time():
  command = "curl -s \"http://%s:%s/wm/flow/measurement-get-per-flow-install-time/json\"" % (ControllerIP, ControllerPort)
  debug("measurement_get_per_flow_install_time %s" % command)
  result = os.popen(command).read()
  print '%s' % (result)
  # parsedResult = json.loads(result)
  # debug("parsed %s" % parsedResult)

if __name__ == "__main__":
  usage_msg = "Get the measured time per flow to install each stored flow path\n"
  usage_msg = usage_msg + "Usage: %s\n" % (sys.argv[0])
  usage_msg = usage_msg + "\n"

  # app.debug = False;

  # Usage info
  if len(sys.argv) > 1 and (sys.argv[1] == "-h" or sys.argv[1] == "--help"):
    print(usage_msg)
    exit(0)

  # Check arguments

  # Do the work
  measurement_get_per_flow_install_time()
