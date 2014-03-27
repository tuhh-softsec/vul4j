#!/bin/bash

# fail on command error
set -e

JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-7-oracle}
ONOS_HOME=${ONOS_HOME:-~/ONOS}
RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}
RAMCLOUD_BRANCH=${RAMCLOUD_BRANCH:-master}

mvn -f ${ONOS_HOME}/pom.xml compile -T 1C

javah -cp ${ONOS_HOME}/target/classes -o ${ONOS_HOME}/src/main/cpp/edu_stanford_ramcloud_JRamCloud.h edu.stanford.ramcloud.JRamCloud

g++ -g -Wall -O3 -shared -fPIC -std=c++0x -I${JAVA_HOME}/include/ -I${JAVA_HOME}/include/linux -I${RAMCLOUD_HOME}/src/ -I${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH}/ -I${RAMCLOUD_HOME}/logcabin/ -I${RAMCLOUD_HOME}/gtest/include/ -L${RAMCLOUD_HOME}/obj.${RAMCLOUD_BRANCH} -o ${ONOS_HOME}/lib/libedu_stanford_ramcloud_JRamCloud.so ${ONOS_HOME}/src/main/cpp/edu_stanford_ramcloud_JRamCloud.cc -lramcloud


