#!/bin/bash

if [ -z "${MVN}" ]; then
    MVN="mvn"
fi

# download package dependencies
${MVN} -T 1C dependency:go-offline

# run goals to download required plugins
${MVN} -T 1C checkstyle:checkstyle
${MVN} -q -T 1C clean test -Dtest=DoNotTest -DfailIfNoTests=false > /dev/null
${MVN} -T 1C compile
