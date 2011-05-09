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

The subdirectories of this directory provide examples of how to use
the Apache Digester's java API.

With the API approach, java code is used to configure the digester with
a set of rules to execute when xml is processed. It is these rules that
determine how the input xml is mapped into a tree of java objects.

An alternative is to use the "xmlrules" digester extension, which allows
the digester rules to be configured via an xml file. This allows the
mapping between input xml and java objects to be modified without
recompilation of any source code.

The examples are graduated in the following order:

1. addressbook
2. catalog
3. dbinsert
4. document-markup
