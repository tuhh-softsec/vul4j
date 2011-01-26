#!/bin/ksh

set -x

$JAVA_HOME/bin/keytool -exportcert -alias $1 -file $1.crt -storepass secret -keystore ./keystore.jks
