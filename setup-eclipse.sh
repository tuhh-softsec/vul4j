#!/bin/bash
if [ -z "${MVN}" ]; then
    MVN="mvn"
fi
${MVN} eclipse:eclipse

