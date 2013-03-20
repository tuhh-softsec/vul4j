#! /usr/bin/env python
# -*- Mode: python; py-indent-offset: 4; tab-width: 8; indent-tabs-mode: t; -*-

#
# A script for generating a number of flows.
#
# The output of the script should be saved to a file, and the flows from
# that file should be added by the following command:
#
#   web/add_flow.py -f filename
# 
# NOTE: Currently, some of the parameters fo the flows are hard-coded,
# and all flows are between same source and destination DPID and ports
# (differentiated by different matchSrcMac and matchDstMac).
#

import copy
import pprint
import os
import sys
import subprocess
import json
import argparse
import io
import time

## Global Var ##

DEBUG=0
pp = pprint.PrettyPrinter(indent=4)

## Worker Functions ##
def log_error(txt):
  print '%s' % (txt)

def debug(txt):
  if DEBUG:
    print '%s' % (txt)


if __name__ == "__main__":
  usage_msg = "Usage: %s <begin-flow-id> <end-flow-id>\n" % (sys.argv[0])

  # app.debug = False;

  # Usage info
  if len(sys.argv) > 1 and (sys.argv[1] == "-h" or sys.argv[1] == "--help"):
    print(usage_msg)
    exit(0)

  # Check arguments
  if len(sys.argv) < 3:
    log_error(usage_msg)
    exit(1)

  # Extract the arguments
  begin_flow_id = int(sys.argv[1], 0)
  end_flow_id = int(sys.argv[2], 0)
  if begin_flow_id > end_flow_id:
    log_error(usage_msg)
    exit(1)

  #
  # Do the work
  #
  # NOTE: Currently, up to 65536 flows are supported.
  # More flows can be supported by iterating by, say, iterating over some of
  # the other bytes of the autogenereated source/destination MAC addresses.
  #
  flow_id = begin_flow_id
  idx = 0
  while flow_id <= end_flow_id:
    mac3 = idx / 255
    mac4 = idx % 255
    str_mac3 = "%0.2x" % mac3
    str_mac4 = "%0.2x" % mac4
    src_mac = "00:00:" + str_mac3 + ":" + str_mac4 + ":00:00";
    dst_mac = "00:01:" + str_mac3 + ":" + str_mac4 + ":00:00";
    print "%s FOOBAR 00:00:00:00:00:00:01:01 1 00:00:00:00:00:00:01:0b 1 matchSrcMac %s matchDstMac %s" % (flow_id, src_mac, dst_mac)
    flow_id = flow_id + 1
    idx = idx + 1
