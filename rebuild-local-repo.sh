#!/bin/bash

if [ -z "${MVN}" ]; then
    MVN="mvn"
fi

# Install Kryo2 workaround to local repo
# - Shaded(rename package name to allow mixing 2 different Kryo version)
# - Install created sharded jar to local repo
${MVN} -f kryo2/pom.xml package exec:exec

# Install modified curators to local repo
${MVN} install:install-file -Dfile=./curator/curator-framework-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-framework -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true
${MVN} install:install-file -Dfile=./curator/curator-client-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-client -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true
${MVN} install:install-file -Dfile=./curator/curator-recipes-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-recipes -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true
${MVN} install:install-file -Dfile=./curator/curator-x-discovery-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-x-discovery -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true

