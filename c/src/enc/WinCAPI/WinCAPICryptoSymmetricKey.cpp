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
 * XSECCryptoSymmetricKey := Bulk encryption algorithms should all be
 *							implemented via this interface
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoSymmetricKey.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

#if defined (HAVE_WINCAPI)

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------

WinCAPICryptoSymmetricKey::WinCAPICryptoSymmetricKey(
						HCRYPTPROV prov,
						XSECCryptoSymmetricKey::SymmetricKeyType type) :
m_keyType(type),
m_keyBuf(""),
m_initialised(false),
m_doPad(true),
m_p(prov),
m_k(0) {

	m_keyBuf.isSensitive();

}

WinCAPICryptoSymmetricKey::~WinCAPICryptoSymmetricKey() {

	if (m_k != 0)
		CryptDestroyKey(m_k);

}

// --------------------------------------------------------------------------------
//           Basic Key interface methods
// --------------------------------------------------------------------------------

XSECCryptoSymmetricKey::SymmetricKeyType WinCAPICryptoSymmetricKey::getSymmetricKeyType() {

	return m_keyType;

}

const XMLCh * WinCAPICryptoSymmetricKey::getProviderName() {

	return DSIGConstants::s_unicodeStrPROVWinCAPI;

}

XSECCryptoKey * WinCAPICryptoSymmetricKey::clone() {

	WinCAPICryptoSymmetricKey * ret;

	XSECnew(ret, WinCAPICryptoSymmetricKey(m_p, m_keyType));
	ret->m_keyLen = m_keyLen;
	ret->m_keyBuf = m_keyBuf;

	if (m_k != 0) {

#if (_WIN32_WINNT >= 0x0400)
		if (CryptDuplicateKey(m_k,
			 				  0,
							  0,
							  &(ret->m_k)) == 0 ) {

			throw XSECCryptoException(XSECCryptoException::SymmetricError,
				"WinCAPI:KeyHMAC Error attempting to clone key parameters");

		}
#else
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"Unable to clone keys in Windows NT 4.0 and below");
#endif
	}
	else
		ret->m_k = 0;

	return ret;

}

// --------------------------------------------------------------------------------
//           Store the key value
// --------------------------------------------------------------------------------

void WinCAPICryptoSymmetricKey::setKey(const unsigned char * key, unsigned int keyLen) {

	m_keyBuf.sbMemcpyIn(key, keyLen);
	m_keyLen = keyLen;

	if (m_k != 0)
		CryptDestroyKey(m_k);
	
	m_p = 0;
	m_k = createWindowsKey(key, keyLen, m_keyType, &m_p);
}

// --------------------------------------------------------------------------------
//           Decrypt
// --------------------------------------------------------------------------------

int WinCAPICryptoSymmetricKey::decryptCtxInit(const unsigned char * iv) {

	// Returns amount of IV data used (in bytes)
	// Sets m_initialised iff the key is OK and the IV is OK.

	if (m_initialised)
		return 0;

	if (m_k == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Cannot initialise without key"); 

	}

	// Set up the context according to the required cipher type
	DWORD cryptMode;
	switch (m_keyType) {

	case (XSECCryptoSymmetricKey::KEY_3DES_CBC_192) :

		// A 3DES CBC key

		if (iv == NULL) {

			return 0;	// Cannot initialise without an IV

		}

		if (!CryptSetKeyParam(m_k, KP_IV, (unsigned char *) iv, 0)) {

			throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error setting IV"); 

		}

		m_blockSize = 8;
		m_bytesInLastBlock = 0;
		m_initialised = true;
		
		return 8;	// 3DEC_CBC uses a 64 bit IV

		break;

	case (XSECCryptoSymmetricKey::KEY_AES_ECB_128) :

			// An 128bit AES key in ECB mode

		cryptMode = CRYPT_MODE_ECB;

		if (!CryptSetKeyParam(m_k, KP_MODE, (BYTE *) (&cryptMode), 0)) {

			throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error setting IV"); 

		}

		m_blockSize = 16;
		m_bytesInLastBlock = 0;
		m_initialised = true;
		
		return 8;	// 3DEC_CBC uses a 64 bit IV

		break;

	default :

		// Cannot do this without an IV
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unknown key type"); 

	}

	return 0;
}


bool WinCAPICryptoSymmetricKey::decryptInit(bool doPad, const unsigned char * iv) {

	m_initialised = false;
	m_doPad = doPad;
	decryptCtxInit(iv);
	return true;

}

unsigned int WinCAPICryptoSymmetricKey::decrypt(const unsigned char * inBuf, 
								 unsigned char * plainBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength) {



	// NOTE: This won't actually stop WinCAPI blowing the buffer, so the onus is
	// on the caller.

	unsigned int offset = 0;
	if (!m_initialised) {
		offset = decryptCtxInit(inBuf);
		if (offset > inLength) {
			throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Not enough data passed in to get IV");
		}
	}

	DWORD outl = inLength - offset;

	// Copy in last block
	if (m_bytesInLastBlock > 0)
		memcpy(plainBuf, m_lastBlock, m_bytesInLastBlock);

	// Copy out the tail, as we _MUST_ know when we come to the end for decryptFinal
	unsigned int rounding = (outl % m_blockSize) + m_blockSize;
	memcpy(&plainBuf[m_bytesInLastBlock], &inBuf[offset], outl - rounding);

	// Copy the tail to m_lastBlock
	memcpy(m_lastBlock, &inBuf[offset + outl - rounding], rounding);
	outl = outl - rounding + m_bytesInLastBlock;
	m_bytesInLastBlock = rounding;
	
	if (!CryptDecrypt(m_k,
				 0,
				 FALSE,
				 0,
				 plainBuf,
				 &outl)) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error during WinCAPI decrypt"); 

	}

	return outl;

}

unsigned int WinCAPICryptoSymmetricKey::decryptFinish(unsigned char * plainBuf,
													  unsigned int maxOutLength) {

	DWORD outl = m_bytesInLastBlock;

	memcpy(plainBuf, m_lastBlock, outl);

	if (!CryptDecrypt(m_k, 
					  0, 
					  FALSE,
					  0,
					  plainBuf,
					  &outl)) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error during WinCAPI decrypt finalisation"); 

	}

	if (m_doPad) {

		// Need to do this ourselves, as WinCAPI appears broken
		if (plainBuf[outl - 1] > 8) {
			throw XSECCryptoException(XSECCryptoException::SymmetricError,
				"WinCAPI:SymmetricKey - Bad padding"); 
		}

		outl -= plainBuf[outl - 1];

	}

	return outl;
}

// --------------------------------------------------------------------------------
//           Encrypt
// --------------------------------------------------------------------------------

void WinCAPICryptoSymmetricKey::encryptCtxInit(const unsigned char * iv) {



	if (m_initialised == true)
		return;
	
	if (m_keyLen == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Cannot initialise without key"); 

	}

	m_initialised = true;

	// Set up the context according to the required cipher type

	const unsigned char * usedIV;
	unsigned char genIV[256];

	// Tell the library that the IV still has to be sent

	switch (m_keyType) {

	case (XSECCryptoSymmetricKey::KEY_3DES_CBC_192) :

		// A 3DES key

		if (iv == NULL) {
			
			BOOL res = CryptGenRandom(m_p, 256, genIV);
			if (res == FALSE) {
				throw XSECCryptoException(XSECCryptoException::SymmetricError,
					"WinCAPI:SymmetricKey - Error generating random IV");
			}

			usedIV = genIV;
			//return 0;	// Cannot initialise without an IV

		}
		else
			usedIV = iv;

		// Set the IV parameter
		if (!CryptSetKeyParam(m_k, KP_IV, (unsigned char *) usedIV, 0)) {

			throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error setting IV"); 

		}

		m_blockSize = 8;
		m_ivSize = 8;
		memcpy(m_lastBlock, usedIV, m_ivSize);
		m_bytesInLastBlock = 0;

		break;

	case (XSECCryptoSymmetricKey::KEY_AES_ECB_128) :

		// An AES key

		m_blockSize = 16;
		m_ivSize = 0;
		m_bytesInLastBlock = 0;

		break;
	
	default :

		// Cannot do this without an IV
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unknown key type"); 

	}

}
bool WinCAPICryptoSymmetricKey::encryptInit(bool doPad, const unsigned char * iv) {

	m_doPad = doPad;
	m_initialised = false;
	encryptCtxInit(iv);
	return true;

}

unsigned int WinCAPICryptoSymmetricKey::encrypt(const unsigned char * inBuf, 
								 unsigned char * cipherBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength) {

	if (m_initialised == false) {

		encryptInit();

	}

	// NOTE: This won't actually stop WinCAPI blowing the buffer, so the onus is
	// on the caller.

	unsigned int offset = 0;
	unsigned char * bufPtr;
	unsigned char * encPtr;	// Ptr to start of block to encrypt

	DWORD outl = 0;
	
	if (m_ivSize > 0) {

		memcpy(cipherBuf, m_lastBlock, m_ivSize);
		offset = m_ivSize;
		outl += m_ivSize;
		m_ivSize = 0;

	}

	bufPtr = &cipherBuf[offset];
	encPtr = bufPtr;

	if (m_bytesInLastBlock > 0) {
		memcpy(bufPtr, m_lastBlock, m_bytesInLastBlock);
		bufPtr = &bufPtr[m_bytesInLastBlock];
		outl += m_bytesInLastBlock;
	}

	unsigned int rounding = (m_bytesInLastBlock + inLength) % m_blockSize;
	rounding += m_blockSize;

	outl += inLength - rounding;
	if (outl > maxOutLength) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Not enough space in output buffer for encrypt"); 
	}

	outl -= offset;

	// Now copy back one block + rounding
	memcpy(m_lastBlock, &inBuf[inLength - rounding], rounding);
	m_bytesInLastBlock = rounding;

	// Finally, copy in last of buffer to encrypt
	memcpy(bufPtr, inBuf, inLength - rounding);

	// Do the enrypt
	if (!CryptEncrypt(m_k, 0, FALSE, 0, encPtr, &outl, maxOutLength)) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error during WinCAPI encrypt"); 
	}
	
	return outl + offset;

}

unsigned int WinCAPICryptoSymmetricKey::encryptFinish(unsigned char * cipherBuf,
													  unsigned int maxOutLength) {

	DWORD outl = m_bytesInLastBlock + m_blockSize;

	if (outl > maxOutLength) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Not enough space in output buffer for encrypt - NOTE WinCAPI requires an extra block to complete encryption"); 
	}

	outl = 0;

	if (m_bytesInLastBlock != 0) {

		memcpy(cipherBuf, m_lastBlock, m_bytesInLastBlock);
		outl = m_bytesInLastBlock;

	}

	if (!CryptEncrypt(m_k, 0, TRUE, 0, cipherBuf, &outl, maxOutLength)) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Error during WinCAPI encrypt"); 
	}

	if (!m_doPad) {

		// It is the responsibility of the caller to ensure they have 
		// passed in block size num bytes

		if (outl >= m_blockSize)
			outl -= m_blockSize;

	}

	return outl;

}


// --------------------------------------------------------------------------------
//           Create a windows key
// --------------------------------------------------------------------------------

HCRYPTKEY WinCAPICryptoSymmetricKey::createWindowsKey(
								const unsigned char * key, 
								unsigned int keyLen, 
								XSECCryptoSymmetricKey::SymmetricKeyType type,
								HCRYPTPROV * prov) {

	// First get the correct Provider handle to load the key into
	
	HCRYPTPROV p;
	
	if (prov == NULL || *prov == 0) {
		WinCAPICryptoProvider * cp = 
			dynamic_cast<WinCAPICryptoProvider*>(XSECPlatformUtils::g_cryptoProvider);

		p = cp->getApacheKeyStore();

		if (p == 0) {

			throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unable to retrieve internal key store"); 

		}

		if (prov != NULL)
			*prov = p;
	}

	else if (prov != NULL)
		p = *prov;

	// Get the key wrapping key
	HCRYPTKEY k;
	if (!CryptGetUserKey(p, AT_KEYEXCHANGE, &k)) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unable to retrieve internal key pair"); 
	}

	// Find out how long the output will be
	DWORD outl = 0;
	if (!CryptEncrypt(k, 0, TRUE, 0, 0, &outl, keyLen)) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unable to determine space required to encrypt key");
	}

	// Create the necessary buffer
	unsigned char * encryptBuf;
	unsigned int encryptBufSize = outl;
	XSECnew(encryptBuf, unsigned char[outl]);
	ArrayJanitor<unsigned char> j_encryptBuf(encryptBuf);

	memcpy(encryptBuf, key, keyLen);
	outl = keyLen;

	// Do the encrypt

	if (!CryptEncrypt(k, 0, TRUE, 0, encryptBuf, &outl, encryptBufSize)) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unable to encrypt key");
	}

	// Now we have the encrypted buffer, create a SIMPLEBLOB structure

	unsigned char * simpleBlob;
	XSECnew(simpleBlob, unsigned char [sizeof (BLOBHEADER) + sizeof (DWORD) +  outl]);
	ArrayJanitor<unsigned char> j_simpleBlob(simpleBlob);

	BLOBHEADER * blobHeader = (BLOBHEADER *) simpleBlob;
	blobHeader->bType = SIMPLEBLOB;
	blobHeader->bVersion = CUR_BLOB_VERSION;
	blobHeader->reserved = 0;

	unsigned int expectedLength;

	switch (type) {

	case (XSECCryptoSymmetricKey::KEY_3DES_CBC_192) :
					blobHeader->aiKeyAlg = CALG_3DES;
					expectedLength = 24;
					break;
	case (XSECCryptoSymmetricKey::KEY_AES_ECB_128) :
					blobHeader->aiKeyAlg = CALG_AES_128;
					expectedLength = 16;
					break;
	default :

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey::createWindowsKey - Unknown Symmetric key type");

	}

	// Check key length - otherwise the user could get some very cryptic error messages
	if (keyLen != expectedLength) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey::createWindowsKey - Keylength incorrect for algorithm");
	}

	DWORD * algId = (DWORD *) (simpleBlob + sizeof(BLOBHEADER));
	*algId = CALG_RSA_KEYX;

	// Copy in the encrypted data
	memcpy(&simpleBlob[sizeof(BLOBHEADER) + sizeof(DWORD)], encryptBuf, outl);

	// Now do the import

	HCRYPTKEY k2;

	if (!CryptImportKey(p, simpleBlob, sizeof(BLOBHEADER) + sizeof(DWORD) + outl, k, CRYPT_EXPORTABLE, &k2)) {
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"WinCAPI:SymmetricKey - Unable to import key");
	}

	return k2;

}


#endif /* HAVE_WINCAPI */
