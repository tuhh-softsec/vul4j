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
  ../src/canon/XSECXMLNSStack.hpp \
  ../src/canon/XSECC14n20010315.hpp \
  ../src/canon/XSECC14n20010315.cpp \
  ../src/canon/XSECXMLNSStack.cpp \
  ../src/canon/XSECCanon.hpp \
  ../src/canon/XSECCannon.cpp

# Signature

dsig_sources = \
  ../src/dsig/DSIGKeyInfoPGPData.hpp \
  ../src/dsig/DSIGReferenceList.cpp \
  ../src/dsig/DSIGKeyInfoValue.hpp \
  ../src/dsig/DSIGXPathHere.hpp \
  ../src/dsig/DSIGAlgorithmHandlerDefault.cpp \
  ../src/dsig/DSIGXPathFilterExpr.hpp \
  ../src/dsig/DSIGKeyInfoMgmtData.hpp \
  ../src/dsig/DSIGTransformXPathFilter.hpp \
  ../src/dsig/DSIGKeyInfoMgmtData.cpp \
  ../src/dsig/DSIGKeyInfoPGPData.cpp \
  ../src/dsig/DSIGSignedInfo.cpp \
  ../src/dsig/DSIGKeyInfoList.hpp \
  ../src/dsig/DSIGConstants.cpp \
  ../src/dsig/DSIGReferenceList.hpp \
  ../src/dsig/DSIGSignature.hpp \
  ../src/dsig/DSIGKeyInfoValue.cpp \
  ../src/dsig/DSIGTransformXSL.hpp \
  ../src/dsig/DSIGObject.cpp \
  ../src/dsig/DSIGAlgorithmHandlerDefault.hpp \
  ../src/dsig/DSIGTransformXSL.cpp \
  ../src/dsig/DSIGTransformXPath.hpp \
  ../src/dsig/DSIGTransformEnvelope.cpp \
  ../src/dsig/DSIGKeyInfoName.hpp \
  ../src/dsig/DSIGObject.hpp \
  ../src/dsig/DSIGTransformBase64.hpp \
  ../src/dsig/DSIGTransformXPath.cpp \
  ../src/dsig/DSIGXPathFilterExpr.cpp \
  ../src/dsig/DSIGReference.hpp \
  ../src/dsig/DSIGSignature.cpp \
  ../src/dsig/DSIGKeyInfoSPKIData.cpp \
  ../src/dsig/DSIGTransformEnvelope.hpp \
  ../src/dsig/DSIGKeyInfo.hpp \
  ../src/dsig/DSIGTransformList.hpp \
  ../src/dsig/DSIGTransformBase64.cpp \
  ../src/dsig/DSIGKeyInfoX509.hpp \
  ../src/dsig/DSIGReference.cpp \
  ../src/dsig/DSIGKeyInfoList.cpp \
  ../src/dsig/DSIGConstants.hpp \
  ../src/dsig/DSIGTransform.hpp \
  ../src/dsig/DSIGTransform.cpp \
  ../src/dsig/DSIGKeyInfoSPKIData.hpp \
  ../src/dsig/DSIGTransformC14n.hpp \
  ../src/dsig/DSIGKeyInfoName.cpp \
  ../src/dsig/DSIGTransformList.cpp \
  ../src/dsig/DSIGSignedInfo.hpp \
  ../src/dsig/DSIGKeyInfoX509.cpp \
  ../src/dsig/DSIGXPathHere.cpp \
  ../src/dsig/DSIGTransformXPathFilter.cpp \
  ../src/dsig/DSIGTransformC14n.cpp

# Main Crypto interface routines

enc_sources = \
  ../src/enc/XSECCryptoX509.hpp \
  ../src/enc/XSECKeyInfoResolverDefault.hpp \
  ../src/enc/XSECCryptoKeyHMAC.hpp \
  ../src/enc/XSECCryptoHash.hpp \
  ../src/enc/XSECCryptoKeyRSA.hpp \
  ../src/enc/XSECCryptoUtils.cpp \
  ../src/enc/XSECCryptoBase64.cpp \
  ../src/enc/XSECCryptoBase64.hpp \
  ../src/enc/XSECCryptoKey.hpp \
  ../src/enc/XSECKeyInfoResolverDefault.cpp \
  ../src/enc/XSCrypt/XSCryptCryptoBase64.hpp \
  ../src/enc/XSCrypt/XSCryptCryptoBase64.cpp \
  ../src/enc/XSECCryptoUtils.hpp \
  ../src/enc/XSECCryptoKeyDSA.hpp \
  ../src/enc/XSECCryptoException.hpp \
  ../src/enc/XSECCryptoX509.cpp \
  ../src/enc/XSECCryptoProvider.hpp \
  ../src/enc/XSECKeyInfoResolver.hpp \
  ../src/enc/XSECCryptoSymmetricKey.hpp \
  ../src/enc/XSECCryptoException.cpp

# Framework files

framework_sources = \
  ../src/framework/XSECURIResolver.hpp \
  ../src/framework/XSECError.cpp \
  ../src/framework/XSECAlgorithmMapper.cpp \
  ../src/framework/XSECException.hpp \
  ../src/framework/XSECEnv.cpp \
  ../src/framework/XSECURIResolverXerces.hpp \
  ../src/framework/XSECW32Config.hpp \
  ../src/framework/XSECProvider.hpp \
  ../src/framework/XSECAlgorithmHandler.hpp \
  ../src/framework/XSECConfig.hpp \
  ../src/framework/XSECAlgorithmMapper.hpp \
  ../src/framework/XSECDefs.hpp \
  ../src/framework/XSECProvider.cpp \
  ../src/framework/XSECError.hpp \
  ../src/framework/XSECException.cpp \
  ../src/framework/XSECEnv.hpp \
  ../src/framework/XSECURIResolverXerces.cpp

txfm_sources = \
  ../src/transformers/TXFMBase.cpp \
  ../src/transformers/TXFMChain.cpp \
  ../src/transformers/TXFMCipher.cpp \
  ../src/transformers/TXFMParser.cpp \
  ../src/transformers/TXFMURL.hpp \
  ../src/transformers/TXFMSB.cpp \
  ../src/transformers/TXFMXPathFilter.hpp \
  ../src/transformers/TXFMEnvelope.cpp \
  ../src/transformers/TXFMBase.hpp \
  ../src/transformers/TXFMBase64.cpp \
  ../src/transformers/TXFMCipher.hpp \
  ../src/transformers/TXFMChain.hpp \
  ../src/transformers/TXFMDocObject.hpp \
  ../src/transformers/TXFMXPathFilter.cpp \
  ../src/transformers/TXFMSB.hpp \
  ../src/transformers/TXFMMD5.hpp \
  ../src/transformers/TXFMC14n.hpp \
  ../src/transformers/TXFMSHA1.cpp \
  ../src/transformers/TXFMParser.hpp \
  ../src/transformers/TXFMEnvelope.hpp \
  ../src/transformers/TXFMC14n.cpp \
  ../src/transformers/TXFMURL.cpp \
  ../src/transformers/TXFMOutputFile.cpp \
  ../src/transformers/TXFMXPath.cpp \
  ../src/transformers/TXFMXPath.hpp \
  ../src/transformers/TXFMXSL.cpp \
  ../src/transformers/TXFMDocObject.cpp \
  ../src/transformers/TXFMConcatChains.cpp \
  ../src/transformers/TXFMConcatChains.hpp \
  ../src/transformers/TXFMXSL.hpp \
  ../src/transformers/TXFMSHA1.hpp \
  ../src/transformers/TXFMMD5.cpp \
  ../src/transformers/TXFMBase64.hpp \
  ../src/transformers/TXFMOutputFile.hpp

# Utility files.  Note we don't worry about checking
# if the UNIX stuff is necessary - we just assume that
# we are running on a *NIX system because compiling under
# make.  Cygwin compiles use the UNIX utilities, not windows

utils_sources = \
  ../src/utils/unixutils/XSECSOAPRequestorSimpleUnix.cpp \
  ../src/utils/unixutils/XSECURIResolverGenericUnix.cpp \
  ../src/utils/unixutils/XSECBinHTTPURIInputStream.cpp \
  ../src/utils/unixutils/XSECBinHTTPURIInputStream.hpp \
  ../src/utils/unixutils/XSECURIResolverGenericUnix.hpp \
  ../src/utils/XSECBinTXFMInputStream.hpp \
  ../src/utils/XSECBinTXFMInputStream.cpp \
  ../src/utils/XSECNameSpaceExpander.hpp \
  ../src/utils/XSECXPathNodeList.cpp \
  ../src/utils/XSECXPathNodeList.hpp \
  ../src/utils/XSECTXFMInputSource.hpp \
  ../src/utils/XSECSOAPRequestorSimple.hpp \
  ../src/utils/XSECSafeBuffer.cpp \
  ../src/utils/XSECSafeBufferFormatter.hpp \
  ../src/utils/XSECTXFMInputSource.cpp \
  ../src/utils/XSECDOMUtils.hpp \
  ../src/utils/XSECSafeBuffer.hpp \
  ../src/utils/XSECPlatformUtils.hpp \
  ../src/utils/XSECDOMUtils.cpp \
  ../src/utils/XSECSafeBufferFormatter.cpp \
  ../src/utils/XSECSOAPRequestorSimple.cpp \
  ../src/utils/XSECSOAPRequestor.hpp \
  ../src/utils/XSECNameSpaceExpander.cpp \
  ../src/utils/XSECPlatformUtils.cpp

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
  ../src/xenc/impl/XENCCipherReferenceImpl.hpp \
  ../src/xenc/XENCCipherData.hpp \
  ../src/xenc/XENCEncryptedKey.hpp \
  ../src/xenc/XENCCipherValue.hpp \
  ../src/xenc/XENCEncryptionMethod.hpp \
  ../src/xenc/XENCCipher.hpp \
  ../src/xenc/XENCEncryptedData.hpp \
  ../src/xenc/XENCEncryptedType.hpp \
  ../src/xenc/XENCCipherReference.hpp

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
  ../src/xkms/impl/XKMSRegisterResultImpl.hpp \
  ../src/xkms/XKMSStatusRequest.hpp \
  ../src/xkms/XKMSResultType.hpp \
  ../src/xkms/XKMSKeyBinding.hpp \
  ../src/xkms/XKMSValidateResult.hpp \
  ../src/xkms/XKMSValidateRequest.hpp \
  ../src/xkms/XKMSUseKeyWith.hpp \
  ../src/xkms/XKMSRegisterRequest.hpp \
  ../src/xkms/XKMSRevokeKeyBinding.hpp \
  ../src/xkms/XKMSConstants.hpp \
  ../src/xkms/XKMSLocateRequest.hpp \
  ../src/xkms/XKMSStatus.hpp \
  ../src/xkms/XKMSPendingRequest.hpp \
  ../src/xkms/XKMSStatusResult.hpp \
  ../src/xkms/XKMSReissueResult.hpp \
  ../src/xkms/XKMSUnverifiedKeyBinding.hpp \
  ../src/xkms/XKMSQueryKeyBinding.hpp \
  ../src/xkms/XKMSRecoverResult.hpp \
  ../src/xkms/XKMSMessageFactory.hpp \
  ../src/xkms/XKMSAuthentication.hpp \
  ../src/xkms/XKMSConstants.cpp \
  ../src/xkms/XKMSRSAKeyPair.hpp \
  ../src/xkms/XKMSReissueRequest.hpp \
  ../src/xkms/XKMSRecoverKeyBinding.hpp \
  ../src/xkms/XKMSRevokeRequest.hpp \
  ../src/xkms/XKMSNotBoundAuthentication.hpp \
  ../src/xkms/XKMSRespondWith.hpp \
  ../src/xkms/XKMSValidityInterval.hpp \
  ../src/xkms/XKMSReissueKeyBinding.hpp \
  ../src/xkms/XKMSMessageAbstractType.hpp \
  ../src/xkms/XKMSPrototypeKeyBinding.hpp \
  ../src/xkms/XKMSResponseMechanism.hpp \
  ../src/xkms/XKMSRecoverRequest.hpp \
  ../src/xkms/XKMSCompoundResult.hpp \
  ../src/xkms/XKMSCompoundRequest.hpp \
  ../src/xkms/XKMSRevokeResult.hpp \
  ../src/xkms/XKMSResult.hpp \
  ../src/xkms/XKMSLocateResult.hpp \
  ../src/xkms/XKMSRegisterResult.hpp \
  ../src/xkms/XKMSKeyBindingAbstractType.hpp \
  ../src/xkms/XKMSRequestAbstractType.hpp

# Conditional crypto routines

openssl_sources = \
  ../src/enc/OpenSSL/OpenSSLCryptoHashHMAC.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyDSA.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyRSA.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoHash.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoProvider.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoProvider.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoBase64.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoX509.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoX509.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoBase64.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoHash.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyDSA.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoHashHMAC.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp \
  ../src/enc/OpenSSL/OpenSSLCryptoSymmetricKey.cpp \
  ../src/enc/OpenSSL/OpenSSLCryptoKeyHMAC.cpp

EXTRA_DIST = \
  ../src/utils/winutils/XSECURIResolverGenericWin32.cpp \
  ../src/utils/winutils/XSECSOAPRequestorSimpleWin32.cpp \
  ../src/utils/winutils/XSECBinHTTPURIInputStream.cpp \
  ../src/utils/winutils/XSECURIResolverGenericWin32.hpp \
  ../src/utils/winutils/XSECBinHTTPURIInputStream.hpp \
  ../src/enc/NSS/NSSCryptoHash.hpp \
  ../src/enc/NSS/NSSCryptoX509.cpp \
  ../src/enc/NSS/NSSCryptoKeyDSA.hpp \
  ../src/enc/NSS/NSSCryptoHashHMAC.cpp \
  ../src/enc/NSS/NSSCryptoKeyRSA.hpp \
  ../src/enc/NSS/NSSCryptoKeyHMAC.hpp \
  ../src/enc/NSS/NSSCryptoHash.cpp \
  ../src/enc/NSS/NSSCryptoKeyDSA.cpp \
  ../src/enc/NSS/NSSCryptoProvider.cpp \
  ../src/enc/NSS/NSSCryptoSymmetricKey.hpp \
  ../src/enc/NSS/NSSCryptoHashHMAC.hpp \
  ../src/enc/NSS/NSSCryptoX509.hpp \
  ../src/enc/NSS/NSSCryptoSymmetricKey.cpp \
  ../src/enc/NSS/NSSCryptoProvider.hpp \
  ../src/enc/NSS/NSSCryptoKeyRSA.cpp \
  ../src/enc/NSS/NSSCryptoKeyHMAC.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoProvider.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoHash.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoKeyDSA.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoHashHMAC.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoKeyDSA.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoSymmetricKey.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoX509.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoKeyRSA.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoX509.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoHashHMAC.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoHash.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoKeyRSA.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoProvider.hpp \
  ../src/enc/WinCAPI/WinCAPICryptoSymmetricKey.cpp \
  ../src/enc/WinCAPI/WinCAPICryptoKeyHMAC.cpp

#
# Now the library specific build items
#

INCLUDES = -I$(top_srcdir)/include
libxml_security_c_la_LDFLAGS = \
  -version-info ${package_libtool_version}
