#!/bin/sh

basedir=`dirname $0`
onosdir="${basedir}/.."

logfile="${onosdir}/logback.xml"
jarfile="${onosdir}/target/floodlight-only.jar"
classpath="${jarfile}:${onosdir}/lib/*:${onosdir}/lib/titan/*"
mainclass="net.floodlightcontroller.core.Main"
propfile="${onosdir}/onos.properties"

#java -Dlogback.configurationFile=logback.xml -cp target/floodlight-only.jar:lib/*:lib/titan/* net.floodlightcontroller.core.Main -cf onos.properties
java -Dlogback.configurationFile=${logfile} -cp ${classpath} ${mainclass} -cf ${propfile}
