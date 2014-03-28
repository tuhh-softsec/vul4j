#!/usr/bin/python

import os
import sys
import time
import socket
import argparse
import paramiko
import subprocess

CASSANDRA_DIR = "/home/ubuntu/apache-cassandra-1.1.4"
CASSANDRA_NODETOOL = CASSANDRA_DIR + "/bin/nodetool"
CASSANDRA_CMD = CASSANDRA_DIR + "/bin/cassandra"

class RemoteOnosNode:

    def __init__(self, ip):
        #self.hostname = hostname
        self.ip = ip

        self.ssh = paramiko.SSHClient()
        self.ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        self.ssh.connect(self.ip, key_filename='/home/ubuntu/.ssh/onlabkey.pem')

    def get_pids(self, process_search_string):
        stdin, stdout, stderr = self.ssh.exec_command('pgrep -f %s' % process_search_string)
        pid_list = stdout.readlines()

        return ([x.strip() for x in pid_list])

    #takes a list of pids to kill
    def kill_all(self, pid_list):
        for pid in pid_list:
            stdin, stdout, stderr = self.ssh.exec_command('kill %s' % pid)

            if stderr.read() != '':
                print "Killing %s failed" % pid
            else:
                print "%s killed" % pid

    def clean(self):
        processes = ['floodlight.jar', 'CassandraDaemon']

        for process in processes:
            self.kill_all(self.get_pids(process))

    def start_onos(self, embedded=False):
        print "*** Starting ONOS:"
        
        if embedded:
            onos_script = ('nohup /home/ubuntu/ONOS/onos-embedded.sh > '
                           '/dev/null 2>&1 &')
        else:
            onos_script = 'nohup /home/ubuntu/ONOS/onos.sh > /dev/null 2>&1 &'
        
        self.ssh.exec_command(onos_script)

        
    def start_cassandra(self):

        print "*** Starting Cassandra:"

        stdin, stdout, stderr = self.ssh.exec_command(CASSANDRA_CMD)
        if stderr.read() != '':
            print "Error starting Casssanda"
            return

        print "*** Waiting while Cassandra bootstaps:"

        node_bootstrapped = False
        while not node_bootstrapped:
            stdin, stdout, stderr = self.ssh.exec_command(CASSANDRA_NODETOOL + ' ring')
            for line in stdout.readlines():
                if (self.ip in line) and ('Up' in line):
                    node_bootstrapped = True

            time.sleep(1)

    def check_status(self):
        #self.check_process('floodlight.jar')
        
        onos_pids = self.get_pids('floodlight.jar')
        onos_ok = len(onos_pids) == 1

        cassandra_pids = self.get_pids('CassandraDaemon')
        cassandra_ok = len(cassandra_pids) == 1

        controller_port = self.check_port_listening(self.ip, '6633')
        
        in_ring = self.check_in_ring()

        if onos_ok and (not cassandra_ok) and in_ring:
            print "ONOS and Cassandra running in EMBEDDED mode"
        elif onos_ok and cassandra_ok and in_ring and controller_port:
            print "ONOS and Cassandra running in SEPARATE mode"
        elif not (onos_ok or cassandra_ok or in_ring or controller_port):
            print "Node is DOWN"
        else:
            print "Node is not fully operational:"
            print "ONOS process pid %s" % onos_pids
            print "Cassandra process pids %s" % cassandra_pids
            print "Controller port open: %s" % controller_port
            print "Node in Cassandra ring: %s" % in_ring

    def check_in_ring(self):
        stdin, stdout, stderr = self.ssh.exec_command(CASSANDRA_NODETOOL + ' ring')

        for line in stdout.readlines():
            if (self.ip in line) and ('Up' in line):
                return True

        return False

    def check_port_listening(self, host, port):
        command = 'telnet -e A %s %s' % (host, port)
        process = subprocess.Popen(command.split(), stdin=subprocess.PIPE, 
                                   stdout=subprocess.PIPE, 
                                   stderr=subprocess.STDOUT)
        output = process.communicate(input='A quit')[0]

        if 'Unable' in output:
            return False

        return True

def start_mininet(mn_host):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    
    ssh.connect(mn_host, key_filename='/home/ubuntu/.ssh/onlabkey.pem')

    stdin, stdout, stderr = ssh.exec_command('sudo mn -c')

    #print (stdout.read())
    #print (stderr.read())
    if not '*** Cleanup complete.' in stderr.read():
        print ("Mininet didn't clean up properly")

    network_file = '/home/ubuntu/sdn2/onos-nw.py'
    stdin, stdout, stderr = ssh.exec_command('sudo python %s > /dev/null 2>&1 &' % network_file)

    print "OUT\n"
    print stdout.read()
    print "ERR\n"
    print stderr.read()

    ssh.close()

def add(args):
    (onos_ip, onos_hostname) = get_ip_hostname(args.hostname)

    pr_hostname = "" if onos_ip == onos_hostname else onos_hostname
    print "Starting up ONOS node %s with IP %s" % (pr_hostname, onos_ip)
    
    remote_node = RemoteOnosNode(onos_ip)
    remote_node.clean()

    #wait a few seconds to let cassandra fully shutdown
    time.sleep(2)

    if args.embedded:
        remote_node.start_onos(embedded=True)
    else:
        remote_node.start_cassandra()
        remote_node.start_onos()


def get_ip_hostname(node):
    """ Takes in either a hostname or an IP address and returns
    (ip, hostname).
    Note if IP is given both values will be the same, i.e. we don't
    actually care about finding the hostname if user specified an IP"""

    if '.' in node:
        return (node, node)
    else:
        return (socket.gethostbyname(node), node)


def status(args):
    for node in args.cluster_nodes.split(','):
        (ip, hostname) = get_ip_hostname(node)

        print "Status of %s:" % hostname

        remote_node = RemoteOnosNode(ip)
        remote_node.check_status()

        print
        
def remove(args):
    remote_node = RemoteOnosNode(args.hostname)
    remote_node.clean()

def parse_args():
    commands = ['add', 'remove', 'status']

    parser = argparse.ArgumentParser(description='Add node to ONOS cluster')
    ##add command
    subparsers = parser.add_subparsers(help='command')
    parser_add = subparsers.add_parser('add', 
                                       help='Add a node to ONOS cluster')
    parser_add.add_argument('hostname',
                            help='hostname of new ONOS node')
    parser_add.add_argument('-e', '--embedded', action='store_true',
                            help='run Cassandra in embedded mode in the same '
                            'JVM as ONOS')
    parser_add.set_defaults(func=add)

    ##status command
    parser_status = subparsers.add_parser('status',
                                          help='Check status of ONOS nodes')
    parser_status.add_argument('cluster_nodes',
                               help='comma-delimited list IP addresses for '
                               'existing cluster nodes')
    parser_status.set_defaults(func=status)

    ##remove command
    parser_remove = subparsers.add_parser('remove', help='Remove an ONOS node '
                                          'from the cluster')
    parser_remove.add_argument('hostname', help='ONOS node to remove')
    parser_remove.set_defaults(func=remove)
                               
    
    return parser.parse_args()


if __name__ == '__main__':
    args = parse_args()

    args.func(args)
    """
    if args.mininet:
        start_mininet(args.mininet)
    else:
        print('Assuming mininet host is the same as ONOS host')
        start_mininet(args.onos)
    """

    
    sys.exit(0)
