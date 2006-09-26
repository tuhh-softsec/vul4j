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

# This makefile builds the samples and tools

noinst_PROGRAMS = ${samples}
bin_PROGRAMS = ${tools}

LDADD = ../lib/libxml-security-c.la

AM_CPPFLAGS = -I${top_srcdir}/include

#
# The following are sample programs.  They are NOT installed
#

samples =

# HMAC sign

samples += simpleHMAC
simpleHMAC_SOURCES = \
  ../src/samples/simpleHMAC.cpp \
  ../src/samples/IOStreamOutputter.cpp

# HMAC validate

samples += simpleValidate
simpleValidate_SOURCES = \
  ../src/samples/simpleValidate.cpp \
  ../src/samples/IOStreamOutputter.cpp

# Encrypt

samples += simpleEncrypt
simpleEncrypt_SOURCES = \
  ../src/samples/simpleEncrypt.cpp \
  ../src/samples/IOStreamOutputter.cpp

# Decrypt

samples += simpleDecrypt
simpleDecrypt_SOURCES = \
  ../src/samples/simpleDecrypt.cpp \
  ../src/samples/IOStreamOutputter.cpp

#
# Finally we compile the tools that can be used to manipulate
# XML Security inputs and outputs
#

tools =

# xtest

tools += xtest
xtest_SOURCES = \
  ../src/tools/xtest/xtest.cpp

tools += c14n
c14n_SOURCES = \
  ../src/tools/c14n/c14n.cpp

tools += checksig
checksig_SOURCES = \
  ../src/tools/checksig/checksig.cpp \
  ../src/tools/checksig/AnonymousResolver.cpp \
  ../src/tools/checksig/InteropResolver.cpp

tools += templatesign
templatesign_SOURCES = \
  ../src/tools/templatesign/templatesign.cpp

tools += txfmout
txfmout_SOURCES = \
  ../src/tools/txfmout/txfmout.cpp

tools += siginf
siginf_SOURCES = \
  ../src/tools/siginf/siginf.cpp

tools += cipher
cipher_SOURCES = \
  ../src/tools/cipher/cipher.cpp \
  ../src/tools/cipher/XencInteropResolver.cpp

tools += xklient
xklient_SOURCES = \
  ../src/tools/xklient/xklient.cpp

