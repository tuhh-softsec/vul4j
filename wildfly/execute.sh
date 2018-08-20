#!/bin/bash

if [ -z $JBOSS_HOME ]
then
    JBOSS_HOME=/opt/jboss/wildfly
fi
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
JBOSS_MODE=${1:-"standalone"}
JBOSS_CONFIG=${2:-"$JBOSS_MODE.xml"}

function wait_for_server() {
    until `$JBOSS_CLI -c "ls /deployment" &> /dev/stdout`; do
        sleep 1
    done
}

echo "=> Starting WildFly server"
$JBOSS_HOME/bin/$JBOSS_MODE.sh -c $JBOSS_CONFIG  &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> Executing the commands"
$JBOSS_CLI -c --user=admin --password=secret --file=`dirname "$0"`/commands.cli

echo "=> Shutting down WildFly"
if [ "$JBOSS_MODE" = "standalone" ]; then
    $JBOSS_CLI -c ":shutdown"
    sleep 10
    echo "=> done."
else
    $JBOSS_CLI -c "/host=*:shutdown"
fi
