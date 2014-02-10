#!/bin/sh

set -x

ONOS_HOME=~/ONOS
RAMCLOUD_HOME=~/ramcloud

if [ -d ${RAMCLOUD_HOME} ]; then
  echo "${RAMCLOUD_HOME} already exist, please rename or remove them."
  exit 1
fi

# clone ramcloud
git clone git://github.com/y-higuchi/ramcloud.git ${RAMCLOUD_HOME}
cd ${RAMCLOUD_HOME}
git checkout custom

# install some app
sudo apt-get -y install build-essential git-core libcppunit-dev libcppunit-doc doxygen libboost-all-dev libpcre3-dev protobuf-compiler libprotobuf-dev libcrypto++-dev libevent-dev scons libssl-dev

# compile ramcloud
git submodule update --init --recursive
patch ${RAMCLOUD_HOME}/logcabin/Core/Time.h < ${ONOS_HOME}/logcabin.patch
make logcabin
make DEBUG=no $*

ln -s ${RAMCLOUD_HOME}/obj.custom obj.blueprint-java

# crate ramcloud lib
cp -p ${ONOS_HOME}/src/main/java/edu/stanford/ramcloud/JRamCloud.java ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
cp -p ${ONOS_HOME}/src/main/cpp/edu_stanford_ramcloud_JRamCloud.cc ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
cd ${HOME}/ramcloud/bindings/java/edu/stanford/ramcloud/
javac JRamCloud.java
./build_so.sh
jar cvf ${RAMCLOUD_HOME}/bindings/java/RamCloud.jar ${RAMCLOUD_HOME}/bindings/java/edu/stanford/ramcloud/*.class

