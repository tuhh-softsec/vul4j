/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

/*
 * XSEC
 *
 * XSECAlgorithmHandlerDefault := Interface class to define handling of
 *								  default encryption algorithms
 *
 * $Id$
 *
 */

#ifndef XENCALGHANDLERDEFAULT_INCLUDE
#define XENCALGHANDLERDEFAULT_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECAlgorithmHandler.hpp>

class TXFMChain;
class XENCEncryptionMethod;
class XSECCryptoKey;

// Xerces

class XENCAlgorithmHandlerDefault : public XSECAlgorithmHandler {

public:
	
	
	virtual ~XENCAlgorithmHandlerDefault() {};


	virtual unsigned int decryptToSafeBuffer(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
	);

	virtual bool appendDecryptCipherTXFM(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc
	);

	virtual bool encryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
	);

	virtual XSECCryptoKey * createKeyForURI(
		const XMLCh * uri,
		unsigned char * keyBuffer,
		unsigned int keyLen
	);

	virtual XSECAlgorithmHandler * clone(void) const;

private:

	void mapURIToKey(const XMLCh * uri, 
		XSECCryptoKey * key,
		XSECCryptoKey::KeyType &kt,
		XSECCryptoSymmetricKey::SymmetricKeyType &skt,
		bool &isSymmetricKeyWrap);
	unsigned int doRSADecryptToSafeBuffer(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result);
	bool doRSAEncryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result);
	unsigned int unwrapKeyAES(
   		TXFMChain * cipherText,
		XSECCryptoKey * key,
		safeBuffer & result);
	unsigned int unwrapKey3DES(
   		TXFMChain * cipherText,
		XSECCryptoKey * key,
		safeBuffer & result);
	bool wrapKeyAES(
   		TXFMChain * cipherText,
		XSECCryptoKey * key,
		safeBuffer & result);
	bool wrapKey3DES(
   		TXFMChain * cipherText,
		XSECCryptoKey * key,
		safeBuffer & result);

};

/*\@}*/

#endif /* XENCALGHANDLERDEFAULT_INCLUDE */

