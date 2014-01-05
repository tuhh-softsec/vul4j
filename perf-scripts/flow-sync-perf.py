#!/usr/bin/python
'''
 Script that tests Flow Synchronizer performance
 Author: Brian O'Connor <bocon@onlab.us>

 Usage: 
   1. Ensure that ONOS is running
   2. sudo ./flow-sync-perf.sh <list of tests>
      e.g. sudo ./flow-sync-perf.sh 1 10 100 1000 
      or to run the default tests:
      sudo ./flow-sync-perf.sh
   3. Results are CSV files in a date stamped directory
'''

import csv
import os
import sys
from time import sleep, strftime
from subprocess import Popen, call, check_output, PIPE
from mininet.net import Mininet
from mininet.topo import SingleSwitchTopo
from mininet.node import RemoteController
from mininet.cli import CLI
from mininet.log import setLogLevel
try:
  import pexpect
except:
  # install pexpect if it cannot be found and re-import
  print '* Installing Pexpect'
  call( 'apt-get install -y python-pexpect', stdout=PIPE, shell=True )
  import pexpect

ONOS_HOME = '..'
ONOS_LOG = '%s/onos-logs/onos.%s.log' % ( ONOS_HOME, check_output( 'hostname').strip() )
print "ONOS Log File:", ONOS_LOG

# Verify that tcpkill is installed
tcpkill_check = Popen( 'which tcpkill', stdout=PIPE, shell=True)
tcpkill_check.communicate()
if tcpkill_check.returncode != 0:
  print '* Installing tcpkill'
  call( 'apt-get install -y dsniff', stdout=PIPE, shell=True )

# ----------------- Tests scenarios -------------------------
def doNothing(n):
  print "Doing nothing with %d flows..." % n

def addFakeFlows(n):
  print "Adding %d random flows to switch..." % n
  for i in range( 1, (n+1) ):
    a = i / (256*256) % 256
    b = i / 256 % 256
    c = i % 256
    ip = '10.%d.%d.%d' % (a,b,c)
    call( 'ovs-ofctl add-flow s1 "ip, nw_src=%s/32, idle_timeout=0, hard_timeout=0, cookie=%d, actions=output:2"' % ( ip, i ), shell=True )

def delFlowsFromSwitch(n):
  print "Removing all %d flows from switch..." % n
  call( 'ovs-ofctl del-flows s1', shell=True )


# ----------------- Utility Functions -------------------------
def wait(time, msg=None):
  if msg:
    print msg,
  for i in range(time):
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(1)
  print ". done"

def startNet(net):
  tail = pexpect.spawn( 'tail -0f %s' % ONOS_LOG )
  sleep(1) 
  net.start()
  print "Waiting for ONOS to detech the switch..."
  index = tail.expect(['Sync time \(ms\)', pexpect.EOF, pexpect.TIMEOUT])
  if index >= 1:
    print '* ONOS not started'
    net.stop()
    exit(1)
  tail.terminate()

def dumpFlows():
  return check_output( 'ovs-ofctl dump-flows s1', shell=True )

def addFlowsToONOS(n):
  print "Adding %d flows to ONOS" % n,
  call( './generate_flows.py 1 %d > /tmp/flows.txt' % n, shell=True )
  #call( '%s/web/add_flow.py -m onos -f /tmp/flows.txt' % ONOS_HOME, shell=True )
  p = Popen( '%s/web/add_flow.py -m onos -f /tmp/flows.txt' % ONOS_HOME, shell=True )
  while p.poll() is None:
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(1)
  print ". done\nWaiting for flow entries to be added to switch",
  while True:
    output = check_output( 'ovs-ofctl dump-flows s1', shell=True )
    lines = len(output.split('\n'))
    if lines >= (n+2):
      break
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(1)
  print ". done\nWaiting for flow entries to be visible in network graph",
  while True:
    output = pexpect.spawn( '%s/web/get_flow.py all' % ONOS_HOME )
    count = 0
    while count < n:
      if output.expect(['FlowEntry', pexpect.EOF], timeout=2000) == 1:
        break
      count += 1 
      print '. done'
      return
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(5)

def removeFlowsFromONOS(checkSwitch=True):
  print "Removing all flows from ONOS",
  #call( '%s/web/delete_flow.py all' % ONOS_HOME, shell=True )
  p = Popen( '%s/web/delete_flow.py all' % ONOS_HOME, shell=True )
  while p.poll() is None:
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(1)
  print ". done"
  if checkSwitch:
    print "Waiting for flow entries to be removed from switch",
    while True:
      output = check_output( 'ovs-ofctl dump-flows s1', shell=True )
      lines = len(output.split('\n'))
      if lines == 2:
        break
      sys.stdout.write('.')
      sys.stdout.flush()
      sleep(1)
    print ". done"
  print "Waiting for flow entries to be removed from network graph",
  while True:
    output = pexpect.spawn( '%s/web/get_flow.py all' % ONOS_HOME )
    if output.expect(['FlowEntry', pexpect.EOF], timeout=2000) == 1:
      break
    sys.stdout.write('.')
    sys.stdout.flush()
    sleep(5)
  print '. done'

# ----------------- Running the test and output  -------------------------
def test(i, fn):
  # Start tailing the onos log
  tail = pexpect.spawn( "tail -0f %s" % ONOS_LOG )
  # disconnect the switch from the controller using tcpkill
  tcp  = Popen( 'exec tcpkill -i lo -9 port 6633 > /dev/null 2>&1', shell=True )
  # wait until the switch has been disconnected
  tail.expect( 'Switch removed' )
  # call the test function
  fn(i) 
  # dump to flows to ensure they have all made it to ovs
  dumpFlows() 
  # end tcpkill process to reconnect the switch to the controller
  tcp.terminate()
  tail.expect('Sync time \(ms\):', timeout=6000)
  tail.expect('([\d.]+,?)+\s')
  print "* Results:", tail.match.group(0)
  tail.terminate()
  wait(3, "Waiting for 3 seconds between tests")
  return tail.match.group(0).strip().split(',')

def initResults(files):
  headers = ['# of FEs', 'Flow IDs from Graph', 'FEs from Switch', 'Compare', 
             'Read FE from graph', 'Extract FE', 'Push', 'Total' ]
  for filename in files.values():
    with open(filename, 'w') as csvfile:
      writer = csv.writer(csvfile)
      writer.writerow(headers)

def outputResults(filename, n, results):
  results.insert(0, n)
  with open(filename, 'a') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(results)

def runPerf( resultDir, tests):
  fileMap = { 'add':    os.path.join(resultDir, 'add.csv'),
              'delete': os.path.join(resultDir, 'delete.csv'),
              'sync':   os.path.join(resultDir, 'sync.csv') }
  initResults(fileMap)
  removeFlowsFromONOS(checkSwitch=False) # clear ONOS before starting
  # start Mininet
  topo = SingleSwitchTopo()
  net = Mininet(topo=topo, controller=RemoteController)
  print "Starting Mininet"
  startNet(net)
  wait(30, "Give ONOS 30 seconds to warm up") # let ONOS "warm-up"
  for i in tests:
    addFlowsToONOS(i)
    outputResults(fileMap['sync'],   i, test(i, doNothing))
    outputResults(fileMap['delete'], i, test(i, delFlowsFromSwitch))
    removeFlowsFromONOS()
    outputResults(fileMap['add'],    i, test(i, addFakeFlows)) # test needs empty DB
  net.stop()

if __name__ == '__main__':
  setLogLevel( 'output' )
  resultDir = strftime( '%Y%m%d-%H%M%S' )
  os.mkdir( resultDir )
  tests = map(int, sys.argv[1:])
  if not tests:
    tests = [1, 10, 100, 1000, 10000]
  runPerf( resultDir, tests )

