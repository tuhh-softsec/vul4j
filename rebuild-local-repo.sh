#!/bin/bash

#
# A helper script to update the modified local copy of "curator" or the "kryo2"
# version.
#
# NOTE: This script is useful only for developers.
# Usage:
#  1. Update the "curator" jar file or the "kryo2" pom.xml file as appropriate.
#  2. Run this script.
#  3. Push the modified repo/ directory to the source code repository.
#

if [ -z "${MVN}" ]; then
    MVN="mvn"
fi

# Install modified curators to local repo
${MVN} install:install-file -Dfile=./curator/curator-framework-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-framework -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true
${MVN} install:install-file -Dfile=./curator/curator-client-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-client -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true
${MVN} install:install-file -Dfile=./curator/curator-recipes-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-recipes -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true
${MVN} install:install-file -Dfile=./curator/curator-x-discovery-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-x-discovery -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=./repo -DcreateChecksum=true

