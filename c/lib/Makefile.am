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
  xsec/canon/XSECXMLNSStack.hpp \
  xsec/canon/XSECCanon.hpp \
  xsec/canon/XSECC14n20010315.hpp

# enc

encinclude_HEADERS = \
  xsec/enc/XSECCryptoKeyHMAC.hpp \
  xsec/enc/XSECCryptoX509.hpp \
  xsec/enc/XSECCryptoKeyDSA.hpp \
  xsec/enc/XSECCryptoKeyEC.hpp \
  xsec/enc/XSECKeyInfoResolver.hpp \
  xsec/enc/XSECCryptoHash.hpp \
  xsec/enc/XSECCryptoBase64.hpp \
  xsec/enc/XSECCryptoSymmetricKey.hpp \
  xsec/enc/XSECCryptoKey.hpp \
  xsec/enc/XSECCryptoProvider.hpp \
  xsec/enc/XSECKeyInfoResolverDefault.hpp \
  xsec/enc/XSECCryptoKeyRSA.hpp \
  xsec/enc/XSECCryptoException.hpp \
  xsec/enc/XSECCryptoUtils.hpp

xscryptinclude_HEADERS = \
  xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp

opensslinclude_HEADERS = \
  xsec/enc/OpenSSL/OpenSSLCryptoBase64.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoProvider.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoX509.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoHashHMAC.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoKeyDSA.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoKeyEC.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp \
  xsec/enc/OpenSSL/OpenSSLCryptoHash.hpp 

nssinclude_HEADERS = \
  xsec/enc/NSS/NSSCryptoX509.hpp \
  xsec/enc/NSS/NSSCryptoHashHMAC.hpp \
  xsec/enc/NSS/NSSCryptoKeyDSA.hpp \
  xsec/enc/NSS/NSSCryptoKeyHMAC.hpp \
  xsec/enc/NSS/NSSCryptoHash.hpp \
  xsec/enc/NSS/NSSCryptoSymmetricKey.hpp \
  xsec/enc/NSS/NSSCryptoProvider.hpp \
  xsec/enc/NSS/NSSCryptoKeyRSA.hpp 

dsiginclude_HEADERS = \
  xsec/dsig/DSIGKeyInfoSPKIData.hpp \
  xsec/dsig/DSIGXPathHere.hpp \
  xsec/dsig/DSIGAlgorithmHandlerDefault.hpp \
  xsec/dsig/DSIGXPathFilterExpr.hpp \
  xsec/dsig/DSIGKeyInfoX509.hpp \
  xsec/dsig/DSIGKeyInfoList.hpp \
  xsec/dsig/DSIGKeyInfoValue.hpp \
  xsec/dsig/DSIGKeyInfoDEREncoded.hpp \
  xsec/dsig/DSIGTransformC14n.hpp \
  xsec/dsig/DSIGTransformXSL.hpp \
  xsec/dsig/DSIGKeyInfo.hpp \
  xsec/dsig/DSIGKeyInfoPGPData.hpp \
  xsec/dsig/DSIGKeyInfoExt.hpp \
  xsec/dsig/DSIGObject.hpp \
  xsec/dsig/DSIGTransformList.hpp \
  xsec/dsig/DSIGTransform.hpp \
  xsec/dsig/DSIGTransformBase64.hpp \
  xsec/dsig/DSIGTransformXPath.hpp \
  xsec/dsig/DSIGKeyInfoMgmtData.hpp \
  xsec/dsig/DSIGSignedInfo.hpp \
  xsec/dsig/DSIGTransformXPathFilter.hpp \
  xsec/dsig/DSIGReferenceList.hpp \
  xsec/dsig/DSIGReference.hpp \
  xsec/dsig/DSIGSignature.hpp \
  xsec/dsig/DSIGKeyInfoName.hpp \
  xsec/dsig/DSIGTransformEnvelope.hpp \
  xsec/dsig/DSIGConstants.hpp

frameworkinclude_HEADERS = \
  xsec/framework/XSECAlgorithmHandler.hpp \
  xsec/framework/XSECURIResolver.hpp \
  xsec/framework/XSECDefs.hpp \
  xsec/framework/XSECEnv.hpp \
  xsec/framework/XSECException.hpp \
  xsec/framework/XSECError.hpp \
  xsec/framework/XSECProvider.hpp \
  xsec/framework/XSECConfig.hpp \
  xsec/framework/XSECURIResolverXerces.hpp \
  xsec/framework/XSECAlgorithmMapper.hpp \
  xsec/framework/XSECW32Config.hpp \
  xsec/framework/XSECVersion.hpp

transformersinclude_HEADERS = \
  xsec/transformers/TXFMXPathFilter.hpp \
  xsec/transformers/TXFMSHA1.hpp \
  xsec/transformers/TXFMParser.hpp \
  xsec/transformers/TXFMOutputFile.hpp \
  xsec/transformers/TXFMURL.hpp \
  xsec/transformers/TXFMBase.hpp \
  xsec/transformers/TXFMCipher.hpp \
  xsec/transformers/TXFMEnvelope.hpp \
  xsec/transformers/TXFMChain.hpp \
  xsec/transformers/TXFMMD5.hpp \
  xsec/transformers/TXFMDocObject.hpp \
  xsec/transformers/TXFMConcatChains.hpp \
  xsec/transformers/TXFMSB.hpp \
  xsec/transformers/TXFMC14n.hpp \
  xsec/transformers/TXFMXSL.hpp \
  xsec/transformers/TXFMXPath.hpp \
  xsec/transformers/TXFMBase64.hpp

utilsinclude_HEADERS = \
  xsec/utils/XSECSafeBuffer.hpp \
  xsec/utils/XSECSOAPRequestor.hpp \
  xsec/utils/XSECTXFMInputSource.hpp \
  xsec/utils/XSECNameSpaceExpander.hpp \
  xsec/utils/XSECSOAPRequestorSimple.hpp \
  xsec/utils/XSECXPathNodeList.hpp \
  xsec/utils/XSECSafeBufferFormatter.hpp \
  xsec/utils/XSECDOMUtils.hpp \
  xsec/utils/XSECBinTXFMInputStream.hpp \
  xsec/utils/XSECPlatformUtils.hpp 

unixutilsinclude_HEADERS = \
  xsec/utils/unixutils/XSECURIResolverGenericUnix.hpp \
  xsec/utils/unixutils/XSECBinHTTPURIInputStream.hpp \

xencinclude_HEADERS = \
  xsec/xenc/XENCEncryptionMethod.hpp \
  xsec/xenc/XENCEncryptedType.hpp \
  xsec/xenc/XENCCipherData.hpp \
  xsec/xenc/XENCEncryptedKey.hpp \
  xsec/xenc/XENCCipherValue.hpp \
  xsec/xenc/XENCEncryptedData.hpp \
  xsec/xenc/XENCCipherReference.hpp \
  xsec/xenc/XENCCipher.hpp

xkmsinclude_HEADERS = \
  xsec/xkms/XKMSNotBoundAuthentication.hpp \
  xsec/xkms/XKMSValidateResult.hpp \
  xsec/xkms/XKMSValidityInterval.hpp \
  xsec/xkms/XKMSStatusRequest.hpp \
  xsec/xkms/XKMSRegisterRequest.hpp \
  xsec/xkms/XKMSPendingRequest.hpp \
  xsec/xkms/XKMSCompoundRequest.hpp \
  xsec/xkms/XKMSUseKeyWith.hpp \
  xsec/xkms/XKMSValidateRequest.hpp \
  xsec/xkms/XKMSUnverifiedKeyBinding.hpp \
  xsec/xkms/XKMSLocateResult.hpp \
  xsec/xkms/XKMSRespondWith.hpp \
  xsec/xkms/XKMSQueryKeyBinding.hpp \
  xsec/xkms/XKMSPrototypeKeyBinding.hpp \
  xsec/xkms/XKMSKeyBindingAbstractType.hpp \
  xsec/xkms/XKMSMessageAbstractType.hpp \
  xsec/xkms/XKMSMessageFactory.hpp \
  xsec/xkms/XKMSConstants.hpp \
  xsec/xkms/XKMSRequestAbstractType.hpp \
  xsec/xkms/XKMSResult.hpp \
  xsec/xkms/XKMSAuthentication.hpp \
  xsec/xkms/XKMSLocateRequest.hpp \
  xsec/xkms/XKMSResultType.hpp \
  xsec/xkms/XKMSStatusResult.hpp \
  xsec/xkms/XKMSKeyBinding.hpp \
  xsec/xkms/XKMSCompoundResult.hpp \
  xsec/xkms/XKMSRegisterResult.hpp \
  xsec/xkms/XKMSResponseMechanism.hpp \
  xsec/xkms/XKMSStatus.hpp \
  xsec/xkms/XKMSRevokeRequest.hpp \
  xsec/xkms/XKMSRevokeResult.hpp \
  xsec/xkms/XKMSRecoverKeyBinding.hpp \
  xsec/xkms/XKMSRevokeKeyBinding.hpp \
  xsec/xkms/XKMSRSAKeyPair.hpp \
  xsec/xkms/XKMSRecoverResult.hpp \
  xsec/xkms/XKMSReissueResult.hpp \
  xsec/xkms/XKMSRecoverRequest.hpp \
  xsec/xkms/XKMSReissueRequest.hpp \
  xsec/xkms/XKMSReissueKeyBinding.hpp


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
  ../xsec/canon/XSECC14n20010315.cpp \
  ../xsec/canon/XSECXMLNSStack.cpp \
  ../xsec/canon/XSECCanon.cpp

# Signature

dsig_sources = \
  ../xsec/dsig/DSIGKeyInfoPGPData.cpp \
  ../xsec/dsig/DSIGReferenceList.cpp \
  ../xsec/dsig/DSIGKeyInfoValue.cpp \
  ../xsec/dsig/DSIGKeyInfoDEREncoded.cpp \
  ../xsec/dsig/DSIGXPathHere.cpp \
  ../xsec/dsig/DSIGAlgorithmHandlerDefault.cpp \
  ../xsec/dsig/DSIGXPathFilterExpr.cpp \
  ../xsec/dsig/DSIGKeyInfoMgmtData.cpp \
  ../xsec/dsig/DSIGTransformXPathFilter.cpp \
  ../xsec/dsig/DSIGSignedInfo.cpp \
  ../xsec/dsig/DSIGKeyInfoList.cpp \
  ../xsec/dsig/DSIGConstants.cpp \
  ../xsec/dsig/DSIGSignature.cpp \
  ../xsec/dsig/DSIGTransformXSL.cpp \
  ../xsec/dsig/DSIGObject.cpp \
  ../xsec/dsig/DSIGTransformXPath.cpp \
  ../xsec/dsig/DSIGTransformEnvelope.cpp \
  ../xsec/dsig/DSIGKeyInfoName.cpp \
  ../xsec/dsig/DSIGTransformBase64.cpp \
  ../xsec/dsig/DSIGReference.cpp \
  ../xsec/dsig/DSIGKeyInfoSPKIData.cpp \
  ../xsec/dsig/DSIGTransformList.cpp \
  ../xsec/dsig/DSIGKeyInfoX509.cpp \
  ../xsec/dsig/DSIGKeyInfoExt.cpp \
  ../xsec/dsig/DSIGTransform.cpp \
  ../xsec/dsig/DSIGTransformC14n.cpp

# Main Crypto interface routines

enc_sources = \
  ../xsec/enc/XSECCryptoX509.cpp \
  ../xsec/enc/XSECKeyInfoResolverDefault.cpp \
  ../xsec/enc/XSECCryptoUtils.cpp \
  ../xsec/enc/XSECCryptoBase64.cpp \
  ../xsec/enc/XSCrypt/XSCryptCryptoBase64.cpp \
  ../xsec/enc/XSECCryptoProvider.cpp \
  ../xsec/enc/XSECCryptoException.cpp

# Framework files

framework_sources = \
  ../xsec/framework/XSECError.cpp \
  ../xsec/framework/XSECAlgorithmMapper.cpp \
  ../xsec/framework/XSECEnv.cpp \
  ../xsec/framework/XSECProvider.cpp \
  ../xsec/framework/XSECException.cpp \
  ../xsec/framework/XSECURIResolverXerces.cpp

txfm_sources = \
  ../xsec/transformers/TXFMBase.cpp \
  ../xsec/transformers/TXFMChain.cpp \
  ../xsec/transformers/TXFMCipher.cpp \
  ../xsec/transformers/TXFMParser.cpp \
  ../xsec/transformers/TXFMSB.cpp \
  ../xsec/transformers/TXFMEnvelope.cpp \
  ../xsec/transformers/TXFMBase64.cpp \
  ../xsec/transformers/TXFMXPathFilter.cpp \
  ../xsec/transformers/TXFMSHA1.cpp \
  ../xsec/transformers/TXFMC14n.cpp \
  ../xsec/transformers/TXFMURL.cpp \
  ../xsec/transformers/TXFMOutputFile.cpp \
  ../xsec/transformers/TXFMXPath.cpp \
  ../xsec/transformers/TXFMXSL.cpp \
  ../xsec/transformers/TXFMDocObject.cpp \
  ../xsec/transformers/TXFMConcatChains.cpp \
  ../xsec/transformers/TXFMMD5.cpp

# Utility files.  Note we don't worry about checking
# if the UNIX stuff is necessary - we just assume that
# we are running on a *NIX system because compiling under
# make.  Cygwin compiles use the UNIX utilities, not windows

utils_sources = \
  ../xsec/utils/unixutils/XSECSOAPRequestorSimpleUnix.cpp \
  ../xsec/utils/unixutils/XSECURIResolverGenericUnix.cpp \
  ../xsec/utils/unixutils/XSECBinHTTPURIInputStream.cpp \
  ../xsec/utils/XSECBinTXFMInputStream.cpp \
  ../xsec/utils/XSECXPathNodeList.cpp \
  ../xsec/utils/XSECSafeBuffer.cpp \
  ../xsec/utils/XSECTXFMInputSource.cpp \
  ../xsec/utils/XSECDOMUtils.cpp \
  ../xsec/utils/XSECSafeBufferFormatter.cpp \
  ../xsec/utils/XSECSOAPRequestorSimple.cpp \
  ../xsec/utils/XSECNameSpaceExpander.cpp \
  ../xsec/utils/XSECPlatformUtils.cpp

# XML Encryption

xenc_sources = \
  ../xsec/xenc/impl/XENCCipherReferenceImpl.cpp \
  ../xsec/xenc/impl/XENCEncryptionMethodImpl.cpp \
  ../xsec/xenc/impl/XENCCipherValueImpl.cpp \
  ../xsec/xenc/impl/XENCAlgorithmHandlerDefault.cpp \
  ../xsec/xenc/impl/XENCEncryptedDataImpl.cpp \
  ../xsec/xenc/impl/XENCCipherDataImpl.cpp \
  ../xsec/xenc/impl/XENCEncryptedTypeImpl.cpp \
  ../xsec/xenc/impl/XENCCipherImpl.cpp \
  ../xsec/xenc/impl/XENCEncryptedKeyImpl.cpp

# XML Key Management
xkms_sources = \
  ../xsec/xkms/impl/XKMSCompoundRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSRecoverRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSRegisterResultImpl.cpp \
  ../xsec/xkms/impl/XKMSRecoverResultImpl.cpp \
  ../xsec/xkms/impl/XKMSCompoundResultImpl.cpp \
  ../xsec/xkms/impl/XKMSRevokeKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSKeyBindingAbstractTypeImpl.cpp \
  ../xsec/xkms/impl/XKMSPrototypeKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSValidateResultImpl.cpp \
  ../xsec/xkms/impl/XKMSAuthenticationImpl.cpp \
  ../xsec/xkms/impl/XKMSStatusResultImpl.cpp \
  ../xsec/xkms/impl/XKMSReissueRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSRequestAbstractTypeImpl.cpp \
  ../xsec/xkms/impl/XKMSRespondWithImpl.cpp \
  ../xsec/xkms/impl/XKMSReissueResultImpl.cpp \
  ../xsec/xkms/impl/XKMSMessageAbstractTypeImpl.cpp \
  ../xsec/xkms/impl/XKMSRevokeResultImpl.cpp \
  ../xsec/xkms/impl/XKMSRevokeRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSPendingRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSValidateRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSRecoverKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSRSAKeyPairImpl.cpp \
  ../xsec/xkms/impl/XKMSUnverifiedKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSLocateRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSResultTypeImpl.cpp \
  ../xsec/xkms/impl/XKMSResultImpl.cpp \
  ../xsec/xkms/impl/XKMSValidityIntervalImpl.cpp \
  ../xsec/xkms/impl/XKMSRegisterRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSStatusImpl.cpp \
  ../xsec/xkms/impl/XKMSStatusRequestImpl.cpp \
  ../xsec/xkms/impl/XKMSReissueKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSMessageFactoryImpl.cpp \
  ../xsec/xkms/impl/XKMSResponseMechanismImpl.cpp \
  ../xsec/xkms/impl/XKMSNotBoundAuthentication.cpp \
  ../xsec/xkms/impl/XKMSLocateResultImpl.cpp \
  ../xsec/xkms/impl/XKMSQueryKeyBindingImpl.cpp \
  ../xsec/xkms/impl/XKMSUseKeyWithImpl.cpp \
  ../xsec/xkms/XKMSConstants.cpp
  
# Conditional crypto routines

openssl_sources = \
  ../xsec/enc/OpenSSL/OpenSSLCryptoHashHMAC.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoHash.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoProvider.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoX509.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoBase64.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoKeyDSA.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoKeyEC.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.cpp \
  ../xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.cpp
  
nss_sources = \
  ../xsec/enc/NSS/NSSCryptoX509.cpp \
  ../xsec/enc/NSS/NSSCryptoHashHMAC.cpp \
  ../xsec/enc/NSS/NSSCryptoHash.cpp \
  ../xsec/enc/NSS/NSSCryptoKeyDSA.cpp \
  ../xsec/enc/NSS/NSSCryptoProvider.cpp \
  ../xsec/enc/NSS/NSSCryptoSymmetricKey.cpp \
  ../xsec/enc/NSS/NSSCryptoKeyRSA.cpp \
  ../xsec/enc/NSS/NSSCryptoKeyHMAC.cpp

EXTRA_DIST = \
  ../xsec/utils/winutils/XSECURIResolverGenericWin32.cpp \
  ../xsec/utils/winutils/XSECSOAPRequestorSimpleWin32.cpp \
  ../xsec/utils/winutils/XSECBinHTTPURIInputStream.cpp \
  ../xsec/utils/winutils/XSECURIResolverGenericWin32.hpp \
  ../xsec/utils/winutils/XSECBinHTTPURIInputStream.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoProvider.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoHash.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoKeyDSA.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoHashHMAC.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoKeyDSA.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoSymmetricKey.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoX509.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoKeyRSA.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoX509.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoHashHMAC.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoHash.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoKeyRSA.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoSymmetricKey.cpp \
  ../xsec/enc/WinCAPI/WinCAPICryptoKeyHMAC.cpp

#
# Now the library specific build items
#

INCLUDES = -I$(top_srcdir)/include
libxml_security_c_la_LDFLAGS = -version-info 16:0:0
