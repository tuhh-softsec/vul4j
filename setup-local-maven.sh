#!/bin/bash

if [ -z "${MVN}" ]; then
    MVN="mvn"
fi

${MVN} install:install-file -Dfile=./lib/curator-framework-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-framework -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
${MVN} install:install-file -Dfile=./lib/curator-client-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-client -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
${MVN} install:install-file -Dfile=./lib/curator-recipes-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-recipes -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
${MVN} install:install-file -Dfile=./lib/curator-x-discovery-1.3.5-SNAPSHOT.jar -DgroupId=com.netflix.curator -DartifactId=curator-x-discovery -Dversion=1.3.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

# download package dependencies
${MVN} dependency:go-offline

# run goals to download required plugins
${MVN} -q test -Dtest=DoNotTest -DfailIfNoTests=false > /dev/null
${MVN} clean compile
