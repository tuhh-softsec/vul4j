#! /bin/sh
if [ -f floodlight.onos1vpc.log ]; then
 mv floodlight.onos1vpc.log floodlight.onos1vpc.log.1
fi
ppid=`ps -edalf |grep java |grep floodlight.jar | awk '{print $4}'`
if [ x$ppid != "x" ]; then
  sudo kill -KILL $ppid
fi
java -jar target/floodlight.jar > floodlight.onos1vpc.log 2>&1 &
