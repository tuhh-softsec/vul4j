#
# Copyright 2006 The Apache Software Foundation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

AUTOMAKE_OPTIONS = foreign

SUBDIRS = include lib bin
ACLOCAL_AMFLAGS = -I m4

EXTRA_DIST = m4/acx_pthread.m4 Projects xml-security-c.spec \
    src/framework/resource.h \
    src/framework/version.rc \
    src/tools/threadTest/threadtest.cpp

dist-hook:
	rm -rf `find $(distdir)/Projects -name .svn`
