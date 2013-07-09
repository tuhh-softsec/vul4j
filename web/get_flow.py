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

def print_flow_path(parsedResult):
  flowId = parsedResult['flowId']['value']
  installerId = parsedResult['installerId']['value']
  srcSwitch = parsedResult['dataPath']['srcPort']['dpid']['value']
  srcPort = parsedResult['dataPath']['srcPort']['port']['value']
  dstSwitch = parsedResult['dataPath']['dstPort']['dpid']['value']
  dstPort = parsedResult['dataPath']['dstPort']['port']['value']

  print "FlowPath: (flowId = %s installerId = %s src = %s/%s dst = %s/%s)" % (flowId, installerId, srcSwitch, srcPort, dstSwitch, dstPort)
  match = parsedResult['flowEntryMatch'];
  #
  # Print the common conditions
  #
  if match == None:
    print "   Match: %s" % (match)
  else:
    # inPort = match['inPort']
    # matchInPort = match['matchInPort']
    srcMac = match['srcMac']
    matchSrcMac = match['matchSrcMac']
    dstMac = match['dstMac']
    matchDstMac = match['matchDstMac']
    ethernetFrameType = match['ethernetFrameType']
    matchEthernetFrameType = match['matchEthernetFrameType']
    vlanId = match['vlanId']
    matchVlanId = match['matchVlanId']
    vlanPriority = match['vlanPriority']
    matchVlanPriority = match['matchVlanPriority']
    srcIPv4Net = match['srcIPv4Net']
    matchSrcIPv4Net = match['matchSrcIPv4Net']
    dstIPv4Net = match['dstIPv4Net']
    matchDstIPv4Net = match['matchDstIPv4Net']
    ipProto = match['ipProto']
    matchIpProto = match['matchIpProto']
    ipToS = match['ipToS']
    matchIpToS = match['matchIpToS']
    srcTcpUdpPort = match['srcTcpUdpPort']
    matchSrcTcpUdpPort = match['matchSrcTcpUdpPort']
    dstTcpUdpPort = match['dstTcpUdpPort']
    matchDstTcpUdpPort = match['matchDstTcpUdpPort']
    # if matchInPort == True:
    #  print "    inPort: %s" % inPort['value']
    if matchSrcMac == True:
      print "    srcMac: %s" % srcMac['value']
    if matchDstMac == True:
      print "    dstMac: %s" % dstMac['value']
    if matchEthernetFrameType == True:
      print "    ethernetFrameType: %s" % hex(ethernetFrameType)
    if matchVlanId == True:
      print "    vlanId: %s" % vlanId
    if matchVlanPriority == True:
      print "    vlanPriority: %s" % vlanPriority
    if matchSrcIPv4Net == True:
      print "    srcIPv4Net: %s" % srcIPv4Net['value']
    if matchDstIPv4Net == True:
      print "    dstIPv4Net: %s" % dstIPv4Net['value']
    if matchIpProto == True:
      print "    ipProto: %s" % ipProto
    if matchIpToS == True:
      print "    ipToS: %s" % ipToS
    if matchSrcTcpUdpPort == True:
      print "    srcTcpUdpPort: %s" % srcTcpUdpPort
    if matchDstTcpUdpPort == True:
      print "    dstTcpUdpPort: %s" % dstTcpUdpPort

  for f in parsedResult['dataPath']['flowEntries']:
    flowEntryId = f['flowEntryId']
    dpid = f['dpid']['value']
    userState = f['flowEntryUserState']
    switchState = f['flowEntrySwitchState']
    match = f['flowEntryMatch'];
    actions = f['flowEntryActions']
    print "  FlowEntry: (%s, %s, %s, %s)" % (flowEntryId, dpid, userState, switchState)

    #
    # Print the match conditions
    #
    if match == None:
      print "   Match: %s" % (match)
    else:
      inPort = match['inPort']
      matchInPort = match['matchInPort']
      srcMac = match['srcMac']
      matchSrcMac = match['matchSrcMac']
      dstMac = match['dstMac']
      matchDstMac = match['matchDstMac']
      ethernetFrameType = match['ethernetFrameType']
      matchEthernetFrameType = match['matchEthernetFrameType']
      vlanId = match['vlanId']
      matchVlanId = match['matchVlanId']
      vlanPriority = match['vlanPriority']
      matchVlanPriority = match['matchVlanPriority']
      srcIPv4Net = match['srcIPv4Net']
      matchSrcIPv4Net = match['matchSrcIPv4Net']
      dstIPv4Net = match['dstIPv4Net']
      matchDstIPv4Net = match['matchDstIPv4Net']
      ipProto = match['ipProto']
      matchIpProto = match['matchIpProto']
      ipToS = match['ipToS']
      matchIpToS = match['matchIpToS']
      srcTcpUdpPort = match['srcTcpUdpPort']
      matchSrcTcpUdpPort = match['matchSrcTcpUdpPort']
      dstTcpUdpPort = match['dstTcpUdpPort']
      matchDstTcpUdpPort = match['matchDstTcpUdpPort']
      if matchInPort == True:
	print "    inPort: %s" % inPort['value']
      if matchSrcMac == True:
	print "    srcMac: %s" % srcMac['value']
      if matchDstMac == True:
	print "    dstMac: %s" % dstMac['value']
      if matchEthernetFrameType == True:
	print "    ethernetFrameType: %s" % hex(ethernetFrameType)
      if matchVlanId == True:
	print "    vlanId: %s" % vlanId
      if matchVlanPriority == True:
	print "    vlanPriority: %s" % vlanPriority
      if matchSrcIPv4Net == True:
	print "    srcIPv4Net: %s" % srcIPv4Net['value']
      if matchDstIPv4Net == True:
	print "    dstIPv4Net: %s" % dstIPv4Net['value']
      if matchIpProto == True:
	print "    ipProto: %s" % ipProto
      if matchIpToS == True:
	print "    ipToS: %s" % ipToS
      if matchSrcTcpUdpPort == True:
	print "    srcTcpUdpPort: %s" % srcTcpUdpPort
      if matchDstTcpUdpPort == True:
	print "    dstTcpUdpPort: %s" % dstTcpUdpPort

    #
    # Print the actions
    #
    if actions == None:
      print "   Actions: %s" % (actions)
    else:
      for a in actions:
	actionType = a['actionType']
	if actionType == "ACTION_OUTPUT":
	  port = a['actionOutput']['port']['value']
	  maxLen = a['actionOutput']['maxLen']
	  print "    actionType: %s port: %s maxLen: %s" % (actionType, port, maxLen)
	if actionType == "ACTION_SET_VLAN_VID":
	  vlanId = a['actionSetVlanId']['vlanId']
	  print "    actionType: %s vlanId: %s" % (actionType, vlanId)
	if actionType == "ACTION_SET_VLAN_PCP":
	  vlanPriority = a['actionSetVlanPriority']['vlanPriority']
	  print "    actionType: %s vlanPriority: %s" % (actionType, vlanPriority)
	if actionType == "ACTION_STRIP_VLAN":
	  stripVlan = a['actionStripVlan']['stripVlan']
	  print "    actionType: %s stripVlan: %s" % (actionType, stripVlan)
	if actionType == "ACTION_SET_DL_SRC":
	  setEthernetSrcAddr = a['actionSetEthernetSrcAddr']['addr']['value']
	  print "    actionType: %s setEthernetSrcAddr: %s" % (actionType, setEthernetSrcAddr)
	if actionType == "ACTION_SET_DL_DST":
	  setEthernetDstAddr = a['actionSetEthernetDstAddr']['addr']['value']
	  print "    actionType: %s setEthernetDstAddr: %s" % (actionType, setEthernetDstAddr)
	if actionType == "ACTION_SET_NW_SRC":
	  setIPv4SrcAddr = a['actionSetIPv4SrcAddr']['addr']['value']
	  print "    actionType: %s setIPv4SrcAddr: %s" % (actionType, setIPv4SrcAddr)
	if actionType == "ACTION_SET_NW_DST":
	  setIPv4DstAddr = a['actionSetIPv4DstAddr']['addr']['value']
	  print "    actionType: %s setIPv4DstAddr: %s" % (actionType, setIPv4DstAddr)
	if actionType == "ACTION_SET_NW_TOS":
	  setIpToS = a['actionSetIpToS']['ipToS']
	  print "    actionType: %s setIpToS: %s" % (actionType, setIpToS)
	if actionType == "ACTION_SET_TP_SRC":
	  setTcpUdpSrcPort = a['actionSetTcpUdpSrcPort']['port']
	  print "    actionType: %s setTcpUdpSrcPort: %s" % (actionType, setTcpUdpSrcPort)
	if actionType == "ACTION_SET_TP_DST":
	  setTcpUdpDstPort = a['actionSetTcpUdpDstPort']['port']
	  print "    actionType: %s setTcpUdpDstPort: %s" % (actionType, setTcpUdpDstPort)
	if actionType == "ACTION_ENQUEUE":
	  port = a['actionEnqueue']['port']['value']
	  queueId = a['actionEnqueue']['queueId']
	  print "    actionType: %s port: %s queueId: %s" % (actionType, port, queueId)

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

  print_flow_path(parsedResult)


def get_installer_flow_paths(installer_id, v1, p1, v2, p2):
  try:
    command = "curl -s \"http://%s:%s/wm/flow/getall-by-installer-id/%s/%s/%s/%s/%s/json\"" % (ControllerIP, ControllerPort, installer_id, v1, p1, v2, p2)
    debug("get_installer_flow_paths %s" % command)

    result = os.popen(command).read()
    debug("result %s" % result)
    if len(result) == 0:
	print "No Flows found"
	return;

    parsedResult = json.loads(result)
    debug("parsed %s" % parsedResult)
  except:
    log_error("Controller IF has issue")
    exit(1)

  for flowPath in parsedResult:
    print_flow_path(flowPath)


def get_endpoints_flow_paths(v1, p1, v2, p2):
  try:
    command = "curl -s \"http://%s:%s/wm/flow/getall-by-endpoints/%s/%s/%s/%s/json\"" % (ControllerIP, ControllerPort, v1, p1, v2, p2)
    debug("get_endpoints_flow_paths %s" % command)

    result = os.popen(command).read()
    debug("result %s" % result)
    if len(result) == 0:
	print "No Flows found"
	return;

    parsedResult = json.loads(result)
    debug("parsed %s" % parsedResult)
  except:
    log_error("Controller IF has issue")
    exit(1)

  for flowPath in parsedResult:
    print_flow_path(flowPath)


def get_all_flow_paths():
  try:
    command = "curl -s \"http://%s:%s/wm/flow/getall/json\"" % (ControllerIP, ControllerPort)
    debug("get_all_flow_paths %s" % command)

    result = os.popen(command).read()
    debug("result %s" % result)
    if len(result) == 0:
	print "No Flows found"
	return;

    parsedResult = json.loads(result)
    debug("parsed %s" % parsedResult)
  except:
    log_error("Controller IF has issue")
    exit(1)

  for flowPath in parsedResult:
    print_flow_path(flowPath)

if __name__ == "__main__":
  usage_msg1 = "Usage:\n"
  usage_msg2 = "%s <flow_id> : Print flow with Flow ID of <flow_id>\n" % (sys.argv[0])
  usage_msg3 = "                   all    : Print all flows\n"
  usage_msg4 = "                   installer <installer-id> <src-dpid> <src-port> <dest-dpid> <dest-port>\n"
  usage_msg5 = "                   endpoints <src-dpid> <src-port> <dest-dpid> <dest-port>"
  usage_msg = usage_msg1 + usage_msg2 + usage_msg3 + usage_msg4 + usage_msg5;

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
  if sys.argv[1] == "all":
    get_all_flow_paths()
  elif sys.argv[1] == "installer":
    if len(sys.argv) < 7:
      log_error(usage_msg)
      exit(1)
    get_installer_flow_paths(sys.argv[2], sys.argv[3], sys.argv[4],
			     sys.argv[5], sys.argv[6])
  elif sys.argv[1] == "endpoints":
    if len(sys.argv) < 6:
      log_error(usage_msg)
      exit(1)
    get_endpoints_flow_paths(sys.argv[2], sys.argv[3], sys.argv[4],
			     sys.argv[5])
  else:
    get_flow_path(sys.argv[1])
