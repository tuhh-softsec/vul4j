#!/bin/sh

# run this script, when RAMCloud java binding is updated

set -x

ONOS_HOME=~/ONOS
RAMCLOUD_HOME=~/ramcloud

# create ramcloud lib
cp -p ${ONOS_HOME}/src/main/java/edu/stanford/ramcloud/JRamCloud.java ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
cp -p ${ONOS_HOME}/src/main/cpp/edu_stanford_ramcloud_JRamCloud.cc ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
cd ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
javac JRamCloud.java
./build_so.sh
jar cvf ${RAMCLOUD_HOME}/bindings/java/RamCloud.jar ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/*.class
