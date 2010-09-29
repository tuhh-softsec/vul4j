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

BUILT_SOURCES = ${lib_includes}
nobase_include_HEADERS = ${lib_includes}

lib_includes = 

# The required include files

#       A line like the following will generate a list of headers
#       for a given directory: (must be issued from base directory)
#
# find src/framework -name '*.hpp' -exec echo {} \\ ";" | sed "s/src/  xsec/g"

# Canon

lib_includes += \
  xsec/canon/XSECXMLNSStack.hpp \
  xsec/canon/XSECCanon.hpp \
  xsec/canon/XSECC14n20010315.hpp

# enc

lib_includes += \
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

lib_includes += \
  xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp

lib_includes += \
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

lib_includes += \
  xsec/enc/NSS/NSSCryptoX509.hpp \
  xsec/enc/NSS/NSSCryptoHashHMAC.hpp \
  xsec/enc/NSS/NSSCryptoKeyDSA.hpp \
  xsec/enc/NSS/NSSCryptoKeyHMAC.hpp \
  xsec/enc/NSS/NSSCryptoHash.hpp \
  xsec/enc/NSS/NSSCryptoSymmetricKey.hpp \
  xsec/enc/NSS/NSSCryptoProvider.hpp \
  xsec/enc/NSS/NSSCryptoKeyRSA.hpp 

# dsig

lib_includes += \
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

# framework
lib_includes += \
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

# transformers

lib_includes += \
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

# utils

lib_includes += \
  xsec/utils/unixutils/XSECURIResolverGenericUnix.hpp \
  xsec/utils/unixutils/XSECBinHTTPURIInputStream.hpp \
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

# XML Encryption

lib_includes += \
  xsec/xenc/XENCEncryptionMethod.hpp \
  xsec/xenc/XENCEncryptedType.hpp \
  xsec/xenc/XENCCipherData.hpp \
  xsec/xenc/XENCEncryptedKey.hpp \
  xsec/xenc/XENCCipherValue.hpp \
  xsec/xenc/XENCEncryptedData.hpp \
  xsec/xenc/XENCCipherReference.hpp \
  xsec/xenc/XENCCipher.hpp

# XML Key Management
lib_includes += \
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

# Now the rules for copying the include files over

SUFFIXES = .hpp

xsec/%.hpp: xsec ../src/%.hpp
	cp ../src/$*.hpp $@

xsec:
	mkdir xsec
	mkdir xsec/canon
	mkdir xsec/dsig
	mkdir xsec/framework
	mkdir xsec/utils
	mkdir xsec/utils/unixutils
	mkdir xsec/enc
	mkdir xsec/enc/OpenSSL
	mkdir xsec/enc/NSS
	mkdir xsec/enc/XSCrypt
	mkdir xsec/transformers
	mkdir xsec/xenc
	mkdir xsec/xkms

# Cleaning

clean-local:
	$(RM) -r xsec
