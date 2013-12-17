#!/usr/bin/env python

"""
onos.py: A simple ONOS Controller() subclass for Mininet

We implement the following classes:

ONOSController: a custom Controller() subclass to start ONOS
OVSSwitchONOS: a custom OVSSwitch() switch that connects to multiple controllers.

We use single Zookeeper and Cassandra instances for now.

As a custom file, exports:

--controller onos
--switch ovso

Usage:

$ sudo ./onos.py

This will start up a simple 2-host, 2 ONOS network

$ sudo mn --custom onos.py --controller onos,2 --switch ovso
"""

from mininet.node import Controller, OVSSwitch
from mininet.net import Mininet
from mininet.cli import CLI
from mininet.topo import SingleSwitchTopo
from mininet.log import setLogLevel, info
from mininet.util import quietRun

from shutil import copyfile
from os import environ
from functools import partial


class ONOS( Controller ):
    "Custom controller class for ONOS"

    # Directories and configuration templates
    home = environ[ 'HOME' ]
    onosDir = home + "/ONOS"
    zookeeperDir = home + "/zookeeper-3.4.5"
    dirBase = '/tmp'
    logDir = dirBase + '/onos-%s.logs'
    cassDir = dirBase + '/onos-%s.cassandra'
    configFile = dirBase + '/onos-%s.properties'
    logbackFile = dirBase + '/onos-%s.logback'
    jmxbase = 7189
    restbase = 8080
    ofbase = 6633

    # Per-instance property template
    fc = 'net.floodlightcontroller.'
    proctag = 'mn-onos-id'
    jvmopts = (
        # We match on this to shut down our instances
        ( proctag, 0 ),
        ( fc + 'restserver.RestApiServer.port', restbase ),
        ( fc + 'core.FloodlightProvider.openflowport', ofbase ),
        ( fc + 'core.FloodlightProvider.controllerid', 0 ) )


    # For maven debugging
    # mvn = 'mvn -o -e -X'

    def __init__( self, name, n=1, drop=True, **params):
        """n: number of ONOS instances to run (1)
           drop: drop root privileges (True)"""
        self.check()
        self.drop = drop
        self.count = n
        self.ids = range( 0, self.count )
        Controller.__init__( self, name, **params )
        # We don't need to run as root, and it can interfere
        # with starting Zookeeper manually
        if self.drop:
            self.user = quietRun( 'who am i' ).split()[ 0 ]
            self.sendCmd( 'su', self.user )
            self.waiting = False
        # Need to run commands from ONOS dir
        self.cmd( 'cd', self.onosDir )
        self.cmd( 'export PATH=$PATH:%s' % self.onosDir )
        if hasattr( self, 'mvn' ):
            self.cmd( 'export MVN="%s"' % self.mvn )

    def check( self ):
        "Check for prerequisites"
        if not quietRun( 'which java' ):
                raise Exception( 'java not found -'
                                 ' make sure it is installed and in $PATH' )
        if not quietRun( 'which mvn' ):
                raise Exception( 'Maven (mvn) not found -'
                                ' make sure it is installed and in $PATH' )

    def startCassandra( self ):
        "Start Cassandra"
        self.cmd( 'start-cassandra.sh start' )
        status = self.cmd( 'start-cassandra.sh status' )
        if 'Error' in status:
            raise Exception( 'Cassandra startup failed: ' + status )

    def stopCassandra( self ):
        "Stop Cassandra"
        self.cmd( 'start-cassandra.sh stop' )

    def startZookeeper( self, initcfg=True ):
        "Start Zookeeper"
        # Reinitialize configuration file
        if initcfg:
            cfg = self.zookeeperDir + '/conf/zoo.cfg'
            template = self.zookeeperDir + '/conf/zoo_sample.cfg'
            copyfile( template, cfg )
        self.cmd( 'start-zk.sh restart' )
        status = self.cmd( 'start-zk.sh status' )
        if 'Error' in status:
            raise Exception( 'Zookeeper startup failed: ' + status )

    def stopZookeeper( self ):
        "Stop Zookeeper"
        self.cmd( 'start-zk.sh stop' )

    def setVars( self, id ):
        "Set and return environment vars"
        # ONOS directories and files
        logdir = self.logDir % id
        cassdir = self.cassDir % id
        logback = self.logbackFile % id
        jmxport = self.jmxbase + id
        self.cmd( 'mkdir -p', logdir, cassdir )
        self.cmd( 'export ONOS_LOGDIR="%s"' % logdir )
        self.cmd( 'export ZOO_LOG_DIR="%s"' % logdir )
        self.cmd( 'export CASS_DIR="%s"' % cassdir )
        self.cmd( 'export ONOS_LOGBACK="%s"' % logback )
        self.cmd( 'export JMX_PORT=%s' % jmxport )
        jvmopts = ('-agentlib:jdwp=transport=dt_socket,address=%s,server=y,suspend=n '
            % ( 8000 + id ) )
        jvmopts += ' '.join( '-D%s=%s '% ( opt, val + id )
            for opt, val in self.jvmopts )
        self.cmd( 'export JVM_OPTS="%s"' % jvmopts )

    def startONOS( self, id ):
        """Start ONOS
           id: identifier for new instance"""
        # self.stopONOS( id )
        self.setVars( id )
        self.cmdPrint( 'start-onos.sh startnokill' )

    def stopONOS( self, id ):
        """Shut down ONOS
           id: identifier for instance"""
        pid = self.cmd( "jps -v | grep %s=%s | awk '{print $1}'" %
            ( self.proctag, id ) ).strip()
        if pid:
            self.cmdPrint( 'kill', pid )

    def start( self, *args ):
        "Start ONOS instances"
        info( '* Starting Cassandra\n' )
        self.startCassandra()
        info( '* Starting Zookeeper\n' )
        self.startZookeeper()
        for id in self.ids:
            info( '* Starting ONOS %s\n' % id )
            self.startONOS( id )

    def stop( self, *args ):
        "Stop ONOS instances"
        for id in self.ids:
            info( '* Stopping ONOS %s\n' % id )
            self.stopONOS( id )
        info( '* Stopping zookeeper\n' )
        self.stopZookeeper()
        info( '* Stopping Cassandra\n' )
        self.stopCassandra()

    def clist( self ):
        "Return list of controller specifiers (proto:ip:port)"
        return [ 'tcp:127.0.0.1:%s' % ( self.ofbase + id )
            for id in range( 0, self.count ) ]


class OVSSwitchONOS( OVSSwitch ):
    "OVS switch which connects to multiple controllers"
    def start( self, controllers ):
        OVSSwitch.start( self, controllers )
        assert len( controllers ) == 1
        c0 = controllers[ 0 ]
        assert type( c0 ) == ONOS
        clist = ','.join( c0.clist() )
        self.cmd( 'ovs-vsctl set-controller', self, clist)
        # Reconnect quickly to controllers (1s vs. 15s max_backoff)
        for uuid in self.controllerUUIDs():
            if uuid.count( '-' ) != 4:
                # Doesn't look like a UUID
                continue
            uuid = uuid.strip()
            self.cmd( 'ovs-vsctl set Controller', uuid,
                      'max_backoff=1000' )


controllers = { 'onos': ONOS }
switches = { 'ovso': OVSSwitchONOS }


if __name__ == '__main__':
    "Simple test of ONOSController"
    setLogLevel( 'info' )
    net = Mininet( topo=SingleSwitchTopo( 2 ),
                   controller=partial( ONOS, n=2 ),
                   switch=OVSSwitchONOS )
    net.start()
    CLI( net )
    net.stop()
