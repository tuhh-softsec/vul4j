#!/usr/bin/python

NWID="a"

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
    def __init__( self, name, ip='10.0.1.217', port=6633, **kwargs):
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
        sw1 = self.addSwitch('sw%s1' % NWID, dpid='00000000000000%s1' % NWID)
        sw2 = self.addSwitch('sw%s2' % NWID, dpid='00000000000000%s2' % NWID)
        sw3 = self.addSwitch('sw%s3' % NWID, dpid='00000000000000%s3' % NWID)
        sw4 = self.addSwitch('sw%s4' % NWID, dpid='00000000000000%s4' % NWID)
        sw5 = self.addSwitch('sw%s5' % NWID, dpid='00000000000000%s5' % NWID)

        host1 = self.addHost( 'host1' )
        host2 = self.addHost( 'host2' )
        host3 = self.addHost( 'host3' )
        host4 = self.addHost( 'host4' )
        host5 = self.addHost( 'host5' )

        root1 = self.addHost( 'root1', inNamespace=False )
        root2 = self.addHost( 'root2', inNamespace=False )
        root3 = self.addHost( 'root3', inNamespace=False )
        root4 = self.addHost( 'root4', inNamespace=False )
        root5 = self.addHost( 'root5', inNamespace=False )


        self.addLink( host1, sw1 )
        self.addLink( host2, sw2 )
        self.addLink( host3, sw3 )
        self.addLink( host4, sw4 )
        self.addLink( host5, sw5 )


        self.addLink( sw1, sw3 )
        self.addLink( sw2, sw3 )
        self.addLink( sw4, sw3 )
        self.addLink( sw5, sw3 )

        self.addLink( root1, host1 )
        self.addLink( root2, host2 )
        self.addLink( root3, host3 )
        self.addLink( root4, host4 )
        self.addLink( root5, host5 )

#        self.addLink( sw1, sw2 )

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

    host1, host2, host3, host4, host5  = net.get( 'host1', 'host2', 'host3', 'host4', 'host5')

    ## Adding 2nd, 3rd and 4th interface to host1 connected to sw1 (for another BGP peering)
    sw1 = net.get('sw%s1' % NWID)
    sw2 = net.get('sw%s2' % NWID)
    sw3 = net.get('sw%s3' % NWID)
    sw4 = net.get('sw%s4' % NWID)
    sw5 = net.get('sw%s5' % NWID)

    net.start()

    sw5.attach('tap%s0' % NWID)

    host1.defaultIntf().setIP('192.168.10.111/24') 
    host2.defaultIntf().setIP('192.168.10.112/24')
    host3.defaultIntf().setIP('192.168.10.113/24')
    host4.defaultIntf().setIP('192.168.10.114/24')
    host5.defaultIntf().setIP('192.168.10.115/24')

    root1, root2, root3, root4, root5  = net.get( 'root1', 'root2', 'root3', 'root4', 'root5' )
    host1.intf('host1-eth1').setIP('1.1.1.1/24')
    root1.intf('root1-eth0').setIP('1.1.1.2/24')

    host2.intf('host2-eth1') .setIP('1.1.2.1/24')
    root2.intf('root2-eth0').setIP('1.1.2.2/24')

    host3.intf('host3-eth1') .setIP('1.1.3.1/24')
    root3.intf('root3-eth0').setIP('1.1.3.2/24')

    host4.intf('host4-eth1') .setIP('1.1.4.1/24')
    root4.intf('root4-eth0').setIP('1.1.4.2/24')

    host5.intf('host5-eth1') .setIP('1.1.5.1/24')
    root5.intf('root5-eth0').setIP('1.1.5.2/24')

    hosts = [ host1, host2, host3, host4, host5 ]
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
