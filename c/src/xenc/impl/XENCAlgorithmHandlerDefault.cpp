/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European 
 * Commission in the <WebSig> project in the ISIS Programme. 
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/transformers/TXFMCipher.hpp>
#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/xenc/XENCEncryptionMethod.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>
#include <xsec/enc/XSECCryptoSymmetricKey.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include "XENCAlgorithmHandlerDefault.hpp"

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

#define _MY_MAX_KEY_SIZE 2048

unsigned char s_3DES_CMS_IV [] = {
	0x4a,
	0xdd,
	0xa2,
	0x2c,
	0x79,
	0xe8,
	0x21,
	0x05
};

unsigned char s_AES_IV [] = {

	0xA6,
	0xA6,
	0xA6,
	0xA6,
	0xA6,
	0xA6,
	0xA6,
	0xA6

};

// --------------------------------------------------------------------------------
//			Compare URI to key type
// --------------------------------------------------------------------------------

void XENCAlgorithmHandlerDefault::mapURIToKey(const XMLCh * uri, 
											  XSECCryptoKey * key,
											  XSECCryptoKey::KeyType &kt,
											  XSECCryptoSymmetricKey::SymmetricKeyType &skt,
											  bool &isSymmetricKeyWrap) {

	if (key == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault::mapURIToKey - trying to process a NULL key");
	}

	XSECCryptoSymmetricKey * keySymmetric;
	bool keyOK = false;

	kt = key->getKeyType();
	skt = XSECCryptoSymmetricKey::KEY_NONE;
	isSymmetricKeyWrap = false;
	
	switch (kt) {

	case XSECCryptoKey::KEY_RSA_PUBLIC :
	case XSECCryptoKey::KEY_RSA_PAIR :
	case XSECCryptoKey::KEY_RSA_PRIVATE :

		keyOK = strEquals(uri, DSIGConstants::s_unicodeStrURIRSA_1_5);
		break;

	case XSECCryptoKey::KEY_SYMMETRIC :

		keySymmetric = dynamic_cast<XSECCryptoSymmetricKey *>(key);
		if (keySymmetric != NULL) {
			skt = keySymmetric->getSymmetricKeyType();

			switch (skt) {

			case XSECCryptoSymmetricKey::KEY_3DES_CBC_192 :
				keyOK = strEquals(uri, DSIGConstants::s_unicodeStrURI3DES_CBC);
				break;
			case XSECCryptoSymmetricKey::KEY_AES_ECB_128 :
			case XSECCryptoSymmetricKey::KEY_AES_CBC_128 :
				isSymmetricKeyWrap = strEquals(uri, DSIGConstants::s_unicodeStrURIKW_AES128);
				keyOK =  isSymmetricKeyWrap || strEquals(uri, DSIGConstants::s_unicodeStrURIAES128_CBC);
				break;
			case XSECCryptoSymmetricKey::KEY_AES_ECB_192 :
			case XSECCryptoSymmetricKey::KEY_AES_CBC_192 :
				isSymmetricKeyWrap = strEquals(uri, DSIGConstants::s_unicodeStrURIKW_AES192);
				keyOK =  isSymmetricKeyWrap || strEquals(uri, DSIGConstants::s_unicodeStrURIAES192_CBC);
				break;
			case XSECCryptoSymmetricKey::KEY_AES_ECB_256 :
			case XSECCryptoSymmetricKey::KEY_AES_CBC_256 :
				isSymmetricKeyWrap = strEquals(uri, DSIGConstants::s_unicodeStrURIKW_AES256);
				keyOK =  isSymmetricKeyWrap || strEquals(uri, DSIGConstants::s_unicodeStrURIAES256_CBC);
				break;
			default:
				break;
			}
		}
		break;

	default:
		 break;
	}

	if (keyOK == false) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault::mapURIToKey - key inappropriate for URI");
	}

}

// --------------------------------------------------------------------------------
//			AES Key wrap/unwrap
// --------------------------------------------------------------------------------

unsigned int XENCAlgorithmHandlerDefault::unwrapKeyAES(
   		TXFMChain * cipherText,
		XSECCryptoKey * key,
		safeBuffer & result) {

	// Cat the encrypted key
	XMLByte buf[_MY_MAX_KEY_SIZE];
	XMLByte aesBuf[16];
	XMLByte aesOutBuf[16];
	TXFMBase * b = cipherText->getLastTxfm();
	int sz = b->readBytes(buf, _MY_MAX_KEY_SIZE);

	if (sz <= 0) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - AES Wrapped Key not found");
	}

	if (sz == _MY_MAX_KEY_SIZE) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - Key to decrypt too big!");
	}

	// Find number of blocks, and ensure we are a multiple of 64 bits
	if (sz % 8 != 0) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - AES wrapped key not a multiple of 64");
	}

	// Do the decrypt - this cast will throw if wrong, but we should
	// not have been able to get through algorithm checks otherwise
	XSECCryptoSymmetricKey * sk = dynamic_cast<XSECCryptoSymmetricKey *>(key);

	int blocks = sz / 8;
	int n = blocks - 1;
	for (int j = 5; j >= 0; j--) {
		for (int i = n ; i > 0 ; --i) {

			// Gather blocks to decrypt
			// A
			memcpy(aesBuf, buf, 8);
			// Ri
			memcpy(&aesBuf[8], &buf[8 * i], 8);
			// A mod t
			aesBuf[7] ^= ((n * j) + i);

			// do decrypt
			sk->decryptInit(false);		// No padding
			int sz = sk->decrypt(aesBuf, aesOutBuf, 16, 16);
			sz += sk->decryptFinish(&aesOutBuf[sz], 16 - sz);

			if (sz != 16) {
				throw XSECException(XSECException::CipherError, 
					"XENCAlgorithmHandlerDefault - Error performing decrypt in AES Unwrap");
			}

			// Copy back to where we are
			// A
			memcpy(buf, aesOutBuf, 8);
			// Ri
			memcpy(&buf[8 * i], &aesOutBuf[8], 8);

		}
	}

	// Check is valid
	if (memcmp(buf, s_AES_IV, 8) != 0) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - decrypt failed - AES IV is not correct");
	}

	// Copy to safebuffer
	result.sbMemcpyIn(&buf[8], n * 8);

	return n * 8;
}

bool XENCAlgorithmHandlerDefault::wrapKeyAES(
   		TXFMChain * cipherText,
		XSECCryptoKey * key,
		safeBuffer & result) {

	// get the raw key
	XMLByte buf[_MY_MAX_KEY_SIZE + 8];
	memcpy(buf, s_AES_IV, 8);
	XMLByte aesBuf[16];
	XMLByte aesOutBuf[32];  // Give this an extra block for WinCAPI
	TXFMBase * b = cipherText->getLastTxfm();
	int sz = b->readBytes(&buf[8], _MY_MAX_KEY_SIZE);

	if (sz <= 0) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - Key not found");
	}

	if (sz == _MY_MAX_KEY_SIZE) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - Key to encrypt too big!");
	}

	// Find number of blocks, and ensure we are a multiple of 64 bits
	if (sz % 8 != 0) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - AES wrapped key not a multiple of 64");
	}

	// Do the decrypt - this cast will throw if wrong, but we should
	// not have been able to get through algorithm checks otherwise
	XSECCryptoSymmetricKey * sk = dynamic_cast<XSECCryptoSymmetricKey *>(key);

	int n = sz / 8;
	int blocks = n + 1;

	for (int j = 0; j <= 5; ++j) {
		for (int i = 1 ; i <= n ; ++i) {

			// Gather blocks to decrypt
			// A
			memcpy(aesBuf, buf, 8);
			// Ri
			memcpy(&aesBuf[8], &buf[8 * i], 8);

			// do encrypt
			sk->encryptInit(false);
			int sz = sk->encrypt(aesBuf, aesOutBuf, 16, 32);
			sz += sk->encryptFinish(&aesOutBuf[sz], 32 - sz);

			if (sz != 16) {
				throw XSECException(XSECException::CipherError, 
					"XENCAlgorithmHandlerDefault - Error performing encrypt in AES wrap");
			}

			// Copy back to where we are
			// A
			memcpy(buf, aesOutBuf, 8);
			// A mod t
			buf[7] ^= ((n * j) + i);
			// Ri
			memcpy(&buf[8 * i], &aesOutBuf[8], 8);

		}
	}

	// Now we have to base64 encode
	XSECCryptoBase64 * b64 = XSECPlatformUtils::g_cryptoProvider->base64();

	if (!b64) {

		throw XSECException(XSECException::CryptoProviderError, 
				"XENCAlgorithmHandlerDefault - Error getting base64 encoder in AES wrap");

	}

	Janitor<XSECCryptoBase64> j_b64(b64);
	unsigned char * b64Buffer;
	int bufLen = ((n + 1) * 8) * 3;
	XSECnew(b64Buffer, unsigned char[bufLen + 1]);// Overkill
	ArrayJanitor<unsigned char> j_b64Buffer(b64Buffer);

	b64->encodeInit();
	int outputLen = b64->encode (buf, (n+1) * 8, b64Buffer, bufLen);
	outputLen += b64->encodeFinish(&b64Buffer[outputLen], bufLen - outputLen);
	b64Buffer[outputLen] = '\0';

	// Copy to safebuffer
	result.sbStrcpyIn((const char *) b64Buffer);

	return true;
}

// --------------------------------------------------------------------------------
//			DES CMS Key wrap/unwrap
// --------------------------------------------------------------------------------

#if 0

Keep for DES keywrap

		// Perform an unwrap on the key
		safeBuffer cipherSB;

		// Set the IV
		cipherSB.sbMemcpyIn(s_CMSIV, 8);

		// Cat the encrypted key
		XMLByte buf[_MY_MAX_KEY_SIZE];
		TXFMBase * b = cipherText->getLastTxfm();
		int offset = 8;
		int sz = b->readBytes(buf, _MY_MAX_KEY_SIZE);

		while (sz > 0) {
			cipherSB.sbMemcpyIn(offset, buf, sz);
			offset += sz;
			sz = b->readBytes(buf, _MY_MAX_KEY_SIZE);
		}

		if (offset > _MY_MAX_KEY_SIZE) {
			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - Key to decrypt too big!");
		}

		// Do the decrypt - this cast will throw if wrong, but we should
		// not have been able to get through algorithm checks otherwise
		XSECCryptoSymmetricKey * sk = dynamic_cast<XSECCryptoSymmetricKey *>(key);

		sk->decryptInit(false);	// No padding
		// If key is bigger than this, then we have a problem
		sz = sk->decrypt(cipherSB.rawBuffer(), buf, offset, _MY_MAX_KEY_SIZE);

		sz += sk->decryptFinish(&buf[sz], _MY_MAX_KEY_SIZE - sz);

		if (sz <= 0) {
			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - Error decrypting key!");
		}

		// We now have the first cut, reverse the key
		XMLByte buf2[_MY_MAX_KEY_SIZE];
		for (int i = 0; i < sz; ++ i) {
			buf2[sz - i] = buf[i];
		}

		// decrypt again
		sk->decryptInit(false);
		offset = sk->decrypt(buf2, buf, sz, _MY_MAX_KEY_SIZE);
		offset += sk->decryptFinish(&buf[offset], _MY_MAX_KEY_SIZE - offset);

#endif

// --------------------------------------------------------------------------------
//			InputStream decryption
// --------------------------------------------------------------------------------
	
bool XENCAlgorithmHandlerDefault::appendDecryptCipherTXFM(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc
		) {

	// We only support this for bulk Symmetric key algorithms

	XSECCryptoKey::KeyType kt;
	XSECCryptoSymmetricKey::SymmetricKeyType skt;
	bool isKeyWrap = false;

	mapURIToKey(encryptionMethod->getAlgorithm(), key, kt, skt, isKeyWrap);
	if (kt != XSECCryptoKey::KEY_SYMMETRIC || isKeyWrap == true) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault::appendDecryptCipherTXFM - only supports bulk symmetric algorithms");
	}

	// Add the decryption TXFM

	TXFMCipher * tcipher;
	XSECnew(tcipher, TXFMCipher(doc, key, false));

	cipherText->appendTxfm(tcipher);

	return true;
}


// --------------------------------------------------------------------------------
//			RSA SafeBuffer decryption
// --------------------------------------------------------------------------------

unsigned int XENCAlgorithmHandlerDefault::doRSADecryptToSafeBuffer(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		DOMDocument * doc,
		safeBuffer & result) {

	// Only works with RSA_PRIVATE or PAIR
	if (key->getKeyType() == XSECCryptoKey::KEY_RSA_PUBLIC) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - RSA Decrypt must use private key");
	}

	XSECCryptoKeyRSA * rsa = dynamic_cast<XSECCryptoKeyRSA *>(key);
	if (rsa == NULL) {	
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault::doRSADecryptToSafeBuffer - Error casting to RSA key");
	}

	// Allocate an output buffer
	unsigned char * decBuf;
	XSECnew(decBuf, unsigned char[rsa->getLength()]);
	ArrayJanitor<unsigned char> j_decBuf(decBuf);

	// Input
	TXFMBase * b = cipherText->getLastTxfm();
	safeBuffer cipherSB;
	XMLByte buf[1024];
	unsigned int offset = 0;

	int bytesRead = b->readBytes(buf, 1024);
	while (bytesRead > 0) {
		cipherSB.sbMemcpyIn(offset, buf, bytesRead);
		offset += bytesRead;
		bytesRead = b->readBytes(buf, 1024);
	}


	// Do decrypt
	unsigned int decryptLen = rsa->privateDecrypt(cipherSB.rawBuffer(), 
												  decBuf, 
												  offset, 
												  rsa->getLength(), 
												  XSECCryptoKeyRSA::PAD_PKCS_1_5, 
												  HASH_NONE, 
												  NULL, 
												  0);

	// Copy to output
	result.sbMemcpyIn(decBuf, decryptLen);
	
	memset(decBuf, 0, decryptLen);

	return decryptLen;

}

// --------------------------------------------------------------------------------
//			SafeBuffer decryption
// --------------------------------------------------------------------------------

unsigned int XENCAlgorithmHandlerDefault::decryptToSafeBuffer(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		DOMDocument * doc,
		safeBuffer & result
		) {

	XSECCryptoKey::KeyType kt;
	XSECCryptoSymmetricKey::SymmetricKeyType skt;
	bool isKeyWrap = false;

	if (encryptionMethod == NULL) {
		throw XSECException(XSECException::CipherError,
			"XENCAlgorithmHandlerDefault::decryptToSafeBuffer - Cannot operate with NULL encryption Method");
	}


	// Check the uri against the key type
	mapURIToKey(encryptionMethod->getAlgorithm(), key, kt, skt, isKeyWrap);

	// RSA?
	if (kt == XSECCryptoKey::KEY_RSA_PAIR || 
		kt == XSECCryptoKey::KEY_RSA_PUBLIC || 
		kt == XSECCryptoKey::KEY_RSA_PRIVATE) {

		return doRSADecryptToSafeBuffer(cipherText, encryptionMethod, key, doc, result);

	}

	// Ensure is symmetric before we continue
	if (kt != XSECCryptoKey::KEY_SYMMETRIC) {
		throw XSECException(XSECException::CipherError,
			"XENCAlgorithmHandlerDefault::decryptToSafeBuffer - Not an RSA key, but not symmetric");
	}

	// Key wrap?

	if (isKeyWrap == true) {

		if (skt == XSECCryptoSymmetricKey::KEY_AES_ECB_128 ||
			skt == XSECCryptoSymmetricKey::KEY_AES_ECB_192 ||
			skt == XSECCryptoSymmetricKey::KEY_AES_ECB_256) {

			return unwrapKeyAES(cipherText, key, result);

		}

		else {
			throw XSECException(XSECException::CipherError,
				"XENCAlgorithmHandlerDefault::decryptToSafeBuffer - don't know how to do key wrap for algorithm");
		}

	}

	// It's symmetric and it's not a key wrap, so just treat as a block algorithm

	TXFMCipher * tcipher;
	XSECnew(tcipher, TXFMCipher(doc, key, false));

	cipherText->appendTxfm(tcipher);

	// Do the decrypt to the safeBuffer

	result.sbStrcpyIn("");
	unsigned int offset = 0;
	XMLByte buf[1024];
	TXFMBase * b = cipherText->getLastTxfm();

	int bytesRead = b->readBytes(buf, 1024);
	while (bytesRead > 0) {
		result.sbMemcpyIn(offset, buf, bytesRead);
		offset += bytesRead;
		bytesRead = b->readBytes(buf, 1024);
	}

	result[offset] = '\0'; 

	return offset;

}

// --------------------------------------------------------------------------------
//			RSA SafeBuffer encryption
// --------------------------------------------------------------------------------

bool XENCAlgorithmHandlerDefault::doRSAEncryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
		) {

	// Only works with RSA_PRIVATE or PAIR
	if (key->getKeyType() == XSECCryptoKey::KEY_RSA_PRIVATE) {
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault - RSA Encrypt must use public key");
	}

	XSECCryptoKeyRSA * rsa = dynamic_cast<XSECCryptoKeyRSA *>(key);
	if (rsa == NULL) {	
		throw XSECException(XSECException::CipherError, 
			"XENCAlgorithmHandlerDefault::doRSAEncryptToSafeBuffer - Error casting to RSA key");
	}
	
	// Allocate an output buffer
	unsigned char * encBuf;
	XSECnew(encBuf, unsigned char[rsa->getLength()]);
	ArrayJanitor<unsigned char> j_encBuf(encBuf);

	// Input
	TXFMBase * b = plainText->getLastTxfm();
	safeBuffer plainSB;
	plainSB.isSensitive();

	XMLByte buf[1024];
	unsigned int offset = 0;

	int bytesRead = b->readBytes(buf, 1024);
	while (bytesRead > 0) {
		plainSB.sbMemcpyIn(offset, buf, bytesRead);
		offset += bytesRead;
		bytesRead = b->readBytes(buf, 1024);
	}


	// Do decrypt
	unsigned int encryptLen = rsa->publicEncrypt(plainSB.rawBuffer(), 
												  encBuf, 
												  offset, 
												  rsa->getLength(), 
												  XSECCryptoKeyRSA::PAD_PKCS_1_5, 
												  HASH_NONE, 
												  NULL, 
												  0);

	// Now need to base64 encode
	XSECCryptoBase64 * b64 = 
		XSECPlatformUtils::g_cryptoProvider->base64();
	Janitor<XSECCryptoBase64> j_b64(b64);

	b64->encodeInit();
	encryptLen = b64->encode(encBuf, encryptLen, buf, 1024);
	result.sbMemcpyIn(buf, encryptLen);
	unsigned int finalLen = b64->encodeFinish(buf, 1024);
	result.sbMemcpyIn(encryptLen, buf, finalLen);
	result[encryptLen + finalLen] = '\0';

	// This is a string, so set the buffer correctly
	result.setBufferType(safeBuffer::BUFFER_CHAR);

	return true;

}


// --------------------------------------------------------------------------------
//			SafeBuffer encryption
// --------------------------------------------------------------------------------

bool XENCAlgorithmHandlerDefault::encryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
		) {

	XSECCryptoKey::KeyType kt;
	XSECCryptoSymmetricKey::SymmetricKeyType skt;
	bool isKeyWrap = false;

	if (encryptionMethod == NULL) {
		throw XSECException(XSECException::CipherError,
			"XENCAlgorithmHandlerDefault::encryptToSafeBuffer - Cannot operate with NULL encryption Method");
	}


	// Check the uri against the key type
	mapURIToKey(encryptionMethod->getAlgorithm(), key, kt, skt, isKeyWrap);

	// RSA?
	if (kt == XSECCryptoKey::KEY_RSA_PRIVATE || 
		kt == XSECCryptoKey::KEY_RSA_PUBLIC || 
		kt == XSECCryptoKey::KEY_RSA_PAIR) {

		return doRSAEncryptToSafeBuffer(plainText, encryptionMethod, key, doc, result);

	}

	// Ensure is symmetric before we continue
	if (kt != XSECCryptoKey::KEY_SYMMETRIC) {
		throw XSECException(XSECException::CipherError,
			"XENCAlgorithmHandlerDefault::encryptToSafeBuffer - Not an RSA key, but not symmetric");
	}

	if (isKeyWrap == true) {

		if (skt == XSECCryptoSymmetricKey::KEY_AES_ECB_128 ||
			skt == XSECCryptoSymmetricKey::KEY_AES_ECB_192 ||
			skt == XSECCryptoSymmetricKey::KEY_AES_ECB_256) {

			return wrapKeyAES(plainText, key, result);

		}

		else {
			throw XSECException(XSECException::CipherError,
				"XENCAlgorithmHandlerDefault::decryptToSafeBuffer - don't know how to do key wrap for algorithm");
		}

	}
	
	// Must be bulk symmetric - do the encryption

	TXFMCipher *tcipher;
	XSECnew(tcipher, TXFMCipher(doc, key, true));
	plainText->appendTxfm(tcipher);

	// Transform to Base64
	TXFMBase64 * tb64;
	XSECnew(tb64, TXFMBase64(doc, false));
	plainText->appendTxfm(tb64);

	// Read into the safeBuffer
	result = "";

	result << plainText->getLastTxfm();

	return true;

}
// --------------------------------------------------------------------------------
//			Key Creation
// --------------------------------------------------------------------------------

XSECCryptoKey * XENCAlgorithmHandlerDefault::createKeyForURI(
		const XMLCh * uri,
		unsigned char * keyBuffer,
		unsigned int keyLen
		) {

	XSECCryptoSymmetricKey * sk = NULL;

	if (strEquals(uri, DSIGConstants::s_unicodeStrURI3DES_CBC)) {
		sk = XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_3DES_CBC_192);
	}
	else if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES128_CBC)) {
		sk = XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_CBC_128);
	}
	else if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES192_CBC)) {
		sk = XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_CBC_192);
	}
	else if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES256_CBC)) {
		sk = XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_CBC_256);
	}

	if (sk != NULL) {
		sk->setKey(keyBuffer, keyLen);
		return sk;
	}

	throw XSECException(XSECException::CipherError, 
		"XENCAlgorithmHandlerDefault - URI Provided, but cannot create associated key");

}


// --------------------------------------------------------------------------------
//			Clone
// --------------------------------------------------------------------------------

XSECAlgorithmHandler * XENCAlgorithmHandlerDefault::clone(void) const {

	XENCAlgorithmHandlerDefault * ret;
	XSECnew(ret, XENCAlgorithmHandlerDefault);

	return ret;

}



