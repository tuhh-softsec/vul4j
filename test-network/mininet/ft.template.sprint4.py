#!/usr/bin/python

NWID="a"
Controllers=[{"ip":'127.0.0.1', "port":6633}, {"ip":'10.0.1.28', "port":6633}]
#Controllers=[{"ip":'127.0.0.1', "port":6633}]

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

        dist_sw = []
        aggr_sw = []
        core_sw = []
        host = []
        root = []

        for i in range (4):
            name_suffix = '%s' % NWID + '0d' + '%02d' % i
            dpid = '0000' + '0000' + '000' + name_suffix
            sw = self.addSwitch('sw'+name_suffix, dpid=dpid)
            dist_sw.append(sw)

        for i in range (4):
            name_suffix = '%s' % NWID + '0a' + '%02d' % i
            dpid = '0000' + '0000' + '000' + name_suffix
            sw = self.addSwitch('sw'+name_suffix, dpid=dpid)
            aggr_sw.append(sw)

        for i in range (1):
            name_suffix = '%s' % NWID + '0c' + '%02d' % i
            dpid = '0000' + '0000' + '000' + name_suffix
            sw = self.addSwitch('sw' + name_suffix, dpid=dpid)
            core_sw.append(sw)

        for i in range (8):
            host.append(self.addHost( 'host%d' % i ))

        for i in range (8):
            root.append(self.addHost( 'root%d' % i, inNamespace=False ))

        for i in range (8):
            self.addLink(host[i], dist_sw[int(i/2)])

        for i in range (4):
            for j in range (int(i/2) * 2, (int(i/2) * 2 + 2)):
                print 'add link btween dist_sw[%d] -> aggr_sw[%d]' % (i, j)
                self.addLink(dist_sw[i], aggr_sw[j])

        for i in range (4):
            for j in range (1):
                print 'add link btween aggr_sw[%d] -> core_sw[%d]' % (i, j)
                self.addLink(aggr_sw[i], core_sw[j])

        for i in range (8):
            self.addLink(root[i], host[i])

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
    topo = SDNTopo()
    info( '*** Creating network\n' )
    #net = Mininet( topo=topo, controller=MyController, link=TCLink)
    net = Mininet( topo=topo, link=TCLink, build=False)
    controllers=[]
    for c in Controllers:
      rc = RemoteController('c%d' % Controllers.index(c), ip=c['ip'],port=c['port'])
      print "controller ip %s port %s" % (c['ip'], c['port'])
      controllers.append(rc)
  
    net.controllers=controllers
    net.build()

    host = []
    for i in range (8):
      host.append(net.get( 'host%d' % i ))

    net.start()

    core_sw = []
    for i in range (1):
        name_suffix = '%s' % NWID + '0c' + '%02d' % i
        net.get('sw' + name_suffix).attach('tap%s0' % NWID)

    for i in range (8):
        host[i].defaultIntf().setIP('192.168.10.10%d/24' % i) 

    root = []
    for i in range (8):
        root.append(net.get( 'root%d' % i ))

    for i in range (8):
        host[i].intf('host%d-eth1' % i).setIP('1.1.%d.1/24' % i)
        root[i].intf('root%d-eth0' % i).setIP('1.1.%d.2/24' % i)

    stopsshd ()
    startsshds ( host )

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
