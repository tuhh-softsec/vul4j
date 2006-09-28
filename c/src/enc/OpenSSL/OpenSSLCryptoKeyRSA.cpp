/*
 * Copyright 2002-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * OpenSSLCryptoKeyRSA := RSA Keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#if defined (HAVE_OPENSSL)

#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/XSECCryptoUtils.hpp>
#include <xsec/framework/XSECError.hpp>

#include <openssl/rsa.h>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);

#include <memory.h>

OpenSSLCryptoKeyRSA::OpenSSLCryptoKeyRSA() :
mp_oaepParams(NULL),
m_oaepParamsLen(0) {

	// Create a new key to be loaded as we go

	mp_rsaKey = RSA_new();

};
OpenSSLCryptoKeyRSA::~OpenSSLCryptoKeyRSA() {


	// If we have a RSA, delete it (OpenSSL will clear the memory)

	if (mp_rsaKey)
		RSA_free(mp_rsaKey);

	if (mp_oaepParams != NULL)
		delete[] mp_oaepParams;

};

void OpenSSLCryptoKeyRSA::setOAEPparams(unsigned char * params, unsigned int paramsLen) {

	if (mp_oaepParams != NULL) {
		delete[] mp_oaepParams;
	}

	m_oaepParamsLen = paramsLen;
	if (params != NULL) {
		XSECnew(mp_oaepParams, unsigned char[paramsLen]);
		memcpy(mp_oaepParams, params, paramsLen);
	}
	else
		mp_oaepParams = NULL;

}

unsigned int OpenSSLCryptoKeyRSA::getOAEPparamsLen(void) {

	return m_oaepParamsLen;

}

const unsigned char * OpenSSLCryptoKeyRSA::getOAEPparams(void) {

	return mp_oaepParams;

}

// Generic key functions

XSECCryptoKey::KeyType OpenSSLCryptoKeyRSA::getKeyType() {

	// Find out what we have
	if (mp_rsaKey == NULL)
		return KEY_NONE;

	if (mp_rsaKey->n != NULL && mp_rsaKey->d != NULL)
		return KEY_RSA_PAIR;

	if (mp_rsaKey->d != NULL)
		return KEY_RSA_PRIVATE;

	if (mp_rsaKey->n != NULL)
		return KEY_RSA_PUBLIC;

	return KEY_NONE;

}

void OpenSSLCryptoKeyRSA::loadPublicModulusBase64BigNums(const char * b64, unsigned int len) {

	if (mp_rsaKey == NULL)
		mp_rsaKey = RSA_new();

	mp_rsaKey->n = OpenSSLCryptoBase64::b642BN((char *) b64, len);

}

void OpenSSLCryptoKeyRSA::loadPublicExponentBase64BigNums(const char * b64, unsigned int len) {

	if (mp_rsaKey == NULL)
		mp_rsaKey = RSA_new();

	mp_rsaKey->e = OpenSSLCryptoBase64::b642BN((char *) b64, len);

}

// "Hidden" OpenSSL functions

OpenSSLCryptoKeyRSA::OpenSSLCryptoKeyRSA(EVP_PKEY *k) {

	// Create a new key to be loaded as we go

	mp_oaepParams = NULL;
	m_oaepParamsLen = 0;

	mp_rsaKey = RSA_new();
	
	if (k == NULL || k->type != EVP_PKEY_RSA)
		return;	// Nothing to do with us

	if (k->pkey.rsa->n)
		mp_rsaKey->n = BN_dup(k->pkey.rsa->n);

	if (k->pkey.rsa->e)
		mp_rsaKey->e = BN_dup(k->pkey.rsa->e);

	if (k->pkey.rsa->d)
		mp_rsaKey->d = BN_dup(k->pkey.rsa->d);

	if (k->pkey.rsa->p)
		mp_rsaKey->p = BN_dup(k->pkey.rsa->p);

	if (k->pkey.rsa->q)
		mp_rsaKey->q = BN_dup(k->pkey.rsa->q);

	if (k->pkey.rsa->dmp1)
		mp_rsaKey->dmp1 = BN_dup(k->pkey.rsa->dmp1);

	if (k->pkey.rsa->dmq1)
		mp_rsaKey->dmq1 = BN_dup(k->pkey.rsa->dmq1);

	if (k->pkey.rsa->iqmp)
		mp_rsaKey->iqmp = BN_dup(k->pkey.rsa->iqmp);

}

// --------------------------------------------------------------------------------
//           Verify a signature encoded as a Base64 string
// --------------------------------------------------------------------------------

bool OpenSSLCryptoKeyRSA::verifySHA1PKCS1Base64Signature(const unsigned char * hashBuf, 
								 unsigned int hashLen,
								 const char * base64Signature,
								 unsigned int sigLen,
								 hashMethod hm = HASH_SHA1) {

	// Use the currently loaded key to validate the Base64 encoded signature

	if (mp_rsaKey == NULL) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Attempt to validate signature with empty key");
	}

	unsigned char sigVal[1024];
	int sigValLen;

	EVP_ENCODE_CTX m_dctx;
	int rc;

	char * cleanedBase64Signature;
	unsigned int cleanedBase64SignatureLen = 0;

	cleanedBase64Signature = 
		XSECCryptoBase64::cleanBuffer(base64Signature, sigLen, cleanedBase64SignatureLen);
	ArrayJanitor<char> j_cleanedBase64Signature(cleanedBase64Signature);

	EVP_DecodeInit(&m_dctx);
	rc = EVP_DecodeUpdate(&m_dctx, 
						  sigVal, 
						  &sigValLen, 
						  (unsigned char *) cleanedBase64Signature, 
						  cleanedBase64SignatureLen);

	if (rc < 0) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Error during Base64 Decode");
	}
	int t = 0;

	EVP_DecodeFinal(&m_dctx, &sigVal[sigValLen], &t); 

	sigValLen += t;

	// Now decrypt

	unsigned char * decryptBuf;

	// Decrypt will always be longer than (RSA_len(key) - 11)
	decryptBuf = new unsigned char [RSA_size(mp_rsaKey)];
	ArrayJanitor<unsigned char> j_decryptBuf(decryptBuf);

	// Note at this time only supports PKCS1 padding
	// As that is what is defined in the standard.
	// If this ever changes we will need to pass some paramaters
	// into this function to allow it to determine what the
	// padding should be and what the message digest OID should
	// be.

	int decryptSize = RSA_public_decrypt(sigValLen, 
											 sigVal, 
											 decryptBuf,
											 mp_rsaKey,
											 RSA_PKCS1_PADDING);

	if (decryptSize < 0) {

/*		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA::verify() - Error decrypting signature"); */
		// Really - this is a failed signature check, not an exception!
		return false;
	}

	/* Check the OID */
	int oidLen = 0;
	unsigned char * oid = getRSASigOID(hm, oidLen);
	
	if (oid == NULL) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA::verify() - Unsupported HASH algorithm for RSA");
	}

	if (decryptSize != (int) (oidLen + hashLen) || hashLen != oid[oidLen-1]) {

		return false;
	
	}

	for (t = 0; t < oidLen; ++t) {
		
		if (oid[t] != decryptBuf[t]) {

			return false;

		}

	}

	for (;t < decryptSize; ++t) {

		if (hashBuf[t-oidLen] != decryptBuf[t]) {

			return false;

		}

	}

	// All OK
	return true;

}

// --------------------------------------------------------------------------------
//           Sign and encode result as a Base64 string
// --------------------------------------------------------------------------------


unsigned int OpenSSLCryptoKeyRSA::signSHA1PKCS1Base64Signature(unsigned char * hashBuf,
		unsigned int hashLen,
		char * base64SignatureBuf,
		unsigned int base64SignatureBufLen,
		hashMethod hm) {

	// Sign a pre-calculated hash using this key

	if (mp_rsaKey == NULL) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Attempt to sign data with empty key");
	}

	// Build the buffer to be encrypted by prepending the SHA1 OID to the hash

	unsigned char * encryptBuf;
	unsigned char * preEncryptBuf;
	unsigned char * oid;
	int oidLen;
	int encryptLen;
	int preEncryptLen;
	
	oid = getRSASigOID(hm, oidLen);
	
	if (oid == NULL) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA::sign() - Unsupported HASH algorithm for RSA");
	}

	if (hashLen != oid[oidLen-1]) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA::sign() - hashLen incorrect for hash type");
	}

	preEncryptLen = hashLen + oidLen;
	preEncryptBuf = new unsigned char[preEncryptLen];
	encryptBuf = new unsigned char[RSA_size(mp_rsaKey)];

	memcpy(preEncryptBuf, oid, oidLen);
	memcpy(&preEncryptBuf[oidLen], hashBuf, hashLen);

	// Now encrypt

	encryptLen = RSA_private_encrypt(preEncryptLen,
								     preEncryptBuf,
									 encryptBuf,
									 mp_rsaKey,
									 RSA_PKCS1_PADDING);

	delete[] preEncryptBuf;

	if (encryptLen < 0) {

		delete[] encryptBuf;
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA::sign() - Error encrypting hash");
	}

	// Now convert to Base 64

	BIO * b64 = BIO_new(BIO_f_base64());
	BIO * bmem = BIO_new(BIO_s_mem());

	BIO_set_mem_eof_return(bmem, 0);
	b64 = BIO_push(b64, bmem);

	// Translate signature to Base64

	BIO_write(b64, encryptBuf, encryptLen);
	BIO_flush(b64);

	unsigned int sigValLen = BIO_read(bmem, base64SignatureBuf, base64SignatureBufLen);

	BIO_free_all(b64);

	delete[] encryptBuf;

	if (sigValLen <= 0) {

		throw XSECCryptoException(XSECCryptoException::DSAError,
			"OpenSSL:RSA - Error base64 encoding signature");
	}

	return sigValLen;
}

// --------------------------------------------------------------------------------
//           decrypt a buffer
// --------------------------------------------------------------------------------

unsigned int OpenSSLCryptoKeyRSA::privateDecrypt(const unsigned char * inBuf,
								 unsigned char * plainBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength,
								 PaddingType padding,
								 hashMethod hm) {

	// Perform a decrypt
	if (mp_rsaKey == NULL) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Attempt to decrypt data with empty key");
	}

#if 0

	/* normally commented out code to determine endian problems */
	unsigned int i;
	unsigned char e[2048];
	unsigned char * inBuf1 = (unsigned char *) inBuf;
	if (inLength < 2048) {
		memcpy(e, inBuf, inLength);
		for (i = 0; i < inLength;++i) {
			inBuf1[i] = e[inLength - 1 - i];
		}
	}

#endif

	int decryptSize;

	switch (padding) {

	case XSECCryptoKeyRSA::PAD_PKCS_1_5 :

		decryptSize = RSA_private_decrypt(inLength,
#if defined(XSEC_OPENSSL_CONST_BUFFERS)
							inBuf,
#else
						    (unsigned char *) inBuf,
#endif
							plainBuf,
							mp_rsaKey,
							RSA_PKCS1_PADDING);
		
		if (decryptSize < 0) {

			throw XSECCryptoException(XSECCryptoException::RSAError,
				"OpenSSL:RSA privateKeyDecrypt - Error Decrypting PKCS1_5 padded RSA encrypt");

		}

		break;

	case XSECCryptoKeyRSA::PAD_OAEP_MGFP1 :
		{

			unsigned char * tBuf;
			int num = RSA_size(mp_rsaKey);
			XSECnew(tBuf, unsigned char[inLength]);
			ArrayJanitor<unsigned char> j_tBuf(tBuf);

			decryptSize = RSA_private_decrypt(inLength,
#if defined(XSEC_OPENSSL_CONST_BUFFERS)
							    inBuf,
#else
						        (unsigned char *) inBuf,
#endif
								tBuf,
								mp_rsaKey,
								RSA_NO_PADDING);
			if (decryptSize < 0) {

				throw XSECCryptoException(XSECCryptoException::RSAError,
					"OpenSSL:RSA privateKeyDecrypt - Error doing raw decrypt of RSA encrypted data");

			}

			// Clear out the "0"s at the front
			int i;
			for (i = 0; i < num && tBuf[i] == 0; ++i)
				--decryptSize;

			decryptSize = RSA_padding_check_PKCS1_OAEP(plainBuf,
													   maxOutLength,
													   &tBuf[i],
													   decryptSize,
													   num,
													   mp_oaepParams,
													   m_oaepParamsLen);

			if (decryptSize < 0) {

				throw XSECCryptoException(XSECCryptoException::RSAError,
					"OpenSSL:RSA privateKeyDecrypt - Error removing OAEPadding");

			}

		}
		break;

	default :

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Unknown padding method");

	}

#if 0

	/* normally commented out code to determine endian problems */
	int i;
	unsigned char t[512];
	if (decryptSize < 512) {
		memcpy(t, plainBuf, decryptSize);
		for (i = 0; i < decryptSize;++i) {
			plainBuf[i] = t[decryptSize - 1 - i];
		}
	}

#endif

	return decryptSize;

}

// --------------------------------------------------------------------------------
//           encrypt a buffer
// --------------------------------------------------------------------------------

unsigned int OpenSSLCryptoKeyRSA::publicEncrypt(const unsigned char * inBuf,
								 unsigned char * cipherBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength,
								 PaddingType padding,
								 hashMethod hm) {

	// Perform an encrypt
	if (mp_rsaKey == NULL) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Attempt to encrypt data with empty key");
	}

	int encryptSize;

	switch (padding) {

	case XSECCryptoKeyRSA::PAD_PKCS_1_5 :

		encryptSize = RSA_public_encrypt(inLength,
#if defined(XSEC_OPENSSL_CONST_BUFFERS)
  					        inBuf,
#else
						    (unsigned char *) inBuf,
#endif
							cipherBuf,
							mp_rsaKey,
							RSA_PKCS1_PADDING);
		
		if (encryptSize < 0) {

			throw XSECCryptoException(XSECCryptoException::RSAError,
				"OpenSSL:RSA publicKeyEncrypt - Error performing PKCS1_5 padded RSA encrypt");

		}

		break;

	case XSECCryptoKeyRSA::PAD_OAEP_MGFP1 :
		{

			unsigned char * tBuf;
			unsigned int num = RSA_size(mp_rsaKey);
			if (maxOutLength < num) {
				throw XSECCryptoException(XSECCryptoException::RSAError,
					"OpenSSL:RSA publicKeyEncrypt - Not enough space in cipherBuf");
			}

			XSECnew(tBuf, unsigned char[num]);
			ArrayJanitor<unsigned char> j_tBuf(tBuf);

			// First add the padding

			encryptSize = RSA_padding_add_PKCS1_OAEP(tBuf,
													 num,
#if defined(XSEC_OPENSSL_CONST_BUFFERS)
  					                                 inBuf,
#else
						                             (unsigned char *) inBuf,
#endif
													 inLength,
													 mp_oaepParams,
													 m_oaepParamsLen);

			if (encryptSize <= 0) {

				throw XSECCryptoException(XSECCryptoException::RSAError,
					"OpenSSL:RSA publicKeyEncrypt - Error adding OAEPadding");

			}

			encryptSize = RSA_public_encrypt(num,
								tBuf,
								cipherBuf,
								mp_rsaKey,
								RSA_NO_PADDING);
			

			if (encryptSize < 0) {

				throw XSECCryptoException(XSECCryptoException::RSAError,
					"OpenSSL:RSA publicKeyEncrypt - Error encrypting padded data");

			}
		}
		break;

	default :

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Unknown padding method");

	}


	return encryptSize;

}

// --------------------------------------------------------------------------------
//           Size in bytes
// --------------------------------------------------------------------------------

unsigned int OpenSSLCryptoKeyRSA::getLength(void) {

	if (mp_rsaKey != NULL)
		return RSA_size(mp_rsaKey);

	return 0;

}

// --------------------------------------------------------------------------------
//           Clone this key
// --------------------------------------------------------------------------------

XSECCryptoKey * OpenSSLCryptoKeyRSA::clone() {

	OpenSSLCryptoKeyRSA * ret;

	XSECnew(ret, OpenSSLCryptoKeyRSA);

	ret->mp_rsaKey = RSA_new();

	if (mp_oaepParams != NULL) {
		XSECnew(ret->mp_oaepParams, unsigned char[m_oaepParamsLen]);
		memcpy(ret->mp_oaepParams, mp_oaepParams, m_oaepParamsLen);
		ret->m_oaepParamsLen = m_oaepParamsLen;
	}
	else {
		ret->mp_oaepParams = NULL;
		ret->m_oaepParamsLen = 0;
	}

	// Duplicate parameters 

	if (mp_rsaKey->n)
		ret->mp_rsaKey->n = BN_dup(mp_rsaKey->n);

	if (mp_rsaKey->e)
		ret->mp_rsaKey->e = BN_dup(mp_rsaKey->e);

	if (mp_rsaKey->d)
		ret->mp_rsaKey->d = BN_dup(mp_rsaKey->d);

	if (mp_rsaKey->p)
		ret->mp_rsaKey->p = BN_dup(mp_rsaKey->p);

	if (mp_rsaKey->q)
		ret->mp_rsaKey->q = BN_dup(mp_rsaKey->q);

	if (mp_rsaKey->dmp1)
		ret->mp_rsaKey->dmp1 = BN_dup(mp_rsaKey->dmp1);

	if (mp_rsaKey->dmq1)
		ret->mp_rsaKey->dmq1 = BN_dup(mp_rsaKey->dmq1);

	if (mp_rsaKey->iqmp)
		ret->mp_rsaKey->iqmp = BN_dup(mp_rsaKey->iqmp);

	return ret;

}

#endif /* HAVE_OPENSSL */
