#!/bin/sh

# Set paths
FL_HOME=`dirname $0`
FL_JAR="${FL_HOME}/target/floodlight.jar"
FL_LOGBACK="${FL_HOME}/logback.xml"

# Set JVM options
JVM_OPTS=""
#JVM_OPTS="$JVM_OPTS -server -d64"
#JVM_OPTS="$JVM_OPTS -Xmx2g -Xms2g -Xmn800m"
#JVM_OPTS="$JVM_OPTS -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
#JVM_OPTS="$JVM_OPTS -XX:MaxInlineSize=8192 -XX:FreqInlineSize=8192"
#JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500 -XX:PreBlockSpin=8"
#JVM_OPTS="$JVM_OPTS -Dpython.security.respectJavaAccessibility=false"

# Set classpath to include titan libs
CLASSPATH=`echo ${FL_HOME}/lib/*.jar ${FL_HOME}/lib/titan/*.jar | sed 's/ /:/g'`


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

# Delete and recreate /tmp/netmap
#rm -rf /tmp/cassandra.titan
#mkdir /tmp/cassandra.titan

# Clear logs
rm onos.log

# Run floodlight
echo "Starting ONOS controller ..."
echo 
#java ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -Xbootclasspath/a:$CLASSPATH -jar ${FL_JAR} -cf ./onos.properties
java ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -jar ${FL_JAR} -cf ./onos.properties
