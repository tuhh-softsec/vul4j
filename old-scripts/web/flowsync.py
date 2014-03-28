#! /usr/bin/env python


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

# @app.route("/wm/onos/flowprogrammer/synchronizer/sync/<dpid>/json")
# Sample output:
#  "true"
def synchronize(dpid):
  try:
    command = "curl -s \"http://%s:%s/wm/onos/flowprogrammer/synchronizer/sync/%s/json\"" % (ControllerIP, ControllerPort, dpid)
    debug("synchronize %s" % command)
     
    result = os.popen(command).read()
    debug("result %s" % result)
    if result == "false":
      print "Failed to synchronize"
      return;
  except:
    log_error("Controller IF has issue")
    exit(1)
  
  print "Synchronization of switch %s has successfully began" % (dpid)

# @app.route("/wm/onos/flowprogrammer/synchronizer/interrupt/<dpid>/json")
# Sample output:
#  "true"
def interrupt(dpid):
  try:
    command = "curl -s \"http://%s:%s/wm/onos/flowprogrammer/synchronizer/interrupt/%s/json\"" % (ControllerIP, ControllerPort, dpid)
    debug("interrupt %s" % command)
     
    result = os.popen(command).read()
    debug("result %s" % result)
    if result == "false":
      print "Failed to interrupt synchronization"
      return;
  except:
    log_error("Controller IF has issue")
    exit(1)
  
  print "Synchronization of switch %s has successfully interrupted" % (dpid)


if __name__ == "__main__":
  usage_msg1 = "Usage:\n"
  usage_msg2 = "%s sync <dpid>      : Start synchronization of the switch\n" % (sys.argv[0])
  usage_msg3 = "                interrupt <dpid> : Interrupt synchronization of the switch\n"
  usage_msg = usage_msg1 + usage_msg2 + usage_msg3;
  
  app.debug = True;

  # Usage info
  if len(sys.argv) > 1 and (sys.argv[1] == "-h" or sys.argv[1] == "--help"):
    print(usage_msg)
    exit(0)

  # Check arguments
  if len(sys.argv) < 2:
    log_error(usage_msg)
    exit(1)

  # Do the work
  if sys.argv[1] == "sync":
    if len(sys.argv) < 3:
      log_error(usage_msg)
      exit(1)
    synchronize(sys.argv[2])
  elif sys.argv[1] == "interrupt":
    if len(sys.argv) < 3:
      log_error(usage_msg)
      exit(1)
    interrupt(sys.argv[2])
  else:
    log_error(usage_msg)
    exit(1)
 