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
N = 1

# ----------------- Running the test and output  -------------------------

class Result(object):
  def __init__(self, tsharkTime, flowmods, onosTime, overhead, details):
    self.tsharkTime = tsharkTime
    self.flowmods = flowmods
    self.onosTime = onosTime
    self.overhead = overhead
    # sorted by start time
    self.tags = sorted(details, key=lambda x: float(x[2]))
    self.details = sorted(details, key=lambda x: float(x[2]), reverse=True)

  def __repr__(self):
    return '%f %f %f %d %s' % (self.tsharkTime, self.onosTime, self.overhead, self.flowmods, self.tags)


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

  sleep(2)
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
  plot(result)

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

def plot(result):
  import matplotlib.pyplot as plt
  import pylab
  import numpy as np
  from matplotlib.ticker import MaxNLocator
  
  tags = [ x[0] for x in result.details ]
  numTags = len(tags)
  scores = [ float(x[1]) for x in result.details ]
  offset = [ float(x[2]) for x in result.details ]
  rankings = [ float(x[3]) - float(x[2]) for x in result.details ]
  counts = [ x[4] for x in result.details ]
  
  fig, ax1 = plt.subplots(figsize=(15, 9))
  plt.subplots_adjust(left=0.3, right=0.8)
  pos = np.arange(numTags)+0.5    # Center bars on the Y-axis ticks
  rects = ax1.barh(pos, rankings, left=offset, align='center', height=0.5, color='m')
  
  ax1.axis([0, result.onosTime, 0, numTags])
  pylab.yticks(pos, tags)
  ax1.set_title('TITLE HERE')
  #plt.text(result.onosTime/2, -0.5, 
  #         'Iteration: ' + str(1), horizontalalignment='center', size='small')
  
  # Set the right-hand Y-axis ticks and labels and set X-axis tick marks at the
  # deciles
  ax2 = ax1.twinx()
  print MaxNLocator(7)
  ax2.xaxis.set_major_locator(MaxNLocator(7)) # max number of xaxis ticks
  #ax2.plot([100, 100], [0, 5], 'white', alpha=0.1)
  ax2.xaxis.grid(True, linestyle='--', which='major', color='grey', alpha=0.25)
  #Plot a solid vertical gridline to highlight the median position
  #plt.plot([50, 50], [0, 5], 'grey', alpha=0.25)
  
  # Build up the score labels for the right Y-axis by first appending a carriage
  # return to each string and then tacking on the appropriate meta information
  # (i.e., 'laps' vs 'seconds'). We want the labels centered on the ticks, so if
  # there is no meta info (like for pushups) then don't add the carriage return to
  # the string
  
  '''
  scoreLabels = [withnew(i, scr) for i, scr in enumerate(scores)]
  scoreLabels = [i+j for i, j in zip(scoreLabels, testMeta)]
  '''
  scoreLabels = ['%.3f ms\n%s'%(i,j)  for i,j in zip(scores,counts)]
  # set the tick locations
  ax2.set_yticks(pos)
  # set the tick labels
  ax2.set_yticklabels(scoreLabels)
  # make sure that the limits are set equally on both yaxis so the ticks line up
  ax2.set_ylim(ax1.get_ylim())
  
  ax1.set_xlabel('Time (ms)') 
  ax2.set_ylabel('Average iteration / Count')
  
  # Lastly, write in the ranking inside each bar to aid in interpretation
  for rect in rects:
      # Rectangle widths are already integer-valued but are floating
      # type, so it helps to remove the trailing decimal point and 0 by
      # converting width to int type
      width = int(rect.get_width())
      offset = int(rect.get_x())
      percent = width / result.onosTime
      onePercent = 0.01 * result.onosTime
  
      rankStr = str(width) + 'ms' 
      if (percent < 0.09):        # The bars aren't wide enough to print the ranking inside
          xloc = offset + width + onePercent   # Shift the text to the right side of the right edge
          clr = 'black'      # Black against white background
          align = 'left'
      else:
          xloc = offset + 0.98*width  # Shift the text to the left side of the right edge
          clr = 'white'      # White on magenta
          align = 'right'
  
      # Center the text vertically in the bar
      yloc = rect.get_y()+rect.get_height()/2.0
      ax1.text(xloc, yloc, rankStr, horizontalalignment=align,
              verticalalignment='center', color=clr)
  
  plt.show()
  plt.savefig('test.png')
if __name__ == '__main__':
  n = N 
  runPerf(n)

