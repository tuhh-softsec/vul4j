ONOS
====

Open Networking Operating System

BELOW TO BE WRITTEN IN DETAIL


Building ONOS
-------------

0. Install custom jars (Only need to be run only once)

    $ ./setup-local-jar.sh

1. Cleanly Build ONOS

    $ mvn clean
    $ mvn compile


Dependencies
------------
1. Zookeeper
    Download and install apache-zookeeper-3.4.5: http://zookeeper.apache.org/releases.html
2. Cassandra
    Download and install apache-cassandra-1.2.2: http://cassandra.apache.org/download/

Running ONOS
------------

1. Start zookeeper

    $ cd (ZOOKEEPER-INSTALL-DIR)/bin
    
    $ ./zkServer.sh start

2. Start cassandra

    $ ./start-cassandra.sh start

  1. Confirm cassandra is running
  
      $ ./start-cassandra.sh status

3. Start ONOS instance

    $ cd (ONOS-INSTALL-DIR)/

    $ ./start-onos.sh start
    
4. Start ONOS rest apis

    $ ./start-rest.sh start

Running ONOS with Cassandra embedded (Optional)
-----------------------------------------------

1. Start Zookeeper

    $ cd (ZOOKEEPER-INSTALL-DIR)/bin
    
    $ ./zkServer.sh start
    
2. Start ONOS and Cassandra embedded

    $ cd (ONOS-INSTAL_DIR)/
    
    $ ./start-onos-embedded.sh start
    
3. Start ONOS rest apis

    $ ./start-rest.sh start

