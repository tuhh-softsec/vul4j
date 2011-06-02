#########################################################################
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#  
#      http://www.apache.org/licenses/LICENSE-2.0
#  
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#########################################################################

This directory contains the example code for parsing RSS (Really Simple
Syndication) newsfeeds, which was originally included directly in the
commons-digester.jar file, in package "org.apache.commons.digester.rss".
The package name has not been changed, so the only impact on applications
relying on these classes will be the need to include an additional JAR
file (commons-digester-rss.jar) in their classpath.

A packaged distribution can be created by using Apache Maven:

* to compile:
  mvn compile

* to build the jar artifact
  mvn package

* to run:
  mvn verify

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly.
