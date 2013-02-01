#!/bin/sh

# Set paths
FL_HOME=`dirname $0`
FL_JAR="${FL_HOME}/target/floodlight.jar"
FL_LOGBACK="${FL_HOME}/logback.xml"
TITAN_CONFIG="/tmp/cassandra.titan"
CASSANDRA_CONFIG="/home/`whoami`/apache-cassandra-1.1.4/conf/cassandra.yaml"

# Set JVM options
JVM_OPTS=""
#JVM_OPTS="$JVM_OPTS -server -d64"
#JVM_OPTS="$JVM_OPTS -Xmx2g -Xms2g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
#JVM_OPTS="$JVM_OPTS -XX:MaxInlineSize=8192 -XX:FreqInlineSize=8192"
#JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500 -XX:PreBlockSpin=8"
#JVM_OPTS="$JVM_OPTS -Dpython.security.respectJavaAccessibility=false"

# Set classpath to include titan libs
#CLASSPATH=`echo ${FL_HOME}/lib/*.jar ${FL_HOME}/lib/titan/*.jar | sed 's/ /:/g'`
CLASSPATH="${FL_HOME}/lib/*.jar:${FL_HOME}/lib/titan/*.jar"

CASSANDRA_OPTS="-Dcom.sun.management.jmxremote.port=7199"
CASSANDRA_OPTS="$CASSANDRA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
CASSANDRA_OPTS="$CASSANDRA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"

# Create a logback file if required
cat <<EOF_LOGBACK >${FL_LOGBACK}
<configuration scan="true" debug="true">
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%level [%logger:%thread] %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>onos.log</file>
    <encoder>
      <pattern>%date %level [%thread] %logger{10} [%file:%line] %msg%n</pattern>
    </encoder>
  </appender>

  <logger name="org" level="WARN"/>
  <logger name="LogService" level="WARN"/> <!-- Restlet access logging -->
  <logger name="net.floodlightcontroller.logging" level="WARN"/>

  <root level="DEBUG">
    <appender-ref ref="STDOUT" />
    <appender-ref ref="FILE" />
  </root>
</configuration>
EOF_LOGBACK

cat <<EOF_TITAN >${TITAN_CONFIG}
storage.backend=embeddedcassandra
storage.hostname=127.0.0.1
storage.keyspace=onos
storage.cassandra-config-dir=file://${CASSANDRA_CONFIG}
EOF_TITAN

# Clear logs
rm onos.log

# Run floodlight
echo "Starting ONOS controller ..."
echo 
#java ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -Xbootclasspath/a:$CLASSPATH -jar ${FL_JAR} -cf ./onos.properties
java ${JVM_OPTS} ${CASSANDRA_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -cp ${CLASSPATH} -jar ${FL_JAR}
