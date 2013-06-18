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

    $ ./start-onos.sh start
    
4. Start ONOS rest apis

    $ ./start-rest.sh start
