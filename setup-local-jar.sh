#!/bin/bash

 mvn install:install-file -Dfile=./lib/curator-framework-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-framework -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
 mvn install:install-file -Dfile=./lib/curator-client-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-client -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
 mvn install:install-file -Dfile=./lib/curator-recipes-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-recipes -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
 mvn install:install-file -Dfile=./lib/curator-x-discovery-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-x-discovery -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
 mvn install:install-file -Dfile=./lib/packetstreamer-thrift-0.1.0.jar -DgroupId=net.floodlightcontroller -DartifactId=packetstreamer-thrift -Dversion=0.1.0 -Dpackaging=jar -DgeneratePom=true
