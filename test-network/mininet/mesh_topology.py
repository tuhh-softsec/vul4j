#!/usr/bin/python
"""Custom topology example

Two directly connected switches plus a host for each switch:

   host --- switch --- switch --- host

Adding the 'topos' dict with a key/value pair to generate our newly defined
topology enables one to pass in '--topo=mytopo' from the command line.
"""

import sys, getopt
from mininet.net import Mininet
from mininet.node import Controller, RemoteController
from mininet.log import setLogLevel, info, error, warn, debug
from mininet.topo import Topo
from mininet.link import Link, TCLink
from mininet.util import dumpNodeConnections

class MyController( Controller ):
    def __init__( self, name, ip='127.0.0.1', port=6633, **kwargs):
        """Init.
           name: name to give controller
           ip: the IP address where the remote controller is
           listening
           port: the port where the remote controller is listening"""
        Controller.__init__( self, name, ip=ip, port=port, **kwargs )

    def start( self ):
        "Overridden to do nothing."
        return

    def stop( self ):
        "Overridden to do nothing."
        return

    def checkListening( self ):
        "Warn if remote controller is not accessible"
        listening = self.cmd( "echo A | telnet -e A %s %d" %
                              ( self.ip, self.port ) )
        if 'Unable' in listening:
            warn( "Unable to contact the remote controller"
                  " at %s:%d\n" % ( self.ip, self.port ) )


class MyTopo( Topo ):
    "Simple topology example."

    def __init__( self, max_switches ):
        "Create custom topo."

        # Initialize topology
        Topo.__init__( self )

        installed_switches = {}
        # add switches first
        for sw in range( max_switches ):
          sw_str = "sw" + `sw + 1`
          msw = self.addSwitch( sw_str )
          installed_switches[sw_str] = msw
        
        # create links between switches
        for sw in range( max_switches ):
          next_sw = sw + 1
          for link in range( next_sw, max_switches ):
            link_from = "sw" + `next_sw`
            link_to = "sw" + `link + 1`
            print "link_from ", link_from, " link to ", link_to
            self.addLink( link_from, link_to )

        # finally add links to hosts
        for sw in range( max_switches ):
          sw_str = "sw" + `sw + 1`
          host_str = "h" + `sw + 1`
          mhost = self.addHost( host_str )
          msw = installed_switches[sw_str]
          self.addLink( msw, mhost)

def main(argv):
  max_switches = ""
  try:
    opts,args = getopt.getopt(argv, "hs:", ["help", "switches="])
  except getopt.GetoptError:
    print "Usage mesh_topology.py [options]"
    print "-s, --switches number of switches to set"
    sys.exit(2)
  for opt, arg in opts:
    if opt == '-h':
      print "Usage mesh_topology.py [options]"
      print "-s, --switches number of switches to set"
      sys.exit()
    elif opt in ("-s", "--switches"):
      max_switches = arg

  switches = 4
  if max_switches != "":
    switches = int(max_switches)
  topos = { 'mytopo': ( lambda: MyTopo(switches) ) }
  net = Mininet(topo=MyTopo(switches), controller=MyController, link=TCLink)
  print dumpNodeConnections(net.switches)

  for sw in range(switches):
    next_sw = sw + 1
    mhost = "h" + `next_sw`
    host = net.get( mhost )
    if next_sw > 255:
      divisor = next_sw / 256
      remainder = next_sw % 256
      host.setMAC('00:00:%02x:%02x:%02x:%02x' % (192, 168, divisor, remainder)) 
      print "Host", host.name, "has IP address", host.IP(), "and MAC address", host.MAC()
    else:
      host.setMAC('00:00:%02x:%02x:%02x:%02x' % (192, 168, 0, next_sw)) 
      print "Host", host.name, "has IP address", host.IP(), "and MAC address", host.MAC()
    
  net.start()

if __name__ == "__main__":
  main(sys.argv[1:])
