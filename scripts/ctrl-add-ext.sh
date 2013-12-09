#! /usr/bin/env python
import sys
import time
import os
import re
import json
import socket

CONFIG_FILE=os.getenv("HOME") + "/ONOS/web/config.json"

def read_config():
  global controllers
  f = open(CONFIG_FILE)
  conf = json.load(f)
  controllers = conf['controllers']
  f.close()

if __name__ == "__main__":
  read_config()
  controllers.pop(0) 
  url = ""
  for c in controllers:
    url += " " + "tcp:%s:6633" % socket.gethostbyname(c)

  switches = os.popen("sudo ovs-vsctl list-br").read().split("\n");
  switches.remove('')
  for s in switches:
    print "set switch %s controller %s" % (s, url)  
    os.popen("sudo ovs-vsctl set-controller %s %s" % (s, url) )
