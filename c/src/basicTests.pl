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
my $dsig_skipped_count = 0;

my $xenc_failure_count = 0;
my $xenc_pass_count = 0;
my $xenc_skipped_count = 0;

my $failure_count = 0;
my $pass_count = 0;

my $total_dsig_count = 0;
my $total_xenc_count = 0;

# Checksig setup
my $checksig_args = "-x";

# Program setup

my $checksig;
my $cipher;

if ($^O =~ m/Win/) {
  $checksig = "../Build/Win32/VC6/Debug/checksig.exe";
  $cipher = "../Build/Win32/VC6/Debug/cipher.exe";
}
else {
  $checksig = "../bin/checksig";
  $cipher = "../bin/cipher";
}

# Directory setup
my $data_dir = "../../data";

# Digital Signature Tests

my $dsig_file = 0;
my $dsig_args = 1;
# Does this test require Xalan (y/n)
my $dsig_flag_xalan = 2;
# Does this test require pothole.com (y/n)
my $dsig_flag_pothole = 3;

# What components do we have available?
my $have_pothole = 1;
my $have_xalan = 1;

# What kind of test results should we expect?
# Without xalan, 13 tests will fail
my $no_xalan_failures = 13;
my $expected_failures = 0;

my @dsig_array=(

"at/iaik/ixsil/coreFeatures/signatures/anonymousReferenceSignature.xml,-a,n,n",
"at/iaik/ixsil/coreFeatures/signatures/manifestSignature.xml,,n,n",

# will fail if no network

"at/iaik/ixsil/coreFeatures/signatures/signatureTypesSignature.xml,,y,n",
"at/iaik/ixsil/signatureAlgorithms/signatures/dSASignature.xml,,n,n",
"at/iaik/ixsil/signatureAlgorithms/signatures/hMACShortSignature.xml,-h secret,n,n",
"at/iaik/ixsil/signatureAlgorithms/signatures/hMACSignature.xml,-h secret,n,n",
"at/iaik/ixsil/signatureAlgorithms/signatures/rSASignature.xml,,n,n",

"at/iaik/ixsil/transforms/signatures/base64DecodeSignature.xml,,n,n",
"at/iaik/ixsil/transforms/signatures/c14nSignature.xml,,n,n",
"at/iaik/ixsil/transforms/signatures/envelopedSignatureSignature.xml,,n,n",
"at/iaik/ixsil/transforms/signatures/xPathSignature.xml,,y,n",



"com/rsasecurity/bdournaee/certj201_enveloped.xml,,n,n",
"com/rsasecurity/bdournaee/certj201_enveloping.xml,,n,n",
"ie/baltimore/merlin-examples/ec-merlin-iaikTests-two/signature.xml,,y,n",
"ie/baltimore/merlin-examples/merlin-exc-c14n-one/exc-signature.xml,,n,n",

"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-keyname.xml,-i,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-retrievalmethod-rawx509crt.xml,-i,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-crt-crl.xml,-i,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-crt.xml,,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-is.xml,-i,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-ski.xml,-i,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/signature-x509-sn.xml,-i,n,n",



"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloped-dsa.xml,,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-b64-dsa.xml,,y,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-dsa.xml,,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-hmac-sha1-40.xml,-h secret,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-hmac-sha1.xml,-h secret,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-enveloping-rsa.xml,,n,n",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-external-b64-dsa.xml,,n,y",
"ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/signature-external-dsa.xml,,n,n",

# These two are removed, as this is a pre-release syntax that the library
# does not understand

# testSig $data_dir/ie/baltimore/merlin-examples/merlin-xmldsig-filter2-one/sign-xfdl.xml
# testSig $data_dir/ie/baltimore/merlin-examples/merlin-xmldsig-filter2-one/signature.xml

"ie/baltimore/merlin-examples/merlin-xmldsig-sixteen/signature.xml,,y,y",
"interop/c14n/Y1/exc-signature.xml,,n,n",

# MD5 now implemented

"interop/c14n/Y2/signature-joseph-exc.xml,,y,n",
"interop/c14n/Y3/signature.xml,,y,n",
"interop/c14n/Y4/signature.xml,,y,n",
"interop/c14n/Y5/signature.xml,,y,n",
"interop/c14n/Y5/signatureCommented.xml,,y,n",

# XPath Filter

"interop/xfilter2/merlin-xpath-filter2-three/sign-spec.xml,,y,n",
"interop/xfilter2/merlin-xpath-filter2-three/sign-xfdl.xml,,y,n",

);

# XML Encryption Tests

my $xenc_result = 0;
my $xenc_file = 1;
my $xenc_args = 2;
my $xenc_flag_xalan = 3;

my @xenc_array=(
#bad-encrypt-content-aes128-cbc-kw-aes192.xml
#decryption-transform-except.xml
#decryption-transform.xml
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes128-cbc-kw-aes192.xml,-i -de,n",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-aes256-cbc-prop.xml,-i -de,n",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-content-tripledes-cbc.xml,-i -de,n",

"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes128-cbc.xml,-i,n",
"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes192-cbc-kw-aes256.xml,-i,n",
"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-aes256-cbc-kw-tripledes.xml,-i,n",
"top secret message,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-data-tripledes-cbc-rsa-oaep-mgf1p.xml,-i,n",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes128-cbc-rsa-1_5.xml,-i -de,n",

# CipherRef now supported

"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes192-cbc-ref.xml,-i -de,y",

"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes256-cbc-carried-kw-aes256.xml,-i -de,n",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-aes256-cbc-retrieved-kw-aes256.xml,-i -de,n",
"<Number>1234 567890 12345</Number>,ie/baltimore/merlin-examples/merlin-xmlenc-five/encrypt-element-tripledes-cbc-kw-aes128.xml,-i -de,n",

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

# PHAOS interop tests.  Commented out tests are currently
# not supported
# bad-alg-enc-element-aes128-kw-3des.xml
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-content-3des-kw-aes192.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-content-aes128-kw-3des.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-content-aes192-kw-aes256.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-content-aes256-kt-rsa1_5.xml,-i -de,n",
# enc-element-3des-ka-dh.xml
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-3des-kt-rsa1_5.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-3des-kt-rsa_oaep_sha1.xml,-i -de,n",
# enc-element-3des-kt-rsa_oaep_sha256.xml
# enc-element-3des-kt-rsa_oaep_sha512.xml
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-3des-kw-3des.xml,-i -de,n",
# enc-element-aes128-ka-dh.xml
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes128-kt-rsa1_5.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes128-kt-rsa_oaep_sha1.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes128-kw-aes128.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes128-kw-aes256.xml,-i -de,n",
# enc-element-aes192-ka-dh.xml
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes192-kt-rsa_oaep_sha1.xml,-i -de,n",
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes192-kw-aes192.xml,-i -de,n",
# enc-element-aes256-ka-dh.xml
"<Number>4019 2445 0277 5567</Number>,com/phaos/phaos-xmlenc-3/enc-element-aes256-kw-aes256.xml,-i -de,n",
"4019 2445 0277 5567,com/phaos/phaos-xmlenc-3/enc-text-3des-kw-aes256.xml,-i,n",
"4019 2445 0277 5567,com/phaos/phaos-xmlenc-3/enc-text-aes128-kw-aes192.xml,-i,n",
"4019 2445 0277 5567,com/phaos/phaos-xmlenc-3/enc-text-aes192-kt-rsa1_5.xml,-i,n",
"4019 2445 0277 5567,com/phaos/phaos-xmlenc-3/enc-text-aes256-kt-rsa_oaep_sha1.xml,-i,n"

);

sub print_args {

  print STDERR "\nUsage: basicTests.pl [--noxalan] [--nopothole]\n\n";
  exit(1);

}

# Process command line options

foreach (@ARGV) {

 SWITCH: {
    if (/^--noxalan$/ || /^-x$/) {$have_xalan = 0; last SWITCH;}
    if (/^--nopothole$/ || /^-x$/) {$have_pothole = 0; last SWITCH;}
    print STDERR "Unknown command : " . $_ . "\n\n";
    print_args();
  }
}

# Run the signature tests

print "\n\n";
print "Running XML Digital Signature Interop Tests\n";
print "-------------------------------------------\n\n";

foreach (@dsig_array) {

  $total_dsig_count++;

  my @fields = split(/\,/, $_);
  my $file_name = $fields[$dsig_file];
  my $args = $fields[$dsig_args];
  my $xalan_flag = $fields[$dsig_flag_xalan];
  my $pothole_flag = $fields[$dsig_flag_pothole];

  if ((($xalan_flag eq "n") | $have_xalan) & (($pothole_flag eq 'n') || $have_pothole)) {

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
  } else {

	print "$file_name SKIPPED\n";
	$dsig_skipped_count++;

  }

};

print "\n\n";
print "DSIG Tests complete\n\n";
print "Total Tests    = $total_dsig_count\n";
print "Number Passed  = $dsig_pass_count\n";
print "Number Skipped = $dsig_skipped_count\n";
print "Number Failed  = $dsig_failure_count\n\n";
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
  my $xalan_flag = $fields[$xenc_flag_xalan];

  if (($xalan_flag eq "n") | $have_xalan) {

	my $result = `$cipher $args $data_dir/$file_name`;

	if ($? == 0 && $result =~ /$expected_result/) {
	  print "$file_name OK\n";
	  $xenc_pass_count++;
	}
	else {
	  print "\nFAILURE\n";
	  print "---------\n";
	  print "\n$file_name failed.  \n\nOutput was \n\n$result\n\n";
	  print "---------\n\n";
	  $xenc_failure_count++;
	}
  } else {

	print "$file_name SKIPPED\n";
	$xenc_skipped_count++;
  }	

};

print "\n\n";
print "XENC Tests complete\n\n";
print "Total Tests    = $total_xenc_count\n";
print "Number Passed  = $xenc_pass_count\n";
print "Number Skipped = $xenc_skipped_count\n";
print "Number Failed  = $xenc_failure_count\n\n";

my $total_count = $total_dsig_count + $total_xenc_count;
my $total_passed = $dsig_pass_count + $xenc_pass_count;
my $total_failed = $dsig_failure_count + $xenc_failure_count;
my $total_skipped = $dsig_skipped_count + $xenc_skipped_count;

print "All tests complete.\n\n";
print "Total Tests   = $total_count\n";
print "Total Passed  = $total_passed\n";
print "Total Skipped = $total_skipped\n";
print "Total Failed  = $total_failed\n\n";

# Now calculate error code
exit ($total_failed);


