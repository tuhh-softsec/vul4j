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

# @app.route("/wm/onos/flowprogrammer/pusher/setrate/<dpid>/<rate>/json")
# Sample output:
#  "true"
def set_rate(dpid,rate):
  try:
    command = "curl -s \"http://%s:%s/wm/onos/flowprogrammer/pusher/setrate/%s/%s/json\"" % (ControllerIP, ControllerPort, dpid, rate)
    debug("set_rate %s" % command)
     
    result = os.popen(command).read()
    debug("result %s" % result)
    if result == "false":
      print "Failed to set rate"
      return;
  except:
    log_error("Controller IF has issue")
    exit(1)
  
  print "Sending rate to %s is successfully set to %s" % (dpid, rate)

# @app.route("/wm/onos/flowprogrammer/pusher/suspend/<dpid>/json")
# Sample output:
#  "true"
def suspend(dpid):
  try:
    command = "curl -s \"http://%s:%s/wm/onos/flowprogrammer/pusher/suspend/%s/json\"" % (ControllerIP, ControllerPort, dpid)
    debug("suspend %s" % command)
     
    result = os.popen(command).read()
    debug("result %s" % result)
    if result == "false":
      print "Failed to suspend"
      return;
  except:
    log_error("Controller IF has issue")
    exit(1)
  
  print "DPID %s is successfully suspended" % dpid

# @app.route("/wm/onos/flowprogrammer/pusher/resume/<dpid>/json")
# Sample output:
#  "true"
def resume(dpid):
  try:
    command = "curl -s \"http://%s:%s/wm/onos/flowprogrammer/pusher/resume/%s/json\"" % (ControllerIP, ControllerPort, dpid)
    debug("resume %s" % command)
     
    result = os.popen(command).read()
    debug("result %s" % result)
    if result == "false":
      print "Failed to resume"
      return;
  except:
    log_error("Controller IF has issue")
    exit(1)
  
  print "DPID %s is successfully resumed" % dpid

# @app.route("/wm/onos/flowprogrammer/pusher/barrier/<dpid>/json")
# Sample output:
#  "{"version":1,"type":"BARRIER_REPLY","length":8,"xid":4,"lengthU":8}"
def barrier(dpid):
  try:
    command = "curl -s \"http://%s:%s/wm/onos/flowprogrammer/pusher/barrier/%s/json\"" % (ControllerIP, ControllerPort, dpid)
    debug("barrier %s" % command)
     
    result = os.popen(command).read()
    debug("result %s" % result)
    if result == "false":
      print "Failed to send barrier"
      return;
  except:
    log_error("Controller IF has issue")
    exit(1)
  
  print "Barrier reply from %s : %s" % (dpid, result)


if __name__ == "__main__":
  usage_msg1 = "Usage:\n"
  usage_msg2 = "%s rate <dpid> <rate> : Set sending rate[bytes/ms] to the switch\n" % (sys.argv[0])
  usage_msg3 = "                   suspend <dpid>    : Suspend sending message to the switch\n"
  usage_msg4 = "                   resume <dpid>     : Resume sending message to the switch\n"
  usage_msg5 = "                   barrier <dpid>    : Send barrier message to the switch\n"
  usage_msg = usage_msg1 + usage_msg2 + usage_msg3 + usage_msg4 + usage_msg5;

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
  if sys.argv[1] == "rate":
    if len(sys.argv) < 4:
      log_error(usage_msg)
      exit(1)
    set_rate(sys.argv[2], sys.argv[3])
  elif sys.argv[1] == "suspend":
    if len(sys.argv) < 3:
      log_error(usage_msg)
      exit(1)
    suspend(sys.argv[2])
  elif sys.argv[1] == "resume":
    if len(sys.argv) < 3:
      log_error(usage_msg)
      exit(1)
    resume(sys.argv[2])
  elif sys.argv[1] == "barrier":
    if len(sys.argv) < 3:
      log_error(usage_msg)
      exit(1)
    barrier(sys.argv[2])
  else:
    log_error(usage_msg)
    exit(1)
