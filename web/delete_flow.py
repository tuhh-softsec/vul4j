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

# @app.route("/wm/flow/delete/<flow-id>/json")
def delete_flow_path(flow_id):
  command = "curl -s \"http://%s:%s/wm/flow/delete/%s/json\"" % (ControllerIP, ControllerPort, flow_id)
  debug("delete_flow_path %s" % command)
  result = os.popen(command).read()
  debug("result %s" % result)
  # parsedResult = json.loads(result)
  # debug("parsed %s" % parsedResult)

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
  flow_id_arg = int(sys.argv[1], 0)
  delete_flow_path(flow_id_arg);
