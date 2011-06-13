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

== overview

The files in this directory are intended as an example of how to use
the Apache Digester's "plugins" functionality.

Topics covered:
* how to declare "plugin points" using PluginCreateRule.
* how to write plugin classes.

If you're just starting with Digester, try the "api" examples first.
This example demonstrates more advanced features of the digester.

== compiling and running

* to compile:
  mvn compile

* to build the jar artifact
  mvn package

* to run the examples:
  mvn verify -P run-uppercase
  mvn verify -P run-substitute
  mvn verify -P run-compound

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly.
