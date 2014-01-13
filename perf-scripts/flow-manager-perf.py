#!/usr/bin/python
'''
 Script that tests Flow Manager performance
 Author: Brian O'Connor <bocon@onlab.us>

'''

import csv
import os
import sys
from time import sleep, strftime
from subprocess import Popen, call, check_output, PIPE
from datetime import datetime

try:
  import pexpect
except:
  # install pexpect if it cannot be found and re-import
  print '* Installing Pexpect'
  call( 'apt-get install -y python-pexpect', stdout=PIPE, shell=True )
  import pexpect

ONOS_HOME = '..'
ONOS_LOG = '%s/onos-logs/onos.%s.log' % ( ONOS_HOME, check_output( 'hostname').strip() )
ONOS_LOG = '/tmp/onos-1.logs/onos.onos-vm.log'
print "ONOS Log File:", ONOS_LOG

PORT = 's1-eth2'
N = 100

# ----------------- Running the test and output  -------------------------

# 17:43:37.206 [main] ERROR n.o.o.o.f.PerformanceMonitor - Performance Results: {a=0.001ms, b=0.0ms, c=0.0ms} with measurement overhead: 0.022 ms

def test():
  # Start tailing the onos log
  tail = pexpect.spawn( "tail -0f %s" % ONOS_LOG )

  # Take link down
  call( 'ifconfig %s down' % PORT, shell=True )

  # Wait for performance results in the log
  tail.expect('Performance Results: \{([^\}]*)\} with measurement overhead: ([\d.]+) ms', timeout=6000)
  s = tail.match.group(1)
  overhead = float( tail.match.group(2) )
  results = dict( re.findall(r'(\w[\w\s]*)=([\d.]+)', s) )

  # Output results
  print "* Results:", results, "Overhead:", overhead
 
  # Bring port back up
  call( 'ifconfig %s up' % PORT, shell=True )

  tail.terminate()
  return results

def outputResults(filename, results):
  with open(filename, 'a') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(results)

def runPerf(n):
  filename = 'results-flowmanager-%s.csv' % strftime( '%Y%m%d-%H%M%S' )
  print 'Starting experiments:'
  start = datetime.now()
  for i in range(n):
    results = test()
    outputResults(filename, results)
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(5000) # sleep for 5 seconds between tests
  totalTime = datetime.now() - start
  print '\nExperiments complete in %s (h:m:s.s)' % totalTime

if __name__ == '__main__':
  n = N 
  runPerf(n)

