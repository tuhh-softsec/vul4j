#!/usr/bin/env python

"""
Small script to run a stress test on a Cassandra cluster.
The script will periodically query data from Cassandra via ONOS
and time the request duration.
"""

import os
import sys
import json
import time
import timeit

host = "localhost"
port = "9000"

url="http://%s:%s/wm/core/topology/switches/all/json" % (host, port)
command = "curl -m 5 -s %s" % url

if len(sys.argv) < 2:
    print "usage: %s output_file" % sys.argv[0]
    sys.exit(1)

output_filename = sys.argv[1]
output_file = open(output_filename, 'w')
output_file.write("Time" + os.linesep)

def do_request():
    try:
        result = os.popen(command).read()
        parsedResult = json.loads(result)
    except Exception as e:
        print "Curl call failed: %s" % e
        sys.exit(1)
        

if __name__ == "__main__":
    while (True):
        time_elapsed = timeit.timeit('do_request()', 'from __main__ import do_request', number=1)
        output_file.write("%s%s" % (time_elapsed, os.linesep))
        output_file.flush()
        time.sleep(1)
