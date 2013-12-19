#!/usr/bin/env python

"""
onos.py: A basic (?) ONOS Controller() subclass for Mininet

We implement the following classes:

ONOSController: a custom Controller() subclass to start ONOS
OVSSwitchONOS: a custom OVSSwitch() switch that connects to multiple controllers.

We use single Zookeeper and Cassandra instances for now.

As a custom file, exports:

--controller onos
--switch ovso

Usage:

$ sudo -E ./onos.py

This will start up a simple 2-host, 2 ONOS network

$ sudo -E mn --custom onos.py --controller onos,2 --switch ovso
"""

from mininet.node import Controller, OVSSwitch
from mininet.net import Mininet
from mininet.cli import CLI
from mininet.topo import LinearTopo
from mininet.log import setLogLevel, info, warn
from mininet.util import quietRun

# This should be cleaned up to avoid interfering with mn
from shutil import copyfile
from os import environ, path
from functools import partial
import time
from sys import argv

class ONOS( Controller ):
    "Custom controller class for ONOS"

    # Directories and configuration templates
    home = environ[ 'HOME' ]
    onosDir = home + "/ONOS"
    zookeeperDir = home + "/zookeeper-3.4.5"
    dirBase = '/tmp'
    logDir = dirBase + '/onos-%s.logs'
    # cassDir = dirBase + '/onos-%s.cassandra'
    configFile = dirBase + '/onos-%s.properties'
    logbackFile = dirBase + '/onos-%s.logback.xml'

    # Base ONOS modules
    baseModules = (
        'net.floodlightcontroller.core.FloodlightProvider',
        'net.floodlightcontroller.threadpool.ThreadPool',
        'net.onrc.onos.ofcontroller.floodlightlistener.NetworkGraphPublisher',
        'net.floodlightcontroller.ui.web.StaticWebRoutable',
        'net.onrc.onos.datagrid.HazelcastDatagrid',
        'net.onrc.onos.ofcontroller.flowmanager.FlowManager',
        'net.onrc.onos.ofcontroller.flowprogrammer.FlowProgrammer',
        'net.onrc.onos.ofcontroller.topology.TopologyManager',
        'net.onrc.onos.registry.controller.ZookeeperRegistry'
    )

    # Additions for reactive forwarding
    reactiveModules = (
            'net.onrc.onos.ofcontroller.proxyarp.ProxyArpManager',
            'net.onrc.onos.ofcontroller.core.config.DefaultConfiguration',
            'net.onrc.onos.ofcontroller.forwarding.Forwarding'
    )

    # Module parameters
    ofbase = 6633
    restbase = 8080
    jmxbase = 7189

    fc = 'net.floodlightcontroller.'

    # Things that vary per ONOS id
    perNodeConfigBase = {
        fc + 'core.FloodlightProvider.openflowport': ofbase,
        fc + 'restserver.RestApiServer.port': restbase,
        fc + 'core.FloodlightProvider.controllerid': 0
    }

    # Things that are static
    staticConfig = {
        'net.onrc.onos.ofcontroller.floodlightlistener.NetworkGraphPublisher.dbconf':
            '/tmp/cassandra.titan',
        'net.floodlightcontroller.core.FloodlightProvider.workerthreads': 16,
        'net.floodlightcontroller.forwarding.Forwarding.idletimeout': 5,
        'net.floodlightcontroller.forwarding.Forwarding.hardtimeout': 0
    }

    # Things that are based on onosDir
    dirConfig = {
        'net.onrc.onos.datagrid.HazelcastDatagrid.datagridConfig':
        '%s/conf/hazelcast.xml',
    }

    proctag = 'mn-onos-id'

    # For maven debugging
    # mvn = 'mvn -o -e -X'

    def __init__( self, name, n=1, reactive=True, runAsRoot=False, **params):
        """n: number of ONOS instances to run (1)
           reactive: run in reactive mode (True)
           runAsRoot: run ONOS as root (False)"""
        self.check()
        self.count = n
        self.reactive = reactive
        self.runAsRoot = runAsRoot
        self.ids = range( 0, self.count )
        Controller.__init__( self, name, **params )
        # We don't need to run as root, and it can interfere
        # with starting Zookeeper manually
        self.user = None
        if not self.runAsRoot:
            try:
                self.user = quietRun( 'who am i' ).split()[ 0 ]
                self.sendCmd( 'su', self.user )
                self.waiting = False
            except:
                warn( '__init__: failed to drop privileges\n' )
        # Need to run commands from ONOS dir
        self.cmd( 'cd', self.onosDir )
        self.cmd( 'export PATH=$PATH:%s' % self.onosDir )
        if hasattr( self, 'mvn' ):
            self.cmd( 'export MVN="%s"' % self.mvn )

    def check( self ):
        "Set onosDir and check for ONOS prerequisites"
        if not quietRun( 'which java' ):
                raise Exception( 'java not found -'
                                 ' make sure it is installed and in $PATH' )
        if not quietRun( 'which mvn' ):
                raise Exception( 'Maven (mvn) not found -'
                                ' make sure it is installed and in $PATH' )
        if 'ONOS_HOME' in environ:
            self.onosDir = environ[ 'ONOS_HOME' ]
        else:
            warn( '* $ONOS_HOME is not set - assuming %s\n' % self.onosDir )
        for script in 'start-zk.sh', 'start-cassandra.sh', 'start-onos.sh':
            script = path.join( self.onosDir, script )
            if not path.exists( script ):
                msg = '%s not found' % script
                if 'ONOS_HOME' not in environ:
                    msg += ' (try setting $ONOS_HOME and/or sudo -E)'
                raise Exception( msg )

    def waitNetstat( self, pid ):
        """Wait for pid to show up in netstat
           We assume that once a process is listening on some
           port, it is ready to go!"""
        while True:
            output = self.cmd( 'sudo netstat -natp | grep %s/' % pid )
            if output:
                return output
            info( '.' )
            time.sleep( 1 )

    def waitStart( self, procname, pattern ):
        "Wait for at least one of procname to show up in netstat"
        # Check script exit code
        exitCode = int( self.cmd( 'echo $?' ) )
        if exitCode != 0:
            raise Exception( '%s startup failed with code %d' %
                             ( procname, exitCode ) )
        info( '* Waiting for %s startup' % procname )
        result = self.cmd( 'pgrep -f %s' % pattern ).split()[ 0 ]
        pid = int( result )
        output = self.waitNetstat( pid )
        info( '\n* %s process %d is listening\n' % ( procname, pid ) )
        info( output )

    def startCassandra( self ):
        "Start Cassandra"
        self.cmd( 'start-cassandra.sh start' )
        self.waitStart( 'Cassandra', 'apache-cassandra' )
        status = self.cmd( 'start-cassandra.sh status' )
        if 'running' not in status:
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
        self.waitStart( 'Zookeeper', 'zookeeper' )
        status = self.cmd( 'start-zk.sh status' )
        if 'Error' in status:
            raise Exception( 'Zookeeper startup failed: ' + status )

    def stopZookeeper( self ):
        "Stop Zookeeper"
        self.cmd( 'start-zk.sh stop' )

    def genProperties( self, id, path='/tmp' ):
        "Generate ONOS properties file"
        filename = path + '/onos-%s.properties' % id
        with open( filename, 'w' ) as f:
            # Write modules list
            modules = list( self.baseModules )
            if self.reactive:
                modules += list( self.reactiveModules )
            f.write( 'floodlight.modules = %s\n' %
                     ',\\\n'.join( modules ) )
            # Write other parameters
            for var, val in self.perNodeConfigBase.iteritems():
                if type( val ) is int:
                    val += id
                f.write( '%s = %s\n' % ( var, val ) )
            for var, val in self.staticConfig.iteritems():
                f.write( '%s = %s\n' % ( var, val ) )
            for var, val in self.dirConfig.iteritems():
                f.write( '%s = %s\n' % ( var, val % self.onosDir) )
        return filename

    def setVars( self, id, propsFile ):
        """Set and return environment vars
           id: ONOS instance number
           propsFile: properties file name"""
        # ONOS directories and files
        logdir = self.logDir % id
        # cassdir = self.cassDir % id
        logback = self.logbackFile % id
        jmxport = self.jmxbase + id
        self.cmd( 'mkdir -p', logdir ) # , cassdir
        self.cmd( 'export ONOS_LOGDIR="%s"' % logdir )
        self.cmd( 'export ZOO_LOG_DIR="%s"' % logdir )
        # self.cmd( 'export CASS_DIR="%s"' % cassdir )
        self.cmd( 'export ONOS_LOGBACK="%s"' % logback )
        self.cmd( 'export JMX_PORT=%s' % jmxport )
        self.cmd( 'export JVM_OPTS="-D%s=%s"' % (
            self.proctag, id ) )
        self.cmd( 'export ONOS_PROPS="%s"' % propsFile )

    def startONOS( self, id ):
        """Start ONOS
           id: new instance number"""
        start = time.time()
        self.stopONOS( id )
        propsFile = self.genProperties( id )
        self.setVars( id, propsFile )
        self.cmdPrint( 'start-onos.sh startnokill' )
        # start-onos.sh waits for ONOS startup
        elapsed = time.time() - start
        info( '* ONOS %s started in %.2f seconds\n' % ( id, elapsed ) )

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
        info( '* Stopping Zookeeper\n' )
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


def waitConnected( switches ):
    "Wait until all switches connect to controllers"
    start = time.time()
    info( '* Waiting for switches to connect...\n' )
    for s in switches:
        info( s )
        while not s.connected():
            info( '.' )
            time.sleep( 1 )
        info( ' ' )
    elapsed = time.time() - start
    info( '\n* Connected in %.2f seconds\n' % elapsed )


controllers = { 'onos': ONOS }
switches = { 'ovso': OVSSwitchONOS }


if __name__ == '__main__':
    # Simple test for ONOS() controller class
    setLogLevel( 'info' )
    size = 2 if len( argv ) != 2 else int( argv[ 1 ] )
    net = Mininet( topo=LinearTopo( size ),
                   controller=partial( ONOS, n=2 ),
                   switch=OVSSwitchONOS )
    net.start()
    waitConnected( net.switches )
    CLI( net )
    net.stop()
