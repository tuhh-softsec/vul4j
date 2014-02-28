ONOS (Open Networking Operating System)
=======================================

ONOS (Open Networking Operating System) is an experimental distributed
SDN OS. Currently, it is under active development. ONOS was announced
and demonstrated at ONS'13.

License
=======
Apache 2.0


Steps to download and setup a development Virtual Machine
==========================================

http://wiki.onlab.us/display/Eng/ONOS+Development+VM

Building ONOS
-------------

1. Cleanly build ONOS

        $ mvn clean
        $ mvn compile

    NOTE: installing maven for the first time may switch java version
    from 1.7 to 1.6. This might prevent Cassandra to run.

External Dependencies
---------------------
1. Zookeeper

    Download and install apache-zookeeper-3.4.5:
    http://zookeeper.apache.org/releases.html

2. RAMCloud

    Run setup-ramcloud.sh to install RAMCloud git repository
    
Configuration
-------------
To configure the processes (ONOS, RAMCLoud and Zookeper) refers to the files in (ONOS-INSTALL-DIR)/conf.
A detailed explanation is given in the ONOS WiKi:
	- "ONOS Documentation/Getting Started with ONOS/Running the RAMCloud branch of ONOS"
	

Running ONOS with RAMCloud as a separate process
-------------------------------------------------

1. Start Zookeeper

        $ cd (ONOS-INSTALL-DIR)/
        $ ./start-zk.sh start

        ## Confirm Zookeeper is running:
        $ ./start.zk.sh status

2. Start RAMCloud Coordinator (only one for cluster)

        $ cd (ONOS-INSTALL-DIR)/
        $ ./start-ramcloud-coordinator.sh start

        ## Confirm RAMCloud Coordinator is running:
        $ ./start-ramcloud-coordinator.sh status
        
3. Start RAMCloud Server

        $ cd (ONOS-INSTALL-DIR)/
        $ ./start-ramcloud-server.sh start

        ## Confirm RAMCloud Server is running:
        $ ./start-ramcloud-server.sh status

4. Start ONOS

        $ cd (ONOS-INSTALL-DIR)/
        $ ./start-onos.sh start

        ## Confirm ONOS is running:
        $ ./start-onos.sh status

5. Start ONOS REST API server

        $ cd (ONOS-INSTALL-DIR)/
        $ ./start-rest.sh start

        ## Confirm the REST API server is running:
        $ ./start-rest.sh status


Developing ONOS in offline environment (Optional)
---------------------------------------------------------------------------

Maven need the Internet connection to download required dependencies and plugins,
when they're used for the first time.

If you need to develop ONOS in an Internet unreachable environment
you may want to run the following helper script before you go offline,
so that required dependencies and plugins for frequently used maven target will be
downloaded to your local environment.

        $ ./prep-for-offline.sh


Running in offline mode (Optional)
----------------------------------

Maven is used to build and run ONOS. By default, maven tries to reach
the repositories. The '-o' option can be given to the 'mvn' command to
suppress this behavior. The `MVN` environmental variable can be used to
set additional options to the 'mvn' command used in ONOS.

* Example: Running in offline mode

        $ env MVN="mvn -o" ./start-onos.sh start
