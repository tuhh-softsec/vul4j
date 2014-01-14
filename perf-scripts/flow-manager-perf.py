#!/usr/bin/python
'''
 Script that tests Flow Manager performance
 Author: Brian O'Connor <bocon@onlab.us>

'''

import csv
import os
import sys
import re
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
ONOS_URL = 'http://127.0.0.1:8080/wm/onos/flows/get/%d/json'
ONOS_LOG = '/tmp/onos-0.logs/onos.onos-vm.log'
print "ONOS Log File:", ONOS_LOG

PORT = 's1-eth2'
N = 5

# ----------------- Running the test and output  -------------------------

class Result(object):
  def __init__(self, tsharkTime, flowmods, onosTime, overhead, details):
    self.tsharkTime = tsharkTime
    self.flowmods = flowmods
    self.onosTime = onosTime
    self.overhead = overhead
    # sorted by start time
    self.details = sorted(details, key=lambda x: float(x[2]) )

  def __repr__(self):
    return '%f %f %f %d %s' % (self.tsharkTime, self.onosTime, self.overhead, self.flowmods, self.details)


def clearResults():
  cmd = 'curl %s' % ONOS_URL % -200
  call( cmd, shell=True )
  pass

def reportResults():
  cmd = 'curl %s' % ONOS_URL % -100
  call( cmd, shell=True )

def test():
  # Start tailing the onos log
  tail = pexpect.spawn( "tail -0f %s" % ONOS_LOG )
  tshark = pexpect.spawn( 'tshark -i lo -R "of.type == 12 || of.type == 14"' )
  tshark.expect('Capturing on lo')
  sleep(1) # wait for tshark to start

  clearResults() # REST call to ONOS
  # Take link down
  call( 'ifconfig %s down' % PORT, shell=True )

  # collect openflow packets using tshark
  count = 0 
  timeout = 6000
  start = -1
  end = -1
  while True:
    i = tshark.expect( ['(\d+\.\d+)', pexpect.TIMEOUT], timeout=timeout )
    if i == 1:
      break
    time = float(tshark.match.group(1))
    if start == -1:
      start = time
    if time > end:
      end = time
    i = tshark.expect( ['Port Status', 'Flow Mod'] )
    if i == 1:
      count += 1
      timeout = 3 #sec
  elapsed = (end - start) * 1000

  # read the performance results from ONOS
  reportResults() # REST call

  # Wait for performance results in the log
  tail.expect('Performance Results: \(avg/start/stop/count\)', timeout=10)
  i = tail.expect('TotalTime:([\d\.]+)/Overhead:([\d\.]+)')
  totalTime = float(tail.match.group(1))
  overhead = float(tail.match.group(2))
  tags = re.findall( r'([\w\.]+)=([\d\.]+)/([\d\.]+)/([\d\.]+)/(\d+)', tail.before )
  result = Result(elapsed, count, totalTime, overhead, tags)
  # Output results
  print result


  # Bring port back up
  call( 'ifconfig %s up' % PORT, shell=True )

  tail.terminate()
  tshark.terminate()
  return []

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
    #outputResults(filename, results)
    sys.stdout.write('$')
    sys.stdout.flush()
    sleep(5) # sleep for 5 seconds between tests
    sys.stdout.write('.')
    sys.stdout.flush()
  totalTime = datetime.now() - start
  print '\nExperiments complete in %s (h:m:s.s)' % totalTime

if __name__ == '__main__':
  n = N 
  runPerf(n)

