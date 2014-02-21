#!/bin/sh

# run this script, when RAMCloud java binding is updated

set -x

ONOS_HOME=${ONOS_HOME:-~/ONOS}
RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}

# create ramcloud lib
cp -pvf ${ONOS_HOME}/src/main/java/edu/stanford/ramcloud/JRamCloud.java ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/
cp -pvf ${ONOS_HOME}/src/main/cpp/edu_stanford_ramcloud_JRamCloud.cc ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/
cd ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/
javac JRamCloud.java
./build_so.sh
jar cvf ${RAMCLOUD_HOME}/bindings/java/RamCloud.jar ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/*.class
