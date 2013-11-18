#!/bin/bash

if [ -z "${MVN}" ]; then
    MVN="mvn"
fi

BASE_DIR=`dirname $0`
ONOS_DIR="`dirname $0`/.."
#CP=$( echo `dirname $0`/../lib/*.jar `dirname $0`/../lib/titan/*.jar . | sed 's/ /:/g')
#CP=`${MVN} -f ${ONOS_DIR}/pom.xml dependency:build-classpath -Dmdep.outputFile=/dev/stdout -l /dev/stderr`

# Use a python script to parse the classpath out of the .classpath file
CP=`${BASE_DIR}/../scripts/parse-classpath.py`

if [[ "$CP" == *"Error reading classpath file"* ]]; then
    echo $CP
    exit 1
fi

# Find Java 
if [ "$JAVA_HOME" = "" ] ; then
    JAVA="java -server"
else
    JAVA="$JAVA_HOME/bin/java -server"
fi

# Set Java options
if [ "$JAVA_OPTIONS" = "" ] ; then
    JAVA_OPTIONS="-Xms32m -Xmx512m"
fi

# Launch the application 
if [ "$1" = "-e" ]; then
  k=$2
  if [ $# -gt 2 ]; then
    for (( i=3 ; i < $# + 1 ; i++ ))
    do
      eval a=\$$i
      k="$k \"$a\""
    done
  fi

  eval $JAVA $JAVA_OPTIONS -cp $CP:$CLASSPATH com.thinkaurelius.titan.tinkerpop.gremlin.ScriptExecutor $k
else
  if [ "$1" = "-v" ]; then
    $JAVA $JAVA_OPTIONS -cp $CP:$CLASSPATH com.tinkerpop.gremlin.Version
  else
    pushd $BASE_DIR >/dev/null
    $JAVA $JAVA_OPTIONS -cp $CP:$CLASSPATH com.thinkaurelius.titan.tinkerpop.gremlin.Console
    popd >/dev/null
  fi
fi

# Return the program's exit code 
exit $?
