#!/bin/sh

# run this script, when RAMCloud java binding is updated

set -x

export ONOS_HOME=${ONOS_HOME:-$(cd `dirname $0`; pwd)}
export RAMCLOUD_HOME=${RAMCLOUD_HOME:-~/ramcloud}

${ONOS_HOME}/ramcloud-build-scripts/build_jni_so.sh

cd ${ONOS_HOME}/target/classes
jar cfe ${ONOS_HOME}/lib/RamCloud.jar edu.stanford.ramcloud.JRamCloud edu/stanford/ramcloud/*.class
