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
from datetime import datetime

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
        return True
    except Exception as e:
        print "Curl call failed at %s: %s" % (datetime.now(), e)
        return False
        
if __name__ == "__main__":
    while (True):
        #time_elapsed = timeit.timeit('do_request()', 'from __main__ import do_request, last_call_successful', number=1)
        start_time = time.time()
        result = do_request()
        end_time = time.time()

        if result:
            output_file.write("%s%s" % (end_time - start_time, os.linesep))
        else:
            output_file.write("%s%s" % (0, os.linesep))
            
        output_file.flush()
        time.sleep(1)
