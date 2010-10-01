#
# Copyright 2006-2010 The Apache Software Foundation.
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

noinst_PROGRAMS = ${samples}
bin_PROGRAMS = ${tools}
LDADD = libxml-security-c.la

#
# The following are sample programs.  They are NOT installed
#

samples =

# HMAC sign

samples += simpleHMAC
simpleHMAC_SOURCES = \
  samples/simpleHMAC.cpp \
  samples/IOStreamOutputter.cpp \
  samples/IOStreamOutputter.hpp

# HMAC validate

samples += simpleValidate
simpleValidate_SOURCES = \
  samples/simpleValidate.cpp \
  samples/IOStreamOutputter.cpp \
  samples/IOStreamOutputter.hpp

# Encrypt

samples += simpleEncrypt
simpleEncrypt_SOURCES = \
  samples/simpleEncrypt.cpp \
  samples/IOStreamOutputter.cpp \
  samples/IOStreamOutputter.hpp

# Decrypt

samples += simpleDecrypt
simpleDecrypt_SOURCES = \
  samples/simpleDecrypt.cpp \
  samples/IOStreamOutputter.cpp \
  samples/IOStreamOutputter.hpp

#
# Finally we compile the tools that can be used to manipulate
# XML Security inputs and outputs
#

tools =

# xtest

tools += xtest
xtest_SOURCES = \
  tools/xtest/xtest.cpp

tools += c14n
c14n_SOURCES = \
  tools/c14n/c14n.cpp

tools += checksig
checksig_SOURCES = \
  tools/checksig/checksig.cpp \
  tools/checksig/AnonymousResolver.hpp \
  tools/checksig/AnonymousResolver.cpp \
  tools/checksig/InteropResolver.hpp \
  tools/checksig/InteropResolver.cpp

tools += templatesign
templatesign_SOURCES = \
  tools/templatesign/templatesign.cpp

tools += txfmout
txfmout_SOURCES = \
  tools/txfmout/txfmout.cpp

tools += siginf
siginf_SOURCES = \
  tools/siginf/siginf.cpp

tools += cipher
cipher_SOURCES = \
  tools/cipher/cipher.cpp \
  tools/cipher/XencInteropResolver.hpp \
  tools/cipher/XencInteropResolver.cpp

tools += xklient
xklient_SOURCES = \
  tools/xklient/xklient.cpp


lib_LTLIBRARIES = libxml-security-c.la

xsecincludedir = $(includedir)/xsec
encincludedir = $(includedir)/xsec/enc
xscryptincludedir = $(includedir)/xsec/enc/XSCrypt
opensslincludedir = $(includedir)/xsec/enc/OpenSSL
nssincludedir = $(includedir)/xsec/enc/NSS
dsigincludedir = $(includedir)/xsec/dsig
frameworkincludedir = $(includedir)/xsec/framework
transformersincludedir = $(includedir)/xsec/transformers
utilsincludedir = $(includedir)/xsec/utils
unixutilsincludedir = $(includedir)/xsec/utils/unixutils
xencincludedir = $(includedir)/xsec/xenc
xkmsincludedir = $(includedir)/xsec/xkms


# The required include files

#       A line like the following will generate a list of headers
#       for a given directory: (must be issued from base directory)
#
# find src/framework -name '*.hpp' -exec echo {} \\ ";" | sed "s/src/  xsec/g"

# Canon

xsecinclude_HEADERS = \
  canon/XSECXMLNSStack.hpp \
  canon/XSECCanon.hpp \
  canon/XSECC14n20010315.hpp

# enc

encinclude_HEADERS = \
  enc/XSECCryptoKeyHMAC.hpp \
  enc/XSECCryptoX509.hpp \
  enc/XSECCryptoKeyDSA.hpp \
  enc/XSECCryptoKeyEC.hpp \
  enc/XSECKeyInfoResolver.hpp \
  enc/XSECCryptoHash.hpp \
  enc/XSECCryptoBase64.hpp \
  enc/XSECCryptoSymmetricKey.hpp \
  enc/XSECCryptoKey.hpp \
  enc/XSECCryptoProvider.hpp \
  enc/XSECKeyInfoResolverDefault.hpp \
  enc/XSECCryptoKeyRSA.hpp \
  enc/XSECCryptoException.hpp \
  enc/XSECCryptoUtils.hpp

xscryptinclude_HEADERS = \
  enc/XSCrypt/XSCryptCryptoBase64.hpp

opensslinclude_HEADERS = \
  enc/OpenSSL/OpenSSLCryptoBase64.hpp \
  enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp \
  enc/OpenSSL/OpenSSLCryptoProvider.hpp \
  enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp \
  enc/OpenSSL/OpenSSLCryptoX509.hpp \
  enc/OpenSSL/OpenSSLCryptoHashHMAC.hpp \
  enc/OpenSSL/OpenSSLCryptoKeyDSA.hpp \
  enc/OpenSSL/OpenSSLCryptoKeyEC.hpp \
  enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp \
  enc/OpenSSL/OpenSSLCryptoHash.hpp 

nssinclude_HEADERS = \
  enc/NSS/NSSCryptoX509.hpp \
  enc/NSS/NSSCryptoHashHMAC.hpp \
  enc/NSS/NSSCryptoKeyDSA.hpp \
  enc/NSS/NSSCryptoKeyHMAC.hpp \
  enc/NSS/NSSCryptoHash.hpp \
  enc/NSS/NSSCryptoSymmetricKey.hpp \
  enc/NSS/NSSCryptoProvider.hpp \
  enc/NSS/NSSCryptoKeyRSA.hpp 

dsiginclude_HEADERS = \
  dsig/DSIGKeyInfoSPKIData.hpp \
  dsig/DSIGXPathHere.hpp \
  dsig/DSIGAlgorithmHandlerDefault.hpp \
  dsig/DSIGXPathFilterExpr.hpp \
  dsig/DSIGKeyInfoX509.hpp \
  dsig/DSIGKeyInfoList.hpp \
  dsig/DSIGKeyInfoValue.hpp \
  dsig/DSIGKeyInfoDEREncoded.hpp \
  dsig/DSIGTransformC14n.hpp \
  dsig/DSIGTransformXSL.hpp \
  dsig/DSIGKeyInfo.hpp \
  dsig/DSIGKeyInfoPGPData.hpp \
  dsig/DSIGKeyInfoExt.hpp \
  dsig/DSIGObject.hpp \
  dsig/DSIGTransformList.hpp \
  dsig/DSIGTransform.hpp \
  dsig/DSIGTransformBase64.hpp \
  dsig/DSIGTransformXPath.hpp \
  dsig/DSIGKeyInfoMgmtData.hpp \
  dsig/DSIGSignedInfo.hpp \
  dsig/DSIGTransformXPathFilter.hpp \
  dsig/DSIGReferenceList.hpp \
  dsig/DSIGReference.hpp \
  dsig/DSIGSignature.hpp \
  dsig/DSIGKeyInfoName.hpp \
  dsig/DSIGTransformEnvelope.hpp \
  dsig/DSIGConstants.hpp

frameworkinclude_HEADERS = \
  framework/XSECAlgorithmHandler.hpp \
  framework/XSECURIResolver.hpp \
  framework/XSECDefs.hpp \
  framework/XSECEnv.hpp \
  framework/XSECException.hpp \
  framework/XSECError.hpp \
  framework/XSECProvider.hpp \
  framework/XSECConfig.hpp \
  framework/XSECURIResolverXerces.hpp \
  framework/XSECAlgorithmMapper.hpp \
  framework/XSECW32Config.hpp \
  framework/XSECVersion.hpp

transformersinclude_HEADERS = \
  transformers/TXFMXPathFilter.hpp \
  transformers/TXFMSHA1.hpp \
  transformers/TXFMParser.hpp \
  transformers/TXFMOutputFile.hpp \
  transformers/TXFMURL.hpp \
  transformers/TXFMBase.hpp \
  transformers/TXFMCipher.hpp \
  transformers/TXFMEnvelope.hpp \
  transformers/TXFMChain.hpp \
  transformers/TXFMMD5.hpp \
  transformers/TXFMDocObject.hpp \
  transformers/TXFMConcatChains.hpp \
  transformers/TXFMSB.hpp \
  transformers/TXFMC14n.hpp \
  transformers/TXFMXSL.hpp \
  transformers/TXFMXPath.hpp \
  transformers/TXFMBase64.hpp

utilsinclude_HEADERS = \
  utils/XSECSafeBuffer.hpp \
  utils/XSECSOAPRequestor.hpp \
  utils/XSECTXFMInputSource.hpp \
  utils/XSECNameSpaceExpander.hpp \
  utils/XSECSOAPRequestorSimple.hpp \
  utils/XSECXPathNodeList.hpp \
  utils/XSECSafeBufferFormatter.hpp \
  utils/XSECDOMUtils.hpp \
  utils/XSECBinTXFMInputStream.hpp \
  utils/XSECPlatformUtils.hpp 

unixutilsinclude_HEADERS = \
  utils/unixutils/XSECURIResolverGenericUnix.hpp \
  utils/unixutils/XSECBinHTTPURIInputStream.hpp

xencinclude_HEADERS = \
  xenc/XENCEncryptionMethod.hpp \
  xenc/XENCEncryptedType.hpp \
  xenc/XENCCipherData.hpp \
  xenc/XENCEncryptedKey.hpp \
  xenc/XENCCipherValue.hpp \
  xenc/XENCEncryptedData.hpp \
  xenc/XENCCipherReference.hpp \
  xenc/XENCCipher.hpp

xkmsinclude_HEADERS = \
  xkms/XKMSNotBoundAuthentication.hpp \
  xkms/XKMSValidateResult.hpp \
  xkms/XKMSValidityInterval.hpp \
  xkms/XKMSStatusRequest.hpp \
  xkms/XKMSRegisterRequest.hpp \
  xkms/XKMSPendingRequest.hpp \
  xkms/XKMSCompoundRequest.hpp \
  xkms/XKMSUseKeyWith.hpp \
  xkms/XKMSValidateRequest.hpp \
  xkms/XKMSUnverifiedKeyBinding.hpp \
  xkms/XKMSLocateResult.hpp \
  xkms/XKMSRespondWith.hpp \
  xkms/XKMSQueryKeyBinding.hpp \
  xkms/XKMSPrototypeKeyBinding.hpp \
  xkms/XKMSKeyBindingAbstractType.hpp \
  xkms/XKMSMessageAbstractType.hpp \
  xkms/XKMSMessageFactory.hpp \
  xkms/XKMSConstants.hpp \
  xkms/XKMSRequestAbstractType.hpp \
  xkms/XKMSResult.hpp \
  xkms/XKMSAuthentication.hpp \
  xkms/XKMSLocateRequest.hpp \
  xkms/XKMSResultType.hpp \
  xkms/XKMSStatusResult.hpp \
  xkms/XKMSKeyBinding.hpp \
  xkms/XKMSCompoundResult.hpp \
  xkms/XKMSRegisterResult.hpp \
  xkms/XKMSResponseMechanism.hpp \
  xkms/XKMSStatus.hpp \
  xkms/XKMSRevokeRequest.hpp \
  xkms/XKMSRevokeResult.hpp \
  xkms/XKMSRecoverKeyBinding.hpp \
  xkms/XKMSRevokeKeyBinding.hpp \
  xkms/XKMSRSAKeyPair.hpp \
  xkms/XKMSRecoverResult.hpp \
  xkms/XKMSReissueResult.hpp \
  xkms/XKMSRecoverRequest.hpp \
  xkms/XKMSReissueRequest.hpp \
  xkms/XKMSReissueKeyBinding.hpp


libxml_security_c_la_SOURCES = ${lib_sources}

lib_sources =

# canon

lib_sources += ${canon_sources}

# Signature

lib_sources += ${dsig_sources}

# Main encryption sources

lib_sources += ${enc_sources}

# Framework sources

lib_sources += ${framework_sources}

# transformers

lib_sources += ${txfm_sources}

# Utility sources

lib_sources += ${utils_sources}

# XML Encryption

lib_sources += ${xenc_sources}

# XML Key Management

lib_sources += ${xkms_sources}

#
# The following blocks are conditionally added on the basis
# of selections made during the "configure" operation

if XSEC_AM_HAVE_OPENSSL
  lib_sources += ${openssl_sources}
endif

if XSEC_AM_HAVE_NSS
  lib_sources += ${nss_sources}
endif

# Sources are build up using a find command:
#
# find xsec/transformers/ -name '*.cpp' -exec echo {} \\ ";" | sed "s/xsec\//  ..\/xsec\//g"

# canon

canon_sources = \
  canon/XSECC14n20010315.cpp \
  canon/XSECXMLNSStack.cpp \
  canon/XSECCanon.cpp

# Signature

dsig_sources = \
  dsig/DSIGKeyInfoPGPData.cpp \
  dsig/DSIGReferenceList.cpp \
  dsig/DSIGKeyInfoValue.cpp \
  dsig/DSIGKeyInfoDEREncoded.cpp \
  dsig/DSIGXPathHere.cpp \
  dsig/DSIGAlgorithmHandlerDefault.cpp \
  dsig/DSIGXPathFilterExpr.cpp \
  dsig/DSIGKeyInfoMgmtData.cpp \
  dsig/DSIGTransformXPathFilter.cpp \
  dsig/DSIGSignedInfo.cpp \
  dsig/DSIGKeyInfoList.cpp \
  dsig/DSIGConstants.cpp \
  dsig/DSIGSignature.cpp \
  dsig/DSIGTransformXSL.cpp \
  dsig/DSIGObject.cpp \
  dsig/DSIGTransformXPath.cpp \
  dsig/DSIGTransformEnvelope.cpp \
  dsig/DSIGKeyInfoName.cpp \
  dsig/DSIGTransformBase64.cpp \
  dsig/DSIGReference.cpp \
  dsig/DSIGKeyInfoSPKIData.cpp \
  dsig/DSIGTransformList.cpp \
  dsig/DSIGKeyInfoX509.cpp \
  dsig/DSIGKeyInfoExt.cpp \
  dsig/DSIGTransform.cpp \
  dsig/DSIGTransformC14n.cpp

# Main Crypto interface routines

enc_sources = \
  enc/XSECCryptoX509.cpp \
  enc/XSECKeyInfoResolverDefault.cpp \
  enc/XSECCryptoUtils.cpp \
  enc/XSECCryptoBase64.cpp \
  enc/XSCrypt/XSCryptCryptoBase64.cpp \
  enc/XSECCryptoProvider.cpp \
  enc/XSECCryptoException.cpp

# Framework files

framework_sources = \
  framework/XSECError.cpp \
  framework/XSECAlgorithmMapper.cpp \
  framework/XSECEnv.cpp \
  framework/XSECProvider.cpp \
  framework/XSECException.cpp \
  framework/XSECURIResolverXerces.cpp

txfm_sources = \
  transformers/TXFMBase.cpp \
  transformers/TXFMChain.cpp \
  transformers/TXFMCipher.cpp \
  transformers/TXFMParser.cpp \
  transformers/TXFMSB.cpp \
  transformers/TXFMEnvelope.cpp \
  transformers/TXFMBase64.cpp \
  transformers/TXFMXPathFilter.cpp \
  transformers/TXFMSHA1.cpp \
  transformers/TXFMC14n.cpp \
  transformers/TXFMURL.cpp \
  transformers/TXFMOutputFile.cpp \
  transformers/TXFMXPath.cpp \
  transformers/TXFMXSL.cpp \
  transformers/TXFMDocObject.cpp \
  transformers/TXFMConcatChains.cpp \
  transformers/TXFMMD5.cpp

# Utility files.  Note we don't worry about checking
# if the UNIX stuff is necessary - we just assume that
# we are running on a *NIX system because compiling under
# make.  Cygwin compiles use the UNIX utilities, not windows

utils_sources = \
  utils/unixutils/XSECSOAPRequestorSimpleUnix.cpp \
  utils/unixutils/XSECURIResolverGenericUnix.cpp \
  utils/unixutils/XSECBinHTTPURIInputStream.cpp \
  utils/XSECBinTXFMInputStream.cpp \
  utils/XSECXPathNodeList.cpp \
  utils/XSECSafeBuffer.cpp \
  utils/XSECTXFMInputSource.cpp \
  utils/XSECDOMUtils.cpp \
  utils/XSECSafeBufferFormatter.cpp \
  utils/XSECSOAPRequestorSimple.cpp \
  utils/XSECNameSpaceExpander.cpp \
  utils/XSECPlatformUtils.cpp

# XML Encryption

xenc_sources = \
  ../src/xenc/impl/XENCCipherReferenceImpl.cpp \
  ../src/xenc/impl/XENCEncryptionMethodImpl.cpp \
  ../src/xenc/impl/XENCEncryptedKeyImpl.hpp \
  ../src/xenc/impl/XENCCipherValueImpl.cpp \
  ../src/xenc/impl/XENCCipherImpl.hpp \
  ../src/xenc/impl/XENCAlgorithmHandlerDefault.hpp \
  ../src/xenc/impl/XENCCipherDataImpl.hpp \
  ../src/xenc/impl/XENCEncryptionMethodImpl.hpp \
  ../src/xenc/impl/XENCAlgorithmHandlerDefault.cpp \
  ../src/xenc/impl/XENCEncryptedDataImpl.cpp \
  ../src/xenc/impl/XENCEncryptedTypeImpl.hpp \
  ../src/xenc/impl/XENCCipherDataImpl.cpp \
  ../src/xenc/impl/XENCEncryptedDataImpl.hpp \
  ../src/xenc/impl/XENCCipherValueImpl.hpp \
  ../src/xenc/impl/XENCEncryptedTypeImpl.cpp \
  ../src/xenc/impl/XENCCipherImpl.cpp \
  ../src/xenc/impl/XENCEncryptedKeyImpl.cpp \
  ../src/xenc/impl/XENCCipherReferenceImpl.hpp

# XML Key Management
xkms_sources = \
  ../src/xkms/impl/XKMSCompoundRequestImpl.cpp \
  ../src/xkms/impl/XKMSRevokeKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSRecoverRequestImpl.cpp \
  ../src/xkms/impl/XKMSRegisterResultImpl.cpp \
  ../src/xkms/impl/XKMSRecoverResultImpl.cpp \
  ../src/xkms/impl/XKMSValidateResultImpl.hpp \
  ../src/xkms/impl/XKMSRevokeResultImpl.hpp \
  ../src/xkms/impl/XKMSCompoundResultImpl.cpp \
  ../src/xkms/impl/XKMSRevokeKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSCompoundResultImpl.hpp \
  ../src/xkms/impl/XKMSUnverifiedKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSKeyBindingAbstractTypeImpl.cpp \
  ../src/xkms/impl/XKMSQueryKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSPrototypeKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSValidateResultImpl.cpp \
  ../src/xkms/impl/XKMSRSAKeyPairImpl.hpp \
  ../src/xkms/impl/XKMSAuthenticationImpl.cpp \
  ../src/xkms/impl/XKMSNotBoundAuthenticationImpl.hpp \
  ../src/xkms/impl/XKMSCompoundRequestImpl.hpp \
  ../src/xkms/impl/XKMSPendingRequestImpl.hpp \
  ../src/xkms/impl/XKMSStatusResultImpl.cpp \
  ../src/xkms/impl/XKMSStatusImpl.hpp \
  ../src/xkms/impl/XKMSRegisterRequestImpl.hpp \
  ../src/xkms/impl/XKMSReissueRequestImpl.cpp \
  ../src/xkms/impl/XKMSKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSUseKeyWithImpl.hpp \
  ../src/xkms/impl/XKMSRequestAbstractTypeImpl.cpp \
  ../src/xkms/impl/XKMSRespondWithImpl.cpp \
  ../src/xkms/impl/XKMSResponseMechanismImpl.hpp \
  ../src/xkms/impl/XKMSResultImpl.hpp \
  ../src/xkms/impl/XKMSReissueResultImpl.cpp \
  ../src/xkms/impl/XKMSAuthenticationImpl.hpp \
  ../src/xkms/impl/XKMSMessageAbstractTypeImpl.cpp \
  ../src/xkms/impl/XKMSRevokeResultImpl.cpp \
  ../src/xkms/impl/XKMSStatusResultImpl.hpp \
  ../src/xkms/impl/XKMSReissueKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSRespondWithImpl.hpp \
  ../src/xkms/impl/XKMSRevokeRequestImpl.cpp \
  ../src/xkms/impl/XKMSPendingRequestImpl.cpp \
  ../src/xkms/impl/XKMSRecoverResultImpl.hpp \
  ../src/xkms/impl/XKMSValidateRequestImpl.cpp \
  ../src/xkms/impl/XKMSStatusRequestImpl.hpp \
  ../src/xkms/impl/XKMSRecoverRequestImpl.hpp \
  ../src/xkms/impl/XKMSRecoverKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSRSAKeyPairImpl.cpp \
  ../src/xkms/impl/XKMSMessageAbstractTypeImpl.hpp \
  ../src/xkms/impl/XKMSUnverifiedKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSValidityIntervalImpl.hpp \
  ../src/xkms/impl/XKMSLocateResultImpl.hpp \
  ../src/xkms/impl/XKMSLocateRequestImpl.cpp \
  ../src/xkms/impl/XKMSResultTypeImpl.cpp \
  ../src/xkms/impl/XKMSRecoverKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSResultImpl.cpp \
  ../src/xkms/impl/XKMSValidityIntervalImpl.cpp \
  ../src/xkms/impl/XKMSRegisterRequestImpl.cpp \
  ../src/xkms/impl/XKMSReissueResultImpl.hpp \
  ../src/xkms/impl/XKMSStatusImpl.cpp \
  ../src/xkms/impl/XKMSReissueRequestImpl.hpp \
  ../src/xkms/impl/XKMSStatusRequestImpl.cpp \
  ../src/xkms/impl/XKMSReissueKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSValidateRequestImpl.hpp \
  ../src/xkms/impl/XKMSKeyBindingAbstractTypeImpl.hpp \
  ../src/xkms/impl/XKMSResultTypeImpl.hpp \
  ../src/xkms/impl/XKMSMessageFactoryImpl.cpp \
  ../src/xkms/impl/XKMSRevokeRequestImpl.hpp \
  ../src/xkms/impl/XKMSResponseMechanismImpl.cpp \
  ../src/xkms/impl/XKMSNotBoundAuthentication.cpp \
  ../src/xkms/impl/XKMSLocateRequestImpl.hpp \
  ../src/xkms/impl/XKMSLocateResultImpl.cpp \
  ../src/xkms/impl/XKMSRequestAbstractTypeImpl.hpp \
  ../src/xkms/impl/XKMSQueryKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSUseKeyWithImpl.cpp \
  ../src/xkms/impl/XKMSMessageFactoryImpl.hpp \
  ../src/xkms/impl/XKMSPrototypeKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSRegisterResultImpl.hpp
  
# Conditional crypto routines

openssl_sources = \
  enc/OpenSSL/OpenSSLCryptoHashHMAC.cpp \
  enc/OpenSSL/OpenSSLCryptoKeyRSA.cpp \
  enc/OpenSSL/OpenSSLCryptoHash.cpp \
  enc/OpenSSL/OpenSSLCryptoProvider.cpp \
  enc/OpenSSL/OpenSSLCryptoX509.cpp \
  enc/OpenSSL/OpenSSLCryptoBase64.cpp \
  enc/OpenSSL/OpenSSLCryptoKeyDSA.cpp \
  enc/OpenSSL/OpenSSLCryptoKeyEC.cpp \
  enc/OpenSSL/OpenSSLCryptoSymmetricKey.cpp \
  enc/OpenSSL/OpenSSLCryptoKeyHMAC.cpp
  
nss_sources = \
  enc/NSS/NSSCryptoX509.cpp \
  enc/NSS/NSSCryptoHashHMAC.cpp \
  enc/NSS/NSSCryptoHash.cpp \
  enc/NSS/NSSCryptoKeyDSA.cpp \
  enc/NSS/NSSCryptoProvider.cpp \
  enc/NSS/NSSCryptoSymmetricKey.cpp \
  enc/NSS/NSSCryptoKeyRSA.cpp \
  enc/NSS/NSSCryptoKeyHMAC.cpp

#
# Now the library specific build items
#
libxml_security_c_la_LDFLAGS = -version-info 16:0:0

install-exec-hook:
	for la in $(lib_LTLIBRARIES) ; do rm -f $(DESTDIR)$(libdir)/$$la ; done

EXTRA_DIST = \
  utils/winutils/XSECURIResolverGenericWin32.cpp \
  utils/winutils/XSECSOAPRequestorSimpleWin32.cpp \
  utils/winutils/XSECBinHTTPURIInputStream.cpp \
  utils/winutils/XSECURIResolverGenericWin32.hpp \
  utils/winutils/XSECBinHTTPURIInputStream.hpp \
  enc/WinCAPI/WinCAPICryptoProvider.cpp \
  enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp \
  enc/WinCAPI/WinCAPICryptoHash.cpp \
  enc/WinCAPI/WinCAPICryptoKeyDSA.cpp \
  enc/WinCAPI/WinCAPICryptoHashHMAC.hpp \
  enc/WinCAPI/WinCAPICryptoKeyDSA.hpp \
  enc/WinCAPI/WinCAPICryptoSymmetricKey.hpp \
  enc/WinCAPI/WinCAPICryptoX509.hpp \
  enc/WinCAPI/WinCAPICryptoKeyRSA.hpp \
  enc/WinCAPI/WinCAPICryptoX509.cpp \
  enc/WinCAPI/WinCAPICryptoHashHMAC.cpp \
  enc/WinCAPI/WinCAPICryptoHash.hpp \
  enc/WinCAPI/WinCAPICryptoKeyRSA.cpp \
  enc/WinCAPI/WinCAPICryptoProvider.hpp \
  enc/WinCAPI/WinCAPICryptoSymmetricKey.cpp \
  enc/WinCAPI/WinCAPICryptoKeyHMAC.cpp

	