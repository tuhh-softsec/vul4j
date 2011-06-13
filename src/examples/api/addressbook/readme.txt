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
* how to create a digester instance
* how to parse a file
* how to use the "object create" rule to create java objects
* how to use the "set properties" rule (basic usage) to map xml attributes
  to java bean properties.
* how to use the "set next" rule to build trees of java objects.
* how to use the "call method rule" (basic usage)
* how to use the "call parameter rule" to process the text contained
  in a tag's body
* how to use the "call parameter rule" to process the contents of an 
  xml attribute.

== compiling and running

* to compile:
  mvn compile

* to build the jar artifact
  mvn package

* to run:
  mvn verify

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly.
