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
the Apache Digester to parse "document-markup" style xml. It also serves as an
example of how to subclass the main Digester class in order to extend
its functionality.

By "document-markup" xml, we mean input like XHTML, where the data is valid
xml and where some elements contain interleaved text and child elements.

For example, "<p>Hi, <i>this</i> is some <b>document-style</b> xml.</p>"

Topics covered:
* how to subclass digester
* how to process markup-style xml.

== compiling and running

* to compile:
  mvn compile

* to build the jar artifact
  mvn package

* to run:
  mvn verify

Alternatively, you can set up your CLASSPATH appropriately, and
run the example directly.

== Notes

The primary use of the Digester is to process xml configuration files.
Such files do not typically interleave text and child elements in the
style encountered with document markup. The standard Digester behaviour is 
therefore to accumulate all text within an xml element's body (of which there is
expected to be only one "segment") and present it to a Rule or user method
as a single string.

While this significantly simplifies the implementation of Rule classes for
the primary Digester goal of parsing configuration files, this process of
simplifying all text within an element into a single string "loses" critical
information necessary to correctly parse "document-markup" xml.

This example shows one method of extending the Digester class to resolve
this issue..

At some time the ability to process "document-markup" style xml may be built 
into the standard Digester class.
