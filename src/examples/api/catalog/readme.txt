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
the Apache Digester's basic functionality via its java interface.

Topics covered:
* how to read xml from a string (instead of a file)
* how to use Digester.getRoot() to retrieve the "root" object
  created when parsing an input file.
* how to use the "factory create" rule to create java objects which
  do not have default (no-argument) constructors.
* how to use the "set properties" rule (advanced usage) to map xml attributes
  to java bean properties with names different from the xml attribute name.
* how to use the SetPropertyRule.
* how to use the ObjectParamRule to pass a constant string to a method.


If you haven't read the "addressbook" example, it is recommended that
you start there first. This example demonstrates more advanced features
of the digester.

== compiling and running

* to compile:
  mvn compile

* to build the jar artifact
  mvn package

* to run:
  mvn verify

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly.
