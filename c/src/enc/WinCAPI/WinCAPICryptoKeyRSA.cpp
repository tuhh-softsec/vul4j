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
 * WinCAPICryptoKeyRSA := RSA Keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoKeyRSA.hpp>
#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/framework/XSECError.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);


WinCAPICryptoKeyRSA::WinCAPICryptoKeyRSA(HCRYPTPROV prov) {

	// Create a new key to be loaded as we go

	m_key = 0;
	m_p = prov;
	m_keySpec = 0;

	mp_exponent = NULL;
	m_exponentLen = 0;
	mp_modulus = NULL;
	m_modulusLen = 0;

};

WinCAPICryptoKeyRSA::~WinCAPICryptoKeyRSA() {


	// If we have a RSA, delete it

	if (m_key == 0)
		CryptDestroyKey(m_key);

	if (mp_exponent)
		delete[] mp_exponent;
	if (mp_modulus)
		delete[] mp_modulus;

};

WinCAPICryptoKeyRSA::WinCAPICryptoKeyRSA(HCRYPTPROV prov, 
										 HCRYPTKEY k) :
m_p(prov) {

	m_key = k;		// NOTE - We OWN this handle
	m_keySpec = 0;

	mp_exponent = mp_modulus = NULL;
	m_exponentLen = m_modulusLen = 0;

}

WinCAPICryptoKeyRSA::WinCAPICryptoKeyRSA(HCRYPTPROV prov, 
										 DWORD keySpec,
										 bool isPrivate) :
m_p(prov) {

	if (isPrivate == false) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPICryptoKeyRSA - Public keys defined via keySpec ctor not yet supported");

		
	}

	m_key = 0;
	m_keySpec = keySpec;

	mp_exponent = mp_modulus = NULL;
	m_exponentLen = m_modulusLen = 0;

}

// Generic key functions

XSECCryptoKey::KeyType WinCAPICryptoKeyRSA::getKeyType() {

	// Find out what we have
	if (m_key == 0) {

		if (m_keySpec != 0)
			return KEY_RSA_PRIVATE;

		if (mp_exponent == NULL ||
			mp_modulus == NULL)
			return KEY_NONE;
		else
			return KEY_RSA_PUBLIC;

	}

	if (m_keySpec != 0)
		return KEY_RSA_PAIR;

	return KEY_RSA_PUBLIC;

}

// --------------------------------------------------------------------------------
//           Load key from parameters
// --------------------------------------------------------------------------------

void WinCAPICryptoKeyRSA::loadPublicModulusBase64BigNums(const char * b64, unsigned int len) {

	if (mp_modulus != NULL) {
		delete[] mp_modulus;
		mp_modulus = NULL;		// In case we get an exception
	}

	mp_modulus = WinCAPICryptoProvider::b642WinBN(b64, len, m_modulusLen);

}

void WinCAPICryptoKeyRSA::loadPublicExponentBase64BigNums(const char * b64, unsigned int len) {

	if (mp_exponent != NULL) {
		delete[] mp_exponent;
		mp_exponent = NULL;		// In case we get an exception
	}

	mp_exponent = WinCAPICryptoProvider::b642WinBN(b64, len, m_exponentLen);

}

void WinCAPICryptoKeyRSA::importKey(void) {
	
	if (m_key != 0 ||
		mp_exponent == NULL ||
		mp_modulus == NULL)

		return;


	// Create a RSA Public-Key blob

	// First build a buffer to hold everything

	BYTE * blobBuffer;
	unsigned int blobBufferLen = WINCAPI_BLOBHEADERLEN + WINCAPI_RSAPUBKEYLEN + m_modulusLen;
	XSECnew(blobBuffer, BYTE[blobBufferLen]);
	ArrayJanitor<BYTE> j_blobBuffer(blobBuffer);

	// Blob header
	BLOBHEADER * header = (BLOBHEADER *) blobBuffer;

	header->bType = PUBLICKEYBLOB;
	header->bVersion = 0x02;			// We are using a version 2 blob
	header->reserved = 0;
	header->aiKeyAlg = CALG_RSA_SIGN;

	// Now the public key header
	RSAPUBKEY * pubkey = (RSAPUBKEY *) (blobBuffer + WINCAPI_BLOBHEADERLEN);

	pubkey->magic = 0x31415352;			// ASCII encoding of RSA1
	pubkey->bitlen = m_modulusLen * 8;		// Number of bits in prime modulus
	pubkey->pubexp = 0;
	BYTE * i = ((BYTE *) &(pubkey->pubexp));
	for (unsigned int j = 0; j < m_exponentLen; ++j)
		*i++ = mp_exponent[j];

	// Now copy in the modulus
	i = (BYTE *) (pubkey);
	i += WINCAPI_RSAPUBKEYLEN;

	memcpy(i, mp_modulus, m_modulusLen);

	// Now that we have the blob, import
	BOOL fResult = CryptImportKey(
					m_p,
					blobBuffer,
					blobBufferLen,
					0,				// Not signed
					0,				// No flags
					&m_key);

	if (fResult == 0) {
		
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA Error attempting to import key parameters");

	}

}


// --------------------------------------------------------------------------------
//           Verify a signature encoded as a Base64 string
// --------------------------------------------------------------------------------

bool WinCAPICryptoKeyRSA::verifySHA1PKCS1Base64Signature(const unsigned char * hashBuf, 
								 unsigned int hashLen,
								 const char * base64Signature,
								 unsigned int sigLen) {

	// Use the currently loaded key to validate the Base64 encoded signature

	if (m_key == 0) {

		// Try to import from the parameters
		importKey();

		if (m_key == 0) {
			throw XSECCryptoException(XSECCryptoException::RSAError,
				"WinCAPI:RSA - Attempt to validate signature with empty key");
		}
	}

	// Decode the signature
	unsigned char * rawSig;
	DWORD rawSigLen;
	XSECnew(rawSig, BYTE [sigLen]);
	ArrayJanitor<BYTE> j_rawSig(rawSig);

	// Decode the signature
	XSCryptCryptoBase64 b64;

	b64.decodeInit();
	rawSigLen = b64.decode((unsigned char *) base64Signature, sigLen, rawSig, sigLen);
	rawSigLen += b64.decodeFinish(&rawSig[rawSigLen], sigLen - rawSigLen);
	
	BYTE * rawSigFinal;
	XSECnew(rawSigFinal, BYTE[rawSigLen]);
	ArrayJanitor<BYTE> j_rawSigFinal(rawSigFinal);

	BYTE * j, *l;
	j = rawSig;
	l = rawSigFinal + rawSigLen - 1;
	
	while (l >= rawSigFinal) {
		*l-- = *j++;
	}

	// Have to create a Windows hash object and feed in the hash
	BOOL fResult;
	HCRYPTHASH h;
	fResult = CryptCreateHash(m_p, 
					CALG_SHA1, 
					0, 
					0,
					&h);

	if (!fResult) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error creating Windows Hash Object");
	}

	// Feed the hash value into the newly created hash object
	fResult = CryptSetHashParam(
					h, 
					HP_HASHVAL,
					(unsigned char *) hashBuf,
					0);

	if (!fResult) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error Setting Hash Value in Windows Hash object");
	}

	// Now validate
	fResult = CryptVerifySignature(
				h,
				rawSigFinal,
				rawSigLen,
				m_key,
				NULL,
				0);

	if (!fResult) {

		DWORD error = GetLastError();

		if (error != NTE_BAD_SIGNATURE) {
			throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error occurred in RSA validation");
		}

		return false;
	}

	return true;

}

// --------------------------------------------------------------------------------
//           Sign and encode result as a Base64 string
// --------------------------------------------------------------------------------


unsigned int WinCAPICryptoKeyRSA::signSHA1PKCS1Base64Signature(unsigned char * hashBuf,
		unsigned int hashLen,
		char * base64SignatureBuf,
		unsigned int base64SignatureBufLen) {

	// Sign a pre-calculated hash using this key

	if (m_keySpec == 0) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Attempt to sign data using a public or un-loaded key");
	}

	// Have to create a Windows hash object and feed in the hash
	BOOL fResult;
	HCRYPTHASH h;
	fResult = CryptCreateHash(m_p, 
					CALG_SHA1, 
					0, 
					0,
					&h);

	if (!fResult) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error creating Windows Hash Object");
	}

	// Feed the hash value into the newly created hash object
	fResult = CryptSetHashParam(
					h, 
					HP_HASHVAL, 
					hashBuf, 
					0);

	if (!fResult) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error Setting Hash Value in Windows Hash object");
	}

	// Now sign
	DWORD rawSigLen;
	fResult = CryptSignHash(
				h,
				m_keySpec,
				NULL,
				0,
				NULL,
				&rawSigLen);

	if (!fResult || rawSigLen < 1) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
		"WinCAPI:RSA - Error occurred obtaining RSA sig length");
	}

	BYTE * rawSig;
	XSECnew(rawSig, BYTE[rawSigLen]);
	ArrayJanitor<BYTE> j_rawSig(rawSig);

	fResult = CryptSignHash(
				h,
				m_keySpec,
				NULL,
				0,
				rawSig,
				&rawSigLen);

	if (!fResult || rawSigLen < 1) {

		throw XSECCryptoException(XSECCryptoException::RSAError,
		"WinCAPI:RSA - Error occurred signing hash");
	}

	// Now encode into a signature block
	BYTE *rawSigFinal;
	XSECnew(rawSigFinal, BYTE[rawSigLen]);
	ArrayJanitor<BYTE> j_rawSigFinal(rawSigFinal);

	BYTE * i, * j;

	i = rawSig;
	j = rawSigFinal + rawSigLen - 1;

	while (j >= rawSigFinal) {
		*j-- = *i++;
	}

	// Now encode
	XSCryptCryptoBase64 b64;
	b64.encodeInit();
	unsigned int ret = b64.encode(rawSigFinal, rawSigLen, (unsigned char *) base64SignatureBuf, base64SignatureBufLen);
	ret += b64.encodeFinish((unsigned char *) &base64SignatureBuf[ret], base64SignatureBufLen - ret);

	return ret;

}

XSECCryptoKey * WinCAPICryptoKeyRSA::clone() {

	WinCAPICryptoKeyRSA * ret;

	XSECnew(ret, WinCAPICryptoKeyRSA(m_p));
	
	if (m_key != 0) {

		// CryptDuplicateKey is not supported in Windows NT, so we need to export and then
		// reimport the key to get a copy

		BYTE keyBuf[2048];
		DWORD keyBufLen = 2048;
		CryptExportKey(m_key, 0, PUBLICKEYBLOB, 0, keyBuf, &keyBufLen);

		// Now re-import
		CryptImportKey(m_p, keyBuf, keyBufLen, NULL, 0, &ret->m_key);
	}

	ret->m_keySpec = m_keySpec;

	ret->m_exponentLen = m_exponentLen;
	if (mp_exponent != NULL) {
		XSECnew(ret->mp_exponent, BYTE[m_exponentLen]);
		memcpy(ret->mp_exponent, mp_exponent, m_exponentLen);
	}
	else
		ret->mp_exponent = NULL;

	ret->m_modulusLen = m_modulusLen;
	if (mp_modulus != NULL) {
		XSECnew(ret->mp_modulus, BYTE[m_modulusLen]);
		memcpy(ret->mp_modulus, mp_modulus, m_modulusLen);
	}
	else
		ret->mp_modulus = NULL;

	return ret;

}
// --------------------------------------------------------------------------------
//           Some utility functions
// --------------------------------------------------------------------------------

void WinCAPICryptoKeyRSA::loadParamsFromKey(void) {

	if (m_key == 0) {

		if (m_keySpec == 0)
			return;

		// See of we can get the user key
		if (!CryptGetUserKey(m_p, m_keySpec, &m_key))
			return;
	}

	// Export key into a keyblob
	BOOL fResult;
	DWORD blobLen;

	fResult = CryptExportKey(
		m_key,
		0,
		PUBLICKEYBLOB,
		0,
		NULL,
		&blobLen);
	
	if (fResult == 0 || blobLen < 1) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error exporting public key");
	}

	BYTE * blob;
	XSECnew(blob, BYTE[blobLen]);
	ArrayJanitor<BYTE> j_blob(blob);

	fResult = CryptExportKey(
		m_key,
		0,
		PUBLICKEYBLOB,
		0,
		blob,
		&blobLen);
	
	if (fResult == 0 || blobLen < 1) {
		throw XSECCryptoException(XSECCryptoException::RSAError,
			"WinCAPI:RSA - Error exporting public key");
	}

	RSAPUBKEY * pk = (RSAPUBKEY *) ( blob + WINCAPI_BLOBHEADERLEN );
	DWORD keyLen = pk->bitlen / 8;

	// Copy the keys
	
	BYTE * i = (BYTE *) ( pk );
	i += WINCAPI_RSAPUBKEYLEN;
	if (mp_modulus != NULL)
		delete[] mp_modulus;

	m_modulusLen = keyLen;
	XSECnew(mp_modulus, BYTE[m_modulusLen]);
	memcpy(mp_modulus, i, m_modulusLen);

	// Take the simple way out
	XSECnew(mp_exponent, BYTE[4]);
	*((DWORD *) mp_exponent) = pk->pubexp;

	// Now cut any leading 0s (Windows is LE, so start least significant end)

	m_exponentLen = 3;
	while (m_exponentLen > 0 && mp_exponent[m_exponentLen] == 0)
		m_exponentLen--;

	m_exponentLen++;	// Make it a length as apposed to an offset
}

unsigned int WinCAPICryptoKeyRSA::getExponentBase64BigNums(char * b64, unsigned int len) {

	if (m_key == 0 && m_keySpec == 0 && mp_exponent == NULL) {

		return 0;	// Nothing we can do

	}

	if (mp_exponent == NULL) {

		loadParamsFromKey();

	}

	unsigned int bLen;
	unsigned char * b =  WinCAPICryptoProvider::WinBN2b64(mp_exponent, m_exponentLen, bLen);
	if (bLen > len)
		bLen = len;
	memcpy(b64, b, bLen);
	delete[] b;

	return bLen;

}

unsigned int WinCAPICryptoKeyRSA::getModulusBase64BigNums(char * b64, unsigned int len) {

	if (m_key == 0 && m_keySpec == 0 && mp_modulus == NULL) {

		return 0;	// Nothing we can do

	}

	if (mp_modulus == NULL) {

		loadParamsFromKey();

	}

	unsigned int bLen;
	unsigned char * b =  WinCAPICryptoProvider::WinBN2b64(mp_modulus, m_modulusLen, bLen);
	if (bLen > len)
		bLen = len;
	memcpy(b64, b, bLen);
	delete[] b;

	return bLen;

}
