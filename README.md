ONOS (Open Networking Operating System)
=======================================

ONOS (Open Networking Operating System) is an experimental distributed
SDN OS. Currently, it is under active development. ONOS was announced
and demonstrated at ONS'13.

Steps to download and setup a development Virtual Machine
==========================================

http://wiki.onlab.us/display/Eng/ONOS+Development+VM

Building ONOS
-------------

0. Install custom jars and dependencies (needs to be run only once)

    $ ./setup-local-maven.sh

1. Cleanly build ONOS

    $ mvn clean
    $ mvn compile

    NOTE: installing maven for the first time may switch java version
    from 1.7 to 1.6. This might prevent Cassandra to run.

Dependencies
------------
1. Zookeeper
    Download and install apache-zookeeper-3.4.5:
    http://zookeeper.apache.org/releases.html

    Edit file (ONOS-INSTALL-DIR)/start-zk.sh and set variable "ZK_DIR"
    to point to the Zookeeper directory.

2. Cassandra
    Download and install apache-cassandra-1.2.4:
    http://cassandra.apache.org/download/

    Edit file (ONOS-INSTALL-DIR)/start-cassandra.sh and set variable
    "CASSANDRA_DIR" to point to the Cassandra directory.

Running ONOS
------------

1. Start Zookeeper

    $ cd (ONOS-INSTALL-DIR)/
    $ ./start-zk.sh start

    ## Confirm Zookeeper is running:
    $ ./start.zk.sh status

2. Start Cassandra

    $ cd (ONOS-INSTALL-DIR)/
    $ ./start-cassandra.sh start

    ## Confirm Cassandra is running:
    $ ./start-cassandra.sh status

3. Start ONOS

    $ cd (ONOS-INSTALL-DIR)/
    $ ./start-onos.sh start

    ## Confirm ONOS is running:
    $ ./start-onos.sh status
    
4. Start ONOS REST API server

    $ cd (ONOS-INSTALL-DIR)/
    $ ./start-rest.sh start

    ## Confirm the REST API server is running:
    $ ./start-rest.sh status

Running ONOS with Cassandra embedded (Optional)
-----------------------------------------------

1. Start Zookeeper

    $ cd (ONOS-INSTALL-DIR)/
    $ ./start-zk.sh start

    ## Confirm Zookeeper is running:
    $ ./start.zk.sh status
    
2. Start ONOS and Cassandra embedded

    $ cd (ONOS-INSTALL_DIR)/
    $ ./start-onos-embedded.sh start

    ## Confirm ONOS is running:
    $ ./start-onos-embedded.sh status
    
3. Start ONOS REST API server

    $ cd (ONOS-INSTALL-DIR)/
    $ ./start-rest.sh start

    ## Confirm the REST API server is running:
    $ ./start-rest.sh status


Running in offline mode (Optional)
----------------------------------

Maven is used to build and run ONOS. By default, maven tries to reach
the repositories. The '-o' option can be given to the 'mvn' command to
suppress this behavior. The MVN environmental variable can be used to
set additional options to the 'mvn' command used in ONOS.

* Example: Running in offline mode

    $ env MVN="mvn -o" ./start-onos.sh start
