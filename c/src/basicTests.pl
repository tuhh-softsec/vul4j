#
# The Apache Software License, Version 1.1
#
#
# Copyright (c) 1999 The Apache Software Foundation.  All rights 
# reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer. 
#
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in
#    the documentation and/or other materials provided with the
#    distribution.
#
# 3. The end-user documentation included with the redistribution,
#    if any, must include the following acknowledgment:  
#       "This product includes software developed by the
#        Apache Software Foundation (http://www.apache.org/)."
#    Alternately, this acknowledgment may appear in the software itself,
#    if and wherever such third-party acknowledgments normally appear.
#
# 4. The names "<WebSig>" and "Apache Software Foundation" must
#    not be used to endorse or promote products derived from this
#    software without prior written permission. For written 
#    permission, please contact apache@apache.org.
#
# 5. Products derived from this software may not be called "Apache",
#    nor may "Apache" appear in their name, without prior written
#    permission of the Apache Software Foundation.
#
# THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
# ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
# USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
# OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
# ====================================================================
#
# This software consists of voluntary contributions made by many
# individuals on behalf of the Apache Software Foundation and was
# originally based on software copyright (c) 2001, Institute for
# Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
# The development of this software was partly funded by the European 
# Commission in the <WebSig> project in the ISIS Programme. 
# For more information on the Apache Software Foundation, please see
# <http://www.apache.org/>.

# A perl based test script for running all the interop examples

# There can be a number of failures.
#
# 1. the file http://xmldsig.pothole.com/xml-stylesheet.txt no
#    longer exists.  I have it on a test http server to make these
#    tests work.
# 2. If you are not online to the Internet, the checks for documents
#    at www.w3.org will also fail.

require 5.000;
use strict;

# Counters

my $dsig_failure_count = 0;
my $dsig_pass_count = 0;
my $xenc_failure_count = 0;
my $xenc_pass_count = 0;

my $failure_count = 0;
my $pass_count = 0;

my $total_dsig_count = 0;
my $total_xenc_count = 0;

# Checksig setup
my $checksig_args = "-x";

# Program setup

my $checksig = "../bin/checksig";
my $cipher = "../bin/cipher";

# Directory setup
my $data_dir = "../../data";

# Digital Signature Tests

my $dsig_file = 0;
my $dsig_args = 1;

my @dsig_array=(

"at/iaik/ixsil/coreFeatures/signatures/anonymousReferenceSignature.xml,-a",
"at/iaik/ixsil/coreFeatures/signatures/manifestSignature.xml,",

# will fail if no network

"at/iaik/ixsil/coreFeatures/signatures/signatureTypesSignature.xml,",
"at/iaik/ixsil/signatureAlgorithms/signatures/dSASignature.xml,",
"at/iaik/ixsil/signatureAlgorithms/signatures/hMACShortSignature.xml,-h secret",
"at/iaik/ixsil/signatureAlgorithms/signatures/hMACSignature.xml,-h secret",
"at/iaik/ixsil/signatureAlgorithms/signatures/rSASignature.xml,",

"at/iaik/ixsil/transforms/signatures/base64DecodeSignature.xml,",
"at/iaik/ixsil/transforms/signatures/c14nSignature.xml,",
"at/iaik/ixsil/transforms/signatures/envelopedSignatureSignature.xml,",
"at/iaik/ixsil/transforms/signatures/xPathSignature.xml,",



"com/rsasecurity/bdournaee/certj201_enveloped.xml,",
"com/rsasecurity/bdournaee/certj201_enveloping.xml,",
"ie/baltimore/merlin-examples/ec-merlin-iaikTests-two/signature.xml,",
"ie/baltimore/merlin-examples/merlin-exc-c14n-one/exc-signature.xml,",

"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-keyname.xml,-i",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-retrievalmethod-rawx509crt.xml,-i",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-crt-crl.xml,-i",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-crt.xml,",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-is.xml,-i",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-ski.xml,-i",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-sn.xml,-i",



"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloped-dsa.xml,",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-b64-dsa.xml,",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-dsa.xml,",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-hmac-sha1-40.xml,-h secret",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-hmac-sha1.xml,-h secret",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-rsa.xml,",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-external-b64-dsa.xml,",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-external-dsa.xml,",

# These two are removed, as this is a pre-release syntax that the library
# does not understand

# testSig $data_dir/ie/baltimore/merlin-examples/merlin-xmldsig-filter2-one/sign-xfdl.xml
# testSig $data_dir/ie/baltimore/merlin-examples/merlin-xmldsig-filter2-one/signature.xml

"ie/baltimore/merlin-examples/merlin-xmldsig-sixteen/signature.xml,",
"interop/c14n/Y1/exc-signature.xml,",

# MD5 now implemented

"interop/c14n/Y2/signature-joseph-exc.xml,",
"interop/c14n/Y3/signature.xml,",
"interop/c14n/Y4/signature.xml,",
"interop/c14n/Y5/signature.xml,",
"interop/c14n/Y5/signatureCommented.xml,",

# XPath Filter

"interop/xfilter2/merlin-xpath-filter2-three/sign-spec.xml,",
"interop/xfilter2/merlin-xpath-filter2-three/sign-xfdl.xml,",

);

# XML Encryption Tests

my $xenc_result = 0;
my $xenc_file = 1;
my $xenc_args = 2;

my @xenc_array=(
#bad-encrypt-content-aes128-cbc-kw-aes192.xml
#decryption-transform-except.xml
#decryption-transform.xml
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes128-cbc-kw-aes192.xml,-i -de",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes256-cbc-prop.xml,-i -de",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-tripledes-cbc.xml,-i -de",

"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes128-cbc.xml,-i",
"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes192-cbc-kw-aes256.xml,-i",
"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes256-cbc-kw-tripledes.xml,-i",
"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-tripledes-cbc-rsa-oaep-mgf1p.xml,-i",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes128-cbc-rsa-1_5.xml,-i -de",

# CipherRef now supported

"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes192-cbc-ref.xml,-i -de",

"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes256-cbc-carried-kw-aes256.xml,-i -de",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes256-cbc-retrieved-kw-aes256.xml,-i -de",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-tripledes-cbc-kw-aes128.xml,-i -de",

# Unsupported Key-wraps
#encrypt-content-aes192-cbc-dh-sha512.xml
#encrypt-data-tripledes-cbc-rsa-oaep-mgf1p-sha256.xml
#encrypt-element-aes256-cbc-kw-aes256-dh-ripemd160.xml

# Don't yet support encrypted keysin signatures (or SHA-2/Ripemd)

#encsig-hmac-sha256-dh.xml
#encsig-hmac-sha256-kw-tripledes-dh.xml
#encsig-hmac-sha256-rsa-1_5.xml
#encsig-hmac-sha256-rsa-oaep-mgf1p.xml
#encsig-ripemd160-hmac-ripemd160-kw-tripledes.xml
#encsig-sha256-hmac-sha256-kw-aes128.xml
#encsig-sha384-hmac-sha384-kw-aes192.xml
#encsig-sha512-hmac-sha512-kw-aes256.xml

# Don't yet check for bad encryption

#bad-encrypt-content-aes128-cbc-kw-aes192.xml

# Don't yet support signature decryption transforms

#decryption-transform-except.xml
#decryption-transform.xml

);

# Run the signature tests

print "\n\n";
print "Running XML Digital Signature Interop Tests\n";
print "-------------------------------------------\n\n";

foreach (@dsig_array) {

  $total_dsig_count++;

  my @fields = split(/\,/, $_);
  my $file_name = $fields[$dsig_file];
  my $args = $fields[$dsig_args];

  my $result = `$checksig $checksig_args $args $data_dir/$file_name`;
  if ($? == 0) {
	print "$file_name OK\n";
	$dsig_pass_count++;
  }
  else {
	print "\nFAILURE\n";
	print "---------\n";
	print "\n$file_name failed.  \n\nMessage was \n\n$result\n\n";
	print "---------\n\n";
	$dsig_failure_count++;
  }

};

print "\n\n";
print "DSIG Tests complete\n\n";
print "Total Tests   = $total_dsig_count\n";
print "Number Passed = $dsig_pass_count\n";
print "Number Failed = $dsig_failure_count\n\n";
print "-------------------------------------------\n\n";

# Now run the encryption tests

print "\n\n";
print "Running XML Encryption Interop Tests\n";
print "------------------------------------\n\n";

foreach (@xenc_array) {

  $total_xenc_count++;

  my @fields = split(/\s*,\s*/, $_);
  my $expected_result = $fields[$xenc_result];
  my $file_name = $fields[$xenc_file];
  my $args = $fields[$xenc_args];

  my $result = `$cipher $args $data_dir/$file_name`;

  if ($? == 0 && $result =~ /$expected_result/) {
	print "$file_name OK\n";
	$xenc_pass_count++;
  }
  else {
	print "\nFAILURE\n";
	print "---------\n";
	print "\n$file_name failed.  \n\nOutut was \n\n$result\n\n";
	print "---------\n\n";
	$xenc_failure_count++;
  }

};

print "\n\n";
print "XENC Tests complete\n\n";
print "Total Tests   = $total_xenc_count\n";
print "Number Passed = $xenc_pass_count\n";
print "Number Failed = $xenc_failure_count\n\n";

my $total_count = $total_dsig_count + $total_xenc_count;
my $total_passed = $dsig_pass_count + $xenc_pass_count;
my $total_failed = $dsig_failure_count + $xenc_failure_count;

print "All tests complete.\n\n";
print "Total Tests  = $total_count\n";
print "Total Passed = $total_passed\n";
print "Total Failed = $total_failed\n\n";
