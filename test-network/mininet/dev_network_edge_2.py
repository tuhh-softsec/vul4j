#!/usr/bin/python
import sys

Controllers=[{"ip":"127.0.0.1", "port":6633}]

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

        switch = []
        host = []

        for i in range (NR_NODES):
            name_suffix = '%02d' % NWID + "." + '%02d' % (int(i)+1)
            dpid_suffix = '%02x' % NWID + '%02x' % (int(i)+1)
            dpid = '0000' + '0000' + '0000' + dpid_suffix
            sw = self.addSwitch('sw'+name_suffix, dpid=dpid)
            switch.append(sw)

        for i in range (NR_NODES):
            host.append(self.addHost( 'host%d.%d' % (NWID,int(i)+1) ))

        for i in range (NR_NODES):
            self.addLink(host[i], switch[i])

        for i in range (1, NR_NODES):
            self.addLink(switch[0], switch[i])

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

def startiperf( host ):
    host.cmd( '/usr/bin/iperf', '-s &' )

def startiperfs ( hosts ):
    for h in hosts:
        startiperf( h )

def stopiperf( ):
    quietRun( "pkill -9 iperf" )

def stopsshd( ):
    "Stop *all* sshd processes with a custom banner"
    info( '*** Shutting down stale sshd/Banner processes ',
          quietRun( "pkill -9 -f Banner" ), '\n' )

def sdnnet(opt):
    topo = SDNTopo()
    info( '*** Creating network\n' )
    net = Mininet( topo=topo, controller=MyController, link=TCLink)
    #net = Mininet( topo=topo, link=TCLink, build=False)
    #controllers=[]
    #for c in Controllers:
    #  rc = RemoteController('c%d' % Controllers.index(c), ip=c['ip'],port=c['port'])
    #  print "controller ip %s port %s" % (c['ip'], c['port'])
    #  controllers.append(rc)

    #net.controllers=controllers
    net.build()

    host = []
    for i in range (NR_NODES):
      host.append(net.get( 'host%d.%d' % (NWID,int(i)+1) ))

    net.start()

    sw=net.get('sw%02x.%02x' % (NWID,1))
    print "center sw", sw
    sw.attach('tap%02x_1' % NWID)
    sw.attach('tap%02x_2' % NWID)

    for i in range (NR_NODES):
        host[i].defaultIntf().setIP('192.168.%d.%d/16' % (NWID,(int(i)+1))) 
        host[i].defaultIntf().setMAC('00:00:%02x:%02x:%02x:%02x' % (192,168,NWID,(int(i)+1))) 

    for i in range (NR_NODES):
       for n in range (2,9):
         for h in range (NR_NODES):
           host[i].setARP('192.168.%d.%d' % (n, (int(h)+1)), '00:00:%02x:%02x:%02x:%02x' % (192,168,n,(int(h)+1)))

    stopsshd ()
#    stopiperf ()
    startsshds ( host )
#    startiperfs ( host )

    if opt=="cli":
        CLI(net)
        stopsshd()
        net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    if len(sys.argv) == 3:
        NWID=int(sys.argv[1])
        NR_NODES=int(sys.argv[2])
        sdnnet("nocli")
    else:
      print "%s <NWID> <NR_NODES>" % sys.argv[0]
      sys.exit(1)
