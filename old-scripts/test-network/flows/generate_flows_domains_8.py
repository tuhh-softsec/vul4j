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
DOMAINS_N = 7
NODES_N = 25

## Worker Functions ##
def log_error(txt):
  print '%s' % (txt)

def debug(txt):
  if DEBUG:
    print '%s' % (txt)


if __name__ == "__main__":
  usage_msg = "Generate a number of flows by using a pre-defined template.\n"
  usage_msg = usage_msg + "\n"
  usage_msg = usage_msg + "NOTE: This script is work-in-progress. Currently all flows are within same\n"
  usage_msg = usage_msg + "pair of switch ports and contain auto-generated MAC-based matching conditions.\n"
  usage_msg = usage_msg + "\n"
  usage_msg = usage_msg + "Usage: %s <begin-flow-id>\n" % (sys.argv[0])
  usage_msg = usage_msg + "\n"
  usage_msg = usage_msg + "    The output should be saved to a file, and the flows should be installed\n"
  usage_msg = usage_msg + "    by using the command './add_flow.py -f filename'\n"


  # app.debug = False

  # Usage info
  if len(sys.argv) > 1 and (sys.argv[1] == "-h" or sys.argv[1] == "--help"):
    print(usage_msg)
    exit(0)

  # Check arguments
  if len(sys.argv) < 2:
    log_error(usage_msg)
    exit(1)

  # Extract the arguments
  begin_flow_id = int(sys.argv[1], 0)

  #
  # Do the work
  #
  # NOTE: Currently, up to 65536 flows are supported.
  # More flows can be supported by iterating by, say, iterating over some of
  # the other bytes of the autogenereated source/destination MAC addresses.
  # 
  # We iterate over each pair of domains. E.g.:
  #  (2,3) (2,4) (2,5) ... (2,8) (3,2) (3,4) ... (3,8) (4,2) ... (8,7)
  # Within each domain we iterate over the corresponding pairs of node IDs:
  #  (2,2) (3,3) (4,4) (5,5) ... (25, 25)
  #
  flow_id = begin_flow_id
  idx = 0
  src_domain_id = 2
  while src_domain_id < DOMAINS_N + 2:
    dst_domain_id = 2
    while dst_domain_id < DOMAINS_N + 2:
      if src_domain_id == dst_domain_id:
        dst_domain_id = dst_domain_id + 1
        continue
      node_id = 2
      while node_id < NODES_N:
        # The matching MAC addresses
        mac2 = idx / 255
        mac3 = idx % 255
        str_mac2 = "%0.2x" % mac2
        str_mac3 = "%0.2x" % mac3
        src_mac = "00:01:" + str_mac2 + ":" + str_mac3 + ":00:00"
        dst_mac = "00:02:" + str_mac2 + ":" + str_mac3 + ":00:00"
        # The src/dst node DPID
        src_node_dpid = "%0.2x" % node_id
        dst_node_dpid = "%0.2x" % node_id
        src_domain_dpid = "%0.2x" % src_domain_id
        dst_domain_dpid = "%0.2x" % dst_domain_id
        src_dpid = "00:00:00:00:00:00:" + src_domain_dpid + ":" + src_node_dpid
        dst_dpid = "00:00:00:00:00:00:" + dst_domain_dpid + ":" + dst_node_dpid
        # The flow from source to destination
        print "%s FOOBAR %s 1 %s 1 matchSrcMac %s matchDstMac %s" % (flow_id, src_dpid, dst_dpid, src_mac, dst_mac)
        flow_id = flow_id + 1
        idx = idx + 1
        node_id = node_id + 1
      dst_domain_id = dst_domain_id + 1
    src_domain_id = src_domain_id + 1
