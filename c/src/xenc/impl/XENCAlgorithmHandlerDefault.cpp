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
//			Internal functions
// --------------------------------------------------------------------------------

void XENCAlgorithmHandlerDefault::mapURIToKey(const XMLCh * uri, XSECCryptoKey * key) {

	if (strEquals(uri, DSIGConstants::s_unicodeStrURI3DES_CBC)) {

		// 3 Key 3DES in CBC mode.
		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_3DES_CBC_192) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 3DES Algorithm, but not a 3DES key");
		
		}

		return;
	}

	if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES128_CBC)) {

		// 3 Key 3DES in CBC mode.
		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_CBC_128) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - AES128 Algorithm, but not a AES128 key");
		
		}

		return;
	}

	if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES192_CBC)) {

		// 3 Key 3DES in CBC mode.
		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_CBC_192) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - AES192 Algorithm, but not a AES192 key");
		
		}

		return;
	}

	if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES256_CBC)) {

		// 3 Key 3DES in CBC mode.
		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_CBC_256) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - AES256 Algorithm, but not a AES256 key");
		
		}

		return;
	}

	throw XSECException(XSECException::CipherError, 
		"XENCAlgorithmHandlerDefault - URI not recognised for Symmetric Key encryption check");

}
	
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

	mapURIToKey(encryptionMethod->getAlgorithm(), key);

	// Add the decryption TXFM

	TXFMCipher * tcipher;
	XSECnew(tcipher, TXFMCipher(doc, key, false));

	cipherText->appendTxfm(tcipher);

	return true;
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

	bool isAESKeyWrap = false;

	// Is this a keyWrap URI?
	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIKW_AES128)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_ECB_128) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 128bit AES Algorithm, but not a AES (ECB) 128 bit key");
		
		}

		isAESKeyWrap = true;

	}

	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIKW_AES192)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_ECB_192) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 192bit AES Algorithm, but not a AES (ECB) 192 bit key");
		
		}

		isAESKeyWrap = true;

	}
	
	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIKW_AES256)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_ECB_256) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 256bit AES Algorithm, but not a AES (ECB) 256 bit key");
		
		}

		isAESKeyWrap = true;

	}

	if (isAESKeyWrap == true) {

		return unwrapKeyAES(cipherText, key, result);

	}

	// Is this an RSA key?
	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIRSA_1_5)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_RSA_PRIVATE && 
			key->getKeyType() != XSECCryptoKey::KEY_RSA_PAIR) {
			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - RSA Decrypt URI but not an RSA key");
		}

		XSECCryptoKeyRSA * rsa = dynamic_cast<XSECCryptoKeyRSA *>(key);
		
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

	// The default case is to just do a standard, padded block decrypt.
	// So the only thing we have to do is ensure key type matches URI.

	mapURIToKey(encryptionMethod->getAlgorithm(), key);

	// Add the decryption TXFM

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
//			SafeBuffer encryption
// --------------------------------------------------------------------------------

bool XENCAlgorithmHandlerDefault::encryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
		) {


	bool isAESKeyWrap = false;

	// Is this a keyWrap URI?
	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIKW_AES128)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_ECB_128) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 128bit AES Algorithm, but not a AES (ECB) 128 bit key");
		
		}

		isAESKeyWrap = true;

	}

	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIKW_AES192)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_ECB_192) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 192bit AES Algorithm, but not a AES (ECB) 192 bit key");
		
		}

		isAESKeyWrap = true;

	}

	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIKW_AES256)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_AES_ECB_256) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 256bit AES Algorithm, but not a AES (ECB) 256 bit key");
		
		}

		isAESKeyWrap = true;

	}

	if (isAESKeyWrap == true) {

		return wrapKeyAES(plainText, key, result);

	}
	
	// Is this an RSA key?
	if (strEquals(encryptionMethod->getAlgorithm(), DSIGConstants::s_unicodeStrURIRSA_1_5)) {

		if (key->getKeyType() != XSECCryptoKey::KEY_RSA_PUBLIC && 
			key->getKeyType() != XSECCryptoKey::KEY_RSA_PAIR) {
			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - RSA Encrypt URI but not an RSA public key");
		}

		XSECCryptoKeyRSA * rsa = dynamic_cast<XSECCryptoKeyRSA *>(key);
		
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
	
	// Check the URI and key match

	mapURIToKey(encryptionMethod->getAlgorithm(), key);

	// Do the encryption
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

	if (strEquals(uri, DSIGConstants::s_unicodeStrURI3DES_CBC)) {

		// 3 Key 3DES in CBC mode.
		XSECCryptoSymmetricKey * sk = 
			XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_3DES_CBC_192);

		sk->setKey(keyBuffer, keyLen);

		return sk;

	}

	if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES128_CBC)) {

		// AES 128bit key in CBC mode.
		XSECCryptoSymmetricKey * sk = 
			XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_CBC_128);

		sk->setKey(keyBuffer, keyLen);

		return sk;

	}

	if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES192_CBC)) {

		// AES 192bit key in CBC mode.
		XSECCryptoSymmetricKey * sk = 
			XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_CBC_192);

		sk->setKey(keyBuffer, keyLen);

		return sk;

	}
	
	if (strEquals(uri, DSIGConstants::s_unicodeStrURIAES256_CBC)) {

		// AES 192bit key in CBC mode.
		XSECCryptoSymmetricKey * sk = 
			XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_CBC_256);

		sk->setKey(keyBuffer, keyLen);

		return sk;

	}

	throw XSECException(XSECException::CipherError, 
		"XENCAlgorithmHandlerDefault - URI Provided, but cannot create associated key");

	return NULL;

}


// --------------------------------------------------------------------------------
//			Clone
// --------------------------------------------------------------------------------

XSECAlgorithmHandler * XENCAlgorithmHandlerDefault::clone(void) const {

	XENCAlgorithmHandlerDefault * ret;
	XSECnew(ret, XENCAlgorithmHandlerDefault);

	return ret;

}



