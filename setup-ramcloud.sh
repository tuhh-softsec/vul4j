#!/bin/sh

# fail on command error
set -e
# echo back each command
set -x

ONOS_HOME=${ONOS_HOME:-~/ONOS}
RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}

if [ -d ${RAMCLOUD_HOME} ]; then
  echo "${RAMCLOUD_HOME} already exist, please rename or remove them."
  exit 1
fi

# install dependencies
sudo apt-get -y install build-essential git-core libcppunit-dev libboost-all-dev libpcre3-dev protobuf-compiler libprotobuf-dev libcrypto++-dev libevent-dev scons libssl-dev libzookeeper-mt-dev

# clone ramcloud
git clone git://fiz.stanford.edu/git/ramcloud.git ${RAMCLOUD_HOME}
cd ${RAMCLOUD_HOME}
git checkout master
# Switch to release 1.0 + fix for newer gcc
git reset --hard 6a3eb98ae14ec6f732f06775220287c7245f5ff0
# bug fix for MultiWrite, make zookeeper path configurable. (TODO send this patch to upstream)
git apply ${ONOS_HOME}/ramcloud-build-scripts/ramcloud.patch

mkdir ${RAMCLOUD_HOME}/private
cp -vf ${ONOS_HOME}/ramcloud-build-scripts/MakefragPrivateTop ${RAMCLOUD_HOME}/private/MakefragPrivateTop

# download submodule (logcabin, gtest)
git submodule update --init --recursive
# cherry-pick logcabin bug fix
cd logcabin
  git cherry-pick --no-commit 77f0ea2da82e7abe71bb4caf084aa527de6dea50 3862499f477d0e371950aebcb829ddd8ee194962
  git apply ${ONOS_HOME}/ramcloud-build-scripts/logcabin.patch
cd ..

# compile logcabin
make logcabin
# compile ramcloud
make DEBUG=no "$@"


# build ramcloud JNI lib
${ONOS_HOME}/build-ramcloud-java-bindings.sh

