/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * OpenSSLCryptoKeyRSA := RSA Keys
 *
 */

#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/framework/XSECError.hpp>

#include <memory.h>

// Define OID for SHA-1 hash

unsigned char sha1OID[] = {
	0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2B, 
	0x0E, 0x03, 0x02, 0x1A, 0x05, 0x00, 0x04, 
	0x14,
};

int sha1OIDLen = 15;

OpenSSLCryptoKeyRSA::OpenSSLCryptoKeyRSA() {

	// Create a new key to be loaded as we go

	mp_rsaKey = RSA_new();

};
OpenSSLCryptoKeyRSA::~OpenSSLCryptoKeyRSA() {


	// If we have a RSA, delete it

	if (mp_rsaKey)
		RSA_free(mp_rsaKey);

};

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
								 unsigned int sigLen) {

	// Use the currently loaded key to validate the Base64 encoded signature

	if (mp_rsaKey == NULL) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Attempt to validate signature with empty key");
	}

	unsigned char sigVal[512];
	int sigValLen;

	EVP_ENCODE_CTX m_dctx;
	int rc;

	EVP_DecodeInit(&m_dctx);
	rc = EVP_DecodeUpdate(&m_dctx, 
						  sigVal, 
						  &sigValLen, 
						  (unsigned char *) base64Signature, 
						  sigLen);

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

		delete[] decryptBuf;
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA::verify() - Error decrypting signature");
	}

	if (decryptSize != (int) (sha1OIDLen + hashLen)) {

		delete[] decryptBuf;
		return false;
	
	}

	for (t = 0; t < sha1OIDLen; ++t) {
		
		if (sha1OID[t] != decryptBuf[t]) {

			delete[] decryptBuf;
			return false;

		}

	}

	for (;t < decryptSize; ++t) {

		if (hashBuf[t-sha1OIDLen] != decryptBuf[t]) {

			delete[] decryptBuf;
			return false;

		}

	}

	// All OK
	delete[] decryptBuf;

	return true;

}

// --------------------------------------------------------------------------------
//           Sign and encode result as a Base64 string
// --------------------------------------------------------------------------------


unsigned int OpenSSLCryptoKeyRSA::signSHA1PKCS1Base64Signature(unsigned char * hashBuf,
		unsigned int hashLen,
		char * base64SignatureBuf,
		unsigned int base64SignatureBufLen) {

	// Sign a pre-calculated hash using this key

	if (mp_rsaKey == NULL) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"OpenSSL:RSA - Attempt to sign data with empty key");
	}

	// Build the buffer to be encrypted by prepending the SHA1 OID to the hash

	unsigned char * encryptBuf;
	unsigned char * preEncryptBuf;
	int encryptLen;
	int preEncryptLen;

	preEncryptLen = hashLen + sha1OIDLen;
	preEncryptBuf = new unsigned char[preEncryptLen];
	encryptBuf = new unsigned char[RSA_size(mp_rsaKey)];

	memcpy(preEncryptBuf, sha1OID, sha1OIDLen);
	memcpy(&preEncryptBuf[sha1OIDLen], hashBuf, hashLen);

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



XSECCryptoKey * OpenSSLCryptoKeyRSA::clone() {

	OpenSSLCryptoKeyRSA * ret;

	XSECnew(ret, OpenSSLCryptoKeyRSA);

	ret->m_keyType = m_keyType;
	ret->mp_rsaKey = RSA_new();

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