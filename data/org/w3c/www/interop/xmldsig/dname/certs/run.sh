#!/bin/ksh

set -x

$JAVA_HOME/bin/keytool -genkeypair -alias $1 -dname "$2" -keyalg DSA -sigalg SHA1WithDSA -validity 36500 -keypass secret -storepass secret -keystore ./keystore.jks
