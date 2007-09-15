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

lib_LTLIBRARIES = libxml-security-c.la

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

# Sources are build up using a find command:
#
# find src/transformers/ -name '*.cpp' -exec echo {} \\ ";" | sed "s/src\//  ..\/src\//g"

# canon

canon_sources = \
  ../src/canon/XSECCannon.cpp \
  ../src/canon/XSECC14n20010315.cpp \
  ../src/canon/XSECXMLNSStack.cpp

# Signature

dsig_sources = \
  ../src/dsig/DSIGXPathFilterExpr.cpp \
  ../src/dsig/DSIGXPathHere.cpp \
  ../src/dsig/DSIGAlgorithmHandlerDefault.cpp \
  ../src/dsig/DSIGKeyInfoX509.cpp \
  ../src/dsig/DSIGKeyInfoList.cpp \
  ../src/dsig/DSIGKeyInfoValue.cpp \
  ../src/dsig/DSIGTransformC14n.cpp \
  ../src/dsig/DSIGTransformXSL.cpp \
  ../src/dsig/DSIGKeyInfoPGPData.cpp \
  ../src/dsig/DSIGObject.cpp \
  ../src/dsig/DSIGTransformList.cpp \
  ../src/dsig/DSIGTransform.cpp \
  ../src/dsig/DSIGTransformBase64.cpp \
  ../src/dsig/DSIGTransformXPath.cpp \
  ../src/dsig/DSIGKeyInfoMgmtData.cpp \
  ../src/dsig/DSIGSignedInfo.cpp \
  ../src/dsig/DSIGTransformXPathFilter.cpp \
  ../src/dsig/DSIGReferenceList.cpp \
  ../src/dsig/DSIGReference.cpp \
  ../src/dsig/DSIGSignature.cpp \
  ../src/dsig/DSIGKeyInfoName.cpp \
  ../src/dsig/DSIGTransformEnvelope.cpp \
  ../src/dsig/DSIGConstants.cpp \
  ../src/dsig/DSIGKeyInfoSPKIData.cpp

# Main Crypto interface routines

enc_sources = \
  ../src/enc/XSCrypt/XSCryptCryptoBase64.cpp \
  ../src/enc/XSECCryptoBase64.cpp \
  ../src/enc/XSECKeyInfoResolverDefault.cpp \
  ../src/enc/XSECCryptoException.cpp \
  ../src/enc/XSECCryptoUtils.cpp \
  ../src/enc/XSECCryptoX509.cpp

# Framework files

framework_sources = \
  ../src/framework/XSECEnv.cpp \
  ../src/framework/XSECError.cpp \
  ../src/framework/XSECException.cpp \
  ../src/framework/XSECProvider.cpp \
  ../src/framework/XSECURIResolverXerces.cpp \
  ../src/framework/XSECAlgorithmMapper.cpp

txfm_sources = \
  ../src/transformers/TXFMOutputFile.cpp \
  ../src/transformers/TXFMURL.cpp \
  ../src/transformers/TXFMBase.cpp \
  ../src/transformers/TXFMCipher.cpp \
  ../src/transformers/TXFMEnvelope.cpp \
  ../src/transformers/TXFMChain.cpp \
  ../src/transformers/TXFMMD5.cpp \
  ../src/transformers/TXFMDocObject.cpp \
  ../src/transformers/TXFMConcatChains.cpp \
  ../src/transformers/TXFMSB.cpp \
  ../src/transformers/TXFMC14n.cpp \
  ../src/transformers/TXFMXSL.cpp \
  ../src/transformers/TXFMXPath.cpp \
  ../src/transformers/TXFMBase64.cpp \
  ../src/transformers/TXFMXPathFilter.cpp \
  ../src/transformers/TXFMSHA1.cpp \
  ../src/transformers/TXFMParser.cpp

# Utility files.  Note we don't worry about checking
# if the UNIX stuff is necessary - we just assume that
# we are running on a *NIX system because compiling under
# make.  Cygwin compiles use the UNIX utilities, not windows

utils_sources = \
  ../src/utils/unixutils/XSECURIResolverGenericUnix.cpp \
  ../src/utils/unixutils/XSECBinHTTPURIInputStream.cpp \
  ../src/utils/unixutils/XSECSOAPRequestorSimpleUnix.cpp \
  ../src/utils/XSECTXFMInputSource.cpp \
  ../src/utils/XSECNameSpaceExpander.cpp \
  ../src/utils/XSECSOAPRequestorSimple.cpp \
  ../src/utils/XSECXPathNodeList.cpp \
  ../src/utils/XSECSafeBufferFormatter.cpp \
  ../src/utils/XSECDOMUtils.cpp \
  ../src/utils/XSECBinTXFMInputStream.cpp \
  ../src/utils/XSECPlatformUtils.cpp \
  ../src/utils/XSECSafeBuffer.cpp

# XML Encryption

xenc_sources = \
  ../src/xenc/impl/XENCAlgorithmHandlerDefault.hpp \
  ../src/xenc/impl/XENCAlgorithmHandlerDefault.cpp \
  ../src/xenc/impl/XENCCipherDataImpl.hpp \
  ../src/xenc/impl/XENCCipherDataImpl.cpp \
  ../src/xenc/impl/XENCCipherImpl.hpp \
  ../src/xenc/impl/XENCCipherImpl.cpp \
  ../src/xenc/impl/XENCCipherReferenceImpl.hpp \
  ../src/xenc/impl/XENCCipherReferenceImpl.cpp \
  ../src/xenc/impl/XENCCipherValueImpl.hpp \
  ../src/xenc/impl/XENCCipherValueImpl.cpp \
  ../src/xenc/impl/XENCEncryptedDataImpl.hpp \
  ../src/xenc/impl/XENCEncryptedDataImpl.cpp \
  ../src/xenc/impl/XENCEncryptedKeyImpl.hpp \
  ../src/xenc/impl/XENCEncryptedKeyImpl.cpp \
  ../src/xenc/impl/XENCEncryptedTypeImpl.hpp \
  ../src/xenc/impl/XENCEncryptedTypeImpl.cpp \
  ../src/xenc/impl/XENCEncryptionMethodImpl.hpp \
  ../src/xenc/impl/XENCEncryptionMethodImpl.cpp

# XML Key Management
xkms_sources = \
  ../src/xkms/impl/XKMSAuthenticationImpl.hpp \
  ../src/xkms/impl/XKMSAuthenticationImpl.cpp \
  ../src/xkms/impl/XKMSCompoundRequestImpl.hpp \
  ../src/xkms/impl/XKMSCompoundRequestImpl.cpp \
  ../src/xkms/impl/XKMSCompoundResultImpl.hpp \
  ../src/xkms/impl/XKMSCompoundResultImpl.cpp \
  ../src/xkms/impl/XKMSKeyBindingAbstractTypeImpl.hpp \
  ../src/xkms/impl/XKMSKeyBindingAbstractTypeImpl.cpp \
  ../src/xkms/impl/XKMSKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSLocateRequestImpl.hpp \
  ../src/xkms/impl/XKMSLocateRequestImpl.cpp \
  ../src/xkms/impl/XKMSLocateResultImpl.hpp \
  ../src/xkms/impl/XKMSLocateResultImpl.cpp \
  ../src/xkms/impl/XKMSMessageAbstractTypeImpl.hpp \
  ../src/xkms/impl/XKMSMessageAbstractTypeImpl.cpp \
  ../src/xkms/impl/XKMSMessageFactoryImpl.hpp \
  ../src/xkms/impl/XKMSMessageFactoryImpl.cpp \
  ../src/xkms/impl/XKMSNotBoundAuthentication.hpp \
  ../src/xkms/impl/XKMSNotBoundAuthentication.cpp \
  ../src/xkms/impl/XKMSPendingRequestImpl.hpp \
  ../src/xkms/impl/XKMSPendingRequestImpl.cpp \
  ../src/xkms/impl/XKMSPrototypeKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSPrototypeKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSQueryKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSQueryKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSRSAKeyPairImpl.hpp \
  ../src/xkms/impl/XKMSRSAKeyPairImpl.cpp \
  ../src/xkms/impl/XKMSRecoverKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSRecoverKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSRecoverRequestImpl.hpp \
  ../src/xkms/impl/XKMSRecoverRequestImpl.cpp \
  ../src/xkms/impl/XKMSRecoverResultImpl.hpp \
  ../src/xkms/impl/XKMSRecoverResultImpl.cpp \
  ../src/xkms/impl/XKMSRegisterRequestImpl.hpp \
  ../src/xkms/impl/XKMSRegisterRequestImpl.cpp \
  ../src/xkms/impl/XKMSRegisterResultImpl.hpp \
  ../src/xkms/impl/XKMSRegisterResultImpl.cpp \
  ../src/xkms/impl/XKMSReissueKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSReissueKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSReissueRequestImpl.hpp \
  ../src/xkms/impl/XKMSReissueRequestImpl.cpp \
  ../src/xkms/impl/XKMSReissueResultImpl.hpp \
  ../src/xkms/impl/XKMSReissueResultImpl.cpp \
  ../src/xkms/impl/XKMSRequestAbstractTypeImpl.hpp \
  ../src/xkms/impl/XKMSRequestAbstractTypeImpl.cpp \
  ../src/xkms/impl/XKMSRespondWithImpl.hpp \
  ../src/xkms/impl/XKMSRespondWithImpl.cpp \
  ../src/xkms/impl/XKMSResponseMechanismImpl.hpp \
  ../src/xkms/impl/XKMSResponseMechanismImpl.cpp \
  ../src/xkms/impl/XKMSResultImpl.hpp \
  ../src/xkms/impl/XKMSResultImpl.cpp \
  ../src/xkms/impl/XKMSResultTypeImpl.hpp \
  ../src/xkms/impl/XKMSResultTypeImpl.cpp \
  ../src/xkms/impl/XKMSRevokeKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSRevokeKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSRevokeRequestImpl.hpp \
  ../src/xkms/impl/XKMSRevokeRequestImpl.cpp \
  ../src/xkms/impl/XKMSRevokeResultImpl.hpp \
  ../src/xkms/impl/XKMSRevokeResultImpl.cpp \
  ../src/xkms/impl/XKMSStatusImpl.hpp \
  ../src/xkms/impl/XKMSStatusImpl.cpp \
  ../src/xkms/impl/XKMSStatusRequestImpl.hpp \
  ../src/xkms/impl/XKMSStatusRequestImpl.cpp \
  ../src/xkms/impl/XKMSStatusResultImpl.hpp \
  ../src/xkms/impl/XKMSStatusResultImpl.cpp \
  ../src/xkms/impl/XKMSUnverifiedKeyBindingImpl.hpp \
  ../src/xkms/impl/XKMSUnverifiedKeyBindingImpl.cpp \
  ../src/xkms/impl/XKMSUseKeyWithImpl.hpp \
  ../src/xkms/impl/XKMSUseKeyWithImpl.cpp \
  ../src/xkms/impl/XKMSValidateRequestImpl.hpp \
  ../src/xkms/impl/XKMSValidateRequestImpl.cpp \
  ../src/xkms/impl/XKMSValidateResultImpl.hpp \
  ../src/xkms/impl/XKMSValidateResultImpl.cpp \
  ../src/xkms/impl/XKMSValidityIntervalImpl.hpp \
  ../src/xkms/impl/XKMSValidityIntervalImpl.cpp \
  ../src/xkms/XKMSConstants.cpp

# Conditional crypto routines

openssl_sources = \
  ../src/enc/OpenSSL/OpenSSLCryptoProvider.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyRSA.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoX509.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoHashHMAC.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyDSA.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyHMAC.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoHash.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoBase64.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoSymmetricKey.cpp

#
# Now the library specific build items
#

INCLUDES = -I$(top_srcdir)/include
libxml_security_c_la_LDFLAGS = \
  -version-info ${package_libtool_version}
