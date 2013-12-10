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

# Verify that tcpkill is installed
if not Popen( 'which tcpkill', stdout=PIPE, shell=True).communicate():
  print '* Installing tcpkill'
  call( 'apt-get install -y dsniff', stdout=PIPE, shell=True )

# ----------------- Tests scenarios -------------------------
def doNothing(n):
  print "Doing nothing with %d flows..." % n

def addFakeFlows(n):
  print "Adding %d random flows..." % n
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
def disconnect():
  tail = Popen( "exec tail -0f ../onos-logs/onos.onosdev1.log", stdout=PIPE, shell=True )
  tcp  = Popen( 'exec tcpkill -i lo -9 port 6633 > /dev/null 2>&1', shell=True )
  tcp  = Popen( 'exec tcpkill -i lo -9 port 6633 > /tmp/tcp 2>&1', shell=True )
  sleep(1)
  tcp.kill()
  results = waitForResult(tail)
  tail.kill()
  return results

def startNet(net):
  tail = pexpect.spawn( 'tail -0f %s/onos-logs/onos.onosdev1.log' % ONOS_HOME )
  net.start()
  index = tail.expect(['Sync time \(ms\)', pexpect.EOF, pexpect.TIMEOUT])
  if index >= 1:
    print '* ONOS not started'
    net.stop()
    exit(1)
  tail.terminate()

def dumpFlows():
  return check_output( 'ovs-ofctl dump-flows s1', shell=True )

def addFlowsToONOS(n):
  call( './generate_flows.py 1 %d > /tmp/flows.txt' % n, shell=True )
  call( '%s/web/add_flow.py -m onos -f /tmp/flows.txt' % ONOS_HOME, shell=True )
  while True:
    output = check_output( 'ovs-ofctl dump-flows s1', shell=True )
    lines = len(output.split('\n'))
    if lines >= (n+2):
      break
    sleep(1)
  count = 0
  while True:
    output = pexpect.spawn( '%s/web/get_flow.py all' % ONOS_HOME )
    while count < n:
      if output.expect(['FlowEntry', pexpect.EOF], timeout=2000) == 1:
        break
      count += 1 
      return
    sleep(5)

def removeFlowsFromONOS():
  call( '%s/web/delete_flow.py all' % ONOS_HOME, shell=True )
  while True:
    output = check_output( 'ovs-ofctl dump-flows s1', shell=True )
    lines = len(output.split('\n'))
    if lines == 2:
      break
    sleep(1)
  while True:
    output = pexpect.spawn( '%s/web/get_flow.py all' % ONOS_HOME )
    if output.expect(['FlowEntry', pexpect.EOF], timeout=2000) == 1:
      break
    sleep(5)


# ----------------- Running the test and output  -------------------------
def test(i, fn):
  # Start tailing the onos log
  tail = pexpect.spawn( "tail -0f %s/onos-logs/onos.onosdev1.log" % ONOS_HOME )
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
  print tail.match.group(0)
  tail.terminate()
  sleep(3)
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
  print results
  with open(filename, 'a') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(results)

def runPerf( resultDir, tests):
  fileMap = { 'add':    os.path.join(resultDir, 'add.csv'),
              'delete': os.path.join(resultDir, 'delete.csv'),
              'sync':   os.path.join(resultDir, 'sync.csv') }
  initResults(fileMap)
  # start Mininet
  topo = SingleSwitchTopo()
  net = Mininet(topo=topo, controller=RemoteController)
  startNet(net)
  removeFlowsFromONOS() # clear ONOS before starting
  sleep(30) # let ONOS "warm-up"
  for i in tests:
    addFlowsToONOS(i)
    outputResults(fileMap['sync'],   i, test(i, doNothing))
    outputResults(fileMap['delete'], i, test(i, delFlowsFromSwitch))
    removeFlowsFromONOS()
    outputResults(fileMap['add'],    i, test(i, addFakeFlows)) # test needs empty DB
  net.stop()

def waitForResult(tail):
  while True:
    line = tail.stdout.readline()
    index = line.find('n.o.o.o.f.FlowSynchronizer')
    if index > 0:
      print line,
    index = line.find('Sync time (ms):')
    if index > 0:
      line = line[index + 15:].strip()
      line = line.replace('-->', '')
      return line.split() # graph, switch, compare, total

if __name__ == '__main__':
  setLogLevel( 'output' )
  resultDir = strftime( '%Y%m%d-%H%M%S' )
  os.mkdir( resultDir )
  tests = sys.argv[1:]
  if not tests:
    tests = [1, 10, 100, 1000, 10000]
  runPerf( resultDir, tests )

exit()

# ---------------------------
