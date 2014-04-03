#!/usr/bin/python

"""
Start up a Simple topology
"""
from mininet.net import Mininet
from mininet.node import Controller, RemoteController
from mininet.log import setLogLevel, info, error, warn, debug
from mininet.cli import CLI
from mininet.topo import Topo
from mininet.util import quietRun
from mininet.moduledeps import pathCheck
from mininet.link import Link, TCLink

from sys import exit
import os.path
from subprocess import Popen, STDOUT, PIPE

import sys


#import argparse

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

class SDNTopo( Topo ):
    "SDN Topology"

    def __init__( self, *args, **kwargs ):
        Topo.__init__( self, *args, **kwargs )
        sw1 = self.addSwitch('sw1', dpid='0000000000000101')
        sw2 = self.addSwitch('sw2', dpid='0000000000000102')
        sw3 = self.addSwitch('sw3', dpid='0000000000000103')
        sw4 = self.addSwitch('sw4', dpid='0000000000000104')
        sw5 = self.addSwitch('sw5', dpid='0000000000000105')
        sw6 = self.addSwitch('sw6', dpid='0000000000000106')

        host1 = self.addHost( 'host1' )
        host2 = self.addHost( 'host2' )
        host3 = self.addHost( 'host3' )
        host4 = self.addHost( 'host4' )
        host5 = self.addHost( 'host5' )
        host6 = self.addHost( 'host6' )

        self.addLink( host1, sw1 )
        self.addLink( host2, sw2 )
        self.addLink( host3, sw3 )
        self.addLink( host4, sw4 )
        self.addLink( host5, sw5 )
        self.addLink( host6, sw6 )

        self.addLink( sw1, sw2 )
        self.addLink( sw1, sw6 )
        self.addLink( sw2, sw3 )
        self.addLink( sw3, sw4 )
        self.addLink( sw3, sw6 )
        self.addLink( sw4, sw5 )
        self.addLink( sw5, sw6 )
        self.addLink( sw4, sw6 )

def startsshd( host ):
    "Start sshd on host"
    info( '*** Starting sshd\n' )
    name, intf, ip = host.name, host.defaultIntf(), host.IP()
    banner = '/tmp/%s.banner' % name
    host.cmd( 'echo "Welcome to %s at %s" >  %s' % ( name, ip, banner ) )
    host.cmd( '/usr/sbin/sshd -o "Banner %s"' % banner, '-o "UseDNS no"' )
    info( '***', host.name, 'is running sshd on', intf, 'at', ip, '\n' )

def startsshds ( hosts ):
    for h in hosts:
        startsshd( h )

def stopsshd( ):
    "Stop *all* sshd processes with a custom banner"
    info( '*** Shutting down stale sshd/Banner processes ',
          quietRun( "pkill -9 -f Banner" ), '\n' )

def sdnnet(opt):
#    os.system('/home/ubuntu/openflow/controller/controller ptcp: &')
#    os.system('/home/ubuntu/openflow/controller/controller ptcp:7000 &')

    topo = SDNTopo()
    info( '*** Creating network\n' )
#    net = Mininet( topo=topo, controller=RemoteController )
    net = Mininet( topo=topo, controller=MyController, link=TCLink)
#    dc = DebugController('c3', ip='127.0.0.1', port=7000)
#    net.addController(dc)
#    net.addController(controller=RemoteController)

    host1, host2, host3, host4, host5, host6 = net.get( 'host1', 'host2', 'host3', 'host4', 'host5', 'host6')

    ## Adding 2nd, 3rd and 4th interface to host1 connected to sw1 (for another BGP peering)
    sw1 = net.get('sw1')
    sw2 = net.get('sw2')
    sw3 = net.get('sw3')
    sw4 = net.get('sw4')
    sw5 = net.get('sw5')
    sw6 = net.get('sw6')

    net.start()

    sw2.attach('tap01_2')
    sw3.attach('tap01_3')
    sw4.attach('tap01_4')
    sw4.attach('tap01_5')
    sw5.attach('tap01_6')
    sw6.attach('tap01_7')
    sw1.attach('tap01_8')

    host1.defaultIntf().setIP('192.168.100.141/16') 
    host2.defaultIntf().setIP('192.168.100.142/16')
    host3.defaultIntf().setIP('192.168.100.143/16')
    host4.defaultIntf().setIP('192.168.100.144/16')
    host5.defaultIntf().setIP('192.168.100.145/16')
    host6.defaultIntf().setIP('192.168.100.146/16')

    hosts = [ host1, host2, host3, host4, host5, host6 ]
    stopsshd ()
    startsshds ( hosts )

    if opt=="cli":
        CLI(net)
        stopsshd()
        net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    if len(sys.argv) == 1:
      sdnnet("cli")
    elif len(sys.argv) == 2 and sys.argv[1] == "-n":
      sdnnet("nocli")
    else:
      print "%s [-n]" % sys.argv[0]
