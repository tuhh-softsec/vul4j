#!/bin/sh

set -x

ONOS_HOME=~/ONOS
RAMCLOUD_HOME=~/ramcloud

# clone ramcloud
git clone https://github.com/y-higuchi/ramcloud.git ${RAMCLOUD_HOME}
cd ${RAMCLOUD_HOME}
git checkout blueprint-java
git checkout 64462be50b9b0add25cf16beea75eb40bf89f62c

# install some app
sudo apt-get -y install build-essential git-core libcppunit-dev libcppunit-doc doxygen libboost-all-dev libpcre3-dev protobuf-compiler libprotobuf-dev libcrypto++-dev libevent-dev scons libssl-dev

# compile ramcloud
git submodule update --init --recursive
patch ${RAMCLOUD_HOME}/src/MasterService.cc < ${ONOS_HOME}/ramcloud.patch 
patch ${RAMCLOUD_HOME}/logcabin/Core/Time.h < ${ONOS_HOME}/logcabin.patch
make logcabin
make

ln -s ${RAMCLOUD_HOME}/obj obj.blueprint-java

# crate ramcloud lib
cp ${ONOS_HOME}/src/main/java/edu/stanford/ramcloud/JRamCloud.java ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
cp ${ONOS_HOME}/src/main/cpp/edu_stanford_ramcloud_JRamCloud.cc ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
cd ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
javac JRamCloud.java
./build_so.sh
jar cvf ${RAMCLOUD_HOME}/bindings/java/RamCloud.jar ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/*.class
