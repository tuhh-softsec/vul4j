#! /usr/bin/env python
import json
import os
from check_status import *

cluster_basename=os.environ.get("ONOS_CLUSTER_BASENAME")
nr_nodes=os.environ.get("ONOS_CLUSTER_NR_NODES")

if __name__ == "__main__":
  print "%s" % check_switch()[1]
  print "%s" % check_link()[1]
  print "%s" % check_controllers(8)[1]
  print "%s" % check_switch_all(8)[1]
