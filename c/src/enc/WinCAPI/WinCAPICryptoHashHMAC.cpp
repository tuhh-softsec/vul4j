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
 * WinCAPICryptoHashHMAC := Windows CAPI Implementation of Message digests
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/enc/WinCAPI/WinCAPICryptoHashHMAC.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <memory.h>

// --------------------------------------------------------------------------------
//           IPAD/OPAD definitions
// --------------------------------------------------------------------------------

static unsigned char ipad[] = {

	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
	0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
};

static unsigned char opad[] = {

	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
	0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 0x5C, 
};

// --------------------------------------------------------------------------------
//           Constructors/Destructors
// --------------------------------------------------------------------------------

WinCAPICryptoHashHMAC::WinCAPICryptoHashHMAC(WinCAPICryptoProvider * owner, HashType alg) {

	mp_ownerProvider = owner;
	m_h = 0;
	m_blockSize = 64;		// We only know SHA-1 and MD5 at this time - both are 64 bytes

	switch (alg) {

	case (XSECCryptoHash::HASH_SHA1) :
	
		m_algId = CALG_SHA;
		break;

	case (XSECCryptoHash::HASH_MD5) :
	
		m_algId = CALG_MD5;
		break;

	default :

		m_algId = 0;

	}

	if(m_algId == 0) {

		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Unknown algorithm"); 
	}

	m_hashType = alg;


}

void WinCAPICryptoHashHMAC::reset() {

	if (m_h != 0)
		CryptDestroyHash(m_h);

}

WinCAPICryptoHashHMAC::~WinCAPICryptoHashHMAC() {

	if (m_h != 0)
		CryptDestroyHash(m_h);

}

// --------------------------------------------------------------------------------
//           Key manipulation
// --------------------------------------------------------------------------------

void WinCAPICryptoHashHMAC::eraseKeys(void) {

	// Overwrite the ipad/opad calculated key values
	unsigned char * i = m_ipadKeyed;
	unsigned char * j = m_opadKeyed;

	for (unsigned int k = 0; k < XSEC_MAX_HASH_BLOCK_SIZE; ++k) {
		*i++ = 0;
		*j++ = 0;
	}

}

void WinCAPICryptoHashHMAC::setKey(XSECCryptoKey *key) {

	
	BOOL fResult;

	// Use this to initialise the ipadKeyed/opadKeyed values

	if (key->getKeyType() != XSECCryptoKey::KEY_HMAC) {

		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:HashHMAC - Non HMAC Key passed to HashHMAC");

	}

	if (m_blockSize > XSEC_MAX_HASH_BLOCK_SIZE) {

		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:HashHMAC - Internal error - have got a blocksize bigger than I can handle");

	}

	// Check to see if this is an internal Windows Key
	if (strEquals(key->getProviderName(), DSIGConstants::s_unicodeStrPROVWinCAPI) &&
		((WinCAPICryptoKeyHMAC *) key)->getWinKey() != 0) {

		HCRYPTKEY k = ((WinCAPICryptoKeyHMAC *) key)->getWinKey();

		fResult = CryptCreateHash(
			mp_ownerProvider->getProviderRSA(),
			CALG_HMAC,
			k,
			0,
			&m_h);

		if (fResult == 0 || m_h == 0) {
			DWORD error = GetLastError();
			throw XSECCryptoException(XSECCryptoException::MDError,
				"WinCAPI:Hash::setKey - Error creating internally keyed hash object"); 
		}

		// Set the HMAC algorithm
		HMAC_INFO hi;

		hi.HashAlgid = m_algId;
		hi.pbInnerString = NULL;		// Use default inner and outer strings
		hi.cbInnerString = 0;
		hi.pbOuterString = NULL;
		hi.cbOuterString = 0;

		fResult = CryptSetHashParam(
			m_h,
			HP_HMAC_INFO,
			(BYTE *) &hi,
			0);

		if (fResult == 0 || m_h == 0) {
			DWORD error = GetLastError();
			throw XSECCryptoException(XSECCryptoException::MDError,
				"WinCAPI:Hash::setKey - Error setting HASH_INFO object"); 
		}



		return;

	}

	// Need to load from raw bit string

	safeBuffer keyBuf;
	unsigned int keyLen = ((XSECCryptoKeyHMAC *) key)->getKey(keyBuf);
	
	if (keyLen > m_blockSize) {

		HCRYPTHASH h;

		fResult = CryptCreateHash(
			mp_ownerProvider->getProviderDSS(),
			m_algId,
			0,
			0,
			&h);

		if (fResult == 0 || h == 0) {
			throw XSECCryptoException(XSECCryptoException::MDError,
				"WinCAPI:Hash::setKey - Error creating hash object"); 
		}

		fResult = CryptHashData(
			h,
			keyBuf.rawBuffer(),
			keyLen,
			0);

		if (fResult == 0 || h == 0) {
			throw XSECCryptoException(XSECCryptoException::MDError,
				"WinCAPI:Hash::setKey - Error hashing key data"); 
		}

		BYTE outData[XSEC_MAX_HASH_SIZE];
		DWORD outDataLen = XSEC_MAX_HASH_SIZE;

		CryptGetHashParam(
			h,
			HP_HASHVAL,
			outData,
			&outDataLen,
			0);

		if (fResult == 0 || h == 0) {
			throw XSECCryptoException(XSECCryptoException::MDError,
				"WinCAPI:Hash::setKey - Error getting hash result"); 
		}

		keyBuf.sbMemcpyIn(outData, outDataLen);
		keyLen = outDataLen;

	}

	// Now create the ipad and opad keyed values
	memcpy(m_ipadKeyed, ipad, m_blockSize);
	memcpy(m_opadKeyed, opad, m_blockSize);

	// XOR with the key
	for (unsigned int i = 0; i < keyLen; ++i) {
		m_ipadKeyed[i] = keyBuf[i] ^ m_ipadKeyed[i];
		m_opadKeyed[i] = keyBuf[i] ^ m_opadKeyed[i];
	}


	// Now create the hash object, and start with the ipad operation
	fResult = CryptCreateHash(
		mp_ownerProvider->getProviderDSS(),
		m_algId,
		0,
		0,
		&m_h);

	if (fResult == 0 || m_h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:HashHMAC - Error creating hash object"); 
	}

	fResult = CryptHashData(
		m_h,
		m_ipadKeyed,
		m_blockSize,
		0);

	if (fResult == 0 || m_h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:HashHMAC - Error performing initial ipad digest"); 
	}

}

// --------------------------------------------------------------------------------
//           Hash operations
// --------------------------------------------------------------------------------

void WinCAPICryptoHashHMAC::hash(unsigned char * data, 
								 unsigned int length) {

	if (m_h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:HashHMAC::hash() - Called prior to setting key"); 
	}

	BOOL fResult = CryptHashData(
		m_h,
		data,
		length,
		0);

	if (fResult == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Error Hashing Data"); 
	}

}

unsigned int WinCAPICryptoHashHMAC::finish(unsigned char * hash,
									   unsigned int maxLength) {

	DWORD retLen;
	BOOL fResult;

	retLen = XSEC_MAX_HASH_SIZE;

	fResult = CryptGetHashParam(
		m_h,
		HP_HASHVAL,
		m_mdValue,
		&retLen,
		0);

	if (fResult == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Error getting hash value"); 
	}

	// Perform the opad operation
	HCRYPTHASH h;
	fResult = CryptCreateHash(
		mp_ownerProvider->getProviderDSS(),
		m_algId,
		0,
		0,
		&h);

	if (fResult == 0 || h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash::finish - Error creating hash object for opad operation"); 
	}

	fResult = CryptHashData(
		h,
		m_opadKeyed,
		m_blockSize,
		0);

	if (fResult == 0 || h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash::finish - Error hashing opad data"); 
	}

	fResult = CryptHashData(
		h,
		m_mdValue,
		retLen,
		0);

	if (fResult == 0 || h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash::finish - Error hashing ipad hash to opad"); 
	}

	// Read out the final hash
	retLen = XSEC_MAX_HASH_SIZE;

	fResult = CryptGetHashParam(
		h,
		HP_HASHVAL,
		m_mdValue,
		&retLen,
		0);

	CryptDestroyHash(h);

	m_mdLen = retLen;
	retLen = (maxLength > m_mdLen ? m_mdLen : maxLength);
	memcpy(hash, m_mdValue, retLen);

	return (unsigned int) retLen;

}

// Get information

XSECCryptoHash::HashType WinCAPICryptoHashHMAC::getHashType(void) {

	return m_hashType;			// This could be any kind of hash

}

