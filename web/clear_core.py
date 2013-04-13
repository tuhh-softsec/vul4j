#! /usr/bin/env python

import os
import json

CONFIG_FILE=os.getenv("HOME") + "/ONOS/web/config.json"

def read_config():
  global LB, TESTBED, controllers, core_switches, ONOS_GUI3_HOST, ONOS_GUI3_CONTROL_HOST
  f = open(CONFIG_FILE)
  conf = json.load(f)
  LB = conf['LB']
  TESTBED = conf['TESTBED']
  controllers = conf['controllers']
  core_switches=conf['core_switches']
  ONOS_GUI3_HOST=conf['ONOS_GUI3_HOST']
  ONOS_GUI3_CONTROL_HOST=conf['ONOS_GUI3_CONTROL_HOST']
  f.close()

if __name__ == "__main__":
  onos_rest_port = 8080
  read_config()

  try:
    sw_list = json.dumps(core_switches)
    command = "curl -s -H 'Content-Type: application/json' -d '%s' http://%s:%s/wm/core/clearflowtable/json" % (sw_list, controllers[0], onos_rest_port)

    print command
    result = os.popen(command).read()
    print result
  except:
    print "REST IF has issue"
    exit
