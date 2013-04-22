#!/bin/bash

# Set paths
FL_HOME=`dirname $0`
FL_JAR="${FL_HOME}/target/floodlight.jar"
FL_ONLY_JAR="${FL_HOME}/target/floodlight-only.jar"
FL_LOGBACK="${FL_HOME}/logback.xml"
LOGDIR=${FL_HOME}/onos-logs
FL_LOG="${LOGDIR}/onos.`hostname`.log"
PCAP_LOG="${LOGDIR}/onos.`hostname`.pcap"
LOGS="$FL_LOG $PCAP_LOG"

# Set JVM options
JVM_OPTS=""
JVM_OPTS="$JVM_OPTS -server -d64"
JVM_OPTS="$JVM_OPTS -Xmx2g -Xms2g -Xmn800m"
JVM_OPTS="$JVM_OPTS -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
JVM_OPTS="$JVM_OPTS -XX:MaxInlineSize=8192 -XX:FreqInlineSize=8192"
JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500 -XX:PreBlockSpin=8"
#JVM_OPTS="$JVM_OPTS -Dpython.security.respectJavaAccessibility=false"

# Set classpath to include titan libs
#CLASSPATH=`echo ${FL_HOME}/lib/*.jar ${FL_HOME}/lib/titan/*.jar | sed 's/ /:/g'`
CLASSPATH="${FL_ONLY_JAR}:${FL_HOME}/lib/*:${FL_HOME}/lib/titan/*"
MAIN_CLASS="net.floodlightcontroller.core.Main"

#<logger name="net.floodlightcontroller.linkdiscovery.internal" level="TRACE"/>
#<appender-ref ref="STDOUT" />

function lotate {
    logfile=$1
    nr_max=${2:-10}
    if [ -f $logfile ]; then
	for i in `seq $(expr $nr_max - 1) -1 1`; do
	    if [ -f ${logfile}.${i} ]; then
		mv -f ${logfile}.${i} ${logfile}.`expr $i + 1`
	    fi
	done
	mv $logfile $logfile.1
    fi
}

function start {
  if [ ! -d ${LOGDIR} ]; then
    mkdir -p ${LOGDIR}
  fi
  # Backup log files
  for log in ${LOGS}; do
    echo "rotate log: $log"
    if [ -f ${log} ]; then
      lotate ${log}
    fi
  done

# Create a logback file if required
  cat <<EOF_LOGBACK >${FL_LOGBACK}
<configuration scan="true" debug="true">
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
<encoder>
<pattern>%level [%logger:%thread] %msg%n</pattern>
</encoder>
</appender>

<appender name="FILE" class="ch.qos.logback.core.FileAppender">
<file>${FL_LOG}</file>
<encoder>
<pattern>%date %level [%thread] %logger{10} [%file:%line] %msg%n</pattern>
</encoder>
</appender>

<logger name="org" level="WARN"/>
<logger name="LogService" level="WARN"/> <!-- Restlet access logging -->
<logger name="net.floodlightcontroller.logging" level="WARN"/>

<root level="DEBUG">
<appender-ref ref="FILE" />
</root>
</configuration>
EOF_LOGBACK

  # Run floodlight
  echo "Starting ONOS controller ..."
  echo 
  #java ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -jar ${FL_JAR} -cf ${FL_HOME}/onos.properties > /dev/null 2>&1 &
  java ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -cp ${CLASSPATH} ${MAIN_CLASS} -cf ${FL_HOME}/onos.properties > /dev/null 2>&1 &


#  echo "java ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -jar ${FL_JAR} -cf ./onos.properties > /dev/null 2>&1 &"
#  sudo -b /usr/sbin/tcpdump -n -i eth0 -s0 -w ${PCAP_LOG} 'tcp port 6633' > /dev/null  2>&1
}

function stop {
  # Kill the existing processes
  flpid=`jps -l |grep ${MAIN_CLASS} | awk '{print $1}'`
  tdpid=`ps -edalf |grep tcpdump |grep ${PCAP_LOG} | awk '{print $4}'`
  pids="$flpid $tdpid"
  for p in ${pids}; do
    if [ x$p != "x" ]; then
      kill -KILL $p
      echo "Killed existing prosess (pid: $p)"
    fi
  done
}

function deldb {
   # Delete the berkeley db database
   if [ -d "/tmp/cassandra.titan" ]; then
      rm -rf /tmp/cassandra.titan
      mkdir /tmp/cassandra.titan
   fi
}
function check_db {
   if [ -d "/tmp/cassandra.titan" ]; then
      echo "Cassandra is running on local berkely db. Exitting"
      exit
   fi
   n=`ps -edalf |grep java |grep apache-cassandra | wc -l`
   if [ x$n == "x0" ]; then
      echo "Cassandra is not running. Exitting"
      exit
   fi
}

case "$1" in
  start)
    stop
    check_db
    start 
    ;;
  startifdown)
    n=`jps -l |grep "net.floodlightcontroller.core.Main" | wc -l`
    if [ $n == 0 ]; then
      start
    else 
      echo "$n instance of onos running"
    fi
    ;;
  stop)
    stop
    ;;
  deldb)
    deldb
    ;;
  status)
    n=`jps -l |grep "net.floodlightcontroller.core.Main" | wc -l`
    echo "$n instance of onos running"
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|startifdown}"
    exit 1
esac
